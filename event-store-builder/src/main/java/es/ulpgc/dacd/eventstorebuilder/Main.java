package es.ulpgc.dacd.eventstorebuilder;

import es.ulpgc.dacd.eventstorebuilder.adapters.EventListener;
import es.ulpgc.dacd.eventstorebuilder.adapters.FileEventStore;
import es.ulpgc.dacd.eventstorebuilder.adapters.EventBrokerConnection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;

public class Main {
    private static final Logger log = LoggerFactory.getLogger(Main.class);

    public static void main(String[] args) {
        if (args.length < 2) {
            log.warn("Usage: <brokerUrl> <clientId> [<topic1> <topic2> ...]");
            return;
        }

        String brokerUrl = args[0];
        String clientId = args[1];
        String[] topics = (args.length > 2) ? Arrays.copyOfRange(args, 2, args.length) : new String[]{"Trips", "Events"};

        EventBrokerConnection broker = new EventBrokerConnection(brokerUrl, clientId);
        EventListener listener = new EventListener(broker.getSession(), new FileEventStore());

        Controller controller = new Controller(listener);
        controller.subscribeToTopics(topics);
    }
}
