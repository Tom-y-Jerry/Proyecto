package es.ulpgc.dacd.business.application.processor;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import es.ulpgc.dacd.business.application.service.DatamartService;

public class ProcessTicketmasterEvent implements EventProcessor {
    private final DatamartService datamart;

    public ProcessTicketmasterEvent(DatamartService datamart) {
        this.datamart = datamart;
    }

    @Override
    public void process(String rawEvent) {
        try {
            JsonObject json = JsonParser.parseString(rawEvent).getAsJsonObject();
            datamart.insertEvent(
                    json.get("id").getAsString(),
                    json.get("name").getAsString(),
                    json.get("date").getAsString(),
                    json.get("time").getAsString(),
                    json.get("city").getAsString(),
                    json.get("ss").getAsString(),
                    rawEvent
            );
        } catch (Exception e) {
            throw new RuntimeException("Error processing Ticketmaster event", e);
        }
    }
}