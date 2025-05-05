package es.ulpgc.dacd.infrastructure;

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

    public Optional<JsonObject> fetchFare(int originId, int destinationId) throws IOException {
        String date = LocalDate.now().toString();
        String url = faresUrl + "?origin_id=" + originId + "&destination_id=" + destinationId + "&date=" + date;

        Request request = new Request.Builder()
                .url(url)
                .addHeader("Authorization", "Token " + apiKey)
                .build();

        try (Response response = client.newCall(request).execute()) {
            JsonArray fares = gson.fromJson(response.body().string(), JsonObject.class)
                    .getAsJsonArray("fares");

            if (fares.isEmpty()) return Optional.empty();
            return Optional.of(fares.get(0).getAsJsonObject());
        }
    }
}
