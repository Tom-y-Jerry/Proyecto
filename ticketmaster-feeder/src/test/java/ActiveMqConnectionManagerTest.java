import static org.junit.jupiter.api.Assertions.*;
import es.ulpgc.dacd.ticketmasterfeeder.infrastructure.adapter.ActiveMQConnectionManager;
import org.junit.jupiter.api.Test;
import javax.jms.Connection;
import javax.jms.Session;

class ActiveMqConnectionManagerTest {
    @Test
    void createsConnectionAndSession() throws Exception {
        ActiveMQConnectionManager mgr = new ActiveMQConnectionManager("vm://localhost?broker.persistent=false");
        Connection conn = mgr.createConnection();
        assertNotNull(conn);
        conn.start();
        Session session = mgr.createSession(conn);
        assertNotNull(session);
        session.close();
        conn.close();
    }
}