import es.ulpgc.dacd.eventstorebuilder.Controller;
import org.junit.jupiter.api.Test;


class ControllerTest {

    @Test
    void shouldSubscribeToTopicsCorrectly() {
        String brokerUrl = "tcp://localhost:61616";
        String clientId = "test-client";
        String[] topics = {"test-topic1", "test-topic2"};

        Controller controller = new Controller();
        controller.start(brokerUrl, clientId, topics);
    }
}
