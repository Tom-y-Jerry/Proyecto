package es.ulpgc.dacd.domain.model;

import java.time.Instant;

public class BlaBlaCarEvent {
    public String ts;
    public String ss;
    public String id;
    public String name;
    public double latitude;
    public double longitude;

    public BlaBlaCarEvent(String ss, String id, String name, double latitude, double longitude) {
        this.ts = Instant.now().toString();
        this.ss = ss;
        this.id = id;
        this.name = name;
        this.latitude = latitude;
        this.longitude = longitude;
    }
}
