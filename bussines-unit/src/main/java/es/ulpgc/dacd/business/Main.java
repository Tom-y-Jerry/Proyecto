package es.ulpgc.dacd.business;

public class Main {
    public static void main(String[] args) {
        String brokerUrl = "tcp://localhost:61616";
        String databasePath = "eventstore.db";

        BusinessConsumer consumer = new BusinessConsumer(databasePath);
        consumer.subscribeTo("prediction.Events", brokerUrl);
        consumer.subscribeTo("prediction.Trips", brokerUrl);

        System.out.println("BusinessConsumer activo. Esperando eventos...");

        try {
            new EventViewerGUI(databasePath).start();
        } catch (Exception e) {
            System.err.println("Error lanzando GUI: " + e.getMessage());
        }
    }
}