package pl.pla9er9.qwery;

import org.springframework.stereotype.Component;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

@Component
public class Trie {
    private final Map<Character, Node> roots = new ConcurrentHashMap<>();

    public void add(String str, Object value) {
        if (str == null || str.isEmpty()) {
            return;
        }

        char c = str.charAt(0);
        Node n = roots.get(c);

        if (n == null) {
            Node newNode = new Node(c, value, str.length() == 1);
            roots.put(c, newNode);
            n = newNode;
        }

        add(str.substring(1), n, value);
    }

    private void add(String str, Node node, Object value) {
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
                childNode.setValue(value);
            }
            add(str, childNode, value);
        } else {
            var newNode = new Node(c, value, isLastCharacter);
            node.addChildNode(newNode);
            add(str, newNode, value);
        }
    }

    public Record[] getAll() {
        List<Record> arr = new ArrayList<>();
        roots.forEach((k, v) -> {
            String nodeChar = String.valueOf(v.getChar());
            List<Record> records = getAll(v, nodeChar);
            arr.addAll(records);
        });

        return arr.toArray(new Record[0]);
    }

    private List<Record> getAll(Node n, String s) {
        List<Record> arr = new ArrayList<>();

        if (n.isStringEnding()) {
            var record = new Record(s, n.getValue());
            arr.add(record);
        }
        n.getAllChildNodes().forEach((k, v) -> {
            var childNodes = getAll(v, s + v.getChar());
            arr.addAll(childNodes);
        });

        return arr;
    }

    public Record[] search(String str, int limit) {
        if (str == null || str.isEmpty()) {
            return new Record[]{};
        }

        var char_ = str.charAt(0);
        var node = this.roots.get(char_);
        if (node == null) {
            return new Record[]{};
        }

        if (str.length() > 1) {
            str = str.substring(1);
        }

        return search(node, str, String.valueOf(char_), limit);
    }

    private Record[] search(Node node, String str, String collected, int limit) {
        var results = new ArrayList<Record>();
        var char_ = str.charAt(0);
        var nodeOptional = node.getChildNodeByChar(char_);

        if (nodeOptional.isEmpty()) {
            var rest = getRecordsFromChildNodes(node, collected, limit);
            return rest.toArray(new Record[0]);
        }

        var childNode = nodeOptional.get();
        if (str.length() > 1) {
            return search(
                    childNode, str.substring(1), collected + childNode.getChar(), limit
            );
        }

        if (childNode.isStringEnding()) {
            var record = new Record(collected + node.getChar(), node.getValue());
            results.add(record);
            limit -= 1;
        }

        var rest = getRecordsFromChildNodes(childNode, collected + childNode.getChar(), limit);
        results.addAll(rest);

        return results.toArray(new Record[0]);
    }

    private List<Record> getRecordsFromChildNodes(Node node, String collected, int limit) {
        var limitAtomic = new AtomicInteger(limit);
        var result = new ArrayList<Record>();

        node.getAllChildNodes().forEach((k, v) -> {
            if (limitAtomic.get() <= 0) {
                return;
            }

            if (v.isStringEnding()) {
                var record = new Record(collected + v.getChar(), v.getValue());
                result.add(record);
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
                node.setValue(null);
            }
            return false;
        }
    }

    public void deleteAll() {
        this.roots.clear();
    }
}
