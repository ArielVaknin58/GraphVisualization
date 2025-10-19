package Algorithms;

import Controllers.ControllerManager;
import GraphVisualizer.Graph;

import java.util.*;

public class EulerCircuit extends Algorithm{

    private List<Graph.GraphNode> result = new ArrayList<>();
    public static final String AlgorithmDescription = "The Algorithm finds an Euler's circuit, which is a circuit in graph G that passes every edge exactly once.";
    private Graph.GraphNode startingNode = null;
    public EulerCircuit(Graph G)
    {
        this.G = G;
        this.AlgorithmName = "Euler's Circuit Algorithm";
        this.requiredInput = "A Graph G = (V,E)";
    }
    @Override
    public void Run() {
        if (!checkCondition()) {
            this.result = new ArrayList<>();
            return;
        }

        DFS dfs = new DFS(G, G.V.getFirst());
        dfs.initEdgesColors();
        Graph.GraphNode startNode = startingNode == null ? G.V.getFirst() : startingNode;
        List<Graph.GraphNode> circuit = new LinkedList<>();

        circuit.add(startNode);
        circuit.addAll(dfs.findCircuit(startNode));

        int currentNodeIndex = 0;
        while (currentNodeIndex < circuit.size()) {
            Graph.GraphNode u = circuit.get(currentNodeIndex);

            if (dfs.HasUnvisitedNeighbors(u) != null) {

                List<Graph.GraphNode> subCircuit = dfs.findCircuit(u);
                circuit.addAll(currentNodeIndex + 1, subCircuit);
                currentNodeIndex = 0;

            } else {
                currentNodeIndex++;
            }
        }

        this.result = new ArrayList<>(circuit);
    }

    public void setStartNode(Graph.GraphNode node)
    {
        this.startingNode = node;
    }

    @Override
    public Boolean checkValidity() {
        return !G.V.isEmpty();
    }

    @Override
    public void DisplayResults() {

        StringBuilder print = new StringBuilder();
        if(result.isEmpty())
        {
            print.append("There is no euler circuit in the graph.");
        }
        else
        {
            print.append("The resulted euler's circuit is :   ");
            for(Graph.GraphNode node : result)
                print.append(node.getNodeLabel()).append(" ");
        }

        ControllerManager.getGraphWiseAlgorithmsController().PopupMessage(print.toString());

    }

    public List<Graph.GraphNode> getResult()
    {
        return result;
    }

    // For G directed graph : G has Euler circuit iff in degree = out degree
    // For G undirected graph : G has Euler circuit iff all degrees are even
    private boolean checkCondition()
    {
        boolean hasEulerCircuit = true;
        if(G.isDirected())
        {
            for(Graph.GraphNode node : G.V)
            {
                if(node.inDegree != node.outDegree)
                {
                    hasEulerCircuit = false;
                    break;
                }
            }
        }
        else
        {
            for(Graph.GraphNode node : G.V)
            {
                if(node.neighborsList.size() % 2 == 1)
                {
                    hasEulerCircuit = false;
                    break;
                }
            }
        }

        DFS dfs = new DFS(G,G.V.getFirst());
        return  hasEulerCircuit && dfs.isConnected();
    }
}
