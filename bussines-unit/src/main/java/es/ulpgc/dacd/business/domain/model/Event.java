package es.ulpgc.dacd.business.domain.model;

public class Event {
    private final String id;
    private final String name;
    private final Instant date;
    private final String time;
    private final String city;

    public Event(String id, String name, Instant date, String time, String city) {
        this.id = id;
        this.name = name;
        this.date = date;
        this.time = time;
        this.city = city;
    }

    public String getId() { return id; }
    public String getName() { return name; }
    public String getDate() { return date; }
    public String getTime() { return time; }
    public String getCity() { return city; }
}
