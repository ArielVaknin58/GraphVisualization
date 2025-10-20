package Algorithms;

import Controllers.ControllerManager;
import GraphVisualizer.Graph;

import java.util.ArrayList;
import java.util.List;

public class HamiltonianPath extends Algorithm{

    public static final String AlgorithmDescription = "The algorithm gets a graph G=(V,E) and returns a path where each vertice appears exactly once.";
    private List<Graph.GraphNode> result;

    public HamiltonianPath(Graph G)
    {
        this.G = G;
        this.AlgorithmName = "Hamiltonian Path Algorithm";
        this.requiredInput = "Acyclic directed Graph";
        this.result = new ArrayList<>();
    }
    @Override
    public void Run() {

        if(G.V.isEmpty())
            return;
        TopologicalSort sort = new TopologicalSort(this.G);
        if(sort.isSingularSort())
        {
            result = sort.getResult();
        }

    }

    @Override
    public Boolean checkValidity() {
        if(G.V.isEmpty())
            return true;
        DFS dfs = new DFS(G,G.V.getFirst());
        return dfs.isAcyclic() && this.G.isDirected();
    }

    @Override
    public void DisplayResults() {
        StringBuilder print = new StringBuilder();
        if(result.isEmpty())
        {
            print.append("There is no hamiltonian path in the graph.");
            ControllerManager.getGraphWiseAlgorithmsController().infoPopup(print.toString());

        }
        else
        {
            print.append("The resulted hamiltonian path is :   ");
            for(Graph.GraphNode node : result)
                print.append(node.getNodeLabel()).append(" ");
            ControllerManager.getGraphWiseAlgorithmsController().SuccessPopup(print.toString());

        }

    }
}
