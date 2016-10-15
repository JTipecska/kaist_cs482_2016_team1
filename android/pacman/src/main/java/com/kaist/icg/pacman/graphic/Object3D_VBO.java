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
    protected float[] color = {(float) Math.random(), (float) Math.random(), (float) Math.random()};
    private ArrayList<float[]> verticesDictionary;
    private ArrayList<float[]> normalsDictionary;
    private ArrayList<VertexInfo> verticesInfoList;
    private ArrayList<Integer> verticesOrderList;

    private ShortBuffer drawOrderBuffer;

    private int i;

    public Object3D_VBO(String file) {
        verticesDictionary = new ArrayList<>();
        normalsDictionary = new ArrayList<>();
        verticesInfoList = new ArrayList<>();
        verticesOrderList = new ArrayList<>();

        loadFile(file);
        buildBuffers();
        Log.d("Object3D_VBO", "[" + file + "] " + verticesInfoList.size() + " vertices / " + verticesOrderList.size() + " vertices draw order");
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
        computeModelMatrix();

        shaderManager.draw(modelMatrix, vertexBuffer,
                normalBuffer, vertexBufferSize,
                material, shader);

        for(i = 0; i<children.size(); i++)
        children.get(i).draw();
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
