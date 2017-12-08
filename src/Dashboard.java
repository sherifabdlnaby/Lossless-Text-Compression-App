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
    private JTextArea console;
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
                //Kinda a Hack-y code style, I'd rather not do that If I have time.
                compressButton.setEnabled(false);
                Thread procces = new Thread(() -> {
                    //Calculate total Selected
                    int i = 0;
                    if (LZ77Select.isSelected()) ++i;
                    if (LZWSelect.isSelected()) ++i;
                    if (HuffmanSelect.isSelected()) ++i;
                    if (ArithmeticSelect.isSelected()) ++i;
                    int incrementValue = (int) ((1 / (double) i) * 100);
                    String filePath = File.getParent();
                    String fileName = File.getName();
                    String inputString = "";
                    Long originalFileSize = File.length();
                    try {
                        inputString = new String(Files.readAllBytes(Paths.get(File.getAbsolutePath())));
                    } catch (IOException e1) {
                        JOptionPane.showMessageDialog(null, "Can't Open File");
                        e1.printStackTrace();
                        return;
                    }
                    progressBar1.setValue(0);
                    console.setText("Running... \n");
                    if (LZ77Select.isSelected()) {
                        Long newFileSize = LZ77.Compress(inputString, fileName, filePath);
                        long savedBytes = originalFileSize - newFileSize;
                        int  ratio = (int)((double)newFileSize/originalFileSize * 100);
                        int  savedRatio = 100 - ratio;
                        console.append(
                                "================================================== \n" +
                                "LZ77 Completed 100% \n" +
                                "- Original Size = " + originalFileSize + " Byte.\n" +
                                "- New Compressed Size = " + newFileSize + " Byte.\n" +
                                "- Saved Bytes = " + savedBytes + " Byte.\n" +
                                "- Compression = " + (savedRatio > 0 ? "+" : "") + savedRatio * -1 + "% of the Original Size\n" +
                                (savedRatio < 0 ? "* If Original File is too Small, compressed file may be bigger due to serialization overhead" : "")
                        );
                        progressBar1.setValue(progressBar1.getValue() + incrementValue);
                        progressBar1.repaint();
                    }

                    if (LZWSelect.isSelected()) {
                        Long newFileSize = LZW.Compress(inputString, fileName, filePath);
                        long savedBytes = originalFileSize - newFileSize;
                        int  ratio = (int)((double)newFileSize/originalFileSize *100);
                        int  savedRatio = 100 - ratio;
                        console.append(
                                        "================================================== \n" +
                                        "LZW Completed 100% \n" +
                                        "- Original Size = " + originalFileSize + " Byte.\n" +
                                        "- New Compressed Size = " + newFileSize + " Byte.\n" +
                                        "- Saved Bytes = " + savedBytes + " Byte.\n" +
                                        "- Compression = " + (savedRatio > 0 ? "+" : "") + savedRatio * -1 + "% of the Original Size\n" +
                                        (savedRatio < 0 ? "* If Original File is too Small, compressed file may be bigger due to serialization overhead" : "")
                        );
                        progressBar1.setValue(progressBar1.getValue() + incrementValue);
                        progressBar1.repaint();
                    }

                    if (HuffmanSelect.isSelected()) {
                        Long newFileSize = Huffman.Compress(inputString, fileName, filePath);
                        long savedBytes = originalFileSize - newFileSize;
                        int  ratio = (int)((double)newFileSize/originalFileSize *100);
                        int  savedRatio = 100 - ratio;
                        console.append(
                                        "================================================== \n" +
                                        "Huffman Encoding Completed 100% \n" +
                                        "- Original Size = " + originalFileSize + " Byte.\n" +
                                        "- New Compressed Size = " + newFileSize + " Byte.\n" +
                                        "- Saved Bytes = " + savedBytes + " Byte.\n" +
                                        "- Compression = " + (savedRatio > 0 ? "+" : "") + savedRatio * -1 + "% of the Original Size\n" +
                                        (savedRatio < 0 ? "* If Original File is too Small, compressed file may be bigger due to serialization overhead" : "")
                        );
                        progressBar1.setValue(progressBar1.getValue() + incrementValue);
                        progressBar1.repaint();
                    }

                    if (ArithmeticSelect.isSelected()) {
                        Long newFileSize = Arithmetic.Compress(inputString, fileName, filePath);
                        long savedBytes = originalFileSize - newFileSize;
                        int  ratio = (int)((double)newFileSize/originalFileSize *100);
                        int  savedRatio = 100 - ratio;
                        console.append(
                                        "================================================== \n" +
                                        "Arithmetic Coding Completed 100% \n" +
                                        "- Original Size = " + originalFileSize + " Byte.\n" +
                                        "- New Compressed Size = " + newFileSize + " Byte.\n" +
                                        "- Saved Bytes = " + savedBytes + " Byte.\n" +
                                        "- Compression = " + (savedRatio > 0 ? "+" : "") + savedRatio * -1 + "% of the Original Size\n" +
                                        (savedRatio < 0 ? "* If Original File is too Small, compressed file may be bigger due to serialization overhead" : "")
                        );
                        progressBar1.setValue(progressBar1.getValue() + incrementValue);
                        progressBar1.repaint();
                    }
                    compressButton.setEnabled(true);
                });
                procces.start();
            }
        });
        decompressButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String extension = File.getName();
                extension = extension.substring(extension.lastIndexOf('.')+1);
                String filePath = File.getParent();
                String fileName = File.getName();
                compressButton.setEnabled(false);
                progressBar1.setValue(0);
                console.setText("Running... \n");
                final String extensionFinal = extension;
                Thread procces = new Thread(() -> {
                    switch (extensionFinal) {
                        case "lz77":
                            LZ77.Decompress(fileName, filePath);
                            break;
                        case "lzw":
                            LZW.Decompress(fileName, filePath);
                            break;
                        case "huffman":
                            Huffman.Decompress(fileName, filePath);
                            break;
                        case "art":
                            Arithmetic.Decompress(fileName, filePath);
                            break;
                    }
                    console.append(
                            "================================================== \n" +
                            "Decompression Completed 100% \n"
                    );
                    progressBar1.setValue(100);
                    progressBar1.repaint();
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

