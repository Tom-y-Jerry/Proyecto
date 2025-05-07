package es.ulpgc.dacd.infrastructure.adapter;

import com.google.gson.*;
import es.ulpgc.dacd.domain.Event;
import es.ulpgc.dacd.infrastructure.TicketMasterApiClient;
import es.ulpgc.dacd.infrastructure.ports.EventProvider;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class TicketMasterEventProvider implements EventProvider {
    private final TicketMasterApiClient client;
    private final Gson gson = new Gson();

    public TicketMasterEventProvider(String eventsUrl, String apiKey) {
        this.client = new TicketMasterApiClient(eventsUrl, apiKey);
    }

    @Override
    public List<Event> provide() {
        List<Event> allEvents = new ArrayList<>();

        try {
            List<String> jsonResponses = client.fetchEventsJson();
            for (String json : jsonResponses) {
                allEvents.addAll(parseEvents(json));
            }
        } catch (Exception e) {
            System.err.println("Error al obtener eventos: " + e.getMessage());
        }

        return allEvents;
    }


    private List<Event> parseEvents(String jsonString) {
        List<Event> events = new ArrayList<>();

        JsonObject json = gson.fromJson(jsonString, JsonObject.class);
        JsonArray eventsArray = json.getAsJsonObject("_embedded").getAsJsonArray("events");

        for (JsonElement e : eventsArray) {
            JsonObject obj = e.getAsJsonObject();

            Instant ts = Instant.now();
            String ss = "feeder-ticketmaster";

            String id = obj.get("id").getAsString();
            String name = obj.get("name").getAsString();

            String dateStr = obj.getAsJsonObject("dates")
                    .getAsJsonObject("start")
                    .get("localDate").getAsString();

            Instant date = LocalDate.parse(dateStr, DateTimeFormatter.ISO_LOCAL_DATE)
                    .atStartOfDay()
                    .toInstant(ZoneOffset.UTC);

            String time = obj.getAsJsonObject("dates")
                    .getAsJsonObject("start")
                    .has("localTime") ?
                    obj.getAsJsonObject("dates").getAsJsonObject("start").get("localTime").getAsString() :
                    "Not specified";

            String city = obj.getAsJsonObject("_embedded")
                    .getAsJsonArray("venues")
                    .get(0).getAsJsonObject()
                    .getAsJsonObject("city")
                    .get("name").getAsString();

            events.add(new Event(ts, ss, id, name, date, time, city));
        }

        return events;
    }
}








