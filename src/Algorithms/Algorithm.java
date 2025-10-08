package Algorithms;

import GraphVisualizer.Graph;

public abstract class Algorithm {

    protected Graph G;
    protected String AlgorithmName;
    protected String AlgorithmDescription;

    Algorithm() {};

    public abstract String getAlgorithmName();

    public abstract String getAlgorithmDescription();

    public abstract void Run();

    public abstract Boolean checkValidity();
}

