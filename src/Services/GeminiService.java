package Services;

import Controllers.Controller;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import javafx.application.Platform;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;

public class GeminiService {

    // Pulling the API Key from the Environment Variable
    private static final String API_KEY = System.getenv("GEMINI_API_KEY");
    // Updated model to gemini-1.5-flash as it's the stable version for this type of task
    private static final String GEMINI_URL = "https://generativelanguage.googleapis.com/v1beta/models/gemini-2.5-flash:generateContent?key=" + API_KEY;
    private static GeminiService singleton = null;
    private final HttpClient httpClient;
    private final ObjectMapper objectMapper;

    public static GeminiService getInstance() {
        if (singleton == null) {
            singleton = new GeminiService();
        }
        return singleton;
    }

    public GeminiService() {
        this.httpClient = HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(10))
                .build();
        this.objectMapper = new ObjectMapper();

        if (API_KEY == null || API_KEY.isEmpty()) {
            System.err.println("WARNING: GEMINI_API_KEY environment variable is not set.");
        }
    }

    /**
     * Sends a request to Google Gemini API.
     */
    public String generateContent(String promptText) {
        if (API_KEY == null || API_KEY.isEmpty()) {
            return "Error: Gemini API Key is missing.";
        }

        try {
            String escapedPrompt = promptText.replace("\\", "\\\\")
                    .replace("\"", "\\\"")
                    .replace("\n", "\\n")
                    .replace("\r", "\\r");

            ObjectNode root = objectMapper.createObjectNode();

            ArrayNode contents = objectMapper.createArrayNode();
            ObjectNode content = objectMapper.createObjectNode();
            ArrayNode parts = objectMapper.createArrayNode();

            ObjectNode textNode = objectMapper.createObjectNode();
            textNode.put("text", promptText);

            parts.add(textNode);
            content.set("parts", parts);
            contents.add(content);

            root.set("contents", contents);

            ObjectNode generationConfig = objectMapper.createObjectNode();
            generationConfig.put("temperature", 0.1);

            root.set("generationConfig", generationConfig);

            String jsonBody = objectMapper.writeValueAsString(root);

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(GEMINI_URL))
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(jsonBody))
                    .timeout(Duration.ofSeconds(30))
                    .build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() != 200) {
                System.out.println(response.body());
                return "Error: Status " + response.statusCode() + " - " + response.body();
            }

            JsonNode Sroot = objectMapper.readTree(response.body());

            // Safety check for the specific structure returned by Gemini
            if (!Sroot.has("candidates") || Sroot.path("candidates").isEmpty()) {
                return "Error: No candidates returned from Gemini.";
            }

            String resultText = Sroot.path("candidates")
                    .get(0)
                    .path("content")
                    .path("parts")
                    .get(0)
                    .path("text")
                    .asText();

            // Remove any possible markdown blocks
            if (resultText.contains("```")) {
                resultText = resultText.replaceAll("```json|```", "").trim();
            }

            return resultText;

        } catch (Exception e) {
            e.printStackTrace();
            return "Error: request failed - " + e.getMessage();
        }
    }


    public String getFinalPromptText(String userPrompt) {
        return """
        Instruction: You are a graph data generator. Your task is to convert the user's request into a strict JSON format. you must ONLY build graphs, not run algorithms or answer questions.
        
        ### JSON SCHEMA ###
        {
          "isDirected": boolean,
          "nodes": ["1", "2", "3", ...],
          "edges": [
            { "from": "1", "to": "2", "weight": 10, "capacity": 5 },
            ...
          ]
        }

        ### RULES ###
        1. Nodes MUST be strings representing numbers starting from 1.
        2. If directedness is not mentioned, the graph should be undirected (isDirected: false).
        3. If 'undirected', for every edge A->B, you MUST add a second edge object B->A.
        4. 'weight': use range if specified, otherwise default to random (1-10) if "random" is mentioned, else 0.
        5. 'capacity': use range if specified, otherwise default to random (1-50) if "random" is mentioned, else 0.
        6. Return ONLY the raw JSON. No markdown tags like ```json.

        ### EXAMPLE ###
        User: "Undirected graph 2 nodes, edge between 1 and 2, weight 5"
        Output: {
          "isDirected": false,
          "nodes": ["1", "2"],
          "edges": [
            {"from": "1", "to": "2", "weight": 5, "capacity": 0},
            {"from": "2", "to": "1", "weight": 5, "capacity": 0}
          ]
        }

        ### ACTUAL TASK ###
        User Request: "%s"
        Output:
        """.formatted(userPrompt);
    }

    public String getfinalPromptForExplainer(String graphReport) {
        return "You are an expert graph analyst. Analyze this JSON graph report and write a human-readable one-paragraph summary " +
                "including node/edge counts, directedness, and connectivity status:\n\n" + graphReport;
    }
}