package Controllers;

import GraphVisualizer.AppSettings;
import GraphVisualizer.Graph;
import Services.*;
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
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import dev.langchain4j.model.chat.listener.*;

import java.time.Duration;
import java.util.Collections;
import dev.langchain4j.memory.chat.MessageWindowChatMemory;
import dev.langchain4j.model.googleai.GoogleAiGeminiChatModel;
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

    private Gson gson = new GsonBuilder().setPrettyPrinting().create();
    private ChatAssistant assistant;

    @FXML
    public void initialize() {

        initAssistant();
        ControllerManager.setChatController(this);
        initChat();

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
            e.printStackTrace();
            addMessageToChat(new ChatRecord("model", "I encountered an error, Please try again."));
            AlertError((Exception) e);
        });

        new Thread(chatTask).start();


    }

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

    private void initChat()
    {
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

    private void initAssistant()
    {
        ChatModelListener listener = new ChatModelListener() {
            @Override
            public void onRequest(ChatModelRequestContext requestContext) {
                System.out.println("--- AI REQUEST ---");
                System.out.println(requestContext.chatRequest().messages());
            }

            @Override
            public void onResponse(ChatModelResponseContext responseContext) {
                System.out.println("--- AI RESPONSE ---");
                System.out.println(responseContext.chatResponse().aiMessage());

                // This shows you exactly how many tokens you used!
                System.out.println("Tokens Used: " + responseContext.chatResponse().tokenUsage());
            }

            @Override
            public void onError(ChatModelErrorContext errorContext) {
                System.err.println("--- AI ERROR ---");
                errorContext.error().printStackTrace();
            }
        };

        assistant = AiServices.builder(ChatAssistant.class)
                .chatModel(GoogleAiGeminiChatModel.builder()
                        .apiKey(System.getenv("GEMINI_API_KEY"))
                        .modelName(AppSettings.AI_model_used)
                        .timeout(Duration.ofSeconds(AppSettings.MODEL_TIMEOUT_IN_SECONDS))
                        .listeners(Collections.singletonList(listener))
                        .build())
                .chatMemory(MessageWindowChatMemory.withMaxMessages(AppSettings.MODEL_MAX_CONTEXT))
                .tools(new GraphTools())
                .build();
    }

}