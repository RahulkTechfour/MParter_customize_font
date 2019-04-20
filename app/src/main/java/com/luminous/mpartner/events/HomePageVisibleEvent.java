package com.luminous.mpartner.events;

public class HomePageVisibleEvent {

    public HomePageVisibleEvent(boolean isVisible) {
        this.isVisible = isVisible;
    }

    boolean isVisible;

    public boolean isVisible() {
        return isVisible;
    }
}
