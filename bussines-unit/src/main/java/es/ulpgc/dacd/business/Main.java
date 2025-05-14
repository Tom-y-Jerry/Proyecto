package es.ulpgc.dacd.business;
import es.ulpgc.dacd.business.gui.EventViewerGUI;
import es.ulpgc.dacd.business.persistence.SQLiteDatamart;

public class Main {
    public static void main(String[] args) {
        if (args.length < 2) {
            System.err.println("You need to pass: <brokerUrl> <dbPath>");
            System.exit(1);
        }

        String brokerUrl = args[0];
        String dbPath = args[1];

        var datamart = new SQLiteDatamart(dbPath);
        var controller = new Controller(datamart, brokerUrl);

        controller.loadHistoricalEvents();
        controller.subscribeToTopics();
        launchUserInterface(dbPath);
    }

    private static void launchUserInterface(String dbPath) {
        try {
            new EventViewerGUI(dbPath).start();
        } catch (Exception e) {
            System.err.println("Error starting GUI: " + e.getMessage());
        }
    }
}
