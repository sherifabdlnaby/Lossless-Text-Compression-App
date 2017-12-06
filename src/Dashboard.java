import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
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
                //Calculate total Selected
                int i = 0;
                if(LZ77.isSelected()) ++i;
                if(LZW.isSelected()) ++i;
                if(Huffman.isSelected()) ++i;
                if(Arithmetic.isSelected()) ++i;

                String filePath = File.getParent();
                String fileName = File.getName();
                try {
                    String inputString = new String(Files.readAllBytes(Paths.get(File.getAbsolutePath())));
                } catch (IOException e1) {
                    JOptionPane.showMessageDialog(null,"Can't Open File");
                    e1.printStackTrace();
                    return;
                }

                if(LZ77.isSelected()){
                    new Thread(new Runnable() {

                        @Override
                        public void run() {

                        }

                    }).start();
                }
                if(LZW.isSelected()) ++i;
                if(Huffman.isSelected()) ++i;
                if(Arithmetic.isSelected()) ++i;


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

