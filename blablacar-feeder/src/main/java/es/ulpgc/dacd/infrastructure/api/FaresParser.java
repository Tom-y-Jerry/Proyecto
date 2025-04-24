package es.ulpgc.dacd.infrastructure.api;

import com.google.gson.*;
import es.ulpgc.dacd.domain.model.Fare;

import java.util.ArrayList;
import java.util.List;

public class FaresParser {
    public static List<Fare> parse(String json) {
        List<Fare> fares = new ArrayList<>();
        JsonArray array = JsonParser.parseString(json)
                .getAsJsonObject()
                .getAsJsonArray("fares");

        for (JsonElement el : array) {
            JsonObject obj = el.getAsJsonObject();
            int id = obj.get("id").getAsInt();
            int originId = obj.get("origin_id").getAsInt();
            int destinationId = obj.get("destination_id").getAsInt();
            String departure = obj.get("departure").getAsString();
            String arrival = obj.get("arrival").getAsString();
            int priceCents = obj.get("price_cents").getAsInt();
            String currency = obj.get("price_currency").getAsString();

            fares.add(new Fare(id, originId, destinationId, departure, arrival, priceCents, currency));
        }
        return fares;
    }
}

