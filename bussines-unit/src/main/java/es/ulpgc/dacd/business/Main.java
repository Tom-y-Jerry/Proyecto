package es.ulpgc.dacd.business;

import es.ulpgc.dacd.business.application.processor.*;
import es.ulpgc.dacd.business.application.service.DatamartService;
import es.ulpgc.dacd.business.domain.model.Event;
import es.ulpgc.dacd.business.domain.model.Trip;
import es.ulpgc.dacd.business.infrastructure.messaging.*;
import es.ulpgc.dacd.business.infrastructure.persistence.SQLiteDatamart;
import es.ulpgc.dacd.business.gui.EventViewerGUI;

public class Main {
    public static void main(String[] args) {
        if (args.length < 2) {
            System.err.println("You need to pass: <brokerUrl> <dbPath>");
            System.exit(1);
        }

        String brokerUrl = args[0];
        String dbPath = args[1];

        DatamartService datamart = setupDatamart(dbPath);

        loadHistoricalEvents(datamart);
        subscribeToTopics(datamart, brokerUrl);
        launchUserInterface(dbPath);
    }

    private static DatamartService setupDatamart(String dbPath) {
        return new SQLiteDatamart(dbPath);
    }

    private static void loadHistoricalEvents(DatamartService datamart) {
        processHistorical("eventstore/Events/feeder-ticketmaster", new ProcessTicketmasterEvent(datamart));
        processHistorical("eventstore/Trips/feeder-blablacar", new ProcessBlaBlaCarTrip(datamart));
    }

    private static void processHistorical(String path, EventProcessor<?> processor) {
        new HistoricalEventLoader(processor).loadFromDirectory(path);
    }

    private static void subscribeToTopics(DatamartService datamart, String brokerUrl) {
        subscribe("Events", new ProcessTicketmasterEvent(datamart), brokerUrl, Event.class);
        subscribe("Trips", new ProcessBlaBlaCarTrip(datamart), brokerUrl, Trip.class);
    }


    private static <T> void subscribe(String topic, EventProcessor<T> processor, String brokerUrl, Class<T> type) {
        new ActiveMQConsumer<>(topic, brokerUrl, processor, type).start();
    }


    private static void launchUserInterface(String dbPath) {
        try {
            new EventViewerGUI(dbPath).start();
        } catch (Exception e) {
            System.err.println("Error starting GUI: " + e.getMessage());
        }
    }
}