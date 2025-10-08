package GraphVisualizer;


import javafx.scene.Group;
import javafx.scene.paint.Color;
import javafx.scene.shape.*;

public class ArrowEdge {

    private Graph.GraphNode from;
    private Graph.GraphNode to;

    private Line shaft;
    private Polygon arrowHead;
    private Group edgeGroup;  // Holds the shaft + arrowhead


    public ArrowEdge(Graph.GraphNode from, Graph.GraphNode to) {
        this.from = from;
        this.to = to;
        edgeGroup = new Group();
        createEdge();
    }

    private void createEdge() {
        // Normal line + arrowhead
        double startX = from.getCircle().getCenterX();
        double startY = from.getCircle().getCenterY();
        double endX = to.getCircle().getCenterX();
        double endY = to.getCircle().getCenterY();

        shaft = new Line(startX, startY, endX, endY);
        shaft.setStrokeWidth(2);
        shaft.setStroke(Color.GRAY);

        arrowHead = new Polygon(0,0, -AppSettings.ARROW_SIZE,-AppSettings.ARROW_SIZE, AppSettings.ARROW_SIZE,-AppSettings.ARROW_SIZE);
        arrowHead.setFill(Color.GRAY);

        // Position arrow at the end
        updateArrowPosition();

        edgeGroup.getChildren().addAll(shaft, arrowHead);

    }

    public Group getEdgeGroup() {
        return edgeGroup;
    }

    public void updatePosition() {

        // Update line
        shaft.setStartX(from.getCircle().getCenterX());
        shaft.setStartY(from.getCircle().getCenterY());
        shaft.setEndX(to.getCircle().getCenterX());
        shaft.setEndY(to.getCircle().getCenterY());

        updateArrowPosition();
    }

    private void updateArrowPosition() {
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

        // Position arrowhead
        // Pythagorean triplet : 3,4,5
        arrowHead.setLayoutX(arrowX + 3);
        arrowHead.setLayoutY(arrowY + 4);

        // Rotate arrow to match line
        double angle = Math.toDegrees(Math.atan2(dy, dx));
        arrowHead.setRotate(angle - 90);
    }

}
