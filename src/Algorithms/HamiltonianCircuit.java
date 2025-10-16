package Algorithms;

import Controllers.ControllerManager;
import GraphVisualizer.Graph;

public class HamiltonianCircuit extends Algorithm{

    public static final String AlgorithmDescription = "The algorithm gets a graph G=(V,E) and returns a path where each vertice appears exactly once.";

    public HamiltonianCircuit(Graph G)
    {
        this.G = G;
        AlgorithmName = "Hamiltonian Circuit Algorithm";
    }
    @Override
    public void Run() {

    }

    @Override
    public Boolean checkValidity() {
        return true;
    }

    @Override
    public void DisplayResults() {
        ControllerManager.getGraphInputController().PopupMessage("No polynomial time algorithm is currently known- open for (perhaps) future modifications.");
    }
}
