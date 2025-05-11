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
        try {
            JsonObject json = JsonParser.parseString(rawEvent).getAsJsonObject();
            long duration = Duration.between(
                    Instant.parse(json.get("departure").getAsString()),
                    Instant.parse(json.get("arrival").getAsString())
            ).toMinutes();

            datamart.insertTrip(
                    json.get("origin").getAsString(),
                    json.get("destination").getAsString(),
                    json.get("departure").getAsString(),
                    json.get("arrival").getAsString(),
                    json.get("price").getAsDouble(),
                    json.get("currency").getAsString(),
                    duration,
                    json.get("ss").getAsString(),
                    rawEvent
            );
        } catch (Exception e) {
            throw new RuntimeException("Error processing BlaBlaCar trip", e);
        }
    }
}

