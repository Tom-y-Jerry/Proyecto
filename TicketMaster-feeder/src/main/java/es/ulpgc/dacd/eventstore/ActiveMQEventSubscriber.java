package es.ulpgc.dacd.eventstore;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.apache.activemq.ActiveMQConnectionFactory;

import javax.jms.*;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class ActiveMQEventSubscriber {
    private static final String URL = "tcp://localhost:61616";
    private static final String TOPIC = "prediction.Ticketmaster";

    public void start() throws Exception {
        ConnectionFactory factory = new ActiveMQConnectionFactory(URL);
        Connection connection = factory.createConnection();
        connection.start();

        Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
        Topic topic = session.createTopic(TOPIC);
        MessageConsumer consumer = session.createConsumer(topic);

        consumer.setMessageListener(message -> {
            System.out.println("📡 Mensaje recibido del topic");
            if (message instanceof TextMessage textMessage) {
                try {
                    String json = textMessage.getText();
                    System.out.println("📥 Evento recibido del broker: " + json);
                    writeToFile(json);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        System.out.println("🎧 Suscrito al topic 'prediction.Ticketmaster'. Esperando eventos...");
    }

    private void writeToFile(String jsonString) {
        JsonObject obj = JsonParser.parseString(jsonString).getAsJsonObject();
        String ss = obj.get("ss").getAsString();
        String ts = obj.get("ts").getAsString();
        String date = ts.substring(0, 10).replace("-", "");
        String path = String.format("eventstore/%s/%s.events", ss, date);

        File file = new File(path);
        file.getParentFile().mkdirs();

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(file, true))) {
            writer.write(jsonString);
            writer.newLine();
            System.out.println("💾 Evento escrito en archivo: " + file.getAbsolutePath());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}




