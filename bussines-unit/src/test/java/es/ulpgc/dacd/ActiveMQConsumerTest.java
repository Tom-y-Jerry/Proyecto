package es.ulpgc.dacd;
import com.google.gson.Gson;
import es.ulpgc.dacd.business.infrastructure.adapters.ActiveMQConsumer;
import es.ulpgc.dacd.business.infrastructure.ports.EventProcessor;
import org.junit.jupiter.api.Test;
import javax.jms.TextMessage;
import static org.mockito.Mockito.*;

class ActiveMQConsumerTest {

    @Test
    void shouldProcessValidJsonMessage() throws Exception {
        DummyEvent expected = new DummyEvent("example", 123);
        String json = serializeToJson(expected);
        TextMessage message = mockTextMessage(json);
        EventProcessor<DummyEvent> processor = mock(EventProcessor.class);

        ActiveMQConsumer<DummyEvent> consumer = createConsumer(processor);
        invokeHandleMessage(consumer, message);

        verify(processor).process(refEq(expected));
    }

    private String serializeToJson(DummyEvent event) {
        return new Gson().toJson(event);
    }

    private TextMessage mockTextMessage(String json) throws Exception {
        TextMessage message = mock(TextMessage.class);
        when(message.getText()).thenReturn(json);
        return message;
    }

    private ActiveMQConsumer<DummyEvent> createConsumer(EventProcessor<DummyEvent> processor) {
        return new ActiveMQConsumer<>("dummy-topic", "tcp://localhost:61616", processor, DummyEvent.class);
    }

    private void invokeHandleMessage(ActiveMQConsumer<DummyEvent> consumer, TextMessage message) throws Exception {
        var method = consumer.getClass().getDeclaredMethod("handleMessage", javax.jms.Message.class);
        method.setAccessible(true);
        method.invoke(consumer, message);
    }

    private static class DummyEvent {
        public String name;
        public int value;

        public DummyEvent() {}
        public DummyEvent(String name, int value) {
            this.name = name;
            this.value = value;
        }
    }
}