package dictionary.server;

import java.util.ArrayList;
import java.util.Map;
import java.util.TreeMap;

public class Trie {

    private static final ArrayList<String> searchedWords = new ArrayList<>();
    private static final TrieNode root = new TrieNode();

    public static ArrayList<String> getSearchedWords() {
        return searchedWords;
    }

    /**
     * Chèn từ `target` vào Trie DS.
     *
     * @param target từ cần chèn
     */
    public static void insert(String target) {
        int length = target.length();

        TrieNode pCrawl = root;

        for (int level = 0; level < length; level++) {
            char index = target.charAt(level);

            if (pCrawl.children.get(index) == null) {
                pCrawl.children.put(index, new TrieNode());
            }

            pCrawl = pCrawl.children.get(index);
        }

        // Đặt `target` kết thúc tại pCrawl
        pCrawl.isEndOfWord = true;
    }

    /**
     * Lấy tất cả các từ kết thúc trong cây con của nút `pCrawl`.
     *
     * @param pCrawl nút hiện tại
     * @param target từ hiện tại mà `pCrawl` đại diện
     */
    private static void dfsGetWordsSubtree(TrieNode pCrawl, String target) {
        if (pCrawl.isEndOfWord) {
            searchedWords.add(target);
        }
        for (char index : pCrawl.children.keySet()) {
            if (pCrawl.children.get(index) != null) {
                dfsGetWordsSubtree(pCrawl.children.get(index), target + index);
            }
        }
    }

    /**
     * Tìm kiếm tất cả các từ bắt đầu bằng `prefix` trong Trie.
     *
     * @param prefix tiền tố cần tìm kiếm
     * @return một ArrayList của String chứa các từ bắt đầu bằng `prefix`
     */
    public static ArrayList<String> search(String prefix) {
        if (prefix.isEmpty()) {
            return new ArrayList<>();
        }
        searchedWords.clear();
        int length = prefix.length();
        TrieNode pCrawl = root;

        for (int level = 0; level < length; level++) {
            char index = prefix.charAt(level);

            if (pCrawl.children.get(index) == null) {
                return getSearchedWords();
            }

            pCrawl = pCrawl.children.get(index);
        }
        dfsGetWordsSubtree(pCrawl, prefix);
        return getSearchedWords();
    }

    /**
     * Xóa từ `target` khỏi Trie DS.
     *
     * @param target từ cần xóa
     */
    public static void delete(String target) {
        int length = target.length();

        TrieNode pCrawl = root;

        for (int level = 0; level < length; level++) {
            char index = target.charAt(level);
            if (pCrawl.children.get(index) == null) {
                System.out.println("Từ này chưa được chèn vào Trie");
                return;
            }
            pCrawl = pCrawl.children.get(index);
        }
        if (!pCrawl.isEndOfWord) {
            System.out.println("Từ này chưa được chèn vào Trie");
            return;
        }

        pCrawl.isEndOfWord = false;
    }

    /** Một Node trên Trie DS. */
    public static class TrieNode {
        Map<Character, TrieNode> children = new TreeMap<>();
        /* isEndOfWord là true nếu nút đại diện cho kết thúc của một từ */
        boolean isEndOfWord;

        TrieNode() {
            isEndOfWord = false;
        }
    }
}
