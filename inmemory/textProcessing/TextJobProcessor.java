package inmemory.textProcessing;

import inmemory.DataContainer;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

import static java.lang.System.nanoTime;
import static java.lang.System.out;

/**
 * Created by Grzegorz on 2017-01-23.
 */
public class TextJobProcessor {

    private long timestamp = 0;
    private double timestampRoznica = 0;
    private Aho sm;
    private String[] searchedWords;
    public TextJobProcessor(String[] searchedWords) {
        this.searchedWords = searchedWords;
        sm = new Aho(false);
        sm.createTrie(searchedWords);
        sm.getFailure();
    }

    public static ArrayList<TextJobPart> loadParts(String filePath, boolean aho) {

        float numberOfLinesInFragment = (float) 1000;
        long linesNumber = 0;
        String[] lines = null;

        int linesCounter = 0;
        int currentFragmentSave = 0;
        TextJobPart[] parts = null;

        StringBuilder everything = new StringBuilder();
        BufferedReader br = null;
        String line;
        try {
            if(new DataContainer().fileFlag){
            br = new BufferedReader(new FileReader(filePath));
                while ((line = br.readLine()) != null) {
                    everything.append(line + "\n");
                    linesNumber++;
                }
            }
            else if(new DataContainer().urlFlag)
            {
                everything = new StringBuilder(filePath);
                linesNumber = new DataContainer().urlLinesNumber;
            }

            lines = everything.toString().split("\\n");
            parts = new TextJobPart[(int) Math.ceil(linesNumber / numberOfLinesInFragment)];

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (br != null) {
                    br.close();
                }
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
        for (int i = 0; i < parts.length; i++) {
            parts[i] = new TextJobPart();
            parts[i].setNumber(i);
            parts[i].setAho(aho);
        }

        int offset = 0;
        for (int i = 0; i < lines.length; i++) {
            if (linesCounter >= numberOfLinesInFragment) {
                currentFragmentSave++;
                parts[currentFragmentSave].setOffset(offset);
                linesCounter = 0;
            }
            offset += lines[i].length()+1;
            parts[currentFragmentSave].addLine(lines[i] + "\n");
            linesCounter++;
        }
        ArrayList<TextJobPart> partsArray = new ArrayList<TextJobPart>();
        for( TextJobPart  part : parts)
            partsArray.add(part);

        return partsArray;
    }

    public TextJobPart processAho(TextJobPart part)
    {
        timestamp = nanoTime();
        sm.search(part);
        part.setProcessingtTime(nanoTime() - timestamp);
        return part;
    }

    public TextJobPart processFind(TextJobPart part)
    {
        timestamp = nanoTime();
        String allText = part.getLines().toString();
        part.wordLength = new ArrayList<>();
        part.wordStart = new ArrayList<>();
        for (String searchedWord : searchedWords) {
            for (int i = -1; (i = allText.indexOf(searchedWord, i + 1)) != -1; ) {
                part.wordStart.add(i + part.getOffset());
                part.wordLength.add(searchedWord.length());
            }
        }
        part.setProcessingtTime(nanoTime() - timestamp);
        return part;
    }

}
