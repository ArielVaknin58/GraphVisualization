package Controllers;

import javafx.scene.control.Alert;

public class Controller {

    public static void AlertError(Exception e,String optionalMessage){

        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setHeaderText("error");
        alert.setContentText(optionalMessage == null ? e.getMessage() : optionalMessage);
        alert.showAndWait();
    }

}
