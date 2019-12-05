package Datatypes;

public class Vec3 {

    public float x, y, z;

    public Vec3() {
        x = y = z = 0;
    }

    public Vec3(float x, float y, float z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public Vec3(Vec3 o) {
        this.x = o.x;
        this.y = o.y;
        this.z = o.z;
    }

    public Vec3(Vec4 o) {
        this.x = o.x;
        this.y = o.y;
        this.z = o.z;
    }

    public Vec3 mul(float s) {
        return new Vec3(x * s, y * s, z * s);
    }

    public Vec3 mul(Vec3 v) {
        return new Vec3(x * v.x, y * v.y, z * v.z);
    }

    public Vec3 div(float s) { return new Vec3(x / s, y / s, z / s); }

    public Vec3 div(Vec3 v) { return new Vec3(x / v.x, y / v.y, z / v.z); }

    public Vec3 add(Vec3 v) {
        return new Vec3(x + v.x, y + v.y, z + v.z);
    }

    public Vec3 add(float s) {
        return new Vec3(x + s, y + s, z + s);
    }

    public Vec3 sub(Vec3 v) { return new Vec3(x - v.x, y - v.y, z - v.z); }

    public float magnitude() {
        return (float) Math.sqrt(x * x + y * y + z * z);
    }

    public float magnitude2() { return x * x + y * y + z * z; }

    public Vec3 normalize() {
        float mag = magnitude();
        if (mag == 0) {
            return new Vec3(0, 0, 0);
        }
        return div(mag);
    }

    public Vec3 cross(Vec3 v) {
        return new Vec3(
                y * v.z - z * v.y,
                z * v.x - x * v.z,
                x * v.y - y * v.x
        );
    }

    public Vec3 abs() {
        return new Vec3(Math.abs(x), Math.abs(y), Math.abs(z));
    }

    public float dot(Vec3 v) {
        return x * v.x + y * v.y + z * v.z;
    }

    public boolean lessThan(Vec3 o) {
        return x < o.x && y < o.y && z < o.z;
    }

    public boolean greaterThan(Vec3 o) {
        return x > o.x && y > o.y && z > o.z;
    }

    public static Vec3 zero() { return new Vec3(0, 0, 0); }

    public static float distance(Vec3 a, Vec3 b) {
        return (float) Math.sqrt(distance2(a, b));
    }

    public static float distance2(Vec3 a, Vec3 b) {
        float dx = a.x - b.x;
        float dy = a.y - b.y;
        float dz = a.z - b.z;
        return dx*dx + dy*dy + dz*dz;
    }

    public static Vec3 reflect(Vec3 d, Vec3 n) {
        return d.sub( n.mul(2 * d.dot(n)) );
    }

    public static Vec3 Interpolate(Vec3 a, Vec3 b, float t) {
        Vec3 delta = b.sub(a);
        if (Math.abs(delta.magnitude2()) <= 1e-6) return a;
        return a.add(delta.mul(t));
    }

    public static Vec3 Reflect(Vec3 d, Vec3 n) {
        return d.sub(n.mul(2 * d.dot(n)));
    }

    public static Vec3 constant(float c) { return new Vec3(c, c, c); }

    // parseVec3 parses vectors that are in the format returned by the toString() method of this class
    public static Vec3 parseVec3(String string) throws NumberFormatException {
        String commaSeparated;
        try {
            // shave the brackets off the ends
            commaSeparated = string.substring(1, string.length() - 1);
        }
        catch (Exception e) {
            throw new NumberFormatException("Incorrect Format!");
        }
        String[] tokens = commaSeparated.split(",");
        Float[] vecValues = new Float[tokens.length];

        if (tokens.length != 3) {
            throw new NumberFormatException("Incorrect number of vector components!");
        }
        else {
            for (int i = 0; i < 3; i++) {
                vecValues[i] = Float.parseFloat(tokens[i]);
            }
            return new Vec3(vecValues[0], vecValues[1], vecValues[2]);
        }
    }

    public String toString() {
        return "[" + x + "," + y + "," + z + "]";
    }
}
