package Controllers;

public class ControllerManager {

    private static GraphInputController graphInputController;

    private static GraphWiseAlgorithmsController graphWiseAlgorithmsController;

    private static SaveGraphPopupController saveGraphPopupController;

    private static ResultsPaneController resultsPaneController;

    private static GraphResultController graphResultController;

    private static MaxFlowPopupController maxFlowPopupController;

    private static NDPopupController ndPopupController;

    private static graphPromptPopupController graphPromptPopupController;

    private static ChatController chatController;

    public static ChatController getChatController() {
        return chatController;
    }

    public static APIModeController apiModeController;

    public static void setApiModeController(APIModeController apiModeController) {
        ControllerManager.apiModeController = apiModeController;
    }

    public static APIModeController getApiModeController() {
        if(apiModeController == null) {
            apiModeController = APIModeController.CreateInstance(false);
        }
        return apiModeController;
    }

    public static void setChatController(ChatController chatController) {
        ControllerManager.chatController = chatController;
    }

    public static Controllers.graphPromptPopupController getGraphPromptPopupController() {
        if(graphPromptPopupController == null) {
            graphPromptPopupController = new graphPromptPopupController();
        }
        return graphPromptPopupController;
    }

    public static void setGraphPromptPopupController(graphPromptPopupController GraphPromptPopupController)
    {
        graphPromptPopupController = GraphPromptPopupController;
    }

    public static NDPopupController getNdPopupController()
    {
        if(ndPopupController == null) {
            ndPopupController = new NDPopupController();
        }
        return ndPopupController;
    }

    public static void setNdPopupController(NDPopupController ndPopupController) {
        ControllerManager.ndPopupController = ndPopupController;
    }

    public static void setMaxFlowPopupController(MaxFlowPopupController maxFlowPopupController) {
        ControllerManager.maxFlowPopupController = maxFlowPopupController;
    }

    public static MaxFlowPopupController getMaxFlowPopupController()
    {
        if(maxFlowPopupController == null) {
            maxFlowPopupController = new MaxFlowPopupController();
        }
        return maxFlowPopupController;
    }

    public static GraphResultController getGraphResultController()
    {
        if(graphResultController == null) {
            graphResultController = new GraphResultController();
        }
        return graphResultController;
    }

    public static void setGraphResultController(GraphResultController graphResultController) {
        ControllerManager.graphResultController = graphResultController;
    }

    public static ResultsPaneController getResultsPaneController()
    {
        if(resultsPaneController == null) {
            resultsPaneController = new ResultsPaneController();
        }
        return resultsPaneController;
    }

    public static void setResultsPaneController(ResultsPaneController resultsPaneController) {
        ControllerManager.resultsPaneController = resultsPaneController;
    }

    public static SaveGraphPopupController getSaveGraphPopupController()
    {
        if(saveGraphPopupController == null) {
            saveGraphPopupController = new SaveGraphPopupController();
        }
        return saveGraphPopupController;
    }

    public static void setSaveGraphPopupController(SaveGraphPopupController saveGraphPopupController) {
        ControllerManager.saveGraphPopupController = saveGraphPopupController;
    }

    public static void setGraphInputController(GraphInputController graphInputController) {
        ControllerManager.graphInputController = graphInputController;
    }

    public static GraphInputController getGraphInputController()
    {
        if(graphInputController == null) {
            graphInputController = new GraphInputController();
        }
        return graphInputController;
    }

    public static void setGraphWiseAlgorithmsController(GraphWiseAlgorithmsController graphWiseAlgorithmsController) {
        ControllerManager.graphWiseAlgorithmsController = graphWiseAlgorithmsController;
    }

    public static GraphWiseAlgorithmsController getGraphWiseAlgorithmsController()
    {
        if(graphWiseAlgorithmsController == null) {
            graphWiseAlgorithmsController = new GraphWiseAlgorithmsController();
        }
        return graphWiseAlgorithmsController;
    }

}
