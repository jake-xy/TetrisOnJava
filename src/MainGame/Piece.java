package MainGame;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

import Utilities.Sound;

import java.awt.*;

public class Piece {
    // general variables
    char[][] matrix = new char[4][4];
    char[][][] matrices = new char[4][4][4];
    char shape;
    int rotation = 0, maxRotation;
    double r, c;
    int top, left, bot, right, width;

    // user input
    boolean swayLeft, swayRight, flyDown = false;
    int swayTick = 0;

    // checking if piece is down logic
    int touchedDownTicks = 0;
    
    // for the sfx
    int prevC;
    int prevR;

    Piece (char shape, boolean swayLeft, boolean swayRight) {
        this.shape = shape;
        this.swayLeft = swayLeft;
        this.swayRight = swayRight;
        this.r = 0;
        this.c = 4;
        getMatrices();
        this.rotation = 0;
        updateMatrix(rotation);
        updateBoundingRect();
        this.maxRotation = getMaxRotation();
        prevC = (int)this.c;
        prevR = (int)this.r;
    }   


    public void move(double rowVel, double colVel, char[][] tetrisMatrix) {
        if (this.bot < 19) this.r += rowVel;
        updateBoundingRect();
        // collision
        while (this.bot > 19) {
            this.r -= 1;
            updateBoundingRect();
        }
        
        this.c += colVel;
        updateBoundingRect();
        // collision
        while (this.left < 0) {
            this.c += 1;
            updateBoundingRect();
        }

        while (this.right > 9) {
            this.c -= 1;
            updateBoundingRect();
        }

        // iterate through every space on this piece matrix
        boolean checklingCollision = true;
        int noCollisionBlock;
        boolean breakOuterLoop;

        while (checklingCollision) {
            noCollisionBlock = 0;
            breakOuterLoop = false;
            for (int r = 0; r < 4; r++) {
                if (breakOuterLoop) break;
                for (int c = 0; c < 4; c++) {
                    // if that space is a block
                    if (this.matrix[r][c] == this.shape) {
                        // if the position of that block in the tetris matrix collides with other blocks on the tetris matrix
                        noCollisionBlock += 1;
                        if (tetrisMatrix[(int)this.r + r][(int)this.c +c] != ' ') {
                            // adjust the position based on where it is moving (left or right)
                            this.c += (colVel * -1 > 0) ? 1 : -1;
                            if (colVel == 0) this.c -= 1;
                            updateBoundingRect();
                            breakOuterLoop = true;
                            break;
                        }
                    }
                    if (noCollisionBlock == 4) {
                        checklingCollision = false;
                        breakOuterLoop = true;
                        break;
                    }
                }
            }
        }
    }


    public void draw(Graphics gr) {
        Graphics2D g = (Graphics2D) gr;
        int rowPos = (int)this.r, colPos = (int)this.c;
        Image image;
        int imageIndex = 0;

        for (int i = 0; i < 7; i++) {
            if (Bag.SHAPES[i] == Character.toUpperCase(this.shape)) {
                imageIndex = i;
                break;
            }
        }

        image = Tetris.blockImages[imageIndex];
        // iterate throug every space in the matrix
        for (int r = 0; r < 4; r++) {
            for (int c = 0; c < 4; c++) {
                // if this space is a block
                if (this.matrix[r][c] == this.shape) {
                    g.drawImage(image,
                    Tetris.ogX + (colPos + c)*Tetris.blockSize + Tetris.blockOffSet,
                    Tetris.ogY + (rowPos + r)*Tetris.blockSize + Tetris.blockOffSet,
                    Tetris.blockSize - Tetris.blockOffSet*2,
                    Tetris.blockSize - Tetris.blockOffSet*2,
                    null
                    );
                }
            }
        }

    }

    public void draw(Graphics gr, int x, int y) {
        Graphics2D g = (Graphics2D) gr;
        Image image;
        int imageIndex = 0;

        for (int i = 0; i < 7; i++) {
            if (Bag.SHAPES[i] == Character.toUpperCase(this.shape)) {
                imageIndex = i;
                break;
            }
        }

        image = Tetris.blockImages[imageIndex];
        // iterate throug every space in the matrix
        for (int r = 0; r < 4; r++) {
            for (int c = 0; c < 4; c++) {
                // if this space is a block
                if (this.matrix[r][c] == this.shape) {
                    g.drawImage(image,
                    x + c*Tetris.blockSize + Tetris.blockOffSet,
                    y + r*Tetris.blockSize + Tetris.blockOffSet,
                    Tetris.blockSize - Tetris.blockOffSet*2,
                    Tetris.blockSize - Tetris.blockOffSet*2,
                    null
                    );
                }
            }
        }

    }



