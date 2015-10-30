package translator;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static java.lang.Character.isAlphabetic;
import static java.util.Collections.emptyList;


/**
 * Author: katooshka
 * Date: 10/24/15.
 */

public class Translator {
    private static final String NOT_CONSONANT = "уеыаоэёяиюъьй";

    public static Map<String, String> adjectivesMap;
    public static Map<String, String> singularNounsMap;
    public static Map<String, String> pluralNounsMap;
    public static Map<String, String> pastVerbsMap;
    public static Map<String, String> presentVerbsMap;
    public static Map<String, String> additionalVocabulary;

    public static void initDictionary() throws IOException {
        adjectivesMap = readVocabulary("adjectives.txt");
        singularNounsMap = readVocabulary("singular_nouns.txt");
        pluralNounsMap = readVocabulary("plural_nouns.txt");
        pastVerbsMap = readVocabulary("past_verbs.txt");
        presentVerbsMap = readVocabulary("present_verbs.txt");
        additionalVocabulary = readAdditionalVocabulary("vocabulary.txt");
    }

    public static Map<String, String> readVocabulary(String filename) throws IOException {
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

    private static Map<String, String> readAdditionalVocabulary(String filename) throws IOException {
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

    public static String doTranslation(String text) {
        return concatenateWords(translateWords(splitText(text)));
    }

    public static List<String> splitText(String text) {
        if (text.isEmpty()) return emptyList();
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
            if (words.get(i).toLowerCase().equals("в") && i < words.size() - 2 && words.get(i + 1).equals(" ")) {
                String possibleNoun = words.get(i + 2).toLowerCase();
                if (singularNounsMap.containsKey(possibleNoun) || pluralNounsMap.containsKey(possibleNoun)) {
                    i += 2;
                    previousWordIsIn = true;
                }
            }
            String word = words.get(i);
            CASE currentCase = defineCase(word);
            String normalizedWord = word.toLowerCase().replace("ё", "е");
            String translatedWord = transformWord(getTranslationFromVocabularies(normalizedWord, previousWordIsIn));
            translatedWord = setCase(currentCase, translatedWord);
            result.add(translatedWord);
        }
        return result;
    }

    private static String enterVowels(String word) {
        StringBuilder result = new StringBuilder();
        for (int i = 0; i < word.length() - 1; i++) {
            result.append(word.charAt(i));
            if (isConsonant(word.charAt(i)) && isConsonant(word.charAt(i + 1)) && word.charAt(i) != word.charAt(i + 1)) {
                result.append("э");
            }
        }
        result.append(word.charAt(word.length() - 1));
        return result.toString();
    }

    private static boolean isConsonant(char ch) {
        return NOT_CONSONANT.indexOf(ch) == -1 && Character.isAlphabetic(ch);
    }

    private static CASE defineCase(String word) {
        int upperCase = 0;
        for (int i = 0; i < word.length(); i++) {
            if (Character.isUpperCase(word.charAt(i))) {
                upperCase++;
            }
        }
        if (Character.isUpperCase(word.charAt(0)) && upperCase == 1) {
            return CASE.CAPITALIZED;
        } else if (upperCase == 0) {
            return CASE.LOWERCASE;
        } else if (upperCase == word.length()) {
            return CASE.UPPERCASE;
        } else {
            return CASE.OTHER;
        }
    }

    private static String setCase(CASE currentCase, String word) {
        if (currentCase == CASE.CAPITALIZED) {
            return Character.toString(word.charAt(0)).toUpperCase() + word.substring(1);
        } else if (currentCase == CASE.UPPERCASE) {
            return word.toUpperCase();
        } else {
            return word;
        }
    }

    private static String getTranslationFromVocabularies(String word, boolean previousWordIsIn) {
        if (word.contains("?")) {
            return " мы" + word;
        }
        String result;
        if (singularNounsMap.containsKey(word)) {
            if (previousWordIsIn) {
                result = singularNounsMap.get(word) + "да";
            } else {
                result = singularNounsMap.get(word);
            }
        } else if (pluralNounsMap.containsKey(word)) {
            if (previousWordIsIn) {
                result = pluralNounsMap.get(word) + "ларда";
            } else {
                result = pluralNounsMap.get(word) + "лар";
            }
        } else if (adjectivesMap.containsKey(word)) {
            result = adjectivesMap.get(word);
        } else if (pastVerbsMap.containsKey(word)) {
            result = pastVerbsMap.get(word) + " итте";
        } else if (presentVerbsMap.containsKey(word)) {
            result = presentVerbsMap.get(word) + " итя";
        } else {
            result = word;
        }
        if (additionalVocabulary.containsKey(result)) {
            return additionalVocabulary.get(result);
        } else {
            return result;
        }
    }

    private static String transformWord(String word) {
        return enterVowels(word).replace("ш", "щ").replace("ч", "щ");
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

