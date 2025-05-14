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

    private final List<Integer> originIds = List.of(11, 90, 16, 298, 3, 1669, 1612, 1590, 1579, 808, 1578,
            1566, 139, 731, 331, 420);

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
        String date = LocalDate.now().toString();
        List<JsonObject> results = new ArrayList<>();

        for (int originId : originIds) {
            String url = faresUrl + "?origin_id=" + originId + "&date=" + date;

            Request request = new Request.Builder()
                    .url(url)
                    .addHeader("Authorization", "Token " + apiKey)
                    .build();

            try (Response response = client.newCall(request).execute()) {
                if (!response.isSuccessful()) {
                    System.err.printf("Error con origen %d: %d%n", originId, response.code());
                    continue;
                }

                JsonArray fares = gson.fromJson(response.body().string(), JsonObject.class)
                        .getAsJsonArray("fares");

                for (JsonElement el : fares) {
                    results.add(el.getAsJsonObject());
                }

            } catch (IOException e) {
                System.err.printf("Excepción con origen %d: %s%n", originId, e.getMessage());
            }
        }

        return results;
    }
}
