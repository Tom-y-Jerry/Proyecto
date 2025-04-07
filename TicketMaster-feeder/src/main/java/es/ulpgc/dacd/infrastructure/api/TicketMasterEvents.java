package es.ulpgc.dacd.infrastructure.api;

import com.google.gson.*;
import es.ulpgc.dacd.domain.model.Event;
import es.ulpgc.dacd.domain.port.Events;

import java.util.*;
import java.util.stream.Collectors;

public class TicketMasterEvents implements Events {
    private final TicketMasterAPIClient apiClient;
    private final Gson gson = new Gson();

    private static final Set<String> POPULAR_CITIES = Set.of(
            "Madrid", "Barcelona", "Valencia", "Seville", "Bilbao",
            "Zaragoza", "Malaga", "Granada", "Vigo", "A Coruña"
    );

    public TicketMasterEvents(TicketMasterAPIClient apiClient) {
        this.apiClient = apiClient;
    }

    @Override
    public List<Event> getCleanEvents() {
        List<Event> events = new ArrayList<>();
        try {
            String json = apiClient.fetchEventsJson(); // aquí usamos el método correcto del cliente
            JsonArray jsonEvents = parseJson(json);

            for (JsonElement el : jsonEvents) {
                JsonObject eventJson = el.getAsJsonObject();
                Event event = parseEvent(eventJson);
                if (event.getCity() != null && isPopularCity(event.getCity())) {
                    events.add(event);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return removeDuplicatesById(events);
    }

    private JsonArray parseJson(String jsonData) {
        JsonObject root = gson.fromJson(jsonData, JsonObject.class);
        JsonObject embedded = root.getAsJsonObject("_embedded");

        JsonArray events = new JsonArray();
        if (embedded != null && embedded.has("events")) {
            JsonElement eventsElement = embedded.get("events");

            if (eventsElement.isJsonArray()) {
                events = eventsElement.getAsJsonArray();
            } else if (eventsElement.isJsonObject()) {
                events.add(eventsElement.getAsJsonObject());
            }
        }

        return events;
    }

    private Event parseEvent(JsonObject event) {
        String id = getAsString(event, "id");
        String name = getAsString(event, "name");
        String date = event.has("dates") ?
                event.getAsJsonObject("dates").getAsJsonObject("start").get("localDate").getAsString() : null;

        String city = null;
        try {
            JsonObject venue = event.getAsJsonObject("_embedded")
                    .getAsJsonArray("venues").get(0).getAsJsonObject();
            city = venue.getAsJsonObject("city").get("name").getAsString();
        } catch (Exception ignored) {}

        return new Event(id, name, date, city);
    }

    private String getAsString(JsonObject obj, String key) {
        return obj.has(key) ? obj.get(key).getAsString() : null;
    }

    private boolean isPopularCity(String cityName) {
        return cityName != null && POPULAR_CITIES.stream()
                .anyMatch(city -> cityName.toLowerCase().contains(city.toLowerCase()));
    }

    private List<Event> removeDuplicatesById(List<Event> events) {
        return events.stream()
                .collect(Collectors.toMap(
                        Event::getId,
                        e -> e,
                        (e1, e2) -> e1
                ))
                .values().stream().toList();
    }
}


