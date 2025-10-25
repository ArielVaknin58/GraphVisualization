package Algorithms;


import GraphVisualizer.DirectedEdge;
import GraphVisualizer.Graph;
import javafx.scene.paint.Color;
import java.util.*;

import static Controllers.Controller.AlertError;

public class KosarajuSharirAlgorithm extends Algorithm{

    public static final String AlgorithmDescription = "The algorithm find strongly connected components in a given directed graph.";
    private Hashtable<String,String> result;

    public KosarajuSharirAlgorithm(Graph graph)
    {
        this.G = graph;
        this.AlgorithmName = "Kosaraju Sharir Algorithm";
        this.requiredInput = "A directed graph";
        this.result = new Hashtable<>();
    }


    @Override
    public void Run() {

        if(this.G.V.isEmpty())
            return;
        DFS dfs = new DFS(this.G,this.G.V.getFirst());
        List<Graph.GraphNode> finishTimesList = dfs.DFSWithEndTimeList();
        Graph transpose = this.G.Transpose();
        DFS transposeDFS = new DFS(transpose,null);
        for(Graph.GraphNode node : finishTimesList.reversed())
        {
            String component = transposeDFS.FindDirectedComponent(transpose.VerticeIndexer.get(node.getNodeLabel()));
            if(!component.isEmpty())
                result.put(node.getNodeLabel(),component);
        }

    }

    @Override
    public void CreateOutputGraph()
    {
        SuperGraph sg = new SuperGraph(this.G);
        sg.Run();
        sg.CreateOutputGraph();
        Hashtable<String,Set<String>> components = sg.getComponents();
        List<Color> colors = generateColors(components.size());
        this.graphResult = new Graph(this.G);
        int componentNumber = 0;
        for(String componentIndex : components.keySet())
        {
            Set<String> verticesLabels = components.get(componentIndex);
            for(String verticeLabel : verticesLabels)
            {
                Graph.GraphNode node = graphResult.VerticeIndexer.get(verticeLabel);
                node.ChangeColor(colors.get(componentNumber));
            }

            for(DirectedEdge edge : graphResult.E)
            {
                if(verticesLabels.contains(edge.getFrom().getNodeLabel()) && verticesLabels.contains(edge.getTo().getNodeLabel()))
                    edge.ChangeColor(colors.get(componentNumber));
            }

            componentNumber++;
        }
    }

    @Override
    public Boolean checkValidity() {
        return this.G.isDirected();
    }

    @Override
    public void DisplayResults() {
        loadResultsPane();
    }

    public Hashtable<String,String> getResult()
    {
        return result;
    }

    public static List<Color> generateColors(int n) {
        List<Color> colors = new ArrayList<>();
        if (n <= 0) {
            return colors;
        }

        double hueStep = 360.0 / n;

        for (int i = 0; i < n; i++) {
            double currentHue = i * hueStep;
            Color color = Color.hsb(currentHue, 1.0, 1.0);
            colors.add(color);
        }

        return colors;
    }
}
