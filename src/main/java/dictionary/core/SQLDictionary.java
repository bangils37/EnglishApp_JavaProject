package dictionary.core;

import java.sql.*;
import java.util.ArrayList;

public class SQLDictionary extends Dictionary {

    private static Connection connection = null;

    private static void closeResource(AutoCloseable resource) {
        try {
            if (resource != null) {
                resource.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void connectToDatabase() throws SQLException {
        System.out.println("Connecting to database...");
        connection = DriverManager.getConnection(Config.MYSQL_URL, Config.USER_NAME, Config.PASSWORD);
        System.out.println("Database connected!\n");
    }

    private void initializeTrie() throws SQLException {
        System.out.println("Initializing Trie DS...");
        ArrayList<Word> words = getAllWords();
        for (Word word : words) {
            Trie.getInstance().insert(word.getName());
        }
        System.out.println("Trie DS initialized!\n");
    }

    private ArrayList<Word> executeQueryAndGetWords(String query, Object... parameters) {
        try (PreparedStatement ps = connection.prepareStatement(query)) {
            setParameters(ps, parameters);
            try (ResultSet rs = ps.executeQuery()) {
                return getWordsFromResultSet(rs);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return new ArrayList<>();
    }

    private void setParameters(PreparedStatement ps, Object... parameters) throws SQLException {
        for (int i = 0; i < parameters.length; i++) {
            ps.setObject(i + 1, parameters[i]);
        }
    }

    private ArrayList<Word> getWordsFromResultSet(ResultSet rs) throws SQLException {
        ArrayList<Word> words = new ArrayList<>();
        while (rs.next()) {
            words.add(new Word(rs.getString("target"), rs.getString("definition")));
        }
        return words;
    }

    private boolean executeUpdateAndHandleIntegrityViolation(String query, Object... parameters) {
        try (PreparedStatement ps = connection.prepareStatement(query)) {
            setParameters(ps, parameters);
            try {
                ps.executeUpdate();
                return true;
            } catch (SQLIntegrityConstraintViolationException e) {
                return false; // Word already in database
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    private boolean executeUpdateAndCheckRowsAffected(String query, Object... parameters) {
        try (PreparedStatement ps = connection.prepareStatement(query)) {
            setParameters(ps, parameters);
            int affectedRows = ps.executeUpdate();
            return affectedRows != 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    private ArrayList<String> getAllWordTargetsFromDatabase() {
        final String SQL_QUERY = "SELECT * FROM dictionary";
        return executeQueryAndGetTargets(SQL_QUERY);
    }

    private ArrayList<String> executeQueryAndGetTargets(String query, Object... parameters) {
        try (PreparedStatement ps = connection.prepareStatement(query)) {
            setParameters(ps, parameters);
            try (ResultSet rs = ps.executeQuery()) {
                return getTargetsFromResultSet(rs);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return new ArrayList<>();
    }

    private ArrayList<String> executeQueryAndGetDifinitions(String query, Object... parameters) {
        try (PreparedStatement ps = connection.prepareStatement(query)) {
            setParameters(ps, parameters);
            try (ResultSet rs = ps.executeQuery()) {
                return getDifinitionsFromResultSet(rs);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return new ArrayList<>();
    }

    private ArrayList<String> getTargetsFromResultSet(ResultSet rs) throws SQLException {
        ArrayList<String> targets = new ArrayList<>();
        while (rs.next()) {
            targets.add(rs.getString("target"));
        }
        return targets;
    }

    private ArrayList<String> getDifinitionsFromResultSet(ResultSet rs) throws SQLException {
        ArrayList<String> definitions = new ArrayList<>();
        while (rs.next()) {
            definitions.add(rs.getString("definition"));
        }
        return definitions;
    }

    private static void closeConnection() {
        closeResource(connection);
        System.out.println("Database disconnected!");
    }

    @Override
    public void initialize() throws SQLException {
        connectToDatabase();
        initializeTrie();
    }

    @Override
    public String lookUpWord(final String target) {
        final String SQL_QUERY = "SELECT definition FROM dictionary WHERE target = ?";
        ArrayList<String> definitions = executeQueryAndGetDifinitions(SQL_QUERY, target);

        if (definitions.isEmpty()) {
            return "404";
        }

        String definition = definitions.get(0);

        return definition;
    }

    @Override
    public boolean insertWord(final String target, final String definition) {
        final String SQL_QUERY = "INSERT INTO dictionary (target, definition) VALUES (?, ?)";
        return executeUpdateAndHandleIntegrityViolation(SQL_QUERY, target, definition);
    }

    @Override
    public boolean deleteWord(final String target) {
        final String SQL_QUERY = "DELETE FROM dictionary WHERE target = ?";
        return executeUpdateAndCheckRowsAffected(SQL_QUERY, target);
    }

    @Override
    public boolean updateWordDefinition(final String target, final String definition) {
        final String SQL_QUERY = "UPDATE dictionary SET definition = ? WHERE target = ?";
        return executeUpdateAndCheckRowsAffected(SQL_QUERY, definition, target);
    }

    @Override
    public ArrayList<Word> getAllWords() {
        final String SQL_QUERY = "SELECT * FROM dictionary";
        return executeQueryAndGetWords(SQL_QUERY);
    }

    public ArrayList<Word> getWordsPartial(int wordIndexFrom, int wordIndexTo) {
        final String SQL_QUERY = "SELECT * FROM dictionary WHERE id BETWEEN ? AND ?";
        return executeQueryAndGetWords(SQL_QUERY, wordIndexFrom, wordIndexTo);
    }

    @Override
    public String lookUpWordByIDGetDefinition(int iD) {
        final String SQL_QUERY = "SELECT definition FROM dictionary WHERE id = ?";
        ArrayList<String> definitions = executeQueryAndGetDifinitions(SQL_QUERY, iD);

        if (definitions.isEmpty()) {
            return "404";
        }

        String definition = definitions.get(0);

        return definition;
    }

    @Override
    public String lookUpWordByIDGetTarget(int iD) {
        final String SQL_QUERY = "SELECT target FROM dictionary WHERE id = ?";
        ArrayList<String> targets = executeQueryAndGetTargets(SQL_QUERY, iD);

        if (targets.isEmpty()) {
            return "404";
        }

        String target = targets.get(0);

        return target;
    }

    @Override
    public ArrayList<String> getAllWordTargets() {
        return getAllWordTargetsFromDatabase();
    }

    @Override
    public void close() {
        closeConnection();
    }
}
