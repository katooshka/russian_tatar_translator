package transform_vocabulary;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import static java.lang.System.currentTimeMillis;

/**
 * Author: katooshka
 * Date: 10/27/15.
 */
public class TransformVocabulary {
    static Map<String, Set<String>> singularNouns = new HashMap<>();
    static Map<String, Set<String>> pluralNouns = new HashMap<>();
    static Map<String, Set<String>> adjectives = new HashMap<>();
    static Map<String, Set<String>> presentVerbs = new HashMap<>();
    static Map<String, Set<String>> pastVerbs = new HashMap<>();

    public static void main(String args[]) throws IOException {
        long time = currentTimeMillis();
        createFormsLists("dictionary.txt");
        writeToFile(singularNouns, "singular_nouns.txt");
        writeToFile(pluralNouns, "plural_nouns.txt");
        writeToFile(adjectives, "adjectives.txt");
        writeToFile(presentVerbs, "present_verbs.txt");
        writeToFile(pastVerbs, "past_verbs.txt");
        System.out.println(currentTimeMillis() - time);
    }

    private static void createFormsLists(String file) throws IOException {
        String previousLine = "";
        String initialForm = null;
        Set<String> pastForms = new HashSet<>();
        Set<String> presentForms = new HashSet<>();
        Path path = Paths.get(file);
        try (BufferedReader br = Files.newBufferedReader(path)) {
            int i = 0;
            for (String line = br.readLine(); line != null; line = br.readLine(), i++) {
                if (!line.isEmpty() && Character.isDigit(line.charAt(0))) {
                    continue;
                }
                if (line.isEmpty()) {
                    previousLine = line;
                    continue;
                }
                String[] firstSplit = line.split("\t");
                String word = firstSplit[0];
                String[] secondSplit = firstSplit[1].split(" ");
                String firstGroup = secondSplit[0];
                String partOfSpeech = firstGroup.split(",")[0];
                String secondGroup = (secondSplit.length >= 2) ? secondSplit[1] : "";

                if (previousLine.equals("")) {
                    initialForm = word;
                }
                if (partOfSpeech.equals("NOUN")
                        && !firstGroup.contains("Name")
                        && !firstGroup.contains("Patr")
                        && !firstGroup.contains("Sgtm")
                        && !firstGroup.contains("Orgn")
                        && !firstGroup.contains("Fixd")
                        && !firstGroup.contains("Geox")
                        && !firstGroup.contains("Arch")
                        && !firstGroup.contains("Ms-f")) {
                    if (secondGroup.contains("sing")) {
                        addToMap(singularNouns, initialForm, word);
                    }
                    if (secondGroup.contains("plur")) {
                        addToMap(pluralNouns, initialForm, word);
                    }
                } else if (partOfSpeech.equals("VERB")) {
                    if (secondGroup.contains("past")) {
                        pastForms.add(word);
                    }
                    if (secondGroup.contains("pres")) {
                        presentForms.add(word);
                    }
                } else if (partOfSpeech.equals("INFN")) {
                    if (!word.equals("/")) {
                        if (!pastForms.isEmpty()) {
                            pastVerbs.put(word, pastForms);
                        }
                        if (!presentForms.isEmpty()) {
                            presentVerbs.put(word, presentForms);
                        }
                    }
                    pastForms = new HashSet<>();
                    presentForms = new HashSet<>();
                } else if (partOfSpeech.equals("ADJF") || partOfSpeech.equals("PRTF")) {
                    addToMap(adjectives, initialForm, word);
                }
                previousLine = line;

            }
        }

    }

    private static void addToMap(Map<String, Set<String>> map, String initialForm, String word) {
        if (!map.containsKey(initialForm)) {
            map.put(initialForm, new HashSet<String>());
        }
        map.get(initialForm).add(word);
    }

    private static void writeToFile(Map<String, Set<String>> map, String filename) throws IOException {
        try (BufferedWriter bw = Files.newBufferedWriter(Paths.get(filename))) {
            for (Map.Entry<String, Set<String>> entry : map.entrySet()) {
                String initialForm = entry.getKey();
                Set<String> forms = entry.getValue();
                String line = initialForm + " " + String.join(" ", forms);
                bw.write(line.toLowerCase().replace("ё", "е"));
                bw.newLine();
            }
        }
    }
}