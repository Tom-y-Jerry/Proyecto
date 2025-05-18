package es.ulpgc.dacd.business.datamart;
import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class SQLiteInitializer {
    public Connection initialize(String path) {
        recreateDatabaseFileIfExists(path);
        Connection conn = initializeConnection(path);
        createTables(conn);
        return conn;
    }

    private Connection initializeConnection(String path) {
        try {
            return DriverManager.getConnection("jdbc:sqlite:" + path);
        } catch (SQLException e) {
            throw new RuntimeException("Failed to connect to database", e);
        }
    }

    private void recreateDatabaseFileIfExists(String path) {
        try {
            File dbFile = new File(path);
            if (dbFile.exists()) dbFile.delete();
        } catch (Exception e) {
            throw new RuntimeException("Failed to delete existing database file", e);
        }
    }

    private void createTables(Connection conn) {
        try (Statement stmt = conn.createStatement()) {
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
