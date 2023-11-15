package dictionary.server;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.SQLIntegrityConstraintViolationException;
import java.util.ArrayList;

public class DatabaseDictionary extends Dictionary {
    private static final String HOST_NAME = "localhost";
    private static final String DB_NAME = "en-vi-dictionary";
    private static final String USER_NAME = "en-vi-dictionary";
    private static final String PASSWORD = "n1-02-dictionary";
    private static final String PORT = "3306";
    private static final String MYSQL_URL =
            "jdbc:mysql://" + HOST_NAME + ":" + PORT + "/" + DB_NAME;

    private static Connection connection = null;

    /**
     * Đóng kết nối đến cơ sở dữ liệu MYSQL.
     *
     * @param connection Biến kết nối
     */
    private static void close(Connection connection) {
        try {
            if (connection != null) {
                connection.close();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Đóng PreparedStatement ps.
     *
     * @param ps PreparedStatement cần đóng
     */
    private static void close(PreparedStatement ps) {
        try {
            if (ps != null) {
                ps.close();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Đóng ResultSet rs.
     *
     * @param rs ResultSet cần đóng
     */
    private static void close(ResultSet rs) {
        try {
            if (rs != null) {
                rs.close();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Kết nối đến cơ sở dữ liệu MYSQL.
     *
     * <p>Tham khảo: https://stackoverflow.com/questions/2839321/connect-java-to-a-mysql-database
     */
    private void connectToDatabase() throws SQLException {
        System.out.println("Đang kết nối đến cơ sở dữ liệu...");
        connection = DriverManager.getConnection(MYSQL_URL, USER_NAME, PASSWORD);
        System.out.println("Đã kết nối đến cơ sở dữ liệu!\n");
    }

    /** Kết nối đến cơ sở dữ liệu MYSQL. Thêm tất cả từ điển từ cơ sở dữ liệu vào cấu trúc dữ liệu Trie. */
    @Override
    public void initialize() throws SQLException {
        connectToDatabase();
        ArrayList<String> targets = getAllWordTargets();
        for (String word : targets) {
            Trie.insert(word);
        }
    }

    /** Đóng kết nối đến cơ sở dữ liệu. */
    @Override
    public void close() {
        close(connection);
        System.out.println("Đã ngắt kết nối với cơ sở dữ liệu!");
    }

    /**
     * Tra cứu từ tiếng Anh `target` trong cơ sở dữ liệu (tìm từ chính xác `target`).
     *
     * @param target từ cần tìm kiếm (toàn bộ từ)
     * @return định nghĩa tiếng Việt của `target`, nếu không tìm thấy trả về "404" dưới dạng String.
     */
    @Override
    public String lookUpWord(final String target) {
        final String SQL_QUERY = "SELECT definition FROM dictionary WHERE target = ?";
        try {
            PreparedStatement ps = connection.prepareStatement(SQL_QUERY);
            ps.setString(1, target);
            try {
                ResultSet rs = ps.executeQuery();
                try {
                    if (rs.next()) {
                        return rs.getString("definition");
                    } else {
                        return "404";
                    }
                } finally {
                    close(rs);
                }
            } finally {
                close(ps);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return "404";
    }

    /**
     * Chèn từ mới vào cơ sở dữ liệu.
     *
     * @param target Từ tiếng Anh
     * @param definition Định nghĩa tiếng Việt
     * @return true nếu `target` chưa được thêm, ngược lại trả về false
     */
    @Override
    public boolean insertWord(final String target, final String definition) {
        final String SQL_QUERY = "INSERT INTO dictionary (target, definition) VALUES (?, ?)";
        try {
            PreparedStatement ps = connection.prepareStatement(SQL_QUERY);
            ps.setString(1, target);
            ps.setString(2, definition);
            try {
                ps.executeUpdate();
            } catch (SQLIntegrityConstraintViolationException e) {
                // `word` đã có trong cơ sở dữ liệu
                return false;
            } finally {
                close(ps);
            }
            Trie.insert(target);
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Xóa từ `target` khỏi cơ sở dữ liệu.
     *
     * <p>Không có gì xảy ra nếu `target` không có trong cơ sở dữ liệu để xóa.
     *
     * @param target từ đã xóa
     * @return true nếu xóa thành công, ngược lại trả về false
     */
    @Override
    public boolean deleteWord(final String target) {
        final String SQL_QUERY = "DELETE FROM dictionary WHERE target = ?";
        try {
            PreparedStatement ps = connection.prepareStatement(SQL_QUERY);
            ps.setString(1, target);
            try {
                int deletedRows = ps.executeUpdate();
                if (deletedRows == 0) {
                    return false;
                }
            } finally {
                close(ps);
            }
            Trie.delete(target);
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Cập nhật từ `target` với định nghĩa tương ứng.
     *
     * <p>Không có gì xảy ra nếu `target` không có trong cơ sở dữ liệu để cập nhật.
     *
     * @param target từ đã cập nhật
     * @param definition định nghĩa đã cập nhật
     * @return true nếu cập nhật thành công, ngược lại trả về false
     */
    @Override
    public boolean updateWordDefinition(final String target, final String definition) {
        final String SQL_QUERY = "UPDATE dictionary SET definition = ? WHERE target = ?";
        try {
            PreparedStatement ps = connection.prepareStatement(SQL_QUERY);
            ps.setString(1, definition);
            ps.setString(2, target);
            try {
                int updatedRows = ps.executeUpdate();
                if (updatedRows == 0) {
                    return false;
                }
            } finally {
                close(ps);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return true;
    }

    /**
     * Lấy tất cả từ trong kết quả của câu truy vấn SQL được chỉ định.
     *
     * @param ps câu truy vấn SQL được bao gồm trong PreparedStatement
     * @return ArrayList của Words
     * @throws SQLException exception
     */
    private ArrayList<Word> getWordsFromResultSet(PreparedStatement ps) throws SQLException {
        try {
            ResultSet rs = ps.executeQuery();
            try {
                ArrayList<Word> words = new ArrayList<>();
                while (rs.next()) {
                    words.add(new Word(rs.getString(2), rs.getString(3)));
                }
                return words;

            } finally {
                close(rs);
            }
        } finally {
            close(ps);
        }
    }

    /**
     * Lấy tất cả từ vào một `ArrayList(Word)>`.
     *
     * @return một 'ArrayList(Word)' chứa tất cả các từ từ cơ sở dữ liệu
     */
    @Override
    public ArrayList<Word> getAllWords() {
        final String SQL_QUERY = "SELECT * FROM dictionary";
        try {
            PreparedStatement ps = connection.prepareStatement(SQL_QUERY);
            return getWordsFromResultSet(ps);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return new ArrayList<>();
    }

    /**
     * Lấy tất cả các từ từ Cơ sở dữ liệu có `id` từ `wordIndexFrom` đến `wordIndexTo`.
     *
     * @param wordIndexFrom giới hạn bên trái
     * @param wordIndexTo giới hạn bên phải
     * @return một ArrayList của Word lấy từ cơ sở dữ liệu
     */
    public ArrayList<Word> getWordsPartial(int wordIndexFrom, int wordIndexTo) {
        final String SQL_QUERY = "SELECT * FROM dictionary WHERE id >= ? AND id <= ?";
        try {
            PreparedStatement ps = connection.prepareStatement(SQL_QUERY);
            ps.setInt(1, wordIndexFrom);
            ps.setInt(2, wordIndexTo);
            return getWordsFromResultSet(ps);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return new ArrayList<>();
    }

    /**
     * Lấy tất cả các từ chỉ có target từ cơ sở dữ liệu (chỉ target, không bao gồm định nghĩa).
     *
     * @return ArrayList chuỗi các từ target
     */
    @Override
    public ArrayList<String> getAllWordTargets() {
        final String SQL_QUERY = "SELECT * FROM dictionary";
        try {
            PreparedStatement ps = connection.prepareStatement(SQL_QUERY);
            try {
                ResultSet rs = ps.executeQuery();
                try {
                    ArrayList<String> targets = new ArrayList<>();
                    while (rs.next()) {
                        targets.add(rs.getString(2));
                    }
                    return targets;

                } finally {
                    close(rs);
                }
            } finally {
                close(ps);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return new ArrayList<>();
    }
}
