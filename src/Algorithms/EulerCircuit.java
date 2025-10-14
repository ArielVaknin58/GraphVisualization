package Algorithms;

import GraphVisualizer.Graph;

import java.util.*;

public class EulerCircuit extends Algorithm{

    private List<Graph.GraphNode> result = new ArrayList<>();

    public EulerCircuit(Graph G)
    {
        this.G = G;
        this.AlgorithmName = "Euler's Circuit Algorithm";
        this.requiredInput = "A Graph G = (V,E)";
        this.AlgorithmDescription = "The Algorithm finds an Euler's circuit, which is a circuit in graph G that passes every edge exactly once.";
    }
    @Override
    public void Run() {

        if(checkCondition())
        {
            DFS dfs = new DFS(G,G.V.getFirst());
            result = dfs.findCircuit(G.V.getFirst());
            int[] indices = CheckForDuplicates(result);
            int duplicate1Index = indices[0];
            int duplicate2Index = indices[1];

            while(duplicate1Index != -1 && duplicate2Index != -1)
            {
                duplicate1Index = Math.min(duplicate1Index, duplicate2Index);
                duplicate2Index = Math.max(duplicate1Index, duplicate2Index);

                // ✅ remove elements between the duplicates (exclusive)
                if (duplicate2Index - duplicate1Index > 1) {
                    result.subList(duplicate1Index + 1, duplicate2Index).clear();
                }

                indices = CheckForDuplicates(result);
                duplicate1Index = indices[0];
                duplicate2Index = indices[1];
            }
        }
    }

    private int[] CheckForDuplicates(List<Graph.GraphNode> list) {
        Map<Graph.GraphNode, Integer> seen = new HashMap<>();

        for (int i = 0; i < list.size(); i++) {
            Graph.GraphNode node = list.get(i);
            if (seen.containsKey(node)) {
                return new int[]{seen.get(node), i}; // first and second indices
            }
            seen.put(node, i);
        }
        return new int[]{-1, -1}; // no duplicates found
    }

    @Override
    public Boolean checkValidity() {
        return true;
    }

    @Override
    public void DisplayResults() {

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
                if((node.inDegree + node.outDegree) % 2 == 1)
                {
                    hasEulerCircuit = false;
                    break;
                }
            }
        }
        return  hasEulerCircuit;
    }
}
