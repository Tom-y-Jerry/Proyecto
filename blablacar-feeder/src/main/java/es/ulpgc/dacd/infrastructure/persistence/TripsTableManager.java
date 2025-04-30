package es.ulpgc.dacd.infrastructure.persistence;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;

public class TripsTableManager {
    private final String dbUrl;

    public TripsTableManager(String dbUrl) {
        this.dbUrl = dbUrl;
    }

    public void ensureTableExists() {
        String sql = """
            CREATE TABLE IF NOT EXISTS trips (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                origin TEXT NOT NULL,
                destination TEXT NOT NULL,
                departure TEXT NOT NULL,
                arrival TEXT NOT NULL,
                price_cents INTEGER NOT NULL,
                currency TEXT NOT NULL
            );
        """;

        try (Connection conn = DriverManager.getConnection(dbUrl);
             Statement stmt = conn.createStatement()) {
            stmt.execute(sql);
        } catch (Exception e) {
            System.err.println("❌ Error al crear tabla 'trips': " + e.getMessage());
        }
    }
}

