package com.kaist.icg.pacman.view;

import android.opengl.GLES20;
import android.os.SystemClock;
import android.util.Log;

import com.kaist.icg.pacman.graphic.Camera;
import com.kaist.icg.pacman.graphic.Drawable;
import com.kaist.icg.pacman.graphic.Object3DFactory;
import com.kaist.icg.pacman.graphic.android.PacManGLRenderer;
import com.kaist.icg.pacman.graphic.android.PacManGLSurfaceView;
import com.kaist.icg.pacman.graphic.pipe.Scene;
import com.kaist.icg.pacman.graphic.ui.GameOverUI;
import com.kaist.icg.pacman.graphic.ui.GameUI;
import com.kaist.icg.pacman.graphic.ui.ImageElement;
import com.kaist.icg.pacman.graphic.ui.UIElement;
import com.kaist.icg.pacman.manager.InputManager;
import com.kaist.icg.pacman.manager.LevelManager;
import com.kaist.icg.pacman.manager.ShaderManager;

import java.lang.reflect.Array;
import java.util.ArrayList;

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

    //Light
    private float[] lightPosition;

    //GameUI
    private GameUI gameUi;

    private boolean gameOver;
    private GameOverUI gameOverUI;

    /**
     * Load assets etc...
     * @param mGLView
     */
    public GameView(PacManGLSurfaceView mGLView) {
        this.glView = mGLView;
        this.glView.setView(this);
        this.gameOver = false;

        inputManager = InputManager.getInstance();
        levelManager = LevelManager.getInstance();
        shaderManager = ShaderManager.getInstance();

        levelManager.setCurrentGameView(this);
    }

    public void init() {
        Log.d("GameView", "Loading game with resolution: " +
                Camera.getInstance().getScreenWidth() + "x" +
                Camera.getInstance().getScreenHeight() + " px");
        lightPosition = new float[] {0.0f, 0.0f, 0.0f};

        scene = new Scene();
        gameUi = new GameUI();
        levelManager.init();

        lastUpdate = SystemClock.uptimeMillis();
        shaderManager.initialize(Camera.getInstance().getProjMatrix(),
                Camera.getInstance().getViewMatrix(), lightPosition);

        this.isInitialized = true;
    }

    /**
     * Update objects positions, player input....
     */
    public void onUpdate(long elapsedTime) {
        if(!gameOver) {
            levelManager.update(elapsedTime);
            inputManager.update(elapsedTime);

            gameUi.updateScore(levelManager.getScore());
            gameUi.updateLives(levelManager.getLife());
            scene.onUpdate(elapsedTime);
        }
        gameUi.update(elapsedTime);
    }

    /**
     * Draw all the scene
     */
    public void onRender() {
        scene.render();

        levelManager.onRender();

        if(gameOver)
            gameOverUI.draw();
        else
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
        gameUi.dispose();
        scene.cleanup();
        gameOverUI.dispose();
    }

    public void gameOver() {
        GameView.this.gameOverUI = new GameOverUI();
        gameOver = true;
    }
}
