import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

public class Huffman {
    private static void AddEncodeDFS(HuffNode node, String encode, HashMap<Character, String> FinalTable)
    {
        node.Code = encode;
        if(node.Char != '\0')
            FinalTable.put(node.Char, encode);
        if (node.LeftNode != null)
            AddEncodeDFS(node.LeftNode, encode + '0', FinalTable);
        if (node.RightNode != null)
            AddEncodeDFS(node.RightNode, encode + '1', FinalTable);
    }
    private static HashMap<String,Character> HuffmanEncoding(String inputString, BitSet bitVector) {

        //Create Frequency & Final Tables
        HashMap<Character, Integer> frequencyTable = new HashMap<Character, Integer>();
        HashMap<Character, String> finalTable    = new HashMap<Character, String>();

        char tmpChar;
        for (int i = 0; i < inputString.length(); i++) {
            tmpChar = inputString.charAt(i);
            if(frequencyTable.containsKey(tmpChar))
                frequencyTable.put(tmpChar, frequencyTable.get(tmpChar) + 1);
            else
                frequencyTable.put(tmpChar, 1);
        }

        //Create Initial Priority Queue
        PriorityQueue<HuffNode> frequencyQueue = new PriorityQueue<>(Comparator.comparing(HuffNode::getFreq));
        for (Map.Entry<Character, Integer> entry : frequencyTable.entrySet()) {
            frequencyQueue.add(new HuffNode(entry.getKey(),entry.getValue()));
        }

        //Creates The Tree
        while (frequencyQueue.size() > 1)
        {
            HuffNode smallest1 = frequencyQueue.poll();
            HuffNode smallest2 = frequencyQueue.poll();
            HuffNode newNode = new HuffNode(smallest1.getFreq() + smallest2.getFreq());
            newNode.LeftNode = smallest1;
            newNode.RightNode = smallest2;
            frequencyQueue.add(newNode);
        }

        //Assign Trie Head to the parameter Trie.
        HuffNode Trie = frequencyQueue.poll();

        //Add Encoding using DFS + Fill FinalTable
        AddEncodeDFS(Trie, "", finalTable);

        //Encoding New String (Using Bytes not bits lol)
        int vectorCounter = 0;
        for (int i = 0; i < inputString.length(); ++i) {
            String value = finalTable.get(inputString.charAt(i));
            for (int j = 0; j < value.length(); ++j, ++vectorCounter)
                if (value.charAt(j) == '1') {
                    bitVector.set(vectorCounter);
                }
        }

        //Generate a Flipped Hashtable
        HashMap<String,Character> flippedTable = new HashMap<>();
        for(Map.Entry<Character, String> entry : finalTable.entrySet())
            flippedTable.put(entry.getValue(), entry.getKey());

        return flippedTable;
    }

    private static String HuffmanDecoding(HashMap<String,Character> writeTable, BitSet bitVector) {
        StringBuilder decompressedString = new StringBuilder();
        String key;
        for (int i = 0; i < bitVector.length();) {
            key = "";
            while(!writeTable.containsKey(key))
                key += bitVector.get(i++) ? "1" : "0";
            decompressedString.append(writeTable.get(key));
        }
        return decompressedString.toString();
    }

    public static boolean Compress(String inputString, String fileName, String filePath) {
        try {
            //Create HashTable Of Encoded and fill bitVector
            BitSet bitVector = new BitSet();
            HashMap<String,Character> WriteTable = HuffmanEncoding(inputString, bitVector);

            //Output Files Names
            String outputFileName = fileName.substring(0,fileName.lastIndexOf('.'))+".huffman";
            String outputPath = filePath + "/" + outputFileName;

            //Write using Java's Object Serialization
            FileOutputStream fileOutputStream = new FileOutputStream(outputPath);
            ObjectOutputStream objectOutputStream = new ObjectOutputStream(fileOutputStream);

            //Write
            objectOutputStream.writeObject(WriteTable);
            objectOutputStream.writeObject(bitVector);
            objectOutputStream.close();

        } catch (IOException exception) {
            System.out.println(exception.getMessage());
            return false;
        }
        return true;
    }

    public static boolean Decompress(String fileName, String filePath) {
        HashMap<String,Character> WriteTable;
        BitSet bitVector;
        FileWriter outputFile;
        try {
            InputStream file = new FileInputStream(filePath+ "/" +fileName);
            InputStream buffer = new BufferedInputStream(file);
            ObjectInput input = new ObjectInputStream(buffer);

            //Read Saved Tags
            WriteTable = (HashMap<String,Character>) input.readObject();
            bitVector = (BitSet) input.readObject();

            String decompressedString = HuffmanDecoding(WriteTable, bitVector);

            //Create output File
            String outputFileName = fileName.substring(0,fileName.lastIndexOf('.'))+"Decompressed.txt";
            String outputPath = filePath + "/" + outputFileName;
            outputFile = new FileWriter(outputPath);

            //Write Decompressed File
            outputFile.write(decompressedString);
            outputFile.close();

        } catch (IOException | ClassNotFoundException exception) {
            System.out.println(exception.getMessage());
            return false;
        }
        return true;
    }

}
