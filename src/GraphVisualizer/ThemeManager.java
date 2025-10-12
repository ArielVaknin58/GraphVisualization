package GraphVisualizer;

import GraphVisualizer.LoggerManager;
import javafx.scene.Scene;
import java.util.ArrayList;
import java.util.List;

public class ThemeManager {

    private final static ThemeManager instance = new ThemeManager();
    private Theme currentTheme;
    private final List<Scene> scenes;

    public ThemeManager() {
        scenes = new ArrayList<>();
        currentTheme = Theme.DEFAULT;
    }

    public static ThemeManager getThemeManager()
    {
        return instance;
    }

    public void AddScene(Scene scene)
    {
        scenes.add(scene);
        applyTheme(scene);
    }

    public Theme getCurrentTheme()
    {
        return currentTheme;
    }

    private void applyTheme(Scene scene) {
        scene.getStylesheets().clear();
        String themePath = currentTheme.getCssPath();
        var url = getClass().getResource(themePath);
        if (url != null) {
            scene.getStylesheets().add(url.toExternalForm());
        } else {
            LoggerManager.Logger().warning("Could not find theme file: " + themePath);
        }
    }

    public void switchTheme(Theme theme) {
        currentTheme = theme;
        for(Scene scene : scenes)
        {
            applyTheme(scene);
        }

    }


}

