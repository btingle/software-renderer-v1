package Datatypes;

public class Vec2 {

    public float x, y;

    public Vec2(float x, float y) {
        this.x = x;
        this.y = y;
    }

    public Vec2(Vec2 o) {
        this.x = o.x;
        this.y = o.y;
    }

    public Vec2(Vec3 o) {
        this.x = o.x;
        this.y = o.y;
    }

    public Vec2(Vec4 o) {
        this.x = o.x;
        this.y = o.y;
    }

    public Vec2 add(Vec2 v) {
        return new Vec2(x + v.x, y + v.y);
    }

    public Vec2 sub(Vec2 v) { return new Vec2(x - v.x, y - v.y); }

    public Vec2 add(float s) {
        return new Vec2(x + s, y + s);
    }

    public Vec2 mul(float s) {
        return new Vec2(x * s, y * s);
    }

    public Vec2 div(float s) { return new Vec2(x / s, y / s); }

    public float cross(Vec2 other) {
        return x * other.y - y * other.x;
    }

    public static Vec2 zero() { return new Vec2(0, 0); }

    public static float distance2(Vec2 a, Vec2 b) {
        float dx = (a.x - b.x);
        float dy = (a.y - b.y);
        return dx*dx + dy*dy;
    }

    public static float distance(Vec2 a, Vec2 b) {
        return (float) Math.sqrt(distance2(a, b));
    }

    public static Vec2 parseVec2(String string) throws NumberFormatException {
        String commaSeparated;
        try {
            // shave the brackets off the end
            commaSeparated = string.substring(1, string.length() - 1);
        }
        catch (Exception e) {
            throw new NumberFormatException("Incorrect Format!");
        }
        String[] tokens = commaSeparated.split(",");
        Float[] vecValues = new Float[tokens.length];

        if (tokens.length != 2) {
            throw new NumberFormatException("Incorrect number of vector components!");
        }
        else {
            for (int i = 0; i < 2; i++) {
                vecValues[i] = Float.parseFloat(tokens[i]);
            }
            return new Vec2(vecValues[0], vecValues[1]);
        }
    }

    public String toString() {
        return "[" + x + ", " + y + "]";
    }
}
