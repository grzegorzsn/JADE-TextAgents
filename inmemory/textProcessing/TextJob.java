package inmemory.textProcessing;
import java.io.Serializable;
import java.util.LinkedList;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

public class TextJob implements Serializable {

    public static void main(String args[]){
        Aho sm = new Aho(false);
        //String[] input = {"abcd","asdd","werew","asdasd"};
        String[] input = {"Assistant"};
        sm.createTrie(input);
        sm.getFailure();

        //CO ILE LINII DZIELIC TEKST | liczba linii / liczba agentow (zaokragl w gore)
        float fragment = (float) 50.0;
        //ile lini w pliku (potrzebne do stworzenia arraya fragmentow o odpowiedniej wielkosci)
        long linesNumber = 0;
        //linie tekstu
        String[] lines = null;

        int linesCounter = 0;
        //do ktorego indeksu arraya ma zapisywac
        int currentFragmentSave = 0;
        //fragmenty tekstu
        TextJobPart[] fragmenty = null;

        //CZYTANIE PLIKU
        StringBuilder everything = new StringBuilder();
        BufferedReader br = null;
        try {
            br = new BufferedReader(new FileReader("D:\\Studia\\PW\\SAG\\JADE-TextAgents\\inmemory\\textProcessing\\test.txt"));
            String line;

            while ((line = br.readLine()) != null) {
                everything.append(line+"\n");
                linesNumber++;
            }
            lines = everything.toString().split("\\n");
            fragmenty = new TextJobPart[(int)Math.ceil(linesNumber / fragment)];

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
        for(int i=0; i<fragmenty.length; i++) {
            fragmenty[i] = new TextJobPart();
        }

        for(int i=0; i<lines.length; i++){
            if(linesCounter > 50) {
                currentFragmentSave++;
                linesCounter=0;
            }
            fragmenty[currentFragmentSave].addLine(lines[i] + "\n");
            linesCounter++;
        }

        for(int i=0; i<fragmenty.length; i++) {
            //LinkedList<String> matches = sm.search(fragmenty[i].getLines().toString(), false);
            ArrayList<Integer> matches = sm.search(fragmenty[i].getLines().toString(), false);
            fragmenty[i].setResults(matches);
            System.out.print("Tekst:\n"+fragmenty[i].getLines()+"\nZnaleziono słowa w liniach: \n"+fragmenty[i].getResults()+"\n");
            //System.out.print(fragmenty[i].getLines().toString());
        }
    }
}
