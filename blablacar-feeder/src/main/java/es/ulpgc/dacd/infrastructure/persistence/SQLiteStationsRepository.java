package es.ulpgc.dacd.infrastructure.persistence;

import es.ulpgc.dacd.domain.model.Station;
import es.ulpgc.dacd.domain.port.StationsRepository;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public final class SQLiteStationsRepository implements StationsRepository {
    private final String dbUrl;

    public SQLiteStationsRepository(String dbUrl) {
        this.dbUrl = dbUrl;
        createTableIfNotExists();
    }

    private void createTableIfNotExists() {
        String sql = """
            CREATE TABLE IF NOT EXISTS stations (
                id INTEGER PRIMARY KEY,
                carrier_id TEXT,
                short_name TEXT,
                long_name TEXT,
                time_zone TEXT,
                latitude REAL,
                longitude REAL,
                is_meta_gare BOOLEAN,
                address TEXT
            );
        """;
        try (Connection conn = DriverManager.getConnection(dbUrl);
             Statement stmt = conn.createStatement()) {
            stmt.execute(sql);
        } catch (SQLException e) {
            System.err.println("❌ Error creando tabla stations: " + e.getMessage());
        }
    }

    @Override
    public void save(Station station) {
        saveAll(List.of(station));
    }

    @Override
    public void saveAll(List<Station> stations) {
        String sql = """
            INSERT OR IGNORE INTO stations
            (id, carrier_id, short_name, long_name, time_zone, latitude, longitude, is_meta_gare, address)
            VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?);
        """;
        try (Connection conn = DriverManager.getConnection(dbUrl)) {
            for (Station s : stations) {
                insertStation(conn, sql, s);
            }
        } catch (SQLException e) {
            System.err.println("❌ Error guardando estaciones: " + e.getMessage());
        }
    }

    private void insertStation(Connection conn, String sql, Station station) throws SQLException {
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, station.getId());
            stmt.setString(2, station.getCarrierId());
            stmt.setString(3, station.getShortName());
            stmt.setString(4, station.getLongName());
            stmt.setString(5, station.getTimeZone());
            stmt.setDouble(6, station.getLatitude());
            stmt.setDouble(7, station.getLongitude());
            stmt.setBoolean(8, station.isMetaGare());
            stmt.setString(9, station.getAddress());
            stmt.executeUpdate();
        }
    }

    @Override
    public List<Station> findAll() {
        List<Station> stations = new ArrayList<>();
        String sql = "SELECT * FROM stations";
        try (Connection conn = DriverManager.getConnection(dbUrl);
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                stations.add(mapResultSet(rs));
            }
        } catch (SQLException e) {
            System.err.println("❌ Error consultando estaciones: " + e.getMessage());
        }
        return stations;
    }

    private Station mapResultSet(ResultSet rs) throws SQLException {
        return new Station(
                rs.getInt("id"),
                rs.getString("carrier_id"),
                rs.getString("short_name"),
                rs.getString("long_name"),
                rs.getString("time_zone"),
                rs.getDouble("latitude"),
                rs.getDouble("longitude"),
                rs.getBoolean("is_meta_gare"),
                rs.getString("address")
        );
    }
}
