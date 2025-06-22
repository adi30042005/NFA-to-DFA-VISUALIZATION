import java.awt.*;
import java.awt.geom.*;
import javax.swing.*;
import java.util.*;
import java.util.List;
import java.util.Map;

public class AutomataVisualizer {
    private Set<String> dfaStates;
    private Map<String, Map<String, String>> dfaTransitions;
    private String startState;
    private Set<String> finalStates;
    private Set<String> alphabet;
    
    public void setDFA(Set<String> dfaStates, Map<String, Map<String, String>> dfaTransitions,
                      String startState, Set<String> finalStates, Set<String> alphabet) {
        this.dfaStates = dfaStates;
        this.dfaTransitions = dfaTransitions;
        this.startState = startState;
        this.finalStates = finalStates;
        this.alphabet = alphabet;
    }
    
    public void drawAutomata(Graphics g, int width, int height) {
        if (dfaStates == null || dfaStates.isEmpty()) {
            g.drawString("Convert an NFA to see visualization", 20, 30);
            return;
        }
        
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        
        // Calculate positions for states in a circle
        Map<String, Point> statePositions = calculateStatePositions(width, height);
        
        // Draw transitions
        drawTransitions(g2d, statePositions);
        
        // Draw states
        drawStates(g2d, statePositions, width, height);
    }
    
    private Map<String, Point> calculateStatePositions(int width, int height) {
        Map<String, Point> statePositions = new HashMap<>();
        int radius = Math.min(width, height) / 2 - 80;
        int centerX = width / 2;
        int centerY = height / 2;
        
        List<String> sortedStates = new ArrayList<>(dfaStates);
        Collections.sort(sortedStates);
        int numStates = sortedStates.size();
        
        for (int i = 0; i < numStates; i++) {
            double angle = 2 * Math.PI * i / numStates - Math.PI/2;
            int x = centerX + (int)(radius * Math.cos(angle));
            int y = centerY + (int)(radius * Math.sin(angle));
            statePositions.put(sortedStates.get(i), new Point(x, y));
        }
        
        return statePositions;
    }
    
    private void drawTransitions(Graphics2D g2d, Map<String, Point> statePositions) {
        g2d.setStroke(new BasicStroke(1.5f));
        
        for (String fromState : dfaTransitions.keySet()) {
            Map<String, String> transitions = dfaTransitions.get(fromState);
            
            for (String symbol : transitions.keySet()) {
                String toState = transitions.get(symbol);
                Point fromPoint = statePositions.get(fromState);
                Point toPoint = statePositions.get(toState);
                
                if (fromPoint == null || toPoint == null) continue;
                
                if (fromState.equals(toState)) {
                    drawSelfLoop(g2d, fromPoint, symbol);
                } else {
                    drawTransitionArrow(g2d, fromPoint, toPoint, symbol);
                }
            }
        }
    }
    
    private void drawSelfLoop(Graphics2D g2d, Point point, String symbol) {
        int loopRadius = 20;
        g2d.drawOval(point.x - loopRadius, point.y - 30 - loopRadius * 2, 
            loopRadius * 2, loopRadius * 2);
        g2d.setColor(Color.BLUE);
        g2d.drawString(symbol, point.x, point.y - 30 - loopRadius * 2);
        g2d.setColor(Color.BLACK);
    }
    
    private void drawTransitionArrow(Graphics2D g2d, Point fromPoint, Point toPoint, String symbol) {
        int stateRadius = 30;
        drawArrow(g2d, fromPoint.x, fromPoint.y, toPoint.x, toPoint.y, stateRadius);
        
        int midX = (fromPoint.x + toPoint.x) / 2;
        int midY = (fromPoint.y + toPoint.y) / 2;
        double angle = Math.atan2(toPoint.y - fromPoint.y, toPoint.x - fromPoint.x);
        int offsetX = (int)(12 * Math.cos(angle + Math.PI/2));
        int offsetY = (int)(12 * Math.sin(angle + Math.PI/2));
        
        g2d.setColor(Color.BLUE);
        g2d.drawString(symbol, midX + offsetX, midY + offsetY);
        g2d.setColor(Color.BLACK);
    }
    
    private void drawStates(Graphics2D g2d, Map<String, Point> statePositions, int width, int height) {
        int stateRadius = 30;
        int centerX = width / 2;
        int centerY = height / 2;
        
        for (String state : dfaStates) {
            Point pos = statePositions.get(state);
            if (pos == null) continue;
            
            // Draw state circle
            g2d.setColor(Color.WHITE);
            g2d.fillOval(pos.x - stateRadius, pos.y - stateRadius, 2 * stateRadius, 2 * stateRadius);
            g2d.setColor(Color.BLACK);
            g2d.drawOval(pos.x - stateRadius, pos.y - stateRadius, 2 * stateRadius, 2 * stateRadius);
            
            // Double circle for final states
            if (finalStates.contains(state)) {
                g2d.drawOval(pos.x - stateRadius + 5, pos.y - stateRadius + 5, 
                    2 * stateRadius - 10, 2 * stateRadius - 10);
            }
            
            // Draw state name
            FontMetrics fm = g2d.getFontMetrics();
            int textWidth = fm.stringWidth(state);
            g2d.drawString(state, pos.x - textWidth / 2, pos.y + 5);
            
            // Draw arrow to start state
            if (state.equals(startState)) {
                drawStartArrow(g2d, pos, centerX, centerY, stateRadius);
            }
        }
    }
    
    private void drawStartArrow(Graphics2D g2d, Point pos, int centerX, int centerY, int stateRadius) {
        double angle = Math.atan2(pos.y - centerY, pos.x - centerX);
        int arrowLength = 40;
        int startX = (int)(pos.x - (stateRadius + arrowLength) * Math.cos(angle));
        int startY = (int)(pos.y - (stateRadius + arrowLength) * Math.sin(angle));
        
        drawArrow(g2d, startX, startY, 
                 (int)(pos.x - stateRadius * Math.cos(angle)), 
                 (int)(pos.y - stateRadius * Math.sin(angle)), 0);
    }
    
    private void drawArrow(Graphics2D g2d, int x1, int y1, int x2, int y2, int stateRadius) {
        double angle = Math.atan2(y2 - y1, x2 - x1);
        
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