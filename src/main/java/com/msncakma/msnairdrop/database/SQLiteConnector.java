package com.msncakma.msnairdrop.database;

import com.msncakma.msnairdrop.MsnAirDrop;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class SQLiteConnector implements DatabaseConnector {
    private final String url;
    private Connection connection;

    public SQLiteConnector(MsnAirDrop plugin) {
        File databaseFile = new File(plugin.getDataFolder(), "database.db");
        this.url = "jdbc:sqlite:" + databaseFile.getAbsolutePath();
        
        try {
            Class.forName("org.sqlite.JDBC");
        } catch (ClassNotFoundException e) {
            plugin.getLogger().severe("SQLite JDBC driver not found: " + e.getMessage());
        }
    }

    @Override
    public Connection getConnection() throws SQLException {
        if (connection == null || connection.isClosed()) {
            connection = DriverManager.getConnection(url);
        }
        return connection;
    }

    @Override
    public void close() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}