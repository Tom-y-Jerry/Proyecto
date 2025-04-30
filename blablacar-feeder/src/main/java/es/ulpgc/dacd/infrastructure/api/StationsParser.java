package es.ulpgc.dacd.infrastructure.api;

import com.google.gson.*;
import es.ulpgc.dacd.domain.model.Station;

import java.util.*;
import java.util.stream.Collectors;

public class StationsParser {
    private final Gson gson = new Gson();

    public List<Station> parse(String json) {
        JsonArray stops = gson.fromJson(json, JsonObject.class).getAsJsonArray("stops");
        List<Station> stations = new ArrayList<>();

        for (JsonElement el : stops) {
            Station station = parseStation(el.getAsJsonObject());
            if (station.getAddress() != null) {
                stations.add(station);
            }
        }

        return removeDuplicatesById(stations);
    }

    private Station parseStation(JsonObject stop) {
        return new Station(
                stop.get("id").getAsInt(),
                getAsString(stop, "_carrier_id"),
                getAsString(stop, "short_name"),
                getAsString(stop, "long_name"),
                getAsString(stop, "time_zone"),
                getAsDouble(stop, "latitude"),
                getAsDouble(stop, "longitude"),
                stop.has("is_meta_gare") && stop.get("is_meta_gare").getAsBoolean(),
                getAsString(stop, "address")
        );
    }

    private String getAsString(JsonObject obj, String key) {
        return obj.has(key) ? obj.get(key).getAsString() : null;
    }

    private double getAsDouble(JsonObject obj, String key) {
        return obj.has(key) ? obj.get(key).getAsDouble() : 0.0;
    }

    private List<Station> removeDuplicatesById(List<Station> stations) {
        return stations.stream()
                .collect(Collectors.toMap(
                        Station::getId,
                        s -> s,
                        (s1, s2) -> s1
                ))
                .values().stream().toList();
    }
}
