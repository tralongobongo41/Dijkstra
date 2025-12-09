import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.*;
import java.util.*;
import java.util.List;

/**
 * Visualizer for Dijkstra's algorithm on the campus graph.
 * DO NOT MODIFY THIS CLASS - This is provided for testing your implementation.
 */
public class GraphVisualizer extends JPanel {

    // Node positions (x, y) for drawing - based on campus map layout
    private static final int[][] NODE_POSITIONS = {
            {400, 50},   // 0: Gate
            {200, 120},  // 1: Student Parking
            {520, 100},  // 2: Senior Parking
            {360, 170},  // 3: Circle
            {470, 190},  // 4: Admissions
            {580, 200},  // 5: Business Office
            {250, 280},  // 6: PA
            {100, 300},  // 7: Athletics
            {450, 270},  // 8: Fountain
            {380, 330},  // 9: Cohen
            {580, 290},  // 10: US 100
            {500, 360},  // 11: Library
            {500, 430},  // 12: CHH
            {320, 420},  // 13: Mariani
            {620, 390},  // 14: VA
            {700, 290},  // 15: Faculty Parking
            {680, 480},  // 16: GD
            {220, 500},  // 17: Science
            {320, 580},  // 18: BD
    };

    // Colors
    private static final Color COLOR_UNVISITED = new Color(60, 60, 60);
    private static final Color COLOR_VISITED = new Color(70, 130, 180);
    private static final Color COLOR_CURRENT = new Color(255, 165, 0);
    private static final Color COLOR_PATH = new Color(50, 205, 50);
    private static final Color COLOR_EDGE = new Color(150, 150, 150);
    private static final Color COLOR_PATH_EDGE = new Color(50, 205, 50);
    private static final Color COLOR_START_END = new Color(220, 20, 60);

    private Graph graph;
    private PathFinder pathFinder;
    private DijkstraResult currentResult;
    private int currentStepIndex = -1;
    private boolean showingFinalPath = false;

    private int startNode = -1;
    private int endNode = -1;

    private JComboBox<String> startCombo;
    private JComboBox<String> endCombo;
    private JButton runButton;
    private JButton stepButton;
    private JButton playButton;
    private JButton resetButton;
    private JSlider speedSlider;
    private JLabel statusLabel;
    private JLabel distanceLabel;

    private javax.swing.Timer autoPlayTimer;
    private boolean isPlaying = false;

