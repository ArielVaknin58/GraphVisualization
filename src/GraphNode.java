import javafx.scene.Group;
import javafx.scene.shape.Circle;
import javafx.scene.text.Text;

//extends Group/circle ?
public class GraphNode{

    private Group nodeObject;
    private String nodeLabel;

    public GraphNode(Circle node, Text text)
    {
        super();
        nodeLabel = text.toString();
        nodeObject = new Group(node,text);
    }

    public Group getNodeObject()
    {
        return nodeObject;
    }

    public String getNodeLabel()
    {
        return nodeLabel;
    }

    public Circle getNodeCircle()
    {
        return (Circle) nodeObject.getChildren().getFirst();
    }
}
