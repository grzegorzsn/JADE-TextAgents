package inmemory;
import java.util.ArrayList;
import java.util.List;

public class DataContainer {
    public static String[] searchedWords;
    public static String getTextFromURL;
    public static String TextToParse;
    public static ArrayList<Integer> foundLines;
<<<<<<< HEAD
    public static ArrayList<Integer> wordIndexStart;
    public static ArrayList<Integer> wordIndexStop;
=======

    public static void PrintDataToConsole() {
        for(int p : foundLines) {
            System.out.print("Line" + foundLines.indexOf(p) + "equals:" + p);
        }
        System.out.print(TextToParse);
    }
>>>>>>> fdc5f005355e330416d702f074c3866e9822da35
}
