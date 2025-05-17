package es.ulpgc.dacd.ticketmasterfeeder.infrastructure.adapter;
import com.google.gson.*;
import es.ulpgc.dacd.ticketmasterfeeder.domain.Event;
import es.ulpgc.dacd.ticketmasterfeeder.infrastructure.ports.EventStorage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.jms.*;
import java.time.Instant;

public class ActiveMQEventStorage implements EventStorage {
    private static final Logger log = LoggerFactory.getLogger(ActiveMQEventStorage.class);

    private final Gson gson;
    private final ActiveMQConnectionManager manager;

    public ActiveMQEventStorage(String brokerUrl) {
        this.gson = new GsonBuilder()
                .registerTypeAdapter(Instant.class, (JsonSerializer<Instant>) (src, typeOfSrc, context) ->
                        new JsonPrimitive(src.toString()))
                .create();
        this.manager = new ActiveMQConnectionManager(brokerUrl);
    }

    @Override
    public void save(Event event) {
        Connection connection = null;
        Session session = null;
        MessageProducer producer = null;
        try {
            connection = manager.createConnection();
            session = manager.createSession(connection);
            producer = manager.createProducer(session, "Events");

            String json = gson.toJson(event);
            TextMessage message = session.createTextMessage(json);
            producer.send(message);
        } catch (Exception e) {
            log.error("Error publicando evento: {}", e.getMessage(), e);
        } finally {
            closeResources(producer, session, connection);
        }
    }

    private void closeResources(MessageProducer producer, Session session, Connection connection) {
        try {
            if (producer != null) producer.close();
            if (session != null) session.close();
            if (connection != null) connection.close();
        } catch (Exception e) {
            log.warn("Error cerrando recursos JMS: {}", e.getMessage(), e);
        }
    }
}
