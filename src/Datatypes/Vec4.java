package Datatypes;

public class Vec4 {

    public float x, y, z, w;

    public Vec4(float x, float y, float z, float w) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.w = w;
    }

    public Vec4(Vec4 o) {
        x = o.x;
        y = o.y;
        z = o.z;
        w = o.w;
    }

    public Vec4(Vec3 o, float w) {
        x = o.x;
        y = o.y;
        z = o.z;
        this.w = w;
    }

    void _divide(float s) {
        x /= s;
        y /= s;
        z /= s;
        w /= s;
    }

    public static Vec4 zero() {
        return new Vec4(0, 0, 0, 0);
    }

    public float magnitude() {
        return (float) Math.sqrt(x * x + y * y + z * z + w * w);
    }

    public Vec4 normalize() {
        float mag = magnitude();
        return mag == 0 ? zero() : div(mag);
    }

    public float get(int i) {
        switch(i) {
            case 0: return x;
            case 1: return y;
            case 2: return z;
            case 3: return w;
        }
        return 0;
    }

    public void set(Vec4 other) { this.x = other.x; this.y = other.y; this.z = other.z; this.w = other.w; }

    public Vec4 sub(Vec4 v) {
        return new Vec4(x - v.x, y - v.y, z - v.z, w - v.w);
    }

    public Vec4 add(Vec4 v) {
        return new Vec4(x + v.x, y + v.y, z + v.z, w + v.w);
    }

    public Vec4 mul(float s) {
        return new Vec4(x * s, y * s, z * s, w * s);
    }

    public Vec4 div(float s) {
        return new Vec4(x / s, y / s, z / s, w / s);
    }

    public void divW() {
        _divide(w);
    }

    public String toString() {
        return "[" + x + "," + y + "," + z + "," + w + "]";
    }
}
