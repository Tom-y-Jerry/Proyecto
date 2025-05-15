package es.ulpgc.dacd.business;

import es.ulpgc.dacd.business.gui.EventViewerGUI;
import es.ulpgc.dacd.business.datamart.SQLiteDatamart;

import javax.swing.*;

public class Main {
    public static void main(String[] args){
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

        SwingUtilities.invokeLater(() -> {
            try {
                new EventViewerGUI(dbPath).setVisible(true);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });
    }
}
