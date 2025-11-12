package GraphVisualizer;


import Algorithms.DFS;
import Controllers.Controller;
import Controllers.ControllerManager;
import Controllers.GraphInputController;
import Exceptions.InvalidEdgeException;
import javafx.scene.Group;
import javafx.scene.control.Tooltip;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.text.Text;

import java.io.Serial;
import java.io.Serializable;
import java.util.*;

public class Graph implements Serializable {

    @Serial
    private static final long serialVersionUID = 2L;

    public final ArrayList<GraphNode> V = new ArrayList<>();
    public final Hashtable<String, GraphNode> VerticeIndexer = new Hashtable<>();
    private final Map<Graph.GraphNode, Map<GraphNode, DirectedEdge>> adjacencyMap = new Hashtable<>();
    public final ArrayList<DirectedEdge> E = new ArrayList<>();
    private List<Integer> availableLabels = new ArrayList<>();
    private boolean isDirected = true;

    public Graph(boolean isDirected)
    {
        this.isDirected = isDirected;
    }

    public boolean isDirected() {
        return isDirected;
    }

    public Graph(Graph other) {

        try
        {
            this.isDirected = other.isDirected;

            for(GraphNode oldNode : other.V)
            {
                this.createNodeWithCoordinates(oldNode.xPosition, oldNode.yPosition, oldNode.getNodeLabel());
            }
            for(DirectedEdge oldEdge : other.E)
            {
                this.createEdge(oldEdge.getFrom().getNodeLabel(),oldEdge.getTo().getNodeLabel(),oldEdge.getWeight(), oldEdge.getFlow(), oldEdge.getCapacity());
            }
        }catch (NullPointerException e)
        {
            Controller.AlertError(new Exception("The graph object is null"));
        }


    }

    public Map<Graph.GraphNode, Map<GraphNode, DirectedEdge>> getAdjacencyMap() {
        return adjacencyMap;
    }
    public Graph Transpose()
    {
        if(!this.isDirected) return this;

        Graph transpose = new Graph(true);
        for(int i = 1; i <= this.V.size(); i++)
        {
            GraphNode node = transpose.createNode(String.valueOf(i));
            transpose.VerticeIndexer.put(String.valueOf(i),node);
        }
        for(DirectedEdge edge : this.E)
        {
            transpose.createEdge(edge.getTo().getNodeLabel(),edge.getFrom().getNodeLabel(), edge.getWeight(), edge.getFlow(), edge.getCapacity());
        }

        return transpose;
    }

    public GraphNode createNode(String label) {
        Random rand = new Random();
        int x = rand.nextInt(1,AppSettings.CONTAINER_WIDTH / 50); //14
        int y = rand.nextInt(1,AppSettings.CONTAINER_HEIGHT / 50); //12
        return createNodeWithCoordinates(50 + 50*x, 50+50*y,label);
    }

    public void createAvailableNode(double x, double y)
    {
        if(availableLabels.isEmpty()) {
            createNodeWithCoordinates(x, y, String.valueOf(this.V.size() + 1));
            return;
        }
        Optional<Integer> label = availableLabels.stream().sorted().findFirst();
        availableLabels.remove(label.get());
        createNodeWithCoordinates(x, y, String.valueOf(label.get()));

    }

    public GraphNode createNodeWithCoordinates(double x, double y, String label)
    {
        GraphNode node = new GraphNode(x, y, label,this);
        if(VerticeIndexer.containsKey(node.nodeLabel))
        {
            GraphNode oldNode = VerticeIndexer.get(node.nodeLabel);
            VerticeIndexer.remove(node.nodeLabel);
            V.remove(oldNode);
        }
        V.add(node);
        this.adjacencyMap.put(node, new HashMap<>());
        VerticeIndexer.put(label, node);
        return node;
    }

    public DirectedEdge createEdge(String fromLabel, String toLabel)
    {
        return this.createEdge(fromLabel,toLabel,0,0,0);
    }

    public DirectedEdge removeEdge(String fromLabel, String toLabel) {
        GraphNode fromNode = VerticeIndexer.get(fromLabel);
        GraphNode toNode = VerticeIndexer.get(toLabel);

        if (fromNode == null || toNode == null) {
            return null; // Nodes don't exist
        }

        DirectedEdge edge = adjacencyMap.get(fromNode).remove(toNode);

        if (edge == null) {
            return null;
        }

        E.remove(edge);
        fromNode.neighborsList.remove(toNode);
        fromNode.connectedEdges.remove(edge);
        toNode.connectedEdges.remove(edge);

        toNode.inDegree--;
        fromNode.outDegree--;
        if (!this.isDirected) {
            fromNode.degree--;
            toNode.neighborsList.remove(fromNode);
            toNode.degree--;
        }

        return edge; // Return the edge so the UI can remove it
    }

