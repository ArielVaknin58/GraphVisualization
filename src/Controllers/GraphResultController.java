package Controllers;

import GraphVisualizer.AppSettings;
import GraphVisualizer.Graph;
import GraphVisualizer.GraphSerializer;
import GraphVisualizer.ThemeManager;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.Objects;

public class GraphResultController extends Controller{

    @FXML
    private AnchorPane graphOutputContainer;
    @FXML
    private Button saveResultButton;
    @FXML
    private Label stateNDLabel;


    private Graph graph;
    @FXML
    private void initialize()
    {
        ControllerManager.setGraphResultController(this);
    }

    public void displayGraph(Graph graph) {
        graphOutputContainer.getChildren().clear();
        Group group = new Group();
        graph.addGraphToGroup(group);
        graphOutputContainer.getChildren().add(group);
    }

    public Label getStateNDLabel() {
        return stateNDLabel;
    }

    @FXML
    private void onSaveGraph()
    {
        try {
            FXMLLoader saveGraphLoader = new FXMLLoader(getClass().getResource(AppSettings.save_Graph_Popup_location));
            AnchorPane saveGraphPane = saveGraphLoader.load();

            Stage popupStage = new Stage();
            popupStage.setTitle("Save Graph");
            Image icon = new Image(Objects.requireNonNull(getClass().getResourceAsStream(AppSettings.App_Icon_location)));
            popupStage.getIcons().add(icon);

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
}
