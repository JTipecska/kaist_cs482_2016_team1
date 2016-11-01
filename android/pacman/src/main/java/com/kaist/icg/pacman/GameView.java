package com.kaist.icg.pacman;

import android.os.SystemClock;
import android.util.Log;

import com.kaist.icg.pacman.graphic.Camera;
import com.kaist.icg.pacman.graphic.android.PacManGLSurfaceView;
import com.kaist.icg.pacman.graphic.pipe.Scene;
import com.kaist.icg.pacman.graphic.ui.UI;
import com.kaist.icg.pacman.manager.InputManager;
import com.kaist.icg.pacman.manager.LevelManager;
import com.kaist.icg.pacman.manager.ShaderManager;

/**
 * Main game class
 */
public class GameView extends View{

    private InputManager inputManager;
    private LevelManager levelManager;
    private ShaderManager shaderManager;

    private long lastUpdate;
    private long elapsedTime;
    private PacManGLSurfaceView glView;

    //FPS stuff
    private long lastFPSupdate;
    private int nbFrameSinceLastFPSupdate;

    //Pipe
    private Scene scene;

    //Light
    private float[] lightPosition;

    //UI
    private UI ui;

    /**
     * Load assets etc...
     * @param mGLView
     */
    public GameView(PacManGLSurfaceView mGLView) {
        this.glView = mGLView;
        this.glView.setView(this);

        inputManager = InputManager.getInstance();
        levelManager = LevelManager.getInstance();
        shaderManager = ShaderManager.getInstance();
    }

    public void init() {
        Log.d("GameView", "Loading game with resolution: " +
                Camera.getInstance().getScreenWidth() + "x" +
                Camera.getInstance().getScreenHeight() + " px");
        lightPosition = new float[] {0.0f, 0.0f, 0.0f};

        scene = new Scene();
        ui = new UI();

        lastUpdate = SystemClock.uptimeMillis();
        shaderManager.initialize(Camera.getInstance().getProjMatrix(),
                Camera.getInstance().getViewMatrix(), lightPosition);

        this.isInitialized = true;
    }

    /**
     * Update objects positions, player input....
     */
    public void onUpdate() {
        nbFrameSinceLastFPSupdate++;

        //Compute time from last onUpdate
        elapsedTime = SystemClock.uptimeMillis() - lastUpdate;

        levelManager.update(elapsedTime);
        inputManager.update(elapsedTime);

        scene.onUpdate(elapsedTime);

        //FPS counter update
        if(SystemClock.uptimeMillis() - lastFPSupdate > 1000) {
            //Compute FPS: number_frame_drew / (elapsed_time / 1000)
            ui.updateFPScounter((int) (nbFrameSinceLastFPSupdate / ((SystemClock.uptimeMillis() -
                                lastFPSupdate) / 1000)));

            nbFrameSinceLastFPSupdate = 0;
            lastFPSupdate = SystemClock.uptimeMillis();
        }
        lastUpdate = SystemClock.uptimeMillis();
    }

    /**
     * Draw all the scene
     */
    public void onRender() {
        scene.render();
        ui.draw();
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
    @Override
    public void cleanup() {

    }
}