    public DirectedEdge createEdge(String fromLabel, String toLabel,int weight, int flow, int capacity) {
        try{
            GraphNode fromNode = VerticeIndexer.get(fromLabel);
            GraphNode toNode = VerticeIndexer.get(toLabel);
            if(fromNode == null || toNode == null)
                throw new InvalidEdgeException();

            if(adjacencyMap.get(fromNode).containsKey(toNode))
                return adjacencyMap.get(fromNode).get(toNode);
            DirectedEdge edge = new DirectedEdge(fromNode, toNode,this.isDirected,weight,flow,capacity);

            // Add edge to nodes
            fromNode.neighborsList.add(toNode);
            fromNode.addConnectedEdge(edge);
            toNode.inDegree++;
            fromNode.outDegree++;
            if(!this.isDirected)
                fromNode.degree++;
            if (fromNode != toNode)
                toNode.addConnectedEdge(edge);

            DirectedEdge toRemove = null; //Removes old duplicate edge if exists
            for(DirectedEdge currentEdge : E)
            {
                if(currentEdge.getFrom().nodeLabel.equals(fromNode.nodeLabel) && currentEdge.getTo().nodeLabel.equals(toNode.nodeLabel))
                    toRemove = currentEdge;
            }
            if(toRemove != null) E.remove(toRemove);
            E.add(edge);

            adjacencyMap.get(edge.getFrom()).put(edge.getTo(), edge);
            edge.setWeight(weight);
            Tooltip edgeTooltip = new Tooltip("Weight: " + weight + ", Flow : "+flow+", Capacity : "+capacity);
            Tooltip.install(edge.getShaft(), edgeTooltip);


            edge.getEdgeGroup().setOnMouseClicked(e -> {
                System.out.println("Clicked edge " + fromLabel + " -> " + toLabel);
            });
            return edge;
        }catch (InvalidEdgeException e)
        {
            Controller.AlertError(e);
        }

        return null;
    }

    public void addGraphToGroup(Group root) {
        for (DirectedEdge edge : E) {
            root.getChildren().add(edge.getEdgeGroup());
        }
        for (GraphNode node : V) {
            root.getChildren().add(node.getNodeObject());
        }
    }

    public ArrayList<GraphNode> getNodes() {
        return V;
    }

    public ArrayList<DirectedEdge> getEdges() {
        return E;
    }

    public void RemoveVertice(GraphNode node)
    {
        if(this.isDirected())
        {
            for(DirectedEdge edge : node.connectedEdges)
            {
                this.adjacencyMap.get(edge.getFrom()).remove(edge.getTo());
                edge.getFrom().outDegree--;
                edge.getTo().inDegree--;
                edge.getFrom().neighborsList.remove(edge.getTo());
            }
        }
        else
        {
            for(DirectedEdge edge : node.connectedEdges)
            {
                this.adjacencyMap.get(edge.getFrom()).remove(edge.getTo());
                edge.getFrom().neighborsList.remove(edge.getTo());
                edge.getFrom().degree--;
            }
        }

        E.removeAll(node.connectedEdges);
        V.remove(node);
        VerticeIndexer.remove(node.nodeLabel);
        availableLabels.add(Integer.parseInt(node.nodeLabel));
    }

    public Map<String, Object> getGraphReportMap()
    {
        int nodeCount = this.V.size();
        int edgeCount = this.E.size();
        boolean isConnected = false;
        if (nodeCount > 0) {
            DFS dfs = new DFS(this,this.V.getFirst());
            isConnected = dfs.isConnected();
        }

        Map<String, Object> graphReportMap = new HashMap<>();

        graphReportMap.put("nodeCount", nodeCount);
        graphReportMap.put("edgeCount", edgeCount);
        graphReportMap.put("isDirected", this.isDirected());
        graphReportMap.put("isConnected", isConnected);

        return graphReportMap;
    }

    public static class GraphNode implements Serializable{
        private Graph G;
        private transient Group nodeObject;
        private transient Circle circle;
        private transient Text label;
        private transient Color verticeColor;
        public List<DirectedEdge> connectedEdges = new ArrayList<>();
        public List<GraphNode> neighborsList = new ArrayList<>();
        private String nodeLabel;
        public int inDegree;
        public int outDegree;
        private int degree;
        public double xPosition;
        public double yPosition;


        public double getxPosition() { return xPosition; }
        public void setxPosition(double x) { this.xPosition = x; }
        public double getyPosition() { return yPosition; }
        public void setyPosition(double y) { this.yPosition = y; }

