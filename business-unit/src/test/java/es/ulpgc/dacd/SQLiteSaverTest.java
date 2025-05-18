package es.ulpgc.dacd;
import es.ulpgc.dacd.business.datamart.SQLiteSaver;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import java.io.File;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

class SQLiteSaverTest {

    private final String testDbPath = "test_trip.db";

    @AfterEach
    void cleanUp() {
        new File(testDbPath).delete();
    }

    @Test
    void shouldInsertTripWithoutErrors() {
        SQLiteSaver saver = new SQLiteSaver(testDbPath);

        assertDoesNotThrow(() -> saver.insertTrip(
                "Madrid",
                "Barcelona",
                "2025-05-17T08:00:00Z",
                "2025-05-17T10:30:00Z",
                25.0,
                "EUR",
                150L,
                "feeder-blablacar",
                "{\"test\":true}"
        ));
    }
}