    public GraphVisualizer() {
        this.graph = new Graph();
        this.pathFinder = new PathFinder(graph);

        setLayout(new BorderLayout());
        setBackground(new Color(240, 240, 240));

        // Control panel
        JPanel controlPanel = createControlPanel();
        add(controlPanel, BorderLayout.NORTH);

        // Graph drawing panel
        JPanel graphPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                drawGraph((Graphics2D) g);
            }
        };
        graphPanel.setBackground(new Color(245, 245, 250));
        graphPanel.setPreferredSize(new Dimension(850, 650));
        add(graphPanel, BorderLayout.CENTER);

        // Legend panel
        JPanel legendPanel = createLegendPanel();
        add(legendPanel, BorderLayout.SOUTH);

        // Auto-play timer
        autoPlayTimer = new javax.swing.Timer(500, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (currentResult != null && currentStepIndex < currentResult.getSteps().size() - 1) {
                    stepForward();
                } else if (currentResult != null && !showingFinalPath) {
                    showingFinalPath = true;
                    repaint();
                    updateStatus();
                } else {
                    stopAutoPlay();
                }
            }
        });
    }

    private JPanel createControlPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 10));
        panel.setBackground(new Color(220, 220, 230));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Start node selector
        panel.add(new JLabel("Start:"));
        startCombo = new JComboBox<>(Graph.NODE_NAMES);
        startCombo.setPreferredSize(new Dimension(130, 28));
        panel.add(startCombo);

        // End node selector
        panel.add(new JLabel("End:"));
        endCombo = new JComboBox<>(Graph.NODE_NAMES);
        endCombo.setSelectedIndex(Graph.NUM_NODES - 1);
        endCombo.setPreferredSize(new Dimension(130, 28));
        panel.add(endCombo);

        // Run button
        runButton = new JButton("Run Algorithm");
        runButton.addActionListener(e -> runAlgorithm());
        panel.add(runButton);

        // Step button
        stepButton = new JButton("Step >");
        stepButton.setEnabled(false);
        stepButton.addActionListener(e -> stepForward());
        panel.add(stepButton);

        // Play/Pause button
        playButton = new JButton("Play");
        playButton.setEnabled(false);
        playButton.addActionListener(e -> toggleAutoPlay());
        panel.add(playButton);

        // Speed slider
        panel.add(new JLabel("Speed:"));
        speedSlider = new JSlider(100, 1000, 500);
        speedSlider.setPreferredSize(new Dimension(100, 28));
        speedSlider.setInverted(true);
        speedSlider.addChangeListener(e -> autoPlayTimer.setDelay(speedSlider.getValue()));
        panel.add(speedSlider);

        // Reset button
        resetButton = new JButton("Reset");
        resetButton.addActionListener(e -> reset());
        panel.add(resetButton);

        // Status panel
        JPanel statusPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 5));
        statusPanel.setBackground(new Color(220, 220, 230));
        statusLabel = new JLabel("Select start and end nodes, then click 'Run Algorithm'");
        statusLabel.setFont(new Font("SansSerif", Font.PLAIN, 13));
        statusPanel.add(statusLabel);

        distanceLabel = new JLabel("");
        distanceLabel.setFont(new Font("SansSerif", Font.BOLD, 14));
        distanceLabel.setForeground(new Color(0, 100, 0));
        statusPanel.add(distanceLabel);

        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(new Color(220, 220, 230));
        mainPanel.add(panel, BorderLayout.NORTH);
        mainPanel.add(statusPanel, BorderLayout.SOUTH);

        return mainPanel;
    }

    private JPanel createLegendPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 30, 8));
        panel.setBackground(new Color(220, 220, 230));
        panel.setBorder(BorderFactory.createEmptyBorder(5, 10, 10, 10));

        panel.add(createLegendItem("Unvisited", COLOR_UNVISITED));
        panel.add(createLegendItem("Visited", COLOR_VISITED));
        panel.add(createLegendItem("Current", COLOR_CURRENT));
        panel.add(createLegendItem("Final Path", COLOR_PATH));
        panel.add(createLegendItem("Start/End", COLOR_START_END));

        return panel;
    }

    private JPanel createLegendItem(String text, Color color) {
        JPanel item = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 0));
        item.setBackground(new Color(220, 220, 230));

        JPanel colorBox = new JPanel();
        colorBox.setBackground(color);
        colorBox.setPreferredSize(new Dimension(18, 18));
        colorBox.setBorder(BorderFactory.createLineBorder(Color.DARK_GRAY));

        item.add(colorBox);
        item.add(new JLabel(text));

        return item;
    }

    private void runAlgorithm() {
        startNode = startCombo.getSelectedIndex();
        endNode = endCombo.getSelectedIndex();

        if (startNode == endNode) {
            JOptionPane.showMessageDialog(this,
                    "Please select different start and end nodes.",
                    "Invalid Selection", JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            currentResult = pathFinder.findShortestPath(startNode, endNode);

            if (currentResult.getSteps().isEmpty()) {
                JOptionPane.showMessageDialog(this,
                        "No steps recorded. Make sure your Dijkstra implementation calls result.addStep().",
                        "No Steps", JOptionPane.WARNING_MESSAGE);
                return;
            }

            currentStepIndex = 0;
            showingFinalPath = false;

            stepButton.setEnabled(true);
            playButton.setEnabled(true);

            updateStatus();
            repaint();

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this,
                    "Error running algorithm: " + e.getMessage() +
                            "\n\nMake sure your Graph and PathFinder implementations are complete.",
                    "Error", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }

    private void stepForward() {
        if (currentResult == null) return;

        if (currentStepIndex < currentResult.getSteps().size() - 1) {
            currentStepIndex++;
            repaint();
            updateStatus();
        } else if (!showingFinalPath) {
            showingFinalPath = true;
            repaint();
            updateStatus();
        }
    }

    private void toggleAutoPlay() {
        if (isPlaying) {
            stopAutoPlay();
        } else {
            startAutoPlay();
        }
    }

    private void startAutoPlay() {
        isPlaying = true;
        playButton.setText("Pause");
        stepButton.setEnabled(false);
        autoPlayTimer.start();
    }

    private void stopAutoPlay() {
        isPlaying = false;
        playButton.setText("Play");
        stepButton.setEnabled(true);
        autoPlayTimer.stop();
    }

    private void reset() {
        stopAutoPlay();
        currentResult = null;
        currentStepIndex = -1;
        showingFinalPath = false;
        startNode = -1;
        endNode = -1;
        stepButton.setEnabled(false);
        playButton.setEnabled(false);
        statusLabel.setText("Select start and end nodes, then click 'Run Algorithm'");
        distanceLabel.setText("");
        repaint();
    }

    private void updateStatus() {
        if (currentResult == null || currentStepIndex < 0) return;

        if (showingFinalPath) {
            if (currentResult.getFinalPath().isEmpty()) {
                statusLabel.setText("No path found between selected nodes!");
                distanceLabel.setText("");
            } else {
                statusLabel.setText("Shortest path found!");
                distanceLabel.setText("Total Distance: " + currentResult.getTotalDistance());
            }
        } else {
            DijkstraResult.AlgorithmStep step = currentResult.getSteps().get(currentStepIndex);
            String nodeName = graph.getNodeName(step.currentNode);
            statusLabel.setText(String.format("Step %d/%d: Processing node '%s' | Visited: %d nodes",
                    currentStepIndex + 1,
                    currentResult.getSteps().size(),
                    nodeName,
                    step.visitedNodes.size()));
            distanceLabel.setText("");
        }
    }

    private void drawGraph(Graphics2D g2d) {
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

        // Get current algorithm state
        Set<Integer> visitedNodes = new HashSet<>();
        int currentNode = -1;
        int[] distances = null;
        List<Integer> finalPath = new ArrayList<>();

        if (currentResult != null && currentStepIndex >= 0) {
            DijkstraResult.AlgorithmStep step = currentResult.getSteps().get(currentStepIndex);
            visitedNodes = step.visitedNodes;
            currentNode = step.currentNode;
            distances = step.distances;

            if (showingFinalPath) {
                finalPath = currentResult.getFinalPath();
            }
        }

        // Draw edges first
        drawEdges(g2d, finalPath);

        // Draw nodes
        drawNodes(g2d, visitedNodes, currentNode, finalPath, distances);
    }

    private void drawEdges(Graphics2D g2d, List<Integer> finalPath) {
        Set<String> pathEdges = new HashSet<>();
        for (int i = 0; i < finalPath.size() - 1; i++) {
            int a = Math.min(finalPath.get(i), finalPath.get(i + 1));
            int b = Math.max(finalPath.get(i), finalPath.get(i + 1));
            pathEdges.add(a + "-" + b);
        }

        g2d.setStroke(new BasicStroke(2.5f));

        for (int i = 0; i < Graph.NUM_NODES; i++) {
            for (int j = i + 1; j < Graph.NUM_NODES; j++) {
                if (graph.hasEdge(i, j)) {
                    int x1 = NODE_POSITIONS[i][0];
                    int y1 = NODE_POSITIONS[i][1];
                    int x2 = NODE_POSITIONS[j][0];
                    int y2 = NODE_POSITIONS[j][1];

                    String edgeKey = i + "-" + j;
                    boolean isPathEdge = pathEdges.contains(edgeKey);

                    if (isPathEdge) {
                        g2d.setColor(COLOR_PATH_EDGE);
                        g2d.setStroke(new BasicStroke(4.5f));
                    } else {
                        g2d.setColor(COLOR_EDGE);
                        g2d.setStroke(new BasicStroke(2.0f));
                    }

                    g2d.drawLine(x1, y1, x2, y2);

                    // Draw weight
                    int midX = (x1 + x2) / 2;
                    int midY = (y1 + y2) / 2;

                    g2d.setColor(new Color(100, 100, 100));
                    g2d.setFont(new Font("SansSerif", Font.BOLD, 11));

                    int weight = graph.getEdgeWeight(i, j);
                    String weightStr = String.valueOf(weight);
                    FontMetrics fm = g2d.getFontMetrics();

                    g2d.setColor(new Color(245, 245, 250));
                    g2d.fillRect(midX - fm.stringWidth(weightStr)/2 - 3,
                            midY - fm.getAscent()/2 - 2,
                            fm.stringWidth(weightStr) + 6,
                            fm.getHeight());

                    g2d.setColor(new Color(80, 80, 80));
                    g2d.drawString(weightStr, midX - fm.stringWidth(weightStr)/2, midY + fm.getAscent()/2 - 1);
                }
            }
        }
    }

    private void drawNodes(Graphics2D g2d, Set<Integer> visitedNodes, int currentNode,
                           List<Integer> finalPath, int[] distances) {
        Set<Integer> pathNodes = new HashSet<>(finalPath);

        for (int i = 0; i < Graph.NUM_NODES; i++) {
            int x = NODE_POSITIONS[i][0];
            int y = NODE_POSITIONS[i][1];
            int radius = 28;

            // Determine node color
            Color nodeColor;
            if (showingFinalPath && pathNodes.contains(i)) {
                nodeColor = COLOR_PATH;
            } else if (i == startNode || i == endNode) {
                nodeColor = COLOR_START_END;
            } else if (i == currentNode) {
                nodeColor = COLOR_CURRENT;
            } else if (visitedNodes.contains(i)) {
                nodeColor = COLOR_VISITED;
            } else {
                nodeColor = COLOR_UNVISITED;
            }

            // Draw node circle
            g2d.setColor(nodeColor);
            g2d.fillOval(x - radius, y - radius, radius * 2, radius * 2);

            g2d.setColor(Color.WHITE);
            g2d.setStroke(new BasicStroke(2));
            g2d.drawOval(x - radius, y - radius, radius * 2, radius * 2);

            // Draw node name
            g2d.setColor(Color.WHITE);
            g2d.setFont(new Font("SansSerif", Font.BOLD, 10));
            FontMetrics fm = g2d.getFontMetrics();
            String name = Graph.NODE_NAMES[i];

            // Handle multi-word names
            if (name.contains(" ")) {
                String[] parts = name.split(" ");
                int lineHeight = fm.getHeight();
                int startY = y - (parts.length - 1) * lineHeight / 2 + fm.getAscent() / 2 - 2;
                for (int p = 0; p < parts.length; p++) {
                    int textWidth = fm.stringWidth(parts[p]);
                    g2d.drawString(parts[p], x - textWidth / 2, startY + p * lineHeight);
                }
            } else {
                int textWidth = fm.stringWidth(name);
                g2d.drawString(name, x - textWidth / 2, y + fm.getAscent() / 2 - 2);
            }

            // Draw distance label (during algorithm steps)
            if (distances != null && !showingFinalPath) {
                String distStr = distances[i] == Integer.MAX_VALUE ? "8" : String.valueOf(distances[i]);
                g2d.setColor(new Color(0, 0, 0));
                g2d.setFont(new Font("SansSerif", Font.BOLD, 12));
                fm = g2d.getFontMetrics();

                int labelX = x + radius + 5;
                int labelY = y - radius + 5;

                g2d.setColor(new Color(255, 255, 200));
                g2d.fillRoundRect(labelX - 2, labelY - fm.getAscent(),
                        fm.stringWidth(distStr) + 6, fm.getHeight(), 4, 4);
                g2d.setColor(new Color(100, 50, 0));
                g2d.drawString(distStr, labelX + 1, labelY);
            }
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("Dijkstra's Algorithm Visualizer - Campus Map");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.add(new GraphVisualizer());
            frame.pack();
            frame.setLocationRelativeTo(null);
            frame.setResizable(false);
            frame.setVisible(true);
        });
    }
}