        public GraphNode(double x, double y, String textLabel,Graph G) {

            xPosition = x;
            yPosition = y;
            circle = new Circle(x, y, AppSettings.nodeRadius, AppSettings.INITIAL_VERTEXCOLOR);
            circle.getStyleClass().add(AppSettings.Graph_node_css_class);
            verticeColor = AppSettings.INITIAL_VERTEXCOLOR;

            this.G = G;
            label = new Text(x - AppSettings.nodeLabelPadding, y + AppSettings.nodeLabelPadding, textLabel);
            label.setFill(Color.BLACK);
            label.getStyleClass().add(AppSettings.node_label_css_class);


            nodeLabel = textLabel;
            nodeObject = new Group(circle, label);

            initNodeEvents();
        }

        public void ChangeColor(Color color)
        {
            verticeColor = color;
            circle.setStyle("-fx-fill: " + toHex(color) + ";");
            nodeObject = new Group(this.circle,label);
        }

        public Color getVerticeColor() {
            return verticeColor;
        }

        public GraphNode(GraphNode other) {
            this.nodeLabel = other.nodeLabel;

            this.circle = null;
            this.label = null;
            this.nodeObject = null;
            this.G = null;

            this.connectedEdges = new ArrayList<>();
            this.neighborsList = new ArrayList<>();
        }

        @Override
        public boolean equals(Object obj) {
            if(obj.getClass().equals(this.getClass()))
                return this.getNodeLabel().equals(((GraphNode) obj).nodeLabel);
            return false;
        }

        @Override
        public int hashCode() {
            return java.util.Objects.hash(nodeLabel);
        }

        public DirectedEdge getneighborEdge(GraphNode node)
        {
            for(DirectedEdge edge : this.connectedEdges)
            {
                if(edge.getFrom().equals(this) && edge.getTo().equals(node))
                    return edge;
            }
            return null;
        }

        public int getDegree()
        {
            return degree;
        }
        public void setGraph(Graph g)
        {
            this.G = g;
        }

        private void initNodeEvents() {
            circle.setFocusTraversable(true);
            circle.setOnMouseClicked(event -> {
                System.out.println("Clicked node " + nodeLabel);
                LoggerManager.Logger().fine("Clicked node " + nodeLabel);

            });

            circle.setOnMousePressed(event -> {
                circle.getStyleClass().add("selected");
                circle.setUserData(new double[]{
                        event.getSceneX(), event.getSceneY(),
                        circle.getCenterX(), circle.getCenterY()
                });
            });

            circle.setOnMouseReleased(event -> {
                circle.getStyleClass().remove("selected");
            });

            circle.setOnMouseDragged(event -> {
                double[] data = (double[]) circle.getUserData();
                double deltaX = event.getSceneX() - data[0];
                double deltaY = event.getSceneY() - data[1];

                circle.setCenterX(data[2] + deltaX);
                circle.setCenterY(data[3] + deltaY);

                // Move label along with circle
                label.setX(circle.getCenterX() - AppSettings.nodeLabelPadding);
                label.setY(circle.getCenterY() + AppSettings.nodeLabelPadding);

                // Update connected edges
                for (DirectedEdge edge : connectedEdges) {
                    if (!edge.isShaftNull())
                        edge.updatePosition(this.G.isDirected);
                }
            });

            Tooltip tooltip = new Tooltip(this.G.isDirected() ? "inDegree : "+this.inDegree+"\noutDegree :"+this.outDegree : "Degree : "+this.degree);
            Tooltip.install(nodeObject, tooltip);

            nodeObject.setOnContextMenuRequested(event -> {
                // 'event.consume()' is important! It stops the default
                // "Save As" menu (from the browser/JavaFX) from appearing.
                event.consume();

                // Call the helper method from the controller.
                // You need a way to get the controller. This assumes your
                // 'G' (Graph) object has a reference to its controller.
                GraphInputController controller = ControllerManager.getGraphInputController(); // You'll need to implement this getter

                if (controller != null) {
                    // 'this' refers to the GraphNode object
                    controller.showVertexContextMenu(this, event);
                }
            });

            nodeObject.setOnMouseEntered(event -> {
                String tooltipText = this.G.isDirected()
                        ? "inDegree : " + this.inDegree + "\noutDegree : " + this.outDegree
                        : "Degree : " + this.degree;

                tooltip.setText(tooltipText);
            });

        }

        public void addConnectedEdge(DirectedEdge edge) {
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

        private String toHex(Color color) {
            int r = (int) (color.getRed() * 255);
            int g = (int) (color.getGreen() * 255);
            int b = (int) (color.getBlue() * 255);

            // Format as a 6-digit hex string
            return String.format("#%02X%02X%02X", r, g, b);
        }



    }
}
