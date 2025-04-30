package es.ulpgc.dacd.infrastructure.persistence;

import es.ulpgc.dacd.domain.model.Fare;
import es.ulpgc.dacd.domain.port.FaresRepository;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public final class SQLiteFaresRepository implements FaresRepository {
    private final String dbUrl;

    public SQLiteFaresRepository(String dbUrl) {
        this.dbUrl = dbUrl;
        new FaresTableManager(dbUrl).ensureTableExists();
    }

    @Override
    public void save(Fare fare) {
        saveAll(List.of(fare));
    }

    @Override
    public void saveAll(List<Fare> fares) {
        String sql = """
            INSERT OR REPLACE INTO fares (
                id, origin_id, destination_id, departure, arrival, price_cents, currency
            ) VALUES (?, ?, ?, ?, ?, ?, ?);
        """;

        try (Connection conn = DriverManager.getConnection(dbUrl);
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            for (Fare fare : fares) {
                stmt.setInt(1, fare.getId());
                stmt.setInt(2, fare.getOriginId());
                stmt.setInt(3, fare.getDestinationId());
                stmt.setString(4, fare.getDeparture());
                stmt.setString(5, fare.getArrival());
                stmt.setInt(6, fare.getPriceCents());
                stmt.setString(7, fare.getCurrency());
                stmt.executeUpdate();
            }

        } catch (SQLException e) {
            System.err.println("❌ Error guardando fares: " + e.getMessage());
        }
    }

    @Override
    public List<Fare> findAll() {
        List<Fare> fares = new ArrayList<>();
        String sql = "SELECT * FROM fares";

        try (Connection conn = DriverManager.getConnection(dbUrl);
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                fares.add(new Fare(
                        rs.getInt("id"),
                        rs.getInt("origin_id"),
                        rs.getInt("destination_id"),
                        rs.getString("departure"),
                        rs.getString("arrival"),
                        rs.getInt("price_cents"),
                        rs.getString("currency")
                ));
            }

        } catch (SQLException e) {
            System.err.println("❌ Error leyendo fares: " + e.getMessage());
        }

        return fares;
    }
}
