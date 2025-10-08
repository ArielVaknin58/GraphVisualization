package GraphVisualizer;

import Exceptions.InvalidEdgeException;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.text.Text;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.Random;

public class Graph {

    private final ArrayList<GraphNode> V = new ArrayList<>();
    private final Hashtable<String, GraphNode> VerticeIndexer = new Hashtable<>();
    private final ArrayList<ArrowEdge> E = new ArrayList<>();

    public static final double nodeRadius = 25;
    public static final double nodeLabelPadding = 5;
    public static final double EdgeWidth = 2;


    // ------------------- Node Class -------------------
    public class GraphNode {
        private Group nodeObject;
        private Circle circle;
        private Text label;
        private List<ArrowEdge> connectedEdges = new ArrayList<>();
        private String nodeLabel;

        public GraphNode(double x, double y, String textLabel) {
            circle = new Circle(x, y, nodeRadius, Color.LIGHTBLUE);
            circle.setStroke(Color.DARKBLUE);

            label = new Text(x - nodeLabelPadding, y + nodeLabelPadding, textLabel);
            label.setFill(Color.BLACK);

            nodeLabel = textLabel;
            nodeObject = new Group(circle, label);

            initDragHandlers();
        }

        private void initDragHandlers() {
            circle.setOnMouseClicked(event -> {
                LoggerManager.Logger().fine("Clicked node " + nodeLabel);
            });

            circle.setOnMousePressed(event -> {
                circle.setFill(Color.GRAY);
                circle.setUserData(new double[]{event.getSceneX(), event.getSceneY(),
                        circle.getCenterX(), circle.getCenterY()});
            });

            circle.setOnMouseReleased(event -> {circle.setFill(Color.LIGHTBLUE);});

            circle.setOnMouseDragged(event -> {
                double[] data = (double[]) circle.getUserData();
                double deltaX = event.getSceneX() - data[0];
                double deltaY = event.getSceneY() - data[1];

                circle.setCenterX(data[2] + deltaX);
                circle.setCenterY(data[3] + deltaY);

                // Move label along with circle
                label.setX(circle.getCenterX() - nodeLabelPadding);
                label.setY(circle.getCenterY() + nodeLabelPadding);

                // Update connected edges
                for (ArrowEdge edge : connectedEdges) {
                    edge.updatePosition();
                }
            });
        }

        public void addConnectedEdge(ArrowEdge edge) {
            connectedEdges.add(edge);
        }

        public Circle getCircle() {
            return circle;
        }

        public Group getNodeObject() {
            return nodeObject;
        }

        public String getNodeLabel() {
            return nodeLabel;
        }
    }

    // ------------------- Graph Methods -------------------
    public GraphNode createNode(String label) {
        Random rand = new Random();
        int x = rand.nextInt(1,14);
        int y = rand.nextInt(1,12);
        GraphNode node = new GraphNode(50 + 50*x, 50+50*y, label);
        V.add(node);
        VerticeIndexer.put(label, node);
        return node;
    }

    public void createEdge(String fromLabel, String toLabel) throws InvalidEdgeException {
        GraphNode fromNode = VerticeIndexer.get(fromLabel);
        GraphNode toNode = VerticeIndexer.get(toLabel);
        if(fromNode == null || toNode == null)
            throw new InvalidEdgeException();

        ArrowEdge edge = new ArrowEdge(fromNode, toNode);

        // Add edge to nodes
        fromNode.addConnectedEdge(edge);
        if (fromNode != toNode)
            toNode.addConnectedEdge(edge);

        E.add(edge);

        // Optional click event on edge
        edge.getEdgeGroup().setOnMouseClicked(e -> {
            System.out.println("Clicked edge " + fromLabel + " -> " + toLabel);
        });
    }

    public Node findNodeByLabel(String label) {
        return VerticeIndexer.get(label).getNodeObject();
    }

    public void addGraphToGroup(Group root) {
        // Add edges first so nodes appear on top
        for (ArrowEdge edge : E) {
            root.getChildren().add(edge.getEdgeGroup());
        }
        for (GraphNode node : V) {
            root.getChildren().add(node.getNodeObject());
        }
    }

    public ArrayList<GraphNode> getNodes() {
        return V;
    }

    public ArrayList<ArrowEdge> getEdges() {
        return E;
    }
}
