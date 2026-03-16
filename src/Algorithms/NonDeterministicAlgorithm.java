package Algorithms;

import Controllers.Controller;
import Controllers.ControllerManager;
import Controllers.GraphInputController;
import Controllers.GraphResultController;
import GraphVisualizer.AppSettings;
import GraphVisualizer.Graph;
import GraphVisualizer.ThemeManager;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import javafx.util.Duration;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

public abstract class NonDeterministicAlgorithm extends Algorithm{

    protected int iterations;
    protected int k;
    protected Set<Graph.GraphNode> currentSet;
    protected boolean isSetFound;
    protected Timeline timeline;
    protected int counter = 1;


    NonDeterministicAlgorithm(Graph g, int iterations, int k)
    {
        this.G = g;
        this.iterations = iterations;
        this.k = k;
        this.isSetFound = false;
        this.currentSet = new HashSet<>();
    }
    protected void LoadAndRun()
    {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(AppSettings.Graph_results_location));
            Scene scene = new Scene(loader.load());
            ThemeManager.getThemeManager().AddScene(scene);
            GraphResultController controller = loader.getController();

            Run();
            CreateOutputGraph();
            if(!ControllerManager.getGraphInputController().isApiMode())
                controller.displayGraph(this.graphResult);

            Stage resultStage = new Stage();
            Image icon = new Image(Objects.requireNonNull(getClass().getResourceAsStream(AppSettings.App_Icon_location)));
            resultStage.getIcons().add(icon);
            resultStage.setTitle(this.AlgorithmName + " results :");
            resultStage.setScene(scene);
            resultStage.show();
            controller.getStateNDLabel().setText("Looking... "+counter+"/"+iterations);
            // --- USE A TIMELINE FOR THE ANIMATION ---

            // Stop any previous timeline
            if (timeline != null) {
                timeline.stop();
            }

//            // Create a new timeline
//            timeline = new Timeline();
//            // We set iterations - 1 because we already ran one iteration
//            timeline.setCycleCount(iterations - 1);
//
//            // Create a KeyFrame that runs every 100 milliseconds
//            KeyFrame keyFrame = new KeyFrame(Duration.millis(100), event -> {
//                if (isSetFound) {
//                    timeline.stop();
//                    controller.getStateNDLabel().setText("Found !");
//                    return;
//                }
//
//                counter++;
//                controller.displayGraph(this.G);
//                controller.getStateNDLabel().setText("Looking... "+counter+"/"+iterations);
//                Run();
//                CreateOutputGraph();
//                controller.displayGraph(this.graphResult);
//            });
//
//            timeline.getKeyFrames().add(keyFrame);

            timeline = new Timeline();
            timeline.setCycleCount(iterations - 1);

            KeyFrame keyFrame = new KeyFrame(Duration.millis(100), event -> {
                if (isSetFound) {
                    timeline.stop();
                    controller.getStateNDLabel().setText("Found !");
                    return;
                }

                counter++;

                // --- CHANGE STARTS HERE ---
                // 1. Perform calculations first
                Run();
                CreateOutputGraph();

                // 2. Update the UI only once with the final result of this iteration
                // This prevents the "flicker" caused by drawing the original graph (this.G)
                controller.displayGraph(this.graphResult);
                controller.getStateNDLabel().setText("Looking... " + counter + "/" + iterations);
                // --- CHANGE ENDS HERE ---
            });

            timeline.getKeyFrames().add(keyFrame);


            timeline.setOnFinished(event -> {
                if (!isSetFound) {
                    controller.getStateNDLabel().setText("not found...");
                    controller.displayGraph(this.G);
                }
            });

            // Play the animation
            timeline.play();


        } catch (Exception e) {
            Controller.AlertError(e);
        }
    }
}
