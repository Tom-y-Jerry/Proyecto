package es.ulpgc.dacd.business.domain;
import java.time.Instant;

public final class Event {
    private final String ss;
    private final String id;
    private final String name;
    private final Instant date;
    private final String time;
    private final String city;

    public Event(String ss, String id, String name, Instant date, String time, String city) {
        this.ss = ss;
        this.id = id;
        this.name = name;
        this.date = date;
        this.time = time;
        this.city = city;
    }

    public String getSs() { return ss; }
    public String getId() { return id; }
    public String getName() { return name; }
    public Instant getDate() { return date; }
    public String getTime() { return time; }
    public String getCity() { return city; }
}
