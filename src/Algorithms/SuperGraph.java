package Algorithms;

import Controllers.ControllerManager;
import GraphVisualizer.DirectedEdge;
import GraphVisualizer.Graph;
import Services.GraphData;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

import static Controllers.Controller.AlertError;

public class SuperGraph extends Algorithm{

    private Hashtable<String, Set<String>> components;
    private Hashtable<String,String> ksaResult;


    public SuperGraph(Graph graph)
    {
        INIT(graph);
    }

    @JsonCreator
    public SuperGraph()
    {
        INIT(ControllerManager.getGraphInputController().getGraph());
    }

    @Override
    protected void INIT(Graph graph) {
        this.G = graph;
        AlgorithmDescription = "The algorithm find Super graph H of graph G, where every connected component in G is a vertice in H";
        this.AlgorithmName = "Super Graph Algorithm";
        this.requiredInput = "A directed graph";
        this.components = new Hashtable<>();
        this.ksaResult = new Hashtable<>();
    }

    @Override
    protected String UpdateParams(Map<String, String> params) {
        return null;
    }

    @Override
    public void Run() {

        KosarajuSharirAlgorithm ksa = new KosarajuSharirAlgorithm(G);
        ksa.Run();
        this.ksaResult = ksa.getResult();
    }


    public Hashtable<String, Set<String>> getComponents()
    {
        return components;
    }

    private String findComponent(String Nodelabel)
    {
        for(String componentKey : components.keySet())
        {
            if(components.get(componentKey).contains(Nodelabel))
                return ksaResult.get(componentKey);
        }
        return null;
    }
    @Override
    public Boolean checkValidity() {
        return this.G.isDirected();
    }

    @Override
    public void DisplayResults() {
        loadResultsPane();
    }

    @Override
    public void CreateOutputGraph() {
        this.graphResult = new Graph(true);
        for(String Hvertice : ksaResult.keySet())
        {
            this.graphResult.createNode(ksaResult.get(Hvertice));
            components.put(Hvertice, new HashSet<>(Arrays.asList(ksaResult.get(Hvertice).split(","))));
        }
        for(DirectedEdge edge : G.E)
        {
            String fromLabel = edge.getFrom().getNodeLabel();
            String toLabel = edge.getTo().getNodeLabel();
            String fromComponent = findComponent(fromLabel);
            String toComponent = findComponent(toLabel);

            assert fromComponent != null;
            if(!fromComponent.equals(toComponent))
            {
                this.graphResult.createEdge(fromComponent,toComponent);
            }

        }
    }

    @Override
    public String WriteOutputToBuffer() {
        CreateOutputGraph();

        StringWriter stringWriter = new StringWriter();
        try (PrintWriter out = new PrintWriter(stringWriter)) {
            out.println("--- "+this.AlgorithmName+" Results ---");
            Gson gson = new GsonBuilder().setPrettyPrinting().create();
            gson.toJson(new GraphData(graphResult), out);
            out.println("\n----------------------------------------------\n\n");

        }
        catch(Exception e)
        {
            return "an error occured : "+e.getMessage();
        }

        return stringWriter.toString();
    }

    @Override
    protected void WriteOutputToFile(Path fileName) {
        CreateOutputGraph();

        try (PrintWriter out = new PrintWriter(
                Files.newBufferedWriter(fileName, StandardCharsets.UTF_8))) {
            out.println("--- "+this.AlgorithmName+" Results ---");
            Gson gson = new GsonBuilder().setPrettyPrinting().create();
            gson.toJson(new GraphData(graphResult), out);
            out.println("\n----------------------------------------------\n\n");

        }
        catch(Exception e)
        {
            AlertError(e);
        }
    }
}
