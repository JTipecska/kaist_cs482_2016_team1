package com.kaist.icg.pacman.graphic;

import android.util.Log;

import com.kaist.icg.pacman.graphic.android.PacManActivity;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Object 3D factory allow loading mesh from .OBJ file more efficiently.
 * It avoid loading the same file multiple times.
 */
public class Object3DFactory {
    private static Object3DFactory INSTANCE;

    public static Object3DFactory getInstance() {
        if(INSTANCE == null)
            INSTANCE = new Object3DFactory();

        return INSTANCE;
    }

    private HashMap<String, Object3DData> objectsData;

    public Object3DFactory() {
        objectsData = new HashMap<>();
    }

    /**
     * Create a new Object3D instance (or a child class) from an .OBJ file
     * @param file The .OBJ file name from assets folder
     * @param type Object class wanted. Must be Object3D or a children class.
     * @return
     */
    public <T extends Object3D>T instanciate(String file, Class<T> type) {
        Object3DData data = objectsData.get(file);

        if(data == null) {
            data = new Object3DData(file);
            objectsData.put(file, data);
        }
        else
            Log.d("Object3DFactory", "Cloning [" + file + "] ");

        try {
            return type.getDeclaredConstructor(int.class,
                    FloatBuffer.class, FloatBuffer.class).newInstance(
                    data.getVertexBufferSize(),
                    data.getVertexBuffer(),
                    data.getNormalBuffer()
            );
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    public class Object3DData {
        private ArrayList<float[]> verticesDictionary;
        private ArrayList<float[]> normalsDictionary;
        private ArrayList<Face> facesDictionary;

        private int nbVertices;

        protected int vertexBufferSize;
        protected FloatBuffer vertexBuffer;
        protected FloatBuffer normalBuffer;

        public Object3DData(String file) {
            verticesDictionary = new ArrayList<>();
            normalsDictionary = new ArrayList<>();
            facesDictionary = new ArrayList<>();

            loadFile(file);
            buildBuffers();
            Log.d("Object3DFactory", "[" + file + "] " + nbVertices + " vertices");

            vertexBufferSize = facesDictionary.size() * 3;
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

        public int getVertexBufferSize() {
            return vertexBufferSize;
        }

        public FloatBuffer getVertexBuffer() {
            return vertexBuffer;
        }

        public FloatBuffer getNormalBuffer() {
            return normalBuffer;
        }
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
