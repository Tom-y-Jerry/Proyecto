package es.ulpgc.dacd.domain.model;

public final class Fare {
    private final int id;
    private final int originId;
    private final int destinationId;
    private final String departure;
    private final String arrival;
    private final int priceCents;
    private final String currency;

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

    public int getId() { return id; }
    public int getOriginId() { return originId; }
    public int getDestinationId() { return destinationId; }
    public String getDeparture() { return departure; }
    public String getArrival() { return arrival; }
    public int getPriceCents() { return priceCents; }
    public String getCurrency() { return currency; }
}

