package es.ulpgc.dacd.eventstorebuilder;

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
        Controller controller = new Controller();
        controller.start(brokerUrl, clientId, topics);
    }
}
