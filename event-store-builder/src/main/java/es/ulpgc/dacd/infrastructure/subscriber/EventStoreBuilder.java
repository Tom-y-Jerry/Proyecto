package es.ulpgc.dacd.infrastructure.subscriber;

public class EventStoreBuilder {
    public static void main(String[] args) {
        String[] topics = (args.length > 0)
                ? args
                : new String[] { "prediction.Trips", "prediction.Events" };

        EventConsumer consumer = new EventConsumer("tcp://localhost:61616");
        for (String topic : topics) {
            consumer.subscribe(topic);
        }
    }
}
