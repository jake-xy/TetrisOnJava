import MainGame.*;
import javax.swing.*;

public class App {
    public static void main(String[] args) throws Exception {
        JFrame win = new JFrame();
        Bag bag = new Bag();
        Tetris tetris = new Tetris(bag);
        MainPanel gamePanel = new MainPanel(tetris);

        win.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        win.setResizable(false);
        win.setTitle("Tetris");
        // win.setLayout(new BorderLayout());

        win.add(gamePanel);
        win.pack();
        win.setLocationRelativeTo(null);
        win.setVisible(true);
    }
}
