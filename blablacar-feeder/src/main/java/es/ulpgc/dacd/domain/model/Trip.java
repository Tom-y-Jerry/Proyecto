package es.ulpgc.dacd.domain.model;

import java.util.List;

public final class Trip {
    private final String origin;
    private final String destination;
    private final String departure;
    private final String arrival;
    private final int priceCents;
    private final String currency;

    public Trip(String origin, String destination, String departure, String arrival, int priceCents, String currency) {
        this.origin = origin;
        this.destination = destination;
        this.departure = departure;
        this.arrival = arrival;
        this.priceCents = priceCents;
        this.currency = currency;
    }

    public String getOrigin() {return origin;}
    public String getDestination() {return destination;}
    public String getDeparture() {return departure;}
    public String getArrival() {return arrival;}
    public int getPriceCents() {return priceCents;}
    public String getCurrency() {return currency;}

}
