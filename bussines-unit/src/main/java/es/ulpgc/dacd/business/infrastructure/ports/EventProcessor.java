package es.ulpgc.dacd.business.infrastructure.ports;

public interface EventProcessor<T> {
    void process(T type);
}