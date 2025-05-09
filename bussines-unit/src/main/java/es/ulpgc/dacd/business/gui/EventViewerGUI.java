package es.ulpgc.dacd.business.gui;

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

    public EventViewerGUI(String dbPath) throws Exception {
        UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        this.conn = DriverManager.getConnection("jdbc:sqlite:" + dbPath);
        setTitle("Ticketmaster Travel Assistant");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(1000, 600);
        setLayout(new BorderLayout());

        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setBorder(new EmptyBorder(10, 10, 10, 10));
        topPanel.setBackground(new Color(125, 84, 163));

        JLabel titleLabel = new JLabel("Select your origin city:");
        titleLabel.setFont(new Font("SansSerif", Font.BOLD, 16));
        titleLabel.setForeground(new Color(40, 14, 44));
        topPanel.add(titleLabel, BorderLayout.WEST);

        originBox.setFont(new Font("SansSerif", Font.PLAIN, 14));
        topPanel.add(originBox, BorderLayout.CENTER);
        add(topPanel, BorderLayout.NORTH);

        JList<Event> eventList = new JList<>(eventListModel);
        eventList.setFont(new Font("SansSerif", Font.PLAIN, 13));
        eventList.setSelectionBackground(new Color(146, 107, 168));
        eventList.setSelectionForeground(Color.BLACK);
        JScrollPane scrollEvents = new JScrollPane(eventList);
        scrollEvents.setBorder(BorderFactory.createTitledBorder("Available Events"));
        scrollEvents.setPreferredSize(new Dimension(500, 0));
        add(scrollEvents, BorderLayout.WEST);

        tripArea.setEditable(false);
        tripArea.setFont(new Font("Monospaced", Font.PLAIN, 13));
        tripArea.setBackground(new Color(124, 120, 151));
        tripArea.setMargin(new Insets(10, 10, 10, 10));
        JScrollPane scrollTrips = new JScrollPane(tripArea);
        scrollTrips.setBorder(BorderFactory.createTitledBorder("Available Trips"));

        JPanel rightPanel = new JPanel();
        rightPanel.setLayout(new BoxLayout(rightPanel, BoxLayout.Y_AXIS));
        scrollTrips.setPreferredSize(new Dimension(600, 100));
        rightPanel.add(scrollTrips);

        ImageIcon icon = new ImageIcon("src/main/resources/bus.png");
        Image scaled = icon.getImage().getScaledInstance(600, 400, Image.SCALE_SMOOTH);
        JLabel imageLabel = new JLabel(new ImageIcon(scaled));
        imageLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        rightPanel.add(imageLabel);
        add(rightPanel, BorderLayout.CENTER);

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

        new java.util.Timer().schedule(new TimerTask() {
            public void run() {
                SwingUtilities.invokeLater(() -> {
                    String old = (String) originBox.getSelectedItem();
                    loadOrigins();
                    if (old != null) {
                        originBox.setSelectedItem(old);
                        selectedOrigin = old;
                        loadEvents();
                    }
                });
            }
        }, 0, 5000);
    }

    private void loadOrigins() {
        Set<String> current = new HashSet<>();
        for (int i = 0; i < originBox.getItemCount(); i++) {
            current.add(originBox.getItemAt(i));
        }
        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT DISTINCT origin FROM trips ORDER BY origin")) {
            while (rs.next()) {
                String origin = rs.getString("origin");
                if (!current.contains(origin)) {
                    originBox.addItem(origin);
                }
            }
        } catch (SQLException e) {
            showError("Error loading origins: " + e.getMessage());
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
            showError("Error loading events: " + e.getMessage());
        }
    }

    private void showTripsFor(Event event) {
        tripArea.setText("\uD83D\uDD0E Searching trips from " + selectedOrigin + " to: " + event.city() + "\n\n");
        try (PreparedStatement ps = conn.prepareStatement(
                "SELECT origin, destination, departure, arrival, price, currency, duration_minutes FROM trips WHERE origin = ? AND destination LIKE ?")) {
            ps.setString(1, selectedOrigin);
            ps.setString(2, "%" + event.city() + "%");

            ResultSet rs = ps.executeQuery();
            List<String> formatted = new ArrayList<>();
            while (rs.next()) {
                formatted.add(formatTrip(
                        rs.getString("origin"),
                        rs.getString("destination"),
                        rs.getString("departure"),
                        rs.getString("arrival"),
                        rs.getDouble("price"),
                        rs.getString("currency"),
                        rs.getInt("duration_minutes")
                ));
            }

            if (formatted.isEmpty()) {
                tripArea.append("\u274C No trips found from " + selectedOrigin + " to " + event.city());
            } else {
                formatted.forEach(s -> tripArea.append(s + "\n"));
            }
        } catch (SQLException e) {
            showError("Error loading trips: " + e.getMessage());
        }
    }

    private String formatTrip(String origin, String destination, String depStr, String arrStr, double price, String currency, int durationMin) {
        Instant departure = Instant.parse(depStr);
        Instant arrival = Instant.parse(arrStr);

        String dep = departure.atZone(ZoneId.systemDefault()).toLocalTime().format(DateTimeFormatter.ofPattern("HH:mm"));
        String arr = arrival.atZone(ZoneId.systemDefault()).toLocalTime().format(DateTimeFormatter.ofPattern("HH:mm"));
        long h = durationMin / 60, m = durationMin % 60;

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
