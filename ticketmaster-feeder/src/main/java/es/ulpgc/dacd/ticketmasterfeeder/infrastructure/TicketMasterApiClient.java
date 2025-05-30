package es.ulpgc.dacd.ticketmasterfeeder.infrastructure;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class TicketMasterApiClient {
    private static final Logger log = LoggerFactory.getLogger(TicketMasterApiClient.class);
    private final OkHttpClient client = new OkHttpClient();
    private final String eventsUrl;
    private final String apiKey;

    public TicketMasterApiClient(String eventsUrl, String apiKey) {
        this.eventsUrl = eventsUrl;
        this.apiKey = apiKey;
    }

    public List<String> fetchEventsJson() throws IOException {
        String[] countryCodes = {"FR", "ES", "DE", "PT", "CH", "BE", "PL", "AT"};
        List<String> jsonResponses = new ArrayList<>();

        for (String countryCode : countryCodes) {
            String fullUrl = eventsUrl + "?apikey=" + apiKey + "&countryCode=" + countryCode + "&size=200";

            Request request = new Request.Builder()
                    .url(fullUrl)
                    .build();

            try (Response response = client.newCall(request).execute()) {
                if (!response.isSuccessful()) {
                    log.error("Error catching events {}: {}", countryCode, response.code());
                    continue;
                }
                jsonResponses.add(response.body().string());
            }
        }
        return jsonResponses;
    }
}