package GraphVisualizer;

import Controllers.APIModeController;
import Controllers.Controller;
import Controllers.ControllerManager;
import Controllers.GraphInputController;
import Services.GraphData;
import Services.GraphTools;
import com.google.gson.GsonBuilder;
import io.javalin.Javalin;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import javafx.fxml.FXMLLoader;
import java.util.Objects;

public class GraphVisualizer extends Application {

    public static void main(String[] args) {

        if (args.length > 0 && args[0].equals(AppSettings.API_MODE_INVOKER)) {
            ControllerManager.setApiModeController(APIModeController.CreateInstance(true));
            runApiMode();
        } else {
            ControllerManager.setApiModeController(APIModeController.CreateInstance(false));
            launch(args);
        }
    }

    private static void runApiMode()
    {
        APIModeController.getInstance().initAPIMode();
    }

    @Override
    public void start(Stage primaryStage) {

        loadMainStage(primaryStage);
    }

    private void loadMainStage(Stage primaryStage)
    {
        try
        {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(AppSettings.Graph_Input_Location));
            Scene scene = new Scene(loader.load());
            ThemeManager.getThemeManager().AddScene(scene);
            Image icon = new Image(Objects.requireNonNull(getClass().getResourceAsStream(AppSettings.App_Icon_location)));
            primaryStage.getIcons().add(icon);

            primaryStage.setScene(scene);
            primaryStage.setTitle(AppSettings.App_Title);
            primaryStage.show();
        }
        catch (Exception e)
        {
            Controller.AlertError(e);
        }
    }







}
