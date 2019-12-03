package Engine.Graphics;

import Datatypes.Vec2;
import Datatypes.Vec3;
import Datatypes.Vec4;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.stream.Collectors;

public class Mesh {

    private Vertex[] vertexBuffer;
    private Vertex[] transformBuffer;
    private Integer[] triangleBuffer;
    private Vec3 min, max;

    public Mesh(Vertex[] vertBuffer) {
        this.vertexBuffer = vertBuffer;
        transformBuffer = new Vertex[vertexBuffer.length];
        triangleBuffer = null;
        calcMinMax();
    }

    public Mesh(Vertex[] vertBuffer, Integer[] triBuffer) {
        vertexBuffer = vertBuffer;
        transformBuffer = new Vertex[vertBuffer.length];
        triangleBuffer = triBuffer;
        calcMinMax();
    }

    Mesh(Vec3[] positions) {
        vertexBuffer = new Vertex[positions.length];
        for (int i = 0; i < vertexBuffer.length; i++) {
            vertexBuffer[i] = new Vertex();
            vertexBuffer[i].setPosition(new Vec4(positions[i], 1));
        }
        transformBuffer = new Vertex[vertexBuffer.length];
        calcMinMax();
    }

    public static Mesh loadObj(String filename) throws IOException {

        ArrayList<Vec3> positions = new ArrayList(); positions.add(Vec3.zero());
        ArrayList<Vec3> normals = new ArrayList();   normals.add(Vec3.zero());
        ArrayList<Vec2> textures = new ArrayList();  textures.add(Vec2.zero());
        ArrayList<Integer> triangles = new ArrayList();

        BufferedReader reader = new BufferedReader(new FileReader(filename));
        ArrayList<String> lines = new ArrayList(reader.lines().collect(Collectors.toList()));

        for (String line : lines) {
            String[] tokens = line.split(" ");

            switch (tokens[0]) {
                case "v" :
                    positions.add(new Vec3(Float.parseFloat(tokens[1]), Float.parseFloat(tokens[2]), Float.parseFloat(tokens[3])));
                    break;
                case "vn":
                    normals.add(new Vec3(Float.parseFloat(tokens[1]), Float.parseFloat(tokens[2]), Float.parseFloat(tokens[3])));
                    break;
                case "vt":
                    textures.add(new Vec2(Float.parseFloat(tokens[1]), Float.parseFloat(tokens[2])));
                    break;
                case "f" :
                    for (int i = 0; i < 3; i++) {
                        String[] indices = tokens[i + 1].split("/");
                        for (int j = 0; j < 3; j++) {
                            if (indices[j].equals(""))
                                triangles.add(0);
                            else
                                triangles.add(Integer.parseInt(indices[j]));
                        }
                    }
                    break;
            }
        }

        Vertex[] vertices = new Vertex[triangles.size() / 3];

        for (int i = 0; i < triangles.size(); i += 9) {
            for (int j = 0; j < 9; j += 3) {
                Vertex newVert = new Vertex(9);

                newVert.setVec4(0, new Vec4(positions.get (triangles.get(i + j + 0)), 1));
                newVert.setVec2(7, new Vec2(textures.get  (triangles.get(i + j + 1))));
                newVert.setVec3(4, new Vec3(normals.get   (triangles.get(i + j + 2))));

                vertices[(i + j) / 3] = newVert;
            }
        }

        return new Mesh(vertices);
    }

    private void calcMinMax() {
        min = new Vec3();
        max = new Vec3();
        for (Vertex v : vertexBuffer) {
            Vec3 p = new Vec3(v.getPosition());
            min.x = Math.min(p.x, min.x);
            min.y = Math.min(p.y, min.y);
            min.z = Math.min(p.z, min.z);
            max.x = Math.max(p.x, max.x);
            max.y = Math.max(p.y, max.y);
            max.z = Math.max(p.z, max.z);
        }
    }

    public Vec3 getMin() {
        return min;
    }

    public Vec3 getMax() {
        return max;
    }

    public int numVertices() {
        return vertexBuffer.length;
    }

    public Vertex getVertex(int index) {
        return vertexBuffer[index];
    }

    public void setTransformedVertex(int index, Vertex v) {
        transformBuffer[index] = v;
    }

    public int numTriangles() {
        if (triangleBuffer == null)
            return vertexBuffer.length / 3;
        else return triangleBuffer.length / 3;
    }

    public Vertex[] getTriangle(int index) {
        Vertex[] tri = new Vertex[3];

        index *= 3;

        if (triangleBuffer == null) {
            tri[0] = transformBuffer[index + 0];
            tri[1] = transformBuffer[index + 1];
            tri[2] = transformBuffer[index + 2];
        }
        else {
            tri[0] = new Vertex(transformBuffer[triangleBuffer[index + 0]]);
            tri[1] = new Vertex(transformBuffer[triangleBuffer[index + 1]]);
            tri[2] = new Vertex(transformBuffer[triangleBuffer[index + 2]]);
        }

        return tri;
    }


}
