package es.ulpgc.dacd.domain.event;

import java.time.Instant;

public class StationEvent {
    public final String ts;
    public final String ss;
    public final String id;
    public final String name;
    public final double latitude;
    public final double longitude;

    public StationEvent(String ss, String id, String name, double latitude, double longitude) {
        this.ts = Instant.now().toString();
        this.ss = ss;
        this.id = id;
        this.name = name;
        this.latitude = latitude;
        this.longitude = longitude;
    }
}
