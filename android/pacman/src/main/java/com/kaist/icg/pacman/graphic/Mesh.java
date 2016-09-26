package com.kaist.icg.pacman.graphic;

import android.opengl.GLES20;
import android.opengl.Matrix;
import android.os.SystemClock;

import com.kaist.icg.pacman.graphic.android.PacManActivity;
import com.kaist.icg.pacman.graphic.android.PacManGLRenderer;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.util.ArrayList;
import java.util.Arrays;

public class Mesh extends Drawable {
    private ArrayList<float[]> verticesDictionary;
    private ArrayList<float[]> normalsDictionary;
    private ArrayList<Face> facesDictionary;

    private final int mProgram;
    private FloatBuffer mVertexBuffer;
    private FloatBuffer mNormalBuffer;

    // attribute handles
    private int mPositionHandle;
    private int mNormalHandle;

    // uniform handles
    private int mProjMatrixHandle;
    private int mModelViewMatrixHandle;
    private int mNormalMatrixHandle;

    private int mLightHandle;
    private int mLight2Handle;
    private int mColorHandle;

    private float[] mModelViewMatrix = new float[16];
    private float[] mModelMatrix = new float[16];
    private float[] mNormalMatrix = new float[16];
    private float[] mLight = new float[3];
    private float[] mLight2 = new float[3];

    private static final int COORDS_PER_VERTEX = 3;
    private static final int VERTEX_STRIDE = COORDS_PER_VERTEX * 4;
    private float color[] = { 0.2f, 0.709803922f, 0.898039216f };

    public Mesh(String file) {
        verticesDictionary = new ArrayList<>();
        normalsDictionary = new ArrayList<>();
        facesDictionary = new ArrayList<>();

        loadFile(file);
        buildBuffers();

        // prepare shaders and OpenGL program
        int vertexShader = PacManGLRenderer.loadShaderFromFile(
                GLES20.GL_VERTEX_SHADER, "basic-gl2.vshader");
        int fragmentShader = PacManGLRenderer.loadShaderFromFile(
                GLES20.GL_FRAGMENT_SHADER, "diffuse-gl2.fshader");

        mProgram = GLES20.glCreateProgram();             // create empty OpenGL Program
        GLES20.glAttachShader(mProgram, vertexShader);   // add the vertex shader to program
        GLES20.glAttachShader(mProgram, fragmentShader); // add the fragment shader to program
        GLES20.glLinkProgram(mProgram);                  // create OpenGL program executables

        mLight = new float[] {2.0f, 3.0f, 14.0f};
        mLight2 = new float[] {-2.0f, -3.0f, -5.0f};
    }

    private void buildBuffers() {
        ByteBuffer byteBuf = ByteBuffer.allocateDirect(facesDictionary.size() * 3 * 3 * 4);
        byteBuf.order(ByteOrder.nativeOrder());
        mVertexBuffer = byteBuf.asFloatBuffer();

        for(Face face : facesDictionary) {
            for(int i = 0; i<face.getVerticesIndex().length; i++) {
                mVertexBuffer.put(verticesDictionary.get(face.getVerticesIndex()[i]));
            }
        }
        mVertexBuffer.position(0);

        byteBuf = ByteBuffer.allocateDirect(facesDictionary.size() * 3 * 3 * 4);
        byteBuf.order(ByteOrder.nativeOrder());
        mNormalBuffer = byteBuf.asFloatBuffer();

        for(Face face : facesDictionary) {
            for(int i = 0; i<face.getNormalsIndex().length; i++) {
                mNormalBuffer.put(normalsDictionary.get(face.getNormalsIndex()[i]));
            }
        }
        mNormalBuffer.position(0);
    }

    private void loadFile(String file) {
        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(
                    PacManActivity.context.getAssets().open(file)));

