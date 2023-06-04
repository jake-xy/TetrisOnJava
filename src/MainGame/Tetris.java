package MainGame;

import java.awt.*;
import javax.swing.*;
import Utilities.*;

public class Tetris {
    final static int blockSize = 24; //32
    final static int blockOffSet = 2; // block offset.. i.e., the amount of border that will be removed around each bloc
    // objects
    char[][] matrix = new char[20][10];
    Bag bag;
    Piece currentPiece;
    ShadowPiece shadowPiece;
    boolean running = true;

    // score logic
    int score = 0, prevClearScore;
    int level = 1;
    int combo = 0;
    int levelUpScore = level*2000;
    boolean canCombo = false;
    boolean justCLearedRows = false;

    // descend animation (sorta) when a clear happens
    boolean blocksDescending = false;
    int descendTicks = 0;
    int[][] unclearedRows = new int[0][2];
    
    // origin position of where the matrix lies on the gamePanel
    final static int ogX = (int)MainPanel.WIN_SIZE.getWidth()/2 - blockSize*10/2;
    final static int ogY = (int)MainPanel.WIN_SIZE.getHeight()/2 - blockSize*20/2;
    
    // Image
    static Image[] blockImages = new Image[7];

    // Audio
    Sound mainTheme = new Sound("../data/Original Tetris Theme.wav");
    
    public Tetris(Bag bag) {
        this.bag = bag;
        this.currentPiece = new Piece(bag.currentShape, false, false);
        this.shadowPiece = new ShadowPiece(currentPiece, this.matrix);
        for (int r = 0; r < 20; r++) for (int c = 0; c < 10; c++) matrix[r][c] = ' ';
        // updateMatrix();
        for (int i = 0; i < 7; i++) {
            blockImages[i] = new ImageIcon("data/" + Bag.SHAPES[i] + ".png").getImage();
        }
        mainTheme.adjustVolume(-20.0f);
        mainTheme.loop();
    }

    public void update() {
        this.currentPiece.update(this.matrix, shadowPiece, this);
        this.shadowPiece.update();

        if (this.blocksDescending) {
            this.descendTicks += 1;

            if (this.descendTicks >= MainPanel.FPS/4) {
                for (int i = 0; i < unclearedRows.length; i++) {
                    for (int c = 0; c < 10; c ++) {
                        if (unclearedRows[i][1] > 0) {
                            int r = unclearedRows[i][0];
    
                            this.matrix[r + unclearedRows[i][1]][c] = this.matrix[r][c];
                            this.matrix[r][c] = ' ';
                        }
                    }
                }

                this.descendTicks = 0;
                this.blocksDescending = false;
            }
        }

        // check placing down
        if ((int)shadowPiece.r == (int)currentPiece.r) {
            currentPiece.touchedDownTicks += 1;
        }

        if (currentPiece.touchedDownTicks >= MainPanel.FPS) {
            // essentially means if it has touched the ground for 1 sec
            placePiece();
        }

        if (currentPiece.r > shadowPiece.r) {
            currentPiece.r = shadowPiece.r;
        }

        // shadowpiece relateed things
        if (this.currentPiece.r > this.shadowPiece.r) {
            currentPiece.r = shadowPiece.r;
            currentPiece.updateBoundingRect();
        }

        // level
        if (this.score >= this.levelUpScore) {
            this.level += 1;
            this.levelUpScore = 2000*level + this.score;
        }
    }


    public void placePiece() {
        int rowPos = (int) currentPiece.r;
        int colPos = (int) currentPiece.c;
        // place the blocks on the piece matrix
        for (int r = 0; r < 4; r++) {
            for (int c = 0; c < 4; c++) {
                if (currentPiece.matrix[r][c] == currentPiece.shape) {
                    this.matrix[rowPos + r][colPos + c] = currentPiece.shape;
                }
            }
        }

        this.currentPiece = bag.getNextPiece(currentPiece);
        this.shadowPiece = new ShadowPiece(currentPiece, matrix);

        new Sound().play("../data/drop down.wav");
        // NOTE: implement a higher pitched thump depending on how high the combo is
        this.clearRows();
    }


    public void clearRows() {
        // check if game over first
        for (int c = 0; c < 10; c++) {
            if (this.matrix[0][c] != ' ') {
                this.running = false;
            }
        }

        boolean isRowCLeared, hasBlocks;
        int clearedCount = 0;
        unclearedRows = new int[0][2];
        this.justCLearedRows = false;
        // holds the rows(position) that has blocks and info whether it's cleared or not (-1 --> cleared, >= 0 --> descend count)
        for (int r = 19; r >= 0; r--) {
            isRowCLeared = true;
            hasBlocks = false;
            for (int c = 0; c < 10; c++) {
                if (this.matrix[r][c] == ' ') {
                    isRowCLeared = false;
                }
                else {
                    hasBlocks = true;
                }
            }

            if (isRowCLeared) {
                // append to out
                clearedCount += 1;
                // clear that row
                for (int col = 0; col < 10; col++) {
                    // matrix[r + clearedCount][col] = matrix[r][col];
                    matrix[r][col] = ' ';
                }

                this.blocksDescending = true;
                this.justCLearedRows = true;
            }

            if (!isRowCLeared && hasBlocks) {
                unclearedRows = Utils.append(unclearedRows, new int[] {r, clearedCount});
            }
                            // on your note, this is what you'do:
                            // unclearedRows append(r, clearedRows)
                            // on another function, maybe descendUnclearedRow(int[][] unclearedRows), is where you'll drop these pieces
                            // (for a slower descent) mayybe a boolean to signal moving down of the piece. Then, have an if statement to check if descending
                            // slowly add to the uncleared rows until it reaches the targetted row pos of that uncleared row, then stop descending
        }

        if (unclearedRows.length == 0) {
            new Sound().play("../data/perfect clear.wav");
            score += 3000;
        }

        if (this.justCLearedRows) {
            // totalScore = baseScore*numOfRowsCleared*level + (numOfRowsCleard - 1 )*baseScore*level + {combo*50*level}
            if (this.canCombo) {
                combo += 1;
            }
            this.score += 100*clearedCount*this.level + (clearedCount - 1)*100*this.level + combo*50*level;
            this.prevClearScore = 100*clearedCount*this.level + (clearedCount - 1)*100*this.level + combo*50*level;
            this.canCombo = true;

            new Sound().play("../data/clear.wav");
            if (this.combo > 0 && this.combo < 3) {
                new Sound().play(String.format("../data/clear %d.wav", this.combo));
            }
            if (this.combo > 3) {
                new Sound().play("../data/clear 3.wav");                
            }
        }
        else {
            this.combo = 0;
            this.canCombo = false;
        }
    }


