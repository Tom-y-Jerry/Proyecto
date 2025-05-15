package es.ulpgc.dacd.eventstorebuilder;

import es.ulpgc.dacd.eventstorebuilder.adapters.EventListener;
import es.ulpgc.dacd.eventstorebuilder.adapters.FileEventStore;
import es.ulpgc.dacd.eventstorebuilder.adapters.EventBrokerConnection;

import java.util.Arrays;

public class Main {
    public static void main(String[] args) {
        if (args.length < 2) {
            System.err.println("Usage: <brokerUrl> <clientId> [<topic1> <topic2> ...]");
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
