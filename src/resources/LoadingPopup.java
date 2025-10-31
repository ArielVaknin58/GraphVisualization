package resources;

import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

public class LoadingPopup {

    private final Stage dialogStage;

    public LoadingPopup() {
        dialogStage = new Stage();
        // This blocks interaction with other windows
        dialogStage.initModality(Modality.APPLICATION_MODAL);
        // This removes the window buttons (minimize, close, etc.)
        dialogStage.initStyle(StageStyle.UNDECORATED);

        // 1. The spinning animation
        ProgressIndicator progressIndicator = new ProgressIndicator();
        progressIndicator.setProgress(ProgressIndicator.INDETERMINATE_PROGRESS);

        // 2. The text
        Label loadingLabel = new Label("Loading, please wait...");
        loadingLabel.setStyle("-fx-font-size: 14px; -fx-text-fill: -fx-text-base-color;");

        // 3. The layout
        VBox vbox = new VBox(20, progressIndicator, loadingLabel);
        vbox.setAlignment(Pos.CENTER);
        vbox.setStyle("-fx-padding: 30; -fx-background-color: -fx-background; -fx-border-color: -fx-box-border; -fx-border-width: 1; -fx-background-radius: 6; -fx-border-radius: 6;");

        Scene scene = new Scene(vbox);
        dialogStage.setScene(scene);
    }

    public void show() {
        dialogStage.show();
    }

    public void hide() {
        dialogStage.close();
    }
}