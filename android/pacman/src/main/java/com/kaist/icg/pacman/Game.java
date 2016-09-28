package com.kaist.icg.pacman;

import android.opengl.GLES20;
import android.os.SystemClock;

import com.kaist.icg.pacman.graphic.Object3D;
import com.kaist.icg.pacman.graphic.android.PacManActivity;
import com.kaist.icg.pacman.graphic.android.PacManGLSurfaceView;
import com.kaist.icg.pacman.manager.InputManager;
import com.kaist.icg.pacman.manager.LevelManager;

/**
 * Main game class
 */
public class Game {

    private InputManager inputManager;
    private LevelManager levelManager;

    private long lastUpdate;
    private long elapsedTime;
    private PacManGLSurfaceView glView;

    //FPS stuff
    private long lastFPSupdate;
    private int nbFrameSinceLastFPSupdate;

    //Test 3D mesh
    private Object3D mesh;
    private long lastColorUpdate;
    private float toColor[] = { 1, 1, 1 };
    private float fromColor[] = { 1, 1, 1 };
    private float currentColor[] = { 1, 1, 1 };
    private float colorAnimPercent;
    private float colorAnimSpeed = 5000;

    /**
     * Load assets etc...
     * @param mGLView
     */
    public  Game(PacManGLSurfaceView mGLView) {
        this.glView = mGLView;
        this.glView.setGame(this);

        inputManager = InputManager.getInstance();
        levelManager = LevelManager.getInstance();

        lastColorUpdate = SystemClock.uptimeMillis();
    }

    public void init() {
        mesh = new Object3D("suzanne.obj");
    }

    /**
     * Called every frame
     */
    public void loop() {
        onRender();
        onUpdate();
    }

    /**
     * Update objects positions, player input....
     */
    private void onUpdate() {
        //Compute time from last onUpdate
        elapsedTime = SystemClock.uptimeMillis() - lastUpdate;

        levelManager.update(elapsedTime);
        inputManager.update(elapsedTime);

        nbFrameSinceLastFPSupdate++;
        if(SystemClock.uptimeMillis() - lastColorUpdate > colorAnimSpeed) {
            fromColor[0] = toColor[0];
            fromColor[1] = toColor[1];
            fromColor[2] = toColor[2];

            toColor[0] = (float) Math.random();
            toColor[1] = (float) Math.random();
            toColor[2] = (float) Math.random();
            lastColorUpdate = SystemClock.uptimeMillis();
        }
        else {
            colorAnimPercent = (float)(SystemClock.uptimeMillis() - lastColorUpdate) / colorAnimSpeed;
            currentColor[0] = fromColor[0] + (toColor[0] - fromColor[0]) * colorAnimPercent;
            currentColor[1] = fromColor[1] + (toColor[1] - fromColor[1]) * colorAnimPercent;
            currentColor[2] = fromColor[2] + (toColor[2] - fromColor[2]) * colorAnimPercent;
        }

        mesh.setColor(currentColor);

        if(SystemClock.uptimeMillis() - lastFPSupdate > 1000) {
            //Compute FPS: number_frame_drew / (elapsed_time / 1000)
            ((PacManActivity) glView.getContext()).setLogText(
                    nbFrameSinceLastFPSupdate / ((SystemClock.uptimeMillis() - lastFPSupdate) / 1000) + " fps");

            nbFrameSinceLastFPSupdate = 0;
            lastFPSupdate = SystemClock.uptimeMillis();
        }

        lastUpdate = SystemClock.uptimeMillis();
    }

    /**
     * Draw all the scene
     */
    private void onRender() {
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT | GLES20.GL_DEPTH_BUFFER_BIT);

        mesh.draw(glView.getRenderer().getProjMatrix(), glView.getRenderer().getViewMatrix());
    }

    public void onPause() {
        inputManager.onPause();
    }

    public void onResume() {
        inputManager.onResume();
    }

    /**
     * Clean memory
     */
    public void cleanup() {

    }
}
