package es.ulpgc.dacd.blablacarfeeder.infrastructure.adapters;
import es.ulpgc.dacd.blablacarfeeder.domain.Trip;
import es.ulpgc.dacd.blablacarfeeder.infrastructure.BlablacarApiClient;
import es.ulpgc.dacd.blablacarfeeder.infrastructure.ports.TripProvider;
import com.google.gson.JsonObject;
import java.time.Instant;
import java.util.*;

public class BlablacarTripProvider implements TripProvider {
    private final BlablacarApiClient apiClient;

    public BlablacarTripProvider(String stopsUrl, String faresUrl, String apiKey) {
        this.apiClient = new BlablacarApiClient(stopsUrl, faresUrl, apiKey);
    }

    @Override
    public List<Trip> provide() {
        List<Trip> trips = new ArrayList<>();
        try {
            Map<Integer, String> names = extractStopNames();
            List<JsonObject> fares = apiClient.fetchFare();

            for (JsonObject fare : fares) {
                trips.add(mapToTrip(fare, names));
            }
        } catch (Exception e) {
            System.err.println("Error en provider: " + e.getMessage());
        }
        return trips;
    }

    private Map<Integer, String> extractStopNames() throws Exception {
        Map<Integer, String> names = new HashMap<>();
        var stops = apiClient.fetchStops();
        for (var el : stops) {
            collectStopNames(el.getAsJsonObject(), names);
        }
        return names;
    }

    private void collectStopNames(JsonObject stop, Map<Integer, String> names) {
        if (stop.has("id") && stop.has("short_name")) {
            names.put(stop.get("id").getAsInt(), stop.get("short_name").getAsString());
        }
        if (stop.has("stops")) {
            for (var child : stop.getAsJsonArray("stops")) {
                collectStopNames(child.getAsJsonObject(), names);
            }
        }
    }

    private Trip mapToTrip(JsonObject f, Map<Integer, String> names) {
        int originId = f.get("origin_id").getAsInt();
        int destinationId = f.get("destination_id").getAsInt();
        String originName = names.getOrDefault(originId, "ID_" + originId);
        String destName = names.getOrDefault(destinationId, "ID_" + destinationId);
        double price = f.get("price_cents").getAsInt() / 100.0;
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
}