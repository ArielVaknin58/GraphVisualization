package Controllers;

public class ControllerManager {

    private static GraphInputController graphInputController;

    private static GraphWiseAlgorithmsController graphWiseAlgorithmsController;

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
