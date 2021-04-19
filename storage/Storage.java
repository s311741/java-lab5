package storage;

import java.io.OutputStreamWriter;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Date;
import java.util.LinkedHashSet;
import java.util.Iterator;
import java.util.HashMap;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import org.json.JSONObject;
import org.json.JSONArray;
import org.json.JSONException;

/*
 * The singleton Storage class to represent the in-memory state of the database
 */
public final class Storage implements Iterable<Flat> {
	private static Storage instance = null;
	public static final String FILENAME = "db.json";

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
		       "filename = " + FILENAME + "\n" +
		       "created at: " + this.creationDate.toString() + "\n";
	}

	/**
	 * Get a new ID for an element
	 */
	public int nextID () {
		return this.idGen++;
	}

	/**
	 * Get the instance of the singleton
	 */
	public static Storage getStorage () {
		if (instance == null) {
			instance = new Storage();
		}
		return instance;
	}

	/**
	 * Add an element, if there isn't an element with such ID already
	 * @param element The element to add (with the ID filled in already)
	 */
	public boolean add (Flat element) {
		if (!this.setValuesByID.put(element.getID(), element)) {
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

	/**
	 * Retrieve the element that is currently evaluated lesser than otheres
	 */
	public Flat getCurrentMinimum () {
		return this.currentMinimum;
	}

	@Override
	public Iterator<Flat> iterator () {
		return this.set.iterator();
	}

	public void dumpToJson () throws IOException {
		JSONObject db = new JSONObject();
		JSONArray ja = new JSONArray();
		for (Flat flat: this) {
			ja.put(flat.toJson());
		}

		db.put("idGen", this.idGen);
		db.put("creationDate", this.creationDate);
		db.put("db", ja);

		OutputStreamWriter w = new OutputStreamWriter(new FileOutputStream(FILENAME), "UTF-8");
		w.write(db.toString());
		w.close();
	}

	/**
	 * Attempt to populate the storage from its file, or, if anything is wrong, complain and do nothing
	 */
	public void tryPopulateFromFile () {
		try {
			FileReader fr = new FileReader(FILENAME);
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
				this.add(Flat.fromJson(ja.getJSONObject(i)));
			}

			try {
				SimpleDateFormat sdf = new SimpleDateFormat("EEE MMM d HH:mm:ss zzz yyyy");
				this.creationDate = sdf.parse(jo.getString("creationDate"));
			} catch (ParseException e) {
			} catch (JSONException e) { }

			fr.close();
		} catch (IOException e) {
			System.err.println("Couldn\'t populate storage: the file " + FILENAME + " cannot be read");
		} catch (JSONException e) {
			System.err.println("Couldn\'t populate storage: The file " + FILENAME + " contains invalid JSON");
		}
	}

	private void findNewMinimum () {
		this.currentMinimum = null;
		for (Flat flat: this) {
			if (this.currentMinimum == null || flat.compareTo(this.currentMinimum) < 0) {
				this.currentMinimum = flat;
			}
		}
	}
}