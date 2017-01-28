package inmemory.agents;
import inmemory.DataContainer;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.*;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import javax.swing.JFileChooser;
import javax.swing.filechooser.FileFilter;
import javax.swing.filechooser.FileNameExtensionFilter;

/**
 * Created by Grzegorz&Krzysztof on 2017-01-23.
 */
public class MachineMasterGUI extends DataContainer{
    private JTextField searcherdWordsSplittedTextField;
    private JButton Next;
    private JPanel machineMasterGUIPanel;
    private JTextField urlTXT;
    private JRadioButton URLradiobutton;
    private JRadioButton path;
    private JButton loadFileButton;
    private JRadioButton ACradiobutton;
    private JRadioButton inMemoryRadio;
    private JLabel AhoCorasickLabel;
    private JLabel InMemoryLabel;
    private JLabel selectedFile;
    private JButton button1;
    private MachineMaster myAgent;
    private static JFrame frame;
    private File file;
    MachineMasterGUI(MachineMaster a)
    {
        myAgent = a;
        loadFileButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JFileChooser fileopen = new JFileChooser();
                FileFilter filter = new FileNameExtensionFilter("txt files", "txt");
                fileopen.addChoosableFileFilter(filter);
                int ret = fileopen.showDialog(null, "Open file");
                if (ret == JFileChooser.APPROVE_OPTION) {
                    file = fileopen.getSelectedFile();
                    String fileName = file.toString().substring(file.toString().lastIndexOf("\\"), file.toString().length());
                    selectedFile.setText(fileName.substring(1,fileName.length()));
                    //System.out.println(file);//zwraca plik ktory wybralismy
            }}
        });
        path.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if(path.isSelected())
                {
                    loadFileButton.setEnabled(true);
                    urlTXT.setEnabled(false);
                    URLradiobutton.setSelected(false);
                }
                else
                {
                    loadFileButton.setEnabled(false);
                }
            }
        });
        URLradiobutton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if(URLradiobutton.isSelected())
                {
                    loadFileButton.setEnabled(false);
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

        searcherdWordsSplittedTextField.addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
                super.focusGained(e);
                if (searcherdWordsSplittedTextField.getText().equals("szukane słowa rozdziel \" ; \""))
                {
                    searcherdWordsSplittedTextField.setText("");
                }
            }

            @Override
            public void focusLost(FocusEvent e) {
                super.focusLost(e);
                if (searcherdWordsSplittedTextField.getText().equals(""))
                {
                    searcherdWordsSplittedTextField.setText("szukane słowa rozdziel \" ; \"");
                }
            }
        });

        Next.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                DataContainer.wipeOut();
                searchedWords = searcherdWordsSplittedTextField.getText().split(";");
                getTextFromURL = urlTXT.getText();
                myAgent.setInmmemory(inMemoryRadio.isSelected());
                if(URLradiobutton.isSelected())
                {
                    urlFlag = true;
                    URL url;
                    try {
                        String urlString=urlTXT.getText();
                        url = new URL(urlString);
                        URLConnection conn = url.openConnection();
                        BufferedReader br = new BufferedReader(
                                new InputStreamReader(conn.getInputStream()));
                        StringBuilder fullText = new StringBuilder();
                        String inputLine;
                        while ((inputLine = br.readLine()) != null) {
                            fullText.append(inputLine);
                            fullText.append("\n");
                            urlLinesNumber++;
                        }
                        br.close();
                        myAgent.manageJob(fullText.toString(), searchedWords);
                    } catch (MalformedURLException re) {
                        re.printStackTrace();
                    } catch (IOException re) {
                        re.printStackTrace();
                    }
                }

                else if (path.isSelected()) {
                    fileFlag=true;
                    String path = file.getAbsolutePath();
                    myAgent.manageJob(path, searchedWords);
                }
                else
                {
                    System.out.println("MASTER: Nie wybrano żadnej akcji!");
                }
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
