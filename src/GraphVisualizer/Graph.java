package GraphVisualizer;


import Controllers.Controller;
import Exceptions.InvalidEdgeException;
import javafx.scene.Group;
import javafx.scene.control.Tooltip;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.text.Text;

import java.io.Serializable;
import java.util.*;

public class Graph implements Serializable {

    private static final long serialVersionUID = 2L;

    public final ArrayList<GraphNode> V = new ArrayList<>();
    public final Hashtable<String, GraphNode> VerticeIndexer = new Hashtable<>();
    private final Map<Graph.GraphNode, Map<GraphNode, DirectedEdge>> adjacencyMap = new Hashtable<>();
    public final ArrayList<DirectedEdge> E = new ArrayList<>();
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
        int x = rand.nextInt(1,14);
        int y = rand.nextInt(1,12);
        GraphNode node = new GraphNode(50 + 50*x, 50+50*y, label,this);
        if(VerticeIndexer.containsKey(node.nodeLabel))
        {
            GraphNode oldNode = VerticeIndexer.get(node.nodeLabel);
            VerticeIndexer.remove(node.nodeLabel);
            V.remove(oldNode);
        }
        V.add(node);
        VerticeIndexer.put(label, node);
        this.adjacencyMap.put(node, new HashMap<>());
        return node;
    }

    public void createNodeWithCoordinates(double x, double y, String label)
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
    }

    public DirectedEdge createEdge(String fromLabel, String toLabel)
    {
        return this.createEdge(fromLabel,toLabel,0,0,0);
    }

    public void removeEdge(String fromLabel, String toLabel)
    {

        GraphNode fromNode = VerticeIndexer.get(fromLabel);
        GraphNode toNode = VerticeIndexer.get(toLabel);

        DirectedEdge edge = new DirectedEdge(fromNode,toNode,this.isDirected());

        fromNode.neighborsList.remove(toNode);
        fromNode.connectedEdges.remove(edge);
        toNode.inDegree--;
        fromNode.outDegree--;


    }

    public DirectedEdge createEdge(String fromLabel, String toLabel,int weight, int flow, int capacity) {
        try{
            GraphNode fromNode = VerticeIndexer.get(fromLabel);
            GraphNode toNode = VerticeIndexer.get(toLabel);
            if(fromNode == null || toNode == null)
                throw new InvalidEdgeException();

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
        // Add edges first so nodes appear on top
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
        List<DirectedEdge> toRemove = new ArrayList<>();
        for(DirectedEdge edge : E)
        {
            if(node.nodeLabel.equals(edge.getFrom().nodeLabel))
            {
                toRemove.add(edge);
                edge.getTo().inDegree--;
            }
            else if(node.nodeLabel.equals(edge.getTo().nodeLabel))
            {
                toRemove.add(edge);
                edge.getFrom().outDegree--;
            }
        }
        E.removeAll(toRemove);
        V.remove(node);
        VerticeIndexer.remove(node.nodeLabel);
    }


    public static class GraphNode implements Serializable{
        private Graph G;
        private transient Group nodeObject;
        private transient Circle circle;
        private transient Text label;
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
            circle = new Circle(x, y, AppSettings.nodeRadius, Color.LIGHTBLUE);
            circle.getStyleClass().add(AppSettings.Graph_node_css_class);

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
            circle.setStyle("-fx-fill: " + toHex(color) + ";");
            nodeObject = new Group(this.circle,label);
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
                CurrentlyPressedNodeHelper.setCurrentNode(this);
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
