package GraphVisualizer;

import Controllers.Controller;
import Controllers.ControllerManager;
import Exceptions.InvalidEdgeException;
import javax.swing.*;
import java.io.*;
import java.nio.file.*;
import java.util.ArrayList;

public class GraphSerializer {

    public static void saveGraph(Graph g, String fileName) {
        try {
            // 1️⃣ Determine the serialization folder relative to the working directory
            Path serializationPath = Paths.get(System.getProperty("user.dir"), "Serializations");

            Files.createDirectories(serializationPath);

            Path graphFile = serializationPath.resolve(fileName + ".ser");

            try (ObjectOutputStream out = new ObjectOutputStream(Files.newOutputStream(graphFile))) {
                out.writeObject(g);
                out.flush();
            }

            ControllerManager.getGraphWiseAlgorithmsController().PopupMessage("Graph "+fileName+".ser saved successfully !");

        } catch (IOException e) {
            Controller.AlertError(e);
        }
    }

    public static Graph loadGraph() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Select a saved graph file");
        fileChooser.setFileFilter(new javax.swing.filechooser.FileNameExtensionFilter("Serialized Graph Files (*.ser)", "ser"));

        int userSelection = fileChooser.showOpenDialog(null);
        if (userSelection == JFileChooser.APPROVE_OPTION) {
            File file = fileChooser.getSelectedFile();

            try (ObjectInputStream in = new ObjectInputStream(new FileInputStream(file))) {
                Object obj = in.readObject();
                if (obj instanceof Graph) {
                    Graph g = (Graph) obj;

                    int numVertices = g.V.size();
                    for (int i = 1; i <= numVertices; i++) {
                        g.createNode(Integer.toString(i));
                    }
                    ArrayList<ArrowEdge> list = new ArrayList<>(g.E);
                    for (ArrowEdge edge : list) {
                        g.createEdge(edge.getFrom().getNodeLabel(), edge.getTo().getNodeLabel());
                    }

                    return g;
                } else {
                    LoggerManager.Logger().severe("The selected file does not contain a valid Graph object.");
                }
            } catch (IOException | ClassNotFoundException e) {
                LoggerManager.Logger().severe("Error loading graph: " + e.getMessage());
            } catch (InvalidEdgeException e) {
                Controller.AlertError(e);
            }
        } else {
            LoggerManager.Logger().severe("File selection canceled by user.");
        }

        return null;
    }
}
