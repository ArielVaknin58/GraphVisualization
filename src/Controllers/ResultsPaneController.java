package Controllers;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
// You will also need to create a generic ResultPair class if you haven't already
import Algorithms.ResultPair;

public class ResultsPaneController<K, V> extends Controller {

    @FXML
    private TableView<ResultPair<K, V>> resultsTable;
    @FXML
    private TableColumn<ResultPair<K, V>, K> nodeCol;
    @FXML
    private TableColumn<ResultPair<K, V>, V> valueCol;
    @FXML
    private Label resultsLabel;

    public void initialize() {
        ControllerManager.setResultsPaneController(this);
        nodeCol.setCellValueFactory(new PropertyValueFactory<>("key"));
        valueCol.setCellValueFactory(new PropertyValueFactory<>("value"));
    }

    public TableView<ResultPair<K, V>> getResultsTable() {
        return resultsTable;
    }

    public TableColumn<ResultPair<K, V>, V> getValueCol() {
        return valueCol;
    }

    public TableColumn<ResultPair<K, V>, K> getNodeCol() {
        return nodeCol;
    }

    public Label getResultsLabel() {
        return resultsLabel;
    }
}