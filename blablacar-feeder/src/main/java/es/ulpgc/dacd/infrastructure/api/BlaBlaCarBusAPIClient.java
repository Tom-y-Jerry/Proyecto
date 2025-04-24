package es.ulpgc.dacd.infrastructure.api;

import okhttp3.*;

public class BlaBlaCarBusAPIClient {
    private final OkHttpClient client = new OkHttpClient();
    private final String apiUrl;
    private final String apiKey;

    public BlaBlaCarBusAPIClient() {
        this.apiUrl = EnvLoader.load("BLABLACAR_SEARCH_API"); // Debe ser https://bus-api.blablacar.com/v1/fares
        this.apiKey = EnvLoader.load("BLABLACAR_API_KEY");

        if (apiKey == null || apiUrl == null) {
            throw new IllegalStateException("❌ Missing API configuration. Check your .env file.");
        }
    }

    public String fetchFaresByDate(String date) throws Exception {
        String fullUrl = apiUrl + "?date=" + date;

        Request request = new Request.Builder()
                .url(fullUrl)
                .addHeader("Authorization", "Token " + apiKey)
                .addHeader("Accept", "application/json")
                .build();

        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                throw new Exception("❌ GET API error: " + response);
            }
            return response.body().string();
        }
    }


    public String searchTrips(String date) throws Exception {
        String fullUrl = apiUrl + "?date=" + date;

        Request request = new Request.Builder()
                .url(fullUrl)
                .addHeader("Authorization", "Token " + apiKey)
                .addHeader("Accept", "application/json")
                .build();

        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                throw new Exception("❌ GET API error: " + response);
            }
            return response.body().string();
        }
    }
}