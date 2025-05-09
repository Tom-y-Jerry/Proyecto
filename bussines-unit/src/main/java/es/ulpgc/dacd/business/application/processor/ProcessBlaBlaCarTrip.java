package es.ulpgc.dacd.business.application.processor;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import es.ulpgc.dacd.business.application.service.DatamartService;

import java.time.Duration;
import java.time.Instant;

public class ProcessBlaBlaCarTrip implements EventProcessor {
    private final DatamartService datamart;

    public ProcessBlaBlaCarTrip(DatamartService datamart) {
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
            long duration = Duration.between(Instant.parse(departure), Instant.parse(arrival)).toMinutes();
            String ss = json.get("ss").getAsString();

            datamart.insertTrip(origin, destination, departure, arrival, price, currency, duration, ss, rawEvent);
        } catch (Exception e) {
            throw new RuntimeException("Error processing BlaBlaCar trip", e);
        }
    }
}
