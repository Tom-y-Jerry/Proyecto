package es.ulpgc.dacd.business.application.processor;

import es.ulpgc.dacd.business.application.service.DatamartService;
import es.ulpgc.dacd.business.domain.model.Event;
import es.ulpgc.dacd.business.application.processor.EventProcessor;

public class ProcessTicketmasterEvent implements EventProcessor<Event> {
    private final DatamartService datamart;

    public ProcessTicketmasterEvent(DatamartService datamart) {
        this.datamart = datamart;
    }

    @Override
    public void process(Event event) {
        try {
            datamart.insertEvent(
                    event.getId(),
                    event.getName(),
                    event.getDate().toString(),
                    event.getTime(),
                    event.getCity(),
                    event.getSs(),
                    event.toString()
            );
        } catch (Exception e) {
            throw new RuntimeException("Error processing Ticketmaster event", e);
        }
    }
}
