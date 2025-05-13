package es.ulpgc.dacd.business.application.processor;

public interface EventProcessor<T> {
    void process(T type);
}