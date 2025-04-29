package es.ulpgc.dacd.infrastructure.api;

import com.google.gson.*;
import es.ulpgc.dacd.domain.model.Event;
import java.util.*;

public class TicketMasterParser {

    private final Gson gson = new Gson();

    public List<Event> parse(String json) {
        JsonArray array = eventsArray(json);
        List<Event> out = new ArrayList<>();
        array.forEach(e -> parseOne(e.getAsJsonObject()).ifPresent(out::add));
        return out;
    }


    private JsonArray eventsArray(String json) {
        JsonObject root = gson.fromJson(json, JsonObject.class);
        JsonObject emb  = root.getAsJsonObject("_embedded");
        return (emb != null && emb.has("events")) ? emb.getAsJsonArray("events") : new JsonArray();
    }

    private Optional<Event> parseOne(JsonObject j) {
        try {
            String id   = j.get("id").getAsString();
            String name = j.get("name").getAsString();
            String date = j.getAsJsonObject("dates").getAsJsonObject("start")
                    .get("localDate").getAsString();
            String city = j.getAsJsonObject("_embedded").getAsJsonArray("venues")
                    .get(0).getAsJsonObject()
                    .getAsJsonObject("city").get("name").getAsString();
            return Optional.of(new Event(id, name, date, city));
        } catch (Exception e) { return Optional.empty(); }
    }
}
