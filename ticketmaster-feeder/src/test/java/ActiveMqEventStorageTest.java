

import static org.junit.jupiter.api.Assertions.*;

import es.ulpgc.dacd.ticketmasterfeeder.infrastructure.adapter.ActiveMQConnectionManager;
import es.ulpgc.dacd.ticketmasterfeeder.infrastructure.adapter.ActiveMQEventStorage;
import org.junit.jupiter.api.Test;
import javax.jms.*;
import es.ulpgc.dacd.ticketmasterfeeder.domain.Event;
import java.time.Instant;

class ActiveMqEventStorageTest {

    @Test
    void publishesAndReceivesEvent() throws Exception {
        String brokerUrl = "vm://localhost?broker.persistent=false";
        ActiveMQConnectionManager mgr = new ActiveMQConnectionManager(brokerUrl);

        Connection conn = mgr.createConnection();
        conn.start();
        Session session = mgr.createSession(conn);
        MessageConsumer consumer =
                session.createConsumer(session.createTopic("Events"));

        ActiveMQEventStorage storage = new ActiveMQEventStorage(brokerUrl);
        Event event = new Event(
                Instant.now(), "src", "id", "name",
                Instant.now(), "time", "city"
        );
        storage.save(event);

        TextMessage msg = (TextMessage) consumer.receive(1_000);
        assertNotNull(msg, "No llegó ningún mensaje");
        assertTrue(msg.getText().contains("\"id\":\"id\""));

        session.close();
        conn.close();
    }
}