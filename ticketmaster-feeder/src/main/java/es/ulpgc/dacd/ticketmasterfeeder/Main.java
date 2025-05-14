package es.ulpgc.dacd.ticketmasterfeeder;

import es.ulpgc.dacd.ticketmasterfeeder.infrastructure.adapter.TicketMasterEventProvider;
import es.ulpgc.dacd.ticketmasterfeeder.infrastructure.adapter.ActiveMQEventStorage;

public class Main {
    public static void main(String[] args) {
        if (args.length < 2) {
            System.out.println("Argumentos requeridos: <eventsUrl> <apiKey> <brokerUrl>");
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
