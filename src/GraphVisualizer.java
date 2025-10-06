import javafx.application.Application;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

import java.util.ArrayList;

public class GraphVisualizer extends Application {

    @Override
    public void start(Stage primaryStage) {
        Group root = new Group();

        Graph G = new Graph();
        ArrayList<Node> nodesToAddToScene = new ArrayList<>();
        // Create labeled nodes
        G.createNode(100, 100, "A");
        G.createNode(300, 100, "B");
        G.createNode(200, 250, "C");

        nodesToAddToScene.add(G.FindVerticeNodeByLabel("A"));
        nodesToAddToScene.add(G.FindVerticeNodeByLabel("B"));
        nodesToAddToScene.add(G.FindVerticeNodeByLabel("C"));

        // Create edges using the circle centers
        G.createEdge("A","B");
        G.createEdge("B","C");
        G.createEdge("C","A");


        // Add everything to the scene graph (edges first, then nodes)
        G.AddGraphToGroup(root);


        Scene scene = new Scene(root, 400, 300, Color.WHITESMOKE);
        primaryStage.setTitle("Simple Graph Visualization");
        primaryStage.setScene(scene);
        primaryStage.show();
    }



    public static void main(String[] args) {
        launch(args);
    }
}
