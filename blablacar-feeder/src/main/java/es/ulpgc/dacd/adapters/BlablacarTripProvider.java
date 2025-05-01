package es.ulpgc.dacd.adapters;

import com.google.gson.*;
import es.ulpgc.dacd.domain.Trip;
import es.ulpgc.dacd.ports.TripProvider;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import java.time.Instant;
import java.time.LocalDate;
import java.util.*;

public class BlablacarTripProvider implements TripProvider {
    private final OkHttpClient client = new OkHttpClient();
    private final Gson gson = new Gson();

    private final String stopsUrl;
    private final String faresUrl;
    private final String apiKey;
    private final int originId;

    public BlablacarTripProvider(String stopsUrl, String faresUrl, String apiKey, int originId) {
        this.stopsUrl = stopsUrl;
        this.faresUrl = faresUrl;
        this.apiKey = apiKey;
        this.originId = originId;
    }

    @Override
    public List<Trip> provide() {
        List<Trip> trips = new ArrayList<>();
        try {
            List<Destination> destinos = getAvailableDestinations();
            for (Destination dest : destinos) {
                getTripFor(originId, dest.id(), dest.name()).ifPresent(trips::add);
            }
        } catch (Exception e) {
            System.err.println("Error en provider: " + e.getMessage());
        }
        return trips;
    }

    public List<Destination> getAvailableDestinations() {
        List<Destination> destinos = new ArrayList<>();
        Map<Integer, String> names = new HashMap<>();

        try {
            Request request = new Request.Builder()
                    .url(stopsUrl)
                    .addHeader("Authorization", "Token " + apiKey)
                    .build();

            Response response = client.newCall(request).execute();
            JsonArray stops = gson.fromJson(response.body().string(), JsonObject.class)
                    .getAsJsonArray("stops");


            for (JsonElement el : stops) {
                collectStopNames(el.getAsJsonObject(), names);
            }

            for (JsonElement el : stops) {
                JsonObject stop = el.getAsJsonObject();
                if (stop.has("id") && stop.get("id").getAsInt() == originId && stop.has("destinations_ids")) {
                    for (JsonElement d : stop.getAsJsonArray("destinations_ids")) {
                        int destId = d.getAsInt();
                        destinos.add(new Destination(destId, names.getOrDefault(destId, "ID_" + destId)));
                    }
                }
            }

        } catch (Exception e) {
            System.out.println("Error al obtener destinos: " + e.getMessage());
        }

        return destinos;
    }

    private void collectStopNames(JsonObject stop, Map<Integer, String> names) {
        if (stop.has("id") && stop.has("short_name")) {
            int id = stop.get("id").getAsInt();
            String name = stop.get("short_name").getAsString();
            names.put(id, name);
        }

        if (stop.has("stops")) {
            for (JsonElement child : stop.getAsJsonArray("stops")) {
                collectStopNames(child.getAsJsonObject(), names);
            }
        }
    }

    private Optional<Trip> getTripFor(int origin, int destId, String destName) {
        String date = LocalDate.now().toString();
        String url = faresUrl + "?origin_id=" + origin + "&destination_id=" + destId + "&date=" + date;

        Request request = new Request.Builder()
                .url(url)
                .addHeader("Authorization", "Token " + apiKey)
                .build();

        try (Response response = client.newCall(request).execute()) {
            JsonArray fares = gson.fromJson(response.body().string(), JsonObject.class)
                    .getAsJsonArray("fares");

            if (fares.isEmpty()) return Optional.empty();

            JsonObject f = fares.get(0).getAsJsonObject();
            String originName = destNameFromId(origin);

            return Optional.of(new Trip(
                    "feeder-blablacar",
                    originName,
                    destName,
                    Instant.parse(f.get("departure").getAsString()),
                    Instant.parse(f.get("arrival").getAsString()),
                    f.get("price_cents").getAsInt(),
                    f.get("price_currency").getAsString()
            ));
        } catch (Exception e) {
            System.err.println("Error destino " + destId + ": " + e.getMessage());
            return Optional.empty();
        }
    }

    private String destNameFromId(int id) {
        return getAvailableDestinations().stream()
                .filter(d -> d.id() == id)
                .map(Destination::name)
                .findFirst()
                .orElse("ID_" + id);
    }

    public record Destination(int id, String name) {}
}
