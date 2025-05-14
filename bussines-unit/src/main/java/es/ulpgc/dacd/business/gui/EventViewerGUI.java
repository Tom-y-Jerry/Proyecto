package es.ulpgc.dacd.business.gui;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.util.*;
import java.util.List;

public class EventViewerGUI extends JFrame {
    private final EventController controller;
    private final DefaultListModel<Event> eventListModel = new DefaultListModel<>();
    private final JTextArea tripArea = new JTextArea();
    private final JComboBox<String> originBox = new JComboBox<>();
    private String selectedOrigin = null;

    public EventViewerGUI(String dbPath) throws Exception {
        UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        this.controller = new EventController(dbPath);
        setupWindow();
        setupTopPanel();
        setupSplitPanel();
        setupEventListeners();
        loadOrigins();
        startAutoRefresh();
    }

    private void setupWindow() {
        setTitle("Ticketmaster Travel Assistant");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(1200, 700);
        setLayout(new BorderLayout());
    }

    private void setupTopPanel() {
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setBorder(new EmptyBorder(10, 10, 10, 10));
        topPanel.setBackground(new Color(0, 100, 182));

        JLabel titleLabel = new JLabel("Selecciona el origen:  ");
        titleLabel.setFont(new Font("Verdana", Font.BOLD, 14));
        titleLabel.setForeground(Color.WHITE);
        topPanel.add(titleLabel, BorderLayout.WEST);

        originBox.setFont(new Font("Verdana", Font.PLAIN, 14));
        originBox.setBackground(Color.WHITE);
        originBox.setForeground(new Color(33, 33, 33));
        originBox.setRenderer(customOriginRenderer());

        topPanel.add(originBox, BorderLayout.CENTER);
        add(topPanel, BorderLayout.NORTH);
    }

    private DefaultListCellRenderer customOriginRenderer() {
        return new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                JLabel label = (JLabel) super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                label.setFont(new Font("Verdana", Font.PLAIN, 14));
                label.setBackground(isSelected ? new Color(27, 137, 177) : Color.WHITE);
                label.setForeground(isSelected ? Color.WHITE : new Color(33, 33, 33));
                return label;
            }
        };
    }

    private void setupSplitPanel() {
        JList<Event> eventList = new JList<>(eventListModel);
        eventList.setFont(new Font("Verdana", Font.PLAIN, 13));
        eventList.setBackground(Color.WHITE);
        eventList.setSelectionBackground(new Color(27, 137, 177));
        eventList.setSelectionForeground(Color.WHITE);

        JScrollPane scrollEvents = new JScrollPane(eventList);
        scrollEvents.setBorder(BorderFactory.createTitledBorder("Eventos disponibles"));
        scrollEvents.setPreferredSize(new Dimension(500, 0));

        tripArea.setEditable(false);
        tripArea.setFont(new Font("SansSerif", Font.PLAIN, 13));
        tripArea.setBackground(Color.WHITE);
        tripArea.setForeground(new Color(33, 33, 33));
        tripArea.setMargin(new Insets(10, 10, 10, 10));

        JScrollPane scrollTrips = new JScrollPane(tripArea);
        scrollTrips.setBorder(BorderFactory.createTitledBorder("Viajes disponibles"));
        scrollTrips.setPreferredSize(new Dimension(600, 100));

        JPanel rightPanel = new JPanel();
        rightPanel.setLayout(new BoxLayout(rightPanel, BoxLayout.Y_AXIS));
        rightPanel.add(scrollTrips);

        JLabel imageLabel = new JLabel(new ImageIcon(new ImageIcon("bussines-unit/src/main/resources/bus.png")
                .getImage().getScaledInstance(600, 400, Image.SCALE_SMOOTH)));
        imageLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        rightPanel.add(imageLabel);

        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, scrollEvents, rightPanel);
        splitPane.setDividerLocation(700);
        splitPane.setResizeWeight(0.8);
        add(splitPane, BorderLayout.CENTER);

        eventList.addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                Event selected = eventList.getSelectedValue();
                if (selected != null && selectedOrigin != null) showTripsFor(selected);
            }
        });
    }

    private void setupEventListeners() {
        originBox.addActionListener(e -> {
            selectedOrigin = (String) originBox.getSelectedItem();
            loadEvents();
        });
    }

    private void startAutoRefresh() {
        new java.util.Timer().schedule(new java.util.TimerTask() {
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
        Set<String> newOrigins = controller.loadOrigins(current);
        for (String o : newOrigins) originBox.addItem(o);
    }

    private void loadEvents() {
        eventListModel.clear();
        if (selectedOrigin == null) return;
        List<Event> events = controller.loadEvents(selectedOrigin);
        eventListModel.addAll(events);
    }

    private void showTripsFor(Event event) {
        tripArea.setText("🔎 Resultados de viajes desde: " + selectedOrigin + " para: " + event.city() + "\n\n");
        List<String> trips = controller.loadTrips(selectedOrigin, event.city());
        if (trips.isEmpty()) {
            tripArea.append("No trips found from " + selectedOrigin + " to " + event.city());
        } else {
            trips.forEach(s -> tripArea.append(s + "\n"));
        }
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