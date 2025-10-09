package Controllers;

public enum Theme {
    BLUE("/styles/BlueTheme.css"),
    GREEN("/styles/GreenTheme.css"),
    BROWN_AND_GOLD("/styles/BrownAndGoldTheme.css"),
    MAGENTA("/styles/MagentaTheme.css"),
    RED("/styles/RedTheme.css"),
    DEFAULT("Default");

    private final String cssPath;

    Theme(String cssPath) {
        this.cssPath = cssPath;
    }

    public String getCssPath() {
        return cssPath;
    }
}
