package es.ulpgc.dacd.infrastructure.persistence;

import es.ulpgc.dacd.domain.model.Trip;
import es.ulpgc.dacd.domain.port.TripsRepository;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class SQLiteTripsRepository implements TripsRepository {
    private final String dbUrl;

    public SQLiteTripsRepository(String dbUrl) {
        this.dbUrl = dbUrl;
        createTableIfNotExists(); // 🛠️ Se asegura de que la tabla exista al crear el repositorio
    }

    private void createTableIfNotExists() {
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
        } catch (SQLException e) {
            System.err.println("❌ Error creando tabla trips: " + e.getMessage());
        }
    }

    public void save(Trip trip) {
        String sql = "INSERT INTO trips (origin, destination, departure, arrival, price_cents, currency) " +
                "VALUES (?, ?, ?, ?, ?, ?)";

        try (Connection conn = DriverManager.getConnection(dbUrl);
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            conn.createStatement().execute("PRAGMA busy_timeout = 5000");

            stmt.setString(1, trip.getOrigin());
            stmt.setString(2, trip.getDestination());
            stmt.setString(3, trip.getDeparture());
            stmt.setString(4, trip.getArrival());
            stmt.setInt(5, trip.getPriceCents());
            stmt.setString(6, trip.getCurrency());

            stmt.executeUpdate();

        } catch (SQLException e) {
            System.err.println("❌ Error guardando trip: " + e.getMessage());
        }
    }

    @Override
    public void saveAll(List<Trip> trip) {

    }

    public List<Trip> findAll() {
        List<Trip> trips = new ArrayList<>();
        String sql = "SELECT origin, destination, departure, arrival, price_cents, currency FROM trips";

        try (Connection conn = DriverManager.getConnection(dbUrl);
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                Trip trip = new Trip(
                        rs.getString("origin"),
                        rs.getString("destination"),
                        rs.getString("departure"),
                        rs.getString("arrival"),
                        rs.getInt("price_cents"),
                        rs.getString("currency")
                );
                trips.add(trip);
            }

        } catch (SQLException e) {
            System.err.println("❌ Error leyendo trips: " + e.getMessage());
        }

        return trips;
    }
}


