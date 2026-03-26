package Controllers;

import GraphVisualizer.AppSettings;
import GraphVisualizer.Graph;
import Services.*;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import javafx.application.Platform;
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
import dev.langchain4j.memory.chat.MessageWindowChatMemory;
import dev.langchain4j.model.googleai.GoogleAiGeminiChatModel;
import dev.langchain4j.model.chat.ChatModel;
import dev.langchain4j.service.AiServices;



public class ChatController extends Controller {

    @FXML
    private ListView<ChatRecord> chatView;

    // 2. The history list MUST be of type ChatRecord
    private ObservableList<ChatRecord> chatHistory = FXCollections.observableArrayList();

    @FXML
    private Button sendButton;
    @FXML
    private TextArea inputArea;

    private GeminiService geminiService = GeminiService.getInstance();
    private Gson gson = new GsonBuilder().setPrettyPrinting().create(); // Create this once
    private ChatAssistant assistant;

    @FXML
    public void initialize() {

        //        // 1. Initialize the Model
//        ChatModel model = GoogleAiGeminiChatModel.builder()
//                .apiKey(System.getenv("GEMINI_API_KEY"))
//                .modelName(AppSettings.AI_model_used)
//                .build();

        assistant = AiServices.builder(ChatAssistant.class)
                .chatModel(GoogleAiGeminiChatModel.builder()
                        .apiKey(System.getenv("GEMINI_API_KEY"))
                        .modelName(AppSettings.AI_model_used)
                        .build())
                .chatMemory(MessageWindowChatMemory.withMaxMessages(13))
                .tools(new GraphTools())
                .build();


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
    private void OnSendButtonClicked()
    {
        String userInput = inputArea.getText();
        if (userInput == null || userInput.isBlank()) return;

        addMessageToChat(new ChatRecord("user", userInput));
        inputArea.clear();

        String userPrompt = buildMasterPrompt(ControllerManager.getGraphInputController().getGraph(), userInput);
        Task<String> chatTask = new Task<>() {
            @Override
            protected String call() {
                Platform.runLater(() -> addMessageToChat(new ChatRecord("model", "Thinking...")));
                return assistant.chat(userPrompt);
            }
        };

        chatTask.setOnSucceeded(event -> {
            chatHistory.removeLast();
            String aiResponse = chatTask.getValue();
            addMessageToChat(new ChatRecord("model", aiResponse));
        });

        chatTask.setOnFailed(event -> {
            chatHistory.removeLast();
            Throwable e = chatTask.getException();
            e.printStackTrace(); // This will show the EXACT error in your IntelliJ console
            addMessageToChat(new ChatRecord("model", "Error: " + e.getMessage()));
        });

        new Thread(chatTask).start();


    }

    private void handleAiResponse(String aiRawResponse) {

        try {
            int firstBrace = aiRawResponse.indexOf('{');
            int lastBrace = aiRawResponse.lastIndexOf('}');

            if (firstBrace == -1 || lastBrace == -1) {
                addMessageToChat(new ChatRecord("model", aiRawResponse));
                return;
            }

            String jsonOnly = aiRawResponse.substring(firstBrace, lastBrace + 1);

            if (jsonOnly.startsWith("\"") && jsonOnly.endsWith("\"")) {
                jsonOnly = com.google.gson.JsonParser.parseString(jsonOnly).getAsString();
            }

            com.google.gson.JsonObject responseJson = com.google.gson.JsonParser.parseString(jsonOnly).getAsJsonObject();

            String type = responseJson.has("type") ? responseJson.get("type").getAsString() : "CHAT";
            String message = responseJson.has("message") ? responseJson.get("message").getAsString() : "";

            if (!message.isEmpty()) {
                addMessageToChat(new ChatRecord("model", message));
            }

            ObjectMapper objectMapper = new ObjectMapper();

            if ("ACTION".equals(type)) {
                GeminiResponse response = objectMapper.readValue(jsonOnly, GeminiResponse.class);

                // Ensure algorithm execution happens safely
                if (response.algorithm != null) {
                    Platform.runLater(() -> {
                        response.algorithm.Execute(ControllerManager.getGraphInputController().getGraph());
                    });
                }

            } else if ("CREATE_GRAPH".equals(type) && responseJson.has("graphData")) {
                GraphData graphData = gson.fromJson(responseJson.get("graphData"), GraphData.class);
                Platform.runLater(() -> GraphInputController.CreateGraphStatic(graphData));
            }

        } catch (Exception e) {
            System.err.println("Error processing AI response: " + e.getMessage());
            e.printStackTrace();
            addMessageToChat(new ChatRecord("model", "I encountered an error processing that request. Check the logs for details."));
        }

    }


//    @FXML
//    private void OnSendButtonClicked() {
//        String userInput = inputArea.getText();
//        if (userInput == null || userInput.isBlank()) return;
//
//        addMessageToChat(new ChatRecord("user", userInput));
//        inputArea.clear();
//
//        String masterPrompt = buildMasterPrompt(ControllerManager.getGraphInputController().getGraph(), chatHistory,userInput);
//
//        Task<String> apiCallTask = new Task<>() {
//            @Override
//            protected String call() throws Exception {
//                Platform.runLater(() -> addMessageToChat(new ChatRecord("model", "Thinking...")));
//                return geminiService.generateContent(masterPrompt);
//            }
//        };
//
//        apiCallTask.setOnSucceeded(event -> {
//            chatHistory.removeLast(); // Remove "Thinking..."
//            String aiRawResponse = apiCallTask.getValue();
//
//            try {
//                // 1. Find the first '{' and last '}' to strip away conversational text
//                int firstBrace = aiRawResponse.indexOf('{');
//                int lastBrace = aiRawResponse.lastIndexOf('}');
//
//                if (firstBrace == -1 || lastBrace == -1) {
//                    // AI didn't return JSON at all, treat as plain text chat
//                    addMessageToChat(new ChatRecord("model", aiRawResponse));
//                    return;
//                }
//
//                String jsonOnly = aiRawResponse.substring(firstBrace, lastBrace + 1);
//                // If the model wrapped the JSON in a string primitive
//                if (jsonOnly.startsWith("\"") && jsonOnly.endsWith("\"")) {
//                    jsonOnly = com.google.gson.JsonParser.parseString(jsonOnly).getAsString();
//                }
//                // 2. Parse the cleaned JSON
//                com.google.gson.JsonObject responseJson = com.google.gson.JsonParser.parseString(jsonOnly).getAsJsonObject();
//
//                String type = responseJson.get("type").getAsString();
//                System.out.println("TYPE = "+ type);
//
//                ObjectMapper objectMapper = new ObjectMapper();
//                String message = responseJson.get("message").getAsString();
//
//
//                // 3. Always show the message in chat
//                addMessageToChat(new ChatRecord("model", message));
//
//                // 4. Route based on type
//                if ("ACTION".equals(type)) {
//                    GeminiResponse response = objectMapper.readValue(aiRawResponse, GeminiResponse.class);
//                    response.algorithm.Execute(ControllerManager.getGraphInputController().getGraph());
//
//                } else if ("CREATE_GRAPH".equals(type)) {
//                    GraphData graphData = gson.fromJson(responseJson.get("graphData"), GraphData.class);
//                    Platform.runLater(() -> GraphInputController.CreateGraphStatic(graphData));
//                }
//
//            } catch (Exception e) {
//                // If parsing still fails, the model might have returned bad JSON structure
//                System.err.println("Malformed JSON from AI: " + aiRawResponse);
//                System.out.println(e.getMessage());
//                addMessageToChat(new ChatRecord("model", "I tried to run that, but I had a formatting error. Please try again."));
//            }
//        });

//
//        new Thread(apiCallTask).start();
//    }

//    private void handleAlgorithmAction(String action, com.google.gson.JsonObject params) {
//        Platform.runLater(() -> {
//            try
//            {
//                switch (action) {
//                    case "run_bfs" -> {
//                        String startNode = params.get("inputNode").getAsString();
//                        GraphTools.runBFS(startNode);
//                    }
//                    case "run_dfs" -> {
//                        String startNode = params.get("inputNode").getAsString();
//                        GraphTools.runDFS(startNode);
//                    }
//                    case "run_bipartite" -> {
//                        GraphTools.runBiPartite();
//                    }
//                    case "run_euler_circuit" -> {
//                        GraphTools.runEulerCircuit();
//                    }
//                    case "run_topological" -> {
//                        GraphTools.runTopologicalSort();
//                    }
//                    case "run_kosaraju" -> {
//                        GraphTools.runKosarajuAlgorithm();
//                    }
//                    case "run_super" -> {
//                        GraphTools.runSuperGraphAlgorithm();
//                    }
//                }
//            }catch (NullPointerException e) {
//                e.printStackTrace();
//                addMessageToChat(new ChatRecord("model", "I tried to run that, but the selected vertice is null. Please try again."));
//
//            }
//
//        });
//    }


//    private String buildMasterPrompt(Graph graph, List<ChatRecord> history, String userRequest) {
//        GraphData graphData = new GraphData(graph);
//        String graphJson = gson.toJson(graphData);
//
//        String historyString = history.stream()
//                .map(record -> record.getRole() + " : " + record.getMessage())
//                .collect(Collectors.joining("\n"));
//
//        return """
//    --- CONTEXT ---
//    Current Graph: %s
//    History: %s
//
//    ### TASK ###
//    User Request: "%s"
//    Output:
//    """.formatted(graphJson, historyString, userRequest);
//    }

    private String buildMasterPrompt(Graph graph, String userRequest) {
        // We only need the current state of the graph here.
        // LangChain4j's ChatMemory will handle the 'history' part for us!
        GraphData graphData = new GraphData(graph);
        String graphJson = gson.toJson(graphData);

        return """
    ### CURRENT GRAPH CONTEXT ###
    %s

    ### USER REQUEST ###
    %s
    """.formatted(graphJson, userRequest);
    }

}