package Exceptions;

import Algorithms.Algorithm;

public class InvalidAlgorithmInputException extends Exception {

    private String specs;

    public InvalidAlgorithmInputException(Algorithm algorithm)
    {
        specs = algorithm.getRequiredInputDescription();
    }
    public String getMessage() {
        return "The input isn't matching the algorithm's requirements.\n Reqired input : "+specs;
    }
}
