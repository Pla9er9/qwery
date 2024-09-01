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
            trie.add(str, null);
        }

        String[] result = recordsToKeys(trie.getAll());
        Arrays.sort(result);

        Assertions.assertArrayEquals(strings, result);
    }

    @Test
    public void testSearch() {
        Trie trie = new Trie();
        String[] strings = new String[]{"item1", "item2"};

        for (String str : strings) {
            trie.add(str, null);
        }

        var result = recordsToKeys(trie.search("item", 2));

        Assertions.assertArrayEquals(strings, result);
    }

    @Test
    public void testDeepSearch() {
        Trie trie = new Trie();
        String[] strings = new String[]{"abc", "abc23", "abc1", "abc11", "abc111", "abc112", "abc12", "abc123", "abc124"};
        Arrays.sort(strings);

        for (String str : strings) {
            trie.add(str, null);
        }

        var result = recordsToKeys(trie.search("a", 100));
        Arrays.sort(result);

        Assertions.assertArrayEquals(strings, result);
    }

    @Test
    public void testSearchLimit() {
        Trie trie = new Trie();
        String[] strings = new String[]{"abc", "abc23", "abc1", "abc11", "abc111", "abc112", "abc12", "abc123", "abc124"};
        Arrays.sort(strings);

        for (String str : strings) {
            trie.add(str, null);
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
            trie.add(str, null);
        }

        trie.add("item3", null);
        trie.delete("item3");

        String[] result = recordsToKeys(trie.getAll());
        Arrays.sort(result);

        Assertions.assertArrayEquals(strings, result);
    }

    @Test
    public void testDeleteAll() {
        Trie trie = new Trie();
        String[] strings = new String[]{"item1", "item2", "item3"};

        for (String str : strings) {
            trie.add(str, null);
        }

        trie.deleteAll();
        String[] result = recordsToKeys(trie.getAll());

        Assertions.assertEquals(0, result.length);
    }

    private String[] recordsToKeys(Record[] records) {
        return Arrays.stream(records).map(Record::key).toArray(String[]::new);
    }
}
