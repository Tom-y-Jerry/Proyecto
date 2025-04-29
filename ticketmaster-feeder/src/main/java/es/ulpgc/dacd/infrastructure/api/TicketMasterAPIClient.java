package es.ulpgc.dacd.infrastructure.api;

import es.ulpgc.dacd.config.EnvLoader;
import okhttp3.*;

public final class TicketMasterAPIClient {

    private static final OkHttpClient HTTP = new OkHttpClient();
    private final String url = EnvLoader.get("TICKETMASTER_API_URL");
    private final String key = EnvLoader.get("TICKETMASTER_API_KEY");

    public String fetchJson() throws Exception {
        Request r = new Request.Builder()
                .url(url + "?apikey=" + key + "&countryCode=ES")
                .build();
        try (Response res = HTTP.newCall(r).execute()) {
            if (!res.isSuccessful()) throw new Exception("HTTP " + res.code());
            return res.body().string();
        }
    }
}
