package Algorithms;

import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;


public class NodeResult {
    private final SimpleStringProperty node;
    private final SimpleIntegerProperty value;

    public NodeResult(String node, int value) {
        this.node = new SimpleStringProperty(node);
        this.value = new SimpleIntegerProperty(value);
    }

    public String getNode() { return node.get(); }
    public int getValue() { return value.get(); }
}
