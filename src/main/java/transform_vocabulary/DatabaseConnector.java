package transform_vocabulary;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.sql.*;

/**
 * Author: katooshka
 * Date: 1/13/16.
 */
// TransferToDatabase
public class DatabaseConnector {

    //TODO: вынести константы

    public static void main(String[] args) throws ClassNotFoundException, SQLException, IOException {
        Class.forName("com.mysql.jdbc.Driver");
        try (Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/vocabulary", "translator", "3705352rfnz")) {
            if (tableExists(connection)) {
                dropTable(connection);
            }
            createTable(connection);
            putVocabularyIntoDatabase(connection, "exact_translation.txt", "ExactTranslation");
            putVocabularyIntoDatabase(connection, "singular_nouns.txt", "SingNoun");
            putVocabularyIntoDatabase(connection, "plural_nouns.txt", "PlurNoun");
            putVocabularyIntoDatabase(connection, "present_verbs.txt", "PresVerb");
            putVocabularyIntoDatabase(connection, "past_verbs.txt", "PastVerb");
            putVocabularyIntoDatabase(connection, "adjectives.txt", "Adjective");
        }
    }

    public static void putVocabularyIntoDatabase(Connection connection, String filename, String wordType) throws IOException, SQLException {
        InputStream inputStream = ClassLoader.getSystemResourceAsStream(filename);
        try (BufferedReader br = new BufferedReader(new InputStreamReader(inputStream))) {
            for (String line = br.readLine(); line != null; line = br.readLine()) {
                String[] splitLine = line.split(" ");
                putStringIntoDatabase(connection, splitLine, splitLine[0], wordType);
            }
        }
    }

    private static boolean tableExists(Connection connection) throws SQLException {
        // закрывать здесь и дальше
        String query = "SHOW TABLES LIKE 'T_DICTIONARY'";
            PreparedStatement preparedStatement = connection.prepareStatement(query);
        ResultSet resultSet = preparedStatement.executeQuery();
        return resultSet.next();
    }

    private static void dropTable(Connection connection) throws SQLException {
        String query = "DROP TABLE T_DICTIONARY";
        PreparedStatement preparedStatement = connection.prepareStatement(query);
        preparedStatement.executeUpdate();
    }

    private static void createTable(Connection connection) throws SQLException {
        String query = "CREATE TABLE T_DICTIONARY (" +
                "WORD VARCHAR(50) NOT NULL, " +
                "INITIAL_FORM VARCHAR(50) NOT NULL, " +
                "WORD_TYPE VARCHAR(50) NOT NULL, " +
                "UNIQUE KEY(WORD))";
        PreparedStatement preparedStatement = connection.prepareStatement(query);
        preparedStatement.executeUpdate();
    }


// понять почему вынесли initialForm

    private static void putStringIntoDatabase(Connection connection, String[] words, String initialForm, String word_type) throws SQLException {
        for (int i = 0; i < words.length; i++) { // заменить тип цикла
            boolean exists = wordExists(connection, words[i]);
            if (!exists) {
                // именованные параметры, скинул линк в вк
                String insertionQuery = "INSERT INTO T_DICTIONARY (WORD, INITIAL_FORM, WORD_TYPE) VALUES (?, ?, ?)";
                PreparedStatement preparedStatement = connection.prepareStatement(insertionQuery);
                preparedStatement.setString(1, words[i]);
                preparedStatement.setString(2, initialForm);
                preparedStatement.setString(3, word_type);
                preparedStatement.executeUpdate();
                // try
                preparedStatement.close();
            }
        }
    }

    private static boolean wordExists(Connection connection, String word) throws SQLException {
        // именованный парамет
        String query = "SELECT * FROM T_DICTIONARY WHERE WORD = ?";
        PreparedStatement preparedStatement = connection.prepareStatement(query);
        preparedStatement.setString(1, word);
        ResultSet result = preparedStatement.executeQuery();
        boolean answer = result.next();
        result.close();
        preparedStatement.close();
        return answer;
        //  try (даже 2)
    }

}