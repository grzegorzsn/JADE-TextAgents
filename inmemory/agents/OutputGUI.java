package inmemory.agents;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

/**
 * Created by Beli on 1/25/2017.
 */
public class OutputGUI {
    private static JFrame frame;
    private JButton Zamknij;
    private JPanel mainPanel;
    private JTextArea textArea;

    public OutputGUI() {
        Zamknij.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.exit(0);
            }
        });
        textArea.addComponentListener(new ComponentAdapter() {
            @Override
            public void componentShown(ComponentEvent e) {
                super.componentShown(e);
            }
        });
    }//raz dwa trzy

    public void showOutput() {
        frame = new JFrame("Found text");
        frame.setContentPane(mainPanel);
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.pack();
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        int centerX = (int) screenSize.getWidth() / 2;
        int centerY = (int) screenSize.getHeight() / 2;
        frame.setLocation(centerX - frame.getWidth() / 2, centerY - frame.getHeight() / 2);
        frame.setVisible(true);
        frame.setResizable(false);
        textArea.append("RAZ DWA TRZY");
        textArea.setEnabled(false);
    }

    private void createUIComponents() {
        // TODO: place custom component creation code here
    }
}