import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

public class Arithmetic {
    public static char terminator = (char) 2; //TODO rmv static

    public static int chunckSize = 8;

    public static class Bounds {
        public double upper;
        public double lower;

        public Bounds(double lower, double upper) {
            this.upper = upper;
            this.lower = lower;
        }
    }

    public static List<Double> ArithmeticEncoding(String stringOrg, LinkedHashMap<Character, Double> probTable) {

        List<Double> encodedDecimals = new LinkedList<>();

        //Create Probabilities Count
        LinkedHashMap<Character, Bounds> boundsTable = new LinkedHashMap<>();
        for (int i = 0; i < stringOrg.length(); i++) {
            if (probTable.containsKey(stringOrg.charAt(i)))
                probTable.put(stringOrg.charAt(i), probTable.get(stringOrg.charAt(i)) + 1);
            else
                probTable.put(stringOrg.charAt(i), 1d);
        }

        int numOfTerminators = (stringOrg.length() / chunckSize) + stringOrg.length() % chunckSize == 0 ? 0 : 1;
        double lengthWithTerm = stringOrg.length() + numOfTerminators;
        probTable.put(terminator, (double) numOfTerminators);

        //Calculate Probabilities
        double prob = 0;
        double uprBound = 0;
        double lwrBound = 0;
        for (Map.Entry<Character, Double> entry : probTable.entrySet()) {
            prob = entry.getValue() / lengthWithTerm;
            probTable.put(entry.getKey(), prob);
            //Calculate Bounds
            lwrBound = uprBound;
            uprBound = lwrBound + prob;
            boundsTable.put(entry.getKey(), new Bounds(lwrBound, uprBound));
        }

        //Encoding
        for (int idx = 0; idx < stringOrg.length(); ) {

            //Extract Substring
            String string = stringOrg.substring(idx, Math.min(idx + chunckSize, stringOrg.length()));
            idx += chunckSize;

            //Add Terminator
            string += terminator;

            //Encode
            lwrBound = 0;
            uprBound = 1;
            for (int i = 0; i < string.length(); i++) {
                double tmpLwr = lwrBound;
                lwrBound = tmpLwr + (uprBound - tmpLwr) * boundsTable.get(string.charAt(i)).lower;
                uprBound = tmpLwr + (uprBound - tmpLwr) * boundsTable.get(string.charAt(i)).upper;
            }
            encodedDecimals.add((lwrBound + uprBound) / 2);
        }
        return encodedDecimals;
    }

    public static String ArithmeticDecoding(List<Double> encodedDecimals, LinkedHashMap<Character, Double> probTable) {

        //Recalculating Bounds
        LinkedHashMap<Character, Bounds> boundsTable = new LinkedHashMap<>();
        //Calculate Probabilities
        double prob = 0;
        double uprBound = 0;
        double lwrBound = 0;
        //Calculate Bounds
        for (Map.Entry<Character, Double> entry : probTable.entrySet()) {
            prob = entry.getValue();
            lwrBound = uprBound;
            uprBound = lwrBound + prob;
            boundsTable.put(entry.getKey(), new Bounds(lwrBound, uprBound));
        }
        StringBuilder decodedString = new StringBuilder();
        //Decode
        for (double decodedDecimal : encodedDecimals) {
            lwrBound = 0;
            uprBound = 1;
            Double value = decodedDecimal;
            char nextChar = '\0';
            do {
                for (Map.Entry<Character, Bounds> entry : boundsTable.entrySet()) {
                    if (value >= entry.getValue().lower && value < entry.getValue().upper) {
                        nextChar = entry.getKey();
                        if (nextChar == terminator)
                            break;
                        //Append
                        decodedString.append(entry.getKey());
                        //Update bounds
                        double tmpLwr = lwrBound;
                        lwrBound = tmpLwr + (uprBound - tmpLwr) * entry.getValue().lower;
                        uprBound = tmpLwr + (uprBound - tmpLwr) * entry.getValue().upper;
                        value = (decodedDecimal - lwrBound) / (uprBound - lwrBound);
                        break;
                    }
                }
            } while (nextChar != terminator);
        }
        return decodedString.toString();
    }

    public static boolean Compress(String inputString, String fileName, String filePath) {
        try {
            //Algorithm
            LinkedHashMap<Character, Double> probFillTable = new LinkedHashMap<>();
            List<Double> EncodedDecimals = ArithmeticEncoding(inputString, probFillTable);

            //Output Files Names
            String outputFileName = fileName.substring(0,fileName.lastIndexOf('.'))+".art";
            String outputPath = filePath + "/" + outputFileName;

            //Write using Java's Object Serialization
            FileOutputStream fileOutputStream = new FileOutputStream(outputPath);
            ObjectOutputStream objectOutputStream = new ObjectOutputStream(fileOutputStream);

            //Write
            objectOutputStream.writeObject(probFillTable);
            objectOutputStream.writeObject(EncodedDecimals);
            objectOutputStream.close();

        } catch (IOException exception) {
            System.out.println(exception.getMessage());
            return false;
        }
        return true;
    }

    public static boolean Decompress(String fileName, String filePath) {
        String decompressedString;
        FileWriter outputFile;
        try {
            InputStream file = new FileInputStream(filePath+ "/" +fileName);
            InputStream buffer = new BufferedInputStream(file);
            ObjectInput input = new ObjectInputStream(buffer);

            //Decompress
            LinkedHashMap<Character, Double> probTable = (LinkedHashMap<Character, Double>) input.readObject();
            List<Double> EncodedDecimals = (LinkedList<Double>) input.readObject();
            decompressedString = ArithmeticDecoding(EncodedDecimals, probTable);


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
