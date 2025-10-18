package GraphVisualizer;


import javafx.scene.Group;
import javafx.scene.control.Tooltip;
import javafx.scene.paint.Color;
import javafx.scene.shape.*;
import javafx.scene.shape.Polygon;
import java.io.Serializable;

public class ArrowEdge implements Serializable {

    private Graph.GraphNode from;
    private Graph.GraphNode to;
    private int weight;
    private transient Line shaft;
    private transient Polygon arrowHead;
    private transient Group edgeGroup;  // Holds the shaft + arrowhead


    public ArrowEdge(Graph.GraphNode from, Graph.GraphNode to,boolean isDirected,int weight) {
        this.from = from;
        this.to = to;
        this.weight = weight;
        edgeGroup = new Group();
        createEdge(isDirected);
    }

    public boolean isShaftNull()
    {
        return this.shaft == null;
    }

    public ArrowEdge(ArrowEdge other) {
        this.from = other.from;
        this.to = other.to;

        this.shaft = null;
        this.arrowHead = null;
        this.edgeGroup = null;
    }

    public int getWeight()
    {
        return this.weight;
    }

    public Graph.GraphNode getFrom()
    {
        return from;
    }

    public Graph.GraphNode getTo()
    {
        return to;
    }

    private void createEdge(boolean isDirected) {
        double startX = from.getCircle().getCenterX();
        double startY = from.getCircle().getCenterY();
        double endX = to.getCircle().getCenterX();
        double endY = to.getCircle().getCenterY();

        shaft = new Line(startX, startY, endX, endY);
        shaft.setStrokeWidth(AppSettings.EdgeWidth);
        shaft.setStroke(Color.GRAY);
        edgeGroup.getChildren().addAll(shaft);

        if(isDirected)
        {
            arrowHead = new Polygon(0,0, -AppSettings.ARROW_SIZE,-AppSettings.ARROW_SIZE, AppSettings.ARROW_SIZE,-AppSettings.ARROW_SIZE);
            arrowHead.setFill(Color.GRAY);
            updateArrowPosition(true);
            edgeGroup.getChildren().addAll(arrowHead);
        }


    }

    public Line getShaft()
    {
        return shaft;
    }
    public Group getEdgeGroup() {
        return edgeGroup;
    }

    public void updatePosition(boolean isDirected) {

        // Update line
        shaft.setStartX(from.getCircle().getCenterX());
        shaft.setStartY(from.getCircle().getCenterY());
        shaft.setEndX(to.getCircle().getCenterX());
        shaft.setEndY(to.getCircle().getCenterY());

        updateArrowPosition(isDirected);
    }

    private void updateArrowPosition(boolean isDirected) {
        double sx = shaft.getStartX();
        double sy = shaft.getStartY();
        double ex = shaft.getEndX();
        double ey = shaft.getEndY();

        // Direction vector
        double dx = ex - sx;
        double dy = ey - sy;
        double length = Math.sqrt(dx*dx + dy*dy);

        if (length == 0) return;

        // Normalize and back off by node radius
        double offset = AppSettings.nodeRadius + 5;
        double arrowX = ex - dx / length * offset;
        double arrowY = ey - dy / length * offset;

        if(isDirected)
        {
            // Position arrowhead
            // Pythagorean triplet : 3,4,5
            arrowHead.setLayoutX(arrowX + 3);
            arrowHead.setLayoutY(arrowY + 4);

            // Rotate arrow to match line
            double angle = Math.toDegrees(Math.atan2(dy, dx));
            arrowHead.setRotate(angle - 90);
        }

    }

}
