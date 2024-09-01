package pl.pla9er9.qwery;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

public class Node {

    private final char char_;
    private Object value;
    private boolean isStringEnding;
    private final Map<Character, Node> nodes;

    public Node(char char_, Object value, boolean isStringEnding) {
        this.char_ = char_;
        this.value = value;
        this.isStringEnding = isStringEnding;
        this.nodes = new ConcurrentHashMap<>();
    }

    public void addChildNode(Node node) {
        if (node == null) {
            return;
        }
        this.nodes.put(node.getChar(), node);
    }

    public void deleteChildNode(char char_) {
        this.nodes.remove(char_);
    }

    public Optional<Node> getChildNodeByChar(char char_) {
        var node = this.nodes.get(char_);
        return Optional.ofNullable(node);
    }

    public void setStringEnding(boolean stringEnding) {
        isStringEnding = stringEnding;
    }

    public Map<Character, Node> getAllChildNodes() {
        return nodes;
    }

    public char getChar() {
        return char_;
    }

    public boolean isStringEnding() {
        return isStringEnding;
    }

    public Object getValue() {
        return value;
    }

    public void setValue(Object value) {
        this.value = value;
    }
}
