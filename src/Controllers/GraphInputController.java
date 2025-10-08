package Controllers;

import Exceptions.InvalidEdgeException;
import Exceptions.LoopException;
import GraphVisualizer.Graph;
import javafx.fxml.FXML;
import javafx.scene.Group;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;

public class GraphInputController extends Controller{

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
    private Label GraphMeasurementsLabel;

    @FXML
    private Label enterVerticesLabel;

    @FXML
    private Label enterEdgesLabel;

    @FXML
    private AnchorPane graphContainer;
    // Store graph data
    private int vertexCount = 0;
    private Graph G = new Graph();

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
        Group group = new Group();
        graph.addGraphToGroup(group);
        graphContainer.getChildren().add(group);
    }
}
