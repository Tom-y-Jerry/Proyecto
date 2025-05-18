package es.ulpgc.dacd.business;
import es.ulpgc.dacd.business.gui.EventViewerGUI;
import es.ulpgc.dacd.business.datamart.SQLiteSaver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;

public class Main {
    private static final Logger log = LoggerFactory.getLogger(Main.class);

    public static void main(String[] args){
        if (args.length < 2) {
            log.warn("You need to pass: <brokerUrl> <dbPath>");
            System.exit(1);
        }

        String brokerUrl = args[0];
        String dbPath = args[1];

        var datamart = new SQLiteSaver(dbPath);
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
