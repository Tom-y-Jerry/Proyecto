package es.ulpgc.dacd.domain.model;

public class Fare {
    public int id;
    public int originId;
    public int destinationId;
    public String departure;
    public String arrival;
    public int priceCents;
    public String currency;

    public Fare(int id, int originId, int destinationId, String departure, String arrival, int priceCents, String currency) {
        this.id = id;
        this.originId = originId;
        this.destinationId = destinationId;
        this.departure = departure;
        this.arrival = arrival;
        this.priceCents = priceCents;
        this.currency = currency;
    }
}

