package es.ulpgc.dacd.infrastructure.adapter;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import es.ulpgc.dacd.domain.model.Event;
import es.ulpgc.dacd.domain.port.Publisher;
import org.apache.activemq.ActiveMQConnectionFactory;

import javax.jms.*;
import java.time.Instant;

public class ActiveMQPublisher implements Publisher {
    private static final String URL = "tcp://localhost:61616";
    private static final String TOPIC = "prediction.Ticketmaster";

    private final Connection connection;
    private final Session session;
    private final MessageProducer producer;
    private final Gson gson = new Gson();

    public ActiveMQPublisher() throws JMSException {
        ConnectionFactory factory = new ActiveMQConnectionFactory(URL);
        this.connection = factory.createConnection();
        this.connection.start();

        this.session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
        Topic topic = session.createTopic(TOPIC);
        this.producer = session.createProducer(topic);
    }

    @Override
    public void publish(Event event) {
        JsonObject json = new JsonObject();
        json.addProperty("ts", Instant.now().toString());
        json.addProperty("ss", "ticketmaster");
        json.addProperty("id", event.getId());
        json.addProperty("nombre", event.getName());
        json.addProperty("fecha", event.getDate());
        json.addProperty("ciudad", event.getCity());

        try {
            TextMessage message = session.createTextMessage(json.toString());
            producer.send(message);
            System.out.println("📤 Evento publicado: " + json);
        } catch (JMSException e) {
            e.printStackTrace();
        }
    }

    public void close() throws Exception {
        producer.close();
        session.close();
        connection.close();
    }
}

