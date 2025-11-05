package Controllers;

import Algorithms.*;
import Exceptions.*;
import GraphVisualizer.*;
import Services.GeminiService;
import Services.GraphData;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Group;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.input.ContextMenuEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.Scene;
import javafx.stage.Modality;
import javafx.stage.Stage;
import resources.LoadingPopup;

import java.io.IOException;
import java.util.List;
import java.util.Objects;

public class GraphInputController extends Controller{

    @FXML
    private AnchorPane saveGraphPane;
    @FXML
    private GridPane controlPanel;
    @FXML
    private Pane algoPlaceholder;
    @FXML
    private TextField verticesField;
    @FXML
    private TextField edgeStartField;
    @FXML
    private TextField edgeEndField;
    @FXML
    private Button enterButton;
    @FXML
    private Button addButton;
    @FXML
    private AnchorPane graphContainer;
    @FXML
    private ComboBox<Theme> ThemeBox;
    @FXML
    private Button saveGraph;
    @FXML
    private Button loadGraph;
    @FXML
    private CheckBox DirectedCheckbox;
    @FXML
    private TextField weightField;
    @FXML
    private Label weightLabel;
    @FXML
    private TextField capacityField;
    private ContextMenu graphPaneContextMenu;
    private ContextMenuEvent currentContextMenuEvent;
    private ContextMenu verticeContextMenu;
    private ContextMenu edgeContextMenu;

    private int vertexCount = 0;
    private static Graph G = new Graph(true);

    public AnchorPane getGraphContainer()
    {
        return graphContainer;
    }

    public void initialize() {
        try {
            FXMLLoader algoLoader = new FXMLLoader(getClass().getResource(AppSettings.Algorithms_Pane_Location));
            ScrollPane algorithmsPane = algoLoader.load();

            algorithmsPane.prefWidthProperty().bind(algoPlaceholder.widthProperty());
            algorithmsPane.prefHeightProperty().bind(algoPlaceholder.heightProperty());

            algoPlaceholder.getChildren().setAll(algorithmsPane);


        } catch (IOException e) {
            AlertError(e);
        }

        graphContainer.setPrefWidth(AppSettings.CONTAINER_WIDTH);
        graphContainer.setPrefHeight(AppSettings.CONTAINER_HEIGHT);

        setupGraphPaneContextMenu();
        weightLabel.setTooltip(new Tooltip("Optional : add weight to edges. leave empty for value 0."));
        ThemeBox.getItems().addAll(Theme.values());
        ThemeBox.setValue(Theme.DEFAULT);
        ThemeBox.valueProperty().addListener((obs, oldTheme, newTheme) -> {
            if (newTheme != null) {
                ThemeManager.getThemeManager().switchTheme(newTheme);
            }
        });

        DirectedCheckbox.setSelected(false);
        DirectedCheckbox.selectedProperty().addListener((obs, wasSelected, isNowSelected) -> {
            enterButton.setDisable(false);
            graphContainer.getChildren().clear();
            this.G = new Graph(isNowSelected);
        });
        ControllerManager.setGraphInputController(this);
    }


