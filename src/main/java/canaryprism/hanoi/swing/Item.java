package canaryprism.hanoi.swing;

import javax.swing.JComponent;

public sealed abstract class Item extends JComponent permits Card, FutureCard {
    
}
