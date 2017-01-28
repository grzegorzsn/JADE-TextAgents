package inmemory.textProcessing;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

/**
 * Created by Grzegorz on 2017-01-23.
 */
public class TextJobProcessor {



    public static ArrayList<TextJobPart> loadParts(String filePath, String[] input) {
        //CO ILE LINII DZIELIC TEKST | liczba linii / liczba agentow (zaokragl w gore)
        float numberOfLinesInFragment = (float) 10.0;
        //ile lini w pliku (potrzebne do stworzenia arraya fragmentow o odpowiedniej wielkosci)
        long linesNumber = 0;
        //linie tekstu
        String[] lines = null;

        int linesCounter = 0;
        //do ktorego indeksu arraya ma zapisywac
        int currentFragmentSave = 0;
        //parts tekstu
        TextJobPart[] parts = null;

        //CZYTANIE PLIKU
        StringBuilder everything = new StringBuilder();
        BufferedReader br = null;
        try {
            br = new BufferedReader(new FileReader(filePath));
            String line;

            while ((line = br.readLine()) != null) {
                everything.append(line + "\n");
                linesNumber++;
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
            parts[i].setInput(input);
        }

        int offset = 0;
        for (int i = 0; i < lines.length; i++) {
            if (linesCounter >= numberOfLinesInFragment) {
                parts[currentFragmentSave].setOffset(offset);
                currentFragmentSave++;
                linesCounter = 0;
            }
            System.out.println(offset);
            offset += lines[i].length()+1;
            parts[currentFragmentSave].addLine(lines[i] + "\n");
            linesCounter++;
        }
        ArrayList<TextJobPart> partsArray = new ArrayList<TextJobPart>();
        for( TextJobPart  part : parts)
            partsArray.add(part);

        return partsArray;
    }

    public static TextJobPart processAho(TextJobPart part)
    {
        Aho sm = new Aho(false);
        sm.createTrie(part.getInput());
        sm.getFailure();
        sm.search(part, false);
        return part;
    }

    public static TextJobPart processFind(TextJobPart part)
    {
        // TODO create processing with find instead of Aho-Corasick
        return part;
    }

}
