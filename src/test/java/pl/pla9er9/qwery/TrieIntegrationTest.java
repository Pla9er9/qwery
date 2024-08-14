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

        System.out.println(Arrays.toString(result));
        Assertions.assertArrayEquals(strings, result);
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
