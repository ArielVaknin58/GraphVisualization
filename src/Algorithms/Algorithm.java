package Algorithms;

import GraphVisualizer.Graph;

public abstract class Algorithm {

    protected Graph G;
    protected String AlgorithmName;
    protected String requiredInput;

    Algorithm() {};

    public String getAlgorithmName() {return this.AlgorithmName;}

    public String getRequiredInputDescription() {return this.requiredInput;}

    public abstract void Run();

    public abstract Boolean checkValidity();

    public abstract void DisplayResults();
}

