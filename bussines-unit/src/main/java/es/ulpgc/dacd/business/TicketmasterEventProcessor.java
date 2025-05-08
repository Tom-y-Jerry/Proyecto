package es.ulpgc.dacd.business;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

public class TicketmasterEventProcessor implements EventProcessor {
    private final Datamart datamart;

    public TicketmasterEventProcessor(Datamart datamart) {
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

            datamart.insertTicketmasterEvent(id, name, date, time, city, ss, rawEvent);

        } catch (Exception e) {
            throw new RuntimeException("Error procesando evento Ticketmaster", e);
        }
    }
}

