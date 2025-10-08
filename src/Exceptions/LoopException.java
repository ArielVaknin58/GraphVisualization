package Exceptions;

public class LoopException extends Exception{

    @Override
    public String getMessage() {
        return "GraphVisualizer doesn't support loops, try a different edge.";
    }
}
