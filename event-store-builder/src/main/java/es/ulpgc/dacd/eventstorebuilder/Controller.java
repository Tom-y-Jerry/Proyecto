package es.ulpgc.dacd.eventstorebuilder;

import es.ulpgc.dacd.eventstorebuilder.adapters.EventListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Controller {
    private static final Logger log = LoggerFactory.getLogger(Controller.class);

    private final EventListener listener;

    public Controller(EventListener listener) {
        this.listener = listener;
    }

    public void subscribeToTopics(String... topics) {
        for (String topic : topics) {
            log.info("Subscribing to topic: {}", topic);
            listener.subscribe(topic);
        }
    }
}