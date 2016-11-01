package com.kaist.icg.pacman;

import android.graphics.Color;
import android.os.SystemClock;

import com.kaist.icg.pacman.graphic.Camera;
import com.kaist.icg.pacman.graphic.Object3DFactory;
import com.kaist.icg.pacman.graphic.android.PacManActivity;
import com.kaist.icg.pacman.graphic.android.PacManGLSurfaceView;
import com.kaist.icg.pacman.graphic.ui.ImageElement;
import com.kaist.icg.pacman.graphic.ui.TextElement;
import com.kaist.icg.pacman.graphic.ui.UIElement;
import com.kaist.icg.pacman.manager.InputManager;
import com.kaist.icg.pacman.manager.ShaderManager;
import com.kaist.icg.pacman.tool.FloatAnimation;

public class MenuView extends View implements InputManager.ITouchListener{

    //FPS stuff
    private long lastUpdate;
    private long elapsedTime;
    private long lastFPSupdate;
    private int nbFrameSinceLastFPSupdate;

    private final ShaderManager shaderManager;
    private InputManager inputManager;
    private PacManGLSurfaceView glView;

    private float[] lightPosition;

    private ImageElement background1;
    private ImageElement background2;
    private ImageElement title;
    private TextElement fpsCounter;

    private TextElement btnNewGame;
    private TextElement btnHighscore;
    private TextElement btnQuit;

    private FloatAnimation backgroundAnimation;

    public MenuView(PacManGLSurfaceView mGLView) {
        this.glView = mGLView;
        this.glView.setView(this);

        lightPosition = new float[] {0.0f, 0.0f, 0.0f};

        inputManager = InputManager.getInstance();
        shaderManager = ShaderManager.getInstance();

        inputManager.setTouchListener(this);
    }

    @Override
    public void init() {
        System.out.println("Init: " + Camera.getInstance().getScreenWidth() + "x" + Camera.getInstance().getScreenHeight());

        background1 = Object3DFactory.getInstance().instanciate("ui.obj", ImageElement.class);
        background1.setTextureFile("menuBg.png");
        background1.setScreenSize(Camera.getInstance().getScreenWidth(), Camera.getInstance().getScreenHeight());
        background1.setScreenPosition(0, 0, UIElement.EAnchorPoint.TopLeft);

        background2 = Object3DFactory.getInstance().instanciate("ui.obj", ImageElement.class);
        background2.setTextureFile("menuBg.png");
        background2.setScreenSize(1080, 1794);
        background2.setScreenPosition(Camera.getInstance().getScreenWidth(), 0, UIElement.EAnchorPoint.TopLeft);

        backgroundAnimation = new FloatAnimation(0, Camera.getInstance().getScreenWidth(), 7000, true, false);

        fpsCounter = Object3DFactory.getInstance().instanciate("ui.obj", TextElement.class);
        fpsCounter.setBackgroundImage("button_yellow.png");
        fpsCounter.setBackgroundColor(Color.TRANSPARENT);
        fpsCounter.setForegroundColor(Color.BLACK);
        fpsCounter.setTextSize(20);
        fpsCounter.setScreenPosition(20, 20, UIElement.EAnchorPoint.TopRight);
        fpsCounter.setPadding(15, 30, 15, 30);
        fpsCounter.setZIndex(1);

        title = Object3DFactory.getInstance().instanciate("ui.obj", ImageElement.class);
        title.setTextureFile("title.png");
        title.setScreenSize(799, 206);
        title.setScreenPosition(0, -500, UIElement.EAnchorPoint.Center);
        title.setZIndex(1);

        btnNewGame = Object3DFactory.getInstance().instanciate("ui.obj", TextElement.class);
        btnNewGame.setBackgroundImage("button_yellow.png");
        btnNewGame.setBackgroundColor(Color.TRANSPARENT);
        btnNewGame.setForegroundColor(Color.BLACK);
        btnNewGame.setTextSize(60);
        btnNewGame.setText("New game");
        btnNewGame.setScreenPosition(0, -100, UIElement.EAnchorPoint.Center);
        btnNewGame.setPadding(30, 50, 40, 50);
        btnNewGame.setZIndex(1);

        btnHighscore = Object3DFactory.getInstance().instanciate("ui.obj", TextElement.class);
        btnHighscore.setBackgroundImage("button_yellow.png");
        btnHighscore.setBackgroundColor(Color.TRANSPARENT);
        btnHighscore.setForegroundColor(Color.BLACK);
        btnHighscore.setTextSize(60);
        btnHighscore.setText("High scores");
        btnHighscore.setScreenPosition(0, 150, UIElement.EAnchorPoint.Center);
        btnHighscore.setPadding(30, 50, 40, 50);
        btnHighscore.setZIndex(1);


        btnQuit = Object3DFactory.getInstance().instanciate("ui.obj", TextElement.class);
        btnQuit.setBackgroundImage("button_yellow.png");
        btnQuit.setBackgroundColor(Color.TRANSPARENT);
        btnQuit.setForegroundColor(Color.BLACK);
        btnQuit.setTextSize(60);
        btnQuit.setText("Quit");
        btnQuit.setScreenPosition(0, 400, UIElement.EAnchorPoint.Center);
        btnQuit.setPadding(30, 50, 40, 50);
        btnQuit.setZIndex(1);

        shaderManager.initialize(Camera.getInstance().getProjMatrix(),
                Camera.getInstance().getViewMatrix(), lightPosition);

        this.isInitialized = true;
    }

