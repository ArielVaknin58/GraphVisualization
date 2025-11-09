package GraphVisualizer;


import Controllers.ControllerManager;
import Controllers.GraphInputController;
import javafx.scene.Group;
import javafx.scene.paint.Color;
import javafx.scene.shape.*;
import javafx.scene.shape.Polygon;
import java.io.Serializable;

public class DirectedEdge implements Serializable,Comparable {

    private Graph.GraphNode from;
    private Graph.GraphNode to;
    private int weight;
    private int flow;
    private int capacity;
    private transient Line shaft;
    private transient Polygon arrowHead;
    private transient Group edgeGroup;  // Holds the shaft + arrowhead


    public DirectedEdge(Graph.GraphNode from, Graph.GraphNode to, boolean isDirected, int weight, int flow, int capacity) {
        this.from = from;
        this.to = to;
        this.weight = weight;
        this.flow = flow;
        this.capacity = capacity;
        edgeGroup = new Group();
        createEdge(isDirected);
    }

    public DirectedEdge(Graph.GraphNode from, Graph.GraphNode to, boolean isDirected)
    {
        this(from,to,isDirected,0,0,1);
    }

    public DirectedEdge(Graph.GraphNode from, Graph.GraphNode to, boolean isDirected, int weight)
    {
        this(from,to,isDirected,weight,0,1);
    }

    public int getCapacity() {
        return capacity;
    }

    public int getFlow() {
        return flow;
    }

    public void setFlow(int flow) {
        this.flow = flow;
    }

    public boolean isShaftNull()
    {
        return this.shaft == null;
    }

    public DirectedEdge(DirectedEdge other) {
        this.from = other.from;
        this.to = other.to;
        this.weight = other.getWeight();
        this.capacity = other.getCapacity();
        this.flow = other.getFlow();

        this.shaft = null;
        this.arrowHead = null;
        this.edgeGroup = null;
    }

    public void ChangeColor(Color color)
    {
        String hexColor = toHex(color);
        shaft.setStyle("-fx-stroke: " + hexColor + ";");
        if (arrowHead != null) {
            arrowHead.setStyle(
                    "-fx-fill: " + hexColor + ";" +
                            "-fx-stroke: " + hexColor + ";"
            );
        }

    }

    public int getWeight()
    {
        return this.weight;
    }

    public void setWeight(int weight)
    {
        this.weight = weight;
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

        initEdgeContextMenu();
    }

    private void initEdgeContextMenu()
    {
        edgeGroup.setOnContextMenuRequested(event -> {
            // 'event.consume()' is important! It stops the default
            // "Save As" menu (from the browser/JavaFX) from appearing.
            event.consume();

            // Call the helper method from the controller.
            // You need a way to get the controller. This assumes your
            // 'G' (Graph) object has a reference to its controller.
            GraphInputController controller = ControllerManager.getGraphInputController(); // You'll need to implement this getter

            if (controller != null) {
                // 'this' refers to the GraphNode object
                controller.showEdgeContextMenu(this, event);
            }
        });


    }

    private String toHex(Color color) {
        int r = (int) (color.getRed() * 255);
        int g = (int) (color.getGreen() * 255);
        int b = (int) (color.getBlue() * 255);

        // Format as a 6-digit hex string
        return String.format("#%02X%02X%02X", r, g, b);
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

    @Override
    public boolean equals(Object obj) {
        DirectedEdge e = (DirectedEdge)obj;
        return e.from.equals(from) && e.to.equals(to);
    }

    @Override
    public int hashCode() {
        return java.util.Objects.hash(from.getNodeLabel(),to.getNodeLabel());
    }

    @Override
    public int compareTo(Object o) {
        DirectedEdge other = (DirectedEdge) o;
        if(this.weight == other.getWeight())
            return 0;
        return this.weight > other.weight ? 1 : -1;
    }
}
