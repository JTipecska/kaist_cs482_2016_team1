package com.kaist.icg.pacman;

public abstract class View {
    protected boolean isInitialized;

    public abstract void init();

    public void loop() {
        if(!isInitialized)
            init();

        onRender();
        onUpdate();
    }
    public abstract void onUpdate();
    public abstract void onRender();
    public abstract void onPause();
    public abstract void onResume();
    public abstract void cleanup();

}
