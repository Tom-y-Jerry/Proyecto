package es.ulpgc.dacd.business.domain.model;

public class Trip {
    private final String origin;
    private final String destination;
    private final String departure;
    private final String arrival;
    private final double price;
    private final String currency;
    private final long durationMinutes;

    public Trip(String origin, String destination, String departure, String arrival,
                double price, String currency, long durationMinutes) {
        this.origin = origin;
        this.destination = destination;
        this.departure = departure;
        this.arrival = arrival;
        this.price = price;
        this.currency = currency;
        this.durationMinutes = durationMinutes;
    }

    public String getOrigin() { return origin; }
    public String getDestination() { return destination; }
    public String getDeparture() { return departure; }
    public String getArrival() { return arrival; }
    public double getPrice() { return price; }
    public String getCurrency() { return currency; }
    public long getDurationMinutes() { return durationMinutes; }
}
