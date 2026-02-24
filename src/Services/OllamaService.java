package Services;

import GraphVisualizer.AppSettings;
import Controllers.Controller;
import javafx.application.Platform;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;

public class OllamaService {

    private static final String OLLAMA_URL = "http://localhost:11434/api/generate";
    private static final String MODEL_NAME = "phi3:mini";
    private static OllamaService singleton = null;
    private final HttpClient httpClient;

    public static OllamaService getInstance() {
        if (singleton == null) {
            singleton = new OllamaService();
        }
        return singleton;
    }

    private OllamaService() {
        // Build a client with a longer timeout because AI inference takes time
        this.httpClient = HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(10))
                .build();
    }

    /**
     * Sends a request to Ollama.
     * @param promptText The prompt
     */
    public String generateContent(String promptText) {
        try {
            // Clean the prompt for JSON escaping (very important for local models)
            String escapedPrompt = promptText.replace("\\", "\\\\")
                    .replace("\"", "\\\"")
                    .replace("\n", "\\n")
                    .replace("\r", "\\r");

            String jsonBody = """
            {
              "model": "%s",
              "prompt": "%s",
              "stream": false
              %s
            }
            """.formatted(MODEL_NAME, escapedPrompt, ", \"format\": \"json\"");

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(OLLAMA_URL))
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(jsonBody))
                    .timeout(Duration.ofSeconds(60)) // Inference can be slow on CPU
                    .build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() != 200) {
                return "Error: Ollama returned status " + response.statusCode();
            }

            // The response from Ollama is a JSON containing the field "response"
            // To keep it simple without adding a JSON library yet, we do a basic extract:
            String body = response.body();
            int start = body.indexOf("\"response\":\"") + 12;
            int end = body.lastIndexOf("\",\"done\":");

            if (start > 11 && end > start) {
                String result = body.substring(start, end);
                // Unescape newlines and quotes returned in the JSON string
                return result.replace("\\n", "\n").replace("\\\"", "\"");
            }

            return body;
        } catch (Exception e) {
            Platform.runLater(() -> {
                // Showing the error on UI so user knows to start Ollama
                Controller.AlertError(new Exception("Make sure Ollama is running (ollama run phi3:mini). " + e.getMessage()));
            });
            return "Error: Local AI server not reachable.";
        }
    }


    public String getFinalPromptText(String userPrompt) {

        return "You are a graph data generator. Parse the user's request and return " +

                "ONLY a single, valid JSON object based on the 'GraphData' and 'EdgeData' " +

                "Java classes. The JSON must have these keys:\n" +

                "1. isDirected (boolean)\n" +

                "2. nodes (a list of strings that can be parsed to numbers, starting from 1 and not 0)\n" +

                "3. edges (a list of objects, each with 'from', 'to', 'weight', and 'capacity' keys):\n" +

                "   - 'from' and 'to' values must be strings that can be parsed to numbers from the nodes list.\n" +

                "   - 'weight':\n" +

                "     - FIRST, check if the user specified a range (e.g., 'random weights between 1 and 10'). If so, you MUST use that range.\n" +

                "     - SECOND, if the user just says 'random weights' (with no range), generate a random integer between 1 and 10 for each edge.\n" +

                "     - THIRD, if weight is not mentioned at all, default to 0.\n" +

                "   - 'capacity':\n" +

                "     - FIRST, check if the user specified a range for capacity (e.g., 'random capacity between 1 and 100'). If so, you MUST use that exact range.\n" +

                "     - SECOND, if the user just says 'random capacities' (with no range), generate a random integer between 1 and 50 for each edge.\n" +

                "     - THIRD, if capacity is not mentioned at all, you MUST default to 0.\n" +

                "   - If the graph is 'undirected', you MUST create the reverse edge (e.g., from 'B' to 'A') for every edge (from 'A' to 'B').\n\n" +

                "User Request: \"" + userPrompt + "\"";

    }



    public String getfinalPromptForExplainer(String graphReport) {

        return "You are an expert graph analyst. Your job is to take the " +

                "following pre-computed graph report (in JSON format) and write " +

                "a human-readable summary for a user.\n\n" +

                "Here is the graph report:\n" +

                graphReport + "\n\n" +

                "Please write a one-paragraph summary that includes:\n" +

                "1. The total number of nodes and edges.\n" +

                "2. Whether the graph is directed or undirected.\n" +

                "3. A clear statement, based on the isConnected flag, about whether " +

                "the graph is connected or has separate parts.";

    }
//    public String getFinalPromptText(String userRequest) {
//        return """
//        You are a graph data generator. Parse the user's request and return ONLY a valid JSON object.
//        The JSON must have these keys:
//        1. "isDirected" (boolean)
//        2. "nodes" (a list of strings representing numbers starting from 1)
//        3. "edges" (a list of objects with "from", "to", "weight", and "capacity"):
//           - "weight": If range mentioned, use it. If "random" mentioned, use 1-10. Else default 0.
//           - "capacity": If range mentioned, use it. If "random" mentioned, use 1-50. Else default 0.
//           - If undirected: provide edges for both directions.
//
//        User Request: "%s"
//        """.formatted(userRequest);
//    }
//
//    public String getfinalPromptForExplainer(String graphReport) {
//        return "You are an expert graph analyst. Analyze this JSON graph report and write a one-paragraph summary " +
//                "including node/edge counts, directedness, and connectivity status:\n\n" + graphReport;
//    }
}