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
package helloopengles.example.com;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.ShortBuffer;

import android.opengl.GLES20;
import android.opengl.GLUtils;

/**
 * A two-dimensional square for use as a drawn object in OpenGL ES 2.0.
 */
public class Square {

    private final FloatBuffer vertexBuffer;
    private final FloatBuffer texCoordBuffer0;
    private final FloatBuffer texCoordBuffer1;
    private final ShortBuffer drawListBuffer;
    private final int mProgram;
    private int mVertexScaleHandle;
    private int mPositionHandle;
    private int mTexHandle0;
    private int mTexHandle1;
    private int mTexCoordHandle0;
    private int mTexCoordHandle1;
    private int mColorHandle;

    // number of coordinates per vertex in this array
    static final int COORDS_PER_VERTEX = 3;
    static float squareCoords[] = {
            -0.5f,  0.5f, 0.0f,   // top left
            -0.5f, -0.5f, 0.0f,   // bottom left
             0.5f, -0.5f, 0.0f,   // bottom right
             0.5f,  0.5f, 0.0f }; // top right

    static float texCoords0[] = {
            0.0f, 1.0f, 0.0f,
            0.0f, 0.0f, 0.0f,
            1.0f, 0.0f, 0.0f,
            1.0f, 1.0f, 0.0f
    };

    static float texCoords1[] = {
            0.0f, 1.0f, 0.0f,
            0.0f, 0.0f, 0.0f,
            1.0f, 0.0f, 0.0f,
            1.0f, 1.0f, 0.0f
    };

    private final short drawOrder[] = { 0, 1, 2, 0, 2, 3 }; // order to draw vertices

    private final int vertexStride = COORDS_PER_VERTEX * 4; // 4 bytes per vertex

    float color[] = { 0.2f, 0.709803922f, 0.898039216f, 1.0f };
    int a =0;
    /**
     * Sets up the drawing object data for use in an OpenGL ES context.
     */
    public Square() {
        // initialize vertex byte buffer for shape coordinates
        ByteBuffer bb = ByteBuffer.allocateDirect(
        // (# of coordinate values * 4 bytes per float)
                squareCoords.length * 4);
        bb.order(ByteOrder.nativeOrder());
        vertexBuffer = bb.asFloatBuffer();
        vertexBuffer.put(squareCoords);
        vertexBuffer.position(0);

        ByteBuffer bbTex0 = ByteBuffer.allocateDirect(
                texCoords0.length * 4);
        bbTex0.order(ByteOrder.nativeOrder());
        texCoordBuffer0 = bbTex0.asFloatBuffer();
        texCoordBuffer0.put(texCoords0);
        texCoordBuffer0.position(0);

        ByteBuffer bbTex1 = ByteBuffer.allocateDirect(
                texCoords1.length * 4);
        bbTex1.order(ByteOrder.nativeOrder());
        texCoordBuffer1 = bbTex1.asFloatBuffer();
        texCoordBuffer1.put(texCoords1);
        texCoordBuffer1.position(0);

        // initialize byte buffer for the draw list
        ByteBuffer dlb = ByteBuffer.allocateDirect(
                // (# of coordinate values * 2 bytes per short)
                drawOrder.length * 2);
        dlb.order(ByteOrder.nativeOrder());
        drawListBuffer = dlb.asShortBuffer();
        drawListBuffer.put(drawOrder);
        drawListBuffer.position(0);

        // prepare shaders and OpenGL program
        int vertexShader = MyGLRenderer.loadShaderFromFile(
                GLES20.GL_VERTEX_SHADER, "asst1-gl2.vshader");
        int fragmentShader = MyGLRenderer.loadShaderFromFile(
                GLES20.GL_FRAGMENT_SHADER, "asst1-gl2.fshader");

        mProgram = GLES20.glCreateProgram();             // create empty OpenGL Program
        GLES20.glAttachShader(mProgram, vertexShader);   // add the vertex shader to program
        GLES20.glAttachShader(mProgram, fragmentShader); // add the fragment shader to program
        GLES20.glLinkProgram(mProgram);                  // create OpenGL program executables


        // Texture
        int[] textureHandles = new int[2];
        GLES20.glGenTextures(2, textureHandles, 0);
        int texture0 = textureHandles[0];
        int texture1 = textureHandles[1];

        GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, texture0);
        GLUtils.texImage2D(GLES20.GL_TEXTURE_2D, 0, MyGLRenderer.loadImage("reachup.png"), 0);
        GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_LINEAR);

        GLES20.glActiveTexture(GLES20.GL_TEXTURE1);
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, texture1);
        GLUtils.texImage2D(GLES20.GL_TEXTURE_2D, 0, MyGLRenderer.loadImage("smiley.png"), 0);
        GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_LINEAR);

        mTexHandle0 = GLES20.glGetUniformLocation(mProgram, "uTexUnit0");
        mTexHandle1 = GLES20.glGetUniformLocation(mProgram, "uTexUnit1");
    }

    /**
     * Encapsulates the OpenGL ES instructions for drawing this shape.
     */
    public void draw(float scale) {
        // Add program to OpenGL environment
        GLES20.glUseProgram(mProgram);

        // get handle to vertex shader's vPosition member
        mPositionHandle = GLES20.glGetAttribLocation(mProgram, "aPosition");
        mTexCoordHandle0 = GLES20.glGetAttribLocation(mProgram, "aTexCoord0");
        mTexCoordHandle1 = GLES20.glGetAttribLocation(mProgram, "aTexCoord1");

        // Enable a handle to the triangle vertices
        GLES20.glEnableVertexAttribArray(mPositionHandle);
        GLES20.glEnableVertexAttribArray(mTexCoordHandle0);
        GLES20.glEnableVertexAttribArray(mTexCoordHandle1);

        // Prepare the triangle coordinate data
        GLES20.glVertexAttribPointer(
                mPositionHandle, COORDS_PER_VERTEX,
                GLES20.GL_FLOAT, false,
                vertexStride, vertexBuffer);

        GLES20.glVertexAttribPointer(
                mTexCoordHandle0, COORDS_PER_VERTEX,
                GLES20.GL_FLOAT, false,
                vertexStride, texCoordBuffer0);

        GLES20.glVertexAttribPointer(
                mTexCoordHandle1, COORDS_PER_VERTEX,
                GLES20.GL_FLOAT, false,
                vertexStride, texCoordBuffer1);

        // get handle to fragment shader's aColor member
        mColorHandle = GLES20.glGetAttribLocation(mProgram, "aColor");

        // Set color for drawing the triangle
        GLES20.glVertexAttrib4fv(mColorHandle, color, 0);


        // Set Textures
        GLES20.glUniform1i(mTexHandle0, 0);
        GLES20.glUniform1i(mTexHandle1, 1);

        // Scaling
        mVertexScaleHandle = GLES20.glGetUniformLocation(mProgram, "uVertexScale");
        GLES20.glUniform1f(mVertexScaleHandle, scale);

        // Draw the square
        GLES20.glDrawElements(
                GLES20.GL_TRIANGLES, drawOrder.length,
                GLES20.GL_UNSIGNED_SHORT, drawListBuffer);

        // Disable vertex array
        GLES20.glDisableVertexAttribArray(mPositionHandle);
        GLES20.glDisableVertexAttribArray(mTexCoordHandle0);
        GLES20.glDisableVertexAttribArray(mTexCoordHandle1);
    }

}