package es.ulpgc.dacd.infrastructure.subscriber;

public class EventStoreBuilder {
    public static void main(String[] args) {
        EventConsumer consumer = new EventConsumer("tcp://localhost:61616");
        consumer.subscribe("prediction.Events");
        consumer.subscribe("prediction.Stations");
    }
}
