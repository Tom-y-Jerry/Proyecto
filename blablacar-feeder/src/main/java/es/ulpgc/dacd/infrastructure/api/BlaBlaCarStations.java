package es.ulpgc.dacd.infrastructure.api;

import com.google.gson.*;
import es.ulpgc.dacd.domain.model.Station;
import es.ulpgc.dacd.domain.port.Stations;
import java.util.*;
import java.util.stream.Collectors;

public final class BlaBlaCarStations implements Stations {
    private final BlaBlaCarAPIClient apiClient;
    private final Gson gson = new Gson();

    public BlaBlaCarStations(BlaBlaCarAPIClient apiClient) {
        this.apiClient = apiClient;
    }

    @Override
    public List<Station> getCleanStations() {
        try {
            String json = apiClient.fetchStopsJson();
            return parseAndFilterStations(json);
        } catch (Exception e) {
            System.err.println("❌ Error obteniendo estaciones: " + e.getMessage());
            return Collections.emptyList();
        }
    }

    private List<Station> parseAndFilterStations(String json) {
        JsonArray stops = gson.fromJson(json, JsonObject.class).getAsJsonArray("stops");
        List<Station> stations = new ArrayList<>();
        for (JsonElement el : stops) {
            Station station = parseStation(el.getAsJsonObject());
            if (station.getAddress() != null) { // Ahora solo se filtra por tener address
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

    private List<Station> removeDuplicatesById(List<Station> stations) {
        return stations.stream()
                .collect(Collectors.toMap(
                        Station::getId,
                        s -> s,
                        (s1, s2) -> s1
                ))
                .values().stream().toList();
    }

    private String getAsString(JsonObject obj, String key) {
        return obj.has(key) ? obj.get(key).getAsString() : null;
    }

    private double getAsDouble(JsonObject obj, String key) {
        return obj.has(key) ? obj.get(key).getAsDouble() : 0.0;
    }
}

