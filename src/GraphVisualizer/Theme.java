package GraphVisualizer;

public enum Theme {
    BLUE("/resources/styles/BlueTheme.css"),
    GREEN("/resources/styles/GreenTheme.css"),
    BROWN_AND_GOLD("/resources/styles/BrownAndGoldTheme.css"),
    MAGENTA("/resources/styles/MagentaTheme.css"),
    RED("/resources/styles/RedTheme.css"),
    ORANGE_AND_GREEN("/resources/styles/OrangeAndGreenTheme.css"),
    DEFAULT("Default");

    private final String cssPath;

    Theme(String cssPath) {
        this.cssPath = cssPath;
    }

    public String getCssPath() {
        return cssPath;
    }
}
