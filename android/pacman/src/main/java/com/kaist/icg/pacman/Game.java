package com.kaist.icg.pacman;

import android.os.SystemClock;

import com.kaist.icg.pacman.graphic.Camera;
import com.kaist.icg.pacman.graphic.Object3DFactory;
import com.kaist.icg.pacman.graphic.android.PacManGLSurfaceView;
import com.kaist.icg.pacman.graphic.pipe.Scene;
import com.kaist.icg.pacman.graphic.ui.TextElement;
import com.kaist.icg.pacman.manager.InputManager;
import com.kaist.icg.pacman.manager.LevelManager;
import com.kaist.icg.pacman.manager.ShaderManager;

/**
 * Main game class
 */
public class Game {

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

    //Test
    private TextElement fpsCounter;

    /**
     * Load assets etc...
     * @param mGLView
     */
    public  Game(PacManGLSurfaceView mGLView) {
        this.glView = mGLView;
        this.glView.setGame(this);

        inputManager = InputManager.getInstance();
        levelManager = LevelManager.getInstance();
        shaderManager = ShaderManager.getInstance();
    }

    public void init() {
        lightPosition = new float[] {0.0f, 0.0f, 0.0f};

        scene = new Scene();
        fpsCounter = Object3DFactory.getInstance().instanciate("plane.obj", TextElement.class);
        fpsCounter.setText("00 FPS");
        fpsCounter.setTextSize(30f);
        fpsCounter.setScreenPosition(Camera.getInstance().getScreenWidth() - fpsCounter.getBounds().width(),
                0);

        lastUpdate = SystemClock.uptimeMillis();
        shaderManager.initialize(Camera.getInstance().getProjMatrix(),
                Camera.getInstance().getViewMatrix(), lightPosition);
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
        nbFrameSinceLastFPSupdate++;

        //Compute time from last onUpdate
        elapsedTime = SystemClock.uptimeMillis() - lastUpdate;

        levelManager.update(elapsedTime);
        inputManager.update(elapsedTime);

        scene.onUpdate(elapsedTime);

        //FPS counter update
        if(SystemClock.uptimeMillis() - lastFPSupdate > 1000) {
            //Compute FPS: number_frame_drew / (elapsed_time / 1000)
            fpsCounter.setText(nbFrameSinceLastFPSupdate / ((SystemClock.uptimeMillis() -
                    lastFPSupdate) / 1000) + " FPS");

            nbFrameSinceLastFPSupdate = 0;
            lastFPSupdate = SystemClock.uptimeMillis();
        }
        lastUpdate = SystemClock.uptimeMillis();
    }

    /**
     * Draw all the scene
     */
    private void onRender() {
        scene.render();
        fpsCounter.draw();
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
