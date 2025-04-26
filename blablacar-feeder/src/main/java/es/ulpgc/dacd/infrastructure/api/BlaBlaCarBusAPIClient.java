package es.ulpgc.dacd.infrastructure.api;
import es.ulpgc.dacd.config.EnvLoader;


import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class BlaBlaCarBusAPIClient {
    private final OkHttpClient client = new OkHttpClient();
    private final String apiUrl;
    private final String apiKey;

    public BlaBlaCarBusAPIClient() {
        this.apiUrl = EnvLoader.load("BLABLACAR_SEARCH_API");
        this.apiKey = EnvLoader.load("BLABLACAR_API_KEY");
        validateConfig();
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
                throw new Exception("❌ Error en API BlaBlaCar Bus: " + response);
            }
            return response.body().string();
        }
    }

    private void validateConfig() {
        if (apiUrl == null || apiKey == null) {
            throw new IllegalStateException("❌ Faltan variables BLABLACAR_SEARCH_API o BLABLACAR_API_KEY en .env");
        }
    }
}