    public void update(char[][] tetrisMatrix, ShadowPiece shadowPiece, Tetris tetris) {
        if ((int)this.r < (int) shadowPiece.r) {
            this.move(0.0167*(tetris.level*0.7), 0, tetrisMatrix);
            if (this.flyDown) {
                this.move(0.4, 0, tetrisMatrix);
                if ((int)this.prevR + 1 == (int) this.r) {
                    tetris.score += 1 * tetris.level;
                    new Sound().play("../data/move.wav");
                    this.prevR = (int)this.r;
                }
            }
        }
        
        if (this.swayLeft) {
            if (swayTick == 0) {
                
                this.move(0, -1, tetrisMatrix);
                this.prevC = (int)this.c;
                new Sound().play("../data/move.wav");
            }
            // I tested this out and on average, it takes about 6 - 9 ticks for someone to push a key then release it quickly
            // count how many ticks the user is holding the sway button
            // if its longer than the average time for a quick press,
            // then that means the user wants to keep it moving
            // So we'll keep it moving. Else, we'll just move it 1 unit
            if (swayTick >= 9) {
                this.move(0, -0.25, tetrisMatrix);
                if ((int)this.prevC - 1 == (int)this.c) {
                    new Sound().play("../data/move.wav");
                    this.prevC = (int)this.c;
                }
            }

            swayTick += 1;
        }

        if (this.swayRight) {
            if (swayTick == 0) {
                this.move(0, 1, tetrisMatrix);
                this.prevC = (int)this.c;
                new Sound().play("../data/move.wav");
            }
            if (swayTick >= 9) {
                this.move(0, 0.25, tetrisMatrix);  
                if ((int)this.prevC + 1 == (int)this.c) {
                    new Sound().play("../data/move.wav");
                    this.prevC = (int)this.c;
                }
            }

            swayTick += 1;
        }
        
        if (!swayLeft && !swayRight) {
            // if (swayTick > 0) System.out.println(swayTick);
            swayTick = 0;
        }
    }



    public void updateBoundingRect() {
        int rowPos = (int) this.r;
        int colPos = (int) this.c;
        int topMost = 3, botMost = 0, leftMost = 3, rightMost = 0;

        // iterate through every block on this piece's matrix
        for (int r = 0; r < 4; r ++) {
            for (int c = 0; c < 4; c++) {
                if (this.matrix[r][c] == this.shape) {
                    // find the outer block positions from the matrix
                    if (r < topMost) topMost= r;
                    if (r > botMost) botMost = r;
                    if (c < leftMost) leftMost = c;
                    if (c > rightMost) rightMost = c;
                }
            }
        }

        this.top = rowPos + topMost;
        this.bot = rowPos + botMost;
        this.left = colPos + leftMost;
        this.right = colPos + rightMost;
        this.width = rightMost - leftMost + 1;
    }



