package Controllers;

import Exceptions.InvalidAlgorithmInputException;
import Exceptions.InvalidEdgeException;
import Exceptions.InvalidGraphSizeException;
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

    public AnchorPane getGraphContainer()
    {
        return graphContainer;
    }

    public void initialize() {
        try {
            FXMLLoader algoLoader = new FXMLLoader(getClass().getResource(AppSettings.Algorithms_Pane_Location));
            ScrollPane algorithmsPane = algoLoader.load();

            FXMLLoader nodeLoader = new FXMLLoader(getClass().getResource(AppSettings.Vertice_Algorithms_Pane_Location));
            ScrollPane nodeDetailsPane = nodeLoader.load();

            nodeDetailsPane.addEventFilter(MouseEvent.MOUSE_PRESSED, event -> {
                if (event.getTarget().equals(nodeDetailsPane)) {
                    event.consume();
                }
            });

            algoPlaceholder.getChildren().setAll(algorithmsPane);

            graphContainer.setOnMouseClicked(event -> {
                if (event.getTarget() == graphContainer) {
                    CurrentlyPressedNodeHelper.setCurrentNode(null);
                }
            });

            CurrentlyPressedNodeHelper.setOnFocusChange(() -> {
                Platform.runLater(() -> {
                    if (CurrentlyPressedNodeHelper.hasFocus()) {
                        ControllerManager.getVerticeWiseAlgorithmsController().updateCurrentNodeLabels();
                        algoPlaceholder.getChildren().setAll(nodeDetailsPane);

                    } else {
                        algoPlaceholder.getChildren().setAll(algorithmsPane);
                    }
                });
            });

        } catch (IOException e) {
            AlertError(e);
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
            enterButton.setDisable(false);
            if (isNowSelected) {
                System.out.println("graph is directed");
            } else {
                System.out.println("graph isnt directed");
            }
            graphContainer.getChildren().clear();
            this.G = new Graph(isNowSelected);
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

            if(intFrom.equals(intTo))
                throw new LoopException();
            G.createEdge(intFrom.toString(),intTo.toString());
            if(!G.isDirected())
                G.createEdge(intTo.toString(),intFrom.toString());

            displayGraph(G);
        }
        catch (NumberFormatException e)
        {
            AlertError(new Exception("input is not a valid edge syntax"));
        }
        catch (InvalidEdgeException | LoopException e)
        {
            AlertError(e);
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
