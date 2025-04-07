package es.ulpgc.dacd.infrastructure.api;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class TicketMasterAPIClient {
    private final String apiKey;
    private final OkHttpClient client = new OkHttpClient();

    public TicketMasterAPIClient() {
        this.apiKey = ApiKeyLoader.loadApiKey();
    }

    public String fetchEventsJson() throws Exception {
        if (apiKey == null || apiKey.isEmpty()) {
            throw new Exception("❌ API key is null or empty. Check your .env file.");
        }

        String apiUrl = "https://app.ticketmaster.com/discovery/v2/events.json?apikey=" + apiKey + "&countryCode=ES";

        Request request = new Request.Builder()
                .url(apiUrl)
                .build();

        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                throw new Exception("API error: " + response);
            }
            return response.body().string();
        }
    }
}

