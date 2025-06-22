import java.util.*;

public class NFAToDFAConverter {
    private static final String DEAD_STATE = "∅";
    private Map<String, Map<String, String>> dfaTransitions = new HashMap<>();
    private Set<String> dfaStates = new HashSet<>();
    private Set<String> dfaFinalStates = new HashSet<>();
    private String dfaStartState;
    private NFAParser nfaParser;
    
    public void convert(NFAParser nfaParser) {
        this.nfaParser = nfaParser;
        clear();
        
        Set<String> startClosure = getEpsilonClosure(nfaParser.getStartState());
        dfaStartState = formatStateSet(startClosure);
        
        Queue<Set<String>> queue = new LinkedList<>();
        Set<String> processedStates = new HashSet<>();
        
        queue.add(startClosure);
        dfaStates.add(dfaStartState);
        
        checkFinalStates(startClosure, dfaStartState);
        
        while (!queue.isEmpty()) {
            Set<String> currentStateSet = queue.poll();
            String currentDFAState = formatStateSet(currentStateSet);
            
            if (processedStates.contains(currentDFAState)) {
                continue;
            }
            
            processedStates.add(currentDFAState);
            
            for (String symbol : nfaParser.getAlphabet()) {
                Set<String> nextStateSet = getEpsilonClosure(move(currentStateSet, symbol));
                String nextDFAState = nextStateSet.isEmpty() ? DEAD_STATE : formatStateSet(nextStateSet);
                
                dfaTransitions.computeIfAbsent(currentDFAState, k -> new HashMap<>())
                    .put(symbol, nextDFAState);
                
                if (!nextStateSet.isEmpty() && !dfaStates.contains(nextDFAState)) {
                    dfaStates.add(nextDFAState);
                    queue.add(nextStateSet);
                    checkFinalStates(nextStateSet, nextDFAState);
                }
            }
        }
        
        addDeadStateIfNeeded();
    }
    
    private void clear() {
        dfaTransitions.clear();
        dfaStates.clear();
        dfaFinalStates.clear();
        dfaStartState = null;
    }
    
    private Set<String> getEpsilonClosure(String state) {
        Set<String> closure = new HashSet<>();
        Stack<String> stack = new Stack<>();
        
        closure.add(state);
        stack.push(state);
        
        while (!stack.empty()) {
            String currentState = stack.pop();
            Set<String> epsilonTransitions = nfaParser.getNFATransitions()
                .getOrDefault(currentState + ",ε", new HashSet<>());
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
            Set<String> transitions = nfaParser.getNFATransitions()
                .getOrDefault(state + "," + symbol, new HashSet<>());
            result.addAll(transitions);
        }
        return result;
    }
    
    private void checkFinalStates(Set<String> stateSet, String dfaState) {
        for (String state : stateSet) {
            if (nfaParser.getFinalStates().contains(state)) {
                dfaFinalStates.add(dfaState);
                break;
            }
        }
    }
    
    private void addDeadStateIfNeeded() {
        boolean needDeadState = dfaTransitions.values().stream()
            .anyMatch(transitions -> transitions.containsValue(DEAD_STATE));
        
        if (needDeadState) {
            dfaStates.add(DEAD_STATE);
            Map<String, String> deadTransitions = new HashMap<>();
            for (String symbol : nfaParser.getAlphabet()) {
                deadTransitions.put(symbol, DEAD_STATE);
            }
            dfaTransitions.put(DEAD_STATE, deadTransitions);
        }
    }
    
    private String formatStateSet(Set<String> stateSet) {
        if (stateSet.isEmpty()) return DEAD_STATE;
        List<String> sortedStates = new ArrayList<>(stateSet);
        Collections.sort(sortedStates);
        return "{" + String.join(",", sortedStates) + "}";
    }
    
    public String generateDFAOutput() {
        StringBuilder output = new StringBuilder();
        
        output.append("DFA States: ").append(String.join(", ", dfaStates)).append("\n\n");
        output.append("DFA Transitions:\n");
        
        List<String> sortedStates = new ArrayList<>(dfaStates);
        Collections.sort(sortedStates);
        
        for (String state : sortedStates) {
            Map<String, String> transitions = dfaTransitions.getOrDefault(state, new HashMap<>());
            List<String> sortedSymbols = new ArrayList<>(nfaParser.getAlphabet());
            Collections.sort(sortedSymbols);
            
            for (String symbol : sortedSymbols) {
                String nextState = transitions.getOrDefault(symbol, DEAD_STATE);
                output.append(state).append(" -- ").append(symbol).append(" --> ").append(nextState).append("\n");
            }
        }
        
        output.append("\nStart State: ").append(dfaStartState).append("\n");
        output.append("Final States: ").append(String.join(", ", dfaFinalStates));
        
        return output.toString();
    }
    
    // Getters for the converted DFA
    public Set<String> getDFAStates() { return dfaStates; }
    public Map<String, Map<String, String>> getDFATransitions() { return dfaTransitions; }
    public String getDFAStartState() { return dfaStartState; }
    public Set<String> getDFAFinalStates() { return dfaFinalStates; }
}