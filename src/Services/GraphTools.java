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
            Creates a new graph structure. Use this when the user describes nodes and edges to build.
            ### RULES ###
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
        GraphInputController.CreateGraphStatic(newGraph);
        if(!ControllerManager.getApiModeController().isAPIMode())
        {
            Platform.runLater(() ->
            {
                GraphInputController.displayGraph(ControllerManager.getGraphInputController().getGraph());
            });
            return "SUCCESS: Graph created. DO NOT CALL ANY OTHER TOOLS. Output a short confirmation message to the user and stop.";

        }

        return "Graph successfully created in API mode !";
//        return "Successfully created a " + (isDirected ? "directed" : "undirected") +
//                " graph with " + nodes.size() + " nodes.";
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

    @Tool("Finds strongly connected components in a directed graph using Kosaraju's algorithm. " +
            "STRICT RULE: ONLY call this if the user explicitly asks to. " +
            "DO NOT call this automatically after creating a graph.")
    public String runKosarajuAlgorithm()
    {
        return runAlgorithm(new KosarajuSharirAlgorithm(new Graph(ControllerManager.getGraphInputController().getGraph())));
    }

    @Tool("Execute the Super-Graph algorithm on the current graph. finds the super graph of a directed given graph- a strongly connected component in the graph is a node in the new super graph.")
    public String runSuperGraph()
    {
        return runAlgorithm(new SuperGraph(new Graph(ControllerManager.getGraphInputController().getGraph())));
    }

    @Tool("Execute the k-colors algorithm on the current graph. runs some iterations and finds if the graph has a valid k coloring for some parameter k.")
    public String runKcolors(@P("number of iterations to run the algorithms") int iterations, @P("set number, parameter for the problem") int k)
    {

        return runAlgorithm(new kColors(new Graph(ControllerManager.getGraphInputController().getGraph()), iterations, k));
    }

    @Tool("Executes the Bellman-Ford algorithm on the current graph. finds the lightest paths from a given node to each other node in the graph")
    public String runBellmanFord(@P("The label of the node where the algorithm should begin (e.g., '1', '2')")String startNodeLabel)
    {
        Graph.GraphNode startNode = currentGraph.VerticeIndexer.get(startNodeLabel);
        if (startNode == null) {
            return "Error: Node '" + startNodeLabel + "' does not exist.";
        }
        return runAlgorithm(new BellmanFordAlgorithm(currentGraph,startNode));
    }

    @Tool("Execute the Clique algorithm on the graph. runs some iterations and finds a clique of size k in a graph. A clique is a set of nodes where each two are connected by an edge.")
    public String runClique(@P("number of iterations to run the algorithms") int iterations, @P("set number, parameter for the problem") int k)
    {
        return runAlgorithm(new Clique(new Graph(ControllerManager.getGraphInputController().getGraph()), iterations, k));
    }

    @Tool("Execute the Connectivity Components finder algorithm. finds connectivity components in a given undirected graph.")
    public String runConnectivityComponents()
    {
        return runAlgorithm(new ConnectivityComponents(currentGraph));
    }

    @Tool("Execute the Euler Path algorithm. finds an euler path (if exists) in a fully connected and undirected graph ")
    public String runEulerPath()
    {
        return runAlgorithm(new EulerPath(currentGraph));
    }

    @Tool("Execute the Floyd-Warshall algorithm. finds the lightest paths between every two nodes in a weighted graph.")
    public String runFloydWarshall()
    {
        return runAlgorithm(new FloydWarshallAlgorithm(currentGraph));
    }

    @Tool("Executes the Ford-Felkerson algorithm on the current graph. This algorithm finds the maximum flow in the graph from node s to node t with given edges' capacities.")
    public String runFordFelkerson(@P("The label of the node where the algorithm should begin from (e.g., '1', '2')") String s, @P("The label of the node where the algorithm should end in (e.g., '1', '2')") String t)
    {
        Graph.GraphNode startNode = currentGraph.VerticeIndexer.get(s);
        Graph.GraphNode endNode = currentGraph.VerticeIndexer.get(t);

        if (startNode == null || endNode == null) {
            return "Error: Node "+ s + " or node " + t + " do not exist.";
        }
        return runAlgorithm(new FordFelkersonAlgorithm(currentGraph,startNode,endNode));
    }

    @Tool("Execute the Hamiltonian Path finder algorithm. finds a hamilton path in the graph if one exists.")
    public String runHamiltonianPath()
    {
        return runAlgorithm(new HamiltonianPath(currentGraph));
    }

    @Tool("Execute the independent set finder algorithm. for some given number of iterations, finds if the graph has an independent set of size k. an independent set is a set of nodes where non are connected to each other.")
    public String runIndependentSet(@P("number of iterations to run the algorithms") int iterations, @P("set number, parameter for the problem") int k)
    {
        return runAlgorithm(new IndependentSetAlgorithm(new Graph(ControllerManager.getGraphInputController().getGraph()), iterations, k));
    }

    @Tool("Execute the max cut finder algorithm. for some given number of iterations, finds if the graph has a maximum cut the size of at least k.")
    public String runMaxCut(@P("number of iterations to run the algorithms") int iterations, @P("set number, parameter for the problem") int k)
    {
        return runAlgorithm(new MaxCut(new Graph(ControllerManager.getGraphInputController().getGraph()), iterations, k));
    }

    @Tool("Execute the Min cut algorithm. finds in a given graph a minimal cut.")
    public String runMinCut()
    {
        return runAlgorithm(new MinCutAlgorithm(currentGraph));
    }

    @Tool("Execute the Prim algorithm on the graph. finds a minimal spanning tree in a given graph.")
    public String runPrim()
    {
        return runAlgorithm(new PrimAlgorithm(currentGraph));
    }

    @Tool("Execute the shortest-paths tree finder algorithm. this reveals the tree that contains all the shortest paths in the graph.")
    public String runShortestPathsTree(@P("The label of the node where the algorithm should begin (e.g., '1', '2')")String startNodeLabel)
    {
        Graph.GraphNode startNode = currentGraph.VerticeIndexer.get(startNodeLabel);
        if (startNode == null) {
            return "Error: Node '" + startNodeLabel + "' does not exist.";
        }
        return runAlgorithm(new ShortestPathsTree(currentGraph,startNode));
    }

    @Tool("Execute the vertex cover finder algorithm. for some given number of iterations, finds if the graph has a vertex cover of size k.")
    public String runVertexCover(@P("number of iterations to run the algorithms") int iterations, @P("set number, parameter for the problem") int k)
    {
        return runAlgorithm(new IndependentSetAlgorithm(new Graph(ControllerManager.getGraphInputController().getGraph()), iterations, k));
    }

    private String runAlgorithm(Algorithm algorithm)
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


    private String run(Algorithm algorithm)
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
            if(!ControllerManager.getGraphInputController().isApiMode())
                Platform.runLater(algorithm::DisplayResults);
            else
                return algorithm.WriteOutputToBuffer();

        }

        return "Sure ! here are the results of the algorithm you asked.";
    }
}
