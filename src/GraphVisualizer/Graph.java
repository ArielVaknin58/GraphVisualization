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

        Hashtable<String, GraphNode> nodeMap = new Hashtable<>();


            for (GraphNode oldNode : other.V) {
                GraphNode newNode = new GraphNode(oldNode);
                newNode.setGraph(this);
                this.V.add(newNode);
                this.VerticeIndexer.put(newNode.nodeLabel, newNode);
                nodeMap.put(oldNode.nodeLabel, newNode);
            }

            for (ArrowEdge oldEdge : other.E) {
                ArrowEdge newEdge = new ArrowEdge(oldEdge);
                newEdge.getFrom().addConnectedEdge(newEdge);
                newEdge.getTo().inDegree++;
                if (newEdge.getFrom() != newEdge.getTo())
                    newEdge.getTo().addConnectedEdge(newEdge);

                E.add(newEdge);
                //newFrom.connectedEdges.add(newEdge);
                //newTo.connectedEdges.add(newEdge);
            }


    }


    public GraphNode createNode(String label) {
        Random rand = new Random();
        int x = rand.nextInt(1,14);
        int y = rand.nextInt(1,12);
        GraphNode node = new GraphNode(50 + 50*x, 50+50*y, label,this);
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
        toNode.inDegree++;
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
        //ControllerManager.getGraphInputController().displayGraph(this);
    }


    public class GraphNode {
        private Graph G;
        private Group nodeObject;
        private Circle circle;
        private Text label;
        public List<ArrowEdge> connectedEdges = new ArrayList<>();
        private String nodeLabel;
        public int inDegree;
        public int outDegree;

        public GraphNode(double x, double y, String textLabel,Graph G) {
            circle = new Circle(x, y, AppSettings.nodeRadius, Color.LIGHTBLUE);
            circle.setStroke(Color.DARKBLUE);

            this.G = G;
            label = new Text(x - AppSettings.nodeLabelPadding, y + AppSettings.nodeLabelPadding, textLabel);
            label.setFill(Color.BLACK);

            nodeLabel = textLabel;
            nodeObject = new Group(circle, label);

            initDragHandlers();
        }

        public GraphNode(GraphNode other) {
            this.nodeLabel = other.nodeLabel;
            //this.inDegree = other.inDegree;
            //this.outDegree = other.outDegree;

            this.circle = null;
            this.label = null;
            this.nodeObject = null;
            this.G = null;

            // Deep copy list structure (edges will be reconnected later)
            this.connectedEdges = new ArrayList<>();
        }

        public void setGraph(Graph g)
        {
            this.G = g;
        }

        private void initDragHandlers() {
            circle.setOnMouseClicked(event -> {
                this.G.RemoveVertice(this);
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
                label.setX(circle.getCenterX() - AppSettings.nodeLabelPadding);
                label.setY(circle.getCenterY() + AppSettings.nodeLabelPadding);

                // Update connected edges
                for (ArrowEdge edge : connectedEdges) {
                    if(!edge.isShaftNull())
                        edge.updatePosition();
                }
            });
        }

        public void addConnectedEdge(ArrowEdge edge) {
            connectedEdges.add(edge);
            this.outDegree++;
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
