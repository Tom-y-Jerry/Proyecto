package es.ulpgc.dacd.infrastructure.subscriber;

import javax.jms.*;
import org.apache.activemq.ActiveMQConnectionFactory;

import java.io.*;
import java.nio.file.*;
import java.time.*;
import java.time.format.DateTimeFormatter;

public class EventConsumer {
    private final Connection connection;
    private final Session session;

    public EventConsumer(String brokerUrl) {
        try {
            ActiveMQConnectionFactory factory = new ActiveMQConnectionFactory(brokerUrl);
            this.connection = factory.createConnection();
            this.connection.setClientID("event-store-builder");
            this.connection.start();
            this.session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
        } catch (Exception e) {
            throw new RuntimeException("Error al conectar al broker", e);
        }
    }

    public void subscribe(String topicName) {
        try {
            Topic topic = session.createTopic(topicName);
            MessageConsumer consumer = session.createDurableSubscriber(topic, topicName + "-subscriber");

            consumer.setMessageListener(message -> {
                if (message instanceof TextMessage textMsg) {
                    try {
                        String json = textMsg.getText();
                        System.out.println("Evento recibido desde " + topicName + ": " + json);
                        saveEvent(topicName, json);
                    } catch (IOException e) {
                        System.err.println("Error guardando evento: " + e.getMessage());
                    } catch (JMSException e) {
                        System.err.println("Error leyendo mensaje JMS: " + e.getMessage());
                    }
                }
            });
        } catch (Exception e) {
            throw new RuntimeException("Error al suscribirse al topic " + topicName, e);
        }
    }

    private void saveEvent(String topic, String json) throws IOException {
        String ts = json.split("\"ts\":\"")[1].split("\"")[0];
        String ss = json.split("\"ss\":\"")[1].split("\"")[0];
        String date = LocalDate.parse(ts.substring(0, 10)).format(DateTimeFormatter.ofPattern("yyyyMMdd"));

        Path dir = Paths.get("eventstore", topic, ss);
        Files.createDirectories(dir);

        Path file = dir.resolve(date + ".events");
        Files.writeString(file, json + System.lineSeparator(), StandardOpenOption.CREATE, StandardOpenOption.APPEND);
    }
}
