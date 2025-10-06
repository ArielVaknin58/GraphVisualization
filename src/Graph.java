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

    public void createNode(double x, double y, String label) {
        Circle circle = new Circle(x, y, 25, Color.LIGHTBLUE);
        circle.setStroke(Color.DARKBLUE);
        Text text = new Text(x - 5, y + 5, label);
        text.setFill(Color.BLACK);

        circle.setOnMouseClicked(event -> {
            System.out.println("Clicked node " + label);
            if(circle.getFill().equals(Color.DARKBLUE))
                circle.setFill(Color.LIGHTBLUE); // change color to show feedback
            else
                circle.setFill(Color.DARKBLUE); // change color to show feedback

        });
        GraphNode res = new GraphNode(circle, text);
        V.add(res);
        VerticeIndexer.put(label, res);

    }

    public void createEdge(String fromLabel, String toLabel) {
        Circle from = VerticeIndexer.get(fromLabel).getNodeCircle();
        Circle to = VerticeIndexer.get(toLabel).getNodeCircle();
        Line line = new Line(from.getCenterX(), from.getCenterY(),
                to.getCenterX(), to.getCenterY());
        line.setStrokeWidth(2);
        line.setStroke(Color.GRAY);
        Edge res = new Edge(line);
        E.add(res);
        edgeIndexer.put(fromLabel+toLabel,res);
    }

    public Node FindVerticeNodeByLabel(String label)
    {
        return VerticeIndexer.get(label).getNodeObject();
    }

    public Node FindEdgeNodeByLabel(String from, String to)
    {
        return edgeIndexer.get(from+to).getLineObject();
    }

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
