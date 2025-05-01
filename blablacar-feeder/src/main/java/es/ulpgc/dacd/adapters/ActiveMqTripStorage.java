package es.ulpgc.dacd.adapters;

import com.google.gson.*;
import es.ulpgc.dacd.domain.Trip;
import es.ulpgc.dacd.ports.TripStorage;
import org.apache.activemq.ActiveMQConnectionFactory;

import javax.jms.*;
import java.time.Instant;

public class ActiveMqTripStorage implements TripStorage {
    private final String brokerUrl = "tcp://localhost:61616";

    private final Gson gson = new GsonBuilder()
            .registerTypeAdapter(Instant.class, (JsonSerializer<Instant>) (src, typeOfSrc, context) ->
                    new JsonPrimitive(src.toString()))
            .create();

    @Override
    public void save(Trip trip) {
        try {
            ConnectionFactory factory = new ActiveMQConnectionFactory(brokerUrl);
            Connection connection = factory.createConnection();
            connection.start();

            Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
            Destination topic = session.createTopic("prediction.Trips");
            MessageProducer producer = session.createProducer(topic);

            String json = gson.toJson(trip);
            TextMessage message = session.createTextMessage(json);
            producer.send(message);

            producer.close();
            session.close();
            connection.close();


        } catch (Exception e) {
            System.err.println("Error publicando trip: " + e.getMessage());
        }
    }
}
