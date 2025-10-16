package Algorithms;

import Controllers.ControllerManager;
import GraphVisualizer.Graph;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class TopologicalSort extends Algorithm {

    private List<Graph.GraphNode> result;

    public static final String AlgorithmDescription = "A topological sort is a linear ordering of vertices in a directed acyclic graph (DAG), where for every directed edge from vertex u to vertex v, u comes before v in the ordering.";

    public TopologicalSort(Graph G)
    {
        super();
        result = new ArrayList<>();
        this.G = G;
        requiredInput = "Acyclic directed Graph G = (V,E)";
        this.AlgorithmName = "Topological Sort";

    }

    @Override
    public void Run() {

        Graph.GraphNode source = checkForSources();
        while(source != null)
        {
            result.add(source);
            G.RemoveVertice(source);
            source = checkForSources();
        }

    }

    @Override
    public Boolean checkValidity() {
        DFS dfs = new DFS(G,G.V.getFirst());
        return dfs.HasCycle() && this.G.isDirected();
    }

    @Override
    public void DisplayResults() {

        StringBuilder print = new StringBuilder("The Topological Sort is: \n");
        Iterator<Graph.GraphNode> it = result.iterator();
        while (it.hasNext()) {
            Graph.GraphNode current = it.next();
            print.append(current.getNodeLabel());
            if (it.hasNext()) {
                print.append(" --> ");
            }
        }

        ControllerManager.getGraphWiseAlgorithmsController().PopupMessage(print.toString());
    }


    private Graph.GraphNode checkForSources()
    {
        for(Graph.GraphNode node : G.getNodes())
        {
            if (node.inDegree == 0)
                return node;
        }

        return null;
    }
}