    public void rotate(char[][] tetrisMatrix) {        
        int dir = 0, dist = 0;
        // int prevTop = this.top;
        int prevBot = this.bot;
        int prevLeft = this.left;
        int prevRight = this.right;
        boolean collideR = false;
        boolean revert = false;
        double prevC = this.c;
        double prevR = this.r;
        // boolean canAdjustY = true;
        
        // rotate it first
        rotation += 1;
        if (rotation >= maxRotation) {
            rotation = 0;
        }
        updateMatrix(rotation);
        updateBoundingRect();

        // check x axis collision 
        boolean breakLoop;
        for (int r = 0; r < 4; r++) {
            breakLoop = false;
            for (int c = 0; c < 4; c++) {
                if (this.matrix[r][c] == this.shape) {
                    dir = 0;
                    dist = 0;
                    // each block's r&c pos relative to the tetris matrix
                    int mR = r + (int)this.r;
                    int mC = c + (int)this.c;
                    // check collision
                    // if this block's pos has a block in the matrix.. collision happens
                    if (mR > 19 || mC < 0 || mC > 9 || tetrisMatrix[mR][mC] != ' ') {
                        // check where it collided
                        // if collision happens at the right of the rect of the previous shape
                        if (mC > prevRight) {
                            // we need to adjust it to go to the left so nega direction
                            dir = -1;
                            // get the adjustment distance
                            dist = this.right - prevRight;
                            breakLoop = true;
                            break;
                        }
                        // if collision happens at the left of the rect of the previous shape
                        if (mC < prevLeft) {
                            // we need to adjust it to go to the left so positive direction
                            dir = 1;
                            // get the adjustment distance
                            dist = prevLeft - this.left;
                            breakLoop = true;
                            break;
                        }
                        collideR = true;
                    }
                }
            }
            if (breakLoop) break;
        }
        if (dir != 0) {
            System.out.println(dir);
            System.out.println(dist);
        }
        // adjust
        this.c += dist*dir;
        // test
        if (collideR) this.r -= (this.bot - prevBot);
        // test
        updateBoundingRect();

        // check for collision again and if there is collision dont rotate
        for (int r = 0; r < 4; r++) {
            breakLoop = false;
            for (int c = 0; c < 4; c++) {
                if (this.matrix[r][c] == this.shape) {
                    dir = 0;
                    dist = 0;
                    // each block's r&c pos relative to the tetris matrix
                    int mR = r + (int)this.r;
                    int mC = c + (int)this.c;
                    // if there is collision
                    if (mR > 19 || mC < 0 || mC > 9 || tetrisMatrix[mR][mC] != ' ') {
                        revert = true;
                        breakLoop = true;
                        break;
                    }
                }
            }
            if (breakLoop) break;
        }

        if (revert) {
            // adjust
            this.c = prevC;
            // test
            this.r = prevR;
            // test
            updateBoundingRect();
            rotateLeft();
            new Sound().play("../data/rotate error.wav");
        }
        else {
            if (this.shape != 'O') {
                new Sound().play("../data/rotate.wav");
            }
            else {
                new Sound().play("../data/rotate error.wav");
            }
        }
    }

    
    public void rotateLeft() {
        rotation -= 1;
        if (rotation < 0) {
            this.rotation = this.maxRotation - 1;
        }
        updateMatrix(rotation);
        updateBoundingRect();
    }


    private void updateMatrix(int rotation) {
        // forloop to iterate through every item/space/location on the matrix
        for (int r = 0; r < 4; r++) {
            for (int c = 0; c < 4; c++) {
                this.matrix[r][c] = matrices[rotation][r][c];
            }
        }
        
        updateBoundingRect();
    }



    private void getMatrices() {
        File f;
        Scanner s;
        String line;
        boolean foundShape = false;
        int var = -1; // current variation
        int row = 0;

        // read the layout file
        try {
            f = new File("data/layout.txt");
            s = new Scanner(f);

            while (s.hasNextLine()) {
                line = s.nextLine();
                // System.out.println(line);

                // look for the shape variation indicator (#)
                if (line.toCharArray()[0] == '#') {
                    foundShape = false;
                    // if that is the shape that we're looking for 
                    if (line.toCharArray()[1] == Character.toUpperCase(this.shape)) {
                        var += 1;
                        row = 0;
                        foundShape = true;
                    }
                }

                if (foundShape) {
                    // if the first character is a space, meaning we're looking at the actual layout with the asterisks (**)
                    if (line.toCharArray()[0] == ' '){
                        // forloop to iterate through every item in the variation matrix
                        
                        for (int col = 0; col < 4; col++) {
                            try {
                                // at the layout, the first * starts at i = 8 and skips a space. Hence, 8 + col*2 is how we access it
                                if (line.toCharArray()[8 + col*2] == '*') {
                                    matrices[var][row][col] = this.shape;
                                }
                                else {
                                    matrices[var][row][col] = ' ';
                                }
                            }
                                // sometimes there's no space on the layout. This is just a fail-safe
                            catch (ArrayIndexOutOfBoundsException e) {
                                matrices[var][row][col] = ' ';
                            }
                            // matrices[var][row][col] = this.shape;
                        }
                    
                        row++;
                    }
                }

            }    
            s.close();
        }
        catch (FileNotFoundException e) {
            System.out.println(e);            
        }
    }

    public int getMaxRotation() {
        int out;
        
        switch (shape) {
            case 'O':
                out = 1;
                break;
            case 'I':
            case 'S':
            case 'Z':
                out = 2;
                break;
            default:
                out = 4;
                break;
        }

        return out;
    }
}
