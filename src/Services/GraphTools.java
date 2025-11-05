package Services;

import Algorithms.Algorithm;
import Algorithms.*;
import Controllers.Controller;
import Controllers.ControllerManager;
import Controllers.GraphInputController;
import GraphVisualizer.AppSettings;
import GraphVisualizer.Graph;
import GraphVisualizer.ThemeManager;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;

import static Controllers.Controller.AlertError;

public class GraphTools {

    private static Graph currentGraph;
    private static Graph.GraphNode InputNode;


    public GraphTools()
    {
        currentGraph = ControllerManager.getGraphInputController().getGraph();
    }
    public static String runBFS(String startNodeLabel)
    {
        Graph.GraphNode startNode = currentGraph.VerticeIndexer.get(startNodeLabel);
        if (startNode == null) {
            return "Error: Node '" + startNodeLabel + "' does not exist.";
        }
        return runAlgorithm(new BFS(currentGraph,startNode));
    }

    public static String runDFS(String startNodeLabel)
    {
        Graph.GraphNode startNode = currentGraph.VerticeIndexer.get(startNodeLabel);
        if (startNode == null) {
            return "Error: Node '" + startNodeLabel + "' does not exist.";
        }
        return runAlgorithm(new DFS(currentGraph,startNode));
    }

    public static String runBiPartite(String startNodeLabel)
    {
        return runAlgorithm(new BiPartite(currentGraph));
    }

    public static String runKosarajuAlgorithm(String input)
    {
        return runAlgorithm(new KosarajuSharirAlgorithm(new Graph(ControllerManager.getGraphInputController().getGraph())));
    }

    public static String runSuperGraphAlgorithm(String input)
    {
        return runAlgorithm(new SuperGraph(new Graph(ControllerManager.getGraphInputController().getGraph())));
    }
    private static String runAlgorithm(Algorithm algorithm)
    {
        try {
            if (currentGraph == null) {
                return "Error: Graph is not loaded.";
            }

            return run(algorithm);

        } catch (Exception e) {
            e.printStackTrace();
            return "An internal error occurred while trying to run BFS: " + e.getMessage();
        }
    }


    public static String CreateDescribedGraph(String input)
    {
        GeminiService gs = GeminiService.getInstance();
        String finalPrompt = gs.getFinalPromptText(input);

        new Thread(() -> {

            String jsonResponse = gs.generateContent(finalPrompt);

            int firstBrace = jsonResponse.indexOf('{');
            int lastBrace = jsonResponse.lastIndexOf('}');

            final String cleanedJson;

            if (firstBrace == -1 || lastBrace == -1 || lastBrace <= firstBrace) {
                // If we can't find a { or }, the response is bad.
                Platform.runLater(() -> {
                    Controller.AlertError(new Exception("The AI returned data I couldn't understand:\n"));
                    System.out.println(jsonResponse);
                });
                return;
            }

            cleanedJson = jsonResponse.substring(firstBrace, lastBrace + 1);
            final GraphData graphData;
            try {
                ObjectMapper objectMapper = new ObjectMapper();
                graphData = objectMapper.readValue(cleanedJson, GraphData.class);
            } catch (Exception e) {
                e.printStackTrace();
                Platform.runLater(() -> {
                    Controller.AlertError(new Exception("The AI returned data I couldn't understand:\n"));
                    System.out.println(cleanedJson);
                });
                return;
            }
            Platform.runLater(() -> {
                try {
                    GraphInputController.CreateGraphStatic(graphData);
                    currentGraph = ControllerManager.getGraphInputController().getGraph();
                } catch (Exception e) {
                    Controller.AlertError(e);
                }
            });

        }).start();
        return "Sure ! here is the graph you requested.";
    }

    public static String ExplainGraph(String input)
    {
        GeminiService gs = GeminiService.getInstance();
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        String jsonReport = gson.toJson(currentGraph.getGraphReportMap());
        return ExplainGraphWithPrompt(gs.getfinalPromptForExplainer(jsonReport));
    }

    private static String ExplainGraphWithPrompt(String finalPrompt) {
        return GeminiService.getInstance().generateContent(finalPrompt);
    }


    private static String run(Algorithm algorithm)
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
            Platform.runLater(algorithm::DisplayResults);

        }

        return "Sure ! here are the results of the algorithm you asked.";
    }
}
