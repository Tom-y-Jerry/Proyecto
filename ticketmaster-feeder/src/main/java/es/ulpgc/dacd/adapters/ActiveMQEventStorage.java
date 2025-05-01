package es.ulpgc.dacd.adapters;

import com.google.gson.*;
import es.ulpgc.dacd.domain.Event;
import es.ulpgc.dacd.ports.EventStorage;
import org.apache.activemq.ActiveMQConnectionFactory;

import javax.jms.*;
import java.time.Instant;

public class ActiveMQEventStorage implements EventStorage {
    private final String brokerUrl = "tcp://localhost:61616";

    private final Gson gson = new GsonBuilder()
            .registerTypeAdapter(Instant.class, (JsonSerializer<Instant>) (src, typeOfSrc, context) ->
                    new JsonPrimitive(src.toString()))
            .create();

    @Override
    public void save(Event event) {
        try {
            ConnectionFactory factory = new ActiveMQConnectionFactory(brokerUrl);
            Connection connection = factory.createConnection();
            connection.start();

            Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
            Destination topic = session.createTopic("prediction.Events");
            MessageProducer producer = session.createProducer(topic);

            String json = String.format(
                    "{\"ts\":\"%s\",\"ss\":\"ticketmaster\",\"id\":\"%s\",\"nombre\":\"%s\",\"fecha\":\"%s\",\"ciudad\":\"%s\"}",
                    Instant.now(),
                    event.getId(),
                    event.getName(),
                    event.getDate(),
                    event.getCity()
            );

            TextMessage message = session.createTextMessage(json);
            producer.send(message);

            producer.close();
            session.close();
            connection.close();

        } catch (Exception e) {
            System.err.println("Error publicando evento: " + e.getMessage());
        }
    }
}