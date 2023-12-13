package dictionary.core;

import java.util.ArrayList;
import java.util.Map;
import java.util.TreeMap;

/**
 * Design partern: Singleton
 */
public class Trie {

    private static Trie instance = null;

    private final ArrayList<String> searchedWords = new ArrayList<>();
    private final TrieNode root = new TrieNode();

    public static class TrieNode {
        Map<Character, TrieNode> children = new TreeMap<>();
        boolean isEndOfWord;

        TrieNode() {
            isEndOfWord = false;
        }
    }

    private Trie() {
    }

    /**
     * Lấy đối tượng của Trie DS.
     *
     * @return đối tượng của Trie DS
     */
    public static Trie getInstance() {
        if (instance == null) {
            instance = new Trie();
        }
        return instance;
    }

    public ArrayList<String> getSearchedWords() {
        return searchedWords;
    }

    /**
     * Chèn từ `target` vào Trie DS.
     *
     * @param target từ để chèn
     */
    public void insert(String target) {
        TrieNode lastNode = traverseAndInsert(target);
        if (lastNode != null) {
            lastNode.isEndOfWord = true;
        }
    }

    private TrieNode traverseAndInsert(String target) {
        int length = target.length();
        TrieNode pCrawl = root;

        for (int level = 0; level < length; level++) {
            char index = target.charAt(level);
            pCrawl = pCrawl.children.computeIfAbsent(index, k -> new TrieNode());
        }

        return pCrawl;
    }

    /**
     * Tìm kiếm tất cả các từ bắt đầu bằng `prefix` trong Trie.
     *
     * @param prefix tiền tố cần tìm kiếm
     * @return ArrayList chứa các từ bắt đầu bằng `prefix`
     */
    public ArrayList<String> search(String prefix) {
        if (prefix.isEmpty()) {
            return new ArrayList<>();
        }

        searchedWords.clear();
        TrieNode lastNode = traverse(prefix);

        if (lastNode != null) {
            dfsGetWordsSubtree(lastNode, prefix);
        }

        return getSearchedWords();
    }

    private TrieNode traverse(String prefix) {
        int length = prefix.length();
        TrieNode pCrawl = root;

        for (int level = 0; level < length; level++) {
            char index = prefix.charAt(level);
            pCrawl = pCrawl.children.get(index);

            if (pCrawl == null) {
                return null;
            }
        }

        return pCrawl;
    }

    /**
     * Xóa từ `target` khỏi Trie DS.
     *
     * @param target từ cần xóa
     */
    public void delete(String target) {
        TrieNode lastNode = traverse(target);

        if (lastNode != null && lastNode.isEndOfWord) {
            lastNode.isEndOfWord = false;
        } else {
            System.out.println("Từ này chưa được chèn vào Trie");
        }
    }

    private void dfsGetWordsSubtree(TrieNode pCrawl, String target) {
        if (pCrawl.isEndOfWord) {
            searchedWords.add(target);
        }
        for (char index : pCrawl.children.keySet()) {
            dfsGetWordsSubtree(pCrawl.children.get(index), target + index);
        }
    }
}