            String line;
            do {
                line = reader.readLine();
                if(line != null) {
                    if (line.startsWith("v ")) { //vertex
                        String[] split = line.split(" ");
                        float[] vertex = new float[3];
                        vertex[0] = Float.parseFloat(split[1]);
                        vertex[1] = Float.parseFloat(split[2]);
                        vertex[2] = Float.parseFloat(split[3]);

                        verticesDictionary.add(vertex);
                    } else if (line.startsWith("vn ")) { //normal
                        String[] split = line.split(" ");
                        float[] normal = new float[3];
                        normal[0] = Float.parseFloat(split[1]);
                        normal[1] = Float.parseFloat(split[2]);
                        normal[2] = Float.parseFloat(split[3]);

                        normalsDictionary.add(normal);
                    } else if (line.charAt(0) == 'f') { //normal
                        facesDictionary.add(new Face(line));
                    }
                }
            } while (line != null);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public void draw(float[] projMatrix, float[] viewMatrix) {
        GLES20.glUseProgram(mProgram);

        Matrix.setIdentityM(mModelMatrix, 0);
        long time = SystemClock.uptimeMillis() % 4000L;
        float angle = 0.090f * ((int) time);
        Matrix.setRotateM(mModelMatrix, 0, angle, 0, 1f, 0);

        Matrix.multiplyMM(mModelViewMatrix, 0, viewMatrix, 0, mModelMatrix, 0);

        normalMatrix(mNormalMatrix, 0, mModelViewMatrix, 0);

        // uniforms
        mProjMatrixHandle = GLES20.glGetUniformLocation(mProgram, "uProjMatrix");
        mModelViewMatrixHandle = GLES20.glGetUniformLocation(mProgram, "uModelViewMatrix");
        mNormalMatrixHandle = GLES20.glGetUniformLocation(mProgram, "uNormalMatrix");
        mColorHandle = GLES20.glGetUniformLocation(mProgram, "uColor");
        mLightHandle = GLES20.glGetUniformLocation(mProgram, "uLight");
        mLight2Handle = GLES20.glGetUniformLocation(mProgram, "uLight2");

        GLES20.glUniformMatrix4fv(mProjMatrixHandle, 1, false, projMatrix, 0);
        GLES20.glUniformMatrix4fv(mModelViewMatrixHandle, 1, false, mModelViewMatrix, 0);
        GLES20.glUniformMatrix4fv(mNormalMatrixHandle, 1, false, mNormalMatrix, 0);

        GLES20.glUniform3fv(mColorHandle, 1, color, 0);
        GLES20.glUniform3fv(mLightHandle, 1, mLight, 0);
        GLES20.glUniform3fv(mLight2Handle, 1, mLight2, 0);

        // attributes
        mPositionHandle = GLES20.glGetAttribLocation(mProgram, "aPosition");
        mNormalHandle = GLES20.glGetAttribLocation(mProgram, "aNormal");

        GLES20.glEnableVertexAttribArray(mPositionHandle);
        GLES20.glEnableVertexAttribArray(mNormalHandle);

        GLES20.glVertexAttribPointer(
                mPositionHandle, COORDS_PER_VERTEX,
                GLES20.GL_FLOAT, false,
                VERTEX_STRIDE, mVertexBuffer);

        GLES20.glVertexAttribPointer(
                mNormalHandle, COORDS_PER_VERTEX,
                GLES20.GL_FLOAT, false,
                VERTEX_STRIDE, mNormalBuffer);

        // Draw the cube
        GLES20.glDrawArrays(GLES20.GL_TRIANGLES, 0, facesDictionary.size() * 3);

        GLES20.glDisableVertexAttribArray(mPositionHandle);
        GLES20.glDisableVertexAttribArray(mNormalHandle);
    }

    private void normalMatrix(float[] dst, int dstOffset, float[] src, int srcOffset) {
        Matrix.invertM(dst, dstOffset, src, srcOffset);
        dst[12] = 0;
        dst[13] = 0;
        dst[14] = 0;

        float[] temp = Arrays.copyOf(dst, 16);

        Matrix.transposeM(dst, dstOffset, temp, 0);
    }

    public class Face {
        private int[] verticesIndex;
        private int[] normalsIndex;

        public Face(String objLine)
        {
            verticesIndex = new int[3];
            normalsIndex = new int[3];

            String[] vertices = objLine.split(" ");
            String[] vertexInfo;

            for(int i = 1; i<4; i++) {
                vertexInfo = vertices[i].split("/");
                verticesIndex[i - 1] = Integer.parseInt(vertexInfo[0]) - 1;
                normalsIndex[i - 1] = Integer.parseInt(vertexInfo[2]) - 1;
            }
        }

        public int[] getVerticesIndex() {
            return verticesIndex;
        }

        public int[] getNormalsIndex() {
            return normalsIndex;
        }

        public String toString() {
            String str = "";
            for(int i : normalsIndex)
                str += normalsDictionary.get(i)[0] + ":" +
                        normalsDictionary.get(i)[1] + ":" +
                        normalsDictionary.get(i)[2] + "\t";

            return str;
        }
    }
}
