package Algorithms;

import Controllers.ControllerManager;
import GraphVisualizer.*;
import javafx.animation.PauseTransition;
import javafx.application.Platform;
import javafx.scene.Group;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.util.Duration;

import java.util.*;

public class DFS extends Algorithm{

    private AnchorPane pane;
    private Graph.GraphNode inputNode;
    private HashMap<String, Color> colors = new HashMap<>();
    private List<ArrowEdge> visitedEdges = new ArrayList<ArrowEdge>();
    private Hashtable<ArrowEdge,Boolean> coloredEdges = new Hashtable<ArrowEdge, Boolean>();
    private Hashtable<String,String> rootVertice;
    private List<Graph.GraphNode> finishTimeList;
    private int dfsAnimationNodeIndex = 0;
    public static final String AlgorithmDescription = "Depth First Search is a search algorithm that traverses a given graph G from a given vertice v by iterating over its neighbors and exhusting all the paths from one child before proceeding to the next - unlike BFS that exhusts all the children nodes before proceeding";


    public DFS(Graph G, Graph.GraphNode inputNode)
    {
        this.G = G;
        this.inputNode = inputNode;
        this.AlgorithmName = "DFS";
        this.requiredInput = "A Graph G = (V,E) and a node u from V";
        this.rootVertice = new Hashtable<>();
        this.finishTimeList = new ArrayList<>();
        initColors();
    }

    @Override
    public void Run() {

        initColors();
        pane = ControllerManager.getGraphInputController().getGraphContainer();
        pane.getStylesheets().clear();
        pane.getStylesheets().add(Objects.requireNonNull(getClass().getResource(AppSettings.DFS_style_css_location)).toExternalForm());

        visitedEdges.clear(); // IMPORTANT: Clear edges from previous run
        dfsAnimationNodeIndex = 0; // Reset the component loop index

        DisplayColorizedResultsGraph(pane);
        if (inputNode != null) {
            dfsRecursiveWithDelay(inputNode, this::runNextDfsComponent);
        } else if (!G.V.isEmpty()) {
            runNextDfsComponent();
        } else {
            cleanupDFSAnimation();
        }
    }

    private void runNextDfsComponent() {
        // Find the next node to start a new DFS traversal
        while (dfsAnimationNodeIndex < G.V.size()) {
            Graph.GraphNode node = G.V.get(dfsAnimationNodeIndex);
            dfsAnimationNodeIndex++; // Increment index now for the *next* call

            if (node != null && colors.get(node.getNodeLabel()) == Color.WHITE) {
                dfsRecursiveWithDelay(node, this::runNextDfsComponent);

                return;
            }
        }

        cleanupDFSAnimation();
    }

    private void cleanupDFSAnimation() {
        // Must run on FX thread
        Platform.runLater(() -> {
            for (Graph.GraphNode node : G.V) {
                // Remove DFS classes
                node.getCircle().getStyleClass().removeAll("node-white","node-grey","node-black");
                // Clear any inline styles from DFS
                node.getCircle().setStyle("");
            }
            for(ArrowEdge edge : G.E)
            {
                // Reset to default color (assuming black, change if different)
                edge.getShaft().setStroke(Color.BLACK);
                if (edge.getShaft() != null) {
                    edge.getShaft().setFill(Color.BLACK);
                }
            }
            pane.getStylesheets().clear();
            ThemeManager.getThemeManager().AddScene(pane.getScene());
            ThemeManager.getThemeManager().switchTheme(ThemeManager.getThemeManager().getCurrentTheme());
            ControllerManager.getGraphInputController().displayGraph(G);
        });
    }

    private void initColors()
    {
        for(Graph.GraphNode node : G.V)
        {
            colors.put(node.getNodeLabel(), Color.WHITE);
        }
    }


    public boolean isAcyclic() {
        initColors();
        if (inputNode != null && colors.get(inputNode.getNodeLabel()) == Color.WHITE) {
            if (visit(inputNode)) {
                return false;
            }
        }

        for (Graph.GraphNode node : G.V) {
            if (colors.get(node.getNodeLabel()) == Color.WHITE) {
                if (visit(node)) {
                    return false;
                }
            }
        }

        return true;
    }

