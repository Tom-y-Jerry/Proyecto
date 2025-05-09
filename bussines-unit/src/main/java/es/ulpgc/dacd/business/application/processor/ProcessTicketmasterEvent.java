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
        JsonObject json = JsonParser.parseString(rawEvent).getAsJsonObject();
        try {
            String id = json.get("id").getAsString();
            String name = json.get("name").getAsString();
            String date = json.get("date").getAsString();
            String time = json.get("time").getAsString();
            String city = json.get("city").getAsString();
            String ss = json.get("ss").getAsString();

            datamart.insertEvent(id, name, date, time, city, ss, rawEvent);
        } catch (Exception e) {
            throw new RuntimeException("Error processing Ticketmaster event", e);
        }
    }
}
