package inmemory.agents;
import inmemory.DataContainer;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.*;
import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import javax.swing.JFileChooser;
import javax.swing.filechooser.FileFilter;
import javax.swing.filechooser.FileNameExtensionFilter;

/**
 * Created by Grzegorz&Krzysztof on 2017-01-23.
 */
public class MachineMasterGUI extends DataContainer{
    private JTextField szukaneSłowaRozdzielTextField;
    private JButton Dalej;
    private JPanel machineMasterGUIPanel;
    private JTextField urlTXT;
    private JRadioButton URLradiobutton;
    private JRadioButton path;
    private JProgressBar progressBar1;
    private JButton wczytajPlikButton;
    private JButton button1;
    private MachineMaster myAgent;
    private static JFrame frame;
    private File file;
    MachineMasterGUI(MachineMaster a)
    {
        myAgent = a;
        wczytajPlikButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JFileChooser fileopen = new JFileChooser();
                FileFilter filter = new FileNameExtensionFilter("txt files", "txt");
                fileopen.addChoosableFileFilter(filter);
                int ret = fileopen.showDialog(null, "Open file");
                if (ret == JFileChooser.APPROVE_OPTION) {
                    file = fileopen.getSelectedFile();
                    //System.out.println(file);//zwraca plik ktory wybralismy
            }}
        });
        path.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if(path.isSelected())
                {
                    wczytajPlikButton.setEnabled(true);
                    urlTXT.setEnabled(false);
                    URLradiobutton.setSelected(false);
                }
                else
                {
                    wczytajPlikButton.setEnabled(false);
                }
            }
        });
        URLradiobutton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if(URLradiobutton.isSelected())
                {
                    wczytajPlikButton.setEnabled(false);
                    urlTXT.setEnabled(true);
                    path.setSelected(false);
                }
                else
                {
                    urlTXT.setEnabled(false);
                }
            }
        });
        urlTXT.addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
                super.focusGained(e);
                if (urlTXT.getText().equals("wpisz adress url..."))
                {
                    urlTXT.setText("");
                }
            }

            @Override
            public void focusLost(FocusEvent e) {
                super.focusLost(e);
                if (urlTXT.getText().equals(""))
                {
                    urlTXT.setText("wpisz adress url...");
                }
            }
        });

        szukaneSłowaRozdzielTextField.addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
                super.focusGained(e);
                if (szukaneSłowaRozdzielTextField.getText().equals("szukane słowa rozdziel \" ; \""))
                {
                    szukaneSłowaRozdzielTextField.setText("");
                }
            }

            @Override
            public void focusLost(FocusEvent e) {
                super.focusLost(e);
                if (szukaneSłowaRozdzielTextField.getText().equals(""))
                {
                    szukaneSłowaRozdzielTextField.setText("szukane słowa rozdziel \" ; \"");
                }

            }
        });
        Dalej.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                fileOutput = urlTXT.getText();
                String path = file.getAbsolutePath();
                requestedData = szukaneSłowaRozdzielTextField.getText().split(";");
                a.manageJob(path, requestedData);
            }
        });

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
