package Utilities;
public class Utils {
    

    public static int[] append(int[] array, int item) {
        int[] out = new int[array.length + 1];

        for (int i = 0; i < array.length; i++) {
            out[i] = array[i];
        }

        out[array.length] = item;

        return out;
    }

    public static int[][] append(int[][] array2D, int[] array) {
        int[][] out = new int[array2D.length + 1][array.length];

        for (int i = 0; i < array2D.length + 1; i++) {
            for (int j = 0; j < array.length; j++) {
                if (i == array2D.length) {
                    out[i][j] = array[j];
                }
                else {
                    out[i][j] = array2D[i][j];
                }
            }
        }

        return out;
    }
}
