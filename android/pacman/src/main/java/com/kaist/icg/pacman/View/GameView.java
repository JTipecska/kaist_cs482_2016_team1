package com.kaist.icg.pacman.view;

import android.opengl.GLES20;
import android.os.SystemClock;
import android.util.Log;

import com.kaist.icg.pacman.graphic.Camera;
import com.kaist.icg.pacman.graphic.android.PacManGLSurfaceView;
import com.kaist.icg.pacman.graphic.pipe.Scene;
import com.kaist.icg.pacman.graphic.ui.GameUI;
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

    private PacManGLSurfaceView glView;

    //Pipe
    private Scene scene;

    //Ghost single Object
    //private Object3D ghost;

    //Light
    private float[] lightPosition;

    //GameUI
    private GameUI gameUi;


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
        gameUi = new GameUI();

        lastUpdate = SystemClock.uptimeMillis();
        shaderManager.initialize(Camera.getInstance().getProjMatrix(),
                Camera.getInstance().getViewMatrix(), lightPosition);

        this.isInitialized = true;
    }

    /**
     * Update objects positions, player input....
     */
    public void onUpdate(long elapsedTime) {

        levelManager.update(elapsedTime);
        inputManager.update(elapsedTime);

        gameUi.update(elapsedTime);
        gameUi.updateScore(levelManager.getScore());
        scene.onUpdate(elapsedTime);
    }

    /**
     * Draw all the scene
     */
    public void onRender() {
        GLES20.glClearColor(0.2f, 0.2f, 0.2f, 1.0f);
        scene.render();
        gameUi.draw();

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
