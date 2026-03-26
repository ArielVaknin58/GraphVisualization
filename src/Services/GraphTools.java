package Services;

import Algorithms.Algorithm;
import Algorithms.*;
import Controllers.ControllerManager;
import Controllers.GraphInputController;
import GraphVisualizer.Graph;
import dev.langchain4j.agent.tool.P;
import dev.langchain4j.agent.tool.Tool;
import javafx.application.Platform;

import java.util.List;

public class GraphTools {

    private static Graph currentGraph;

    public GraphTools()
    {
        currentGraph = ControllerManager.getGraphInputController().getGraph();
    }

    @Tool("""
            Creates a new graph structure. Use this when the user describes nodes and edges to build.### RULES ###
                    1. Nodes MUST be strings representing numbers starting from 1.
                    2. If directedness is not mentioned, the graph should be undirected (isDirected: false).
                    3. If 'undirected', for every edge A->B, you MUST add a second edge object B->A.
                    4. 'weight': use range if specified, otherwise default to random (1-10) if "random" is mentioned, else 0.
                    5. 'capacity': use range if specified, otherwise default to random (1-50) if "random" is mentioned, else 0.""")
    public String createGraph(
            @P("The boolean flag for directed/undirected") boolean isDirected,
            @P("List of node labels, e.g., ['1', '2']") List<String> nodes,
            @P("List of edge objects with from, to, weight, and capacity") List<EdgeData> edges
    ) {
        GraphData newGraph = new GraphData(isDirected, nodes, edges);
        Platform.runLater(() -> GraphInputController.CreateGraphStatic(newGraph));

        return "Successfully created a " + (isDirected ? "directed" : "undirected") +
                " graph with " + nodes.size() + " nodes.";
    }


    @Tool("Executes a Breadth-First Search (BFS) on the current graph starting from a specific node. " +
            "Use this to find the shortest path in unweighted graphs or to explore reachable nodes.")
    public String runBFS(@P("The label of the node where the search should begin (e.g., '1', '2')")String startNodeLabel)
    {
        Graph.GraphNode startNode = currentGraph.VerticeIndexer.get(startNodeLabel);
        if (startNode == null) {
            return "Error: Node '" + startNodeLabel + "' does not exist.";
        }
        return runAlgorithm(new BFS(currentGraph,startNode));
    }

    @Tool("Executes a Depth-First Search (BFS) on the current graph starting from a specific node. " +
            "Use this to traverse graphs or to explore reachable nodes.")
    public String runDFS(@P("The label of the node where the search should begin (e.g., '1', '2')")String startNodeLabel)
    {
        Graph.GraphNode startNode = currentGraph.VerticeIndexer.get(startNodeLabel);
        if (startNode == null) {
            return "Error: Node '" + startNodeLabel + "' does not exist.";
        }
        return runAlgorithm(new DFS(currentGraph,startNode));
    }

    @Tool("Execute the Euler circuit algorithm on the current graph. Use this to find an Euler Circuit in graph if one exists.")
    public String runEulerCircuit()
    {
        return runAlgorithm(new EulerCircuit(currentGraph));
    }

    @Tool("Execute the Topological Sort algorithm on the current graph. Used to find a topological sort in a directed and acyclic graph.")
    public String runTopologicalSort()
    {
        return runAlgorithm(new TopologicalSort(currentGraph));
    }

    @Tool("Execute the Bi Partite algorithm on the current graph. used to find a division of the graph into 2 groups of nodes.")
    public String runBiPartite()
    {
        return runAlgorithm(new BiPartite(currentGraph));
    }

    @Tool("Execute the Kosaraju-Sharir algorithm on the current graph. used to find connectivity components in a directed graph.")
    public String runKosarajuAlgorithm()
    {
        return runAlgorithm(new KosarajuSharirAlgorithm(new Graph(ControllerManager.getGraphInputController().getGraph())));
    }

    @Tool("Execute the Super-Graph algorithm on the current graph. finds the super graph of a directed given graph- a strongly connected component in the graph is a node in the new super graph.")
    public String runSuperGraphAlgorithm()
    {
        return runAlgorithm(new SuperGraph(new Graph(ControllerManager.getGraphInputController().getGraph())));
    }

    @Tool("Execute the k-colors algorithm on the current graph. runs some iterations and finds if the graph has a valid k coloring for some parameter k.")
    public String runKcolors(@P("number of iterations to run the algorithms") int iterations, @P("set number, parameter for the problem") int k)
    {

        return runAlgorithm(new kColors(new Graph(ControllerManager.getGraphInputController().getGraph()), iterations, k));
    }
    private static String runAlgorithm(Algorithm algorithm)
    {
        try {
            if (currentGraph == null) {
                return "Error: Graph is not loaded.";
            }

            if(!algorithm.checkValidity())
                return "Error: Not a valid input for the algorithm. \nRequired input : "+ algorithm.getRequiredInputDescription();
            return run(algorithm);

        } catch (Exception e) {
            e.printStackTrace();
            return "An internal error occurred while trying to run BFS: " + e.getMessage();
        }
    }


    private static String run(Algorithm algorithm)
    {
        if (!algorithm.checkValidity())
        {
            return "Error: the input graph is not a valid input for the algorithm. a valid input is : "+algorithm.getRequiredInputDescription();
        }
        else
        {
            if(algorithm.getClass() == DFS.class)
                Platform.runLater(algorithm::Run);
            else
                algorithm.Run();
            Platform.runLater(algorithm::DisplayResults);

        }

        return "Sure ! here are the results of the algorithm you asked.";
    }
}
