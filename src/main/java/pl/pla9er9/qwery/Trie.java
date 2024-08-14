package pl.pla9er9.qwery;

import org.springframework.stereotype.Component;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

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

    private  void add(String str, Node node) {
        if (str.isEmpty()) {
            return;
        }

        var c = str.charAt(0);
        var isLastCharacter = str.length() == 1;
        var childNodeOptional = node.getChildNodeByChar(c);
        str =  str.substring(1);

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
            arr.addAll(getAll(v,  String.valueOf(v.getChar())));
        });

        return arr.toArray(new String[0]);
    }

    private List<String> getAll(Node n, String s) {
        List<String> arr = new ArrayList<>();

        if (n.isStringEnding()) {
            arr.add(s);
        }
        n.getAllChildNodes().forEach((k, v) -> {
            arr.addAll(getAll(v, s + v.getChar()));
        });

        return arr;
    }

    public String[] search(String str, int limit) {
        if (str == null || str.isEmpty()) {
           return new String[]{};
        }

        limit = Math.min(limit, 35);

        var char_ = str.charAt(0);
        var node = this.roots.get(char_);
        if (node == null) {
            return new String[]{};
        }

        return search(node, str.substring(1),  String.valueOf(char_));
    }

    private String[] search(Node node, String str, String collected) {
        var char_ = str.charAt(0);
        var nodeOptional = node.getChildNodeByChar(char_);
        if (nodeOptional.isEmpty()) {
            return new String[]{};
        }

        var childNode = nodeOptional.get();
        if (str.length() > 1) {
            return search(
                    childNode, str.substring(1), collected + childNode.getChar()
            );
        }

        if (node.isStringEnding()) {
            return new String[]{collected + node.getChar()};
        }

        var results = new ArrayList<String>();
        childNode.getAllChildNodes().forEach((k, n) -> {
            var v = getRestOfRecord(n, collected + char_);
            if (v != null) {
                results.add(v);
            }
        });

        return results.toArray(new String[0]);
    }


//        if (str.length() == 1) {
//            if (node.isStringEnding() && limit == 1) {
//                return new String[]{orginal };
//            }
//
//            node.getAllChildNodes().forEach((k, n) -> {
//                results.add(getRestOfRecord(n, orginal));
//            });
//
//            return results.toArray(new String[0]);
//        } else {
//            var nodeOptional = node.getChildNodeByChar(char_);
//            if (nodeOptional.isEmpty()) {
//                return new String[]{};
//            }
//            return search(nodeOptional.get(), str.substring(1), orginal, limit);
//        }

    private String getRestOfRecord(Node node, String collected) {
        if (node.isStringEnding()) {
            return collected + node.getChar();
        }

        var childNodes = node.getAllChildNodes().values();
        var firstChildOptional = childNodes.stream().findFirst();

        if (firstChildOptional.isEmpty()) {
            return null;
        }

        var firstChild = firstChildOptional.get();

        return getRestOfRecord(firstChild, collected + firstChild.getChar());
    }


    /**
     * @return if string was found and deleted
     */
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
        if (str.length() == 0) {
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

        if (node.getAllChildNodes().size() == 0) {
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

//    public void print() {
//        print(this.roots);
//        System.out.println();
//    }
//
//    private void print(Map<Character, Node> map) {
//        map.forEach((k, v) -> {
//            System.out.print(v.getChar());
//            print(v.getAllChildNodes());
//        });
//    }
}
