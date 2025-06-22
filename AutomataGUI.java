import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;

public class AutomataGUI extends JFrame {
    private JTextArea inputArea, outputArea;
    private JPanel drawingPanel;
    private NFAParser nfaParser;
    private NFAToDFAConverter converter;
    private AutomataVisualizer visualizer;
    
    public AutomataGUI() {
        setTitle("NFA to DFA Converter");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());
        
        nfaParser = new NFAParser();
        converter = new NFAToDFAConverter();
        visualizer = new AutomataVisualizer();
        
        initComponents();
    }
    
    private void initComponents() {
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
                visualizer.drawAutomata(g, drawingPanel.getWidth(), drawingPanel.getHeight());
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
        exampleButton.addActionListener(this::loadExample);
        
        // Add convert action
        convertButton.addActionListener(this::convertNFAToDFA);
    }
    
    private void loadExample(ActionEvent e) {
        inputArea.setText("States: q0,q1,q2\n" +
                "Alphabet: a,b\n" +
                "Transition: q0,a,q0\n" +
                "Transition: q0,a,q1\n" +
                "Transition: q0,e,q2\n" +
                "Transition: q1,b,q2\n" +
                "Start: q0\n" +
                "Final: q2");
    }
    
    private void convertNFAToDFA(ActionEvent e) {
        try {
            nfaParser.parse(inputArea.getText());
            converter.convert(nfaParser);
            outputArea.setText(converter.generateDFAOutput());
            visualizer.setDFA(converter.getDFAStates(), converter.getDFATransitions(), 
                             converter.getDFAStartState(), converter.getDFAFinalStates(), 
                             nfaParser.getAlphabet());
            drawingPanel.repaint();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage(), 
                "Input Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}