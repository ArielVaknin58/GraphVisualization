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
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.util.Objects;

public class Controller {


    public static void AlertError(Exception e){

        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setHeaderText("error");
        alert.setContentText(e.getMessage());
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

            Image icon = new Image(Objects.requireNonNull(getClass().getResourceAsStream(AppSettings.App_Icon_location)));
            popupStage.getIcons().add(icon);
            Scene popupScene = new Scene(pane, 430, 150);
            ThemeManager.getThemeManager().AddScene(popupScene);
            popupStage.setScene(popupScene);
            popupStage.showAndWait();

        } catch (Exception e) {AlertError(e);}

    }
}
