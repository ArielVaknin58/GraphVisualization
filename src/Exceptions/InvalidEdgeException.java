package Exceptions;

public class InvalidEdgeException extends Exception{

    @Override
    public String getMessage() {
        return "one of the vertices doesn't exist";
    }


}
