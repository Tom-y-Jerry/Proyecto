package es.ulpgc.dacd.infrastructure.persistence;

import es.ulpgc.dacd.domain.model.Fare;
import org.junit.jupiter.api.*;
import java.sql.*;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

class SQLiteFaresRepositoryTest {
    private static final String TEST_DB_URL = "jdbc:sqlite:test_fares.db";
    private SQLiteFaresRepository repository;

    @BeforeEach
    void setUp() {
        repository = new SQLiteFaresRepository(TEST_DB_URL);
    }

    @AfterEach
    void tearDown() {
        try (Connection conn = DriverManager.getConnection(TEST_DB_URL)) {
            conn.createStatement().execute("DROP TABLE IF EXISTS fares;");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Test
    void shouldSaveAndFindFares() {
        Fare fare = new Fare(1, 10, 20, "2025-08-01T10:00", "2025-08-01T12:00", 1500, "EUR");
        repository.save(fare);

        List<Fare> result = repository.findAll();
        assertEquals(1, result.size());
        assertEquals(1, result.get(0).id);
    }
}
