package Controllers;

import Algorithms.*;
import Exceptions.*;
import GraphVisualizer.*;
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

import java.io.IOException;
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

    private int vertexCount = 0;
    private Graph G = new Graph(true);

    public AnchorPane getGraphContainer()
    {
        return graphContainer;
    }

    public void initialize() {
        try {
            FXMLLoader algoLoader = new FXMLLoader(getClass().getResource(AppSettings.Algorithms_Pane_Location));
            ScrollPane algorithmsPane = algoLoader.load();

//            FXMLLoader nodeLoader = new FXMLLoader(getClass().getResource(AppSettings.Vertice_Algorithms_Pane_Location));
//            ScrollPane nodeDetailsPane = nodeLoader.load();

//            nodeDetailsPane.prefWidthProperty().bind(algoPlaceholder.widthProperty());
//            nodeDetailsPane.prefHeightProperty().bind(algoPlaceholder.heightProperty());
            algorithmsPane.prefWidthProperty().bind(algoPlaceholder.widthProperty());
            algorithmsPane.prefHeightProperty().bind(algoPlaceholder.heightProperty());
//
//            nodeDetailsPane.addEventFilter(MouseEvent.MOUSE_PRESSED, event -> {
//                if (event.getTarget().equals(nodeDetailsPane)) {
//                    event.consume();
//                }
//            });

            algoPlaceholder.getChildren().setAll(algorithmsPane);


        } catch (IOException e) {
            AlertError(e);
        }

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
            graphPaneContextMenu.hide(); // Hide after action
        });

        MenuItem clearGraphItem = new MenuItem("Clear Graph");
        clearGraphItem.setOnAction(event -> {
            System.out.println("Clear Graph action");
            this.G = new Graph(DirectedCheckbox.isSelected());
            displayGraph(G);
            graphPaneContextMenu.hide(); // Hide after action
        });


        // Add the direct items AND THE SUBMENU to the main context menu
        graphPaneContextMenu.getItems().addAll(addNodeItem, clearGraphItem);

        // --- Event Handlers for the Pane ---
        graphContainer.setOnContextMenuRequested(event -> {
            currentContextMenuEvent = event;
            System.out.println("Context menu requested"); // Logging
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
                if (event.getTarget() == graphContainer) {
                    System.out.println("Primary click on container background - Deselecting node.");

                    //CurrentlyPressedNodeHelper.setCurrentNode(null);
                    // Add any UI update logic needed after deselection here
                }
            }
            // Hide context menu if it's showing

        });
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
            //enterButton.setDisable(true);
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
        MenuItem deleteItem = new MenuItem("Delete edge "+edge.getFrom().getNodeLabel()+"-->"+edge.getTo().getNodeLabel());
        deleteItem.setOnAction(e -> {
            if(!this.G.isDirected())
                this.G.removeEdge(edge.getTo().getNodeLabel(),edge.getFrom().getNodeLabel());
            this.G.removeEdge(edge.getFrom().getNodeLabel(),edge.getTo().getNodeLabel());
            displayGraph(G);
        });


        ContextMenu contextMenu = new ContextMenu();
        contextMenu.getItems().addAll(deleteItem);
        edge.getEdgeGroup().setOnMouseClicked(event1 -> {
            if (contextMenu.isShowing()) {
                contextMenu.hide();
            }
        });
        contextMenu.show(graphContainer, event.getScreenX(), event.getScreenY());
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

    public void displayGraph(Graph graph) {
        graphContainer.getChildren().clear();
        Group group = new Group();
        graph.addGraphToGroup(group);
        graphContainer.getChildren().add(group);
    }
}
