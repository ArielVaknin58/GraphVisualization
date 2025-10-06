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
        // Create labeled nodes
        G.createNode(100, 100, "A");
        G.createNode(300, 100, "B");
        G.createNode(200, 250, "C");
        G.createNode(400,350,"D");

        // Create edges using the circle centers
        G.createEdge("A","B");
        G.createEdge("B","C");
        G.createEdge("C","A");
        G.createEdge("D","A");
        G.createEdge("D","B");
        G.createEdge("D","C");

        // Add everything to the scene graph (edges first, then nodes)
        G.AddGraphToGroup(root);

        Scene scene = new Scene(root, 400, 300, Color.WHITESMOKE);
        primaryStage.setTitle("Graph Visualization");
        primaryStage.setScene(scene);
        primaryStage.show();
    }



    public static void main(String[] args) {
        launch(args);
    }
}
