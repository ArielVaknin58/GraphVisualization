import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.scene.text.Text;

import java.util.*;


public class Graph {

    private final ArrayList<GraphNode> V = new ArrayList<>();
    private final Hashtable<String, GraphNode> VerticeIndexer = new Hashtable<>();
    private final Hashtable<String, Edge> edgeIndexer = new Hashtable<>();
    private final ArrayList<Edge> E = new ArrayList<>();

    private class Edge {
        private final Line lineObject;
        private final GraphNode from;
        private final GraphNode to;

        public Edge(GraphNode from, GraphNode to) {
            this.from = from;
            this.to = to;
            Circle fromCircle = from.getCircle();
            Circle toCircle = to.getCircle();
            lineObject = new Line(fromCircle.getCenterX(), fromCircle.getCenterY(),
                    toCircle.getCenterX(), toCircle.getCenterY());
            lineObject.setStrokeWidth(AppSettings.EdgeWidth);
            lineObject.setStroke(Color.GRAY);
        }

        public void updatePosition() {
            lineObject.setStartX(from.getCircle().getCenterX());
            lineObject.setStartY(from.getCircle().getCenterY());
            lineObject.setEndX(to.getCircle().getCenterX());
            lineObject.setEndY(to.getCircle().getCenterY());
        }

        public Line getLineObject() {
            return lineObject;
        }

        private void setOnClick()
        {
            lineObject.setOnMouseClicked(event -> {
            //System.out.println("clicked on edge "+fromLabel+toLabel);
            if(lineObject.getStroke().equals(Color.GRAY))
                lineObject.setStroke(Color.GOLD);
            else
                lineObject.setStroke(Color.GRAY);

        });
        }
    }


    private class GraphNode {
        private final Group nodeObject;
        private final Circle circle;
        private final Text label;
        private final ArrayList<GraphNode> neighbors;

        public GraphNode(Circle i_circle, Text text) {
            circle = i_circle;
            circle.setStroke(Color.DARKBLUE);
            label = text;
            nodeObject = new Group(circle, label);
            neighbors = new ArrayList<>();

            setupDrag();
            setupClick();
        }

        public void addNeighbor(GraphNode neighbor)
        {
            neighbors.add(neighbor);
        }
        public Group getNodeObject() {
            return nodeObject;
        }

        public Circle getCircle() {
            return circle;
        }

        public Text getLabel() {
            return label;
        }

        // Click to toggle color
        private void setupClick() {
            circle.setOnMouseClicked(event -> {
                if (circle.getFill().equals(Color.DARKBLUE))
                    circle.setFill(Color.LIGHTBLUE);
                else
                    circle.setFill(Color.DARKBLUE);
            });
        }

        // Dragging
        private void setupDrag() {
            circle.setOnMousePressed(event -> {
                circle.setUserData(new double[]{event.getSceneX(), event.getSceneY(), circle.getCenterX(), circle.getCenterY()});
            });

            circle.setOnMouseDragged(event -> {
                double[] data = (double[]) circle.getUserData();
                double deltaX = event.getSceneX() - data[0];
                double deltaY = event.getSceneY() - data[1];
                double newX = data[2] + deltaX;
                double newY = data[3] + deltaY;

                circle.setCenterX(newX);
                circle.setCenterY(newY);

                // Move the label relative to the circle
                label.setX(newX - AppSettings.nodeLabelPadding);
                label.setY(newY + AppSettings.nodeLabelPadding);

                // Update connected edges
                for (GraphNode node : neighbors) {
                    Edge edge = edgeIndexer.get(this.label.getText()+node.label.getText());
                        edge.updatePosition();
                }
            });
        }
    }

    public void createNode(double x, double y, String label) {
        Circle circle = new Circle(x, y, AppSettings.nodeRadius, AppSettings.defaultNodeColor);
        circle.setStroke(Color.DARKBLUE);
        Text text = new Text(x - AppSettings.nodeLabelPadding, y + AppSettings.nodeLabelPadding, label);
        text.setFill(AppSettings.defaultNodeLabelColor);

        GraphNode res = new GraphNode(circle, text);
        V.add(res);
        VerticeIndexer.put(label, res);
    }

    public void createEdge(String fromLabel, String toLabel) {
        Circle from = VerticeIndexer.get(fromLabel).getCircle();
        Circle to = VerticeIndexer.get(toLabel).getCircle();
        Line line = new Line(from.getCenterX(), from.getCenterY(),
                to.getCenterX(), to.getCenterY());
        line.setStrokeWidth(AppSettings.EdgeWidth);
        line.setStroke(Color.GRAY);

        Edge res = new Edge(VerticeIndexer.get(fromLabel),VerticeIndexer.get(toLabel));
        res.from.addNeighbor(res.to);
        res.to.addNeighbor(res.from);
        E.add(res);
        edgeIndexer.put(fromLabel+toLabel,res);
        edgeIndexer.put(toLabel+fromLabel,res);

    }

//    public Node FindVerticeNodeByLabel(String label)
//    {
//        return VerticeIndexer.get(label).getNodeObject();
//    }
//
//    public Node FindEdgeNodeByLabel(String from, String to)
//    {
//        return edgeIndexer.get(from+to).getLineObject();
//    }

    public void AddGraphToGroup(Group root)
    {
        for (Edge edge : E)
        {
            root.getChildren().addAll(edge.getLineObject());
        }
        for (GraphNode node : V)
        {
            root.getChildren().addAll(node.getNodeObject());
        }

    }
}
