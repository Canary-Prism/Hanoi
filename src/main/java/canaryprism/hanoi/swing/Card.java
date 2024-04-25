package canaryprism.hanoi.swing;

import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.SwingConstants;
import javax.swing.UIManager;

import java.awt.Color;

import com.formdev.flatlaf.ui.FlatBorder;

public class Card extends JComponent {

    private final JLabel label;
    public Card(int number) {
        this.label = new JLabel(String.valueOf(number), SwingConstants.CENTER);
        setBorder(new FlatBorder());
    }

    @Override
    public void setBounds(int x, int y, int width, int height) {
        super.setBounds(x, y, width, height);
        label.setBounds(x, x, width, height);
    }

    @Override
    protected void paintComponent(java.awt.Graphics g) {
        var bounds = getBounds();

        g.setColor(UIManager.getColor("Panel.background"));
        g.fillRect(0, 0, bounds.width, bounds.height);
        g.setColor(Color.white);

        g.drawRect(0, 0, bounds.width, bounds.height);
        // super.paintComponent(g);
        label.paint(g);
    }
}
