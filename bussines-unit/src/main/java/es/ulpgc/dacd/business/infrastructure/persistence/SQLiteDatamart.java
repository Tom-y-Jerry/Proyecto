package es.ulpgc.dacd.business.infrastructure.persistence;

import es.ulpgc.dacd.business.application.service.DatamartService;

import java.io.File;
import java.sql.*;

public class SQLiteDatamart implements DatamartService {
    private final Connection connection;

    public SQLiteDatamart(String dbPath) {
        this.connection = initializeDatabase(dbPath);
        recreateTables();
    }

    private Connection initializeDatabase(String path) {
        try {
            File dbFile = new File(path);
            if (dbFile.exists()) dbFile.delete();
            return DriverManager.getConnection("jdbc:sqlite:" + path);
        } catch (SQLException e) {
            throw new RuntimeException("Failed to initialize database", e);
        }
    }

    private void recreateTables() {
        try (Statement stmt = connection.createStatement()) {
            stmt.execute(createEventsTable());
            stmt.execute(createTripsTable());
        } catch (SQLException e) {
            throw new RuntimeException("Failed to create tables", e);
        }
    }

    private String createEventsTable() {
        return """
            CREATE TABLE events (
                id TEXT, name TEXT, date TEXT, time TEXT, city TEXT,
                ss TEXT, json TEXT, UNIQUE(id, date, time)
            )
        """;
    }

    private String createTripsTable() {
        return """
            CREATE TABLE trips (
                origin TEXT, destination TEXT,
                departure TEXT, arrival TEXT,
                price REAL, currency TEXT,
                duration_minutes INTEGER,
                ss TEXT, json TEXT,
                UNIQUE(origin, destination, departure, arrival)
            )
        """;
    }

    @Override
    public void insertEvent(String id, String name, String date, String time, String city, String ss, String json) {
        String sql = "INSERT OR IGNORE INTO events (id, name, date, time, city, ss, json) VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, id);
            stmt.setString(2, name);
            stmt.setString(3, date);
            stmt.setString(4, time);
            stmt.setString(5, city);
            stmt.setString(6, ss);
            stmt.setString(7, json);
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Failed to insert event", e);
        }
    }

    @Override
    public void insertTrip(String origin, String destination, String departure, String arrival, double price, String currency, long durationMinutes, String ss, String json) {
        String sql = "INSERT OR IGNORE INTO trips (origin, destination, departure, arrival, price, currency, duration_minutes, ss, json) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
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
        } catch (SQLException e) {
            throw new RuntimeException("Failed to insert trip", e);
        }
    }

    public Connection getConnection() {
        return connection;
    }
}
