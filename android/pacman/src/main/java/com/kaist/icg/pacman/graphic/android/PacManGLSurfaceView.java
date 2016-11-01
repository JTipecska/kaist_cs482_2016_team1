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

import android.content.Context;
import android.opengl.GLSurfaceView;
import android.util.AttributeSet;
import android.view.MotionEvent;

import com.kaist.icg.pacman.View;
import com.kaist.icg.pacman.manager.InputManager;

/**
 * A view container where OpenGL ES graphics can be drawn on screen.
 * This view can also be used to capture touch events, such as a user
 * interacting with drawn objects.
 */
public class PacManGLSurfaceView extends GLSurfaceView {

    private final PacManGLRenderer renderer;
    private View view;


    public PacManGLSurfaceView(Context context, AttributeSet attrs) {
        super(context, attrs);

        // Create an OpenGL ES 2.0 context.
        setEGLContextClientVersion(2);

        //Pass the GameView as argument so renderer can call GameView.loop on each frame
        //Android is ready to draw a new frame > renderer.onDrawFrame > GameView.loop
        renderer = new PacManGLRenderer();

        // Set the Renderer for drawing on the GLSurfaceView
        setRenderer(renderer);

        // Render the view only when there is a change in the drawing data
        // setRenderMode(GLSurfaceView.RENDERMODE_WHEN_DIRTY);
    }

    @Override
    public boolean onTouchEvent(MotionEvent e) {

        float x = e.getX();
        float y = e.getY();

        switch (e.getAction()) {
            case MotionEvent.ACTION_DOWN:
                InputManager.getInstance().onTouchStart(x, y);
                //requestRender();
                break;
            case MotionEvent.ACTION_UP:
                InputManager.getInstance().onTouchEnd(x, y);
                //requestRender();
                break;
        }

        return true;
    }

    public void setView(View view) {
        this.view = view;
        renderer.setView(view);
    }

    public PacManGLRenderer getRenderer() {
        return renderer;
    }
}
