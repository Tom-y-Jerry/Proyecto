package es.ulpgc.dacd.infrastructure.api;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class BlaBlaCarAPIClient {
    private static final String API_URL = "https://bus-api.blablacar.com/v3/stops";
    private final String apiKey;
    private final OkHttpClient client = new OkHttpClient();

    public BlaBlaCarAPIClient(String apiKey) {
        // Ruta externa al archivo que contiene la API key
        this.apiKey = ApiKeyLoader.loadApiKey();
    }

    public String fetchStopsJson() throws Exception {
        Request request = new Request.Builder()
                .url(API_URL)
                .addHeader("Authorization", "Token " + apiKey)
                .addHeader("apikeyblablacar.txt" , apiKey)
                .build();

        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                throw new Exception("API error: " + response);
            }
            return response.body().string();
        }
    }
}
