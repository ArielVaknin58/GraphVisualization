package Services;

import GraphVisualizer.AppSettings;
import com.google.genai.Client;
import com.google.genai.types.GenerateContentResponse;
import com.google.genai.errors.ApiException; // Correct package is "exceptions"
import java.io.IOException; // This is required for network calls

import Controllers.Controller; // For your AlertError
import javafx.application.Platform;

public class GeminiService {

    private Client client;
    private final String apiKey;
    private boolean isReady = false;

    public GeminiService() {
        this.apiKey = System.getenv("GEMINI_API_KEY");

        if (this.apiKey == null || this.apiKey.isEmpty()) {
            System.err.println("FATAL ERROR: GEMINI_API_KEY environment variable not set.");
            return;
        }

        this.client = Client.builder().apiKey(this.apiKey).build();
        this.isReady = true;
    }

    public boolean isReady() {
        return this.isReady;
    }

    public String generateContent(String promptText) {
        if (!isReady()) {
            return "Error: GeminiService is not initialized. Please set the GEMINI_API_KEY.";
        }

        try {
            GenerateContentResponse response = client.models.generateContent(AppSettings.AI_model_used, promptText, null);
            return response.text();

        } catch (ApiException e) {
            Platform.runLater(() -> {
                Controller.AlertError(e);
            });
        }

        return "Error: Failed to get response.";
    }
}