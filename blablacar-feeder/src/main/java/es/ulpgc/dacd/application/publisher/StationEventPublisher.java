package es.ulpgc.dacd.application.publisher;

import com.google.gson.Gson;
import es.ulpgc.dacd.domain.model.Station;
import es.ulpgc.dacd.domain.event.StationEvent;
import es.ulpgc.dacd.domain.port.Stations;
import es.ulpgc.dacd.infrastructure.broker.ActiveMQPublisher;
import javax.jms.JMSException;
import java.util.List;

public final class StationEventPublisher {
    private final Stations stations;
    private final ActiveMQPublisher publisher;
    private final Gson gson = new Gson();

    public StationEventPublisher(Stations stations, ActiveMQPublisher publisher) {
        this.stations = stations;
        this.publisher = publisher;
    }

    public void run() {
        List<Station> stationList = fetchStations();
        publishStations(stationList);
        closePublisher();
    }

    private List<Station> fetchStations() {
        return stations.getCleanStations();
    }

    private void publishStations(List<Station> stationList) {
        stationList.forEach(this::publishStationEvent);
    }

    private void publishStationEvent(Station station) {
        StationEvent event = new StationEvent(
                "feeder-blablacar",
                String.valueOf(station.getId()),
                station.getLongName(),
                station.getLatitude(),
                station.getLongitude()
        );
        try {
            publisher.publish("prediction.Stations", gson.toJson(event));
        } catch (JMSException e) {
            System.err.println("❌ Error publicando evento: " + e.getMessage());
        }
    }

    private void closePublisher() {
        try {
            publisher.close();
        } catch (JMSException e) {
            System.err.println("❌ Error cerrando publisher: " + e.getMessage());
        }
    }
}
