package GraphVisualizer;


import javafx.scene.paint.Color;

public class AppSettings {
    public static int nodeRadius = 25;
    public static int nodeLabelPadding = 5;
    public static int EdgeWidth = 4;
    public static int VERTICES_IN_LINE_IN_FILES = 5;
    public static final double ARROW_SIZE = 10;
    public static final int MAX_VERTICES = 30;
    public static final int MAX_WEIGHT = 1000000;
    public static final int CONTAINER_WIDTH = 2100; //1050
    public static final int CONTAINER_HEIGHT = 1600; //800
    public static final Color INITIAL_VERTEXCOLOR = Color.LIGHTBLUE;

    public static final String App_Title = "GraphVisualizer";
    public static final String SUCCESS_ICON_LOCATION = "/resources/GreenV.png";
    public static final String INFO_ICON_LOCATION = "/resources/exclamation.png";
    public static final String Graph_Input_Location = "/resources/GraphInput.fxml";
    public static final String Generic_PopupWindow_location = "/resources/GenericPopup.fxml";
    public static final String Algorithms_Pane_Location = "/resources/AlgorithmsPane.fxml";
    public static final String App_Icon_location = "/resources/GraphVisualizerIcon.png";
    public static final String save_Graph_Popup_location = "/resources/saveGraphPopup.fxml";
    public static final String Results_Popup_location = "/resources/results.fxml";
    public static final String DFS_style_css_location = "/resources/styles/DFSStyle.css";
    public static final String Graph_results_location = "/resources/GraphResultPane.fxml";
    public static final String Max_Flow_Popup_Location = "/resources/maxFlowPopup.fxml";
    public static final String ND_Popup_Location = "/resources/NDpopup.fxml";
    public static final String Graph_node_css_class = "graph-node";
    public static final String node_label_css_class = "node-label";

    // AI RELATED VALUES //
    public static final String AI_model_used = "gemini-2.5-pro";
    public static final int MAX_RETRIES = 3;
    public static final int MODEL_TIMEOUT_IN_SECONDS = 120;
    public static final int MODEL_MAX_CONTEXT = 13;
    public static final String graph_Prompt_Popup_Location = "/resources/graphPromptPopup.fxml";
    public static final String chat_window_location = "/resources/ChatWindow.fxml";

    // API MODE
    public static final String API_MODE_INVOKER = "--api";
    public static final int API_MODE_PORT = 7070;
    public static final String API_MODE_K_STRING = "setSize";
    public static final String API_MODE_ITERATIONS_STRING = "iterations";
    public static final String API_MODE_INPUT_NODE_STRING = "inputNode";
    public static final String API_MODE_S_STRING = "sourceNode";
    public static final String API_MODE_T_STRING = "destinationNode";


}
