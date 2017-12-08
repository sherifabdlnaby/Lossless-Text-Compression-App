import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

public class LZW {
    private static int DictionarySize = 128;

    private static List<Integer> LZWCompress(String string) {
        List<Integer> Tags = new Vector<Integer>();
        Map<String, Integer> Dictionary = new HashMap<String, Integer>();
        int dictionarySize = DictionarySize; //Create a non-static copy of the configured Dictionary Size.

        //Populate Dictionary with ASCII
        for (int i = 0; i < DictionarySize; i++) {
            Dictionary.put(String.valueOf((char) i), i);
        }

        String searchWord = "";
        String newSearchWord = "";
        //Main Loop
        for (int i = 0; i < string.length(); ) {
            //Get greatest match (Growing searchWord loop)
            for (int j = i + 1; j <= string.length(); j++) {
                newSearchWord = string.substring(i, j);
                if (Dictionary.containsKey(newSearchWord))
                    searchWord = newSearchWord;
                else
                    break;
            }
            //Currently searchWord is the greatest Match.
            //Add Tag
            Tags.add(Dictionary.get(searchWord));

            //Add new Dictionary Entry
            Dictionary.put(newSearchWord, dictionarySize);
            dictionarySize++;

            //Shift pointer
            i += searchWord.length();
        }
        return Tags;
    }

    private static String LZWDecompress(List<Integer> Tags) {
        StringBuilder originalString = new StringBuilder();
        Map<Integer, String> Dictionary = new HashMap<Integer, String>();
        int dictionarySize = DictionarySize; //Create a non-static copy of the configured Dictionary Size.

        //Populate Dictionary with ASCII
        for (int i = 0; i < DictionarySize; i++) {
            Dictionary.put(i, String.valueOf((char) i));
        }

        //Add First Tag
        originalString.append(Dictionary.get(Tags.get(0)));
        String currentItrWord = originalString.toString();
        String lastItrWord = currentItrWord;

        //Decompress Loop Begin
        for (int i = 1; i < Tags.size(); i++) {
            //Get new Word, save old word.
            if (Dictionary.containsKey(Tags.get(i))) {
                lastItrWord = currentItrWord;
                currentItrWord = Dictionary.get(Tags.get(i));

                //Add new dictionary Entry
                Dictionary.put(dictionarySize++, lastItrWord + currentItrWord.charAt(0));
            } else {
                currentItrWord = currentItrWord + currentItrWord.charAt(0);
                lastItrWord = currentItrWord;

                //Add new dictionary Entry
                Dictionary.put(dictionarySize++, currentItrWord);
            }

            //Add to the String
            originalString.append(currentItrWord);
        }

        return originalString.toString();
    }

    //Returns number of bytes of the compressed file
    public static long Compress(String inputString, String fileName, String filePath) {
        try {
            //Read Original File into String
            inputString = new String(Files.readAllBytes(Paths.get(filePath + "/" +fileName)));

            //Compress and Generate Tags
            List<Integer> tags = LZWCompress(inputString);

            //Output Files Names
            String outputFileName = fileName.substring(0,fileName.lastIndexOf('.'))+".lzw";
            String outputPath = filePath + "/" + outputFileName;

            //Write using Java's Object Serialization
            FileOutputStream fileOutputStream = new FileOutputStream(outputPath);
            ObjectOutputStream objectOutputStream = new ObjectOutputStream(fileOutputStream);

            //Write
            objectOutputStream.writeObject(tags);
            objectOutputStream.close();

            //
            File newFile = new File(outputPath);
            return newFile.length();
        } catch (IOException exception) {
            System.out.println(exception.getMessage());
            return -1;
        }
    }

    public static boolean Decompress(String fileName, String filePath) {
        List<Integer> tags;
        FileWriter outputFile;
        try {
            InputStream file = new FileInputStream(filePath+ "/" +fileName);
            InputStream buffer = new BufferedInputStream(file);
            ObjectInput input = new ObjectInputStream(buffer);

            //Read Saved Tags
            tags = (List<Integer>) input.readObject();

            //Decompress
            String decompressedString = LZWDecompress(tags);

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
