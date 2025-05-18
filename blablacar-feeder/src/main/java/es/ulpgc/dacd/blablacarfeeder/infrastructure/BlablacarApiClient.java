package es.ulpgc.dacd.blablacarfeeder.infrastructure;
import com.google.gson.*;
import okhttp3.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.io.IOException;
import java.time.LocalDate;
import java.util.*;

public class BlablacarApiClient {
    private static final Logger log = LoggerFactory.getLogger(BlablacarApiClient.class);

    private final OkHttpClient client = new OkHttpClient();
    private final Gson gson = new Gson();

    private final String stopsUrl;
    private final String faresUrl;
    private final String apiKey;

    private final List<Integer> originIds = List.of(11, 16, 298, 3, 1590, 1579, 1578, 139, 731, 331, 13);

    public BlablacarApiClient(String stopsUrl, String faresUrl, String apiKey) {
        this.stopsUrl = stopsUrl;
        this.faresUrl = faresUrl;
        this.apiKey = apiKey;
    }

    public JsonArray fetchStops() throws IOException {
        Request request = new Request.Builder()
                .url(stopsUrl)
                .addHeader("Authorization", "Token " + apiKey).build();
        try (Response response = client.newCall(request).execute()) {
            return gson.fromJson(response.body().string(), JsonObject.class).getAsJsonArray("stops");
        }
    }

    public List<JsonObject> fetchFare() {
        String date = LocalDate.now().toString();
        List<JsonObject> results = new ArrayList<>();

        for (int originId : originIds) {
            String url = faresUrl + "?origin_id=" + originId + "&date=" + date;

            Request request = new Request.Builder()
                    .url(url)
                    .addHeader("Authorization", "Token " + apiKey).build();
            try (Response response = client.newCall(request).execute()) {
                if (!response.isSuccessful()) {
                    log.warn("Error con origen {}: código {}", originId, response.code());
                    continue;
                }
                JsonArray fares = gson.fromJson(response.body().string(), JsonObject.class)
                        .getAsJsonArray("fares");
                for (JsonElement ele : fares) {
                    results.add(ele.getAsJsonObject());
                }
            } catch (IOException e) {
                log.error("Excepción con origen {}: {}", originId, e.getMessage());
            }
        }
        return results;
    }
}