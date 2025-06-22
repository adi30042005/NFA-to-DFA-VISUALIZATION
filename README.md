# NFA to DFA Visualizer

This Java Swing-based application converts a **Non-deterministic Finite Automaton (NFA)** into its equivalent **Deterministic Finite Automaton (DFA)** and visualizes the process.

---

## 🎯 Features

- Interactive GUI to input states, transitions, start & final states
- NFA to DFA conversion using subset construction algorithm
- Graphical visualization of the resulting DFA
- Modularized Java code for clarity and reuse

---

## 🧠 Concepts Used

- Subset Construction Algorithm (Power Set Construction)
- DFA and NFA theory (Automata Theory)
- State management, string parsing
- Java Swing for GUI

---

## 📁 Project Structure

src/
├── AutomataGUI.java # Main GUI window
├── AutomataVisualizer.java # Handles drawing the automaton
├── NFAParser.java # Parses NFA input
├── NFAToDFA.java # Core logic for NFA to DFA conversion
└── NFAToDFAConverter.java # Utility for managing state sets

yaml
Copy
Edit

---

## 🛠️ Technologies

- Java 8+
- Java Swing (UI)
- Plain text input parsing
- Object-Oriented Programming principles

---

## ▶️ How to Run

1. Navigate to the project directory:
   ```bash
   cd NFA-to-DFA-VISUALIZATION
Compile:

bash
Copy
Edit
javac *.java
Run:

bash
Copy
Edit
java AutomataGUI
🔍 Example
Input NFA:

States: q0, q1

Alphabet: 0, 1

Start: q0

Final: q1

Transitions:

From	Input	To
q0	0	q0
q0	1	q0,q1

Output DFA:
States like {q0}, {q0,q1}, etc., are created, and transitions defined accordingly. Output is shown both in console and GUI.

📷 Screenshot
![image](https://github.com/user-attachments/assets/44c64ad9-5edb-45ec-8eea-c4046ede9457)



📄 License
MIT License — use and modify freely with attribution.

🙌 Acknowledgements
Made with ❤️ for automata theory projects and academic demos.
