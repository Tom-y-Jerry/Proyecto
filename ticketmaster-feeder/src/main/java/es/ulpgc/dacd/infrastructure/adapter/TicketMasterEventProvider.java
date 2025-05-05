package es.ulpgc.dacd.infrastructure.adapter;

import com.google.gson.*;
import es.ulpgc.dacd.domain.Event;
import es.ulpgc.dacd.infrastructure.TicketMasterApiClient;
import es.ulpgc.dacd.infrastructure.ports.EventProvider;

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
        try {
            String json = client.fetchEventsJson();
            return parseEvents(json);
        } catch (Exception e) {
            System.err.println("Error al obtener eventos: " + e.getMessage());
            return new ArrayList<>();
        }
    }

    private List<Event> parseEvents(String jsonString) {
        List<Event> events = new ArrayList<>();

        JsonObject json = gson.fromJson(jsonString, JsonObject.class);
        JsonArray eventsArray = json.getAsJsonObject("_embedded").getAsJsonArray("events");

        for (JsonElement e : eventsArray) {
            JsonObject obj = e.getAsJsonObject();

            String id = obj.get("id").getAsString();
            String name = obj.get("name").getAsString();

            String date = obj.getAsJsonObject("dates")
                    .getAsJsonObject("start")
                    .get("localDate").getAsString();

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

            events.add(new Event(id, name, date, time, city));
        }

        return events;
    }
}







