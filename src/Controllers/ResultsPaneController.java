package Controllers;

import Algorithms.NodeResult;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;

public class ResultsPaneController extends Controller{

    @FXML
    private TableView<NodeResult> resultsTable;
    @FXML
    private TableColumn<NodeResult, String> nodeCol;
    @FXML
    private TableColumn<NodeResult, Integer> valueCol;
    @FXML
    private Label resultsLabel;

    public void initialize() {
        ControllerManager.setResultsPaneController(this);
        nodeCol.setCellValueFactory(new PropertyValueFactory<>("node"));
        valueCol.setCellValueFactory(new PropertyValueFactory<>("value"));
    }

    public TableView<NodeResult> getResultsTable() {
        return resultsTable;
    }

    public TableColumn<NodeResult, Integer> getValueCol() {
        return valueCol;
    }

    public TableColumn<NodeResult, String> getNodeCol() {
        return nodeCol;
    }

    public Label getResultsLabel() {
        return resultsLabel;
    }
}
