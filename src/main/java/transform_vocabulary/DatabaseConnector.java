package transform_vocabulary;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.sql.*;
import java.util.HashMap;
import java.util.Map;

/**
 * Author: katooshka
 * Date: 1/13/16.
 */
public class DatabaseConnector {

    public static Connection connection = null;

    //TODO: вынести коннекшн в отдельный файл

    public static void main(String[] args) throws ClassNotFoundException, SQLException, IOException {
        try {
            Class.forName("com.mysql.jdbc.Driver");
            connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/vocabulary", "translator", "3705352rfnz");
            putVocabularyIntoDatabase("past_verbs.txt", "past_verbs");
            printAllFromTable(connection);
        } finally {
            if (connection != null) {
                connection.close();
            }
        }
    }

    public static void putVocabularyIntoDatabase(String filename, String word_type) throws IOException, SQLException {
        if (tableExists()) {
            dropTable();
        }
        createTable();
        InputStream inputStream = ClassLoader.getSystemResourceAsStream(filename);
        try (BufferedReader br = new BufferedReader(new InputStreamReader(inputStream))) {
            for (String line = br.readLine(); line != null; line = br.readLine()) {
                String[] splitLine = line.split(" ");
                    putStringIntoDatabase(splitLine, word_type);
            }
        }
    }

    private static boolean tableExists() throws SQLException {
        String query = "SHOW TABLES LIKE 'T_DICTIONARY'";
        PreparedStatement preparedStatement = connection.prepareStatement(query);
        return preparedStatement.executeUpdate() != 0;
    }

    private static void dropTable() throws SQLException {
        String query = "DROP TABLE T_DICTIONARY";
        PreparedStatement preparedStatement = connection.prepareStatement(query);
        preparedStatement.executeUpdate();
    }

    private static void createTable() throws SQLException {
        String query = "CREATE TABLE T_DICTIONARY ( " +
                "WORD varchar(50) NOT NULL, INITIAL_FORM varchar(50) NOT NULL, " +
                "WORD_TYPE varchar(50) NOT NULL, UNIQUE KEY(WORD))";
        PreparedStatement preparedStatement = connection.prepareStatement(query);
        preparedStatement.executeUpdate();
    }

    private static void putStringIntoDatabase(String[] words, String word_type) throws SQLException {
        for (int i = 0; i < words.length; i++) {
            if (!wordExists(words[i])) {
                String insertionQuery = "INSERT INTO T_DICTIONARY (WORD, INITIAL_FORM, WORD_TYPE) VALUES (?, ?, ?)";
                PreparedStatement preparedStatement = connection.prepareStatement(insertionQuery);
                preparedStatement.setString(1, words[i]);
                preparedStatement.setString(2, words[0]);
                preparedStatement.setString(3, word_type);
                preparedStatement.executeUpdate();
                printAllFromTable(connection);
            }
        }
    }

    private static boolean wordExists(String word) throws SQLException {
//        try {
            String query = "SELECT * FROM T_DICTIONARY WHERE WORD = ?";
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setString(1, word);
            ResultSet result = preparedStatement.executeQuery();
            if (result != null) {
                return true;
            } else return false;
//        } catch (NullPointerException e) {
//            return false;
//        }

    }

    private static void printAllFromTable(Connection connection) throws SQLException {
        Statement statement = connection.createStatement();
        ResultSet result = statement.executeQuery("SELECT * FROM T_DICTIONARY");
        while (result.next()) {
            String sentence = result.getString("WORD");
            String author = result.getString("INITIAL_FORM");
            String tag = result.getString("WORD_TYPE");

            System.out.println("1 : " + sentence);
            System.out.println("2 : " + author);
            System.out.println("3 : " + tag);
            System.out.println();
        }
    }

    private static void executeInsertions(Connection connection) throws SQLException {
        insertNewRow(connection,
                "a",
                "b", "c");
    }

    private static void insertNewRow(Connection connection, String word, String inf, String type) throws SQLException {
        String insertionQuery = "INSERT INTO T_DICTIONARY (WORD, INITIAL_FORM, WORD_TYPE) VALUES (?, ?, ?)";
        PreparedStatement preparedStatement = connection.prepareStatement(insertionQuery);
        preparedStatement.setString(1, word);
        preparedStatement.setString(2, inf);
        preparedStatement.setString(3, type);
        preparedStatement.executeUpdate();
    }
}
