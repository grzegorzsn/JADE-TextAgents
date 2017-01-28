package inmemory;
import java.util.ArrayList;
import java.util.List;

public class DataContainer {
    public static String[] searchedWords;
    public static String getTextFromURL;
    public static String TextToParse;
    public static ArrayList<Integer> foundLines;
    public static ArrayList<Integer> wordIndexStart;
    public static ArrayList<Integer> wordIndexStop;
    public static boolean fileFlag;
    public static boolean urlFlag;
    public static int urlLinesNumber;

    public static void PrintDataToConsole() {
        for(int p : foundLines) {
            System.out.print("Line" + foundLines.indexOf(p) + "equals:" + p);
        }
        System.out.print(TextToParse);
    }
}
