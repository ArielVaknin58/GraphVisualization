package Algorithms;

import Controllers.Controller;
import Controllers.ControllerManager;
import Controllers.GraphResultController;
import GraphVisualizer.AppSettings;
import GraphVisualizer.DirectedEdge;
import GraphVisualizer.Graph;
import GraphVisualizer.ThemeManager;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static Controllers.Controller.AlertError;

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
        TopologicalSort sort = new TopologicalSort(new Graph(this.G));
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
        if (result.isEmpty()) {
            ControllerManager.getGraphInputController().infoPopup("The graph contains a negative-weight cycle !");
            return;
        }
        loadResultsPane();
    }

    @Override
    public void CreateOutputGraph() {
        this.graphResult = new Graph(this.G);
        for(int i = 0 ; i < result.size() - 1; i++)
        {
            Graph.GraphNode node = graphResult.VerticeIndexer.get(result.get(i).getNodeLabel());
            node.ChangeColor(Color.RED);
            for(DirectedEdge edge : node.connectedEdges)
            {
                if(edge.getTo().equals(result.get(i+1)))
                    edge.ChangeColor(Color.RED);
            }
        }
        graphResult.VerticeIndexer.get(result.getFirst().getNodeLabel()).ChangeColor(Color.BLUEVIOLET);
        graphResult.VerticeIndexer.get(result.getLast().getNodeLabel()).ChangeColor(Color.BLUEVIOLET);
    }

    @Override
    protected void WriteOutputToFile(Path fileName) {
        try (PrintWriter out = new PrintWriter(
                Files.newBufferedWriter(fileName, StandardCharsets.UTF_8))) {
            out.println("--- "+this.AlgorithmName+" Results ---");
            int counter = 1;
            for (Graph.GraphNode node : result) {
                if(counter % AppSettings.VERTICES_IN_LINE_IN_FILES == 0 || node.equals(result.getLast()))
                {
                    out.println(node.getNodeLabel());
                    if(!node.equals(result.getLast()))
                        out.print("--> ");
                }
                else
                {
                    out.print(node.getNodeLabel() + "--> ");
                }
                counter++;
            }
            out.println("----------------------------------------------\n\n");

        }
        catch(Exception e)
        {
            AlertError(e);
        }
    }
}
