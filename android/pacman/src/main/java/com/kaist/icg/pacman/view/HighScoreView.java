package com.kaist.icg.pacman.view;

import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.SystemClock;

import com.kaist.icg.pacman.client.Score;
import com.kaist.icg.pacman.client.ScoreService;
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

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HighScoreView extends View implements InputManager.ITouchListener {
    private static final int MAX_SCORE = 5;
    private static final DateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy");

    private final ShaderManager shaderManager;
    private InputManager inputManager;
    private PacManGLSurfaceView glView;

    private float[] lightPosition;

    private ImageElement background1;
    private ImageElement background2;
    private ImageElement title;
    private ImageElement highScoreBg;
    private FPSCounterElement fpsCounter;
    private TextElement btnBack;
    private TextElement notConnected;

    private FloatAnimation backgroundAnimation;
    private FloatAnimation titleInAnimation;
    private boolean titleIn;
    private FloatAnimation titleOutAnimation;
    private boolean titleOut;

    private List<Score> scores;
    private CopyOnWriteArrayList<TextElement> scoresElements;

    private boolean isConnected;

    public HighScoreView(PacManGLSurfaceView mGLView) {
        this.glView = mGLView;
        this.glView.setView(this);
        this.scoresElements = new CopyOnWriteArrayList<>();
        this.isConnected = isNetworkAvailable();

        lightPosition = new float[] {0.0f, 0.0f, 0.0f};

        inputManager = InputManager.getInstance();
        shaderManager = ShaderManager.getInstance();

        inputManager.setTouchListener(this);

        if(isConnected) {
            try {
                Call<List<Score>> call = ScoreService.getService().getAllScores();

                call.enqueue(new Callback<List<Score>>() {
                    @Override
                    public void onResponse(Response<List<Score>> response) {
                        if (!titleIn) {
                            btnBack.setOpacity(1);
                            highScoreBg.setOpacity(1);
                            for (TextElement txt : scoresElements)
                                txt.setOpacity(1);
                        }

                        scores = response.body();
                        Collections.reverse(scores);

                        int from = -((1192 - 200) / 2) + 170 + 100;
                        int step = (1192 - 200) / scores.size();
                        for (int i = 0; i < scores.size(); i++) {
                            Date date = new Date(scores.get(i).getDate());
                            TextElement txt = Object3DFactory.getInstance().instanciate("ui.obj", TextElement.class);
                            txt.setBackgroundColor(Color.TRANSPARENT);
                            txt.setForegroundColor(Color.BLACK);
                            txt.setTextSize(30);
                            txt.setScreenPosition(0, from + step * i, UIElement.EAnchorPoint.Center);
                            txt.setText(scores.get(i).getName() + "     " +
                                    scores.get(i).getScore() + "      " +
                                    dateFormat.format(date));
                            txt.setOpacity(highScoreBg.getOpacity());
                            txt.setZIndex(2);

                            scoresElements.add(txt);
                        }
                    }

                    @Override
                    public void onFailure(Throwable t) {

                    }
                });
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void init() {
        background1 = Object3DFactory.getInstance().instanciate("ui.obj", ImageElement.class);
        background1.setTextureFile("menuBg.png");
        background1.setScreenSize(Camera.getInstance().getScreenWidth(), Camera.getInstance().getScreenHeight());
        background1.setScreenPosition(0, 0, UIElement.EAnchorPoint.TopLeft);

        background2 = Object3DFactory.getInstance().instanciate("ui.obj", ImageElement.class);
        background2.setTextureFile("menuBg.png");
        background2.setScreenSize(1080, 1794);
        background2.setScreenPosition(Camera.getInstance().getScreenWidth(), 0, UIElement.EAnchorPoint.TopLeft);

        highScoreBg = Object3DFactory.getInstance().instanciate("ui.obj", ImageElement.class);
        highScoreBg.setTextureFile("highScoreBg.png");
        highScoreBg.setScreenSize(956, 1192);
        highScoreBg.setScreenPosition(0, 170, UIElement.EAnchorPoint.Center);
        highScoreBg.setZIndex(1);
        highScoreBg.setOpacity(0);

        backgroundAnimation = new FloatAnimation(0, Camera.getInstance().getScreenWidth(), 7000, true, false);

        fpsCounter = Object3DFactory.getInstance().instanciate("ui.obj", FPSCounterElement.class);
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

        btnBack = Object3DFactory.getInstance().instanciate("ui.obj", TextElement.class);
        btnBack.setBackgroundImage("button_yellow.png");
        btnBack.setBackgroundColor(Color.TRANSPARENT);
        btnBack.setForegroundColor(Color.BLACK);
        btnBack.setTextSize(40);
        btnBack.setText("Back");
        btnBack.setScreenPosition(-5, 30, UIElement.EAnchorPoint.TopLeft);
        btnBack.setPadding(20, 40, 30, 40);
        btnBack.setZIndex(1);
        btnBack.setOpacity(0);

        notConnected = Object3DFactory.getInstance().instanciate("ui.obj", TextElement.class);
        notConnected.setBackgroundImage("button_yellow.png");
        notConnected.setBackgroundColor(Color.TRANSPARENT);
        notConnected.setForegroundColor(Color.BLACK);
        notConnected.setTextSize(30);
        notConnected.setText("Device not connected to internet");
        notConnected.setScreenPosition(0, 170, UIElement.EAnchorPoint.Center);
        notConnected.setPadding(40, 80, 60, 80);
        notConnected.setZIndex(1);
        notConnected.setOpacity(0);

        shaderManager.initialize(Camera.getInstance().getProjMatrix(),
                Camera.getInstance().getViewMatrix(), lightPosition);

        titleInAnimation = new FloatAnimation(-500, -650, 300, false, false);
        titleInAnimation.setAnimationStateListener(new FloatAnimation.IAnimationStateListener() {
            @Override
            public void onEnd() {
                btnBack.setOpacity(1);
                notConnected.setOpacity(1);
                if(scores != null) {
                    highScoreBg.setOpacity(1);
                    for(TextElement txt : scoresElements)
                        txt.setOpacity(1);
                }
                titleIn = false;
            }
        });
        titleIn = true;

        this.isInitialized = true;
    }

    @Override
    public void onUpdate(long elapsedTime) {
        //Compute time from last onUpdate
        elapsedTime = SystemClock.uptimeMillis() - lastUpdate;

        inputManager.update(elapsedTime);

        backgroundAnimation.update();
        background1.setScreenPosition((int) -backgroundAnimation.getValue(), 0, UIElement.EAnchorPoint.TopLeft);
        background2.setScreenPosition((int) (Camera.getInstance().getScreenWidth() - backgroundAnimation.getValue()),
                0, UIElement.EAnchorPoint.TopLeft);

        fpsCounter.update(elapsedTime);

        if(titleIn)
            title.setScreenPosition(0, (int) titleInAnimation.update(), UIElement.EAnchorPoint.Center);

        if(titleOut)
            title.setScreenPosition(0, (int) titleOutAnimation.update(), UIElement.EAnchorPoint.Center);
    }

    @Override
    public void onRender() {
        background1.draw();
        background2.draw();
        fpsCounter.draw();
        title.draw();
        btnBack.draw();

        if(isConnected) {
            highScoreBg.draw();
            for (TextElement element : scoresElements)
                element.draw();
        }
        else
            notConnected.draw();
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
        btnBack.dispose();
        highScoreBg.dispose();
        for(TextElement element : scoresElements)
            element.dispose();
    }

    @Override
    public void onTouchStart(float x, float y) {
        if(btnBack.getBounds().contains((int)x, (int)y)) {
            btnBack.setOpacity(0);
            highScoreBg.setOpacity(0);
            for(TextElement txt : scoresElements)
                txt.setOpacity(0);

            titleOutAnimation = new FloatAnimation(-650, -500, 300, false, false);
            titleOut = true;
            titleOutAnimation.setAnimationStateListener(new FloatAnimation.IAnimationStateListener() {
                @Override
                public void onEnd() {
                    titleOut = false;
                    PacManActivity.current.startMenuView();
                }
            });
        }
    }

    @Override
    public void onTouchEnd(float x, float y) {

    }

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) PacManActivity.current.getSystemService(PacManActivity.context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }
}
