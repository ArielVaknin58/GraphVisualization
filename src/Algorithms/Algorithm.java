package Algorithms;

import GraphVisualizer.Graph;

public abstract class Algorithm {

    protected Graph G;
    protected String AlgorithmName;
    protected String AlgorithmDescription;
    protected String requiredInput;

    Algorithm() {};

    public abstract String getAlgorithmName();

    public abstract String getAlgorithmDescription();

    public abstract String getRequiredInputDescription();

    public abstract void Run();

    public abstract Boolean checkValidity();

    public abstract void DisplayResults();
}

