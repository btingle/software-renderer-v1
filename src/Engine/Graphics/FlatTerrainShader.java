package Engine.Graphics;

import Datatypes.Color;
import Datatypes.Vec2;
import Datatypes.Vec3;
import Datatypes.Vec4;

// fast terrain shader that uses a flat shading model, no specular highlights, and light intensity is calculated per vertex
public class FlatTerrainShader extends Shader {

    public Texture texture;
    public Vec3 specularColor;
    public float specularStrength;
    public float shininess;

    public FlatTerrainShader(Texture texture, Vec3 specularColor, float specularStrength, float shininess) {
        this.texture = texture;
        this.specularColor = specularColor;
        this.specularStrength = specularStrength;
        this.shininess = shininess;
    }

    final int NORMAL_IN = 4;

    // lightweight per-vertex info, only texture coordinates, fog depth, and light intensity, 4 floats total
    final int TEXTURE_OUT = 4;
    final int FOGDEPTH_OUT = 6;
    final int INTENSITY_OUT = 7;
    final int OUT_STRIDE = 8;

    // Input: a vertex from the vertex buffer
    // Output: a modified vertex, optionally with different attributes
    Vertex vertex(Vertex in) {

        Vec4 pos = in.getPosition();

        Vec2  tex       = new Vec2(Math.abs(pos.x), Math.abs(pos.z));
        float intensity = Math.max(0f, in.getVec3(NORMAL_IN).dot(directionalLight));
        float fogDepth  = MV.multiply(pos).magnitude();

        Vertex out = new Vertex(OUT_STRIDE);
        out.setPosition(MVP.multiply(pos));
        out.setVec2(TEXTURE_OUT, tex);
        out.setFloat(FOGDEPTH_OUT, fogDepth);
        out.setFloat(INTENSITY_OUT, intensity);

        return out;
    }

    // Input: an interpolated vertex for a pixel location
    // Output: a color value for that pixel location
    Color fragment(Vertex in) {

        Vec2  tex = in.getVec2(TEXTURE_OUT);
        float intensity = in.getFloat(INTENSITY_OUT);
        float fog = smoothstep(fogMin, fogMax, in.getFloat(FOGDEPTH_OUT));

        float intensityOut = clamp(0, 1, intensity + ambientLightIntensity);

        Color rgb = texture.getPixel(tex.x, tex.y);
        Vec3  out = rgb.mul(intensityOut);

        return new Color(Vec3.Interpolate(out, fogColor, 1 - fog));
    }

    private static float smoothstep(float min, float max, float val) {
        return Math.max(0, Math.min(1, (max - val) / (max - min)));
    }

    private static float clamp(float min, float max, float val) {
        return Math.max(min, Math.min(max, val));
    }

}
