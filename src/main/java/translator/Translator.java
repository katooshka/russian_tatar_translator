package translator;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

import static java.lang.Character.isAlphabetic;


/**
 * Author: katooshka
 * Date: 10/24/15.
 */

public class Translator {

    private static Map<String, String> vocabulary = new HashMap<>();

    public static void main(String[] args) throws IOException {
        //addAdditionalVocabulary("vocabulary.txt");
        //System.out.println(vocabulary);
        createVocabulary(prepareText("dictionary.txt"));
    }

    public static void addAdditionalVocabulary(String filename) throws IOException {
        String file = ClassLoader.getSystemResource(filename).getPath();
        Path path = Paths.get(file);
        List<String> lines = Files.readAllLines(path);
        for (String line : lines) {
            for (int i = 0; i < line.split(" ").length; i++) {
                vocabulary.put(line.split(" ")[0], line.split(" ")[1]);
            }
        }
    }

    private static List<String> prepareText(String filename) throws IOException {
        String file = ClassLoader.getSystemResource(filename).getPath();
        Path path = Paths.get(file);
        List<String> text = Files.readAllLines(path);
        for (Iterator<String> lineIterator = text.iterator(); lineIterator.hasNext();){
            String currentLine = lineIterator.next();
            if (currentLine.matches("^[0-9]*$")){
                lineIterator.remove();
            }
        }
        return text;
    }

    public static void createVocabulary(List<String> text) {
        Map<String, List<String>> nounMap = new HashMap<>();
        Map<String, String> adjectiveMap = new HashMap<>();
        Map<String, List<String>> verbMap = new HashMap<>();
        String previousLine = "";
        StringBuilder initialForm = new StringBuilder();
        for (String line : text) {
            if (previousLine.equals("")){
                initialForm.append(normalizedLine(line, 0));
            }
            if (normalizedLine(line, 1).equals("NOUN")){
                List<String> characteristics = new ArrayList<>();
                characteristics.add(initialForm.toString());
                characteristics.add(normalizedLine(line, 4));
                nounMap.put(normalizedLine(line, 0), characteristics);
            }
            if (normalizedLine(line, 1).equals("ADJF") || normalizedLine(line, 1).equals("PRTF")) {
                adjectiveMap.put(normalizedLine(line, 0), initialForm.toString());
            }
            if (normalizedLine(line, 1).equals("VERB")){
                List<String> characteristics = new ArrayList<>();
                characteristics.add(initialForm.toString());
                characteristics.add(normalizedLine(line, 5));
                characteristics.add(normalizedLine(line, 6));
                verbMap.put(normalizedLine(line, 0), characteristics);
            }
        }
        System.out.println(nounMap);
        System.out.println(adjectiveMap);
        System.out.println(verbMap);

    }

    public static String normalizedLine (String line, int wordNumber){
        return line.split("[ ,]+")[wordNumber];
    }

    public static List<String> splitText(String text) {
        List<String> result = new ArrayList<>();
        StringBuilder currentWordChars = new StringBuilder();
        CharType lastCharType = null;
        for (int i = 0; i < text.length(); i++) {
            CharType currentCharType = isAlphabetic(text.charAt(i)) ? CharType.ALPHABETIC : CharType.PUNCTUATION;
            if (lastCharType == currentCharType || lastCharType == null) {
                currentWordChars.append(text.charAt(i));
            } else {
                result.add(currentWordChars.toString());
                currentWordChars.setLength(0);
                currentWordChars.append(text.charAt(i));
            }
            lastCharType = currentCharType;
        }
        result.add(currentWordChars.toString());
        return result;
    }

    public static List<String> translateWords(List<String> words) {
        List<String> result = new ArrayList<>();
        for (String word : words) {
            String normalizedWord = word.toLowerCase();
            if (vocabulary.containsKey(normalizedWord)){
                result.add(vocabulary.get(normalizedWord));
            }
            else {
                result.add(word);
            }
        }
        return result;
    }

    public static String concatenateWords (List<String> words) {
        StringBuilder text = new StringBuilder();
        for (String word : words) {
            text.append(word);
        }
        return text.toString();
    }

    private enum CharType {
        ALPHABETIC, PUNCTUATION
    }
}

