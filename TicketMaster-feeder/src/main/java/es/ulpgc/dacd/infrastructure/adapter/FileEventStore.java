package es.ulpgc.dacd.infrastructure.adapter;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import es.ulpgc.dacd.domain.model.Event;
import es.ulpgc.dacd.domain.port.Subscriber;
import java.io.*;
import java.time.LocalDate;

public class FileEventStore implements Subscriber {
    private final Gson gson = new Gson();

    @Override
    public void store(Event event) {
        String ts = java.time.Instant.now().toString();
        String ss = "ticketmaster";
        String date = ts.substring(0, 10).replace("-", "");
        String path = String.format("eventstore/%s/%s.events", ss, date);

        JsonObject json = new JsonObject();
        json.addProperty("ts", ts);
        json.addProperty("ss", ss);
        json.addProperty("id", event.getId());
        json.addProperty("nombre", event.getName());
        json.addProperty("fecha", event.getDate());
        json.addProperty("ciudad", event.getCity());

        File file = new File(path);
        file.getParentFile().mkdirs();

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(file, true))) {
            writer.write(json.toString());
            writer.newLine();
            System.out.println("📝 Evento guardado: " + json);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}

