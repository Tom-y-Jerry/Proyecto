package es.ulpgc.dacd.business;

import es.ulpgc.dacd.business.application.processor.*;
import es.ulpgc.dacd.business.application.service.DatamartService;
import es.ulpgc.dacd.business.infrastructure.messaging.*;
import es.ulpgc.dacd.business.infrastructure.persistence.SQLiteDatamart;
import es.ulpgc.dacd.business.gui.EventViewerGUI;

public class Main {
    private static final String BROKER_URL = "tcp://localhost:61616";
    private static final String DB_PATH = "datamart.db";

    public static void main(String[] args) {
        DatamartService datamart = setupDatamart();

        loadHistoricalEvents(datamart);
        subscribeToTopics(datamart);
        launchUserInterface();
    }

    private static DatamartService setupDatamart() {
        return new SQLiteDatamart(DB_PATH);
    }

    private static void loadHistoricalEvents(DatamartService datamart) {
        processHistorical("eventstore/prediction.Events/feeder-ticketmaster", new ProcessTicketmasterEvent(datamart));
        processHistorical("eventstore/prediction.Trips/feeder-blablacar", new ProcessBlaBlaCarTrip(datamart));
    }

    private static void processHistorical(String path, EventProcessor processor) {
        new HistoricalEventLoader(processor).loadFromDirectory(path);
    }

    private static void subscribeToTopics(DatamartService datamart) {
        subscribe("prediction.Events", new ProcessTicketmasterEvent(datamart));
        subscribe("prediction.Trips", new ProcessBlaBlaCarTrip(datamart));
    }

    private static void subscribe(String topic, EventProcessor processor) {
        new ActiveMQConsumer(topic, BROKER_URL, processor).start();
    }

    private static void launchUserInterface() {
        try {
            new EventViewerGUI(DB_PATH).start();
        } catch (Exception e) {
            System.err.println("Error starting GUI: " + e.getMessage());
        }
    }
}

