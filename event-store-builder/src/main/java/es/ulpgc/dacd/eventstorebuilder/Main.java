package es.ulpgc.dacd.eventstorebuilder;

public class Main {
    public static void main(String[] args) {
        if (args.length < 2) {
            System.out.println("You need to pass: <brokerUrl> <clientId> [<topic1> <topic2>]");
            return;
        }

        String brokerUrl = args[0];
        String clientId = args[1];

        String[] topics = (args.length > 2)
                ? extractTopics(args)
                : new String[] { "Trips", "Events" };

        EventBrokerConnection broker = new EventBrokerConnection(brokerUrl, clientId);
        EventListener listener = new EventListener(broker.getSession(), new FileEventStore());

        for (String topic : topics) {
            listener.subscribe(topic);
        }
    }

    private static String[] extractTopics(String[] args) {
        String[] topics = new String[args.length - 2];
        System.arraycopy(args, 2, topics, 0, topics.length);
        return topics;
    }
}
