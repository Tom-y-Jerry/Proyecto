package es.ulpgc.dacd.domain.model;

public class Fare {
    public final int id;
    public final int originId;
    public final int destinationId;
    public final String departure;
    public final String arrival;
    public final int priceCents;
    public final String currency;

    public Fare(int id, int originId, int destinationId,
                String departure, String arrival,
                int priceCents, String currency) {
        this.id = id;
        this.originId = originId;
        this.destinationId = destinationId;
        this.departure = departure;
        this.arrival = arrival;
        this.priceCents = priceCents;
        this.currency = currency;
    }

    public double getPriceEuros() {
        return priceCents / 100.0;
    }
}
