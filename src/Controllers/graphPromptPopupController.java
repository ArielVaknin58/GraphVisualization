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



    private void OnCreateClicked() {
        GeminiService gs = new GeminiService();
        LoadingPopup loadingPopup = new LoadingPopup();

        String text = promptBox.getText();
        String userPrompt = text.isEmpty() ? "Create a directed graph with 3 nodes and edges from 1 to 2 and 1 to 3" : text;
        String finalPrompt = gs.getFinalPromptText(userPrompt);

        loadingPopup.show();
        new Thread(() -> {
            String jsonResponse = gs.generateContent(finalPrompt);

            // 1. Check if the service itself returned an error message
            if (jsonResponse.startsWith("Error:")) {
                Platform.runLater(() -> {
                    loadingPopup.hide();
                    Controller.AlertError(new Exception(jsonResponse));
                });
                return;
            }

            System.out.println(jsonResponse);
            // 2. Extract JSON (Handles cases where AI adds conversational text)
            int firstBrace = jsonResponse.indexOf('{');
            int lastBrace = jsonResponse.lastIndexOf('}');

            if (firstBrace == -1 || lastBrace == -1 || lastBrace <= firstBrace) {
                Platform.runLater(() -> {
                    loadingPopup.hide();
                    Controller.AlertError(new Exception("The AI didn't provide a valid JSON object."));
                    System.out.println("Raw Response: " + jsonResponse);
                });
                return;
            }

            String cleanedJson = jsonResponse.substring(firstBrace, lastBrace + 1);

            try {
                ObjectMapper objectMapper = new ObjectMapper();
                GraphData graphData = objectMapper.readValue(cleanedJson, GraphData.class);

                Platform.runLater(() -> {
                    loadingPopup.hide();
                    GraphInputController.CreateGraphStatic(graphData);
                    SuccessPopup("Graph successfully created by AI!");
                    Stage stage = (Stage) graphPromptAnchorPane.getScene().getWindow();
                    stage.close();
                });

            } catch (Exception e) {
                e.printStackTrace();
                Platform.runLater(() -> {
                    loadingPopup.hide();
                    Controller.AlertError(new Exception("Failed to parse AI response. Check console for details."));
                    System.out.println("Attempted to parse: " + cleanedJson);
                });
            }
        }).start();
    }
}
