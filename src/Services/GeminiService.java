package Services;

import GraphVisualizer.AppSettings;
import com.google.genai.Client;
import com.google.genai.types.GenerateContentResponse;
import com.google.genai.errors.ApiException; // Correct package is "exceptions"
import java.io.IOException; // This is required for network calls

import Controllers.Controller; // For your AlertError
import javafx.application.Platform;

public class GeminiService {

    private Client client;
    private final String apiKey;
    private boolean isReady = false;
    private static GeminiService singleton = null;


    public static GeminiService getInstance() {
        if(singleton == null) {
            singleton = new GeminiService();
        }
        return singleton;
    }

    private GeminiService() {
        this.apiKey = System.getenv("GEMINI_API_KEY");

        if (this.apiKey == null || this.apiKey.isEmpty()) {
            System.err.println("FATAL ERROR: GEMINI_API_KEY environment variable not set.");
            return;
        }

        this.client = Client.builder().apiKey(this.apiKey).build();
        this.isReady = true;
    }

    public boolean isReady() {
        return this.isReady;
    }

    public String generateContent(String promptText) {
        if (!isReady()) {
            return "Error: GeminiService is not initialized. Please set the GEMINI_API_KEY.";
        }

        try {
            GenerateContentResponse response = client.models.generateContent(AppSettings.AI_model_used, promptText, null);
            return response.text();

        } catch (ApiException e) {
            Platform.runLater(() -> {
                Controller.AlertError(e);
            });
        }

        return "Error: Failed to get response.";
    }

    public String getFinalPromptText(String userPrompt) {
        String finalPrompt = "You are a graph data generator. Parse the user's request and return " +
                "ONLY a single, valid JSON object based on the 'GraphData' and 'EdgeData' " +
                "Java classes. The JSON must have these keys: isDirected (boolean), " +
                "nodes (a list of strings that can be parsed to numbers, starting from 1 and not 0), and edges (a list of objects, " +
                "each with 'from' and 'to' string keys where the values can be parsed to numbers. and if the graph is undirected then also create the other edge. also edges can have weights so if it isn't mentioned then the weight of the edge is 0).\n\n" +
                "User Request: \"" + userPrompt + "\"";
        return finalPrompt;
    }

    public String getfinalPromptForExplainer(String graphReport) {
        String prompt = "You are an expert graph analyst. Your job is to take the " +
                "following pre-computed graph report (in JSON format) and write " +
                "a human-readable summary for a user.\n\n" +
                "Here is the graph report:\n" +
                graphReport + "\n\n" +
                "Please write a one-paragraph summary that includes:\n" +
                "1. The total number of nodes and edges.\n" +
                "2. Whether the graph is directed or undirected.\n" +
                "3. A clear statement, based on the isConnected flag, about whether " +
                "the graph is connected or has separate parts.";
        return prompt;
    }
}