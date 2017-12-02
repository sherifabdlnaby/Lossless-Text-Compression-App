package com.multimedia.gui;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.LinkedList;

public class LZ77 {

    private static LinkedList<Tag> LZ77Compress(String string) {
        LinkedList<Tag> Tags = new LinkedList<>();
        //Start Looping
        int pivot = 0;
        while ( pivot < string.length() ) {
            int searchWindowMin = Math.max(0, pivot - 1024);
            String searchWindow = string.substring(searchWindowMin,pivot);
            String searchWord = string.substring(pivot,pivot+1);
            String newSearchWord = searchWord;
            int foundOffset = -2;
            int newFoundOffset = foundOffset;
            //Find Longest Match
            int end = Math.min(pivot+2,string.length());
            int lookAheadWindowSize = Math.min(end + 1024, string.length());
            for (; end <= lookAheadWindowSize; end++) {
                newFoundOffset = searchWindow.lastIndexOf(newSearchWord);
                if(newFoundOffset != -1) {
                    foundOffset = newFoundOffset;
                    searchWord = newSearchWord;
                    newSearchWord = string.substring(pivot,end);
                    if (end < lookAheadWindowSize)   //Handles if there is no more characters to find a non-match.
                        continue;
                }
                //Reached here = Didn't Find --> Add Longest Match Or New Single Letter
                //New Match
                if(foundOffset != -2) {
                    //Search for consecutive matches
                    int consecutiveMatches = 0;
                    int consecutiveSearchStart = pivot + searchWord.length();
                    while (string.regionMatches(consecutiveSearchStart, searchWord, 0, searchWord.length())) {
                        consecutiveMatches++;
                        consecutiveSearchStart += searchWord.length();
                    }
                    int totalWordSize = searchWord.length() + searchWord.length() * consecutiveMatches;
                    Tags.add(
                            new Tag(
                                    pivot-(foundOffset+searchWindowMin),
                                    searchWord.length(),
                                    pivot + searchWord.length() < string.length() && consecutiveMatches == 0 ?
                                            string.charAt(pivot + searchWord.length()) : '\0'
                            )
                    );
                    if (consecutiveMatches > 0) {
                        Tags.add(
                                new Tag(
                                        searchWord.length(),
                                        searchWord.length() * consecutiveMatches,
                                        pivot + totalWordSize < string.length() ? string.charAt(pivot + totalWordSize) : '\0'
                                )
                        );
                    }
                    pivot += totalWordSize + 1;
                }
                //New Single Letter
                else {
                    Tags.add( new Tag(0,0,searchWord.charAt(0)));
                    pivot++;
                }
                break;
            }
        }
        return Tags;
    }

    private static String LZ77Decompress(LinkedList<Tag> Tags) {
        String orignialString = "";
        for (Tag tag : Tags) {
            if (tag.Position == 0)
                orignialString += tag.NextSymbol;
            else {
                int offset = orignialString.length() - tag.Position;
                int end;
                int repeatCount = 1;
                if (tag.Position < tag.Length) //Repeat Tag Detected
                {
                    end = offset + tag.Position;
                    repeatCount = tag.Length / tag.Position;
                } else {
                    end = offset + tag.Length;
                }
                String add = orignialString.substring(offset, end);

                for (int i = 0; i < repeatCount; i++) {
                    orignialString += add;
                }

                if (tag.NextSymbol != '\0')
                    orignialString += tag.NextSymbol;
            }
        }
        return orignialString;
    }

    public static boolean Compress(String fileName, String filePath) {
        FileReader input;
        String inputString = "";
        try {
            //Read Original File into String
            inputString = new String(Files.readAllBytes(Paths.get(filePath + "/" +fileName)));

            //Compress and Generate Tags
            LinkedList<Tag> tags = LZ77Compress(inputString);

            //Output Files Names
            String outputFileName = fileName.substring(0,fileName.lastIndexOf('.'))+".lz77";
            String outputPath = filePath + "/" + outputFileName;

            //Write using Java's Object Serialization
            FileOutputStream fileOutputStream = new FileOutputStream(outputPath);
            ObjectOutputStream objectOutputStream = new ObjectOutputStream(fileOutputStream);

            //Write
            objectOutputStream.writeObject(tags);
            objectOutputStream.close();
        } catch (IOException exception) {
            System.out.println(exception.getMessage());
            return false;
        }
        return true;
    }

    public static boolean Decompress(String fileName, String filePath) {
        LinkedList<Tag> tags;
        FileWriter outputFile;
        try {
            InputStream file = new FileInputStream(filePath+ "/" +fileName);
            InputStream buffer = new BufferedInputStream(file);
            ObjectInput input = new ObjectInputStream(buffer);

            //Read Saved Tags
            tags = (LinkedList<Tag>) input.readObject();

            //Decompress
            String decompressedString = LZ77Decompress(tags);

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
