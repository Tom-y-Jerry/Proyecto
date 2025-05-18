package es.ulpgc.dacd.business.datamart;
import es.ulpgc.dacd.business.infrastructure.ports.DatamartService;
import java.sql.*;

public class SQLiteSaver implements DatamartService {
    private final Connection connection;

    public SQLiteSaver(String dbPath) {
        this.connection = new SQLiteInitializer().initialize(dbPath);
    }

    @Override
    public void insertEvent(String id, String name, String date, String time, String city, String ss, String json) {
        String sql = "INSERT OR IGNORE INTO events (id, name, date, time, city, ss, json) VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, id);
            stmt.setString(2, name);
            stmt.setString(3, date);
            stmt.setString(4, time);
            stmt.setString(5, city);
            stmt.setString(6, ss);
            stmt.setString(7, json);
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Failed to insert event", e);
        }
    }

    @Override
    public void insertTrip(String origin, String destination, String departure, String arrival, double price, String currency, long durationMinutes, String ss, String json) {
        String sql = "INSERT OR IGNORE INTO trips (origin, destination, departure, arrival, price, currency, duration_minutes, ss, json) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, origin);
            stmt.setString(2, destination);
            stmt.setString(3, departure);
            stmt.setString(4, arrival);
            stmt.setDouble(5, price);
            stmt.setString(6, currency);
            stmt.setLong(7, durationMinutes);
            stmt.setString(8, ss);
            stmt.setString(9, json);
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Failed to insert trip", e);
        }
    }
}


