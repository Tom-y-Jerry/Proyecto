package es.ulpgc.dacd.business.infrastructure.messaging;

import com.google.gson.*;
import es.ulpgc.dacd.business.application.processor.EventProcessor;

import java.io.*;
import java.lang.reflect.Type;
import java.time.Instant;

public class HistoricalEventLoader<T> {
    private final EventProcessor<T> processor;
    private final Class<T> type;
    private final Gson gson;

    public HistoricalEventLoader(EventProcessor<T> processor, Class<T> type) {
        this.processor = processor;
        this.type = type;
        this.gson = new GsonBuilder()
                .registerTypeAdapter(Instant.class, new InstantDeserializer())
                .create();
    }

    public void loadFromDirectory(String directoryPath) {
        File folder = new File(directoryPath);
        traverseDirectory(folder);
    }

    private void traverseDirectory(File file) {
        if (file.isDirectory()) {
            File[] children = file.listFiles();
            if (children != null) {
                for (File child : children) traverseDirectory(child);
            }
        } else if (file.getName().endsWith(".events")) {
            loadFile(file);
        }
    }

    private void loadFile(File file) {
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                T item = gson.fromJson(line, type);
                processor.process(item);
            }
            System.out.println("Cargado archivo histórico: " + file.getName());
        } catch (Exception e) {
            System.err.println("Error leyendo archivo: " + file.getAbsolutePath());
        }
    }

    private static class InstantDeserializer implements JsonDeserializer<Instant> {
        @Override
        public Instant deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context)
                throws JsonParseException {
            return Instant.parse(json.getAsString());
        }
    }
}
