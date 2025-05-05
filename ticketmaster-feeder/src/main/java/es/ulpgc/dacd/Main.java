package es.ulpgc.dacd;

import es.ulpgc.dacd.infrastructure.adapter.TicketMasterEventProvider;
import es.ulpgc.dacd.infrastructure.adapter.ActiveMQEventStorage;

public class Main {
    public static void main(String[] args) {
        if (args.length < 2) {
            System.out.println("Argumentos requeridos: <eventsUrl> <apiKey>");
            return;
        }

        String eventsUrl = args[0];
        String apiKey = args[1];

        var provider = new TicketMasterEventProvider(eventsUrl, apiKey);
        var storage = new ActiveMQEventStorage();

        new Controller(provider, storage).execute();
    }
}
