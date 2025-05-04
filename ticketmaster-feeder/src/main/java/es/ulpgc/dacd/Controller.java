package es.ulpgc.dacd;

import es.ulpgc.dacd.domain.Event;
import es.ulpgc.dacd.ports.EventStorage;

import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class Controller {
    private final EventStorage storage;

        this.storage = storage;
    }

    public void execute() {
        ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();

        scheduler.scheduleAtFixedRate(() -> {
            System.out.println("Ejecutando consulta de eventos...");
            for (Event event : events) {
                storage.save(event);
                System.out.printf(formatEvent(event));
            }
        }, 0, 1, TimeUnit.HOURS);
    }

    private String formatEvent(Event event) {
        return String.format("Evento: %s | %s | %s | %s\n",
                event.getId(),
                event.getName(),
                event.getDate(),
                event.getCity());
    }
}
