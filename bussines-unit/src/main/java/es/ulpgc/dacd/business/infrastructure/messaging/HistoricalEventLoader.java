package es.ulpgc.dacd.business.infrastructure.messaging;

import es.ulpgc.dacd.business.application.processor.EventProcessor;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;

public class HistoricalEventLoader {
    private final EventProcessor processor;

    public HistoricalEventLoader(EventProcessor processor) {
        this.processor = processor;
    }

    public void loadFromDirectory(String directoryPath) {
        File folder = new File(directoryPath);
        loadRecursively(folder);
    }

    private void loadRecursively(File file) {
        if (file.isDirectory()) {
            File[] children = file.listFiles();
            if (children != null) {
                for (File child : children) {
                    loadRecursively(child);
                }
            }
        } else if (file.getName().endsWith(".events")) {
            loadFromFile(file);
        }
    }

    private void loadFromFile(File file) {
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                processor.process(line);
            }
            System.out.println("📂 Cargado archivo histórico: " + file.getName());
        } catch (Exception e) {
            System.err.println("❌ Error leyendo archivo: " + file.getAbsolutePath());
        }
    }
}
