package es.ulpgc.dacd.infrastructure.persistence;

import es.ulpgc.dacd.domain.model.Station;
import org.junit.jupiter.api.*;
import java.sql.*;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

class SQLiteStationsRepositoryTest {
    private static final String TEST_DB_URL = "jdbc:sqlite:test_stations.db";
    private SQLiteStationsRepository repository;

    @BeforeEach
    void setUp() {
        repository = new SQLiteStationsRepository(TEST_DB_URL);
    }

    @AfterEach
    void tearDown() {
        try (Connection conn = DriverManager.getConnection(TEST_DB_URL)) {
            conn.createStatement().execute("DROP TABLE IF EXISTS stations;");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Test
    void shouldSaveAndFindStations() {
        Station station = new Station(1, "carrier", "short", "long", "timezone",
                40.0, -3.0, false, "address");
        repository.save(station);

        List<Station> result = repository.findAll();
        assertEquals(1, result.size());
        assertEquals(1, result.get(0).getId());
    }
}
