package es.ulpgc.dacd.blablacarfeeder.infrastructure;
import com.google.gson.*;
import okhttp3.*;
import java.io.IOException;
import java.time.LocalDate;
import java.util.*;

public class BlablacarApiClient {
    private final OkHttpClient client = new OkHttpClient();
    private final Gson gson = new Gson();

    private final String stopsUrl;
    private final String faresUrl;
    private final String apiKey;

    private final List<Integer> originIds = List.of(11, 90, 16, 298, 3, 914, 1671, 1669, 1612, 1590, 1579);

    public BlablacarApiClient(String stopsUrl, String faresUrl, String apiKey) {
        this.stopsUrl = stopsUrl;
        this.faresUrl = faresUrl;
        this.apiKey = apiKey;
    }

    public JsonArray fetchStops() throws IOException {
        Request request = new Request.Builder()
                .url(stopsUrl)
                .addHeader("Authorization", "Token " + apiKey)
                .build();
        try (Response response = client.newCall(request).execute()) {
            return gson.fromJson(response.body().string(), JsonObject.class).getAsJsonArray("stops");
        }
    }

    public List<JsonObject> fetchFare() throws IOException {
        String Date = LocalDate.now().toString();
        List<JsonObject> responses = new ArrayList<>();

        for (int originId : originIds) {
            for (int destinationId : originIds) {
                if (originId == destinationId) continue;

                String url = faresUrl + "?origin_id=" + originId + "&destination_id=" + destinationId + "&date=" + Date;

                Request request = new Request.Builder()
                        .url(url)
                        .addHeader("Authorization", "Token " + apiKey)
                        .build();

                try (Response response = client.newCall(request).execute()) {
                    if (!response.isSuccessful()) {
                        System.err.println("Error con origen " + originId + " y destino " + destinationId + ": " + response.code());
                        continue;
                    }

                    JsonArray fares = gson.fromJson(response.body().string(), JsonObject.class).getAsJsonArray("fares");

                    if (!fares.isEmpty()) {
                        responses.add(fares.get(0).getAsJsonObject());
                    }
                }
            }
        }

        return responses;
    }
}