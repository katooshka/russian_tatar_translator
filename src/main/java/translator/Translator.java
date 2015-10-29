package translator;

import java.io.BufferedReader;
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
    public static Map<String, String> adjectivesMap;
    public static Map<String, String> singularNounsMap;
    public static Map<String, String> pluralNounsMap;
    public static Map<String, String> pastVerbsMap;
    public static Map<String, String> presentVerbsMap;
    public static Map<String, String> additionalVocabulary;

    public static void main(String[] args) throws IOException {
        initDictionary();
    }

    public static void initDictionary() throws IOException {
        adjectivesMap = addVocabulary("adjectives.txt");
        singularNounsMap = addVocabulary("singular_nouns.txt");
        pluralNounsMap = addVocabulary("plural_nouns.txt");
        pastVerbsMap = addVocabulary("past_verbs.txt");
        presentVerbsMap = addVocabulary("present_verbs.txt");
        additionalVocabulary = addAdditionalVocabulary("vocabulary.txt");
    }

    // add -> read
    public static Map<String, String> addVocabulary(String filename) throws IOException {
        Map<String, String> result = new HashMap<>();
        String file = ClassLoader.getSystemResource(filename).getPath();
        Path path = Paths.get(file);
        try (BufferedReader br = Files.newBufferedReader(path)) {
            for (String line = br.readLine(); line != null; line = br.readLine()) {
                String[] splitLine = line.split(" ");
                for (int i = 1; i < splitLine.length; i++) {
                    result.put(splitLine[i], splitLine[0]);
                }
            }
            return result;
        }
    }

    // add -> read
    private static Map<String, String> addAdditionalVocabulary(String filename) throws IOException {
        Map<String, String> result = new HashMap<>();
        String file = ClassLoader.getSystemResource(filename).getPath();
        Path path = Paths.get(file);
        List<String> lines = Files.readAllLines(path);
        for (String line : lines) {
            String[] splitLine = line.split(";");
            result.put(splitLine[0], splitLine[1]);
        }
        return result;
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
        for (int i = 0; i < words.size(); i++) {
            boolean previousWordIsIn = false;
            if (words.get(i).toLowerCase().equals("в") && i <= words.size() - 2 && words.get(i + 1).equals(" ")) {
                if (singularNounsMap.containsKey(words.get(i + 2).toLowerCase())) {
                    i += 2;
                    previousWordIsIn = true;
                }
            }
            String word = words.get(i);
            // определять регистр
            // убрать е при нормализации
            String normalizedWord = word.toLowerCase();
            getTranslationFromVocabularies(normalizedWord, result, previousWordIsIn);
            // вернуть регистр
        }
        return result;
    }

    //    private static String getTranslationFromVocabularies(String word) {
    // transformWord(getTranslationFromVocabularies)
    private static void getTranslationFromVocabularies(String word, List<String> result, boolean previousWordIsIn) {
        // проверить есть ли знак вопроса
        if (additionalVocabulary.containsKey(word)) {
            result.add(additionalVocabulary.get(word));
        } else if (singularNounsMap.containsKey(word)) {
            if (previousWordIsIn) {
                // ... c ДА
            } else {
                // ... просто начальная форма
            }
        } else if (pluralNounsMap.containsKey(word)) {
            result.add(transformWord(pluralNounsMap.get(word) + "лар"));
        } else if (adjectivesMap.containsKey(word)) {
            result.add(transformWord(adjectivesMap.get(word)));
        } else if (pastVerbsMap.containsKey(word)) {
            result.add(transformWord(pastVerbsMap.get(word) + " " + "итте"));
        } else if (presentVerbsMap.containsKey(word)) {
            result.add(transformWord(presentVerbsMap.get(word) + " итя"));
        } else {
            result.add(transformWord(word));
        }
    }

    private static String transformWord(String word) {
        // добавлять э
        return word.replace("ш", "щ").replace("ч", "щ");
    }

    public static String concatenateWords(List<String> words) {
        StringBuilder text = new StringBuilder();
        for (String word : words) {
            text.append(word);
        }
        return text.toString();
    }

    private enum CharType {
        ALPHABETIC, PUNCTUATION
    }

    private enum CASE {
        LOWERCASE, UPPERCASE, CAPITALIZED, OTHER
    }
}

