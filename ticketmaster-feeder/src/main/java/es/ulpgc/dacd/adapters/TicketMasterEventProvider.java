package es.ulpgc.dacd.adapters;

import com.google.gson.*;
import es.ulpgc.dacd.domain.Event;
import es.ulpgc.dacd.ports.EventProvider;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import java.util.ArrayList;
import java.util.List;

public class TicketMasterEventProvider implements EventProvider {
    private final OkHttpClient client = new OkHttpClient();
    private final Gson gson = new Gson();

    private final String eventsUrl;
    private final String apiKey;

    public TicketMasterEventProvider(String eventsUrl, String apiKey) {
        this.eventsUrl = eventsUrl;
        this.apiKey = apiKey;
    }

    @Override
    public List<Event> provide() {
        List<Event> events = new ArrayList<>();

        try {
            Request request = new Request.Builder()
                    .url(eventsUrl + "?apikey=" + apiKey + "&countryCode=GB&size=200")
                    .build();

            Response response = client.newCall(request).execute();
            String body = response.body().string();

            JsonObject json = gson.fromJson(body, JsonObject.class);
            JsonArray eventsArray = json.getAsJsonObject("_embedded").getAsJsonArray("events");

            for (JsonElement e : eventsArray) {
                JsonObject obj = e.getAsJsonObject();

                String id = obj.get("id").getAsString();
                String name = obj.get("name").getAsString();
                String date = obj.getAsJsonObject("dates")
                        .getAsJsonObject("start")
                        .get("localDate").getAsString();

                String city = obj.getAsJsonObject("_embedded")
                        .getAsJsonArray("venues")
                        .get(0).getAsJsonObject()
                        .getAsJsonObject("city")
                        .get("name").getAsString();

                events.add(new Event(id, name, date, city));
            }

        } catch (Exception e) {
            System.err.println("Error al obtener eventos: " + e.getMessage());
        }

        return events;
    }
}


