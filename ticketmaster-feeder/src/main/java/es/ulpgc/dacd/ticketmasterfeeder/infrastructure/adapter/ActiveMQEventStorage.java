package es.ulpgc.dacd.ticketmasterfeeder.infrastructure.adapter;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializer;
import es.ulpgc.dacd.ticketmasterfeeder.domain.Event;
import es.ulpgc.dacd.ticketmasterfeeder.infrastructure.ports.EventStorage;
import org.apache.activemq.ActiveMQConnectionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.jms.*;
import java.time.Instant;

public class ActiveMQEventStorage implements EventStorage {
    private static final Logger log = LoggerFactory.getLogger(ActiveMQEventStorage.class);

    private final String brokerurl;
    private final Gson gson = new GsonBuilder()
            .registerTypeAdapter(Instant.class, (JsonSerializer<Instant>) (src, typeOfSrc, context) ->
                    new JsonPrimitive(src.toString()))
            .create();

    public ActiveMQEventStorage(String brokerurl) {
        this.brokerurl = brokerurl;
    }

    @Override
    public void save(Event event) {
        try {
            ConnectionFactory factory = new ActiveMQConnectionFactory(brokerurl);
            Connection connection = factory.createConnection();
            connection.start();

            Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
            Destination topic = session.createTopic("Events");
            MessageProducer producer = session.createProducer(topic);

            String json = gson.toJson(event);
            TextMessage message = session.createTextMessage(json);
            producer.send(message);

            producer.close();
            session.close();
            connection.close();
        } catch (Exception e) {
            log.error("Error publicando evento: {}", e.getMessage(), e);
        }
    }
}