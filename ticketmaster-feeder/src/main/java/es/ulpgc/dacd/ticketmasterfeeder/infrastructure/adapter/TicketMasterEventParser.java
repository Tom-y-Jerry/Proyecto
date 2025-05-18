package es.ulpgc.dacd.ticketmasterfeeder.infrastructure.adapter;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import es.ulpgc.dacd.ticketmasterfeeder.domain.Event;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class TicketMasterEventParser {
    private final Gson gson = new Gson();

    public List<Event> parseEvents(String jsonString) {
        JsonObject json = gson.fromJson(jsonString, JsonObject.class);
        JsonArray eventsArray = json.getAsJsonObject("_embedded").getAsJsonArray("events");
        return parseEventsArray(eventsArray);
    }

    private List<Event> parseEventsArray(JsonArray array) {
        List<Event> events = new ArrayList<>();
        for (JsonElement e : array) {
            JsonObject obj = e.getAsJsonObject();
            events.add(parseSingleEvent(obj));
        }
        return events;
    }

    private Event parseSingleEvent(JsonObject obj) {
        Instant ts = Instant.now();
        String ss = "feeder-ticketmaster";
        String id = obj.get("id").getAsString();
        String name = obj.get("name").getAsString();
        String dateStr = obj.getAsJsonObject("dates").getAsJsonObject("start").get("localDate").getAsString();
        Instant date = LocalDate.parse(dateStr, DateTimeFormatter.ISO_LOCAL_DATE)
                .atStartOfDay().toInstant(ZoneOffset.UTC);
        String time = obj.getAsJsonObject("dates").getAsJsonObject("start").has("localTime")
                ? obj.getAsJsonObject("dates").getAsJsonObject("start").get("localTime").getAsString()
                : "Not specified";
        String city = obj.getAsJsonObject("_embedded").getAsJsonArray("venues")
                .get(0).getAsJsonObject().getAsJsonObject("city").get("name").getAsString();
        return new Event(ts, ss, id, name, date, time, city);
    }
}