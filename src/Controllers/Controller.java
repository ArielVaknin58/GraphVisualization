package Controllers;

import GraphVisualizer.AppSettings;
import GraphVisualizer.ThemeManager;
import javafx.animation.FadeTransition;
import javafx.animation.Interpolator;
import javafx.animation.TranslateTransition;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Duration;

public class Controller {


    public static void AlertError(Exception e,String optionalMessage){

        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setHeaderText("error");
        alert.setContentText(optionalMessage == null ? e.getMessage() : optionalMessage);
        alert.showAndWait();
    }

    public void PopupMessage(String message) {

        try {
            // Load FXML
            FXMLLoader loader = new FXMLLoader(getClass().getResource(AppSettings.Generic_PopupWindow_location));
            AnchorPane pane = loader.load();

            // Lookup for label and image
            Label label = (Label) pane.lookup("#label");
            ImageView myImage = (ImageView) pane.lookup("#myImage");

            // Ensure label is set correctly
            label.setText(message);

            // Fade effect
            FadeTransition fade = new FadeTransition();
            fade.setNode(myImage);
            fade.setDuration(Duration.millis(1000));
            fade.setCycleCount(TranslateTransition.INDEFINITE);
            fade.setInterpolator(Interpolator.LINEAR);
            fade.setFromValue(0);
            fade.setToValue(1);
            fade.play();

            // Show popup
            Stage popupStage = new Stage();
            popupStage.initModality(Modality.APPLICATION_MODAL);
            popupStage.setTitle("result");

            Scene popupScene = new Scene(pane, 430, 150);
            ThemeManager.getThemeManager().AddScene(popupScene);
            popupStage.setScene(popupScene);
            popupStage.showAndWait();

        } catch (Exception e) {AlertError(e,null);}

    }
}
