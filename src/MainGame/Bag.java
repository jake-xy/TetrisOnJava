package MainGame;
import java.util.Random;

public class Bag {
    final static char[] SHAPES = {'I', 'O', 'T', 'S', 'Z', 'J', 'L'};
    char[] contents = new char[0];
    Random random = new Random();
    char currentShape, holdShape = ' ';
    boolean canHold = true;

    public Bag() {
        addContents();
    }



    public Piece hold(Piece currentPiece) {
        if (this.canHold) {
            if (holdShape == ' ') {
                holdShape = currentShape;
                contents = pop(contents, 0);
                currentShape = contents[0];
            }
            else {
                contents[0] = holdShape;
                holdShape = currentShape;
                currentShape = contents[0];
            }
            this.canHold = false;
            return new Piece(currentShape, currentPiece.swayLeft, currentPiece.swayRight);
        }

        return currentPiece;
    }



    public Piece getNextPiece(Piece currentPiece) {
        this.contents = pop(contents, 0);
        this.currentShape = contents[0];
        this.canHold = true;

        if (this.contents.length <= 4) {
            this.addContents();
        }

        return new Piece(this.currentShape, currentPiece.swayLeft, currentPiece.swayRight);
    }



    public void addContents() {
        int randint;
        boolean inBag;
        char[] newContents = new char[0];

        while (newContents.length < 7) {

            do {
                randint = random.nextInt(0, 7);
                inBag = false;

                for (int i = 0; i < newContents.length; i++) {
                    if (newContents[i] == SHAPES[randint]) {
                        inBag = true;
                    }
                }

            } while (inBag);
            
            newContents = append(newContents, SHAPES[randint]);
        }

        // add the new contents to the main contents
        for (int i = 0; i < newContents.length; i ++) {
            contents = append(contents, newContents[i]);
        }

        currentShape = contents[0];
    }



    public char[] append(char[] array, char item) {
        char[] out = new char[array.length + 1];

        for (int i = 0; i < array.length; i ++) {
            out[i] = array[i];
        }

        out[array.length] = item;

        return out;
    }



    public char[] pop(char[] array) {
        char[] out = new char[array.length - 1];
        int index = 0;

        for (int i = 0; i < index; i++) {
            out[i] = array[i];
        }

        for (int i = index; i < array.length - 1; i ++) {
            out[i] = array[i+1];
        }

        return out;
    }

    

    public char[] pop(char[] array, int index) {
        char[] out = new char[array.length - 1];

        for (int i = 0; i < index; i++) {
            out[i] = array[i];
        }

        for (int i = index; i < array.length - 1; i ++) {
            out[i] = array[i+1];
        }

        return out;
    }
}
