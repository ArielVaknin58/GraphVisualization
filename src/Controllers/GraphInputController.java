package Controllers;

import Exceptions.InvalidEdgeException;
import Exceptions.LoopException;
import GraphVisualizer.*;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Group;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;

import java.io.IOException;

public class GraphInputController extends Controller{

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

    private int vertexCount = 0;
    private Graph G = new Graph(true);

    public void initialize()
    {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(AppSettings.Algorithms_Pane_Location));
            ScrollPane algorithmsPane = loader.load();
            algoPlaceholder.getChildren().setAll(algorithmsPane);

        } catch (IOException e) {
            AlertError(e,null);
        }

        ThemeBox.getItems().addAll(Theme.values());
        ThemeBox.setValue(Theme.DEFAULT);
        ThemeBox.valueProperty().addListener((obs, oldTheme, newTheme) -> {
            if (newTheme != null) {
                ThemeManager.getThemeManager().switchTheme(newTheme);
            }
        });
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
        displayGraph(G);
    }

    @FXML
    private void onSaveGraph()
    {
        GraphSerializer.saveGraph(G,"bloop");

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
