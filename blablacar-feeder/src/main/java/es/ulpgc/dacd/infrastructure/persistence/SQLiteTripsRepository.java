package es.ulpgc.dacd.infrastructure.persistence;

import es.ulpgc.dacd.domain.model.Trip;
import es.ulpgc.dacd.domain.port.TripsRepository;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public final class SQLiteTripsRepository implements TripsRepository {
    private final String dbUrl;

    public SQLiteTripsRepository(String dbUrl) {
        this.dbUrl = dbUrl;
        new TripsTableManager(dbUrl).ensureTableExists();
    }

    @Override
    public void save(Trip trip) {
        saveAll(List.of(trip));
    }

    @Override
    public void saveAll(List<Trip> trips) {
        String sql = """
            INSERT INTO trips (origin, destination, departure, arrival, price_cents, currency)
            VALUES (?, ?, ?, ?, ?, ?);
        """;

        try (Connection conn = DriverManager.getConnection(dbUrl);
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            for (Trip trip : trips) {
                stmt.setString(1, trip.getOrigin());
                stmt.setString(2, trip.getDestination());
                stmt.setString(3, trip.getDeparture());
                stmt.setString(4, trip.getArrival());
                stmt.setInt(5, trip.getPriceCents());
                stmt.setString(6, trip.getCurrency());
                stmt.executeUpdate();
            }

        } catch (SQLException e) {
            System.err.println("❌ Error guardando trip: " + e.getMessage());
        }
    }

    @Override
    public List<Trip> findAll() {
        List<Trip> trips = new ArrayList<>();
        String sql = "SELECT origin, destination, departure, arrival, price_cents, currency FROM trips";

        try (Connection conn = DriverManager.getConnection(dbUrl);
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                trips.add(new Trip(
                        rs.getString("origin"),
                        rs.getString("destination"),
                        rs.getString("departure"),
                        rs.getString("arrival"),
                        rs.getInt("price_cents"),
                        rs.getString("currency")
                ));
            }

        } catch (SQLException e) {
            System.err.println("❌ Error leyendo trips: " + e.getMessage());
        }

        return trips;
    }
}
