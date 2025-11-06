package Algorithms;

import Controllers.Controller;
import Controllers.ControllerManager;
import Controllers.GraphResultController;
import Controllers.SaveGraphPopupController;
import GraphVisualizer.AppSettings;
import GraphVisualizer.Graph;
import GraphVisualizer.ThemeManager;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Objects;

public abstract class Algorithm {

    protected Graph G;
    protected String AlgorithmName;
    protected String requiredInput;
    protected Graph graphResult;

    Algorithm() {};

    public String getAlgorithmName() {return this.AlgorithmName;}

    public String getRequiredInputDescription() {return this.requiredInput;}

    public abstract void Run();

    public abstract Boolean checkValidity();

    public abstract void DisplayResults();

    public abstract void CreateOutputGraph();


    public void OutputContentToFile()
    {
        try {
            FXMLLoader saveGraphLoader = new FXMLLoader(getClass().getResource(AppSettings.save_Graph_Popup_location));
            AnchorPane saveGraphPane = saveGraphLoader.load();
            SaveGraphPopupController loader = saveGraphLoader.getController();


            Stage popupStage = new Stage();
            popupStage.setTitle("Save Results as file");

            popupStage.initModality(Modality.APPLICATION_MODAL);

            Scene popupScene = new Scene(saveGraphPane);
            popupStage.setScene(popupScene);
            loader.setAlgorithm(this);

            ThemeManager.getThemeManager().AddScene(popupScene);

            popupStage.showAndWait();
        }
        catch (IOException e) {
            Controller.AlertError(e);
        }
    }


    public void PrintOutputToFile(String fileName)
    {
        try {
            Path apiModeOutput = Paths.get(System.getProperty("user.dir"), "ApiModeOutput");
            Files.createDirectories(apiModeOutput);
            Path textFile = apiModeOutput.resolve(fileName + ".txt");

            WriteOutputToFile(textFile);

            ControllerManager.getGraphWiseAlgorithmsController().SuccessPopup("results stored as : " + fileName + ".txt");

        } catch (IOException e) {
            Controller.AlertError(e);
        }
    }

    protected void WriteOutputToFile(Path fileName) {};

    protected void loadResultsPane()
    {
        if(this.G.V.isEmpty())
        {
            ControllerManager.getGraphInputController().infoPopup("Graph is empty,nothing to show");
        }
        try
        {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(AppSettings.Graph_results_location));
            Scene scene = new Scene(loader.load());
            ThemeManager.getThemeManager().AddScene(scene);
            GraphResultController controller = loader.getController();
            CreateOutputGraph();
            controller.displayGraph(this.graphResult);

            Stage resultStage = new Stage();
            Image icon = new Image(Objects.requireNonNull(getClass().getResourceAsStream(AppSettings.App_Icon_location)));
            resultStage.getIcons().add(icon);
            //resultStage.initModality(Modality.APPLICATION_MODAL);
            resultStage.setTitle(this.AlgorithmName+" results :");

            resultStage.setScene(scene);
            resultStage.show();
        }
        catch (Exception e)
        {
            Controller.AlertError(e);
        }
    }
}

