package es.ulpgc.dacd.business.infrastructure.ports;

public interface DatamartService {
    void insertEvent(String id, String name, String date, String time, String city, String ss, String json);
    void insertTrip(String origin, String destination, String departure, String arrival, double price, String currency, long durationMinutes, String ss, String json);
}