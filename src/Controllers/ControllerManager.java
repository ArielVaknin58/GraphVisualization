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

    public static Controllers.graphPromptPopupController getGraphPromptPopupController() {
        return graphPromptPopupController;
    }

    public static void setGraphPromptPopupController(graphPromptPopupController graphPromptPopupController)
    {
        graphPromptPopupController = graphPromptPopupController;
    }

    public static NDPopupController getNdPopupController() {
        return ndPopupController;
    }

    public static void setNdPopupController(NDPopupController ndPopupController) {
        ControllerManager.ndPopupController = ndPopupController;
    }

    public static void setMaxFlowPopupController(MaxFlowPopupController maxFlowPopupController) {
        ControllerManager.maxFlowPopupController = maxFlowPopupController;
    }

    public static MaxFlowPopupController getMaxFlowPopupController() {
        return maxFlowPopupController;
    }

    public static GraphResultController getGraphResultController() {
        return graphResultController;
    }

    public static void setGraphResultController(GraphResultController graphResultController) {
        ControllerManager.graphResultController = graphResultController;
    }

    public static ResultsPaneController getResultsPaneController() {
        return resultsPaneController;
    }

    public static void setResultsPaneController(ResultsPaneController resultsPaneController) {
        ControllerManager.resultsPaneController = resultsPaneController;
    }

    public static SaveGraphPopupController getSaveGraphPopupController() {
        return saveGraphPopupController;
    }

    public static void setSaveGraphPopupController(SaveGraphPopupController saveGraphPopupController) {
        ControllerManager.saveGraphPopupController = saveGraphPopupController;
    }

    public static void setGraphInputController(GraphInputController graphInputController) {
        ControllerManager.graphInputController = graphInputController;
    }

    public static GraphInputController getGraphInputController() {
        return graphInputController;
    }

    public static void setGraphWiseAlgorithmsController(GraphWiseAlgorithmsController graphWiseAlgorithmsController) {
        ControllerManager.graphWiseAlgorithmsController = graphWiseAlgorithmsController;
    }

    public static GraphWiseAlgorithmsController getGraphWiseAlgorithmsController() {
        return graphWiseAlgorithmsController;
    }

}
