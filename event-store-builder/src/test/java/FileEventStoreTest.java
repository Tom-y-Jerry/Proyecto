import es.ulpgc.dacd.eventstorebuilder.adapters.FileEventStore;
import org.junit.jupiter.api.*;
import java.io.IOException;
import java.nio.file.*;
import java.time.Instant;

import static org.junit.jupiter.api.Assertions.*;

class FileEventStoreTest {

    private final String topic = "test.topic";
    private final String source = "testSource";
    private final String ts = Instant.now().toString();
    private final String json = String.format("{\"ts\":\"%s\", \"ss\":\"%s\", \"data\":\"value\"}", ts, source);
    private final Path baseDir = Paths.get("eventstore", topic, source);

    @BeforeEach
    void cleanBefore() throws IOException {
        if (Files.exists(baseDir)) {
            Files.walk(baseDir)
                    .sorted((a, b) -> b.compareTo(a))
                    .map(Path::toFile)
                    .forEach(java.io.File::delete);
        }
    }

    @Test
    void shouldStoreEventInCorrectFile() throws IOException {
        FileEventStore store = new FileEventStore();
        store.handle(topic, json);

        String date = ts.substring(0, 10).replace("-", "");
        Path file = baseDir.resolve(date + ".events");

        assertTrue(Files.exists(file));
        String content = Files.readString(file);
        assertTrue(content.contains("data"));
        assertTrue(content.contains("value"));
    }
}
