import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;


public class Dashboard extends JPanel {
    private JPanel Panel;
    private JTextField restaurantTextField;
    private JButton browseButton;
    private JTextArea NamesField;
    private JCheckBox LZ77;
    private JCheckBox LZW;
    private JCheckBox Huffman;
    private JCheckBox Arithmetic;
    private JButton compressButton;
    private JProgressBar progressBar1;

    public static void main(String[] args) {
        JFrame jFrame = new JFrame("Restaurant Simulation");
        jFrame.setContentPane(new Dashboard().Panel);
        jFrame.setDefaultCloseOperation(jFrame.EXIT_ON_CLOSE);
        jFrame.pack();
        jFrame.setVisible(true);
    }
}

