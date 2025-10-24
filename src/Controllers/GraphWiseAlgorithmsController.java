package Controllers;

import Algorithms.*;
import Exceptions.InvalidAlgorithmInputException;
import GraphVisualizer.AppSettings;
import GraphVisualizer.ThemeManager;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Tooltip;
import javafx.scene.image.Image;
import javafx.scene.layout.AnchorPane;
import GraphVisualizer.Graph;
import javafx.scene.control.Button;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.Objects;
import java.util.function.BiConsumer;


public class GraphWiseAlgorithmsController extends Controller{

    @FXML
    private VBox AlgorithmsPane;
    @FXML
    private Button TopologicalSortButton;
    @FXML
    private Button eulerCircuitButton;
    @FXML
    private Button hamiltonButton;
    @FXML
    private Button eulerPathButton;
    @FXML
    private Button connectivityButton;
    @FXML
    private Button KosarajuButton;
    @FXML
    private Button superGraphButton;
    @FXML
    private Button bipartiteButton;
    @FXML
    private Button FloydWarshallButton;
    @FXML
    private Button mstButton;
    @FXML
    private Button mincutButton;
    @FXML
    private Button isButton;
    @FXML
    private Button vertexCoverButton;
    @FXML
    private Button maxCutButton;
    @FXML
    private Button CliqueButton;


    public void initialize()
    {
        ControllerManager.setGraphWiseAlgorithmsController(this);
        TopologicalSortButton.setTooltip(new Tooltip(TopologicalSort.AlgorithmDescription));
        eulerCircuitButton.setTooltip(new Tooltip(EulerCircuit.AlgorithmDescription));
        hamiltonButton.setTooltip(new Tooltip(HamiltonianPath.AlgorithmDescription));
        eulerPathButton.setTooltip(new Tooltip(EulerPath.AlgorithmDescription));
        connectivityButton.setTooltip(new Tooltip(ConnectivityComponents.AlgorithmDescription));
        KosarajuButton.setTooltip((new Tooltip(KosarajuSharirAlgorithm.AlgorithmDescription)));
        superGraphButton.setTooltip(new Tooltip(SuperGraph.AlgorithmDescription));
        bipartiteButton.setTooltip(new Tooltip(BiPartite.AlgorithmDescription));
        FloydWarshallButton.setTooltip(new Tooltip(FloydWarshallAlgorithm.AlgorithmDescription));
        mincutButton.setTooltip(new Tooltip(MinCutAlgorithm.AlgorithmDescription));
        mstButton.setTooltip(new Tooltip(PrimAlgorithm.AlgorithmDescription));
        isButton.setTooltip(new Tooltip(IndependentSetAlgorithm.AlgorithmDescription));
        vertexCoverButton.setTooltip(new Tooltip(VertexCover.AlgorithmDescription));
        maxCutButton.setTooltip(new Tooltip(MaxCut.AlgorithmDescription));
        CliqueButton.setTooltip(new Tooltip(Clique.AlgorithmDescription));
    }

    private void run(Algorithm algorithm)
    {
        if (!algorithm.checkValidity())
        {
            AlertError(new InvalidAlgorithmInputException(algorithm));
        }
        else
        {
            algorithm.Run();
            algorithm.DisplayResults();
        }

    }
    public void OnTopologicalClick()
    {
        run(new TopologicalSort(new Graph(ControllerManager.getGraphInputController().getGraph())));
    }

    public void OnEulerCircuitClick()
    {
        run(new EulerCircuit(new Graph(ControllerManager.getGraphInputController().getGraph())));
    }

    public void onHamiltonianPathClicked()
    {
        run(new HamiltonianPath(new Graph(ControllerManager.getGraphInputController().getGraph())));
    }


    public void onEulerPathClicked()
    {
        run(new EulerPath(new Graph(ControllerManager.getGraphInputController().getGraph())));
    }

    public void OnConnectivityClicked()
    {
        run(new ConnectivityComponents(new Graph(ControllerManager.getGraphInputController().getGraph())));
    }

    public void OnKosarajuClick()
    {
        run(new KosarajuSharirAlgorithm(new Graph(ControllerManager.getGraphInputController().getGraph())));
    }

    public void OnSuperGraphClicked()
    {
        run(new SuperGraph(new Graph(ControllerManager.getGraphInputController().getGraph())));
    }

    public void OnBipartiteClicked()
    {
        run(new BiPartite(new Graph(ControllerManager.getGraphInputController().getGraph())));
    }

    public void OnFloydWarshallClicked()
    {
        run(new FloydWarshallAlgorithm(new Graph(ControllerManager.getGraphInputController().getGraph())));
    }

    public void onmstButtonClicked()
    {
        run(new PrimAlgorithm(new Graph(ControllerManager.getGraphInputController().getGraph())));
    }

    public void OnMinCutClicked()
    {
        run(new MinCutAlgorithm(new Graph(ControllerManager.getGraphInputController().getGraph())));
    }

    public void OnISClicked()
    {
        LoadNDController("size of independent set :",this::runIS);
    }

    public void runIS(int iterations,int k)
    {
        run(new IndependentSetAlgorithm(new Graph(ControllerManager.getGraphInputController().getGraph()),iterations,k));
    }

    public void OnVertexCoverClicked()
    {
        LoadNDController("size of vertex cover :",this::runVertexCover);
    }

    public void runVertexCover(int iterations,int k)
    {
        run(new VertexCover(new Graph(ControllerManager.getGraphInputController().getGraph()),iterations,k));
    }

    public void OnMaxCutClicked()
    {
        LoadNDController("size of cut :",this::runMaxCut);
    }

    public void runMaxCut(int iterations,int k)
    {
        run(new MaxCut(new Graph(ControllerManager.getGraphInputController().getGraph()),iterations,k));
    }

    public void OnClique()
    {
        LoadNDController("size of clique set :",this::runClique);
    }

    public void runClique(int iterations,int k)
    {
        run(new Clique(new Graph(ControllerManager.getGraphInputController().getGraph()),iterations,k));
    }
    private void LoadNDController(String kDesctiption, BiConsumer<Integer, Integer> methodToUse)
    {
        try
        {
                FXMLLoader NDLoader = new FXMLLoader(getClass().getResource(AppSettings.ND_Popup_Location));
                Pane NDPopupPane = NDLoader.load();
                NDPopupController controller = NDLoader.getController();
                ControllerManager.setNdPopupController(controller);
                controller.setMethod(methodToUse);

                Stage popupStage = new Stage();
                popupStage.setTitle("Non-Deterministic Decision");
                popupStage.initModality(Modality.APPLICATION_MODAL);
                javafx.scene.image.Image icon = new Image(Objects.requireNonNull(getClass().getResourceAsStream(AppSettings.App_Icon_location)));
                popupStage.getIcons().add(icon);
                controller.getKLabel().setText(kDesctiption);

                Scene popupScene = new Scene(NDPopupPane);
                popupStage.setScene(popupScene);
                ThemeManager.getThemeManager().AddScene(popupScene);
                popupStage.showAndWait();

        } catch (Exception e) {
            AlertError(e);
        }
    }
}

