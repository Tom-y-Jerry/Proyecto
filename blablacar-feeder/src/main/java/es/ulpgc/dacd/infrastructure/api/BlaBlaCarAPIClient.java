package es.ulpgc.dacd.infrastructure.api;
import es.ulpgc.dacd.config.EnvLoader;

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
        validateConfig();
    }

    public String fetchStopsJson() throws Exception {
        Request request = new Request.Builder()
                .url(apiUrl)
                .addHeader("Authorization", "Token " + apiKey)
                .build();
        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                throw new Exception("❌ Error en API BlaBlaCar: " + response);
            }
            return response.body().string();
        }
    }

    private void validateConfig() {
        if (apiUrl == null || apiKey == null) {
            throw new IllegalStateException("❌ Faltan variables BLABLACAR_API_URL o BLABLACAR_API_KEY en .env");
        }
    }
}
