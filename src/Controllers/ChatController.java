package Controllers;

import GraphVisualizer.Graph;
import Services.ChatRecord; // Make sure this is: public record ChatRecord(String role, String content) {}
import Services.GeminiService;
import Services.GraphData;
import Services.GraphTools;
import autovalue.shaded.org.objectweb.asm.Type;
import com.google.genai.types.FunctionDeclaration;
import com.google.genai.types.GenerateContentConfig;
import com.google.genai.types.Schema;
import com.google.genai.types.Tool;
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

import java.lang.reflect.Method;
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
    private GeminiService geminiService = GeminiService.getInstance();
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
        if (userInput == null || userInput.isBlank()) {
            return;
        }

        // 8. Add the ChatRecord object, NOT a string
        addMessageToChat(new ChatRecord("user", userInput));
        inputArea.clear();

        // 9. Build the master prompt from the ChatRecord list
        String masterPrompt = buildMasterPrompt(ControllerManager.getGraphInputController().getGraph(), chatHistory);

        Task<String> apiCallTask = new Task<>() {
            @Override
            protected String call() throws Exception {
                Platform.runLater(() -> addMessageToChat(new ChatRecord("model", "Typing...")));
                return geminiService.generateContent(masterPrompt);
            }
        };

        apiCallTask.setOnSucceeded(event -> {
            String aiResponse = apiCallTask.getValue();
            chatHistory.remove(chatHistory.size() - 1);
            addMessageToChat(new ChatRecord("model", aiResponse));
        });

        apiCallTask.setOnFailed(event -> {
            chatHistory.remove(chatHistory.size() - 1);
            addMessageToChat(new ChatRecord("model", "Sorry, I encountered an error."));
            apiCallTask.getException().printStackTrace();
            AlertError((Exception) apiCallTask.getException());
        });

        new Thread(apiCallTask).start();
    }


    private String buildMasterPrompt(Graph graph, List<ChatRecord> history) {
        GraphData graphData = new GraphData(graph);
        String graphJson = gson.toJson(graphData);

        // Convert the list of objects back into the string format for the AI
        String historyString = history.stream()
                .map(record -> record.getRole() + " : " + record.getMessage())
                .collect(Collectors.joining("\n"));

        return "You are a helpful graph analyst assistant. " +
                "You will be given the current graph data in JSON format, " +
                "followed by a conversation history. " +
                "Base all your answers on the provided graph data and the history.\n\n" +
                "--- CURRENT GRAPH DATA ---\n" +
                graphJson + "\n\n" +
                "--- CONVERSATION HISTORY ---\n" +
                historyString + "\n" +
                "--- END OF CONTEXT ---\n\n" +
                "Please provide a response to the last 'user' message.";
    }

}