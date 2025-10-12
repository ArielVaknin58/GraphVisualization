package Controllers;

import javafx.fxml.FXML;
import javafx.scene.layout.AnchorPane;

public class GraphResultController extends Controller{

    @FXML
    private AnchorPane graphOutputContainer;




    private void initialize()
    {
        ControllerManager.setGraphResultController(this);
    }
}
