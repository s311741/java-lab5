package storage.server;

import java.sql.*;
import java.util.HashMap;
import storage.*;

public class UserServer {
	private static UserServer instance = null;
	private UserServer () { }
	public static UserServer getServer () {
		if (instance == null) {
			instance = new UserServer();
		}
		return instance;
	}

	private HashMap<String, String> users = new HashMap<String, String>(); // name -> pw hash
	private DatabaseConnection db = null;

	public boolean connect (DatabaseConnection db) {
		this.db = db;

		// create table, if needed
		try {
			this.db.getStatement().execute(
				"CREATE TABLE IF NOT EXISTS users (" +
				"name text not null primary key," +
				"pwhash text not null)");
		} catch (SQLException e) {
			System.err.println("Failed to create users table:");
			e.printStackTrace();
			return false;
		}

		// populate in-memory representation
		this.users.clear();
		try {
			final String query = "SELECT * FROM users";
			ResultSet result = this.db.getStatement().executeQuery(query);
			while (result.next()) {
				String name = result.getString(1);
				String hash = result.getString(2);
				this.users.put(name, hash);
			}
		} catch (SQLException e) {
			System.err.println("Failed to query the database for users:");
			e.printStackTrace();
			return false;
		}

		return true;
	}

	public boolean registerUser (UserCredentials login) {
		try {
			String query = "INSERT INTO users (name, pwhash) VALUES (?,?)";
			PreparedStatement st = this.db.getConnection().prepareStatement(query);
			st.setString(1, login.getName());
			st.setString(2, login.getHash());
			st.execute();
		} catch (SQLException e) {
			System.err.println("Error adding user:");
			e.printStackTrace();
			return false;
		}

		this.users.put(login.getName(), login.getHash());
		return true;
	}

	public boolean isValidLogin (UserCredentials login) {
		if (login == null) System.err.println("WTF???");
		String hash = this.users.get(login.getName());
		return (hash != null) && hash.equals(login.getHash());
	}
}