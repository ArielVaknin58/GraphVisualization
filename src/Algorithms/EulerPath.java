package Algorithms;

import Controllers.Controller;
import Controllers.ControllerManager;
import Exceptions.InvalidEdgeException;
import GraphVisualizer.Graph;
import java.util.ArrayList;
import java.util.List;

public class EulerPath extends Algorithm{

    private List<Graph.GraphNode> result = new ArrayList<>();
    public static final String AlgorithmDescription = "The Algorithm finds an Euler's path, which is a path in graph G that passes every edge exactly once.";

    public EulerPath(Graph G)
    {
        this.G = G;
        this.AlgorithmName = "Euler Circuit Algorithm";
        this.requiredInput = "An undirected,fully connected graph G=(V,E)";
    }

    @Override
    public void Run() {
        // Preliminary checks
        if (G.V.isEmpty() || !checkCondition()) {
            this.result = new ArrayList<>();
            return;
        }

        // Find the odd-degree vertices first
        List<Graph.GraphNode> oddNodes = new ArrayList<>();
        for (Graph.GraphNode node : G.V) {
            if ((node.getDegree()) % 2 == 1) {
                oddNodes.add(node);
            }
        }

        // Case 1: The graph has an Euler Circuit (0 odd nodes)
        if (oddNodes.isEmpty()) {
            EulerCircuit eulerCircuit = new EulerCircuit(G);
            eulerCircuit.Run();
            this.result = eulerCircuit.getResult();
            return; // Done
        }

        // Case 2: The graph has an Euler Path (2 odd nodes)
        try {
            Graph copyG = new Graph(G);
            Graph.GraphNode oddVertice1 = copyG.VerticeIndexer.get(oddNodes.get(0).getNodeLabel());
            Graph.GraphNode oddVertice2 = copyG.VerticeIndexer.get(oddNodes.get(1).getNodeLabel());

            // 1. Create a new temporary ("dummy") vertex
            String dummyNodeLabel = String.valueOf(copyG.V.size()+1); // A unique name
            copyG.createNode(dummyNodeLabel);
            Graph.GraphNode dummyNode = copyG.VerticeIndexer.get(dummyNodeLabel);

            // 2. Connect the odd vertices to the new vertex
            copyG.createEdge(oddVertice1.getNodeLabel(), dummyNodeLabel,0);
            copyG.createEdge(dummyNodeLabel, oddVertice1.getNodeLabel(),0);
            copyG.createEdge(oddVertice2.getNodeLabel(), dummyNodeLabel,0);
            copyG.createEdge(dummyNodeLabel, oddVertice2.getNodeLabel(),0);

            // 3. Find the Euler Circuit on the MODIFIED graph
            EulerCircuit eulerCircuit = new EulerCircuit(copyG);
            eulerCircuit.setStartNode(oddVertice1); // Start anywhere, e.g., one of the odd nodes
            eulerCircuit.Run();
            List<Graph.GraphNode> circuit = eulerCircuit.getResult();

            // 4. Reconstruct the path by removing the dummy node
            int dummyIndex = -1;
            for (int i = 0; i < circuit.size(); i++) {
                if (circuit.get(i).getNodeLabel().equals(dummyNodeLabel)) {
                    dummyIndex = i;
                    break;
                }
            }

            if (dummyIndex != -1) {
                // The circuit will contain a sequence like [...U, Dummy, V...].
                // The path is found by "rotating" the circuit around the dummy node.
                List<Graph.GraphNode> path = new ArrayList<>();

                // Add the part of the path *after* the dummy node
                path.addAll(circuit.subList(dummyIndex + 1, circuit.size() - 1)); // -1 to exclude duplicate start/end node

                // Add the part of the path *before* the dummy node
                path.addAll(circuit.subList(0, dummyIndex));

                this.result = path;
                return;
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        // If we reach here, something went wrong.
        this.result = new ArrayList<>();
    }

    private boolean checkCondition()
    {
        int oddDegCounter = 0;
        for(Graph.GraphNode node : G.V)
        {
            if((node.inDegree + node.outDegree) % 2 == 1)
                oddDegCounter++;
        }
        return oddDegCounter == 0 || oddDegCounter == 2;

    }

    @Override
    public Boolean checkValidity() {
        if(G.V.isEmpty())
            return false;
        DFS dfs = new DFS(G,G.V.getFirst());
        return !this.G.isDirected() && dfs.isConnected();
    }

    @Override
    public void DisplayResults() {

        StringBuilder print = new StringBuilder();
        if(result.isEmpty())
        {
            print.append("There is no euler path in the graph.");
        }
        else
        {
            print.append("The resulted euler's path is :   ");
            for(Graph.GraphNode node : result)
                print.append(node.getNodeLabel()).append(" ");
        }

        ControllerManager.getGraphWiseAlgorithmsController().PopupMessage(print.toString());
    }
}
