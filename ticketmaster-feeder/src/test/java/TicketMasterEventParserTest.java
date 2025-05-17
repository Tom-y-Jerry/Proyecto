import static org.junit.jupiter.api.Assertions.*;
import es.ulpgc.dacd.ticketmasterfeeder.infrastructure.adapter.TicketMasterEventParser;
import org.junit.jupiter.api.Test;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.List;

class TicketMasterEventParserTest {
    private final TicketMasterEventParser parser = new TicketMasterEventParser();

    @Test
    void parseEventsWithLocalDateAndTime() {
        String json = "{"
                + "\"_embedded\":{\"events\":[{"
                + "\"id\":\"1\","
                + "\"name\":\"Concert\","
                + "\"dates\":{\"start\":{\"localDate\":\"2025-05-20\",\"localTime\":\"20:00:00\"}},"
                + "\"_embedded\":{\"venues\":[{\"city\":{\"name\":\"Barcelona\"}}]}"
                + "}]}}";

        List<es.ulpgc.dacd.ticketmasterfeeder.domain.Event> events = parser.parseEvents(json);
        assertEquals(1, events.size());

        var e = events.get(0);
        assertEquals("1", e.getId());
        assertEquals("Concert", e.getName());
        // Date should be start of day UTC
        Instant expectedDate = LocalDate.parse("2025-05-20", DateTimeFormatter.ISO_LOCAL_DATE)
                .atStartOfDay().toInstant(ZoneOffset.UTC);
        assertEquals(expectedDate, e.getDate());
        assertEquals("20:00:00", e.getTime());
        assertEquals("Barcelona", e.getCity());
        assertEquals("feeder-ticketmaster", e.getSs());
        assertNotNull(e.getTs());
    }

    @Test
    void parseEventsWithoutLocalTime() {
        String json = "{"
                + "\"_embedded\":{\"events\":[{"
                + "\"id\":\"2\","
                + "\"name\":\"Meeting\","
                + "\"dates\":{\"start\":{\"localDate\":\"2025-06-01\"}},"
                + "\"_embedded\":{\"venues\":[{\"city\":{\"name\":\"Valencia\"}}]}"
                + "}]}}";

        List<es.ulpgc.dacd.ticketmasterfeeder.domain.Event> events = parser.parseEvents(json);
        assertEquals(1, events.size());

        var e = events.get(0);
        assertEquals("2", e.getId());
        assertEquals("Meeting", e.getName());
        Instant expectedDate = LocalDate.parse("2025-06-01", DateTimeFormatter.ISO_LOCAL_DATE)
                .atStartOfDay().toInstant(ZoneOffset.UTC);
        assertEquals(expectedDate, e.getDate());
        // Default when localTime absent
        assertEquals("Not specified", e.getTime());
        assertEquals("Valencia", e.getCity());
        assertEquals("feeder-ticketmaster", e.getSs());
        assertNotNull(e.getTs());
    }
}
