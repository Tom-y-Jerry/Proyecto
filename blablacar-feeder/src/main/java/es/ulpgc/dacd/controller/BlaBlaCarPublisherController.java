package es.ulpgc.dacd.controller;

import com.google.gson.*;
import es.ulpgc.dacd.domain.model.Station;
import es.ulpgc.dacd.domain.model.BlaBlaCarEvent;
import es.ulpgc.dacd.infrastructure.api.BlaBlaCarAPIClient;
import es.ulpgc.dacd.infrastructure.api.BlaBlaCarStations;
import es.ulpgc.dacd.infrastructure.broker.ActiveMQPublisher;

import javax.jms.JMSException;
import java.util.List;

public class BlaBlaCarPublisherController {
    private final Gson gson = new Gson();
    private final BlaBlaCarStations stations;
    private final ActiveMQPublisher publisher;

    public BlaBlaCarPublisherController(BlaBlaCarAPIClient apiClient, ActiveMQPublisher publisher) {
        this.stations = new BlaBlaCarStations(apiClient);
        this.publisher = publisher;
    }

    public void run() {
        int count = 0;

        try {
            List<Station> cleanStations = stations.getCleanStations();

            for (Station s : cleanStations) {
                BlaBlaCarEvent event = new BlaBlaCarEvent(
                        "feeder-blablacar",
                        String.valueOf(s.getId()),
                        s.getLongName(),
                        s.getLatitude(),
                        s.getLongitude()
                );

                String jsonEvent = gson.toJson(event);
                publisher.publish("prediction.Stations", jsonEvent);
                count++;
            }

            System.out.println("✅ Se publicaron " + count + " eventos filtrados.");

        } catch (Exception e) {
            System.err.println("❌ Error al publicar eventos: " + e.getMessage());
        } finally {
            try {
                publisher.close();
            } catch (JMSException e) {
                System.err.println("❌ Error cerrando publisher: " + e.getMessage());
            }
        }
    }
}
