package es.ulpgc.dacd.infrastructure;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import java.io.IOException;

public class TicketMasterApiClient {
    private final OkHttpClient client = new OkHttpClient();
    private final String eventsUrl;
    private final String apiKey;

    public TicketMasterApiClient(String eventsUrl, String apiKey) {
        this.eventsUrl = eventsUrl;
        this.apiKey = apiKey;
    }

    public String fetchEventsJson() throws IOException {
        Request request = new Request.Builder()
                .url(eventsUrl + "?apikey=" + apiKey + "&countryCode=GB&size=200")
                .build();

        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                throw new IOException("Unexpected response code: " + response.code());
            }
            return response.body().string();
        }
    }
}

