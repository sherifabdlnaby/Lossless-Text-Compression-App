import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;


public class Dashboard extends JPanel {
    private JPanel Panel;
    private JTextField filePathField;
    private JButton browseButton;
    private JTextArea NamesField;
    private JCheckBox LZ77;
    private JCheckBox LZW;
    private JCheckBox Huffman;
    private JCheckBox Arithmetic;
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
                LZ77.setEnabled(false);
                LZW.setEnabled(false);
                Huffman.setEnabled(false);
                Arithmetic.setEnabled(false);
                switch (extension) {
                    case "txt":
                        compressButton.setEnabled(true);
                        decompressButton.setEnabled(false);
                        decompressType.setText("");
                        LZ77.setEnabled(true);
                        LZW.setEnabled(true);
                        Huffman.setEnabled(true);
                        Arithmetic.setEnabled(true);
                        break;
                    case "lz77":
                        decompressType.setText("LZ77 - Lempel Ziv 1977");
                        break;
                    case "lzw":
                        decompressType.setText("LZ77 - Lempel Ziv Welch");
                        break;
                    case "huffman":
                        decompressType.setText("Huffman Encoding");
                        break;
                    case "art":
                        decompressType.setText("Arithmetic Coding");
                        break;
                }
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

