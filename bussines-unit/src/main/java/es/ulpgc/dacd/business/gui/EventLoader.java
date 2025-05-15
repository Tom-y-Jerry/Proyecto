package es.ulpgc.dacd.business.gui;

import java.sql.*;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class EventLoader {
    private final Connection conn;

    public EventLoader(String dbPath) throws SQLException {
        this.conn = DriverManager.getConnection("jdbc:sqlite:" + dbPath);
    }

    public Set<String> loadOrigins(Set<String> current) {
        Set<String> newOrigins = new HashSet<>();
        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT DISTINCT origin FROM trips ORDER BY origin")) {
            while (rs.next()) {
                String origin = rs.getString("origin");
                if (!current.contains(origin)) {
                    newOrigins.add(origin);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error loading origins: " + e.getMessage());
        }
        return newOrigins;
    }

    public List<EventViewerGUI.Event> loadEvents(String selectedOrigin) {
        List<EventViewerGUI.Event> events = new ArrayList<>();
        try (PreparedStatement ps = conn.prepareStatement("""
                SELECT e.id, e.name, e.date, e.time, e.city
                FROM events e
                WHERE EXISTS (
                    SELECT 1 FROM trips t
                    WHERE t.origin = ? AND t.destination LIKE '%' || e.city || '%'
                )
                ORDER BY e.date, e.time
                """)) {
            ps.setString(1, selectedOrigin);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    events.add(new EventViewerGUI.Event(
                            rs.getString("id"),
                            rs.getString("name"),
                            rs.getString("date"),
                            rs.getString("time"),
                            rs.getString("city")
                    ));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error loading events: " + e.getMessage());
        }
        return events;
    }

    public List<String> loadTrips(String origin, String destinationCity) {
        List<String> trips = new ArrayList<>();
        try (PreparedStatement ps = conn.prepareStatement("""
                SELECT origin, destination, departure, arrival, price, currency, duration_minutes
                FROM trips
                WHERE origin = ? AND destination LIKE ?""")) {
            ps.setString(1, origin);
            ps.setString(2, "%" + destinationCity + "%");

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    trips.add(formatTrip(
                            rs.getString("origin"),
                            rs.getString("destination"),
                            rs.getString("departure"),
                            rs.getString("arrival"),
                            rs.getDouble("price"),
                            rs.getString("currency"),
                            rs.getInt("duration_minutes")
                    ));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error loading trips: " + e.getMessage());
        }
        return trips;
    }

    private String formatTrip(String origin, String destination, String depStr, String arrStr, double price, String currency, int durationMin) {
        Instant departure = Instant.parse(depStr);
        Instant arrival = Instant.parse(arrStr);

        LocalDate depDate = departure.atZone(ZoneId.systemDefault()).toLocalDate();
        LocalDate arrDate = arrival.atZone(ZoneId.systemDefault()).toLocalDate();

        String depTime = departure.atZone(ZoneId.systemDefault()).toLocalTime().format(DateTimeFormatter.ofPattern("HH:mm"));
        String arrTime = arrival.atZone(ZoneId.systemDefault()).toLocalTime().format(DateTimeFormatter.ofPattern("HH:mm"));

        long h = durationMin / 60, m = durationMin % 60;

        return String.format("\uD83D\uDE8C %s → %s | %s %s | %s %s | %dh %02dmin | %.2f %s",
                origin, destination,
                depDate, depTime,
                arrDate, arrTime,
                h, m, price, currency);
    }
}

