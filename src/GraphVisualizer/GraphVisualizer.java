package GraphVisualizer;


import Controllers.Controller;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.fxml.FXMLLoader;

import java.util.logging.*;

public class GraphVisualizer extends Application {

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        try
        {

            FXMLLoader loader = new FXMLLoader(getClass().getResource(AppSettings.Graph_Input_Location));
            Scene scene = new Scene(loader.load());
            ThemeManager.getThemeManager().AddScene(scene);

            primaryStage.setScene(scene);
            primaryStage.setTitle(AppSettings.App_Title);
            primaryStage.show();
        }
        catch (Exception e)
        {
            Controller.AlertError(e,null);
        }

    }







}
