/*
 * Copyright (C) 2011 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.kaist.icg.pacman.graphic.android;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;

import com.kaist.icg.pacman.manager.InputManager;
import com.kaist.icg.pacman.manager.TextureManager;
import com.kaist.icg.pacman.view.GameView;
import com.kaist.icg.pacman.view.MenuView;
import com.kaist.icg.pacman.view.View;

/**
 * GameView activity.
 */
public class PacManActivity extends Activity {

    public static Context context;
    public static PacManActivity current;

    private PacManGLSurfaceView glView;
    private View oldView;
    private View currentView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //Remove status and title bar (fullscreen)
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);

        //Create view from res/layout/main.xml layout
        setContentView(R.layout.main);

        PacManActivity.context = getApplicationContext();
        PacManActivity.current = this;
        glView = (PacManGLSurfaceView) findViewById(R.id.main_glSurfaceView);

        currentView = new MenuView(glView);
    }

    @Override
    protected void onPause() {
        super.onPause();

        glView.onPause();
        currentView.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();

        glView.onResume();
        currentView.onResume();
    }

    public void startNewGame() {
        glView.getRenderer().setGlRunnable(new PacManGLRenderer.IGLRunnable() {
            @Override
            public void run() {

                cleanupCurrentView();
                currentView = new GameView(glView);
                currentView.init();
            }
        });
    }

    private void cleanupCurrentView() {
        InputManager.getInstance().setTouchListener(null);
        currentView.cleanup();
        TextureManager.getInstance().cleanup();
    }
}