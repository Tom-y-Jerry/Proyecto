package es.ulpgc.dacd.business.application.processor;

public interface EventProcessor {
    void process(String rawEvent);
}