package Controllers;

import Services.GeminiService;
import Services.GraphData;
import com.fasterxml.jackson.databind.ObjectMapper;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import resources.LoadingPopup;


public class graphPromptPopupController extends Controller{

    @FXML
    private AnchorPane graphPromptAnchorPane;
    @FXML
    private TextArea promptBox;
    @FXML
    private Button createButton;

    private void initialize()
    {
        ControllerManager.setGraphPromptPopupController(this);
        createButton.setTooltip(new Tooltip("send graph description to Gemini"));
    }

    @FXML
    private void OnCreateClicked()
    {
        GeminiService gs = GeminiService.getInstance();
        LoadingPopup loadingPopup = new LoadingPopup();

        String text = promptBox.getText();
        String userPrompt = text.isEmpty() ? "Create a directed graph with 3 nodes and edges from 1 to 2 and 1 to 3" : text;
        String finalPrompt = gs.getFinalPromptText(userPrompt);


        loadingPopup.show();
        new Thread(() -> {

            String jsonResponse = gs.generateContent(finalPrompt);

            int firstBrace = jsonResponse.indexOf('{');
            int lastBrace = jsonResponse.lastIndexOf('}');

            final String cleanedJson;

            if (firstBrace == -1 || lastBrace == -1 || lastBrace <= firstBrace) {
                // If we can't find a { or }, the response is bad.
                Platform.runLater(() -> {
                    Controller.AlertError(new Exception("The AI returned data I couldn't understand:\n"));
                    System.out.println(jsonResponse);
                });
                return;
            }

            cleanedJson = jsonResponse.substring(firstBrace, lastBrace + 1);
            final GraphData graphData;
            try {
                ObjectMapper objectMapper = new ObjectMapper();
                graphData = objectMapper.readValue(cleanedJson, GraphData.class);
            } catch (Exception e) {
                e.printStackTrace();
                Platform.runLater(() -> {
                    Controller.AlertError(new Exception("The AI returned data I couldn't understand:\n"));
                    System.out.println(cleanedJson);
                });
                return;
            }
            Platform.runLater(() -> {
                try {
                    loadingPopup.hide();
                    GraphInputController.CreateGraphStatic(graphData);
                    SuccessPopup("Graph successfully created by AI!");
                    Stage stage = (Stage) graphPromptAnchorPane.getScene().getWindow();
                    stage.close();
                } catch (Exception e) {
                    Controller.AlertError(e);
                }
            });

        }).start();
    }
}
