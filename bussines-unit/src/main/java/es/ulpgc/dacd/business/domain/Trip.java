package es.ulpgc.dacd.business.domain;
import java.time.Instant;

public final class Trip {
    private final String ss;
    private final String origin;
    private final String destination;
    private final Instant departure;
    private final Instant arrival;
    private final double price;
    private final String currency;

    public Trip(String ss, String origin, String destination, Instant departure, Instant arrival, double price, String currency) {
        this.ss = ss;
        this.origin = origin;
        this.destination = destination;
        this.departure = departure;
        this.arrival = arrival;
        this.price = price;
        this.currency = currency;
    }

    public String getSs() {
        return ss;
    }
    public String getOrigin() {
        return origin;
    }
    public String getDestination() {
        return destination;
    }
    public Instant getDeparture() {
        return departure;
    }
    public Instant getArrival() {
        return arrival;
    }
    public double getPrice() {
        return price;
    }
    public String getCurrency() {
        return currency;
    }
}