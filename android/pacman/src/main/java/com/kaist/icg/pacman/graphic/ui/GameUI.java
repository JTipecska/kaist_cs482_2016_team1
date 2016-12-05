package com.kaist.icg.pacman.graphic.ui;

import android.graphics.Color;

import com.kaist.icg.pacman.graphic.Drawable;
import com.kaist.icg.pacman.graphic.Object3DFactory;
import com.kaist.icg.pacman.graphic.ui.custom.FPSCounterElement;
import com.kaist.icg.pacman.manager.LevelManager;

public class GameUI extends Drawable{

    private FPSCounterElement fpsCounter;
    private ImageElement statusBackground;
    private TextElement lifeText;
    private TextElement pointText;

    public GameUI() {
        fpsCounter = Object3DFactory.getInstance().instanciate("objects/ui.obj", FPSCounterElement.class);

        statusBackground = Object3DFactory.getInstance().instanciate("objects/Ui2.obj", ImageElement.class);
        statusBackground.setTextureFile("status.png");
        statusBackground.setScreenSize(450, 200);
        statusBackground.setScreenPosition(0, 0, UIElement.EAnchorPoint.TopLeft);

        lifeText = Object3DFactory.getInstance().instanciate("objects/ui.obj", TextElement.class);
        lifeText.setBackgroundColor(Color.TRANSPARENT);
        lifeText.setForegroundColor(Color.BLACK);
        lifeText.setTextSize(40);
        lifeText.setScreenPosition(20, 20, UIElement.EAnchorPoint.TopLeft);
        lifeText.setText("Lives: 3");
        lifeText.setZIndex(1);

        pointText = Object3DFactory.getInstance().instanciate("objects/ui.obj", TextElement.class);
        pointText.setBackgroundColor(Color.TRANSPARENT);
        pointText.setForegroundColor(Color.BLACK);
        pointText.setTextSize(40);
        pointText.setScreenPosition(400, 20, UIElement.EAnchorPoint.TopLeft);
        pointText.setText("Points: 0");
        pointText.setZIndex(1);

        this.addChild(fpsCounter);
        this.addChild(statusBackground);
        this.addChild(lifeText);
        this.addChild(pointText);
    }

    public void update(long elapsedTime) {
        fpsCounter.update(elapsedTime);
    }

    public void updateScore(int score) { pointText.setText("Points: " + score);}

    public void updateLives (int life) {lifeText.setText("Lives: " + life);}
}
