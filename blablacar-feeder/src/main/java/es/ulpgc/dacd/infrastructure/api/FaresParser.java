package es.ulpgc.dacd.infrastructure.api;

import com.google.gson.*;
import es.ulpgc.dacd.domain.model.Fare;
import java.util.ArrayList;
import java.util.List;

public final class FaresParser {
    public static List<Fare> parse(String json) {
        JsonArray array = JsonParser.parseString(json)
                .getAsJsonObject()
                .getAsJsonArray("fares");
        List<Fare> fares = new ArrayList<>();
        for (JsonElement el : array) {
            fares.add(parseFare(el.getAsJsonObject()));
        }
        return fares;
    }

    private static Fare parseFare(JsonObject obj) {
        return new Fare(
                obj.get("id").getAsInt(),
                obj.get("origin_id").getAsInt(),
                obj.get("destination_id").getAsInt(),
                obj.get("departure").getAsString(),
                obj.get("arrival").getAsString(),
                obj.get("price_cents").getAsInt(),
                obj.get("price_currency").getAsString()
        );
    }
}
