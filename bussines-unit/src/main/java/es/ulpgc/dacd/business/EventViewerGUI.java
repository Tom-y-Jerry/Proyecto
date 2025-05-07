package es.ulpgc.dacd.business;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.sql.*;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.List;

public class EventViewerGUI extends JFrame {
    private final Connection conn;
    private final DefaultListModel<Event> eventListModel = new DefaultListModel<>();
    private final JTextArea tripArea = new JTextArea();
    private final JComboBox<String> originBox = new JComboBox<>();
    private String selectedOrigin = null;

    public EventViewerGUI(String dbPath) throws SQLException {
        this.conn = DriverManager.getConnection("jdbc:sqlite:" + dbPath);
        setTitle("Viajes a eventos de Ticketmaster");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(900, 600);
        setLayout(new BorderLayout());

        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setBorder(new EmptyBorder(10, 10, 10, 10));
        topPanel.add(new JLabel("Selecciona tu ciudad de origen:"), BorderLayout.WEST);
        topPanel.add(originBox, BorderLayout.CENTER);
        add(topPanel, BorderLayout.NORTH);

        JList<Event> eventList = new JList<>(eventListModel);
        JScrollPane scrollEvents = new JScrollPane(eventList);
        scrollEvents.setBorder(new EmptyBorder(10, 10, 10, 10));
        scrollEvents.setPreferredSize(new Dimension(320, 0));
        add(scrollEvents, BorderLayout.WEST);

        tripArea.setEditable(false);
        tripArea.setFont(new Font("monospaced", Font.PLAIN, 14));
        JScrollPane scrollTrips = new JScrollPane(tripArea);
        scrollTrips.setBorder(new EmptyBorder(10, 10, 10, 10));
        add(scrollTrips, BorderLayout.CENTER);

        originBox.addActionListener(e -> {
            selectedOrigin = (String) originBox.getSelectedItem();
            loadEvents();
        });

        eventList.addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                Event selected = eventList.getSelectedValue();
                if (selected != null && selectedOrigin != null) showTripsFor(selected);
            }
        });

        loadOrigins();
        setVisible(true);
    }

    private void loadOrigins() {
        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT DISTINCT origin FROM trips ORDER BY origin")) {
            while (rs.next()) {
                originBox.addItem(rs.getString("origin"));
            }
        } catch (SQLException e) {
            showError("Error cargando ciudades de origen: " + e.getMessage());
        }
    }

    private void loadEvents() {
        eventListModel.clear();

        if (selectedOrigin == null) return;

        try (PreparedStatement ps = conn.prepareStatement(
                """
                SELECT e.id, e.name, e.date, e.time, e.city
                FROM events e
                WHERE EXISTS (
                    SELECT 1 FROM trips t
                    WHERE t.origin = ? AND t.destination LIKE '%' || e.city || '%'
                )
                ORDER BY e.date, e.time
                """)) {

            ps.setString(1, selectedOrigin);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    eventListModel.addElement(new Event(
                            rs.getString("id"),
                            rs.getString("name"),
                            rs.getString("date"),
                            rs.getString("time"),
                            rs.getString("city")
                    ));
                }
            }

        } catch (SQLException e) {
            showError("Error cargando eventos filtrados: " + e.getMessage());
        }
    }

    private void showTripsFor(Event event) {
        tripArea.setText("\uD83D\uDD0E Buscando viajes desde " + selectedOrigin + " hacia: " + event.city() + "\n\n");
        try (PreparedStatement ps = conn.prepareStatement(
                "SELECT * FROM trips WHERE origin = ? AND destination LIKE ?")) {
            ps.setString(1, selectedOrigin);
            ps.setString(2, "%" + event.city() + "%");

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
                tripArea.append("\u274C No se encontraron viajes desde " + selectedOrigin + " hacia " + event.city());
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
            String cleanDate = date.length() > 10 ? date.substring(0, 10) : date;
            String cleanTime = time.replace("T", "").replace("Z", "").split("\\.")[0];
            if (cleanTime.equalsIgnoreCase("Not specified")) cleanTime = "--:--";
            return String.format("%s - %s %s [%s]", name, cleanDate, cleanTime, city);
        }
    }

    public void start() {
        SwingUtilities.invokeLater(() -> setVisible(true));
    }
}