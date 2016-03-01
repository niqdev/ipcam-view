package com.github.niqdev.mjpeg;

public enum DisplayMode {

    STANDARD(1), BEST_FIT(4), FULLSCREEN(8);

    private final int value;

    DisplayMode(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }
}
