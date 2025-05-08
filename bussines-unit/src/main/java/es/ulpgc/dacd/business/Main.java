package es.ulpgc.dacd.business;

public class Main {
    public static void main(String[] args) {
        String brokerUrl = "tcp://localhost:61616";
        String databasePath = "datamart.db";

        Datamart datamart = new Datamart(databasePath);
        EventProcessor ticketmasterProcessor = new TicketmasterEventProcessor(datamart);
        EventProcessor blablacarProcessor = new BlaBlaCarEventProcessor(datamart);

        HistoricalEventLoader ticketmasterLoader = new HistoricalEventLoader(ticketmasterProcessor);
        ticketmasterLoader.loadFromDirectory("eventstore/prediction.Events/feeder-ticketmaster");

        HistoricalEventLoader blablacarLoader = new HistoricalEventLoader(blablacarProcessor);
        blablacarLoader.loadFromDirectory("eventstore/prediction.Trips/feeder-blablacar");

        BusinessConsumer ticketmasterConsumer = new BusinessConsumer("prediction.Events", brokerUrl, ticketmasterProcessor);
        ticketmasterConsumer.start();

        BusinessConsumer blablacarConsumer = new BusinessConsumer("prediction.Trips", brokerUrl, blablacarProcessor);
        blablacarConsumer.start();

        try {
            new EventViewerGUI(databasePath).start();
        } catch (Exception e) {
            System.err.println("Error lanzando GUI: " + e.getMessage());
        }
    }
}
