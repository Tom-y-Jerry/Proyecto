package es.ulpgc.dacd.blablacarfeeder.infrastructure.adapters;
import com.google.gson.*;
import es.ulpgc.dacd.blablacarfeeder.domain.Trip;
import es.ulpgc.dacd.blablacarfeeder.infrastructure.ports.TripStorage;
import org.apache.activemq.ActiveMQConnectionFactory;
import javax.jms.*;
import java.time.Instant;

public class ActiveMqTripStorage implements TripStorage {
    private final String brokerUrl = "tcp://localhost:61616";

    private final Gson gson = new GsonBuilder()
            .registerTypeAdapter(Instant.class, (JsonSerializer<Instant>) (src, typeOfSrc, context) ->
                    new JsonPrimitive(src.toString()))
            .create();

    public ActiveMqTripStorage(String brokerurl) {
        this.brokerurl = brokerurl;
    }
    @Override
    public void save(Trip trip) {
        try {
            ConnectionFactory factory = new ActiveMQConnectionFactory(brokerUrl);
            Connection connection = factory.createConnection();
            connection.start();

            Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
            Destination topic = session.createTopic("Trips");
            MessageProducer producer = session.createProducer(topic);


            String json = gson.toJson(trip);
            TextMessage message = session.createTextMessage(json);
            producer.send(message);
            System.out.println("📤 Evento enviado: " + json);

            producer.close();
            session.close();
            connection.close();


        } catch (Exception e) {
            System.err.println("Error publicando trip: " + e.getMessage());
        }
    }
}