package Controllers;

import GraphVisualizer.Graph;
import Services.*;
import com.fasterxml.jackson.databind.ObjectMapper;
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
        if (userInput == null || userInput.isBlank()) return;

        addMessageToChat(new ChatRecord("user", userInput));
        inputArea.clear();

        String masterPrompt = buildMasterPrompt(ControllerManager.getGraphInputController().getGraph(), chatHistory,userInput);

        Task<String> apiCallTask = new Task<>() {
            @Override
            protected String call() throws Exception {
                Platform.runLater(() -> addMessageToChat(new ChatRecord("model", "Thinking...")));
                return geminiService.generateContent(masterPrompt);
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
                System.out.println("TYPE = "+ type);

                ObjectMapper objectMapper = new ObjectMapper();
                String message = responseJson.get("message").getAsString();


                // 3. Always show the message in chat
                addMessageToChat(new ChatRecord("model", message));

                // 4. Route based on type
                if ("ACTION".equals(type)) {
                    GeminiResponse response = objectMapper.readValue(aiRawResponse, GeminiResponse.class);
                    response.algorithm.Execute(responseJson.getAsJsonObject("parameters"), ControllerManager.getGraphInputController().getGraph());
//                    String action = responseJson.get("action").getAsString();
//                    System.out.println("ACTION = "+ action);
//                    handleAlgorithmAction(action, responseJson.getAsJsonObject("parameters"));
                } else if ("CREATE_GRAPH".equals(type)) {
                    GraphData graphData = gson.fromJson(responseJson.get("graphData"), GraphData.class);
                    Platform.runLater(() -> GraphInputController.CreateGraphStatic(graphData));
                }

            } catch (Exception e) {
                // If parsing still fails, the model might have returned bad JSON structure
                System.err.println("Malformed JSON from AI: " + aiRawResponse);
                System.out.println(e.getMessage());
                addMessageToChat(new ChatRecord("model", "I tried to run that, but I had a formatting error. Please try again."));
            }
        });

        new Thread(apiCallTask).start();
    }

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


    private String buildMasterPrompt(Graph graph, List<ChatRecord> history, String userRequest) {
        GraphData graphData = new GraphData(graph);
        String graphJson = gson.toJson(graphData);

        String historyString = history.stream()
                .map(record -> record.getRole() + " : " + record.getMessage())
                .collect(Collectors.joining("\n"));

        return """
    Instruction: You are an expert AI for a Graph Visualization application.
    You MUST analyze the user's request and respond with a single, valid JSON object following the schema below.
    
    
    ### JSON SCHEMA ###
    {
      "type": "CHAT" | "ACTION" | "CREATE_GRAPH",
      "message": "Human-readable response or explanation",
      "action": "run_bfs" | "run_dfs" | "run_bipartite" | "run_euler_circuit" | "run_topological" | "none",
      "parameters": { "inputNode": "string" , "iterations": "integer" , "k": "integer" , "s": "integer" , "t": "integer"}
      "graphData": {
        "isDirected": boolean,
        "nodes": ["1", "2", ...],
        "edges": [{"from": "1", "to": "2", "weight": 0, "capacity": 0}]
      }
    }
    ### AVAILABLE ALGORITHM TOKENS ###
        - "run_bfs": Breadth-First Search
        - "run_dfs": Depth-First Search
        - "run_bipartite": Check if graph is Bipartite
        - "run_euler_circuit": Find Euler Circuit
        - "run_topological": Topological Sort
        - "run_prim": Minimum Spanning Tree
        - "run_bellman_ford": Minimum Spanning Tree (Bellman's)
        - "run_connectivity": finds connectivity components in an undirected graph
        - "run_euler_path": finds an euler path in a given graph
        - "run_floyd_warshall": finds lightest paths between every two vertices
        - "run_ford_felkerson": finds max flow in a graph
        - "run_hamilton_path": finds a hamilton path in a given graph
        - "run_kosaraju": finds connectivity components in a directed graph
        - "run_mincut": finds a minimal cut in a graph
        - "run_shortest_paths_tree": finds shortest paths between every two vertices
        - "run_super": creates the super graph of a given directed graph
        # NON DETERMINISTIC ALGORITHM TOKENS #
            - "run_clique": finds a click of size k in a graph
            - "run_independent_set": finds an independent set of size k in a graph
            - "run_k_colors": finds a valid coloring of k colors in the given graph
            - "run_vertex_cover": finds a vertex cover of size k in the given graph
        
    ### RULES FOR PARAMETERS ###
        1. If an algorithm is deterministic (like BFS/DFS), set "iterations" and "k": null.
        2. If the algorithm is Ford Felkerson - then set s and t to be the starting and finishing nodes of the algorithm respectively.
        3. If an algorithm is NON-DETERMINISTIC or STOCHASTIC (like Random Walk or Meta-heuristics):
           - Check if the user provided a number of iterations and k.
           - If they DID NOT set iterations, set "type": "CHAT" and ask the user: "How many iterations would you like to run for this algorithm?" 
           - If they DID NOT set k, set "type": "CHAT" and ask the user: "what parameter would you like to give for the algorithm?" 
           - If they DID, set "type": "ACTION" and fill the "iterations" and "k" parameters.
        
    ### INTENT RULES ###
    1. CREATE_GRAPH: Use this when the user describes a new graph to build. You MUST populate the "graphData" object.
    2. ACTION: Use this when the user asks to run an algorithm on the CURRENT graph. Use the action tokens provided.
    3. CHAT: Use this for general questions, status checks, or conversation. Set "action" to "none" and "graphData" to null.

    ### CRITICAL CONSTRAINTS ###
    - Nodes MUST be strings representing numbers (e.g., "1", "2").
    - Be wary of algorithm input constraints. if the algorithm expects a directed graph and the graph is undirected then mention the input is not right.
    - Edges MUST be objects with "from" and "to" keys. NEVER nested lists.
    - If "parameters" are not needed, return: "parameters": {}.
    - Do NOT include markdown tags like ```json in your response.

    --- CONTEXT ---
    Current Graph: %s
    History: %s

    ### TASK ###
    User Request: "%s"
    Output:
    """.formatted(graphJson, historyString, userRequest);
    }

}