    public void dropPiece() {
        // score
        this.score += 2 * (shadowPiece.r - currentPiece.r) * this.level;
        // drop
        this.currentPiece.r = shadowPiece.r;
        this.currentPiece.updateBoundingRect();
        this.currentPiece.touchedDownTicks = MainPanel.FPS;
    }


    public void draw(Graphics gr) {

        Graphics2D g = (Graphics2D) gr;
        // drawing the background / the field thingy
        int imageIndex = 0;

        // black background of the blocks area
        g.setColor(new Color(50,50,50));
        g.fillRect(ogX, ogY, blockSize*10, blockSize*20);
        // lines / grid of the area
        g.setColor(new Color(150,150,150));
        for (int i = 1; i <= 9; i++ ) {
            g.drawLine(ogX + i*blockSize, ogY, ogX + i*blockSize, ogY + blockSize*20);
        }

        for (int i = 1; i <= 19; i++) {
            g.drawLine(ogX, ogY + i*blockSize, ogX + blockSize*10, ogY + i*blockSize);
        }
        // border line
        g.setStroke(new BasicStroke(6));
        g.setColor(Color.cyan);
        // g.drawRect(ogX, ogY, blockSize*10, blockSize*20);
        g.drawRoundRect(ogX - 4, ogY - 4, blockSize*10 + 8, blockSize*20 + 8, 30, 30);
        
        // drawing the blocks sprite

        for (int r = 0; r < 20; r++) {

            for (int c = 0; c < 10; c++) {
                for (int i = 0; i < 7; i++) {
                    // getting the appropriate index (in blockImages[]) based on the current piece's shape
                    if (Bag.SHAPES[i] == this.matrix[r][c]) {
                        imageIndex = i;
                        break;
                    }
                }

                // actually drawing the sprites
                if (this.matrix[r][c] != ' ') {
                    g.drawImage(blockImages[imageIndex], 
                        ogX + blockSize*c + blockOffSet, 
                        ogY + blockSize*r + blockOffSet,
                        blockSize - blockOffSet*2, 
                        blockSize - blockOffSet*2,
                        null
                    );
                }
            }
        }

        shadowPiece.draw(g);
        currentPiece.draw(g);

        // drawing the hold piece
        // g.setColor(Color.cyan);
        // g.drawRect(ogX - blockSize*5, ogY, blockSize*5, blockSize*5);
        g.setColor(Color.black);
        g.setFont(new Font("Courier New", 1, blockSize));
        g.drawString("HOLD", ogX - blockSize*3 - 18, ogY + blockSize);
        if (bag.holdShape != ' ') {
            Piece holdPiece = new Piece(bag.holdShape, false, false);
            if (holdPiece.shape == 'I' && holdPiece.rotation == 0) {
                holdPiece.rotateLeft();
            }
            int x = (int)(ogX - blockSize*2.5) - (holdPiece.right - holdPiece.left + 1)*blockSize / 2;
            int y = (int)(ogY + blockSize*2.5) - (holdPiece.bot - holdPiece.top + 1)*blockSize / 2;
            holdPiece.draw(g, x, y);
        }

        // drawing the next pieces
        g.drawString("NEXT", ogX + blockSize*12 - 18, ogY + blockSize);
        for (int i = 1; i <= 3; i++) {
            Piece nextPiece = new Piece(bag.contents[i], false, false);
            if (nextPiece.shape == 'I' && nextPiece.rotation == 0) nextPiece.rotateLeft();
            int x = (int)(ogX + 12.5*blockSize) - (nextPiece.right - nextPiece.left + 1)*blockSize / 2;
            int y = (int)(ogY + blockSize*5*(i-1) + blockSize*2.5) - (nextPiece.bot - nextPiece.top + 1)*blockSize / 2;
            nextPiece.draw(g, x, y);
        }

        // draw the score
        g.drawString("SCORE", (int)(ogX - blockSize*2.5) - 5*blockSize/2, ogY + blockSize*6);
        g.drawString(String.format("%d", this.score), (int)(ogX - blockSize*2.5) - blockSize/2, ogY + blockSize*7);

        // drawing the level
        g.drawString("LEVEL", ogX - blockSize*4 - 18, ogY + blockSize*8 + 20);
        g.drawString(String.format("%d", this.level), ogX - blockSize*4 - 18, ogY + blockSize*8 + 50);

    }
}

// baseScore = 100, 

