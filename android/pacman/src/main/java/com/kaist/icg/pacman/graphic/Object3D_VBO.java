package com.kaist.icg.pacman.graphic;

import android.opengl.GLES20;
import android.util.Log;

import com.kaist.icg.pacman.graphic.android.PacManActivity;
import com.kaist.icg.pacman.graphic.android.PacManGLRenderer;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.ShortBuffer;
import java.util.ArrayList;

/**
 * OBJ file parser with VBO optimisation
 *
 */
public class Object3D_VBO extends Drawable {
    private ArrayList<float[]> verticesDictionary;
    private ArrayList<float[]> normalsDictionary;
    private ArrayList<VertexInfo> verticesInfoList;
    private ArrayList<Integer> verticesOrderList;

    private ShortBuffer drawOrderBuffer;

    private int mLightHandle;
    private int mLight2Handle;
    private int mColorHandle;

    private float[] mLight = new float[3];
    private float[] mLight2 = new float[3];

    private float color[] = { 1f, 1f, 1f };

    public Object3D_VBO(String file) {
        verticesDictionary = new ArrayList<>();
        normalsDictionary = new ArrayList<>();
        verticesInfoList = new ArrayList<>();
        verticesOrderList = new ArrayList<>();

        loadFile(file);
        buildBuffers();
        Log.d("Object3D_VBO", "[" + file + "] " + verticesInfoList.size() + " vertices / " + verticesOrderList.size() + " vertices draw order");

        // prepare shaders and OpenGL program
        int vertexShader = PacManGLRenderer.loadShaderFromFile(
                GLES20.GL_VERTEX_SHADER, "basic-gl2.vshader");
        int fragmentShader = PacManGLRenderer.loadShaderFromFile(
                GLES20.GL_FRAGMENT_SHADER, "diffuse-gl2.fshader");

        program = GLES20.glCreateProgram();             // create empty OpenGL Program
        GLES20.glAttachShader(program, vertexShader);   // add the vertex shader to program
        GLES20.glAttachShader(program, fragmentShader); // add the fragment shader to program
        GLES20.glLinkProgram(program);                  // create OpenGL program executables

        mLight = new float[] {2.0f, 3.0f, 14.0f};
        mLight2 = new float[] {-2.0f, -3.0f, -5.0f};
    }

    /**
     * Load OBJ file and build the model
     * @param file File path
     */
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
                    } else if (line.startsWith("f ")) { //face
                        String[] vertices = line.split(" ");
                        String[] vertexInfo;
                        int vertexIndex, normalIndex, vertexInfoIndex;

                        for(int i = 1; i<4; i++) {
                            vertexInfo = vertices[i].split("/");
                            vertexIndex = Integer.parseInt(vertexInfo[0]) - 1;
                            normalIndex = Integer.parseInt(vertexInfo[2]) - 1;

                            vertexInfoIndex = addVertexInfo(vertexIndex, normalIndex);
                            verticesOrderList.add(vertexInfoIndex);
                        }
                    }
                }
            } while (line != null);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Build vertices and normals buffer (FloatBuffer) from the model
     */
    private void buildBuffers() {
        ByteBuffer byteBuf = ByteBuffer.allocateDirect(verticesInfoList.size() * 3 * 4);
        byteBuf.order(ByteOrder.nativeOrder());
        vertexBuffer = byteBuf.asFloatBuffer();

        for(VertexInfo vi : verticesInfoList) {
            vertexBuffer.put(verticesDictionary.get(vi.getVertexIndex()));
        }
        vertexBuffer.position(0);

        byteBuf = ByteBuffer.allocateDirect(verticesInfoList.size() * 3 * 4);
        byteBuf.order(ByteOrder.nativeOrder());
        normalBuffer = byteBuf.asFloatBuffer();

        for(VertexInfo vi : verticesInfoList) {
            normalBuffer.put(normalsDictionary.get(vi.getNormalIndex()));
        }
        normalBuffer.position(0);

        byteBuf = ByteBuffer.allocateDirect(verticesOrderList.size() * 2);
        byteBuf.order(ByteOrder.nativeOrder());
        drawOrderBuffer = byteBuf.asShortBuffer();

        for(int index : verticesOrderList) {
            drawOrderBuffer.put((short) index);
        }
        drawOrderBuffer.position(0);
    }

    /**
     * Draw the mesh on the current OpenGL context
     * @param projectionMatrix
     * @param viewMatrix
     */
    public void draw(float[] projectionMatrix, float[] viewMatrix) {
        GLES20.glUseProgram(program);
        prepareDraw(projectionMatrix, viewMatrix);

        mColorHandle = GLES20.glGetUniformLocation(program, "uColor");
        mLightHandle = GLES20.glGetUniformLocation(program, "uLight");
        mLight2Handle = GLES20.glGetUniformLocation(program, "uLight2");

        GLES20.glUniform3fv(mColorHandle, 1, color, 0);
        GLES20.glUniform3fv(mLightHandle, 1, mLight, 0);
        GLES20.glUniform3fv(mLight2Handle, 1, mLight2, 0);

        GLES20.glDrawElements(GLES20.GL_TRIANGLES, verticesOrderList.size(), GLES20.GL_UNSIGNED_SHORT, drawOrderBuffer);

        endDraw();
    }

    public void setColor(float[] color) {
        this.color = color;
    }

    /**
     * Inner class which handle information of one vertex
     */
    public class VertexInfo {
        private int vertexIndex;
        private int normalIndex;

        public VertexInfo(int vertexIndex, int normalIndex) {
            this.vertexIndex = vertexIndex;
            this.normalIndex = normalIndex;
        }

        public int getVertexIndex() {
            return vertexIndex;
        }

        public int getNormalIndex() {
            return normalIndex;
        }
    }

    private int addVertexInfo(int vertexIndex, int normalIndex) {
        for(int i = 0; i<verticesInfoList.size(); i++) {
            if(verticesInfoList.get(i).getVertexIndex() == vertexIndex &&
                    verticesInfoList.get(i).getNormalIndex() == normalIndex) {
                return i;
            }
        }

        verticesInfoList.add(new VertexInfo(vertexIndex, normalIndex));
        return verticesInfoList.size() - 1;
    }
}
