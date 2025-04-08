package es.ulpgc.dacd.domain.port;

import es.ulpgc.dacd.domain.model.Event;

public interface Subscriber {
    void store(Event event);
}

