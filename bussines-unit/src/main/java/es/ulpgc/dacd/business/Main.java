package es.ulpgc.dacd.business;
import es.ulpgc.dacd.business.application.processor.*;
import es.ulpgc.dacd.business.application.service.DatamartService;
import es.ulpgc.dacd.business.infrastructure.messaging.*;
import es.ulpgc.dacd.business.infrastructure.persistence.SQLiteDatamart;
import es.ulpgc.dacd.business.gui.EventViewerGUI;

public class Main {
    public static void main(String[] args) {
        String brokerUrl = "tcp://localhost:61616";
        String databasePath = "datamart.db";

        DatamartService datamart = new SQLiteDatamart(databasePath);
        EventProcessor ticketmasterProcessor = new ProcessTicketmasterEvent(datamart);
        EventProcessor blablacarProcessor = new ProcessBlaBlaCarTrip(datamart);

        new HistoricalEventLoader(ticketmasterProcessor).loadFromDirectory("eventstore/prediction.Events/feeder-ticketmaster");
        new HistoricalEventLoader(blablacarProcessor).loadFromDirectory("eventstore/prediction.Trips/feeder-blablacar");

        new ActiveMQConsumer("prediction.Events", brokerUrl, ticketmasterProcessor).start();
        new ActiveMQConsumer("prediction.Trips", brokerUrl, blablacarProcessor).start();

        try {
            new EventViewerGUI(databasePath).start();
        } catch (Exception e) {
            System.err.println("Error starting GUI: " + e.getMessage());
        }
    }
}
