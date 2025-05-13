package es.ulpgc.dacd.infrastructure.adapter;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializer;
import es.ulpgc.dacd.domain.Event;
import es.ulpgc.dacd.infrastructure.ports.EventStorage;
import org.apache.activemq.ActiveMQConnectionFactory;

import javax.jms.*;
import java.time.Instant;

public class ActiveMQEventStorage implements EventStorage {
    Gson gson = new GsonBuilder()
            .registerTypeAdapter(Instant.class, (JsonSerializer<Instant>) (src, typeOfSrc, context) ->
                    new JsonPrimitive(src.toString()))
            .create();

    @Override
    public void save(Event event) {
        try {
            String brokerUrl = "tcp://localhost:61616";
            ConnectionFactory factory = new ActiveMQConnectionFactory(brokerUrl);
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
            System.err.println("Error publicando evento: " + e.getMessage());
        }
    }
}



