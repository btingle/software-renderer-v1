package Datatypes;

public class Color extends Vec3 {

    final static int rMask = 0xFF;
    final static int gMask = 0xFF00;
    final static int bMask = 0xFF0000;

    public Color(int rgb) {
        float inv255 = 1f / 255f;
        x = (rgb & rMask) * inv255;
        y = ((rgb & gMask) >> 8) * inv255;
        z = ((rgb & bMask) >> 16) * inv255;
    }

    public Color(float r, float g, float b) {
        super(r, g, b);
    }
    public Color(Vec3 vec) { super(vec); }

    public int getRGB() {
        clampColors();
        int r = (int)(x * 255) & 0xFF;
        int g = (int)(y * 255) & 0xFF;
        int b = (int)(z * 255) & 0xFF;
        return r | (g << 8) | (b << 16) | (0xFF << 24);
    }

    public static Color white() { return new Color(1, 1, 1); }
    public static Color black() { return new Color(0, 0, 0); }

    // color values are clamped in the 0 -> 1 range
    public void clampColors() {
        x = Math.max(0, Math.min(x, 1));
        y = Math.max(0, Math.min(y, 1));
        z = Math.max(0, Math.min(z, 1));
    }

    public static Color blend(Color a, Color b, Color c, Vec3 weight) {
        return new Color
                (a.x * weight.x + b.x * weight.y + c.x * weight.z,
                 a.y * weight.x + b.y * weight.y + c.y * weight.z,
                 a.z * weight.x + b.z * weight.y + c.z * weight.z);
    }
}
