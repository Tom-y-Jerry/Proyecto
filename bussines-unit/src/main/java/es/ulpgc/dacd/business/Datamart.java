package es.ulpgc.dacd.business;

import java.io.File;
import java.sql.*;

public class Datamart {
    private final Connection connection;

    public Datamart(String dbPath) {
        try {
            File dbFile = new File(dbPath);
            if (dbFile.exists()) {
                dbFile.delete();
            }

            connection = DriverManager.getConnection("jdbc:sqlite:" + dbPath);
            recreateTables();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private void recreateTables() throws SQLException {
        Statement stmt = connection.createStatement();

        stmt.execute("""
            CREATE TABLE events (
                id TEXT, name TEXT, date TEXT, time TEXT, city TEXT,
                ss TEXT, json TEXT, UNIQUE(id, date, time)
            )
        """);

        stmt.execute("""
            CREATE TABLE trips (
                origin TEXT, destination TEXT,
                departure TEXT, arrival TEXT,
                price REAL, currency TEXT,
                duration_minutes INTEGER,
                ss TEXT, json TEXT,
                UNIQUE(origin, destination, departure, arrival)
            )
        """);
    }

    public void insertTicketmasterEvent(String id, String name, String date, String time, String city, String ss, String json) throws SQLException {
        PreparedStatement stmt = connection.prepareStatement("""
            INSERT OR IGNORE INTO events (id, name, date, time, city, ss, json)
            VALUES (?, ?, ?, ?, ?, ?, ?)
        """);

        stmt.setString(1, id);
        stmt.setString(2, name);
        stmt.setString(3, date);
        stmt.setString(4, time);
        stmt.setString(5, city);
        stmt.setString(6, ss);
        stmt.setString(7, json);
        stmt.executeUpdate();
    }

    public void insertBlaBlaCarEvent(String origin, String destination, String departure, String arrival, double price, String currency, long durationMinutes, String ss, String json) throws SQLException {
        PreparedStatement stmt = connection.prepareStatement("""
            INSERT OR IGNORE INTO trips (origin, destination, departure, arrival, price, currency, duration_minutes, ss, json)
            VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)
        """);

        stmt.setString(1, origin);
        stmt.setString(2, destination);
        stmt.setString(3, departure);
        stmt.setString(4, arrival);
        stmt.setDouble(5, price);
        stmt.setString(6, currency);
        stmt.setLong(7, durationMinutes);
        stmt.setString(8, ss);
        stmt.setString(9, json);
        stmt.executeUpdate();
    }

}

