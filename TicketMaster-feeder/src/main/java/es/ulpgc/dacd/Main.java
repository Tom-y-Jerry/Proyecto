package es.ulpgc.dacd;
import es.ulpgc.dacd.controller.TicketMasterServiceController;
import es.ulpgc.dacd.controller.TicketmasterFeederService;
import es.ulpgc.dacd.domain.port.Events;
import es.ulpgc.dacd.domain.port.EventsRepository;
import es.ulpgc.dacd.domain.port.Publisher;
import es.ulpgc.dacd.infrastructure.api.TicketMasterAPIClient;
import es.ulpgc.dacd.infrastructure.api.TicketMasterEvents;
import es.ulpgc.dacd.infrastructure.adapter.SQLiteEventsRepository;
import es.ulpgc.dacd.infrastructure.adapter.ActiveMQPublisher;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class Main {
    private static final String DB_URL = "jdbc:sqlite:data.db";

    public static void main(String[] args) {
        try {
            TicketMasterAPIClient apiClient = new TicketMasterAPIClient();
            Events events = new TicketMasterEvents(apiClient);
            EventsRepository repository = new SQLiteEventsRepository(DB_URL);
            Publisher publisher = new ActiveMQPublisher();
            TicketmasterFeederService service = new TicketmasterFeederService(publisher);

            ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();
            scheduler.scheduleAtFixedRate(service::run, 0, 1, TimeUnit.HOURS);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
