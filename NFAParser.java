import java.util.*;

public class NFAParser {
    private Map<String, Set<String>> nfaTransitions = new HashMap<>();
    private Set<String> states = new HashSet<>();
    private Set<String> alphabet = new HashSet<>();
    private String startState;
    private Set<String> finalStates = new HashSet<>();
    
    public void parse(String input) {
        clear();
        String[] lines = input.split("\n");
        
        for (String line : lines) {
            line = line.trim();
            if (line.startsWith("States:")) {
                parseStates(line.substring(7));
            } else if (line.startsWith("Alphabet:")) {
                parseAlphabet(line.substring(9));
            } else if (line.startsWith("Transition:")) {
                parseTransition(line.substring(11));
            } else if (line.startsWith("Start:")) {
                startState = line.substring(6).trim();
            } else if (line.startsWith("Final:")) {
                parseFinalStates(line.substring(6));
            }
        }
    }
    
    private void clear() {
        nfaTransitions.clear();
        states.clear();
        alphabet.clear();
        finalStates.clear();
        startState = null;
    }
    
    private void parseStates(String statesStr) {
        String[] statesList = statesStr.split(",");
        for (String state : statesList) {
            states.add(state.trim());
        }
    }
    
    private void parseAlphabet(String alphabetStr) {
        String[] alphabetList = alphabetStr.split(",");
        for (String symbol : alphabetList) {
            alphabet.add(symbol.trim());
        }
    }
    
    private void parseTransition(String transitionStr) {
        String[] parts = transitionStr.split(",");
        if (parts.length != 3) {
            throw new IllegalArgumentException("Invalid transition format: " + transitionStr);
        }
        
        String fromState = parts[0].trim();
        String symbol = parts[1].trim();
        String toState = parts[2].trim();
        
        if (symbol.equals("e") || symbol.equals("ε")) {
            symbol = "ε";
        }
        
        nfaTransitions.computeIfAbsent(fromState + "," + symbol, k -> new HashSet<>()).add(toState);
    }
    
    private void parseFinalStates(String finalStatesStr) {
        String[] finalStatesList = finalStatesStr.split(",");
        for (String state : finalStatesList) {
            finalStates.add(state.trim());
        }
    }
    
    // Getters for the parsed data
    public Map<String, Set<String>> getNFATransitions() { return nfaTransitions; }
    public Set<String> getStates() { return states; }
    public Set<String> getAlphabet() { return alphabet; }
    public String getStartState() { return startState; }
    public Set<String> getFinalStates() { return finalStates; }
}