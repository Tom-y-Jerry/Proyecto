package es.ulpgc.dacd.blablacarfeeder;
import es.ulpgc.dacd.blablacarfeeder.infrastructure.adapters.ActiveMqTripStorage;
import es.ulpgc.dacd.blablacarfeeder.infrastructure.adapters.BlablacarTripProvider;

public class Main {
    public static void main(String[] args) {
        if (args.length < 3) {
            System.out.println("You need to pass: <stopsUrl> <faresUrl> <apiKey> <brokerUrl>");
            return;
        }

        String stopsUrl = args[0];
        String faresUrl = args[1];
        String apiKey = args[2];
        String brokerurl = args[3];

        var provider = new BlablacarTripProvider(stopsUrl, faresUrl, apiKey);
        var storage = new ActiveMqTripStorage(brokerurl);
        new Controller(provider, storage).execute();

    }
}