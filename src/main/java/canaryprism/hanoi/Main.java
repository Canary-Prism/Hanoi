package canaryprism.hanoi;

import java.util.ArrayList;
import java.util.List;

import javax.swing.JFrame;

import com.formdev.flatlaf.themes.FlatMacDarkLaf;

import canaryprism.hanoi.swing.StackPlayer;

public class Main {
    public static void main(String[] args) {

        if (args.length != 1) {
            System.err.println("Error: expected 1 argument (int), got " + args.length + " arguments.");
            System.exit(1);
        }

        final int n = Integer.parseInt(args[0]);

        FlatMacDarkLaf.setup();
        var frame = new JFrame();

        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        var player = new StackPlayer(60);

        frame.getContentPane().add(player);

        frame.setSize(600, StackPlayer.card_height * n + 20);
        frame.setResizable(false);

        frame.setVisible(true);

        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }


        for (int i = n; i > 0; i--) {
            player.newCard(i, 0).join();
        }


        var moves = hanoi(n, 0, 2);

        for (var move : moves) {
            player.moveCard(move.from, move.to).join();
        }

        player.close();
    }

    record Move(int from, int to) {}

    static ArrayList<Move> hanoi(int n, int stack_from, int stack_to) {
        if (n == 1) {
            var list = new ArrayList<Move>();
            list.add(new Move(stack_from, stack_to));
            return list;
        }
        var list = hanoi(n - 1, stack_from, neither(stack_from, stack_to));
        list.add(new Move(stack_from, stack_to));
        list.addAll(hanoi(n - 1, neither(stack_from, stack_to), stack_to));
        return list;
    }

    static int neither(int a, int b) {
        return List.of(0, 1, 2).stream().filter(i -> i != a && i != b).findAny().get();
    }
}
