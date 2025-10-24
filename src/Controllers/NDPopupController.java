package Controllers;


import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

import java.util.function.BiConsumer;

public class NDPopupController extends Controller{

    @FXML
    private AnchorPane NDPopupPane;
    @FXML
    private TextField iterationsField;
    @FXML
    private Label KLabel;
    @FXML
    private TextField KField;
    @FXML
    private Button EnterButton;

    private BiConsumer<Integer,Integer> method;



    @FXML
    public void initialize()
    {
        ControllerManager.setNdPopupController(this);
    }

    public Label getKLabel()
    {
        return KLabel;
    }

    public void setMethod(BiConsumer<Integer,Integer> method)
    {
        this.method = method;
    }
    @FXML
    private void OnEnterClicked()
    {
        try {
            String iterations = iterationsField.getText();
            String K = KField.getText();

            if (iterations.isEmpty() || K.isEmpty()) {
                AlertError(new Exception("Please enter content"));
                return;
            }

            int iteration = Integer.parseInt(iterations);
            int k = Integer.parseInt(K);
            if(iteration <= 0 || (k < 0 || k > ControllerManager.getGraphInputController().getGraph().V.size()))
            {
                AlertError(new Exception("Please enter a valid iteration/k values."));
                return;
            }

            Stage stage = (Stage) NDPopupPane.getScene().getWindow();
            stage.close();
            method.accept(iteration,k);

        }catch (NumberFormatException e) {
            AlertError(e);
        }


    }

}
