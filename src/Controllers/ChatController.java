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
    private VerifierAgent verifier;

    @FXML
    public void initialize() {

        ControllerManager.setChatController(this);
        initAssistant();
        initVerifier();
        initChat();


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

        Task<String> orchestrationTask = new Task<>() {
            @Override
            protected String call() throws Exception {
                // 1. Initial Prompt with Graph Context
                String currentUserInput = buildMasterPrompt(
                        ControllerManager.getGraphInputController().getGraph(),
                        userInput
                );

                String lastAssistantResponse = "";
                int attempts = 0;

                while (attempts <= AppSettings.MAX_RETRIES) {
                    lastAssistantResponse = assistant.chat(currentUserInput);

                    // Log for your eyes only
                    System.out.println("[Attempt " + attempts + "] Assistant response: " + lastAssistantResponse);

                    String auditInput = String.format("<req>%s</req>\n<ans>%s</ans>", userInput, lastAssistantResponse);
                    String verifierRaw = verifier.chat(auditInput);
                    VerificationResult result = gson.fromJson(cleanJsonResponse(verifierRaw), VerificationResult.class);

                    if (result != null && result.valid) {
                        return lastAssistantResponse;
                    }

                    attempts++;
                    // If we are here, the Verifier REJECTED the Assistant (e.g. because it talked about colors)
                    currentUserInput = "Your previous response was rejected by the supervisor. " +
                            "Reason: " + (result != null ? result.criticism_for_agent : "Invalid response format") + ". " +
                            "Provide a clean, direct response now.";
                }

                return lastAssistantResponse; // Return the last thing said even if validation failed
            }
        };

        orchestrationTask.setOnRunning(event -> {
            addMessageToChat(new ChatRecord("model", "Thinking..."));
        });

        orchestrationTask.setOnSucceeded(event -> {
            if (!chatHistory.isEmpty()) chatHistory.removeLast(); // Remove "Thinking..."
            addMessageToChat(new ChatRecord("model", orchestrationTask.getValue()));
        });

        orchestrationTask.setOnFailed(event -> {
            if (!chatHistory.isEmpty()) chatHistory.removeLast();
            Throwable e = orchestrationTask.getException();
            e.printStackTrace(); // Check IntelliJ console for the error
            addMessageToChat(new ChatRecord("model", "Connection Error: " + e.getMessage()));
        });

        new Thread(orchestrationTask).start();
    }


    private String cleanJsonResponse(String rawResponse) {
        if (rawResponse == null || rawResponse.isBlank()) return "{}";

        String cleaned = rawResponse.trim();

        // 1. Strip Markdown Code Blocks
        // This regex finds ```json [content] ``` and extracts just the [content]
        if (cleaned.contains("```")) {
            cleaned = cleaned.replaceAll("(?s)^```json\\s*", "")
                    .replaceAll("(?s)^```\\s*", "")
                    .replaceAll("(?s)\\s*```$", "");
        }

        // 2. Isolate the JSON Object
        // This finds the first '{' and the last '}' to ignore any text before or after
        int firstBrace = cleaned.indexOf('{');
        int lastBrace = cleaned.lastIndexOf('}');

        if (firstBrace != -1 && lastBrace != -1 && lastBrace > firstBrace) {
            cleaned = cleaned.substring(firstBrace, lastBrace + 1);
        }

        // 3. Handle potential double-encoding
        // Sometimes the model wraps the JSON object inside a string primitive
        if (cleaned.startsWith("\"") && cleaned.endsWith("\"")) {
            try {
                cleaned = com.google.gson.JsonParser.parseString(cleaned).getAsString();
            } catch (Exception e) {
                // If it's not actually a string primitive, keep it as is
            }
        }

        return cleaned.trim();
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
                System.out.println("---ASSISTANT AI REQUEST ---");
                System.out.println(requestContext.chatRequest().messages());
            }

            @Override
            public void onResponse(ChatModelResponseContext responseContext) {
                System.out.println("---ASSISTANT AI RESPONSE ---");
                System.out.println(responseContext.chatResponse().aiMessage());

                // This shows you exactly how many tokens you used!
                System.out.println("ASSISTANT : Tokens Used: " + responseContext.chatResponse().tokenUsage());
            }

            @Override
            public void onError(ChatModelErrorContext errorContext) {
                System.err.println("--- ASSISTANT AI ERROR ---");
                errorContext.error().printStackTrace();
            }
        };

        assistant = AiServices.builder(ChatAssistant.class)
                .chatModel(GoogleAiGeminiChatModel.builder()
                        .apiKey(System.getenv("GEMINI_API_KEY"))
                        .modelName(AppSettings.AI_model_used)
                        .timeout(Duration.ofSeconds(AppSettings.MODEL_TIMEOUT_IN_SECONDS))
                        .listeners(Collections.singletonList(listener))
                        .temperature(0.0)
                        .build())
                .chatMemory(MessageWindowChatMemory.withMaxMessages(AppSettings.MODEL_MAX_CONTEXT))
                .tools(new GraphTools())
                .build();

    }

    private void initVerifier()
    {
        ChatModelListener listener = new ChatModelListener() {
            @Override
            public void onRequest(ChatModelRequestContext requestContext) {
                System.out.println("---VERIFIER AI REQUEST ---");
                System.out.println(requestContext.chatRequest().messages());
            }

            @Override
            public void onResponse(ChatModelResponseContext responseContext) {
                System.out.println("---VERIFIER AI RESPONSE ---");
                System.out.println(responseContext.chatResponse().aiMessage());

                // This shows you exactly how many tokens you used!
                System.out.println("VERIFIER Tokens Used: " + responseContext.chatResponse().tokenUsage());
            }

            @Override
            public void onError(ChatModelErrorContext errorContext) {
                System.err.println("--- VERIFIER AI ERROR ---");
                errorContext.error().printStackTrace();
            }
        };

        verifier = AiServices.builder(VerifierAgent.class)
                .chatModel(GoogleAiGeminiChatModel.builder()
                        .apiKey(System.getenv("GEMINI_API_KEY"))
                        .modelName(AppSettings.AI_model_used)
                        .timeout(Duration.ofSeconds(AppSettings.MODEL_TIMEOUT_IN_SECONDS))
                        .listeners(Collections.singletonList(listener))
                        .temperature(0.0)
                        .maxRetries(AppSettings.MAX_RETRIES)
                        .build())
                .chatMemory(MessageWindowChatMemory.withMaxMessages(AppSettings.MODEL_MAX_CONTEXT))
                .tools(new GraphTools())
                .build();


    }

}