package es.ulpgc.dacd.adapters;

import es.ulpgc.dacd.domain.Trip;
import es.ulpgc.dacd.infrastructure.api.BlablacarApiClient;
import es.ulpgc.dacd.ports.TripProvider;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.time.Instant;
import java.util.*;

public class BlablacarTripProvider implements TripProvider {
    private final BlablacarApiClient apiClient;
    private final int originId;

    public BlablacarTripProvider(String stopsUrl, String faresUrl, String apiKey, int originId) {
        this.apiClient = new BlablacarApiClient(stopsUrl, faresUrl, apiKey);
        this.originId = originId;
    }

    @Override
    public List<Trip> provide() {
        List<Trip> trips = new ArrayList<>();
        try {
            Map<Integer, String> names = new HashMap<>();
            List<Destination> destinations = extractDestinations(names);

            for (Destination dest : destinations) {
                apiClient.fetchFare(originId, dest.id()).ifPresent(fare -> {
                    trips.add(mapToTrip(fare, names.get(originId), dest.name()));
                });
            }
        } catch (Exception e) {
            System.err.println("Error en provider: " + e.getMessage());
        }
        return trips;
    }

    private List<Destination> extractDestinations(Map<Integer, String> names) throws Exception {
        List<Destination> result = new ArrayList<>();
        JsonArray stops = apiClient.fetchStops();

        for (JsonElement el : stops) {
            collectStopNames(el.getAsJsonObject(), names);
        }

        for (JsonElement el : stops) {
            JsonObject stop = el.getAsJsonObject();
            if (stop.has("id") && stop.get("id").getAsInt() == originId && stop.has("destinations_ids")) {
                for (JsonElement d : stop.getAsJsonArray("destinations_ids")) {
                    int destId = d.getAsInt();
                    result.add(new Destination(destId, names.getOrDefault(destId, "ID_" + destId)));
                }
            }
        }

        return result;
    }

    private void collectStopNames(JsonObject stop, Map<Integer, String> names) {
        if (stop.has("id") && stop.has("short_name")) {
            names.put(stop.get("id").getAsInt(), stop.get("short_name").getAsString());
        }
        if (stop.has("stops")) {
            for (JsonElement child : stop.getAsJsonArray("stops")) {
                collectStopNames(child.getAsJsonObject(), names);
            }
        }
    }

    private Trip mapToTrip(JsonObject f, String originName, String destName) {
        return new Trip(
                "feeder-blablacar",
                originName,
                destName,
                Instant.parse(f.get("departure").getAsString()),
                Instant.parse(f.get("arrival").getAsString()),
                price,
                f.get("price_currency").getAsString()
        );
    }


    public record Destination(int id, String name) {}
}
