package Exceptions;

public class InvalidCapacityException extends Exception{

    @Override
    public String getMessage() {
        return "capacity cannot be negative, try again.";
    }
}
