package Engine.Graphics;

import Datatypes.Color;
import Datatypes.Vec3;

public class Cubemap {

    Texture[] faces = new Texture[6];

    private static Vec3 skyboxVertices[] = {
            // positions
            new Vec3(-10.0f,  10.0f, -10.0f),
            new Vec3(-10.0f, -10.0f, -10.0f),
            new Vec3(10.0f, -10.0f, -10.0f),
            new Vec3(10.0f, -10.0f, -10.0f),
            new Vec3(10.0f,  10.0f, -10.0f),
            new Vec3(-10.0f,  10.0f, -10.0f),

            new Vec3(-10.0f, -10.0f,  10.0f),
            new Vec3(-10.0f, -10.0f, -10.0f),
            new Vec3(-10.0f,  10.0f, -10.0f),
            new Vec3(-10.0f,  10.0f, -10.0f),
            new Vec3(-10.0f,  10.0f,  10.0f),
            new Vec3(-10.0f, -10.0f,  10.0f),

            new Vec3(10.0f, -10.0f, -10.0f),
            new Vec3(10.0f, -10.0f,  10.0f),
            new Vec3(10.0f,  10.0f,  10.0f),
            new Vec3(10.0f,  10.0f,  10.0f),
            new Vec3(10.0f,  10.0f, -10.0f),
            new Vec3(10.0f, -10.0f, -10.0f),

            new Vec3(-10.0f, -10.0f,  10.0f),
            new Vec3(-10.0f,  10.0f,  10.0f),
            new Vec3(10.0f,  10.0f,  10.0f),
            new Vec3(10.0f,  10.0f,  10.0f),
            new Vec3(10.0f, -10.0f,  10.0f),
            new Vec3(-10.0f, -10.0f,  10.0f),

            new Vec3(-10.0f,  10.0f, -10.0f),
            new Vec3(10.0f,  10.0f, -10.0f),
            new Vec3(10.0f,  10.0f,  10.0f),
            new Vec3(10.0f,  10.0f,  10.0f),
            new Vec3(-10.0f,  10.0f,  10.0f),
            new Vec3(-10.0f,  10.0f, -10.0f),

            new Vec3(-10.0f, -10.0f, -10.0f),
            new Vec3(-10.0f, -10.0f,  10.0f),
            new Vec3(10.0f, -10.0f, -10.0f),
            new Vec3(10.0f, -10.0f, -10.0f),
            new Vec3(-10.0f, -10.0f,  10.0f),
            new Vec3(10.0f, -10.0f,  10.0f)
    };
    public static Mesh skyboxMesh = new Mesh(skyboxVertices);

    public Cubemap(String[] faceFilenames) {
        faces[0] = new Texture(faceFilenames[0]); // positive x
        faces[1] = new Texture(faceFilenames[1]); // positive y
        faces[2] = new Texture(faceFilenames[2]); // positive z
        faces[3] = new Texture(faceFilenames[3]); // negative x
        faces[4] = new Texture(faceFilenames[4]); // negative y
        faces[5] = new Texture(faceFilenames[5]); // negative z
    }

    public Color getPixel(Vec3 dir) {

        float[] axes = new float[] { dir.x, dir.y, dir.z };

        float u = 0, v = 0;
        int faceIndex = getMaxDirection(axes);
        switch (faceIndex) {
            case 0: // positive X
                v = ((dir.y / dir.x) + 1) * 0.5f;
                u = 1 - ((dir.z / dir.x) + 1) * 0.5f;
                break;
            case 3: // negative X
                v = 1 - ((dir.y / dir.x) + 1) * 0.5f;
                u = 1 - ((dir.z / dir.x) + 1) * 0.5f;
                break;
            case 1: // positive Y
                v = ((dir.x / dir.y) + 1) * 0.5f;
                u = ((dir.z / dir.y) + 1) * 0.5f;
                break;
            case 4: // negative Y
                v = ((dir.x / dir.y) + 1) * 0.5f;
                u = 1 - ((dir.z / dir.y) + 1) * 0.5f;
                break;
            case 2: // positive Z
                u = ((dir.x / dir.z) + 1) * 0.5f;
                v = ((dir.y / dir.z) + 1) * 0.5f;
                break;
            case 5: // negative Z
                u = ((dir.x / dir.z) + 1) * 0.5f;
                v = 1 - ((dir.y / dir.z) + 1) * 0.5f;
                break;
        }

        return faces[faceIndex].getPixel(u, v);
    }

    private int getMaxDirection(float[] axes) {

        int maxI = 0;
        maxI = Math.abs(axes[1]) > Math.abs(axes[maxI]) ? 1 : maxI;
        maxI = Math.abs(axes[2]) > Math.abs(axes[maxI]) ? 2 : maxI;

        if (axes[maxI] < 0) {
            return maxI + 3;
        } else return maxI;
    }

}
