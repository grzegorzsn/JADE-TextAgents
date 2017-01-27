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
    public static String colorTextData(String Data, JTextPane panel)
    {
        int counter = 0;
        Scanner scanner = new Scanner(Data);
        while (scanner.hasNextLine()) {
            String line = scanner.nextLine();
            for (int i : foundLines) {
                StyledDocument doc = panel.getStyledDocument();
                Style style = panel.addStyle("I'm a Style", null);
                if(i == counter)
                {
                    StyleConstants.setForeground(style, Color.red);
                    try {
                        doc.insertString(doc.getLength(), line, style);
                        break;
                    } catch (BadLocationException ex) {
                    }
                }
                else {
                    StyleConstants.setForeground(style, Color.black);
                    try {
                        doc.insertString(doc.getLength(), line, style);
                        break;
                    } catch (BadLocationException ex) {
                    }
                }
            }
            counter++;
        }
        scanner.close();

        return Data;
    }

    public void showOutput() {
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