package es.ulpgc.dacd.infrastructure.persistence;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;

public class StationsTableManager {
    private final String dbUrl;

    public StationsTableManager(String dbUrl) {
        this.dbUrl = dbUrl;
    }

    public void ensureTableExists() {
        String sql = """
            CREATE TABLE IF NOT EXISTS stations (
                id INTEGER PRIMARY KEY,
                carrier_id TEXT,
                short_name TEXT,
                long_name TEXT,
                time_zone TEXT,
                latitude REAL,
                longitude REAL,
                is_meta_gare BOOLEAN,
                address TEXT
            );
        """;

        try (Connection conn = DriverManager.getConnection(dbUrl);
             Statement stmt = conn.createStatement()) {
            stmt.execute(sql);
        } catch (Exception e) {
            System.err.println("❌ Error al crear tabla 'stations': " + e.getMessage());
        }
    }
}

