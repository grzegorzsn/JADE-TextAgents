package inmemory.agents;
import inmemory.DataContainer;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.Scanner;
import java.awt.Color;
import javax.swing.JTextPane;
import javax.swing.text.BadLocationException;
import javax.swing.text.Style;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;

/**
 * Created by Beli on 1/25/2017.
 */
public class OutputGUI extends DataContainer {
    private static JFrame frame;
    //private JButton Zamknij;
    private JPanel mainPanel;
    private JTextPane textPanel;

    public OutputGUI() {
        /*Zamknij.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                frame.dispose();
            }
        });*/
        textPanel.addComponentListener(new ComponentAdapter() {
            @Override
            public void componentShown(ComponentEvent e) {
                super.componentShown(e);
            }
        });
    }
    public void colorTextData(String Data, JTextPane panel)
    {
        int counter = 0;
        int counter2 = 0;
        Scanner scanner = new Scanner(Data);
        StyledDocument doc = panel.getStyledDocument();
        Style style = panel.addStyle("I'm a Style", null);
        String wordToChange;
        try {
            doc.insertString(0, Data, style);
            StyleConstants.setForeground(style, Color.red);
            System.out.println(doc.getLength());
            System.out.println("GUI - How many words found: "+wordIndexStart.size());
            for(int i=0; i<wordIndexStart.size(); i++) {
                System.out.println(wordIndexStart.get(i));
                wordToChange = doc.getText(wordIndexStart.get(i), wordIndexStop.get(i));
                System.out.println("Word to change: " + wordToChange + " IT IS FROM: "+wordIndexStart.get(i));
                doc.remove(wordIndexStart.get(i), wordIndexStop.get(i));
                doc.insertString(wordIndexStart.get(i), wordToChange, style);
            }
        } catch (BadLocationException e) {
            e.printStackTrace();
        }
        /*while (scanner.hasNextLine()) {
            String line = scanner.nextLine();

            StyleConstants.setForeground(style, Color.black);
            try {
                doc.insertString(doc.getLength(), line+"\n", style);
            } catch (BadLocationException e) {
                e.printStackTrace();
            }

            if(foundLines.contains(counter)) {
                StyleConstants.setForeground(style, Color.red);
                try {
                    //GDY CIAGLE TA SAMA LINIA np. "ASD" w "qqqASDqqqASDqqqASD" 3 wyniki w tej samej linii

                    while(counter2 < foundLines.size() && counter == foundLines.get(counter2)) {
                        System.out.println("START: "+wordIndexStart.get(counter2)+"END: "+wordIndexStop.get(counter2));
                        *//*wordToChange = line.substring(wordIndexStart.get(counter2), wordIndexStop.get(counter2));
                        doc.remove(wordIndexStart.get(counter2), wordIndexStart.get(counter2)+wordIndexStop.get(counter2));
                        doc.insertString(wordIndexStart.get(counter2), wordToChange, style);*//*
                        *//*StyleConstants.setForeground(style, Color.black);
                        //System.out.println("STARTINDEX: " + wordIndexStart.get(counter2) + "ENDINDEX: " + wordIndexStop.get(counter2));
                        if(wordIndexStart.get(counter2) > 0 && wordIndexStop.get(counter2-1) != wordIndexStart.get(counter2)) {
                            doc.insertString(doc.getLength(), line.substring(0, wordIndexStart.get(counter2)), style);
                        }

                        StyleConstants.setForeground(style, Color.red);
                        doc.insertString(doc.getLength(), line.substring(wordIndexStart.get(counter2), wordIndexStop.get(counter2)), style);

                        StyleConstants.setForeground(style, Color.black);
                        //JEZELI NIE MA JUZ TEGO SAMEGO SLOWA W LINII
                        if(counter2+1 < foundLines.size())
                            if(counter != foundLines.get(counter2+1)) {
                                doc.insertString(doc.getLength(), line.substring(wordIndexStop.get(counter2), line.length()), style);
                            }else{
                                doc.insertString(doc.getLength(), line.substring(wordIndexStop.get(counter2), wordIndexStart.get(counter2+1)), style);
                            }*//*

                        counter2++;
                    }

                }
                catch (BadLocationException ex) {
                }
            }else {
                *//*StyleConstants.setForeground(style, Color.black);
                try {
                    doc.insertString(doc.getLength(), line + "\n", style);
                }
                catch (BadLocationException ex) {
                }*//*
            }

            counter++;
        }
        scanner.close();
        */
    }

    public void showOutput() {
        //ZEBY NIE TWORZYC W KOLKO JFrame TYLKO ODSWIEZAC JEGO ZAWARTOSC !! DISPOSE_ON_CLOSE Dziala w tle
        /*if(frame != null) {
            //colorTextData(TextToParse, textPanel);
            //frame.pack();
            //frame.setVisible(true);

            return;
        }*/

        System.out.println("JFrame Creating.....");

        frame = new JFrame("Found text");
        frame.setContentPane(mainPanel);
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        int centerX = (int) screenSize.getWidth() / 2;
        int centerY = (int) screenSize.getHeight() / 2;

        frame.setLocation(centerX - frame.getWidth() / 2, centerY - frame.getHeight() / 2);
        frame.setResizable(true);
        colorTextData(TextToParse, textPanel);
        //textPanel.setText(TextToParse);
        textPanel.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(textPanel);
        //scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setBounds(0, 0, 600, 800);
        JPanel contentPane = new JPanel(null);
        contentPane.setPreferredSize(new Dimension(600, 800));
        contentPane.add(scrollPane);
        frame.setContentPane(contentPane);
        frame.pack();
        frame.setVisible(true);

    }


    private void createUIComponents() {
        // TODO: place custom component creation code here
    }
}
