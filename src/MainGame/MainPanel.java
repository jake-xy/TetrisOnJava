package MainGame;
import javax.swing.*;

import Utilities.Sound;
import java.awt.*;
import java.awt.event.*;

public class MainPanel extends JPanel implements Runnable{
    final static Dimension WIN_SIZE = new Dimension(640, 720);
    Thread gameThread;
    final static int FPS = 60;
    Tetris tetris;
    Image canvas;
    Graphics graphics;

    public MainPanel(Tetris tetris) {
        this.tetris = tetris;

        this.addKeyListener(new AL());
        this.setPreferredSize(WIN_SIZE);
        this.setFocusable(true);
        this.startGameThread();
    }

    public void startGameThread() {
        gameThread = new Thread(this);
        gameThread.start();
    }

    public void paint(Graphics g) {
        canvas = createImage(this.getWidth(), this.getHeight());
        graphics = canvas.getGraphics();

        this.draw(graphics);
        g.drawImage(canvas, 0, 0, null);
    }

    public void draw(Graphics g) {
        // this is where you'll draw all your objects
        tetris.draw(g);
    }

    public void run() {
        // main loop
        while (tetris.running) {

            tetris.update();
            repaint();

            try {
                Thread.sleep((long)1000/FPS);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        System.out.println("Game Over da ze");
    }

    public class AL extends KeyAdapter {
        public void keyPressed(KeyEvent e) {
            switch (e.getKeyCode()) {
                case KeyEvent.VK_LEFT:
                    // tetris.currentPiece.move(0, -1, tetris.matrix);
                    tetris.currentPiece.swayLeft = true;
                    break;
                case KeyEvent.VK_RIGHT:
                    // tetris.currentPiece.move(0, 1, tetris.matrix);
                    tetris.currentPiece.swayRight = true;
                    break;
                case KeyEvent.VK_UP: 
                    tetris.currentPiece.rotate(tetris.matrix);
                    // new Sound().play("../data/rotate.wav");
                    break;
                case KeyEvent.VK_DOWN:
                    tetris.currentPiece.flyDown = true;
                    break;
                case KeyEvent.VK_SHIFT:
                    new Sound().play("../data/hold.wav");
                    tetris.currentPiece = tetris.bag.hold(tetris.currentPiece);
                    tetris.shadowPiece = new ShadowPiece(tetris.currentPiece, tetris.matrix);
                    break;
                case KeyEvent.VK_SPACE:
                    tetris.dropPiece();
                    break;
                
            }
        }

        public void keyReleased(KeyEvent e) {
            switch (e.getKeyCode()) {
                case KeyEvent.VK_LEFT:
                    tetris.currentPiece.swayLeft = false;
                    break;
                case KeyEvent.VK_RIGHT:
                    tetris.currentPiece.swayRight = false;
                    break;
                case KeyEvent.VK_DOWN:
                    tetris.currentPiece.flyDown = false;
                    break;
            }
        }
    }
}


