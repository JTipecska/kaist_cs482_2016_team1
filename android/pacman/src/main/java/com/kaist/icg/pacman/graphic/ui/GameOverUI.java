package com.kaist.icg.pacman.graphic.ui;


import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.text.InputType;
import android.widget.EditText;

import com.kaist.icg.pacman.client.AddScoreResponse;
import com.kaist.icg.pacman.client.Score;
import com.kaist.icg.pacman.client.ScoreService;
import com.kaist.icg.pacman.graphic.Camera;
import com.kaist.icg.pacman.graphic.Drawable;
import com.kaist.icg.pacman.graphic.Object3DFactory;
import com.kaist.icg.pacman.graphic.android.PacManActivity;
import com.kaist.icg.pacman.manager.InputManager;
import com.kaist.icg.pacman.manager.LevelManager;

import java.util.Collections;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class GameOverUI extends Drawable implements InputManager.ITouchListener {

    private ImageElement background;
    private TextElement title;
    private TextElement backToMenu;
    private TextElement yourScore;
    private TextElement score;
    private TextElement yourName;
    private String nameStr;

    public GameOverUI() {
        nameStr = "PacMan";
        InputManager.getInstance().setTouchListener(this);

        background = Object3DFactory.getInstance().instanciate("objects/ui.obj", ImageElement.class);
        background.setTextureFile("highScoreBg.png");
        background.setScreenSize(Camera.getInstance().getScreenWidth()/5*3, Camera.getInstance().getScreenHeight()/2);
        background.setScreenPosition(0, 120, UIElement.EAnchorPoint.Center);
        background.setZIndex(1);

        title = Object3DFactory.getInstance().instanciate("objects/ui.obj", TextElement.class);
        title.setBackgroundImage("button_yellow.png");
        title.setBackgroundColor(Color.TRANSPARENT);
        title.setForegroundColor(Color.RED);
        title.setTextSize(60);
        title.setText("Game Over !");
        title.setScreenPosition(0, -600, UIElement.EAnchorPoint.Center);
        title.setPadding(30, 50, 40, 50);
        title.setZIndex(1);

        yourScore = Object3DFactory.getInstance().instanciate("objects/ui.obj", TextElement.class);
        yourScore.setBackgroundColor(Color.TRANSPARENT);
        yourScore.setForegroundColor(Color.BLACK);
        yourScore.setTextSize(30);
        yourScore.setText("Your score:");
        yourScore.setScreenPosition(0, -200, UIElement.EAnchorPoint.Center);
        yourScore.setZIndex(2);

        score = Object3DFactory.getInstance().instanciate("objects/ui.obj", TextElement.class);
        score.setBackgroundColor(Color.TRANSPARENT);
        score.setForegroundColor(Color.BLACK);
        score.setTextSize(90);
        score.setText(LevelManager.getInstance().getScore() + "");
        score.setScreenPosition(0, -30, UIElement.EAnchorPoint.Center);
        score.setZIndex(2);

        backToMenu = Object3DFactory.getInstance().instanciate("objects/ui.obj", TextElement.class);
        backToMenu.setBackgroundImage("button_yellow.png");
        backToMenu.setBackgroundColor(Color.TRANSPARENT);
        backToMenu.setForegroundColor(Color.BLACK);
        backToMenu.setTextSize(30);
        backToMenu.setText("Back to main menu");
        backToMenu.setScreenPosition(0, 450, UIElement.EAnchorPoint.Center);
        backToMenu.setPadding(30, 50, 40, 50);
        backToMenu.setZIndex(2);

        if(isNetworkAvailable()) {
            Call<List<Score>> call = ScoreService.getService().getAllScores();

            call.enqueue(new Callback<List<Score>>() {
                @Override
                public void onResponse(Response<List<Score>> response) {
                    List<Score> scores = response.body();
                    Collections.reverse(scores);
                    for(int i = 0; i<scores.size(); i++) {
                        System.out.println("Compare with: " + scores.get(i).getScore());
                        if(LevelManager.getInstance().getScore() >= scores.get(i).getScore()) {
                            TextElement newHighScore = Object3DFactory.getInstance().instanciate("objects/ui.obj", TextElement.class);
                            newHighScore.setBackgroundColor(Color.TRANSPARENT);
                            newHighScore.setForegroundColor(Color.RED);
                            newHighScore.setTextSize(30);
                            newHighScore.setText("New high score !");
                            newHighScore.setScreenPosition(0, 130, UIElement.EAnchorPoint.Center);
                            newHighScore.setZIndex(2);

                            addChild(newHighScore);

                            TextElement name = Object3DFactory.getInstance().instanciate("objects/ui.obj", TextElement.class);
                            name.setBackgroundColor(Color.TRANSPARENT);
                            name.setForegroundColor(Color.BLACK);
                            name.setTextSize(25);
                            name.setText("Your name: (touch to edit)");
                            name.setScreenPosition(0, 180, UIElement.EAnchorPoint.Center);
                            name.setZIndex(2);

                            addChild(name);

                            yourName = Object3DFactory.getInstance().instanciate("objects/ui.obj", TextElement.class);
                            yourName.setBackgroundColor(Color.TRANSPARENT);
                            yourName.setForegroundColor(Color.BLACK);
                            yourName.setTextSize(40);
                            yourName.setText(nameStr);
                            yourName.setPadding(30, 50, 40, 50);
                            yourName.setScreenPosition(0, 280, UIElement.EAnchorPoint.Center);
                            yourName.setZIndex(2);

                            addChild(yourName);
                            i = scores.size();
                        }
                    }
                }

                @Override
                public void onFailure(Throwable t) {

                }
            });
        }

        this.addChild(background);
        this.addChild(title);
        this.addChild(yourScore);
        this.addChild(score);
        this.addChild(backToMenu);
    }

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) PacManActivity.current.getSystemService(PacManActivity.context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    @Override
    public void onTouchStart(float x, float y) {
        if(backToMenu.getBounds().contains((int)x, (int)y)) {
            if(isNetworkAvailable()) {
                Call<AddScoreResponse> call = ScoreService.getService().addNewScore(nameStr, LevelManager.getInstance().getScore());
                call.enqueue(new Callback<AddScoreResponse>() {
                    @Override
                    public void onResponse(Response<AddScoreResponse> response) {
                        System.out.println("Response from score server: " + response.body().isNew());
                        PacManActivity.current.startMenuView();
                    }

                    @Override
                    public void onFailure(Throwable t) {
                        System.out.println("Failed: " + t.getMessage());
                    }
                });
            }
            else
                PacManActivity.current.startMenuView();
        }
        else if(yourName != null && yourName.getBounds().contains((int)x, (int)y)) {
            AlertDialog.Builder builder = new AlertDialog.Builder(PacManActivity.current);
            builder.setTitle("Name:");

            final EditText input = new EditText(PacManActivity.current);
            input.setInputType(InputType.TYPE_CLASS_TEXT);
            builder.setView(input);

            builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    nameStr = input.getText().toString();
                    yourName.setText(nameStr);
                }
            });
            builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.cancel();
                }
            });

            builder.show();
        }
    }

    @Override
    public void onTouchEnd(float x, float y) {

    }
}
