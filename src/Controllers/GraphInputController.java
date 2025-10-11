package Controllers;

import Exceptions.InvalidEdgeException;
import Exceptions.LoopException;
import GraphVisualizer.*;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Group;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.Scene;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;

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

    private int vertexCount = 0;
    private Graph G = new Graph(true);


    public void initialize() {
        try {
            // Load the main (Algorithms) pane
            FXMLLoader algoLoader = new FXMLLoader(getClass().getResource(AppSettings.Algorithms_Pane_Location));
            ScrollPane algorithmsPane = algoLoader.load();

            // Load the node details pane (for when a GraphNode is focused)
            FXMLLoader nodeLoader = new FXMLLoader(getClass().getResource(AppSettings.Vertice_Algorithms_Pane_Location));
            ScrollPane nodeDetailsPane = nodeLoader.load();

            nodeDetailsPane.addEventFilter(MouseEvent.MOUSE_PRESSED, event -> {
                // Only consume clicks on the pane itself, not on its children
                if (event.getTarget().equals(nodeDetailsPane)) {
                    event.consume();
                }
            });

            // Initially show the algorithms pane
            algoPlaceholder.getChildren().setAll(algorithmsPane);

            graphContainer.setOnMouseClicked(event -> {
                // Only deselect if clicking on the background itself
                if (event.getTarget() == graphContainer) {
                    CurrentlyPressedNodeHelper.setCurrentNode(null);
                }
            });

            // 👇 Register a reactive listener for node focus changes
            CurrentlyPressedNodeHelper.setOnFocusChange(() -> {
                Platform.runLater(() -> {
                    if (CurrentlyPressedNodeHelper.hasFocus()) {
                        // A node is in focus → show the node details pane

                        ControllerManager.getVerticeWiseAlgorithmsController().updateCurrentNodeLabels();
                        algoPlaceholder.getChildren().setAll(nodeDetailsPane);

                    } else {
                        // No node in focus → show the algorithms pane again
                        algoPlaceholder.getChildren().setAll(algorithmsPane);
                    }
                });
            });

        } catch (IOException e) {
            AlertError(e, null);
        }

        // Theme dropdown setup
        ThemeBox.getItems().addAll(Theme.values());
        ThemeBox.setValue(Theme.DEFAULT);
        ThemeBox.valueProperty().addListener((obs, oldTheme, newTheme) -> {
            if (newTheme != null) {
                ThemeManager.getThemeManager().switchTheme(newTheme);
            }
        });

        DirectedCheckbox.selectedProperty().addListener((obs, wasSelected, isNowSelected) -> {
            if (isNowSelected) {
                System.out.println("graph is directed");
                //displayGraph(new Graph(true));
            } else {
                System.out.println("graph isnt directed");
                //displayGraph(new Graph(false));
            }
            graphContainer.getChildren().clear();
        });
        // Register this controller for access elsewhere
        ControllerManager.setGraphInputController(this);
    }


    public Graph getGraph()
    {
        return G;
    }

    @FXML
    private void onLoadGraph()
    {
        this.G = GraphSerializer.loadGraph();
        assert G != null;
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
            AlertError(e,null);
        }


    }

    @FXML
    private void onEnterVertices() {
        try {
            String input = verticesField.getText();
            if(input.isEmpty())
                throw new NumberFormatException();
            vertexCount = Integer.parseInt(verticesField.getText());
            enterButton.setDisable(true);
            for(Integer i = 1 ; i <= vertexCount ; i++)
                G.createNode(i.toString());

            displayGraph(G);
        } catch (NumberFormatException e) {
            AlertError(e,"input isn't a parsable integer");
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

            if(intFrom.equals(intTo))
                throw new LoopException();
            G.createEdge(intFrom.toString(),intTo.toString());
            displayGraph(G);
        }
        catch (NumberFormatException e)
        {
            AlertError(e,"input is not a valid edge syntax");
        }
        catch (InvalidEdgeException | LoopException e)
        {
            AlertError(e,null);
        }

        System.out.println("Added edge: " + from + " → " + to);

        edgeStartField.clear();
        edgeEndField.clear();
    }


    public void displayGraph(Graph graph) {
        graphContainer.getChildren().clear();
        Group group = new Group();
        graph.addGraphToGroup(group);
        graphContainer.getChildren().add(group);
    }
}
