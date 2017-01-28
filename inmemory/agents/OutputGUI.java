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
    private JPanel mainPanel;
    private JTextPane textPanel;

    public OutputGUI() {
        textPanel.addComponentListener(new ComponentAdapter() {
            @Override
            public void componentShown(ComponentEvent e) {
                super.componentShown(e);
            }
        });
    }
    public void colorTextData(String Data, JTextPane panel)
    {
        StyledDocument doc = panel.getStyledDocument();
        Style style = panel.addStyle("I'm a Style", null);
        String wordToChange;
        try {
            doc.insertString(0, Data, style);
            StyleConstants.setForeground(style, Color.red);
            System.out.println(doc.getLength());
            System.out.println("GUI - How many words found: "+wordIndexStart.size());
            for(int i=0; i<wordIndexStart.size(); i++) {
                wordToChange = doc.getText(wordIndexStart.get(i), wordIndexStop.get(i));
                System.out.println("Word to change: " + wordToChange + " IT IS FROM: "+wordIndexStart.get(i));
                doc.remove(wordIndexStart.get(i), wordIndexStop.get(i));
                doc.insertString(wordIndexStart.get(i), wordToChange, style);
            }
        } catch (BadLocationException e) {
            e.printStackTrace();
        }
    }

    public void showOutput() {
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
        textPanel.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(textPanel);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setBounds(0, 0, 800, 600);
        JPanel contentPane = new JPanel(null);
        contentPane.setPreferredSize(new Dimension(800, 600));
        contentPane.add(scrollPane);
        frame.setContentPane(contentPane);
        frame.pack();
        frame.setResizable(false);
        frame.setVisible(true);

    }


    private void createUIComponents() {
        // TODO: place custom component creation code here
    }
}
