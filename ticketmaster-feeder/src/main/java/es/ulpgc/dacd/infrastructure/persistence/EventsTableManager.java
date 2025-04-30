package es.ulpgc.dacd.infrastructure.persistence;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;

public class EventsTableManager {
    private final String dbUrl;

    public EventsTableManager(String dbUrl) {
        this.dbUrl = dbUrl;
    }

    public void ensureTableExists() {
        String sql = """
            CREATE TABLE IF NOT EXISTS events (
                id TEXT PRIMARY KEY,
                nombre TEXT,
                fecha TEXT,
                ciudad TEXT
            );
        """;

        try (Connection conn = DriverManager.getConnection(dbUrl);
             Statement stmt = conn.createStatement()) {
            stmt.execute(sql);
        } catch (Exception e) {
            System.err.println("❌ Error al crear tabla 'events': " + e.getMessage());
        }
    }
}

