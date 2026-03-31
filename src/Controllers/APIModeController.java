package Controllers;

import GraphVisualizer.AppSettings;

public class APIModeController extends Controller{

    private boolean APIMode = false;
    private static APIModeController instance = null;
    private int port = AppSettings.API_MODE_PORT;

    private APIModeController(boolean apimode)
    {
        APIMode = apimode;
    }

    public APIModeController getInstance()
    {
        return instance;
    }

    public int getPort()
    {
        return port;
    }

    public static APIModeController CreateInstance(boolean apimode)
    {
        if(instance == null)
        {
            instance = new APIModeController(apimode);
        }

        return instance;
    }

    public boolean isAPIMode() {
        return APIMode;
    }

    public void setAPIMode(boolean APIMode) {
        this.APIMode = APIMode;
    }
}
