package Controllers;

import GraphVisualizer.Graph;
import Services.ChatRecord; // Make sure this is: public record ChatRecord(String role, String content) {}
import Services.OllamaService;
import Services.GraphData;
import Services.GraphTools;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import javafx.application.Platform; // Import this!
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.text.Text; // Import Text and TextFlow
import javafx.scene.text.TextFlow;

import java.util.List;
import java.util.stream.Collectors;

public class ChatController extends Controller {

    @FXML
    private ListView<ChatRecord> chatView;

    // 2. The history list MUST be of type ChatRecord
    private ObservableList<ChatRecord> chatHistory = FXCollections.observableArrayList();

    @FXML
    private Button sendButton;
    @FXML
    private TextArea inputArea;

    private GraphTools tools;
    private OllamaService ollamaService = OllamaService.getInstance();
    private Gson gson = new GsonBuilder().setPrettyPrinting().create(); // Create this once
    //private GenerateContentConfig geminiToolConfig;


    @FXML
    public void initialize() {

        tools = new GraphTools();
        //            Method runBFS = GraphTools.class.getMethod("runBFS",String.class);
//            Method runDFS = GraphTools.class.getMethod("runDFS",String.class);
//            Method runBipartite = GraphTools.class.getMethod("runBiPartite",String.class);
//            Method createGraph = GraphTools.class.getMethod("CreateDescribedGraph",String.class);
//            Method explainGraph = GraphTools.class.getMethod("ExplainGraph",String.class);
//            Method kosarajuAlgorithm = GraphTools.class.getMethod("runKosarajuAlgorithm",String.class);
//            Method superGraph = GraphTools.class.getMethod("runSuperGraphAlgorithm",String.class);
//
//            geminiToolConfig = GenerateContentConfig.builder().tools(Tool.builder().functions(runBFS).build(),
//                                                                     Tool.builder().functions(runDFS).build(),
//                                                                     Tool.builder().functions(runBipartite).build(),
//                                                                     Tool.builder().functions(createGraph).build(),
//                                                                     Tool.builder().functions(explainGraph).build(),
//                                                                     Tool.builder().functions(kosarajuAlgorithm).build(),
//                                                                     Tool.builder().functions(superGraph).build()
//
//
//                    ).build();

        ControllerManager.setChatController(this);
        chatView.setItems(chatHistory);
        chatView.setCellFactory(lv -> new ListCell<ChatRecord>() {

            private Text senderText = new Text();
            private Text contentText = new Text();
            private TextFlow textFlow = new TextFlow(senderText, contentText);
            private HBox bubble = new HBox(textFlow);
            {
                setGraphic(bubble);
                getStyleClass().add("chat-list-cell");
                textFlow.maxWidthProperty().bind(lv.widthProperty().multiply(0.7));
            }

            @Override
            protected void updateItem(ChatRecord message, boolean empty) {
                super.updateItem(message, empty);
                if (empty || message == null) {
                    setGraphic(null);
                } else {
                    senderText.setText(message.getRole() + ": \n");
                    contentText.setText(message.getMessage());

                    // 5. This is the correct way to align bubbles
                    if ("user".equals(message.getRole())) {
                        setAlignment(Pos.CENTER_RIGHT);
                        senderText.getStyleClass().setAll("sender-user");
                        textFlow.getStyleClass().setAll("chat-bubble-user");
                    } else {
                        setAlignment(Pos.CENTER_LEFT);
                        senderText.getStyleClass().setAll("sender-model");
                        textFlow.getStyleClass().setAll("chat-bubble-model");
                    }

                    setGraphic(bubble);
                }
            }
        });

        chatView.setSelectionModel(null);
        chatView.setFocusTraversable(false);
    }

    private void addMessageToChat(ChatRecord message) {
        chatHistory.add(message);
        chatView.scrollTo(chatHistory.size() - 1);
    }

