package es.ulpgc.dacd.domain.model;

import java.time.Instant;

public class TicketmasterEvent {
    public String ts;
    public String ss;
    public String id;
    public String nombre;
    public String fecha;
    public String ciudad;

    public TicketmasterEvent(String ss, String id, String nombre, String fecha, String ciudad) {
        this.ts = Instant.now().toString();
        this.ss = ss;
        this.id = id;
        this.nombre = nombre;
        this.fecha = fecha;
        this.ciudad = ciudad;
    }
}
