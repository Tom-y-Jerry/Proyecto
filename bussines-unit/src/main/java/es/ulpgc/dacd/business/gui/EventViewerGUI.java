package es.ulpgc.dacd.business.gui;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.util.*;
import java.util.List;

import static javax.swing.UIManager.*;

public class EventViewerGUI extends JFrame {
    private final EventLoader controller;
    private final DefaultListModel<Event> eventListModel = new DefaultListModel<>();
    private final JTextArea tripArea = new JTextArea();
    private final JComboBox<String> originBox = new JComboBox<>();
    private String selectedOrigin = null;

    public EventViewerGUI(String dbPath) throws Exception {
        setLookAndFeel(getSystemLookAndFeelClassName());
        this.controller = new EventLoader(dbPath);
        setupWindow();
        setupTopPanel();
        setupSplitPanel();
        setupEventListeners();
        loadOrigins();
        startAutoRefresh();
    }

    private void setupWindow() {
        setTitle("De Ruta al Concierto");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(1200, 700);
        setLayout(new BorderLayout());
    }

    private void setupTopPanel() {
        JPanel topPanel = createTopPanel();
        add(topPanel, BorderLayout.NORTH);
    }

    private JPanel createTopPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(new EmptyBorder(10, 10, 10, 10));
        panel.setBackground(new Color(0, 100, 182));

        JLabel titleLabel = createTitleLabel();
        setupOriginBox();

        panel.add(titleLabel, BorderLayout.WEST);
        panel.add(originBox, BorderLayout.CENTER);

        return panel;
    }

    private JLabel createTitleLabel() {
        JLabel label = new JLabel("Selecciona el origen:  ");
        label.setFont(new Font("Verdana", Font.BOLD, 14));
        label.setForeground(Color.WHITE);
        return label;
    }

    private void setupOriginBox() {
        originBox.setFont(new Font("Verdana", Font.PLAIN, 14));
        originBox.setBackground(Color.WHITE);
        originBox.setForeground(new Color(33, 33, 33));
        originBox.setRenderer(customOriginRenderer());
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
        JScrollPane scrollEvents = createEventScroll();
        JPanel rightPanel = createRightPanel();
        JSplitPane splitPane = createSplitPane(scrollEvents, rightPanel);

        add(splitPane, BorderLayout.CENTER);
    }

    private JScrollPane createEventScroll() {
        JList<Event> eventList = createEventList();
        JScrollPane scroll = new JScrollPane(eventList);
        scroll.setBorder(BorderFactory.createTitledBorder("Eventos disponibles"));
        scroll.setPreferredSize(new Dimension(500, 0));
        addEventSelectionListener(eventList);
        return scroll;
    }

    private JPanel createRightPanel() {
        JScrollPane scrollTrips = createTripPanel();
        JLabel imageLabel = createImageLabel();

        JPanel rightPanel = new JPanel();
        rightPanel.setLayout(new BoxLayout(rightPanel, BoxLayout.Y_AXIS));
        rightPanel.add(scrollTrips);
        rightPanel.add(imageLabel);
        return rightPanel;
    }

    private JSplitPane createSplitPane(JScrollPane left, JPanel right) {
        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, left, right);
        splitPane.setDividerLocation(700);
        splitPane.setResizeWeight(0.8);
        return splitPane;
    }


    private JList<Event> createEventList() {
        JList<Event> list = new JList<>(eventListModel);
        list.setFont(new Font("SansSerif", Font.PLAIN, 13));
        list.setBackground(Color.WHITE);
        list.setSelectionBackground(new Color(27, 137, 177));
        list.setSelectionForeground(Color.WHITE);

        list.setCellRenderer((lst, value, index, isSelected, cellHasFocus) -> {
            JTextArea area = new JTextArea(value.toString());
            area.setWrapStyleWord(true);
            area.setLineWrap(true);
            area.setOpaque(true);
            area.setFont(list.getFont());
            area.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
            if (isSelected) {
                area.setBackground(list.getSelectionBackground());
                area.setForeground(list.getSelectionForeground());
            } else {
                area.setBackground(list.getBackground());
                area.setForeground(list.getForeground());
            }
            return area;
        });
        return list;
    }


    private JScrollPane createTripPanel() {
        tripArea.setEditable(false);
        tripArea.setFont(new Font("SansSerif", Font.PLAIN, 13));
        tripArea.setBackground(Color.WHITE);
        tripArea.setForeground(new Color(33, 33, 33));
        tripArea.setMargin(new Insets(10, 10, 10, 10));
        JScrollPane scrollTrips = new JScrollPane(tripArea);
        scrollTrips.setBorder(BorderFactory.createTitledBorder("Viajes disponibles"));
        scrollTrips.setPreferredSize(new Dimension(600, 100));
        return scrollTrips;
    }

    private JLabel createImageLabel() {
        ImageIcon icon = new ImageIcon("bussines-unit/src/main/resources/bus.png");
        Image scaled = icon.getImage().getScaledInstance(600, 400, Image.SCALE_SMOOTH);
        JLabel label = new JLabel(new ImageIcon(scaled));
        label.setAlignmentX(Component.CENTER_ALIGNMENT);
        return label;
    }

    private void addEventSelectionListener(JList<Event> eventList) {
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
        }, 0, 2000);
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
        tripArea.setText("Resultados de viajes desde: " + selectedOrigin + " para: " + event.city() + "\n\n");
        List<String> trips = controller.loadTrips(selectedOrigin, event.city());
        if (trips.isEmpty()) {
            tripArea.append("No se encontraron viajes desde: " + selectedOrigin + " para: " + event.city());
        } else {
            trips.forEach(s -> tripArea.append(s + "\n"));
        }
    }

    public record Event(String id, String name, String date, String time, String city) {
        @Override
        public String toString() {
            String cleanDate = date.length() > 10 ? date.substring(0, 10) : date;
            String cleanTime = time.replace("T", "").replace("Z", "").split("\\.")[0];
            if (cleanTime.equalsIgnoreCase("Not specified")) cleanTime = "--:--";
            return String.format(
                    "\uD83C\uDFAB Evento: %s" + "\n" +
                            "\uD83D\uDCC5 Fecha: %s" + "\n" +
                            "\uD83D\uDD50 Hora: %s" + "\n" +
                            "\uD83D\uDCCD Ciudad: %s" + "\n",
                    name, cleanDate, cleanTime, city
            );
        }
    }
}