package es.ulpgc.dacd.blablacarfeeder.infrastructure.adapters;
import com.google.gson.*;
import es.ulpgc.dacd.blablacarfeeder.domain.Trip;
import es.ulpgc.dacd.blablacarfeeder.infrastructure.ports.TripStorage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import javax.jms.*;
import java.time.Instant;

public class ActiveMqTripStorage implements TripStorage {
    private static final Logger log = LoggerFactory.getLogger(ActiveMqTripStorage.class);

    private final Gson gson;
    private final ActiveMqConnectionManager manager;

    public ActiveMqTripStorage(String brokerUrl) {
        this.gson = new GsonBuilder()
                .registerTypeAdapter(Instant.class, (JsonSerializer<Instant>) (src, typeOfSrc, context) ->
                        new JsonPrimitive(src.toString()))
                .create();
        this.manager = new ActiveMqConnectionManager(brokerUrl);
    }

    @Override
    public void save(Trip trip) {
        Connection connection = null;
        Session session = null;
        MessageProducer producer = null;

        try {
            connection = manager.createConnection();
            session = manager.createSession(connection);
            producer = manager.createProducer(session, "Trips");

            String json = gson.toJson(trip);
            TextMessage message = session.createTextMessage(json);
            producer.send(message);
        } catch (Exception e) {
            log.error("Error publicando trip: {}", e.getMessage(), e);
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