package es.ulpgc.dacd.infrastructure.persistence;
import es.ulpgc.dacd.domain.model.Event;
import es.ulpgc.dacd.domain.port.EventsRepository;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class SQLiteEventsRepository implements EventsRepository {
    private final String dbUrl;

    public SQLiteEventsRepository(String dbUrl) {
        this.dbUrl = dbUrl;
        createTableIfNotExists();
    }

    private void createTableIfNotExists() {
        String sql = """
            CREATE TABLE IF NOT EXISTS events (
                id TEXT PRIMARY KEY,
                nombre TEXT,
                fecha TEXT,
                ciudad TEXT
            );
        """;
        try (Connection conn = DriverManager.getConnection(dbUrl);
             Statement stmt = conn.createStatement()) {
            stmt.execute(sql);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void save(Event event) {
        saveAll(List.of(event));
    }

    @Override
    public void saveAll(List<Event> events) {
        String sql = """
            INSERT OR IGNORE INTO events (id, nombre, fecha, ciudad)
            VALUES (?, ?, ?, ?);
        """;

        try (Connection conn = DriverManager.getConnection(dbUrl);
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            for (Event e : events) {
                stmt.setString(1, e.getId());
                stmt.setString(2, e.getName());
                stmt.setString(3, e.getDate());
                stmt.setString(4, e.getCity());
                stmt.executeUpdate();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public List<Event> findAll() {
        List<Event> events = new ArrayList<>();
        String sql = "SELECT * FROM events";

        try (Connection conn = DriverManager.getConnection(dbUrl);
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                events.add(new Event(
                        rs.getString("id"),
                        rs.getString("nombre"),
                        rs.getString("fecha"),
                        rs.getString("ciudad")
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return events;
    }
}

