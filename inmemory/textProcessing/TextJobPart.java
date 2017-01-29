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


public class TextJobPart implements Comparable, Serializable {
    private boolean aho;
    private int numberInQueue = 0; //numer fragmentu
    private int offset = 0; //numer startowej linii
    StringBuilder lines; //tekst
    ArrayList<Integer> results = new ArrayList<Integer>();
    //private String[] input;
    private int id = 0;



    public ArrayList<Integer> wordStart = new ArrayList<Integer>();
    public ArrayList<Integer> wordLength = new ArrayList<Integer>();

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
        ArrayList<Integer> resultsShifted = new ArrayList<>();
        for(int result : results)
            resultsShifted.add(result + offset);
        return resultsShifted;
    }
    public int getId() {
        return id;
    }
    public void setId(int id) {
        this.id = id;
    }
    public int getOffset() {
        return offset;
    }
    public int getNumberInQueue() {
        return numberInQueue;
    }

    @Override
    public int compareTo(Object o) {
        TextJobPart other = ((TextJobPart)o);
        return this.numberInQueue > other.numberInQueue ? +1 : this.numberInQueue < other.numberInQueue ? -1 : 0;
    }

    public boolean isAho() {
        return aho;
    }

    public void setAho(boolean aho) {
        this.aho = aho;
    }

}
