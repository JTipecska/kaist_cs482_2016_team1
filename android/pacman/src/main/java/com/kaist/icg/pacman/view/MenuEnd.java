package com.kaist.icg.pacman.view;

import android.graphics.Color;

import com.kaist.icg.pacman.graphic.Camera;
import com.kaist.icg.pacman.graphic.Object3DFactory;
import com.kaist.icg.pacman.graphic.android.PacManActivity;
import com.kaist.icg.pacman.graphic.android.PacManGLSurfaceView;
import com.kaist.icg.pacman.graphic.ui.ImageElement;
import com.kaist.icg.pacman.graphic.ui.TextElement;
import com.kaist.icg.pacman.graphic.ui.UIElement;
import com.kaist.icg.pacman.graphic.ui.custom.FPSCounterElement;
import com.kaist.icg.pacman.manager.InputManager;
import com.kaist.icg.pacman.manager.ShaderManager;
import com.kaist.icg.pacman.tool.FloatAnimation;

public class MenuEnd extends View implements InputManager.ITouchListener{
    private final ShaderManager shaderManager;
    private InputManager inputManager;
    private PacManGLSurfaceView glView;

    private float[] lightPosition;

    private ImageElement background1;
    private ImageElement background2;
    private ImageElement title;
    private ImageElement gameOver;


    private TextElement btnHighscore;
    private TextElement btnQuit;
    private TextElement btnRestart;

    private FloatAnimation backgroundAnimation;
    private FloatAnimation fadeOutAnimation;
    private boolean fadeOut;
    private FloatAnimation fadeInAnimation;
    private boolean fadeIn;

    public MenuEnd(PacManGLSurfaceView mGLView) {
        this(mGLView, false);
    }

    public MenuEnd(PacManGLSurfaceView mGLView, boolean fadeIn) {
        this.glView = mGLView;
        this.glView.setView(this);

        lightPosition = new float[] {0.0f, 0.0f, 0.0f};

        inputManager = InputManager.getInstance();
        shaderManager = ShaderManager.getInstance();

        inputManager.setTouchListener(this);
        this.fadeIn = fadeIn;
    }

    @Override
    public void init() {
        background1 = Object3DFactory.getInstance().instanciate("objects/ui.obj", ImageElement.class);
        background1.setTextureFile("menuBg.png");
        background1.setScreenSize(Camera.getInstance().getScreenWidth(), Camera.getInstance().getScreenHeight());
        background1.setScreenPosition(0, 0, UIElement.EAnchorPoint.TopLeft);

        background2 = Object3DFactory.getInstance().instanciate("objects/ui.obj", ImageElement.class);
        background2.setTextureFile("menuBg.png");
        background2.setScreenSize(1080, 1794);
        background2.setScreenPosition(Camera.getInstance().getScreenWidth(), 0, UIElement.EAnchorPoint.TopLeft);



        backgroundAnimation = new FloatAnimation(0, Camera.getInstance().getScreenWidth(), 7000, true, false);


        title = Object3DFactory.getInstance().instanciate("objects/ui.obj", ImageElement.class);
        title.setTextureFile("title.png");
        title.setScreenSize(799, 206);
        title.setScreenPosition(0, -300, UIElement.EAnchorPoint.Center);
        title.setZIndex(1);

        gameOver = Object3DFactory.getInstance().instanciate("objects/ui.obj", ImageElement.class);
        gameOver.setTextureFile("GameOver.png");
        gameOver.setScreenSize(799, 206);
        gameOver.setScreenPosition(0, -600, UIElement.EAnchorPoint.Center);
        gameOver.setZIndex(1);

        btnHighscore = Object3DFactory.getInstance().instanciate("objects/ui.obj", TextElement.class);
        btnHighscore.setBackgroundImage("button_yellow.png");
        btnHighscore.setBackgroundColor(Color.TRANSPARENT);
        btnHighscore.setForegroundColor(Color.BLACK);
        btnHighscore.setTextSize(60);
        btnHighscore.setText("High scores");
        btnHighscore.setScreenPosition(0, 300, UIElement.EAnchorPoint.Center);
        btnHighscore.setPadding(30, 50, 40, 50);
        btnHighscore.setZIndex(1);

        btnQuit = Object3DFactory.getInstance().instanciate("objects/ui.obj", TextElement.class);
        btnQuit.setBackgroundImage("button_yellow.png");
        btnQuit.setBackgroundColor(Color.TRANSPARENT);
        btnQuit.setForegroundColor(Color.BLACK);
        btnQuit.setTextSize(60);
        btnQuit.setText("Quit");
        btnQuit.setScreenPosition(0, 550, UIElement.EAnchorPoint.Center);
        btnQuit.setPadding(30, 50, 40, 50);
        btnQuit.setZIndex(1);


        btnRestart = Object3DFactory.getInstance().instanciate("objects/ui.obj", TextElement.class);
        btnRestart.setBackgroundImage("button_yellow.png");
        btnRestart.setBackgroundColor(Color.TRANSPARENT);
        btnRestart.setForegroundColor(Color.BLACK);
        btnRestart.setTextSize(60);
        btnRestart.setText("Restart Game");
        btnRestart.setScreenPosition(0, 50, UIElement.EAnchorPoint.Center);
        btnRestart.setPadding(30, 50, 40, 50);
        btnRestart.setZIndex(1);


        shaderManager.initialize(Camera.getInstance().getProjMatrix(),
                Camera.getInstance().getViewMatrix(), lightPosition);

        this.isInitialized = true;
    }

    @Override
    public void onUpdate(long elapsedTime) {
        inputManager.update(elapsedTime);

        backgroundAnimation.update();
        background1.setScreenPosition((int) -backgroundAnimation.getValue(), 0, UIElement.EAnchorPoint.TopLeft);
        background2.setScreenPosition((int) (Camera.getInstance().getScreenWidth() - backgroundAnimation.getValue()),
                0, UIElement.EAnchorPoint.TopLeft);


        if(fadeOut) {
            fadeOutAnimation.update();
            btnRestart.setOpacity(fadeOutAnimation.getValue());
            btnHighscore.setOpacity(fadeOutAnimation.getValue());
            btnQuit.setOpacity(fadeOutAnimation.getValue());
        }
    }

    @Override
    public void onRender() {
        background1.draw();
        background2.draw();
        title.draw();
        gameOver.draw();
        btnHighscore.draw();
        btnQuit.draw();
        btnRestart.draw();
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
        title.dispose();
        gameOver.dispose();
        btnHighscore.dispose();
        btnQuit.dispose();
        btnRestart.dispose();
    }

    @Override
    public void onTouchStart(float x, float y) {
        if(btnRestart.getBounds().contains((int)x, (int)y)) {
            PacManActivity.current.startNewGame();
        }
        else if(btnHighscore.getBounds().contains((int)x, (int)y)) {
            fadeOutAnimation = new FloatAnimation(1, 0, 300, false, false);
            fadeOut = true;
            fadeOutAnimation.setAnimationStateListener(new FloatAnimation.IAnimationStateListener() {
                @Override
                public void onEnd() {
                    fadeOut = false;
                    PacManActivity.current.startHighScoreView();
                }
            });
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