    private void setupGraphPaneContextMenu() {
        graphPaneContextMenu = new ContextMenu();

        MenuItem addNodeItem = new MenuItem("Add Node Here");
        addNodeItem.setOnAction(event -> {
            if (currentContextMenuEvent != null) {
                System.out.println("Add Node at: (" + currentContextMenuEvent.getX() + ", " + currentContextMenuEvent.getY() + ")");
                this.G.createAvailableNode(currentContextMenuEvent.getX(),currentContextMenuEvent.getY());
                displayGraph(G);
            }
            graphPaneContextMenu.hide();
        });

        MenuItem clearGraphItem = new MenuItem("Clear Graph");
        clearGraphItem.setOnAction(event -> {
            System.out.println("Clear Graph action");
            this.G = new Graph(DirectedCheckbox.isSelected());
            displayGraph(G);
            graphPaneContextMenu.hide();
        });

        MenuItem AIGraphCreation = new MenuItem("create graph with AI");
        AIGraphCreation.setOnAction(event -> {
            System.out.println("build AI graph action");
            loadGraphPromptPopup();
            graphPaneContextMenu.hide();

        });

        MenuItem openChat = new MenuItem("chat with Graphy");
        openChat.setOnAction(event ->{
            System.out.println("opens chat");
            launchChat();
            graphPaneContextMenu.hide();
        });

        graphPaneContextMenu.getItems().addAll(addNodeItem, clearGraphItem, AIGraphCreation, openChat);


        // --- Event Handlers for the Pane ---
        graphContainer.setOnContextMenuRequested(event -> {
            currentContextMenuEvent = event;
            System.out.println("Context menu requested");
            graphPaneContextMenu.show(graphContainer, event.getScreenX(), event.getScreenY());
            event.consume();
        });

        graphContainer.setOnMouseClicked(event -> {
            if (event.getButton() == javafx.scene.input.MouseButton.PRIMARY) {
                if (graphPaneContextMenu.isShowing()) {
                    graphPaneContextMenu.hide();
                }
                if(verticeContextMenu != null && verticeContextMenu.isShowing())
                {
                    verticeContextMenu.hide();
                }
                if(edgeContextMenu != null && edgeContextMenu.isShowing())
                {
                    edgeContextMenu.hide();
                }
                if (event.getTarget() == graphContainer) {
                    System.out.println("Primary click on container background - Deselecting node.");

                }
            }

        });
    }

    private void launchChat()
    {
        try
        {
            FXMLLoader NDLoader = new FXMLLoader(getClass().getResource(AppSettings.chat_window_location));
            Pane chatWindow = NDLoader.load();
            NDLoader.getController();

            Stage popupStage = new Stage();
            popupStage.setTitle("chat with AI");
            popupStage.resizableProperty().set(false);
            javafx.scene.image.Image icon = new Image(Objects.requireNonNull(getClass().getResourceAsStream(AppSettings.App_Icon_location)));
            popupStage.getIcons().add(icon);

            Scene popupScene = new Scene(chatWindow);
            popupStage.setScene(popupScene);
            ThemeManager.getThemeManager().AddScene(popupScene);
            popupStage.showAndWait();

        } catch (Exception e) {
            AlertError(e);
        }
    }
    private void ExplainGraphWithPrompt(String finalPrompt) {
        GeminiService gs = GeminiService.getInstance();
        LoadingPopup loadingPopup = new LoadingPopup();
        loadingPopup.show();
        new Thread(() -> {

            String analysis = gs.generateContent(finalPrompt);
            Platform.runLater(() -> {
                infoPopup(analysis);
                loadingPopup.hide();
            });
        }).start();
    }

    public Graph getGraph()
    {
        return G;
    }

    @FXML
    private void onLoadGraph()
    {
        this.G = GraphSerializer.loadGraph();
        if(G == null) return;
        displayGraph(G);
    }

    private void loadGraphPromptPopup()
    {
        try {
            FXMLLoader saveGraphLoader = new FXMLLoader(getClass().getResource(AppSettings.graph_Prompt_Popup_Location));
            AnchorPane saveGraphPane = saveGraphLoader.load();

            Stage popupStage = new Stage();
            popupStage.setTitle("create graph with AI prompt");

            popupStage.initModality(Modality.APPLICATION_MODAL);
            Scene popupScene = new Scene(saveGraphPane);
            popupStage.setScene(popupScene);
            ThemeManager.getThemeManager().AddScene(popupScene);
            popupStage.showAndWait();
        }
        catch (IOException e) {
            AlertError(e);
        }
    }

    @FXML
    private void onSaveGraph()
    {
        try {
            FXMLLoader saveGraphLoader = new FXMLLoader(getClass().getResource(AppSettings.save_Graph_Popup_location));
            AnchorPane saveGraphPane = saveGraphLoader.load();

            Stage popupStage = new Stage();
            popupStage.setTitle("Save Graph");

            popupStage.initModality(Modality.APPLICATION_MODAL);

            Scene popupScene = new Scene(saveGraphPane);
            popupStage.setScene(popupScene);

            ThemeManager.getThemeManager().AddScene(popupScene);

            popupStage.showAndWait();
        }
        catch (IOException e) {
            AlertError(e);
        }


    }

