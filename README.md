# NFA to DFA Converter

A Java application that provides a graphical interface for converting Non-deterministic Finite Automata (NFA) to Deterministic Finite Automata (DFA) with visual representation.

## Features

- User-friendly GUI for inputting NFA definitions
- Automatic conversion from NFA to DFA using the subset construction algorithm
- Support for epsilon (ε) transitions
- Visual representation of the resulting DFA
- Textual output of the DFA transitions and states

## Screenshots

![image](https://github.com/user-attachments/assets/16db9751-c066-49db-87e0-13c9605c5caf)
![image](https://github.com/user-attachments/assets/8c2e3186-af26-4763-ac13-6e04556d5d82)


## Installation

### Prerequisites

- Java Development Kit (JDK) 8 or higher
- Swing library (included in standard JDK)

### Building from Source

1. Clone the repository:
   ```
   git clone https://github.com/adi30042005/NFA-to-DFA-VISUALIZATION.git
   ```

2. Navigate to the project directory:
   ```
   cd NFA-to-DFA-VISUALIZATION
   ```

3. Compile the Java files:
   ```
   javac *.java
   ```

4. Run the application:
   ```
   java NFAtoDFA
   ```

## Usage

1. Enter your NFA definition in the input text area using the following format:
   ```
   States: q0,q1,q2,...
   Alphabet: a,b,...
   Transition: state,symbol,nextState
   Transition: state,e,nextState  (for epsilon transitions)
   Start: startState
   Final: finalState1,finalState2,...
   ```

2. Click the "Convert NFA to DFA" button to perform the conversion.

3. The resulting DFA will be displayed in the output text area and visualized in the drawing panel.

4. You can also click the "Load Example" button to see a pre-defined example.

## Example Input

```
States: q0,q1,q2
Alphabet: a,b
Transition: q0,a,q0
Transition: q0,a,q1
Transition: q0,e,q2
Transition: q1,b,q2
Start: q0
Final: q2
```

## Algorithm Details

The converter implements the subset construction algorithm for NFA-to-DFA conversion:

1. Computes epsilon closures for all states in the NFA
2. Creates a new DFA start state from the epsilon closure of the NFA start state
3. For each DFA state and input symbol, computes the next state using the move function and epsilon closure
4. Marks a DFA state as accepting if it contains any accepting NFA state

## Implementation Notes

- The program handles the special "empty set" (∅) state for transitions that lead nowhere
- State names in the DFA are represented as sets of NFA states (e.g., {q0,q1,q2})
- The visualization places states in a circular arrangement for clear representation

## Contributing

Contributions are welcome! Please feel free to submit a Pull Request.

## License

This project is licensed under the MIT License - see the LICENSE file for details.

## Acknowledgments

- Based on formal language theory and automata conversion algorithms
- Inspired by educational tools for teaching automata theory
