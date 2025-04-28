import java.util.*;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class NFAtoDFA {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new AutomataGUI();
        });
    }
}

class AutomataGUI extends JFrame {
    private JTextArea inputArea, outputArea;
    private JPanel drawingPanel;
    private Map<String, Set<String>> nfaTransitions = new HashMap<>();
    private Set<String> states = new HashSet<>();
    private Set<String> alphabet = new HashSet<>();
    private String startState;
    private Set<String> finalStates = new HashSet<>();
    private Map<String, Map<String, String>> dfaTransitions = new HashMap<>();
    private Set<String> dfaStates = new HashSet<>();
    private Set<String> dfaFinalStates = new HashSet<>();
    private final String DEAD_STATE = "∅";
    
    public AutomataGUI() {
        setTitle("NFA to DFA Converter");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());
        
        // Create components
        JPanel topPanel = new JPanel(new GridLayout(1, 2, 10, 10));
        
        inputArea = new JTextArea();
        inputArea.setFont(new Font("Monospaced", Font.PLAIN, 12));
        JScrollPane inputScroll = new JScrollPane(inputArea);
        inputScroll.setBorder(BorderFactory.createTitledBorder("NFA Input"));
        
        outputArea = new JTextArea();
        outputArea.setFont(new Font("Monospaced", Font.PLAIN, 12));
        outputArea.setEditable(false);
        JScrollPane outputScroll = new JScrollPane(outputArea);
        outputScroll.setBorder(BorderFactory.createTitledBorder("DFA Output"));
        
        topPanel.add(inputScroll);
        topPanel.add(outputScroll);
        
