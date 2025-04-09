package es.ulpgc.dacd.infrastructure.api;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class BlaBlaCarAPIClient {
    private final String apiUrl;
    private final String apiKey;
    private final OkHttpClient client = new OkHttpClient();

    public BlaBlaCarAPIClient() {
        this.apiUrl = EnvLoader.load("BLABLACAR_API_URL");
        this.apiKey = EnvLoader.load("BLABLACAR_API_KEY");

        if (apiUrl == null || apiKey == null) {
            throw new IllegalStateException("❌ Missing BlaBlaCar API configuration. Check your .env file.");
        }
    }

    public String fetchStopsJson() throws Exception {
        Request request = new Request.Builder()
                .url(apiUrl)
                .addHeader("Authorization", "Token " + apiKey)
                .addHeader("apikeyblablacar.txt", apiKey)
                .build();

        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                throw new Exception("API error: " + response);
            }
            return response.body().string();
        }
    }
}
