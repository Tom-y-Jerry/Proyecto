package es.ulpgc.dacd.infrastructure.persistence;

import es.ulpgc.dacd.domain.model.Fare;
import es.ulpgc.dacd.domain.port.FaresRepository;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class  SQLiteFaresRepository implements FaresRepository {
    private final String dbUrl;

    public SQLiteFaresRepository(String dbUrl) {
        this.dbUrl = dbUrl;
        createTableIfNotExists();
    }

    private void createTableIfNotExists() {
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
        } catch (SQLException e) {
            System.err.println("❌ Error creando tabla fares: " + e.getMessage());
        }
    }

    @Override
    public void save(Fare fare) {
        saveAll(List.of(fare));
    }

    @Override
    public void saveAll(List<Fare> fares) {
        String sql = """
            INSERT OR IGNORE INTO fares
            (id, origin_id, destination_id, departure, arrival, price_cents, currency)
            VALUES (?, ?, ?, ?, ?, ?, ?);
        """;
        try (Connection conn = DriverManager.getConnection(dbUrl)) {
            for (Fare fare : fares) {
                insertFare(conn, sql, fare);
            }
        } catch (SQLException e) {
            System.err.println("❌ Error guardando fares: " + e.getMessage());
        }
    }

    private void insertFare(Connection conn, String sql, Fare fare) throws SQLException {
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
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

    @Override
    public List<Fare> findAll() {
        List<Fare> fares = new ArrayList<>();
        String sql = "SELECT * FROM fares";
        try (Connection conn = DriverManager.getConnection(dbUrl);
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                fares.add(mapResultSet(rs));
            }
        } catch (SQLException e) {
            System.err.println("❌ Error consultando fares: " + e.getMessage());
        }
        return fares;
    }

    private Fare mapResultSet(ResultSet rs) throws SQLException {
        return new Fare(
                rs.getInt("id"),
                rs.getInt("origin_id"),
                rs.getInt("destination_id"),
                rs.getString("departure"),
                rs.getString("arrival"),
                rs.getInt("price_cents"),
                rs.getString("currency")
        );
    }
}
