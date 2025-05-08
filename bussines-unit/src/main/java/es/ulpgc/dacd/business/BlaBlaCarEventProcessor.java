package es.ulpgc.dacd.business;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

public class BlaBlaCarEventProcessor implements EventProcessor {
    private final Datamart datamart;

    public BlaBlaCarEventProcessor(Datamart datamart) {
        this.datamart = datamart;
    }

    @Override
    public void process(String rawEvent) {
        JsonObject json = JsonParser.parseString(rawEvent).getAsJsonObject();

        try {
            String origin = json.get("origin").getAsString();
            String destination = json.get("destination").getAsString();
            String departure = json.get("departure").getAsString();
            String arrival = json.get("arrival").getAsString();
            double price = json.get("price").getAsDouble();
            String currency = json.get("currency").getAsString();
            String ss = json.get("ss").getAsString();

            datamart.insertBlaBlaCarEvent(origin, destination, departure, arrival, price, currency, ss, rawEvent);
        } catch (Exception e) {
            throw new RuntimeException("Error procesando evento BlaBlaCar", e);
        }
    }
}
