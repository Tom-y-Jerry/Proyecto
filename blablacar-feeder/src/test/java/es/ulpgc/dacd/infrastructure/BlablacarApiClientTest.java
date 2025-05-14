package es.ulpgc.dacd.infrastructure;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import es.ulpgc.dacd.blablacarfeeder.infrastructure.BlablacarApiClient;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class BlablacarApiClientTest {

    @Test
    public void shouldExtractOriginIdsCorrectly() throws Exception {
        BlablacarApiClient client = mock(BlablacarApiClient.class);

        JsonArray stops = new JsonArray();

        JsonObject stop1 = new JsonObject();
        stop1.addProperty("id", 1);
        stop1.addProperty("short_name", "Las Palmas");
        stop1.add("destinations_ids", new JsonArray());
        stops.add(stop1);

        JsonObject stop2 = new JsonObject();
        stop2.addProperty("id", 2);
        stop2.addProperty("short_name", "Tenerife");
        stop2.add("destinations_ids", new JsonArray());
        stops.add(stop2);

        when(client.fetchStops()).thenReturn(stops);
        JsonArray fetchedStops = client.fetchStops();

        Map<Integer, String> origins = new HashMap<>();
        for (JsonElement el : stops) {
            JsonObject stop = el.getAsJsonObject();
            if (stop.has("id") && stop.has("short_name")) {
                origins.put(stop.get("id").getAsInt(), stop.get("short_name").getAsString());
            }
        }


        assertEquals(2, origins.size());
        assertEquals("Las Palmas", origins.get(1));
        assertEquals("Tenerife", origins.get(2));
    }
}
