package es.ulpgc.dacd.infrastructure.api;

import com.google.gson.*;
import es.ulpgc.dacd.domain.model.Trip;
import es.ulpgc.dacd.domain.port.Trips;

import java.sql.*;
import java.util.*;

public final class BlaBlaCarTrips implements Trips {
    private final String dbUrl;

    public BlaBlaCarTrips(String dbUrl) {
        this.dbUrl = dbUrl;
    }

    @Override
    public List<Trip> getCleanTrips() {
        try (Connection conn = DriverManager.getConnection(dbUrl)) {
            conn.createStatement().execute("PRAGMA busy_timeout = 5000");

            Map<Integer, String> stationNames = getStationNames(conn);
            return buildTripsFromFares(conn, stationNames);

        } catch (SQLException e) {
            System.err.println("❌ Error obteniendo trips: " + e.getMessage());
            return List.of();
        }
    }

    private Map<Integer, String> getStationNames(Connection conn) throws SQLException {
        Map<Integer, String> names = new HashMap<>();
        String sql = "SELECT id, short_name FROM stations";
        try (PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                names.put(rs.getInt("id"), rs.getString("short_name"));
            }
        }
        return names;
    }

    private List<Trip> buildTripsFromFares(Connection conn, Map<Integer, String> stationNames) throws SQLException {
        List<Trip> trips = new ArrayList<>();
        String sql = "SELECT origin_id, destination_id, departure, arrival, price_cents, currency FROM fares";

        try (PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                String origin = stationNames.get(rs.getInt("origin_id"));
                String destination = stationNames.get(rs.getInt("destination_id"));

                if (origin != null && destination != null) {
                    trips.add(new Trip(
                            origin,
                            destination,
                            rs.getString("departure"),
                            rs.getString("arrival"),
                            rs.getInt("price_cents"),
                            rs.getString("currency")
                    ));
                }
            }
        }
        return trips;
    }
}

