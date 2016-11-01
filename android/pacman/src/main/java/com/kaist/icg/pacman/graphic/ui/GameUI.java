package com.kaist.icg.pacman.graphic.ui;

import android.graphics.Color;

import com.kaist.icg.pacman.graphic.Drawable;
import com.kaist.icg.pacman.graphic.Object3DFactory;
import com.kaist.icg.pacman.graphic.ui.custom.FPSCounterElement;

public class GameUI extends Drawable{

    private FPSCounterElement fpsCounter;
    private ImageElement statusBackground;
    private TextElement levelText;
    private TextElement pointText;

    public GameUI() {
        fpsCounter = Object3DFactory.getInstance().instanciate("ui.obj", FPSCounterElement.class);

        statusBackground = Object3DFactory.getInstance().instanciate("ui.obj", ImageElement.class);
        statusBackground.setTextureFile("status.png");
        statusBackground.setScreenSize(450, 200);
        statusBackground.setScreenPosition(0, 0, UIElement.EAnchorPoint.TopLeft);

        levelText = Object3DFactory.getInstance().instanciate("ui.obj", TextElement.class);
        levelText.setBackgroundColor(Color.TRANSPARENT);
        levelText.setForegroundColor(Color.BLACK);
        levelText.setTextSize(40);
        levelText.setScreenPosition(20, 20, UIElement.EAnchorPoint.TopLeft);
        levelText.setText("Level: 1");
        levelText.setZIndex(1);

        pointText = Object3DFactory.getInstance().instanciate("ui.obj", TextElement.class);
        pointText.setBackgroundColor(Color.TRANSPARENT);
        pointText.setForegroundColor(Color.BLACK);
        pointText.setTextSize(40);
        pointText.setScreenPosition(20, 90, UIElement.EAnchorPoint.TopLeft);
        pointText.setText("Points: 42");
        pointText.setZIndex(1);

        this.addChild(fpsCounter);
        this.addChild(statusBackground);
        this.addChild(levelText);
        this.addChild(pointText);
    }

    public void update(long elapsedTime) {
        fpsCounter.update(elapsedTime);
    }
}
