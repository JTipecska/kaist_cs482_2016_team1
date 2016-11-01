package com.kaist.icg.pacman.view;

import android.os.SystemClock;

public abstract class View {
    protected boolean isInitialized;
    protected long lastUpdate;
    protected long elapsedTime;

    public abstract void init();

    public void loop() {
        if(!isInitialized)
            init();

        onRender();

        //Compute time from last onUpdate
        elapsedTime = SystemClock.uptimeMillis() - lastUpdate;
        onUpdate(elapsedTime);
        lastUpdate = SystemClock.uptimeMillis();
    }
    public abstract void onUpdate(long elapsedTime);
    public abstract void onRender();
    public abstract void onPause();
    public abstract void onResume();
    public abstract void cleanup();

}
