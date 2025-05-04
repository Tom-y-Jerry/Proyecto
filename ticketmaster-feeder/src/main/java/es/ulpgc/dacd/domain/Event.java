package es.ulpgc.dacd.domain;


public class Event {
    private final String id;
    private final String name;
    private final String date;
    private final String city;

    public Event(String id, String name, String date, String city) {
        this.id = id;
        this.name = name;
        this.date = date;
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

    public String getCity() {
        return city;
    }

}

