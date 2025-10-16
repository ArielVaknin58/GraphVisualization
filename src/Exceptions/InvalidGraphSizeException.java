package Exceptions;

public class InvalidGraphSizeException extends Exception{

    @Override
    public String getMessage() {
        return "GraphVisualizer currently doesn't support more than 15 vertices, try a different number.";
    }
}