    public static void CreateGraphStatic(GraphData data) {
        try{
            if (data == null || data.nodes == null || data.edges == null) {
                Controller.AlertError(new Exception("AI returned invalid graph data."));
                return;
            }

            // 1. Create the new graph
            G = new Graph(data.isDirected);

            // 2. Create all the nodes
            if(data.nodes.size() > AppSettings.MAX_VERTICES)
                throw new InvalidGraphSizeException();
            for (String nodeID : data.nodes) {
                if (nodeID != null && !nodeID.trim().isEmpty()) {
                    G.createNode(nodeID);
                }
            }

            // 3. Create all the edges
            for (Services.EdgeData edge : data.edges) {
                if (edge != null && edge.from != null && edge.to != null) {
                    G.createEdge(edge.from, edge.to, edge.weight,0,0);
                }
            }
        }catch (Exception e) {
            AlertError(e);
        }


        displayGraph(G);

    }
    @FXML
    private void onEnterVertices() {
        try {
            graphContainer.getChildren().clear();
            this.G = new Graph(DirectedCheckbox.isSelected());
            String input = verticesField.getText();
            if(input.isEmpty())
                throw new NumberFormatException();
            vertexCount = Integer.parseInt(verticesField.getText());
            if(vertexCount > AppSettings.MAX_VERTICES)
                throw new InvalidGraphSizeException();
            for(int i = 1; i <= vertexCount ; i++)
                G.createNode(Integer.toString(i));

            displayGraph(G);
        } catch (NumberFormatException e) {
            AlertError(new Exception("input isn't a parsable integer"));
        } catch (InvalidGraphSizeException e) {
            AlertError(e);
        }
    }

    @FXML
    private void onAddEdge() {
        String from = edgeStartField.getText();
        String to = edgeEndField.getText();

        try
        {
            if (from.isEmpty() || to.isEmpty())
            {
                throw new InvalidEdgeException();
            }
            Integer intFrom = Integer.parseInt(from);
            Integer intTo = Integer.parseInt(to);
            String weightInput = weightField.getText();
            int weight = 0;
            if(!weightInput.isEmpty())
            {
                weight = Integer.parseInt(weightInput);
                if(weight < -AppSettings.MAX_WEIGHT || weight > AppSettings.MAX_WEIGHT)
                    throw new InvalidEdgeException();
            }

            String capacity = capacityField.getText();
            int capacityAmount = 0;
            if (!capacity.isEmpty())
            {
                capacityAmount = Integer.parseInt(capacity);
                if(capacityAmount < 0)
                    throw new InvalidCapacityException();

            }

            if(intFrom.equals(intTo))
                throw new LoopException();
            G.createEdge(intFrom.toString(),intTo.toString(),weight,0,capacityAmount);
            if(!G.isDirected())
                G.createEdge(intTo.toString(),intFrom.toString(),weight,0,capacityAmount);

            displayGraph(G);
        }
        catch (NumberFormatException e)
        {
            AlertError(new Exception("input is not a valid edge syntax"));
        }
        catch (InvalidEdgeException | LoopException | InvalidCapacityException e)
        {
            AlertError(e);
        }

        System.out.println("Added edge: " + from + " → " + to);

        edgeStartField.clear();
        edgeEndField.clear();
    }

