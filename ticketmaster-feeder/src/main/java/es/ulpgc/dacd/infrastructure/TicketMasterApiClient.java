package es.ulpgc.dacd.infrastructure;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class TicketMasterApiClient {
    private final OkHttpClient client = new OkHttpClient();
    private final String eventsUrl;
    private final String apiKey;

    public TicketMasterApiClient(String eventsUrl, String apiKey) {
        this.eventsUrl = eventsUrl;
        this.apiKey = apiKey;
    }

    public List<String> fetchEventsJson() throws IOException {
        String[] countryCodes = {"FR", "ES", "GB", "DE", "PT"};
        List<String> jsonResponses = new ArrayList<>();

        for (String countryCode : countryCodes) {
            String fullUrl = eventsUrl + "?apikey=" + apiKey + "&countryCode=" + countryCode + "&size=200";

            Request request = new Request.Builder()
                    .url(fullUrl)
                    .build();

            try (Response response = client.newCall(request).execute()) {
                if (!response.isSuccessful()) {
                    System.err.println("Error al obtener eventos de " + countryCode + ": " + response.code());
                    continue;
                }
                jsonResponses.add(response.body().string());
            }
        }

        return jsonResponses;
    }
}

