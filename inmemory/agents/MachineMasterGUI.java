package inmemory.agents;

import javax.swing.*;
import java.awt.*;

/**
 * Created by Grzegorz on 2017-01-23.
 */
public class MachineMasterGUI{
    private JTextField textField1;
    private JTextField textField2;
    private JButton button1;
    private JPanel machineMasterGUIPanel;
    private MachineMaster myAgent;
    private static JFrame frame;

    MachineMasterGUI(MachineMaster a)
    {
        myAgent = a;
    }

    public void showGui() {
        frame = new JFrame("In-memory Agent Platform");
        frame.setContentPane(this.machineMasterGUIPanel);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        int centerX = (int)screenSize.getWidth() / 2;
        int centerY = (int)screenSize.getHeight() / 2;
        frame.setLocation(centerX - frame.getWidth() / 2, centerY - frame.getHeight() / 2);
        frame.setVisible(true);
        frame.setResizable(false);
    }

    public void close()
    {
        frame.dispose();
    }
}
