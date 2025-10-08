package Controllers;

public enum Theme {
    BLUE("/styles/BlueTheme.css"),
    GREEN("/styles/GreenTheme.css"),
    BROWNnGOLD("/styles/BrownAndGoldTheme.css"),
    MAGENTA("/styles/Magenta.css"),
    RED("/styles/RedTheme.css");

    private final String cssPath;

    Theme(String cssPath) {
        this.cssPath = cssPath;
    }

    public String getCssPath() {
        return cssPath;
    }
}
