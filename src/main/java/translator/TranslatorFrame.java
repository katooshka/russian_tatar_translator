package translator;


import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;
import java.io.IOException;

import static java.awt.Font.BOLD;
import static java.awt.Font.ITALIC;
import static javax.swing.SwingConstants.CENTER;
import static translator.Translator.doTranslation;

/**
 * Author: katooshka
 * Date: 10/24/15.
 */
//TODO: ?
//TODO: дополнительные слова

//TODO: добавить прокрутку в форму
//TODO: Loading
//TODO: сделать формочку не кривой

public class TranslatorFrame {
    public static void main(String[] args) throws IOException {
        drawFrame();
    }

    public static void drawFrame() throws IOException {
        //SwingUtilities.invokeLater()
        JFrame frame = new JFrame();
        frame.setPreferredSize(new Dimension(700, 400));

        JPanel translatorNamePanel = new JPanel();
        translatorNamePanel.setLayout(new FlowLayout(FlowLayout.CENTER, 0, 50));
        JLabel translatorName = new JLabel("Русско-татарский переводчик");
        translatorNamePanel.add(translatorName);
        Font nameFont = new Font("Verdana", BOLD, 18);
        translatorName.setFont(nameFont);
        translatorName.setVerticalAlignment(CENTER);
        translatorName.setHorizontalAlignment(CENTER);

        Font languageNameFont = new Font("Verdana", ITALIC, 14);
        JLabel initialLanguage = new JLabel("Русский");
        initialLanguage.setFont(languageNameFont);
        JLabel translationLanguage = new JLabel("Татарский");
        translationLanguage.setFont(languageNameFont);


        JTextArea initialText = new JTextArea();
        initialText.setWrapStyleWord(true);
        initialText.setLineWrap(true);

        JTextArea translatedText = new JTextArea();
        translatedText.setWrapStyleWord(true);
        translatedText.setLineWrap(true);

        Translator.initDictionary();

        initialText.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                String changedText = doTranslation(initialText.getText());
                translatedText.setText(changedText);
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                String changedText = doTranslation(initialText.getText());
                translatedText.setText(changedText);
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                String changedText = doTranslation(initialText.getText());
                translatedText.setText(changedText);
            }
        });

        JPanel initialTextPanel = new JPanel();
        initialTextPanel.setBorder(BorderFactory.createEmptyBorder(0, 20, 10, 10));
        initialTextPanel.setLayout(new GridLayout(2, 1, 0, 0));
        initialTextPanel.add(initialLanguage);
        initialLanguage.setHorizontalAlignment(CENTER);
        initialTextPanel.add(initialText);

        JPanel translatedTextPanel = new JPanel();
        translatedTextPanel.setBorder(BorderFactory.createEmptyBorder(0, 10, 10, 20));
        translatedTextPanel.setLayout(new GridLayout(2, 1, 0, 0));
        translatedTextPanel.add(translationLanguage);
        translationLanguage.setHorizontalAlignment(CENTER);
        translatedTextPanel.add(translatedText);

        JPanel texts = new JPanel();
        texts.setLayout(new GridLayout(1, 2, 0, 0));
        texts.add(initialTextPanel);
        texts.add(translatedTextPanel);

        JLabel allComponents = new JLabel();
        allComponents.setLayout(new BorderLayout(20, 0));
        allComponents.add(translatorNamePanel, BorderLayout.NORTH);
        allComponents.add(texts, BorderLayout.CENTER);

        frame.setContentPane(allComponents);
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
    }
}