    public void showVertexContextMenu(Graph.GraphNode vertice, ContextMenuEvent event) {
        // Create menu items
        MenuItem deleteItem = new MenuItem("Delete Vertex " + vertice.getNodeLabel());
        deleteItem.setOnAction(e -> {
            this.G.RemoveVertice(vertice);
            displayGraph(G);
        });

        Menu algorithmSubMenu = new Menu("Select Algorithm");

        MenuItem runBfsItem = new MenuItem("Run BFS");
        runBfsItem.setOnAction(event1 -> {
            run(new BFS(new Graph(this.getGraph()), vertice));
            graphPaneContextMenu.hide();
        });

        MenuItem runDfsItem = new MenuItem("Run DFS");
        runDfsItem.setOnAction(event1 -> {
            run(new DFS((this.getGraph()), vertice));
            graphPaneContextMenu.hide();
        });

        MenuItem shortestPathsTreeItem = new MenuItem("Run Shortest Paths Tree");
        shortestPathsTreeItem.setOnAction(event1 -> {
            run(new ShortestPathsTree(new Graph(this.getGraph()), vertice));
            graphPaneContextMenu.hide();
        });

        MenuItem lightestPathsItem = new MenuItem("Run Bellman-Ford");
        lightestPathsItem.setOnAction(event1 -> {
            run(new BellmanFordAlgorithm(new Graph(this.getGraph()), vertice));
            graphPaneContextMenu.hide();
        });

        MenuItem fordFelkersonItem = new MenuItem("Run Ford-Felkerson");
        fordFelkersonItem.setOnAction(event1 -> {
            OnfordFelkersonClicked(vertice);
            graphPaneContextMenu.hide();
        });

        algorithmSubMenu.getItems().addAll(runBfsItem, runDfsItem,shortestPathsTreeItem, lightestPathsItem,fordFelkersonItem);

        ContextMenu contextMenu = new ContextMenu();
        verticeContextMenu = contextMenu;
        contextMenu.getItems().addAll(deleteItem, algorithmSubMenu);

        vertice.getNodeObject().setOnMouseClicked(event1 -> {
            if (contextMenu.isShowing()) {
                contextMenu.hide();
            }
        });
        // Show the menu at the mouse's screen position
        // We show it "on" the graphPane, but at the mouse's coordinates
        contextMenu.show(graphContainer, event.getScreenX(), event.getScreenY());
    }

    public void showEdgeContextMenu(DirectedEdge edge, ContextMenuEvent event) {
        // Create menu items
        edgeContextMenu = new ContextMenu();

        MenuItem deleteItem;
        if(this.G.isDirected())
            deleteItem = new MenuItem("Delete edge "+edge.getFrom().getNodeLabel()+"-->"+edge.getTo().getNodeLabel());
        else
            deleteItem = new MenuItem("Delete edge between "+edge.getFrom().getNodeLabel()+" and "+edge.getTo().getNodeLabel());
        deleteItem.setOnAction(e -> {
            if (edgeContextMenu.isShowing()) {
                edgeContextMenu.hide();
            }
            if(!this.G.isDirected())
                this.G.removeEdge(edge.getTo().getNodeLabel(),edge.getFrom().getNodeLabel());
            this.G.removeEdge(edge.getFrom().getNodeLabel(),edge.getTo().getNodeLabel());
            displayGraph(G);

        });


        edgeContextMenu.getItems().addAll(deleteItem);
        edge.getEdgeGroup().setOnMouseClicked(event1 -> {
            if (edgeContextMenu.isShowing()) {
                edgeContextMenu.hide();
            }
        });
        edgeContextMenu.show(graphContainer, event.getScreenX(), event.getScreenY());
    }

    private void run(Algorithm algorithm)  {
        if (!algorithm.checkValidity())
        {
            AlertError(new InvalidAlgorithmInputException(algorithm));
        }
        else
        {
            algorithm.Run();
            algorithm.DisplayResults();
        }

    }

    private void OnfordFelkersonClicked(Graph.GraphNode source)
    {
        try {
            FXMLLoader saveGraphLoader = new FXMLLoader(getClass().getResource(AppSettings.Max_Flow_Popup_Location));
            AnchorPane saveGraphPane = saveGraphLoader.load();

            ControllerManager.getMaxFlowPopupController().setSourceVertice(source);
            Stage popupStage = new Stage();
            popupStage.setTitle("Enter Destination vertice");
            popupStage.initModality(Modality.APPLICATION_MODAL);
            Image icon = new Image(Objects.requireNonNull(getClass().getResourceAsStream(AppSettings.App_Icon_location)));
            popupStage.getIcons().add(icon);

            Scene popupScene = new Scene(saveGraphPane);
            popupStage.setScene(popupScene);
            ThemeManager.getThemeManager().AddScene(popupScene);
            popupStage.showAndWait();
        }
        catch (IOException e) {
            AlertError(e);
        }
    }

    public void runFordFelkerson(Graph.GraphNode sourceNode,Graph.GraphNode destination)
    {
        run(new FordFelkersonAlgorithm(new Graph(ControllerManager.getGraphInputController().getGraph()), sourceNode,destination));
    }

    public static void displayGraph(Graph graph) {
        ControllerManager.getGraphInputController().graphContainer.getChildren().clear();
        Group group = new Group();
        graph.addGraphToGroup(group);
        ControllerManager.getGraphInputController().graphContainer.getChildren().add(group);
    }
}
