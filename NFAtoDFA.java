import javax.swing.*;
import java.awt.*;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;
import java.util.Collections;
import java.util.Set;

public class NFAtoDFA {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new AutomataGUI().setVisible(true);
        });
    }
}