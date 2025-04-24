package es.ulpgc.dacd.infrastructure.adapter;

import es.ulpgc.dacd.domain.model.Fare;

import java.sql.*;
import java.util.List;

public class SQLiteFaresRepository {
    private final String dbUrl;

    public SQLiteFaresRepository(String dbUrl) {
        this.dbUrl = dbUrl;
        createTableIfNotExists();
    }

    private void createTableIfNotExists() {
        try (Connection conn = DriverManager.getConnection(dbUrl);
             Statement stmt = conn.createStatement()) {
            stmt.executeUpdate("""
                CREATE TABLE IF NOT EXISTS fares (
                    id INTEGER PRIMARY KEY,
                    origin_id INTEGER,
                    destination_id INTEGER,
                    departure TEXT,
                    arrival TEXT,
                    price_cents INTEGER,
                    currency TEXT
                );
            """);
        } catch (SQLException e) {
            throw new RuntimeException("❌ Error creating fares table", e);
        }
    }

    public void saveAll(List<Fare> fares) {
        try (Connection conn = DriverManager.getConnection(dbUrl)) {
            for (Fare fare : fares) {
                try (PreparedStatement stmt = conn.prepareStatement("""
                    INSERT OR IGNORE INTO fares (id, origin_id, destination_id, departure, arrival, price_cents, currency)
                    VALUES (?, ?, ?, ?, ?, ?, ?);
                """)) {
                    stmt.setInt(1, fare.id);
                    stmt.setInt(2, fare.originId);
                    stmt.setInt(3, fare.destinationId);
                    stmt.setString(4, fare.departure);
                    stmt.setString(5, fare.arrival);
                    stmt.setInt(6, fare.priceCents);
                    stmt.setString(7, fare.currency);
                    stmt.executeUpdate();
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("❌ Error saving fares", e);
        }
    }
}

