package storage.server;

import java.io.*;
import java.util.Date;
import java.util.LinkedHashSet;
import java.util.HashMap;
import java.util.Iterator;
import java.util.stream.Stream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.FileSystems;
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

	private LinkedHashSet<Flat> set = new LinkedHashSet<Flat>();
	private HashMap<Integer, Flat> setValuesByID = new HashMap<Integer, Flat>();
	private Flat currentMinimum = null;

	private static final String dbUrl = "jdbc:postgresql://localhost:5432/flats?user=postgres";
	private Connection dbConnection;
	private Statement dbStatement;

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
		       "created at: " + this.creationDate.toString() + "\n";
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

		try {
			PreparedStatement st = element.prepareStatement(this.dbConnection, "flats");
			int rows = st.executeUpdate();

			if (rows == 0) {
				throw new SQLException("No rows affected");
			}
			System.err.println("rows: " + rows);

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

		if (this.setValuesByID.put(element.getID(), element) != null) {
			System.err.println("Managed to assign an element an existing ID, somehow");
			System.exit(1);
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
	 * Retrieve the element that is currently evaluated lesser than others
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

	public boolean tryConnectToDatabase () {
		// Load database driver
		try {
			Class.forName("org.postgresql.Driver");
		} catch (ClassNotFoundException e) {
			System.err.println("Cannot load DB driver:");
			e.printStackTrace();
			return false;
		}

		// Connect to database
		try {
			this.dbConnection = DriverManager.getConnection(dbUrl);
			this.dbStatement = dbConnection.createStatement();
			System.err.println("Successfully connected to " + dbUrl);
		} catch (SQLException e) {
			System.err.println("Failed to connect to the database:");
			e.printStackTrace();
			return false;
		}

		try {
			// Create the table if there isn't one yet

			this.dbStatement.execute(
				"CREATE TABLE IF NOT EXISTS flats (" +
				"id serial primary key not null," +
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
			System.err.println("Failed to (try to) create table:");
			e.printStackTrace();
			return false;
		}

		// Populate our in-memory representation from the database

		this.set.clear();
		this.setValuesByID.clear();

		try {
			final String query = "SELECT * FROM FLATS ORDER BY id;";
			ResultSet result = this.dbStatement.executeQuery(query);
			while (result.next()) {
				Flat element = Flat.fromSQLResult(result, 0);
				this.set.add(element);
				this.setValuesByID.put(element.getID(), element);
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

	private void findNewMinimum () {
		this.currentMinimum = null;
		for (Flat flat: this.set) {
			if (this.currentMinimum == null || flat.compareTo(this.currentMinimum) < 0) {
				this.currentMinimum = flat;
			}
		}
	}
}