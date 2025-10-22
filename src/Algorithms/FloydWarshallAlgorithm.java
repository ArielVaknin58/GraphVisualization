package Algorithms;

import Controllers.ControllerManager;
import Controllers.ResultsPaneController;
import GraphVisualizer.AppSettings;
import GraphVisualizer.DirectedEdge;
import GraphVisualizer.Graph;
import GraphVisualizer.ThemeManager;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.image.Image;
import javafx.stage.Modality;
import javafx.stage.Stage;
import java.util.*;

import static Controllers.Controller.AlertError;

public class FloydWarshallAlgorithm extends Algorithm{

    public static final String AlgorithmDescription = "This algorithm finds the lightest paths from every couple of vertices in graph G.";
    private HashMap<Graph.GraphNode,HashMap<Graph.GraphNode,Integer>> weightsMatrix;
    private HashMap<Graph.GraphNode,HashMap<Graph.GraphNode, Graph.GraphNode>> parents;
    private boolean hasNegativeCycle;

    public FloydWarshallAlgorithm(Graph graph)
    {
        this.G = graph;
        this.AlgorithmName = "Floyd-Warshall Algorithm";
        this.requiredInput = "a directed,weighted graph G=(V,E)";
        this.hasNegativeCycle = false;
        init();

    }

    private void init()
    {
        this.weightsMatrix = new HashMap<>();
        this.parents = new HashMap<>();

        for (Graph.GraphNode node : G.V) {
            weightsMatrix.put(node, new HashMap<>());
            parents.put(node, new HashMap<>());
        }

        for(Graph.GraphNode current1 : G.V)
        {
            for(Graph.GraphNode current2 : G.V)
            {
                if(current1.equals(current2))
                {
                    weightsMatrix.get(current1).put(current2,0);
                }
                else
                {
                    weightsMatrix.get(current1).put(current2,Integer.MAX_VALUE);
                }
                parents.get(current1).put(current2,null);
            }
        }

        for(DirectedEdge edge : G.E)
        {
            weightsMatrix.get(edge.getFrom()).put(edge.getTo(), edge.getWeight());
        }

    }

    @Override
    public void Run() {

        for(Graph.GraphNode kNode : G.V)
        {
            for(Graph.GraphNode iNode : G.V)
            {
                for(Graph.GraphNode jNode : G.V)
                {
                    int distIK = weightsMatrix.get(iNode).get(kNode);
                    int distKJ = weightsMatrix.get(kNode).get(jNode);

                    if (distIK == Integer.MAX_VALUE || distKJ == Integer.MAX_VALUE) {
                        continue;
                    }

                    int newDist = distIK + distKJ;
                    int oldDist = weightsMatrix.get(iNode).get(jNode);

                    if (newDist < oldDist) {
                        weightsMatrix.get(iNode).put(jNode, newDist);
                        parents.get(iNode).put(jNode, parents.get(kNode).get(jNode));
                    }
                }
            }
        }
        for(Graph.GraphNode kNode : G.V) {
            if (weightsMatrix.get(kNode).get(kNode) < 0) {
                hasNegativeCycle = true;
                return;
            }
        }
    }

    @Override
    public Boolean checkValidity() {
        return this.G.isDirected();
    }

    @Override
    public void DisplayResults() {
        if (hasNegativeCycle) {
            ControllerManager.getGraphInputController().infoPopup("The graph contains a negative-weight cycle !");
            return;
        }

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(AppSettings.Results_Popup_location));
            Scene scene = new Scene(loader.load());
            ThemeManager.getThemeManager().AddScene(scene);
            ResultsPaneController controller = loader.getController();
            controller.getResultsLabel().setText("Lightest paths in the graph :");
            TableView<ObservableList<String>> table = controller.getResultsTable(); // Make sure this getter exists
            table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_ALL_COLUMNS);
            List<Graph.GraphNode> orderedNodes = new ArrayList<>(G.V);

            // Create the Header List
            List<String> headers = new ArrayList<>();
            headers.add("from\\to"); // Top-left corner cell
            for (Graph.GraphNode node : orderedNodes) {
                headers.add(node.getNodeLabel());
            }

            ObservableList<ObservableList<String>> data = createMatrix(orderedNodes);

            table.getColumns().clear();
            table.getItems().clear();

            for (int i = 0; i < headers.size(); i++) {
                final int colIndex = i;

                TableColumn<ObservableList<String>, String> column = new TableColumn<>(headers.get(i));

                column.setCellValueFactory(param ->
                        new SimpleStringProperty(param.getValue().get(colIndex))
                );
                if (i == 0) {
                    column.setStyle("-fx-font-weight: bold;");
                }
                table.getColumns().add(column);
            }
            table.setItems(data);

            Stage resultStage = new Stage();
            Image icon = new Image(Objects.requireNonNull(getClass().getResourceAsStream(AppSettings.App_Icon_location)));
            resultStage.getIcons().add(icon);
            resultStage.initModality(Modality.APPLICATION_MODAL);
            resultStage.setTitle(this.AlgorithmName +" results");
            resultStage.setScene(scene);
            resultStage.show();

        } catch (Exception e) {
            AlertError(e);
        }
    }

    @Override
    public void CreateOutputGraph() {}

    private ObservableList<ObservableList<String>> createMatrix(List<Graph.GraphNode> orderedNodes)
    {
        ObservableList<ObservableList<String>> data = FXCollections.observableArrayList();

        for (Graph.GraphNode iNode : orderedNodes) {
            ObservableList<String> row = FXCollections.observableArrayList();

            // Add the row header label (e.g., "1", "2"...)
            row.add(iNode.getNodeLabel());

            for (Graph.GraphNode jNode : orderedNodes) {
                // Get the weight from the matrix you already calculated in Run()
                Integer weight = weightsMatrix.get(iNode).get(jNode);

                String displayWeight = (weight == Integer.MAX_VALUE) ? "∞" : weight.toString();

                row.add(displayWeight);
            }
            data.add(row);
        }

        return data; // Return only the data rows
    }

}
