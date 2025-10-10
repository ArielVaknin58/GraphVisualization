package GraphVisualizer;

import GraphVisualizer.LoggerManager;
import javafx.scene.Scene;
import java.util.ArrayList;
import java.util.List;

public class ThemeManager {

    private final static ThemeManager instance = new ThemeManager();
    private Theme currentTheme;  // currently applied theme
    private final List<Scene> scenes;

    public ThemeManager() {
        scenes = new ArrayList<>();
        //default
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

    // Apply the current theme to a Scene
    private void applyTheme(Scene scene) {
        scene.getStylesheets().clear();
//        if(currentTheme.equals(Theme.DEFAULT))
//            return;
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

