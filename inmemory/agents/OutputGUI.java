package inmemory.agents;
import inmemory.DataContainer;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

/**
 * Created by Beli on 1/25/2017.
 */
public class OutputGUI extends DataContainer {
    private static JFrame frame;
    private JButton Zamknij;
    private JPanel mainPanel;
    private JTextArea textArea;

    public OutputGUI() {
        Zamknij.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                frame.dispose();
            }
        });
        textArea.addComponentListener(new ComponentAdapter() {
            @Override
            public void componentShown(ComponentEvent e) {
                super.componentShown(e);
            }
        });
    }

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
        textArea.append(fileOutput + " oraz lista containerów" + requestedData);
        textArea.setEnabled(false);
    }

    private void createUIComponents() {
        // TODO: place custom component creation code here
    }
}