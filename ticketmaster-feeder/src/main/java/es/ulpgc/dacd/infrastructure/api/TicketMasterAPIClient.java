package es.ulpgc.dacd.infrastructure.api;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class TicketMasterAPIClient {
    private final String apiUrl;
    private final String apiKey;
    private final OkHttpClient client = new OkHttpClient();

    public TicketMasterAPIClient() {
        this.apiUrl = EnvLoader.load("TICKETMASTER_API_URL");
        this.apiKey = EnvLoader.load("TICKETMASTER_API_KEY");

        if (apiUrl == null || apiKey == null) {
            throw new IllegalStateException("❌ Missing TicketMaster API configuration. Check your .env file.");
        }
    }

    public String fetchEventsJson() throws Exception {
        String fullUrl = apiUrl + "?apikey=" + apiKey + "&countryCode=ES";

        Request request = new Request.Builder()
                .url(fullUrl)
                .build();

        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                throw new Exception("API error: " + response);
            }
            return response.body().string();
        }
    }
}
