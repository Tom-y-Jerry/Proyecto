

import static org.junit.jupiter.api.Assertions.*;
import es.ulpgc.dacd.ticketmasterfeeder.domain.Event;
import org.junit.jupiter.api.Test;
import java.time.Instant;

class EventTest {
    @Test
    void testGetters() {
        Instant ts = Instant.parse("2025-05-17T10:15:30Z");
        String ss = "source";
        String id = "evt1";
        String name = "Test Event";
        Instant date = Instant.parse("2025-05-18T00:00:00Z");
        String time = "12:00";
        String city = "Madrid";

        Event event = new Event(ts, ss, id, name, date, time, city);

        assertEquals(ts, event.getTs());
        assertEquals(ss, event.getSs());
        assertEquals(id, event.getId());
        assertEquals(name, event.getName());
        assertEquals(date, event.getDate());
        assertEquals(time, event.getTime());
        assertEquals(city, event.getCity());
    }
}