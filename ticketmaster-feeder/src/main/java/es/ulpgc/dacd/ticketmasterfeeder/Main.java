package es.ulpgc.dacd.ticketmasterfeeder;
import es.ulpgc.dacd.ticketmasterfeeder.infrastructure.adapter.TicketMasterEventProvider;
import es.ulpgc.dacd.ticketmasterfeeder.infrastructure.adapter.ActiveMQEventStorage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Main {
    private static final Logger log = LoggerFactory.getLogger(Main.class);

    public static void main(String[] args) {
        if (args.length < 3) {
            log.warn("Arguments requiered: <eventsUrl> <apiKey> <brokerUrl>");
            return;
        }

        String eventsUrl = args[0];
        String apiKey = args[1];
        String brokerurl = args[2];
        var provider = new TicketMasterEventProvider(eventsUrl, apiKey);
        var storage = new ActiveMQEventStorage(brokerurl);
        new Controller(provider, storage).execute();
    }
}