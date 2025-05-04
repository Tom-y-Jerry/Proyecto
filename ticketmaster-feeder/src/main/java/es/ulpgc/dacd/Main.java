package es.ulpgc.dacd;

import es.ulpgc.dacd.adapters.ActiveMQEventStorage;

public class Main {
    public static void main(String[] args) {
        if (args.length < 2) {
            System.out.println("Argumentos requeridos: <eventsUrl> <apiKey>");
            return;
        }

        String eventsUrl = args[0];
        String apiKey = args[1];

        var storage = new ActiveMQEventStorage();

        new Controller(provider, storage).execute();
    }
}
