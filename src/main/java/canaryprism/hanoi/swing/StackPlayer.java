package canaryprism.hanoi.swing;

import java.util.ArrayList;
import java.util.List;

import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;
import java.awt.Point;
import java.awt.Rectangle;
import javax.swing.JComponent;

public class StackPlayer extends JComponent implements AutoCloseable {

    public static final int card_width = 40;
    public static final int card_height = 30;

    public final double fps;
    public final double dt;

    private volatile boolean running = true;
    private volatile int current_animations = 0;

    public StackPlayer(double fps) {
        setLayout(null);

        this.fps = fps;
        this.dt = 1000.0 / fps;

        if (fps <= 0 || Double.isInfinite(fps) || Double.isNaN(fps)){
            throw new IllegalArgumentException("malformed fps");
        }

        new Thread(() -> {
            while (running) {
                if (current_animations > 0)
                    repaint();
                try {
                    Thread.sleep((long) dt);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }, "StackPlayer").start();
    }

    private final List<Card> stack_a = new ArrayList<>();
    private final List<Card> stack_b = new ArrayList<>();
    private final List<Card> stack_c = new ArrayList<>();

    private final List<Card> moving = new ArrayList<>();

    private Point getStackPosition(int stack) {
        return switch (stack) {
            case 0 -> new Point(getSize().width / 6, 10 + (card_height - 10) * stack_a.size());
            case 1 -> new Point(getSize().width / 2, 10 + (card_height - 10) * stack_b.size());
            case 2 -> new Point(5 * getSize().width / 6, 10 + (card_height - 10) * stack_c.size());
            default -> throw new IllegalArgumentException("Invalid stack");
        };
    }

    private Rectangle spawnRect() {
        return new Rectangle(0, getSize().height - card_height, card_width, card_height);
    }

    public CompletableFuture<Void> newCard(int number, int stack) {
        var card = new Card(number);
        card.setBounds(spawnRect());

        var target = switch (stack) {
            case 0 -> stack_a;
            case 1 -> stack_b;
            case 2 -> stack_c;
            default -> throw new IllegalArgumentException("Invalid stack");
        };

        final var travel_time = 250.0;

        var steps = (int) (travel_time / dt);

        var path = new Point[steps];

        var destx = getStackPosition(stack).x;
        var desty = getStackPosition(stack).y;

        var xdistance = destx - card.getBounds().x;
        var ydistance = desty - card.getBounds().y;

        int index;

        synchronized (moving) {
            moving.add(card);
            index = target.size();
            target.add(null);
        }

        return CompletableFuture.runAsync(() -> {

            for (int i = 0; i < steps; i++) {
                path[i] = new Point(
                    card.getBounds().x + (int) (xdistance * i / steps),
                    card.getBounds().y + (int) (ydistance * i / steps)
                );
            }

            current_animations++;

            for (int i = 0; i < steps; i++) {

                card.setBounds(path[i].x, path[i].y, card_width, card_height);
                // repaint();
                try {
                    Thread.sleep((long) dt);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

            card.setBounds(destx, desty, card_width, card_height);

            synchronized (moving) {
                moving.remove(card);
                target.set(index, card);
            }
            
            current_animations--;

            repaint();
        });
    }


    public CompletableFuture<Void> moveCard(int stack_from, int stack_to) {
        var from = switch (stack_from) {
            case 0 -> stack_a;
            case 1 -> stack_b;
            case 2 -> stack_c;
            default -> throw new IllegalArgumentException("Invalid stack");
        };
        var to = switch (stack_to) {
            case 0 -> stack_a;
            case 1 -> stack_b;
            case 2 -> stack_c;
            default -> throw new IllegalArgumentException("Invalid stack");
        };

        if (from.isEmpty()) {
            throw new IllegalArgumentException("Empty stack");
        }

        if (from == to) {
            throw new IllegalArgumentException("Same stack");
        }

        var card = from.getLast();

        final var travel_time = 200.0;

        var steps = (int) (travel_time / dt);

        var path = new Point[steps];

        var destx = getStackPosition(stack_to).x;
        var desty = getStackPosition(stack_to).y;

        var xdistance = destx - card.getBounds().x;
        var ydistance = desty - card.getBounds().y;

        int index;

        synchronized (moving) {
            moving.add(card);
            from.remove(card);
            index = to.size();
            to.add(null);
        }

        return CompletableFuture.runAsync(() -> {

            for (int i = 0; i < steps; i++) {
                path[i] = new Point(
                    card.getBounds().x + (int) (xdistance * i / steps),
                    card.getBounds().y + (int) (ydistance * i / steps)
                );
            }

            current_animations++;

            for (int i = 0; i < steps; i++) {

                card.setBounds(path[i].x, path[i].y, card_width, card_height);
                // repaint();
                try {
                    Thread.sleep((long) dt);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

            card.setBounds(destx, desty, card_width, card_height);

            synchronized (moving) {
                moving.remove(card);
                to.set(index, card);
            }
            
            current_animations--;

            repaint();
        });
    }


    @Override
    protected void paintChildren(java.awt.Graphics g) {
        Consumer<Card> painter = (card) -> {

            if (card == null) {
                return;
            }

            var g2 = g.create(card.getBounds().x, card.getBounds().y, card_width, card_height);

            card.paint(g2);

            g2.dispose();
            
        };

        synchronized (moving) {
            stack_a.forEach(painter);
            stack_b.forEach(painter);
            stack_c.forEach(painter);
            moving.forEach(painter);
        }
    }

    @Override
    public void close() {
        repaint();
        running = false;
    }
    
}
