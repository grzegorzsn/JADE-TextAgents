package inmemory.textProcessing;

import java.io.Serializable;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.ArrayList;

/**
 * Created with IntelliJ IDEA.
 * User: admin
 * Date: 25.01.17
 * Time: 02:16
 * To change this template use File | Settings | File Templates.
 */
public class TextJobPart implements Serializable {

    private int numberInQueue = 0; //numer fragmentu
    private int offset = 0; //numer startowej linii
    StringBuilder lines; //tekst
    ArrayList<Integer> results = new ArrayList<Integer>();
    private String[] input;

    public TextJobPart(){
        lines = new StringBuilder();
    }
    public void setNumber(int fragmentNumber) {
         numberInQueue = fragmentNumber;
    }
    public void setText(StringBuilder text) {
        lines = text;
    }
    public void addLine(String line) {
        lines.append(line);
    }
    public void setOffset(int x) {
        offset = x;
    }
    public void setResults(ArrayList<Integer> x) {
        results = x;
    }
    public StringBuilder getLines(){
        return lines;
    }
    public ArrayList<Integer> getResults(){
        return results;
    }

    public String[] getInput() {
        return input;
    }

    public void setInput(String[] input) {
        this.input = input;
    }
    public int getOffset() {
        return offset;
    }
    public int getNumberInQueue() {
        return numberInQueue;
    }


}
