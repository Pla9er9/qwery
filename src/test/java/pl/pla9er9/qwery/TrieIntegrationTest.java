package pl.pla9er9.qwery;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.Arrays;

public class TrieIntegrationTest {
    @Test
    public void testAddToTree() {
        Trie trie = new Trie();
        String[] strings = new String[]{"abc", "abcd", "lol", "xpp"};
        Arrays.sort(strings);

        for (String str : strings) {
            trie.add(str);
        }

        String[] result = trie.getAll();
        Arrays.sort(result);

        Assertions.assertArrayEquals(strings, result);
    }

    @Test
    public void testSearch() {
        Trie trie = new Trie();
        String[] strings = new String[]{"item1", "item2"};

        for (String str : strings) {
            trie.add(str);
        }

        var result = trie.search("item", 2);

        Assertions.assertArrayEquals(strings, result);
    }

    @Test
    public void testDeepSearch() {
        Trie trie = new Trie();
        String[] strings = new String[]{"abc", "abc23", "abc1", "abc11", "abc111", "abc112", "abc12", "abc123", "abc124"};
        Arrays.sort(strings);

        for (String str : strings) {
            trie.add(str);
        }

        var result = trie.search("a", 100);
        Arrays.sort(result);

        Assertions.assertArrayEquals(strings, result);
    }

    @Test
    public void testSearchLimit() {
        Trie trie = new Trie();
        String[] strings = new String[]{"abc", "abc23", "abc1", "abc11", "abc111", "abc112", "abc12", "abc123", "abc124"};
        Arrays.sort(strings);

        for (String str : strings) {
            trie.add(str);
        }

        for (int limit = 0; limit < strings.length; limit++) {
            var result = trie.search("a", limit);
            Assertions.assertEquals(limit, result.length);
        }
    }

    @Test
    public void testDelete() {
        Trie trie = new Trie();
        String[] strings = new String[]{"item1", "item2"};

        for (String str : strings) {
            trie.add(str);
        }

        trie.add("item3");
        trie.delete("item3");

        String[] result = trie.getAll();
        Arrays.sort(result);

        Assertions.assertArrayEquals(strings, result);
    }

    @Test
    public void testDeleteAll() {
        Trie trie = new Trie();
        String[] strings = new String[]{"item1", "item2", "item3"};

        for (String str : strings) {
            trie.add(str);
        }

        trie.deleteAll();
        String[] result = trie.getAll();

        Assertions.assertEquals(0, result.length);
    }
}
