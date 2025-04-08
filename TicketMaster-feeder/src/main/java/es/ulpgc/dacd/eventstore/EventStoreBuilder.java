package es.ulpgc.dacd.eventstore;

public class EventStoreBuilder {
    public static void main(String[] args) {
        try {
            ActiveMQEventSubscriber subscriber = new ActiveMQEventSubscriber();
            subscriber.start();

            Thread.currentThread().join(); // que no se cierre
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}