    private boolean visit(Graph.GraphNode currentNode) {
        colors.put(currentNode.getNodeLabel(), Color.GREY);

        for (Graph.GraphNode neighbor : currentNode.neighborsList)
        {
            Color neighborColor = colors.get(neighbor.getNodeLabel());
            if (neighborColor == Color.GREY) {
                return true;
            } else if (neighborColor == Color.WHITE) {
                if (visit(neighbor)) {
                    return true;
                }
            }
        }
        colors.put(currentNode.getNodeLabel(), Color.BLACK);
        return false;
    }

    private void visitRegular(Graph.GraphNode currentNode)
    {
        colors.put(currentNode.getNodeLabel(), Color.GREY);

        for (Graph.GraphNode neighbor : currentNode.neighborsList) {
            if (colors.get(neighbor.getNodeLabel()) == Color.WHITE) {
                visitRegular(neighbor);
            }

        }
        colors.put(currentNode.getNodeLabel(), Color.BLACK);
        finishTimeList.add(currentNode);
    }

    public List<Graph.GraphNode> findCircuit(Graph.GraphNode node)
    {
        initEdgesColors();
        List<Graph.GraphNode> myList = new ArrayList<>();
        Graph.GraphNode currentNode = node;
        while(HasUnvisitedNeighbors(currentNode) != null)
        {
            Graph.GraphNode neighbor = HasUnvisitedNeighbors(currentNode);
            coloredEdges.put(currentNode.getneighborEdge(neighbor),true);
            if(!G.isDirected()) {
                coloredEdges.put(neighbor.getneighborEdge(currentNode),true);
            }
            myList.add(neighbor);
            currentNode = neighbor;
        }

        return myList;
    }

    Graph.GraphNode HasUnvisitedNeighbors(Graph.GraphNode node)
    {
        if(node == null || node.neighborsList.isEmpty()) return null;
        for(ArrowEdge edge : node.connectedEdges)
        {
            if(edge.getFrom().equals(node) && coloredEdges.get(edge).equals(false))
                return edge.getTo();
        }
        return null;
    }

    public void initEdgesColors()
    {
        for(ArrowEdge edge : G.E)
        {
            coloredEdges.put(edge,false);
        }
    }
    public boolean isConnected()
    {
        initColors();
        if (inputNode != null && colors.get(inputNode.getNodeLabel()) == Color.WHITE) {
            visitRegular(inputNode);
        }

        for (Graph.GraphNode node : G.V)
        {
            if(colors.get(node.getNodeLabel()).equals(Color.WHITE))
                return false;
        }
        return true;
    }


    private void dfsRecursiveWithDelay(Graph.GraphNode node, Runnable onFinished) {
        // Mark this node as discovered (GRAY)
        colors.put(node.getNodeLabel(), Color.GREY);
        Platform.runLater(() -> DisplayColorizedResultsGraph(pane));

        // Wait before processing neighbors
        PauseTransition pause = new PauseTransition(Duration.seconds(0.8));
        pause.setOnFinished(event -> {
            List<Graph.GraphNode> neighbors = node.neighborsList;
            processNextNeighbor(node, neighbors, 0, onFinished);
        });
        pause.play();
    }

    private void processNextNeighbor(Graph.GraphNode node, List<Graph.GraphNode> neighbors, int index, Runnable onFinished) {
        if (index >= neighbors.size()) {
            // All neighbors done → finish this node
            finishNode(node, onFinished);
            return;
        }

        Graph.GraphNode neighbor = neighbors.get(index);
        if (colors.get(neighbor.getNodeLabel()) == Color.WHITE) {
            visitedEdges.add(new ArrowEdge(node,neighbor,G.isDirected(),0));
            // Explore this neighbor (recursive DFS)
            dfsRecursiveWithDelay(neighbor, () -> {
                // After finishing neighbor, move to next
                processNextNeighbor(node, neighbors, index + 1, onFinished);
            });
        } else {
            // Already visited neighbor — skip to next
            processNextNeighbor(node, neighbors, index + 1, onFinished);
        }
    }

    private void finishNode(Graph.GraphNode node, Runnable onFinished) {
        colors.put(node.getNodeLabel(), Color.BLACK);
        Platform.runLater(() -> DisplayColorizedResultsGraph(pane));

        PauseTransition endPause = new PauseTransition(Duration.seconds(0.8));
        endPause.setOnFinished(event -> {
            if (onFinished != null) onFinished.run();
        });
        endPause.play();
    }


