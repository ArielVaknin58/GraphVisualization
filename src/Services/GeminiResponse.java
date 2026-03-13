package Services;

import Algorithms.Algorithm;
import com.fasterxml.jackson.annotation.JsonUnwrapped;

public class GeminiResponse {
    public String type;
    public String message;

    // This is the magic part:
    @JsonUnwrapped // Tells Jackson that 'action' and 'parameters' are at the top level of this object
    public Algorithm algorithm;
}