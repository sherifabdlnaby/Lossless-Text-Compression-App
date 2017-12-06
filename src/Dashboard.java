import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;


public class Dashboard extends JPanel {
    private JPanel Panel;
    private JTextField filePathField;
    private JButton browseButton;
    private JTextArea NamesField;
    private JCheckBox LZ77Select;
    private JCheckBox LZWSelect;
    private JCheckBox HuffmanSelect;
    private JCheckBox ArithmeticSelect;
    private JButton compressButton;
    private JProgressBar progressBar1;
    private JButton decompressButton;
    private JTextField decompressType;
    private File File;
    public Dashboard() {
        browseButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JFileChooser browser = new JFileChooser();
                browser.showOpenDialog(null);
                File = browser.getSelectedFile();
                filePathField.setText(File.getAbsolutePath());
                String extension = File.getName();
                extension = extension.substring(extension.lastIndexOf('.')+1);
                compressButton.setEnabled(false);
                decompressButton.setEnabled(true);
                LZ77Select.setEnabled(false);
                LZWSelect.setEnabled(false);
                HuffmanSelect.setEnabled(false);
                ArithmeticSelect.setEnabled(false);
                switch (extension) {
                    case "txt":
                        compressButton.setEnabled(true);
                        decompressButton.setEnabled(false);
                        decompressType.setText("");
                        LZ77Select.setEnabled(true);
                        LZWSelect.setEnabled(true);
                        HuffmanSelect.setEnabled(true);
                        ArithmeticSelect.setEnabled(true);
                        break;
                    case "lz77":
                        decompressType.setText("LZ77Select - Lempel Ziv 1977");
                        break;
                    case "lzw":
                        decompressType.setText("LZ77Select - Lempel Ziv Welch");
                        break;
                    case "huffman":
                        decompressType.setText("HuffmanSelect Encoding");
                        break;
                    case "art":
                        decompressType.setText("ArithmeticSelect Coding");
                        break;
                    default:
                        compressButton.setEnabled(false);
                        decompressButton.setEnabled(false);
                        JOptionPane.showMessageDialog(null,"Invalid Extension. \n Only *.txt, *.lz77, *.lzw, *.huffman, *.art are supported");
                }
            }
        });

        compressButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                //Kinda a Hack-y code style, I'd rather not do that If I have time.
                compressButton.setEnabled(false);
                Thread procces = new Thread(() -> {
                    //Calculate total Selected
                    int i = 0;
                    if (LZ77Select.isSelected()) ++i;
                    if (LZWSelect.isSelected()) ++i;
                    if (HuffmanSelect.isSelected()) ++i;
                    if (ArithmeticSelect.isSelected()) ++i;
                    int incrmentValue = (int) ((1 / (double) i) * 100);
                    String filePath = File.getParent();
                    String fileName = File.getName();
                    String inputString = "";
                    try {
                        inputString = new String(Files.readAllBytes(Paths.get(File.getAbsolutePath())));
                    } catch (IOException e1) {
                        JOptionPane.showMessageDialog(null, "Can't Open File");
                        e1.printStackTrace();
                        return;
                    }
                    progressBar1.setValue(0);

                    if (LZ77Select.isSelected()) {
                        String finalInputString = inputString;
                        LZ77.Compress(finalInputString, fileName, filePath);
                        progressBar1.setValue(progressBar1.getValue() + incrmentValue);
                        progressBar1.repaint();
                    }

                    if (LZWSelect.isSelected()) {
                        String finalInputString = inputString;
                        LZW.Compress(finalInputString, fileName, filePath);
                        progressBar1.setValue(progressBar1.getValue() + incrmentValue);
                        progressBar1.repaint();
                    }

                    if (HuffmanSelect.isSelected()) {
                        String finalInputString = inputString;
                        Huffman.Compress(finalInputString, fileName, filePath);
                        progressBar1.setValue(progressBar1.getValue() + incrmentValue);
                        progressBar1.repaint();
                    }

                    if (ArithmeticSelect.isSelected()) {
                        String finalInputString = inputString;
                        Arithmetic.Compress(finalInputString, fileName, filePath);
                        progressBar1.setValue(progressBar1.getValue() + incrmentValue);
                        progressBar1.repaint();
                    }
                    compressButton.setEnabled(true);
                });
                procces.start();
            }
        });
    }

    public static void main(String[] args) {
        JFrame jFrame = new JFrame("Restaurant Simulation");
        jFrame.setContentPane(new Dashboard().Panel);
        jFrame.setDefaultCloseOperation(jFrame.EXIT_ON_CLOSE);
        jFrame.pack();
        jFrame.setVisible(true);
    }
}

