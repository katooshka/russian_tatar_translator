package translator;


import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;

import static java.awt.Font.BOLD;
import static java.awt.Font.PLAIN;
import static translator.Translator.doTranslation;

/**
 * Author: katooshka
 * Date: 10/24/15.
 */

public class TranslatorFrame {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                drawFrame();
            }
        });
    }

    public static void drawFrame() {
        try {
            Translator.initDictionary();
        } catch (Exception e) {
            System.err.println("Failed to load dictionary");
            return;
        }
        JFrame frame = new JFrame();
        frame.setPreferredSize(new Dimension(700, 400));

        Font nameFont = new Font("Verdana", BOLD, 18);
        Font languageNameFont = new Font("Verdana", PLAIN, 14);

        final JTextArea initialText = new JTextArea();
        initialText.setWrapStyleWord(true);
        initialText.setLineWrap(true);

        final JTextArea translatedText = new JTextArea();
        translatedText.setWrapStyleWord(true);
        translatedText.setLineWrap(true);
        translatedText.setEditable(false);

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
        JScrollPane initialTextScrollPane = new JScrollPane(initialText);
        initialTextPanel.setLayout(new BorderLayout(20, 20));
        initialTextPanel.setBorder(BorderFactory.createTitledBorder(new EmptyBorder(5, 5, 5, 5),
                "Русский", TitledBorder.CENTER, TitledBorder.ABOVE_TOP, languageNameFont));
        initialTextPanel.add(initialTextScrollPane, BorderLayout.CENTER);

        JPanel translatedTextPanel = new JPanel();
        JScrollPane translatedTextScrollPane = new JScrollPane(translatedText);
        translatedTextPanel.setLayout(new BorderLayout(20, 20));
        translatedTextPanel.setBorder(BorderFactory.createTitledBorder(new EmptyBorder(5, 5, 5, 5),
                "Татарский", TitledBorder.CENTER, TitledBorder.ABOVE_TOP, languageNameFont));
        translatedTextPanel.add(translatedTextScrollPane);

        JPanel texts = new JPanel();
        texts.setLayout(new GridLayout(1, 2, 20, 20));
        texts.add(initialTextPanel);
        texts.add(translatedTextPanel);
        texts.setBorder(BorderFactory.createTitledBorder(new EmptyBorder(5, 5, 5, 5),
                "Русско-татарский переводчик", TitledBorder.CENTER, TitledBorder.ABOVE_TOP, nameFont));

        frame.setContentPane(texts);
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
    }
}