    @Override
    public Boolean checkValidity() {
        return true;
    }

    @Override
    public void DisplayResults()
    {
        ThemeManager.getThemeManager().switchTheme(ThemeManager.getThemeManager().getCurrentTheme());
    }

    private void DisplayColorizedResultsGraph(Pane pane)
    {
        pane.getChildren().clear();
        Group group = new Group();
        addGraphToGroup(group);
        pane.getChildren().add(group);
    }

    private void addGraphToGroup(Group root) {
        // Add edges first
        for (ArrowEdge edge : G.E) {
            boolean matched = false;
            for (ArrowEdge visitedEdge : visitedEdges) {
                if (edge.getFrom().getNodeLabel().equals(visitedEdge.getFrom().getNodeLabel())
                        && edge.getTo().getNodeLabel().equals(visitedEdge.getTo().getNodeLabel()) || edge.getFrom().getNodeLabel().equals(visitedEdge.getTo().getNodeLabel())
                        && edge.getTo().getNodeLabel().equals(visitedEdge.getFrom().getNodeLabel())) {
                    matched = true;
                    break;
                }
            }
            edge.getShaft().setStroke(matched ? Color.RED : Color.BLACK);

            edge.getShaft().setStrokeWidth(AppSettings.EdgeWidth);
            root.getChildren().add(edge.getEdgeGroup());
        }

        // Add nodes with dynamic CSS classes
        for (Graph.GraphNode node : G.V) {
            // Remove previous DFS color classes
            node.getCircle().getStyleClass().removeAll("node-white", "node-grey", "node-black");

            Color color = colors.get(node.getNodeLabel());
            if (color != null) {
                String cssClass = switch (color.toString()) {
                    case "0xffffffff" -> "node-white";
                    case "0xff808080" -> "node-grey"; // grey
                    case "0xff000000" -> "node-black"; // black
                    default -> null;
                };
                if (cssClass != null) {
                    node.getCircle().getStyleClass().add(cssClass);
                } else {
                    // fallback: inline style if color not predefined
                    node.getCircle().setStyle("-fx-fill: " + toRgbString(color) + ";");
                }
            }

            // Add the node to the group
            root.getChildren().add(node.getNodeObject());
        }
    }

    private String toRgbString(Color c) {
        return String.format("rgb(%d, %d, %d)",
                (int) (c.getRed() * 255),
                (int) (c.getGreen() * 255),
                (int) (c.getBlue() * 255));
    }

    public Hashtable<String,String> FindConnectivityComponents()
    {
        if(G.isDirected()) return null;
        initColors();
        for(Graph.GraphNode current : G.V)
        {
            if (current != null && colors.get(current.getNodeLabel()) == Color.WHITE) {
                visitWithRoot(current,current);
            }
        }

        return rootVertice;
    }

    public String FindDirectedComponent(Graph.GraphNode current)
    {
        if(current == null || colors.get(current.getNodeLabel()) != Color.WHITE)
            return "";

        if (colors.get(current.getNodeLabel()) == Color.WHITE) {
            visitWithRoot(current,current);
        }

        return rootVertice.get(current.getNodeLabel());
    }
    public List<Graph.GraphNode> DFSWithEndTimeList()
    {
        if(!G.isDirected()) return null;
        initColors();
        for(Graph.GraphNode current : G.V)
        {
            if (current != null && colors.get(current.getNodeLabel()) == Color.WHITE) {
                visitRegular(current);
            }
        }

        return finishTimeList;
    }
    private void visitWithRoot(Graph.GraphNode currentNode,Graph.GraphNode root)
    {
        colors.put(currentNode.getNodeLabel(), Color.GREY);
        String newValue;
        if(currentNode.equals(root))
        {
            rootVertice.put(root.getNodeLabel(), currentNode.getNodeLabel());
            newValue = rootVertice.get(root.getNodeLabel());

        }
        else
            newValue = rootVertice.get(root.getNodeLabel()) +","+ currentNode.getNodeLabel();
        rootVertice.put(root.getNodeLabel(),newValue);

        for (Graph.GraphNode neighbor : currentNode.neighborsList) {
            if (colors.get(neighbor.getNodeLabel()) == Color.WHITE) {
                visitWithRoot(neighbor,root);
            }

        }
        colors.put(currentNode.getNodeLabel(), Color.BLACK);
    }

}
