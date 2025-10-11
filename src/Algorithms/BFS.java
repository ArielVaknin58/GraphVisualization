package Algorithms;

import Controllers.ControllerManager;
import GraphVisualizer.Graph;

import java.util.*;

public class BFS extends Algorithm{

    private Graph.GraphNode inputNode;
    private HashMap<String, Integer> result = new HashMap();

    public BFS(Graph G, Graph.GraphNode inputNode)
    {
        this.G = G;
        this.inputNode = inputNode;
        result.put(inputNode.getNodeLabel(),0);
        for(Graph.GraphNode node : G.V)
        {
            if(!Objects.equals(node.getNodeLabel(), inputNode.getNodeLabel()))
                result.put(node.getNodeLabel(),Integer.MAX_VALUE);
        }
        this.AlgorithmName = "BFS";
        this.requiredInput = "A Graph G = (V,E) and a node u from V";
        this.AlgorithmDescription = "The Breadth First Search Algorithm gets a given graph and a vertice v, and returns a list of the lengths of the shortest paths from v to each vertice in G. (can also produce the paths themselves).";
    }
    @Override
    public String getAlgorithmName() {
        return this.AlgorithmName;
    }

    @Override
    public String getAlgorithmDescription() {
        return this.AlgorithmDescription;
    }

    @Override
    public String getRequiredInputDescription() {
        return this.requiredInput;
    }

    @Override
    public void Run() {
        Queue<Graph.GraphNode> Q = new LinkedList<>();
        Q.add(inputNode);

        while (!Q.isEmpty()) {
            Graph.GraphNode current = Q.remove();
            int currentDistance = result.get(current.getNodeLabel());

            for (Graph.GraphNode node : current.neighborsList) {

                // If neighbor has no distance yet (infinity)
                Integer value = result.get(node.getNodeLabel());
                if (currentDistance + 1 < value) {
                    result.put(node.getNodeLabel(), currentDistance + 1);
                    Q.add(node);
                }

            }
        }
    }


    @Override
    public Boolean checkValidity() {
        return true;
    }

    @Override
    public void DisplayResults() {

        StringBuilder print = new StringBuilder();
        print.append("The lengths of the shortest paths from vertice "+this.inputNode.getNodeLabel()+" are :\n");
        Iterator<String> it = result.keySet().iterator();
        while(it.hasNext())
        {
            String currentNode = it.next();
            Integer current = result.get(currentNode);
            print.append(currentNode +" : "+ current +"  ");

        }

        ControllerManager.getVerticeWiseAlgorithmsController().PopupMessage(print.toString());
    }
}
