package es.ulpgc.dacd.infraestructure.persistence;

import es.ulpgc.dacd.domain.model.Event;
import es.ulpgc.dacd.infrastructure.persistence.SQLiteEventsRepository;
import org.junit.jupiter.api.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.*;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class SQLiteEventsRepositoryTest {

    private static final String TEST_DB_URL = "jdbc:sqlite:test.db";
    private SQLiteEventsRepository repository;

    @BeforeEach
    void setUp() {
        repository = new SQLiteEventsRepository(TEST_DB_URL);
        clearDatabase();
    }

    @AfterAll
    static void cleanUp() throws Exception {
        Files.deleteIfExists(Path.of("test.db"));
    }

    void clearDatabase() {
        try (Connection conn = DriverManager.getConnection(TEST_DB_URL);
             Statement stmt = conn.createStatement()) {
            stmt.execute("DELETE FROM events");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Test
    void testSave() {
        Event event = new Event("1", "Concierto", "2025-05-01", "Madrid");
        repository.save(event);

        List<Event> allEvents = repository.findAll();
        assertEquals(1, allEvents.size());
        assertEquals("Concierto", allEvents.get(0).getName());
    }

    @Test
    void testSaveAll() {
        List<Event> events = List.of(
                new Event("1", "Evento1", "2025-05-01", "Madrid"),
                new Event("2", "Evento2", "2025-06-01", "Barcelona")
        );

        repository.saveAll(events);
        List<Event> result = repository.findAll();

        assertEquals(2, result.size());
        assertTrue(result.stream().anyMatch(e -> e.getName().equals("Evento1")));
        assertTrue(result.stream().anyMatch(e -> e.getName().equals("Evento2")));
    }

    @Test
    void testFindAllEmpty() {
        List<Event> events = repository.findAll();
        assertTrue(events.isEmpty());
    }
}

