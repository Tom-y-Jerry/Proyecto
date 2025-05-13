package es.ulpgc.dacd.business.infrastructure.persistence;

import java.io.File;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

public class SQLiteInitializer {
    private final Connection connection;

    public SQLiteInitializer(Connection connection) {
        this.connection = connection;
    }

    public void initialize() {
        recreateDatabaseFileIfExists();
        createTables();
    }

    private void recreateDatabaseFileIfExists() {
        try {
            String path = connection.getMetaData().getURL().replace("jdbc:sqlite:", "");
            File dbFile = new File(path);
            if (dbFile.exists()) dbFile.delete();
        } catch (Exception e) {
            throw new RuntimeException("Failed to delete existing database file", e);
        }
    }

    private void createTables() {
        try (Statement stmt = connection.createStatement()) {
            stmt.execute(createEventsTable());
            stmt.execute(createTripsTable());
        } catch (SQLException e) {
            throw new RuntimeException("Failed to create tables", e);
        }
    }

    private String createEventsTable() {
        return """
            CREATE TABLE IF NOT EXISTS events (
                id TEXT, name TEXT, date TEXT, time TEXT, city TEXT,
                ss TEXT, json TEXT, UNIQUE(id, date, time)
            )
        """;
    }

    private String createTripsTable() {
        return """
            CREATE TABLE IF NOT EXISTS trips (
                origin TEXT, destination TEXT,
                departure TEXT, arrival TEXT,
                price REAL, currency TEXT,
                duration_minutes INTEGER,
                ss TEXT, json TEXT,
                UNIQUE(origin, destination, departure, arrival)
            )
        """;
    }
}

