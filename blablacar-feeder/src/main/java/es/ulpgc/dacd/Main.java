package es.ulpgc.dacd;
import es.ulpgc.dacd.infrastructure.adapters.ActiveMqTripStorage;
import es.ulpgc.dacd.infrastructure.adapters.BlablacarTripProvider;

public class Main {
    public static void main(String[] args) {
        if (args.length < 3) {
            System.out.println("You need to pass: <stopsUrl> <faresUrl> <apiKey>");
            return;
        }

        String stopsUrl = args[0];
        String faresUrl = args[1];
        String apiKey = args[2];

        var provider = new BlablacarTripProvider(stopsUrl, faresUrl, apiKey);
        var storage = new ActiveMqTripStorage();
        new Controller(provider, storage).execute();

    }
}