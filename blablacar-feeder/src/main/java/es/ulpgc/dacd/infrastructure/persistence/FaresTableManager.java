package es.ulpgc.dacd.infrastructure.persistence;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;

public class FaresTableManager {
    private final String dbUrl;

    public FaresTableManager(String dbUrl) {
        this.dbUrl = dbUrl;
    }

    public void ensureTableExists() {
        String sql = """
            CREATE TABLE IF NOT EXISTS fares (
                id INTEGER PRIMARY KEY,
                origin_id INTEGER,
                destination_id INTEGER,
                departure TEXT,
                arrival TEXT,
                price_cents INTEGER,
                currency TEXT
            );
        """;

        try (Connection conn = DriverManager.getConnection(dbUrl);
             Statement stmt = conn.createStatement()) {
            stmt.execute(sql);
        } catch (Exception e) {
            System.err.println("❌ Error al crear tabla 'fares': " + e.getMessage());
        }
    }
}

