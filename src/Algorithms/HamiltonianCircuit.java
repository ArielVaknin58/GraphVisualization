package Algorithms;

import Controllers.ControllerManager;
import GraphVisualizer.Graph;

public class HamiltonianCircuit extends Algorithm{

    public HamiltonianCircuit(Graph G)
    {
        this.G = G;
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
        ControllerManager.getGraphInputController().PopupMessage("You Wish lol");
    }
}
