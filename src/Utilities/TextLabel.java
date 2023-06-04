package Utilities;

import javax.swing.JLabel;

public class TextLabel extends JLabel{
    String text;
    int x, y;
    int fontSize;

    TextLabel (String text, int[] pos, int fontSize) {
        this.text = text;
        this.x = pos[0];
        this.y = pos[1];
        this.fontSize = fontSize;
    }


}
