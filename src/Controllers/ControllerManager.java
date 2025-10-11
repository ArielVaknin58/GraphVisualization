package Controllers;

public class ControllerManager {

    private static GraphInputController graphInputController;

    private static GraphWiseAlgorithmsController graphWiseAlgorithmsController;

    private static VerticeWiseAlgorithmsController verticeWiseAlgorithmsController;

    private static SaveGraphPopupController saveGraphPopupController;

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

    public static VerticeWiseAlgorithmsController getVerticeWiseAlgorithmsController() {
        return verticeWiseAlgorithmsController;
    }

    public static void setVerticeWiseAlgorithmsController(VerticeWiseAlgorithmsController verticeWiseAlgorithmsController) {
        ControllerManager.verticeWiseAlgorithmsController = verticeWiseAlgorithmsController;
    }
}
