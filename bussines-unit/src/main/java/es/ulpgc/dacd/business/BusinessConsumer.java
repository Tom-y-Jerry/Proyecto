package es.ulpgc.dacd.business;

import com.google.gson.*;
import org.apache.activemq.ActiveMQConnectionFactory;

import javax.jms.*;
import java.sql.*;
import java.sql.Connection;

public class BusinessConsumer {
    private final Connection sqlConnection;

    public BusinessConsumer(String sqlitePath) {
        try {
            this.sqlConnection = DriverManager.getConnection("jdbc:sqlite:" + sqlitePath);
            createTablesIfNeeded();
        } catch (SQLException e) {
            throw new RuntimeException("Error conectando a SQLite", e);
        }
    }

    public void subscribeTo(String topic, String brokerUrl) {
        try {
            ActiveMQConnectionFactory factory = new ActiveMQConnectionFactory(brokerUrl);
            javax.jms.Connection connection = factory.createConnection();
            connection.setClientID("business-unit-" + topic);
            connection.start();

            Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
            Topic dest = session.createTopic(topic);
            MessageConsumer consumer = session.createDurableSubscriber(dest, topic + "-business-subscriber");

            consumer.setMessageListener(message -> {
                if (message instanceof TextMessage textMsg) {
                    try {
                        String json = textMsg.getText();
                        storeInSQLite(topic, json);
                    } catch (Exception e) {
                        System.err.println("\u274c Error procesando mensaje: " + e.getMessage());
                    }
                }
            });

        } catch (Exception e) {
            throw new RuntimeException("Error al suscribirse al broker", e);
        }
    }

    private void createTablesIfNeeded() throws SQLException {
        try (Statement stmt = sqlConnection.createStatement()) {
            stmt.execute("""
                CREATE TABLE IF NOT EXISTS events (
                    id TEXT,
                    name TEXT,
                    date TEXT,
                    time TEXT,
                    city TEXT,
                    ss TEXT,
                    json TEXT
                )""");

            stmt.execute("""
                CREATE TABLE IF NOT EXISTS trips (
                    origin TEXT,
                    destination TEXT,
                    departure TEXT,
                    arrival TEXT,
                    price REAL,
                    currency TEXT,
                    ss TEXT,
                    json TEXT
                )""");
        }
    }

    private void storeInSQLite(String topic, String json) throws SQLException {
        JsonObject obj = JsonParser.parseString(json).getAsJsonObject();
        String ss = obj.get("ss").getAsString();

        if (ss.equals("ticketmaster")) {
            PreparedStatement ps = sqlConnection.prepareStatement("""
                INSERT INTO events (id, name, date, time, city, ss, json)
                VALUES (?, ?, ?, ?, ?, ?, ?)""");

            ps.setString(1, obj.get("id").getAsString());
            ps.setString(2, obj.get("name").getAsString());
            ps.setString(3, obj.get("date").getAsString());
            ps.setString(4, obj.get("time").getAsString());
            ps.setString(5, obj.get("city").getAsString());
            ps.setString(6, ss);
            ps.setString(7, json);
            ps.executeUpdate();

        } else if (ss.equals("feeder-blablacar")) {
            PreparedStatement ps = sqlConnection.prepareStatement("""
                INSERT INTO trips (origin, destination, departure, arrival, price, currency, ss, json)
                VALUES (?, ?, ?, ?, ?, ?, ?, ?)""");

            ps.setString(1, obj.get("origin").getAsString());
            ps.setString(2, obj.get("destination").getAsString());
            ps.setString(3, obj.get("departure").getAsString());
            ps.setString(4, obj.get("arrival").getAsString());
            ps.setDouble(5, obj.get("price").getAsDouble());
            ps.setString(6, obj.get("currency").getAsString());
            ps.setString(7, ss);
            ps.setString(8, json);
            ps.executeUpdate();
        }
    }
}