    @Override
    public void onUpdate() {
        nbFrameSinceLastFPSupdate++;

        //Compute time from last onUpdate
        elapsedTime = SystemClock.uptimeMillis() - lastUpdate;

        inputManager.update(elapsedTime);

        backgroundAnimation.update();
        background1.setScreenPosition((int) -backgroundAnimation.getValue(), 0, UIElement.EAnchorPoint.TopLeft);
        background2.setScreenPosition((int) (Camera.getInstance().getScreenWidth() - backgroundAnimation.getValue()),
                0, UIElement.EAnchorPoint.TopLeft);

        //FPS counter update
        if(SystemClock.uptimeMillis() - lastFPSupdate > 1000) {
            //Compute FPS: number_frame_drew / (elapsed_time / 1000)
            fpsCounter.setText((int) (nbFrameSinceLastFPSupdate / ((SystemClock.uptimeMillis() -
                    lastFPSupdate) / 1000)) + " FPS");

            nbFrameSinceLastFPSupdate = 0;
            lastFPSupdate = SystemClock.uptimeMillis();
        }
        lastUpdate = SystemClock.uptimeMillis();
    }

    @Override
    public void onRender() {
        background1.draw();
        background2.draw();
        fpsCounter.draw();
        title.draw();
        btnNewGame.draw();
        btnHighscore.draw();
        btnQuit.draw();
    }

    @Override
    public void onPause() {
        inputManager.onPause();
    }

    @Override
    public void onResume() {
        inputManager.onResume();
    }

    @Override
    public void cleanup() {
        background1.dispose();
        background2.dispose();
        fpsCounter.dispose();
        title.dispose();
        btnNewGame.dispose();
        btnHighscore.dispose();
        btnQuit.dispose();
    }

    @Override
    public void onTouchStart(float x, float y) {
        if(btnNewGame.getBounds().contains((int)x, (int)y)) {
            PacManActivity.current.startNewGame();
        }
        else if(btnHighscore.getBounds().contains((int)x, (int)y)) {
            //TODO: HighScoreView
        }
        else if(btnQuit.getBounds().contains((int)x, (int)y)) {
            PacManActivity.current.finish();
            System.exit(0);
        }
    }

    @Override
    public void onTouchEnd(float x, float y) {

    }
}
