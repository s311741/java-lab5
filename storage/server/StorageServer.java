package storage.server;

import java.io.OutputStreamWriter;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Date;
import java.util.LinkedHashSet;
import java.util.Iterator;
import java.util.HashMap;
import java.util.stream.Stream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.FileSystems;
import org.json.JSONObject;
import org.json.JSONArray;
import org.json.JSONException;
import storage.*;

public class StorageServer implements Iterable<Flat> {
	private static StorageServer instance = null;
	private StorageServer () { }
	/**
	 * Get the instance of the singleton
	 */
	public static StorageServer getServer () {
		if (instance == null) {
			instance = new StorageServer();
		}
		return instance;
	}

	private String filename = "db.json";
	private boolean fileExistsYet = true;

	private static Date creationDate = new Date();

	private LinkedHashSet<Flat> set = new LinkedHashSet<Flat>();
	private HashMap<Integer, Flat> setValuesByID = new HashMap<Integer, Flat>();
	private Flat currentMinimum = null;
	private int idGen = 0;

	/**
	 * Get some human-readable information about the internals of the storage
	 */
	public String info () {
		return "Collection using " + this.set.getClass().toString() + " and " +
		       this.setValuesByID.getClass().toString() + "\n" +
		       Integer.toString(set.size()) + " elements\n" +
		       (this.currentMinimum == null
		                ? "Minimum doesn\'t exist"
		                : "Minimum has id " + this.currentMinimum.getID().toString()) + "\n" +
		       "next available ID = " + Integer.toString(this.idGen) + "\n" +
		       "filename = " + this.filename + "\n" +
		       "created at: " + this.creationDate.toString() + "\n";
	}

	/**
	 * Get a new ID for an element
	 */
	public int nextID () {
		return this.idGen++;
	}

	/**
	 * Set the filename to back the database
	 */
	public boolean setFile (String filename) {
		Path path = FileSystems.getDefault().getPath(filename).normalize();

		if (!Files.exists(path)) {
			this.fileExistsYet = false;

			System.err.print("No such file: " + filename);

			try {
				Files.createFile(path);
			} catch (IOException e) {
				System.err.println(" and failed to create it");
				return false;
			}

			System.err.println(" - created, will populate on save");
			return true;
		}

		this.fileExistsYet = true;

		if (!Files.isReadable(path)) {
			System.err.println("The file " + filename + " is not readable");
			return false;
		}

		if (!Files.isWritable(path)) {
			System.err.println("The file " + filename + " is not writable");
			return false;
		}

		this.filename = filename;
		return true;
	}

	/**
	 * Add an element, autogenerating its ID
	 * @param element The element to add (with the ID null)
	 */
	public boolean add (Flat element) {
		if (element.getID() != null) {
			System.err.println("Received an element with non-null ID");
			return false;
		}
		element.setID(this.nextID());
		return this.addWithID(element);
	}

	/**
	 * Add an element with the ID already filled in
	 * @param element The element to add (with the ID filled in)
	 */
	public boolean addWithID (Flat element) {
		if (this.setValuesByID.put(element.getID(), element) != null) {
			return false;
		}
		if (this.currentMinimum == null || element.compareTo(this.currentMinimum) < 0) {
			this.currentMinimum = element;
		}
		return this.set.add(element);
	}

	/**
	 * Remove an element, given its id
	 * @param id ID of the element to remove
	 */
	public boolean removeByID (int id) {
		Flat element = this.setValuesByID.get(id);
		if (element == null) {
			return false;
		}
		this.setValuesByID.remove(id);
		this.set.remove(element);

		if (element == this.currentMinimum) {
			this.findNewMinimum();
		}
		return true;

	}

	/**
	 * Remove an element, given the reference to it
	 * @param flat The element to remove
	 */
	public boolean removeByReference (Flat flat) {
		if (!this.set.contains(flat)) {
			return false;
		}
		this.setValuesByID.remove(flat.getID());
		this.set.remove(flat);

		if (flat == this.currentMinimum) {
			this.findNewMinimum();
		}
		return true;
	}

	/**
	 * Remove all elements
	 */
	public void clear () {
		this.currentMinimum = null;
		this.set.clear();
		this.setValuesByID.clear();
	}

	public Stream<Flat> stream () {
		return this.set.stream();
	}

	/**
	 * Retrieve the element that is currently evaluated lesser than otheres
	 */
	public Flat getCurrentMinimum () {
		return this.currentMinimum;
	}

	public boolean isEmpty () {
		return this.set.isEmpty();
	}

	@Override
	public Iterator<Flat> iterator () {
		return this.set.iterator();
	}

	/**
	 * Dump the database to the filename set by setFile()
	 */
	public synchronized boolean tryDumpToJson () {
		JSONObject db = new JSONObject();
		JSONArray ja = new JSONArray();
		for (Flat flat: this.set) {
			ja.put(flat.toJson());
		}

		db.put("idGen", this.idGen);
		db.put("creationDate", this.creationDate.getTime());
		db.put("db", ja);

		try {
			OutputStreamWriter w = new OutputStreamWriter(new FileOutputStream(this.filename), "UTF-8");
			w.write(db.toString(4));
			w.close();
		} catch (IOException e) {
			System.err.println("Failed to dump the DB to JSON:");
			e.printStackTrace();
			return false;
		}

		this.fileExistsYet = true;
		return true;
	}

	/**
	 * Attempt to populate the storage from its file, or, if anything is wrong, complain and do nothing
	 */
	public void tryPopulateFromFile () {
		if (!this.fileExistsYet) {
			return;
		}
		try {
			FileReader fr = new FileReader(this.filename);
			StringBuilder sb = new StringBuilder();
			int c;

			while ((c = fr.read()) != -1) {
				sb.append((char) c);
			}

			JSONObject jo = new JSONObject(sb.toString());

			this.clear();
			this.idGen = jo.getInt("idGen");

			JSONArray ja = jo.getJSONArray("db");
			int size = ja.length();
			for (int i = 0; i < size; i++) {
				boolean success;
				try {
					success = this.addWithID(Flat.fromJson(ja.getJSONObject(i)));
				} catch (JSONException e) {
					success = false;
				}
				if (!success) {
					System.err.println("Failed to add the object at position "
					                   + Integer.toString(i) + " from " + this.filename);
				}
			}

			try {
				this.creationDate = new Date(jo.getLong("creationDate"));
			} catch (JSONException e) {
				System.err.println("Couldn\'t find creationDate in " + this.filename);
				this.creationDate = new Date();
			}

			fr.close();
		} catch (IOException e) {
			System.err.println("Couldn\'t populate storage: the file " + this.filename + " cannot be read");
		} catch (JSONException e) {
			System.err.println("Couldn\'t populate storage: The file " + this.filename + " contains invalid JSON");
		}
	}

	private void findNewMinimum () {
		this.currentMinimum = null;
		for (Flat flat: this.set) {
			if (this.currentMinimum == null || flat.compareTo(this.currentMinimum) < 0) {
				this.currentMinimum = flat;
			}
		}
	}
}