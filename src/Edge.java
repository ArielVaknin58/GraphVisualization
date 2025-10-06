import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;

//extends Line ?
public class Edge{

    private Line lineObject;

    public Edge(Line line)
    {
        lineObject = line;
    }

    public Line getLineObject()
    {
        return lineObject;
    }
}
