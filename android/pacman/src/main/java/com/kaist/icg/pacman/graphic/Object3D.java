package com.kaist.icg.pacman.graphic;

import android.opengl.GLES20;
import android.util.Log;

import com.kaist.icg.pacman.graphic.android.PacManActivity;
import com.kaist.icg.pacman.graphic.android.PacManGLRenderer;
import com.kaist.icg.pacman.manager.ShaderManager;
import com.kaist.icg.pacman.tool.Material;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.ArrayList;

/**
 * OBJ file parser
 */
public class Object3D extends Drawable {
    private int nbVertices;
    protected float[] color = {(float) Math.random(), (float) Math.random(), (float) Math.random()};

    private ArrayList<float[]> verticesDictionary;
    private ArrayList<float[]> normalsDictionary;
    private ArrayList<Face> facesDictionary;

    private int i;

    public Object3D(String file) {
        verticesDictionary = new ArrayList<>();
        normalsDictionary = new ArrayList<>();
        facesDictionary = new ArrayList<>();

        loadFile(file);
        buildBuffers();
        Log.d("Object3D", "[" + file + "] " + nbVertices + " vertices");

        vertexBufferSize = facesDictionary.size() * 3;
        material = new Material(color);
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
                        facesDictionary.add(new Face(line));
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
        nbVertices = 0;

        ByteBuffer byteBuf = ByteBuffer.allocateDirect(facesDictionary.size() * 3 * 3 * 4);
        byteBuf.order(ByteOrder.nativeOrder());
        vertexBuffer = byteBuf.asFloatBuffer();

        for(Face face : facesDictionary) {
            for(int i = 0; i<face.getVerticesIndex().length; i++) {
                vertexBuffer.put(verticesDictionary.get(face.getVerticesIndex()[i]));
                nbVertices++;
            }
        }
        vertexBuffer.position(0);

        byteBuf = ByteBuffer.allocateDirect(facesDictionary.size() * 3 * 3 * 4);
        byteBuf.order(ByteOrder.nativeOrder());
        normalBuffer = byteBuf.asFloatBuffer();

        for(Face face : facesDictionary) {
            for(int i = 0; i<face.getNormalsIndex().length; i++) {
                normalBuffer.put(normalsDictionary.get(face.getNormalsIndex()[i]));
            }
        }
        normalBuffer.position(0);
    }

    /**
     * Draw the mesh on the current OpenGL context
     */
    @Override
    public void draw() {
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
     * Inner class which handle face information (3 vertices)
     */
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
    }
}