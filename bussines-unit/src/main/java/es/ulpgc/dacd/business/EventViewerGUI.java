package es.ulpgc.dacd.business;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.sql.*;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class EventViewerGUI extends JFrame {
    private final Connection conn;
    private final DefaultListModel<Event> eventListModel = new DefaultListModel<>();
    private final JTextArea tripArea = new JTextArea();

    public EventViewerGUI(String dbPath) throws SQLException {
        this.conn = DriverManager.getConnection("jdbc:sqlite:" + dbPath);
        setTitle("Eventos Ticketmaster");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(800, 600);
        setLayout(new BorderLayout());

        JList<Event> eventList = new JList<>(eventListModel);
        JScrollPane scrollEvents = new JScrollPane(eventList);
        scrollEvents.setBorder(new EmptyBorder(10, 10, 10, 10));
        add(scrollEvents, BorderLayout.WEST);

        tripArea.setEditable(false);
        tripArea.setFont(new Font("monospaced", Font.PLAIN, 14));
        JScrollPane scrollTrips = new JScrollPane(tripArea);
        scrollTrips.setBorder(new EmptyBorder(10, 10, 10, 10));
        add(scrollTrips, BorderLayout.CENTER);

        eventList.addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                Event selected = eventList.getSelectedValue();
                if (selected != null) showTripsFor(selected);
            }
        });

        loadEvents();
        setVisible(true);
    }

    private void loadEvents() {
        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT id, name, date, time, city FROM events ORDER BY date, time")) {
            while (rs.next()) {
                eventListModel.addElement(new Event(
                        rs.getString("id"),
                        rs.getString("name"),
                        rs.getString("date"),
                        rs.getString("time"),
                        rs.getString("city")
                ));
            }
        } catch (SQLException e) {
            showError("Error cargando eventos: " + e.getMessage());
        }
    }

    private void showTripsFor(Event event) {
        tripArea.setText("\uD83D\uDD0E Buscando viajes para: " + event.city() + "\n\n");
        try (PreparedStatement ps = conn.prepareStatement(
                "SELECT * FROM trips WHERE destination LIKE ?")) {
            ps.setString(1, "%" + event.city() + "%");

            ResultSet rs = ps.executeQuery();
            List<String> formatted = new ArrayList<>();
            while (rs.next()) {
                String out = formatTrip(
                        rs.getString("origin"),
                        rs.getString("destination"),
                        rs.getString("departure"),
                        rs.getString("arrival"),
                        rs.getDouble("price"),
                        rs.getString("currency")
                );
                formatted.add(out);
            }

            if (formatted.isEmpty()) {
                tripArea.append("\u274C No se encontraron viajes hacia " + event.city());
            } else {
                formatted.forEach(s -> tripArea.append(s + "\n"));
            }

        } catch (SQLException e) {
            showError("Error buscando viajes: " + e.getMessage());
        }
    }

    private String formatTrip(String origin, String destination, String depStr, String arrStr, double price, String currency) {
        Instant departure = Instant.parse(depStr);
        Instant arrival = Instant.parse(arrStr);

        String dep = departure.atZone(ZoneId.systemDefault())
                .toLocalTime().format(DateTimeFormatter.ofPattern("HH:mm"));
        String arr = arrival.atZone(ZoneId.systemDefault())
                .toLocalTime().format(DateTimeFormatter.ofPattern("HH:mm"));

        Duration duration = Duration.between(departure, arrival);
        long h = duration.toHours();
        long m = duration.toMinutes() % 60;

        return String.format("\uD83D\uDE8C %s → %s | %s - %s | %dh %02dmin | %.2f %s",
                origin, destination, dep, arr, h, m, price, currency);
    }

    private void showError(String msg) {
        JOptionPane.showMessageDialog(this, msg, "Error", JOptionPane.ERROR_MESSAGE);
    }

    public record Event(String id, String name, String date, String time, String city) {
        @Override
        public String toString() {
            return String.format("%s (%s %s) - %s", name, date, time, city);
        }
    }

    public void start() {
        SwingUtilities.invokeLater(() -> setVisible(true));
    }
}