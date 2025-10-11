package GraphVisualizer;

import javafx.scene.shape.Circle;

public class CurrentlyPressedNodeHelper {

    private static Circle currentCircle = null;
    private static Graph.GraphNode currentNode = null;

    private static Runnable onFocusChange;

    public static void setCurrentNode(Graph.GraphNode node) {
        if (node == currentNode) return; // same node, do nothing

        currentNode = node;
        currentCircle = (node != null) ? node.getCircle() : null;

        if (node != null) {
            System.out.println("Node " + node.getNodeLabel() + " is now selected.");
        } else {
            System.out.println("Node deselected.");
        }

        if (onFocusChange != null) {
            onFocusChange.run();
        }
    }

    public static Graph.GraphNode getCurrentNode() {
        return currentNode;
    }

    public static Circle getCurrentCircle() {
        return currentCircle;
    }

    public static boolean hasFocus() {
        return currentNode != null;
    }

    public static void setOnFocusChange(Runnable callback) {
        onFocusChange = callback;
    }
}
