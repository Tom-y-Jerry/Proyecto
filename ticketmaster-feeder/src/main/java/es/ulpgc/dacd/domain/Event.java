package es.ulpgc.dacd.domain;

public class Event {
    private final String id;
    private final String name;
    private final String date;         // Fecha en formato yyyy-MM-dd
    private final String time;         // Hora en formato HH:mm:ss o "Not specified"
    private final String city;// Rango de precios o "Not available"

    public Event(String id, String name, String date, String time, String city) {
        this.id = id;
        this.name = name;
        this.date = date;
        this.time = time;
        this.city = city;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getDate() {
        return date;
    }

    public String getTime() {
        return time;
    }

    public String getCity() {
        return city;
    }
}




