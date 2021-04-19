package storage;

import java.io.OutputStreamWriter;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.LinkedHashSet;
import java.util.Iterator;
import org.json.JSONObject;
import org.json.JSONArray;
import org.json.JSONException;

public final class Storage implements Iterable<Flat> {
	private static Storage instance = null;
	public static final String FILENAME = "db.json";

	private LinkedHashSet<Flat> set = new LinkedHashSet<Flat>();
	private int idGen = 0;

	public int getNextId () { return idGen++; }

	public static Storage getStorage () {
		if (instance == null) {
			instance = new Storage();
		}
		return instance;
	}

	public boolean add (Flat element) {
		return this.set.add(element);
	}

	public void clear () {
		this.set.clear();
	}

	@Override
	public Iterator<Flat> iterator () {
		return this.set.iterator();
	}

	public void dumpToFile () throws IOException {
		JSONObject db = new JSONObject();
		JSONArray ja = new JSONArray();
		for (Flat flat: this) {
			ja.put(flat.toJson());
		}

		db.put("idGen", this.idGen);
		db.put("db", ja);

		OutputStreamWriter w = new OutputStreamWriter(new FileOutputStream(FILENAME),"UTF-8");
		w.write(db.toString());
		w.close();
	}

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

			fr.close();
		} catch (IOException e) {
			System.err.println("Couldn\'t populate storage: the file " + FILENAME + " cannot be read");
		} catch (JSONException e) {
			System.err.println("Couldn\'t populate storage: The file " + FILENAME + " contains invalid JSON");
		}
	}
}