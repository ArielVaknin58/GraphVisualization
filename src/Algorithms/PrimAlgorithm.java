package Algorithms;

import Controllers.Controller;
import Controllers.GraphResultController;
import GraphVisualizer.AppSettings;
import GraphVisualizer.DirectedEdge;
import GraphVisualizer.Graph;
import GraphVisualizer.ThemeManager;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.util.Comparator;
import java.util.HashMap;
import java.util.Objects;
import java.util.PriorityQueue;

public class PrimAlgorithm extends Algorithm {

    public static final String AlgorithmDescription = "The algorithm finds minimal spanning tree in a given undirected graph.";
    private HashMap<Graph.GraphNode,Boolean> isInTree;
    private HashMap<Graph.GraphNode,Integer> weightsToConnect;
    private HashMap<Graph.GraphNode, Graph.GraphNode> parents;
    private Graph result;

    public PrimAlgorithm(Graph graph)
    {
        this.G = graph;
        this.AlgorithmName = "Prim's Algorithm";
        this.requiredInput = "A weighted, undirected and fully connected graph";
        this.result = new Graph(false);
        init();
    }

    private void init()
    {
        this.isInTree = new HashMap<>();
        this.weightsToConnect = new HashMap<>();
        this.parents = new HashMap<>();
        for(Graph.GraphNode node : G.V)
        {
            isInTree.put(node,false);
            weightsToConnect.put(node,Integer.MAX_VALUE);
            parents.put(node,null);
        }
    }

    @Override
    public void Run() {
        if (G.V.isEmpty()) {
            return;
        }
        init();

        PriorityQueue<Graph.GraphNode> Q = new PriorityQueue<>(
                Comparator.comparingInt(weightsToConnect::get)
        );

        Graph.GraphNode startNode = G.V.getFirst();
        weightsToConnect.put(startNode, 0);
        Q.addAll(G.V);

        while (!Q.isEmpty()) {
            Graph.GraphNode currentNode = Q.poll();
            if (weightsToConnect.get(currentNode) == Integer.MAX_VALUE) {
                break;
            }
            isInTree.put(currentNode, true);
            for (DirectedEdge edge : currentNode.connectedEdges) {
                if (edge.getFrom().equals(currentNode)) {
                    Graph.GraphNode neighborNode = edge.getTo();
                    if (!isInTree.get(neighborNode)) {
                        boolean relaxed = relax(currentNode, neighborNode, edge);
                        if (relaxed) {
                            Q.remove(neighborNode);
                            Q.add(neighborNode);
                        }
                    }
                }
            }
        }

        BuildMinSpanningTree();

    }

    private void BuildMinSpanningTree()
    {
        this.result = new Graph(this.G);
        for(Graph.GraphNode node : parents.keySet())
        {
            Graph.GraphNode parent = parents.get(node);
            if(parent != null)
            {
                DirectedEdge edge = result.createEdge(parent.getNodeLabel(),node.getNodeLabel(),0);
                edge.ChangeColor(Color.RED);
            }
        }

    }

    private boolean relax(Graph.GraphNode source, Graph.GraphNode dest, DirectedEdge edge)
    {
        int sourceWeight = weightsToConnect.get(source);
        int destWeight = weightsToConnect.get(dest);
        int edgeWeight = edge.getWeight();

        // This check correctly prevents integer overflow
        if (sourceWeight != Integer.MAX_VALUE && destWeight > sourceWeight + edgeWeight)
        {
            // Use the local variables for the update
            weightsToConnect.put(dest, sourceWeight + edgeWeight);
            parents.put(dest, source);
            return true;
        }
        return false;
    }

    @Override
    public Boolean checkValidity() {
        if(G.V.isEmpty())
        {
            return true;
        }
        DFS dfs = new DFS(this.G,this.G.V.getFirst());
        return !this.G.isDirected() && dfs.isConnected();
    }

    @Override
    public void DisplayResults() {
        try
        {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(AppSettings.Graph_results_location));
            Scene scene = new Scene(loader.load());
            ThemeManager.getThemeManager().AddScene(scene);
            GraphResultController controller = loader.getController();
            controller.displayGraph(this.result);

            Stage resultStage = new Stage();
            Image icon = new Image(Objects.requireNonNull(getClass().getResourceAsStream(AppSettings.App_Icon_location)));
            resultStage.getIcons().add(icon);
            //resultStage.initModality(Modality.APPLICATION_MODAL);
            resultStage.setTitle(this.AlgorithmName+" results :");

            resultStage.setScene(scene);
            resultStage.show();
        }
        catch (Exception e)
        {
            Controller.AlertError(e);
        }
    }
}
