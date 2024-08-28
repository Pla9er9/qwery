package pl.pla9er9.qwery;

import org.springframework.stereotype.Component;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

@Component
public class Trie {
    private final Map<Character, Node> roots = new ConcurrentHashMap<>();

    public void add(String str) {
        if (str == null || str.isEmpty()) {
            return;
        }

        char c = str.charAt(0);
        Node n = roots.get(c);

        if (n == null) {
            Node newNode = new Node(c, str.length() == 1);
            roots.put(c, newNode);
            n = newNode;
        }

        add(str.substring(1), n);
    }

    private void add(String str, Node node) {
        if (str.isEmpty()) {
            return;
        }

        var c = str.charAt(0);
        var isLastCharacter = str.length() == 1;
        var childNodeOptional = node.getChildNodeByChar(c);
        str = str.substring(1);

        if (childNodeOptional.isPresent()) {
            var childNode = childNodeOptional.get();
            if (isLastCharacter) {
                childNode.setStringEnding(true);
            }
            add(str, childNode);
        } else {
            var newNode = new Node(c, isLastCharacter);
            node.addChildNode(newNode);
            add(str, newNode);
        }
    }

    public String[] getAll() {
        List<String> arr = new ArrayList<>();
        roots.forEach((k, v) -> {
            String nodeChar = String.valueOf(v.getChar());
            List<String> fullString = getAll(v, nodeChar);
            arr.addAll(fullString);
        });

        return arr.toArray(new String[0]);
    }

    private List<String> getAll(Node n, String s) {
        List<String> arr = new ArrayList<>();

        if (n.isStringEnding()) {
            arr.add(s);
        }
        n.getAllChildNodes().forEach((k, v) -> {
            var childNodes = getAll(v, s + v.getChar());
            arr.addAll(childNodes);
        });

        return arr;
    }

    public String[] search(String str, int limit) {
        if (str == null || str.isEmpty()) {
            return new String[]{};
        }

        var char_ = str.charAt(0);
        var node = this.roots.get(char_);
        if (node == null) {
            return new String[]{};
        }

        if (str.length() > 1) {
            str = str.substring(1);
        }

        return search(node, str, String.valueOf(char_), limit);
    }

    private String[] search(Node node, String str, String collected, int limit) {
        var results = new ArrayList<String>();
        var char_ = str.charAt(0);
        var nodeOptional = node.getChildNodeByChar(char_);

        if (nodeOptional.isEmpty()) {
            var rest = getRecordsFromChildNodes(node, collected, limit);
            return rest.toArray(new String[0]);
        }

        var childNode = nodeOptional.get();
        if (str.length() > 1) {
            return search(
                    childNode, str.substring(1), collected + childNode.getChar(), limit
            );
        }

        if (childNode.isStringEnding()) {
            results.add(collected + node.getChar());
            limit -= 1;
        }

        var rest = getRecordsFromChildNodes(childNode, collected + childNode.getChar(), limit);
        results.addAll(rest);

        return results.toArray(new String[0]);
    }

    private List<String> getRecordsFromChildNodes(Node node, String collected, int limit) {
        var limitAtomic = new AtomicInteger(limit);
        var result = new ArrayList<String>();

        node.getAllChildNodes().forEach((k, v) -> {
            if (limitAtomic.get() <= 0) {
                return;
            }

            if (v.isStringEnding()) {
                result.add(collected + v.getChar());
                limitAtomic.decrementAndGet();
            }

            var found = getRecordsFromChildNodes(v, collected + v.getChar(), limitAtomic.get());
            result.addAll(found);
            limitAtomic.updateAndGet(e -> e - found.size());
        });

        return result;
    }

    public void delete(String str) {
        if (str == null || str.isEmpty()) {
            return;
        }

        var c = str.charAt(0);
        var node = this.roots.get(c);

        var shouldDelete = delete(str, node);
        if (shouldDelete) {
            this.roots.remove(c);
        }
    }

    private boolean delete(String str, Node node) {
        if (str.isEmpty()) {
            return false;
        }

        if (str.length() > 1) {
            str = str.substring(1);
        }

        var c = str.charAt(0);
        var nodeOptional = node.getChildNodeByChar(c);

        if (nodeOptional.isPresent()) {
            var shouldDelete = delete(str, nodeOptional.get());
            if (shouldDelete) {
                node.deleteChildNode(c);
            }
        }

        if (node.getAllChildNodes().isEmpty()) {
            return true;
        } else {
            if (str.length() == 1) {
                node.setStringEnding(false);
            }
            return false;
        }
    }

    public void deleteAll() {
        this.roots.clear();
    }
}
