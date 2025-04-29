package es.ulpgc.dacd.entrypoints.scheduler;

import es.ulpgc.dacd.application.service.EventService;
import es.ulpgc.dacd.infrastructure.api.*;
import es.ulpgc.dacd.infrastructure.persistence.SQLiteEventsRepository;
import java.util.concurrent.*;

public class SchedulerMain {

    private static final String DB = "jdbc:sqlite:data.db";

    public static void main(String[] a) {
        EventService svc = new EventService(
                new TicketMasterEvents(new TicketMasterAPIClient()),
                new SQLiteEventsRepository(DB));
        ScheduledExecutorService ex = Executors.newSingleThreadScheduledExecutor();
        ex.scheduleAtFixedRate(svc::run, 0, 1, TimeUnit.HOURS);
    }
}
