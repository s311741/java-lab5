package storage.server;

import java.io.*;
import java.util.Date;
import java.util.concurrent.*;
import java.util.Iterator;
import java.util.stream.Stream;
import storage.*;
import java.sql.*;

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

	private static Date creationDate = new Date();
	private ConcurrentHashMap<Integer, Flat> values = new ConcurrentHashMap<Integer, Flat>();
	private Flat currentMinimum = null;

	private DatabaseConnection db;

	/**
	 * Get some human-readable information about the internals of the storage
	 */
	public String info () {
		return "Collection using " + this.values.getClass().getSimpleName() + "\n" +
		       Integer.toString(this.values.size()) + " elements\n" +
		       (this.currentMinimum == null
		                ? "Minimum doesn\'t exist"
		                : "Minimum has id " + this.currentMinimum.getID().toString()) + "\n" +
		       "created at: " + this.creationDate.toString() + "\n";
	}

	/**
	 * Add an element, autogenerating its ID
	 * @param element The element to add (with the ID null)
	 */
	public boolean add (Flat element, String userName) {
		if (element.getID() != null) {
			System.err.println("Received an element with non-null ID");
			return false;
		}

		element.setCreatorName(userName);

		try {
			PreparedStatement st = element.prepareStatement(this.db.getConnection(), "flats");
			if (st.executeUpdate() == 0) {
				throw new SQLException("No rows affected");
			}
			ResultSet keys = st.getGeneratedKeys();
			if (keys.next()) {
				element.forceUpdateID(keys.getInt(1));
			} else {
				throw new SQLException("No ID obtained");
			}
		} catch (SQLException e) {
			System.err.println("Failed to add element:");
			e.printStackTrace();
			return false;
		}

		if (this.values.put(element.getID(), element) != null) {
			System.err.println("Managed to assign an element an existing ID, somehow");
			System.exit(1);
		}

		if (this.currentMinimum == null || element.compareTo(this.currentMinimum) < 0) {
			this.currentMinimum = element;
		}

		return true;
	}

	/**
	 * Remove an element, given its id
	 * @param id ID of the element to remove
	 */
	public boolean removeByID (int id, String userName) {
		Flat element = this.values.get(id);
		if (element == null) {
			return false;
		}

		if (!element.getCreatorName().equals(userName)) {
			return false;
		}

		try {
			this.db.getStatement().execute("DELETE FROM flats WHERE id = " + id);
		} catch (SQLException e) {
			System.err.println("Failed to delete element:");
			e.printStackTrace();
			return false;
		}

		this.values.remove(id);

		if (element == this.currentMinimum) {
			this.findNewMinimum();
		}
		return true;
	}

	/**
	 * Remove all elements
	 */
	public void clear () {
		try {
			this.db.getStatement().execute("DELETE FROM flats");
		} catch (SQLException e) {
			System.err.println("Failed to clear database:");
			e.printStackTrace();
			return;
		}

		this.currentMinimum = null;
		this.values.clear();
	}

	public Stream<Flat> stream () {
		return this.values.values().stream();
	}

	/**
	 * Retrieve the element that is currently evaluated lesser than others
	 */
	public Flat getCurrentMinimum () {
		return this.currentMinimum;
	}

	public boolean isEmpty () {
		return this.values.isEmpty();
	}

	@Override
	public Iterator<Flat> iterator () {
		return this.values.values().iterator();
	}

	public synchronized void forceDropTable () {
		try {
			this.db.getStatement().execute("DROP TABLE IF EXISTS flats");
		} catch (SQLException e) {
			System.err.println("Failed to drop table:");
			e.printStackTrace();
		}
	}

	public synchronized boolean connect (DatabaseConnection db) {
		this.db = db;

		try {
			// Create the tables if they aren't there yet
			this.db.getStatement().execute(
				"CREATE TABLE IF NOT EXISTS flats (" +
				"id serial primary key not null," +
				"creator_name text not null," +
				"name text not null," +
				"coord_x real not null," +
				"coord_y double precision not null," +
				"created_unixtime bigint not null," +
				"area bigint not null," +
				"num_rooms bigint not null," +
				"furnish text," +
				"view text not null," +
				"transport text," +
				"house_name text not null," +
				"house_year integer not null," +
				"house_num_flats integer not null," +
				"house_num_lifts bigint not null)");
		} catch (SQLException e) {
			System.err.println("Failed to (try to) create flats table:");
			e.printStackTrace();
			return false;
		}

		// Populate in-memory representation from the database

		this.values.clear();

		try {
			final String query = "SELECT * FROM FLATS ORDER BY id";
			ResultSet result = this.db.getStatement().executeQuery(query);
			while (result.next()) {
				Flat element = Flat.fromSQLResult(result, 0);
				this.values.put(element.getID(), element);
			}
			this.findNewMinimum();
			System.err.println("Successfully queried the database for items");
		} catch (SQLException e) {
			System.err.println("Failed to query the database for items:");
			e.printStackTrace();
			return false;
		}

		return true;
	}

	private synchronized void findNewMinimum () {
		this.currentMinimum = null;
		for (Flat flat: this.values.values()) {
			if (this.currentMinimum == null || flat.compareTo(this.currentMinimum) < 0) {
				this.currentMinimum = flat;
			}
		}
	}
}