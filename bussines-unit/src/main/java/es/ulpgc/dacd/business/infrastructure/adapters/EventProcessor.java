package es.ulpgc.dacd.business.infrastructure.adapters;

public interface EventProcessor<T> {
    void process(T type);
}