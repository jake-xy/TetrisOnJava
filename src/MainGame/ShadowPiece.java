package MainGame;
import java.awt.*;

public class ShadowPiece {
    Piece motherPiece;
    char matrix[][] = new char[4][4];
    double r, c;
    int top, bot, left, right;
    char shape;
    char[][] tetrisMatrix;

    ShadowPiece(Piece piece, char[][] tetrisMatrix) {
        this.motherPiece = piece;
        this.r = motherPiece.r;
        this.c = motherPiece.c;
        this.shape = piece.shape;
        this.tetrisMatrix = tetrisMatrix;
        updateBoundingRect();
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

    }


    public void update() {
        // update the matrix
        for (int r = 0; r < 4; r++) {
            for (int c = 0; c < 4; c++) {
                this.matrix[r][c] = motherPiece.matrix[r][c];
            }
        }
        updateBoundingRect();

        // position
        this.c = motherPiece.c;
        updateBoundingRect();
        

        
        this.r = motherPiece.r;
        updateBoundingRect();
        // collision with other blocks
        boolean checkingCollision = true;
        while (checkingCollision) {
            this.r += 1;
            this.updateBoundingRect();
            
            // iterate through every space of the piece matrix
            boolean breakOuterLoop = false;
            for (int r = 0; r < 4; r++) {
                if (breakOuterLoop) break;
                for (int c = 0; c < 4; c++) {
                    // if that space on the matrix is a block
                    if (this.matrix[r][c] == this.shape) {
                        // if this position on the matrix has a block
                        if (bot < 20 && tetrisMatrix[r + (int)this.r][c + (int)this.c] != ' ') {
                            this.r -= 1;
                            this.updateBoundingRect();
                            checkingCollision = false;
                            breakOuterLoop = true;
                            break;
                        }
                    }
                }
            }

            if (this.bot > 19) {
                this.r -= 1;
                updateBoundingRect();
            }

            if (this.bot == 19) {
                checkingCollision = false;
                break;
            }
        }
        
        updateBoundingRect();
    }

    public void draw(Graphics gr) {
        Graphics2D g = (Graphics2D) gr;
        int rowPos = (int)this.r, colPos = (int)this.c;

        g.setColor(Color.gray);
        for (int r = 0; r < 4; r++) {
            for (int c = 0; c < 4; c ++) {
                if (this.matrix[r][c] == this.shape) {
                    g.fillRect(
                        Tetris.ogX + (colPos + c)*Tetris.blockSize + Tetris.blockOffSet,
                        Tetris.ogY + (rowPos + r)*Tetris.blockSize + Tetris.blockOffSet,
                        Tetris.blockSize - Tetris.blockOffSet*2,
                        Tetris.blockSize - Tetris.blockOffSet*2
                        );
                }
            }
        }

    }
}
