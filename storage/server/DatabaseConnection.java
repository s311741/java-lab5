package storage.server;

import java.sql.*;

public class DatabaseConnection {
	private Connection connection;
	private Statement statement;

	public DatabaseConnection (String username, String password, String host, String dbName) {
		try {
			Class.forName("org.postgresql.Driver");
		} catch (ClassNotFoundException e) {
			System.err.println("Cannot load DB driver:");
			e.printStackTrace();
			System.exit(1);
		}

		try {
			String url = "jdbc:postgresql://" + host + ":5432/" + dbName + "?user=" + username;
			if (password != null && !password.isEmpty()) {
				url += "&password=" + password;
			}

			System.err.println("url:");
			System.err.println(url);

			this.connection = DriverManager.getConnection(url);
			this.statement = this.connection.createStatement();
		} catch (SQLException e) {
			System.err.println("Failed to connect to the database:");
			e.printStackTrace();
			System.exit(1);
		}
	}

	public Statement getStatement () {
		return this.statement;
	}

	public Connection getConnection () {
		return this.connection;
	}
}