    @FXML
    private void OnSendButtonClicked() {
        String userInput = inputArea.getText();
        if (userInput == null || userInput.isBlank()) return;

        addMessageToChat(new ChatRecord("user", userInput));
        inputArea.clear();

        String masterPrompt = buildMasterPrompt(ControllerManager.getGraphInputController().getGraph(), chatHistory);

        Task<String> apiCallTask = new Task<>() {
            @Override
            protected String call() throws Exception {
                Platform.runLater(() -> addMessageToChat(new ChatRecord("model", "Thinking...")));
                // Ensure OllamaService.generateContent(prompt, true) returns the raw JSON string
                return ollamaService.generateContent(masterPrompt);
            }
        };

        apiCallTask.setOnSucceeded(event -> {
            chatHistory.removeLast(); // Remove "Thinking..."
            String aiRawResponse = apiCallTask.getValue();

            try {
                // 1. Find the first '{' and last '}' to strip away conversational text
                int firstBrace = aiRawResponse.indexOf('{');
                int lastBrace = aiRawResponse.lastIndexOf('}');

                if (firstBrace == -1 || lastBrace == -1) {
                    // AI didn't return JSON at all, treat as plain text chat
                    addMessageToChat(new ChatRecord("model", aiRawResponse));
                    return;
                }

                String jsonOnly = aiRawResponse.substring(firstBrace, lastBrace + 1);
                // If the model wrapped the JSON in a string primitive
                if (jsonOnly.startsWith("\"") && jsonOnly.endsWith("\"")) {
                    jsonOnly = com.google.gson.JsonParser.parseString(jsonOnly).getAsString();
                }
                // 2. Parse the cleaned JSON
                com.google.gson.JsonObject responseJson = com.google.gson.JsonParser.parseString(jsonOnly).getAsJsonObject();

                String type = responseJson.get("type").getAsString();
                String message = responseJson.get("message").getAsString();

                // 3. Always show the message in chat
                addMessageToChat(new ChatRecord("model", message));

                // 4. Route based on type
                if ("ACTION".equals(type)) {
                    String action = responseJson.get("action").getAsString();
                    handleAlgorithmAction(action, responseJson.getAsJsonObject("parameters"));
                } else if ("CREATE_GRAPH".equals(type)) {
                    GraphData graphData = gson.fromJson(responseJson.get("graphData"), GraphData.class);
                    Platform.runLater(() -> GraphInputController.CreateGraphStatic(graphData));
                }

            } catch (Exception e) {
                // If parsing still fails, the model might have returned bad JSON structure
                System.err.println("Malformed JSON from AI: " + aiRawResponse);
                addMessageToChat(new ChatRecord("model", "I tried to run that, but I had a formatting error. Please try again."));
            }
        });

        new Thread(apiCallTask).start();
    }

    private void handleAlgorithmAction(String action, com.google.gson.JsonObject params) {
        Platform.runLater(() -> {
            String startNode = params.get("startNode").getAsString();
            switch (action) {
                case "run_bfs" -> {
                    GraphTools.runBFS(startNode);
                }
                case "run_dfs" -> {
                    GraphTools.runDFS(startNode);
                }
                case "run_bipartite" -> {
                    GraphTools.runBiPartite(startNode);
                }
            }
        });
    }

    private String buildMasterPrompt(Graph graph, List<ChatRecord> history) {
        GraphData graphData = new GraphData(graph);
        String graphJson = gson.toJson(graphData);

        String historyString = history.stream()
                .map(record -> record.getRole() + " : " + record.getMessage())
                .collect(Collectors.joining("\n"));

        return """
    You are a Graph AI. You MUST return ONLY valid JSON.
    
    JSON Structure:
    {
      "type": "CHAT" | "ACTION" | "CREATE_GRAPH",
      "message": "text description",
      "action": "run_bfs" | "run_dfs" | "run_bipartite" | "none",
      "parameters": {"startNode": "1"},
      "graphData": {
        "isDirected": true,
        "nodes": ["1", "2"],
        "edges": [
          {"from": "1", "to": "2", "weight": 0, "capacity": 0}
        ]
      }
    }

    CRITICAL RULES:
    1. "edges" MUST be a list of OBJECTS with 'from' and 'to' keys. 
    2. NEVER return edges as a list of lists like [["1","2"]].
    3. If type is CREATE_GRAPH, you MUST provide the full "graphData" object.
    
    --- CONTEXT ---
    Graph: %s
    History: %s
    """.formatted(graphJson, historyString);
    }

}