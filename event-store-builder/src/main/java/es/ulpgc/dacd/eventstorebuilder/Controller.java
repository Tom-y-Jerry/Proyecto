package es.ulpgc.dacd.eventstorebuilder;

import es.ulpgc.dacd.eventstorebuilder.adapters.EventListener;
import es.ulpgc.dacd.eventstorebuilder.adapters.FileEventStore;
import es.ulpgc.dacd.eventstorebuilder.adapters.EventBrokerConnection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Controller {
    private static final Logger log = LoggerFactory.getLogger(Controller.class);

    public void start(String brokerUrl, String clientId, String... topics) {
        EventBrokerConnection broker = new EventBrokerConnection(brokerUrl, clientId);
        EventListener listener = new EventListener(broker.getSession(), new FileEventStore());

        for (String topic : topics) {
            log.info("Subscribing to topic: {}", topic);
            listener.subscribe(topic);
        }
    }
}
