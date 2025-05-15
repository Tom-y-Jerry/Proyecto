import es.ulpgc.dacd.eventstorebuilder.adapters.EventBrokerConnection;
import org.junit.jupiter.api.Test;

import javax.jms.Session;

import static org.junit.jupiter.api.Assertions.assertNotNull;

class EventBrokerConnectionTest {

    @Test
    void shouldReturnValidSession() {
        EventBrokerConnection connection = new EventBrokerConnection("tcp://localhost:61616", "test-client");
        Session session = connection.getSession();

        assertNotNull(session);
    }
}
