import es.ulpgc.dacd.eventstorebuilder.Controller;
import es.ulpgc.dacd.eventstorebuilder.adapters.EventListener;
import org.junit.jupiter.api.Test;

import static org.mockito.Mockito.*;

class ControllerTest {

    @Test
    void shouldSubscribeToAllTopics() {
        EventListener listener = mock(EventListener.class);
        Controller controller = new Controller(listener);

        controller.subscribeToTopics("topic1", "topic2");

        verify(listener).subscribe("topic1");
        verify(listener).subscribe("topic2");
    }
}
