package Engine.Graphics;

import Datatypes.Vec2;
import Datatypes.Vec3;
import Datatypes.Vec4;

public class Vertex {

    float[] data;

    public Vertex() {
        data = new float[4];
    }

    public Vertex(int size) {
        data = new float[size];
    }

    public Vertex(float[] data) {
        this.data = data;
    }

    public Vertex(Vertex other) {
        data = new float[other.data.length];
        System.arraycopy(other.data, 0, data, 0, other.data.length);
    }

    public Vec4 getPosition() {
        return getVec4(0);
    }

    public void setPosition(Vec4 val) {
        setVec4(0, val);
    }

    public Vec4 getVec4(int offset) {
        return new Vec4(data[offset+0], data[offset+1], data[offset+2], data[offset+3]);
    }

    public void setVec4(int offset, Vec4 val) {
        data[offset+0] = val.x;
        data[offset+1] = val.y;
        data[offset+2] = val.z;
        data[offset+3] = val.w;
    }

    public Vec3 getVec3(int offset) {
        return new Vec3(data[offset+0], data[offset+1], data[offset+2]);
    }

    public void setVec3(int offset, Vec3 val) {
        data[offset+0] = val.x;
        data[offset+1] = val.y;
        data[offset+2] = val.z;
    }

    public Vec2 getVec2(int offset) {
        return new Vec2(data[offset+0], data[offset+1]);
    }

    public void setVec2(int offset, Vec2 val) {
        data[offset+0] = val.x;
        data[offset+1] = val.y;
    }

    public float getFloat(int offset) { return data[offset]; }
    public void setFloat(int offset, float value) { data[offset] = value; }

    public Vertex add(Vertex other) {
        float[] nData = new float[data.length];
        for (int i = 0; i < data.length; i++) {
            nData[i] = data[i] + other.data[i];
        }
        return new Vertex(nData);
    }

    public Vertex sub(Vertex other) {
        float[] nData = new float[data.length];
        for (int i = 0; i < data.length; i++) {
            nData[i] = data[i] - other.data[i];
        }
        return new Vertex(nData);
    }

    public Vertex muls(float s) {
        float[] nData = new float[data.length];
        for (int i = 0; i < data.length; i++) {
            nData[i] = data[i] * s;
        }
        return new Vertex(nData);
    }

    public static Vertex Interpolate(Vertex a, Vertex b, Vertex c, float u, float v) {
        int length = a.data.length;
        Vertex interpolated = new Vertex(length);
        for (int i = 0; i < length; i++) {
            interpolated.data[i] = a.data[i] + (b.data[i] - a.data[i]) * u + (c.data[i] - a.data[i]) * v;
        }
        return interpolated;
    }

    public static Vertex Interpolate(Vertex a, Vertex b, float u) {
        int length = a.data.length;
        Vertex interpolated = new Vertex(length);
        for (int i = 0; i < length; i++) {
            interpolated.data[i] = a.data[i] + (b.data[i] - a.data[i]) * u;
        }
        return interpolated;
    }
}
