package storage.server;

import java.sql.*;

public class DatabaseConnection {
	private Connection connection;
	private Statement statement;

	public DatabaseConnection (String username, String password) {
		try {
			Class.forName("org.postgresql.Driver");
		} catch (ClassNotFoundException e) {
			System.err.println("Cannot load DB driver:");
			e.printStackTrace();
			System.exit(1);
		}

		try {
			String url = "jdbc:postgresql://pg:5432/studs?user=" + username + "&password=" + password;
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