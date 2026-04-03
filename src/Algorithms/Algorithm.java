package Algorithms;

import Controllers.*;
import Exceptions.InvalidAlgorithmInputException;
import GraphVisualizer.AppSettings;
import GraphVisualizer.Graph;
import GraphVisualizer.ThemeManager;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;
import java.util.Objects;

import static Controllers.Controller.AlertError;

public abstract class Algorithm implements Command{

    protected Graph G;
    protected String AlgorithmName;
    protected String requiredInput;
    protected Graph graphResult;
    protected static String AlgorithmDescription;

    Algorithm() {};

    protected abstract void INIT(Graph graph);

    public void Execute(Graph graph)
    {
        this.G = graph;
        Platform.runLater(() ->
                RunAlgorithm(this));

    }

    protected abstract String UpdateParams(Map<String, String> params);

    @Override
    public String executeCommand(Map<String, String> params) {

        try
        {
            if (!this.checkValidity())
            {
                return new InvalidAlgorithmInputException(this).getMessage();
            }
            else
            {
                String error = UpdateParams(params);
                if(error != null)
                    return error;
                this.Run();
                return this.WriteOutputToBuffer();

            }
        }catch (Exception e)
        {
            return "An error occured while executing the command : +"+e.getMessage();
        }
    }

    private void RunAlgorithm(Algorithm algorithm) {
        try
        {
            if (!algorithm.checkValidity())
            {
                AlertError(new InvalidAlgorithmInputException(algorithm));
            }
            else
            {
                Platform.runLater(() -> {
                    algorithm.Run();
                    if(ControllerManager.getGraphInputController().isApiMode())
                        algorithm.OutputContentToFile();
                    else
                        algorithm.DisplayResults();
                });

            }
        }catch (Exception e)
        {
            AlertError(e);
        }
    }

    public static String getAlgorithmDescription() { return AlgorithmDescription; }

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
            AlertError(e);
        }
    }

    public abstract String WriteOutputToBuffer();

    public void PrintOutputToFile(String fileName)
    {
        try {
            Path apiModeOutput = Paths.get(System.getProperty("user.dir"), "ApiModeOutput");
            Files.createDirectories(apiModeOutput);
            Path textFile = apiModeOutput.resolve(fileName + ".txt");

            WriteOutputToFile(textFile);

            ControllerManager.getGraphWiseAlgorithmsController().SuccessPopup("results stored as : " + fileName + ".txt");

        } catch (IOException e) {
            AlertError(e);
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
            AlertError(e);
        }
    }
}

