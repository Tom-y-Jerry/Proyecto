package es.ulpgc.dacd.eventstorebuilder;

import es.ulpgc.dacd.eventstorebuilder.infrastructure.adapters.EventListener;

public class Controller {
    private final EventListener listener;

    public Controller(EventListener listener) {
        this.listener = listener;
    }

    public void subscribeToTopics(String... topics) {
        for (String topic : topics) {
            System.out.println("📡 Subscribing to topic: " + topic);
            listener.subscribe(topic);
        }
    }
}
