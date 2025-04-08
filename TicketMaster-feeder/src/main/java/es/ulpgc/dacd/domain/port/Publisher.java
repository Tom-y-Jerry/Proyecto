package es.ulpgc.dacd.domain.port;

import es.ulpgc.dacd.domain.model.Event;

public interface Publisher {
    void publish(Event event);
}

