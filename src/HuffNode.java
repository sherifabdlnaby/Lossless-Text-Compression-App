import java.io.Serializable;
public class HuffNode implements Serializable {
    public char Char;
    public String Code;
    public Integer Freq;
    public HuffNode RightNode;
    public HuffNode LeftNode;
    public Integer getFreq() {
        return Freq;
    }
    public HuffNode(Integer freq) {
        Freq = freq;
    }
    public HuffNode(char aChar, Integer freq) {
        Char = aChar;
        Freq = freq;
    }
}
