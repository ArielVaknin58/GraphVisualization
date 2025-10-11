package GraphVisualizer;


import Exceptions.InvalidEdgeException;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.text.Text;

import java.awt.*;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.Random;

public class Graph implements Serializable {

    private static final long serialVersionUID = 2L;

    public final ArrayList<GraphNode> V = new ArrayList<>();
    public final Hashtable<String, GraphNode> VerticeIndexer = new Hashtable<>();
    public final ArrayList<ArrowEdge> E = new ArrayList<>();
    private boolean isDirected = true;



    // ------------------- Graph Methods -------------------
    public Graph(boolean isDirected)
    {
        this.isDirected = isDirected;
    }

    public Graph(Graph other) {

        this.isDirected = other.isDirected;


            for (GraphNode oldNode : other.V) {
                GraphNode newNode = new GraphNode(oldNode);
                newNode.setGraph(this);
                this.VerticeIndexer.put(newNode.nodeLabel, newNode);
                this.V.add(newNode);
            }

            for (ArrowEdge oldEdge : other.E) {
                ArrowEdge newEdge = new ArrowEdge(oldEdge);
                GraphNode fromNode = VerticeIndexer.get(oldEdge.getFrom().getNodeLabel());
                GraphNode toNode = VerticeIndexer.get(oldEdge.getTo().getNodeLabel());

                fromNode.addConnectedEdge(newEdge);
                toNode.inDegree++;
                fromNode.outDegree++;
                if (newEdge.getFrom() != newEdge.getTo())
                    newEdge.getTo().addConnectedEdge(newEdge);

                E.add(newEdge);
            }


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
        return node;
    }

    public void createEdge(String fromLabel, String toLabel) throws InvalidEdgeException {
        GraphNode fromNode = VerticeIndexer.get(fromLabel);
        GraphNode toNode = VerticeIndexer.get(toLabel);
        if(fromNode == null || toNode == null)
            throw new InvalidEdgeException();

        ArrowEdge edge = new ArrowEdge(fromNode, toNode);

        // Add edge to nodes
        fromNode.neighborsList.add(toNode);
        fromNode.addConnectedEdge(edge);
        toNode.inDegree++;
        fromNode.outDegree++;
        if (fromNode != toNode)
            toNode.addConnectedEdge(edge);

        ArrowEdge toRemove = null; //Removes old duplicate edge if exists
        for(ArrowEdge currentEdge : E)
        {
            if(currentEdge.getFrom().nodeLabel.equals(fromNode.nodeLabel) && currentEdge.getTo().nodeLabel.equals(toNode.nodeLabel))
                toRemove = currentEdge;
        }
        if(toRemove != null) E.remove(toRemove);
        E.add(edge);

        // Optional click event on edge
        edge.getEdgeGroup().setOnMouseClicked(e -> {
            System.out.println("Clicked edge " + fromLabel + " -> " + toLabel);
        });
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

    public void RemoveVertice(GraphNode node)
    {
        List<ArrowEdge> toRemove = new ArrayList<>();
        for(ArrowEdge edge : E)
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


    public class GraphNode implements Serializable{
        private Graph G;
        private transient Group nodeObject;
        private transient Circle circle;
        private transient Text label;
        public List<ArrowEdge> connectedEdges = new ArrayList<>();
        public List<GraphNode> neighborsList = new ArrayList<>();
        private String nodeLabel;
        public int inDegree;
        public int outDegree;

        public GraphNode(double x, double y, String textLabel,Graph G) {
            circle = new Circle(x, y, AppSettings.nodeRadius, Color.LIGHTBLUE);
            circle.getStyleClass().add("graph-node");

            this.G = G;
            label = new Text(x - AppSettings.nodeLabelPadding, y + AppSettings.nodeLabelPadding, textLabel);
            label.setFill(Color.BLACK);
            label.getStyleClass().add("node-label");


            nodeLabel = textLabel;
            nodeObject = new Group(circle, label);

            initDragHandlers();
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

        public void setGraph(Graph g)
        {
            this.G = g;
        }

        private void initDragHandlers() {
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
                    for (ArrowEdge edge : connectedEdges) {
                        if (!edge.isShaftNull())
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


}