        // Drawing panel for visualization
        drawingPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                drawAutomata(g);
            }
        };
        drawingPanel.setBackground(Color.WHITE);
        drawingPanel.setBorder(BorderFactory.createTitledBorder("Visualization"));
        
        // Button panel
        JPanel buttonPanel = new JPanel();
        JButton convertButton = new JButton("Convert NFA to DFA");
        JButton exampleButton = new JButton("Load Example");
        
        buttonPanel.add(convertButton);
        buttonPanel.add(exampleButton);
        
        // Add components to frame
        add(topPanel, BorderLayout.NORTH);
        add(drawingPanel, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);
        
        // Add example NFA
        exampleButton.addActionListener(e -> {
            inputArea.setText("States: q0,q1,q2\n" +
                    "Alphabet: a,b\n" +
                    "Transition: q0,a,q0\n" +
                    "Transition: q0,a,q1\n" +
                    "Transition: q0,e,q2\n" +
                    "Transition: q1,b,q2\n" +
                    "Start: q0\n" +
                    "Final: q2");
        });
        
        // Add convert action
        convertButton.addActionListener(e -> {
            try {
                parseNFA();
                convertToDFA();
                outputArea.setText(generateDFAOutput());
                drawingPanel.repaint();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage(), "Input Error", JOptionPane.ERROR_MESSAGE);
            }
        });
        
        setVisible(true);
    }
    
    private void parseNFA() {
        // Clear previous data
        nfaTransitions.clear();
        states.clear();
        alphabet.clear();
        finalStates.clear();
        
        String[] lines = inputArea.getText().split("\n");
        
        for (String line : lines) {
            line = line.trim();
            if (line.startsWith("States:")) {
                String[] statesList = line.substring(7).split(",");
                for (String state : statesList) {
                    states.add(state.trim());
                }
            } else if (line.startsWith("Alphabet:")) {
                String[] alphabetList = line.substring(9).split(",");
                for (String symbol : alphabetList) {
                    alphabet.add(symbol.trim());
                }
            } else if (line.startsWith("Transition:")) {
                String[] parts = line.substring(11).split(",");
                if (parts.length != 3) {
                    throw new IllegalArgumentException("Invalid transition format: " + line);
                }
                
                String fromState = parts[0].trim();
                String symbol = parts[1].trim();
                String toState = parts[2].trim();
                
                // Handle epsilon transitions
                if (symbol.equals("e") || symbol.equals("ε")) {
                    symbol = "ε";
                }
                
                nfaTransitions.computeIfAbsent(fromState + "," + symbol, k -> new HashSet<>()).add(toState);
            } else if (line.startsWith("Start:")) {
                startState = line.substring(6).trim();
            } else if (line.startsWith("Final:")) {
                String[] finalStatesList = line.substring(6).split(",");
                for (String state : finalStatesList) {
                    finalStates.add(state.trim());
                }
            }
        }
    }
    
    private Set<String> getEpsilonClosure(String state) {
        Set<String> closure = new HashSet<>();
        Stack<String> stack = new Stack<>();
        
        closure.add(state);
        stack.push(state);
        
        while (!stack.empty()) {
            String currentState = stack.pop();
            
            Set<String> epsilonTransitions = nfaTransitions.getOrDefault(currentState + ",ε", new HashSet<>());
            for (String nextState : epsilonTransitions) {
                if (!closure.contains(nextState)) {
                    closure.add(nextState);
                    stack.push(nextState);
                }
            }
        }
        
        return closure;
    }
    
    private Set<String> getEpsilonClosure(Set<String> states) {
        Set<String> closure = new HashSet<>();
        
        for (String state : states) {
            closure.addAll(getEpsilonClosure(state));
        }
        
        return closure;
    }
    
    private Set<String> move(Set<String> states, String symbol) {
        Set<String> result = new HashSet<>();
        
        for (String state : states) {
            Set<String> transitions = nfaTransitions.getOrDefault(state + "," + symbol, new HashSet<>());
            result.addAll(transitions);
        }
        
        return result;
    }
    
    private void convertToDFA() {
        dfaTransitions.clear();
        dfaStates.clear();
        dfaFinalStates.clear();
        
        // Get epsilon closure of start state
        Set<String> startClosure = getEpsilonClosure(startState);
        
        // Format the state set as a string
        String dfaStartState = formatStateSet(startClosure);
        
        Queue<Set<String>> queue = new LinkedList<>();
        Set<String> processedStates = new HashSet<>();
        
        queue.add(startClosure);
        dfaStates.add(dfaStartState);
        
        // Check if the start state contains any final states
        for (String state : startClosure) {
            if (finalStates.contains(state)) {
                dfaFinalStates.add(dfaStartState);
                break;
            }
        }
        
        while (!queue.isEmpty()) {
            Set<String> currentStateSet = queue.poll();
            String currentDFAState = formatStateSet(currentStateSet);
            
            if (processedStates.contains(currentDFAState)) {
                continue;
            }
            
            processedStates.add(currentDFAState);
            
            // For each input symbol
            for (String symbol : alphabet) {
                // Apply move function
                Set<String> moveResult = move(currentStateSet, symbol);
                
                // Apply epsilon closure
                Set<String> nextStateSet = getEpsilonClosure(moveResult);
                
                String nextDFAState;
                if (nextStateSet.isEmpty()) {
                    nextDFAState = DEAD_STATE;
                } else {
                    nextDFAState = formatStateSet(nextStateSet);
                }
                
                // Add transition
                dfaTransitions.computeIfAbsent(currentDFAState, k -> new HashMap<>()).put(symbol, nextDFAState);
                
                // Add to DFA states if not empty set
                if (!nextStateSet.isEmpty() && !dfaStates.contains(nextDFAState)) {
                    dfaStates.add(nextDFAState);
                    queue.add(nextStateSet);
                    
                    // Check if this state contains any final states from NFA
                    for (String state : nextStateSet) {
                        if (finalStates.contains(state)) {
                            dfaFinalStates.add(nextDFAState);
                            break;
                        }
                    }
                }
            }
        }
        
        // Add dead state if any transition points to it
        boolean needDeadState = false;
        for (Map<String, String> transitions : dfaTransitions.values()) {
            if (transitions.containsValue(DEAD_STATE)) {
                needDeadState = true;
                break;
            }
        }
        
        if (needDeadState) {
            dfaStates.add(DEAD_STATE);
            // Add self-loops for dead state
            Map<String, String> deadTransitions = new HashMap<>();
            for (String symbol : alphabet) {
                deadTransitions.put(symbol, DEAD_STATE);
            }
            dfaTransitions.put(DEAD_STATE, deadTransitions);
        }
    }
    
    private String formatStateSet(Set<String> stateSet) {
        if (stateSet.isEmpty()) {
            return DEAD_STATE;
        }
        
        java.util.List<String> sortedStates = new ArrayList<>(stateSet);
        Collections.sort(sortedStates);
        return "{" + String.join(",", sortedStates) + "}";
    }
    
    private String generateDFAOutput() {
        StringBuilder output = new StringBuilder();
        
        output.append("DFA States: ");
        output.append(String.join(", ", dfaStates));
        output.append("\n\n");
        
        output.append("DFA Transitions:\n");
        java.util.List<String> sortedStates = new ArrayList<>(dfaStates);
        Collections.sort(sortedStates);
        
        for (String state : sortedStates) {
            Map<String, String> transitions = dfaTransitions.getOrDefault(state, new HashMap<>());
            java.util.List<String> sortedSymbols = new ArrayList<>(alphabet);
            Collections.sort(sortedSymbols);
            
            for (String symbol : sortedSymbols) {
                String nextState = transitions.getOrDefault(symbol, DEAD_STATE);
                output.append(state).append(" -- ").append(symbol).append(" --> ").append(nextState).append("\n");
            }
        }
        
        output.append("\nStart State: ").append(formatStateSet(getEpsilonClosure(startState))).append("\n");
        
        output.append("Final States: ");
        output.append(String.join(", ", dfaFinalStates));
        
        return output.toString();
    }
    
    private void drawAutomata(Graphics g) {
        if (dfaStates.isEmpty()) {
            g.drawString("Convert an NFA to see visualization", 20, 30);
            return;
        }
        
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        
        int width = drawingPanel.getWidth();
        int height = drawingPanel.getHeight();
        
        // Calculate positions for states in a circle
        Map<String, Point> statePositions = new HashMap<>();
        int radius = Math.min(width, height) / 2 - 80;
        int centerX = width / 2;
        int centerY = height / 2;
        int stateRadius = 30;
        
        java.util.List<String> sortedStates = new ArrayList<>(dfaStates);
        Collections.sort(sortedStates);
        int numStates = sortedStates.size();
        
        // Place states in a circle
        for (int i = 0; i < numStates; i++) {
            double angle = 2 * Math.PI * i / numStates - Math.PI/2; // Start from top
            int x = centerX + (int)(radius * Math.cos(angle));
            int y = centerY + (int)(radius * Math.sin(angle));
            statePositions.put(sortedStates.get(i), new Point(x, y));
        }
        
        // Draw transitions
        g2d.setStroke(new BasicStroke(1.5f));
        for (String fromState : dfaTransitions.keySet()) {
            Map<String, String> transitions = dfaTransitions.get(fromState);
            
            for (String symbol : transitions.keySet()) {
                String toState = transitions.get(symbol);
                Point fromPoint = statePositions.get(fromState);
                Point toPoint = statePositions.get(toState);
                
                // Skip if positions are not calculated
                if (fromPoint == null || toPoint == null) continue;
                
                // Self-loop
                if (fromState.equals(toState)) {
                    // Draw a loop above the state
                    int loopRadius = 20;
                    g2d.drawOval(fromPoint.x - loopRadius, fromPoint.y - stateRadius - loopRadius * 2, 
                        loopRadius * 2, loopRadius * 2);
                    // Place symbol on the loop
                    g2d.setColor(Color.BLUE);
                    g2d.drawString(symbol, fromPoint.x, fromPoint.y - stateRadius - loopRadius * 2);
                    g2d.setColor(Color.BLACK);
                } else {
                    // Draw arrow between states
                    drawArrow(g2d, fromPoint.x, fromPoint.y, toPoint.x, toPoint.y, stateRadius);
                    
                    // Place the symbol on the arrow
                    int midX = (fromPoint.x + toPoint.x) / 2;
                    int midY = (fromPoint.y + toPoint.y) / 2;
                    // Offset the label slightly to avoid overlapping with the line
                    double angle = Math.atan2(toPoint.y - fromPoint.y, toPoint.x - fromPoint.x);
                    int offsetX = (int)(12 * Math.cos(angle + Math.PI/2));
                    int offsetY = (int)(12 * Math.sin(angle + Math.PI/2));
                    
                    g2d.setColor(Color.BLUE);
                    g2d.drawString(symbol, midX + offsetX, midY + offsetY);
                    g2d.setColor(Color.BLACK);
                }
            }
        }
        
        // Draw states
        for (String state : dfaStates) {
            Point pos = statePositions.get(state);
            if (pos == null) continue;
            
            // Draw the circle
            g2d.setColor(Color.WHITE);
            g2d.fillOval(pos.x - stateRadius, pos.y - stateRadius, 2 * stateRadius, 2 * stateRadius);
            g2d.setColor(Color.BLACK);
            g2d.drawOval(pos.x - stateRadius, pos.y - stateRadius, 2 * stateRadius, 2 * stateRadius);
            
            // Double circle for final states
            if (dfaFinalStates.contains(state)) {
                g2d.drawOval(pos.x - stateRadius + 5, pos.y - stateRadius + 5, 
                    2 * stateRadius - 10, 2 * stateRadius - 10);
            }
            
            // Draw the state name
            FontMetrics fm = g2d.getFontMetrics();
            int textWidth = fm.stringWidth(state);
            g2d.drawString(state, pos.x - textWidth / 2, pos.y + 5);
            
            // Draw arrow to start state
            String startDFAState = formatStateSet(getEpsilonClosure(startState));
            if (state.equals(startDFAState)) {
                // Draw an arrow pointing to the start state from outside
                int arrowLength = 40;
                double angle = Math.atan2(pos.y - centerY, pos.x - centerX);
                int startX = (int)(pos.x - (stateRadius + arrowLength) * Math.cos(angle));
                int startY = (int)(pos.y - (stateRadius + arrowLength) * Math.sin(angle));
                drawArrow(g2d, startX, startY, 
                          (int)(pos.x - stateRadius * Math.cos(angle)), 
                          (int)(pos.y - stateRadius * Math.sin(angle)), 0);
            }
        }
    }
    
    private void drawArrow(Graphics2D g2d, int x1, int y1, int x2, int y2, int stateRadius) {
        double angle = Math.atan2(y2 - y1, x2 - x1);
        
        // Adjust start and end points if a state radius is provided
        if (stateRadius > 0) {
            x1 = (int)(x1 + stateRadius * Math.cos(angle));
            y1 = (int)(y1 + stateRadius * Math.sin(angle));
            x2 = (int)(x2 - stateRadius * Math.cos(angle));
            y2 = (int)(y2 - stateRadius * Math.sin(angle));
        }
        
        g2d.drawLine(x1, y1, x2, y2);
        
        // Draw arrowhead
        int arrowSize = 10;
        int dx = x2 - x1;
        int dy = y2 - y1;
        double length = Math.sqrt(dx * dx + dy * dy);
        double dirX = dx / length;
        double dirY = dy / length;
        
        double perpX = -dirY;
        double perpY = dirX;
        
        int[] xPoints = new int[3];
        int[] yPoints = new int[3];
        
        xPoints[0] = x2;
        yPoints[0] = y2;
        
        xPoints[1] = (int)(x2 - arrowSize * dirX + arrowSize * 0.5 * perpX);
        yPoints[1] = (int)(y2 - arrowSize * dirY + arrowSize * 0.5 * perpY);
        
        xPoints[2] = (int)(x2 - arrowSize * dirX - arrowSize * 0.5 * perpX);
        yPoints[2] = (int)(y2 - arrowSize * dirY - arrowSize * 0.5 * perpY);
        
        g2d.fillPolygon(xPoints, yPoints, 3);
    }
}