package Engine.Graphics;

import Datatypes.*;

// basic shader with per-fragment lighting
public class PhongShader extends Shader {

    public Texture texture;
    public Vec3 specularColor;
    public float specularStrength;
    public float shininess;

    public PhongShader(Texture texture, Vec3 specularColor, float specularStrength, float shininess) {
        this.texture = texture;
        this.specularColor = specularColor;
        this.specularStrength = specularStrength;
        this.shininess = shininess;
    }

    final int NORMAL_IN = 4;
    final int TEXTURE_IN = 7;

    final int TEXTURE_OUT = 4;
    final int NORMAL_OUT = 6;
    final int VIEWDIR_OUT = 9;
    //final int FRAGPOS_OUT = 9;
    final int FOGDEPTH_OUT = 12;
    final int OUT_STRIDE = 13;

    // Input: a vertex from the vertex buffer
    // Output: a modified vertex, optionally with different attributes
    Vertex vertex(Vertex in) {

        Vec4 pos = in.getPosition();
        Vec3 norm = in.getVec3(NORMAL_IN);

        // calculate per-vertex values
        Vec3 fragPos = M.transform(new Vec3(pos));
        float fogDepth = Math.abs((MV.multiply(pos)).z);
        pos = MVP.multiply(pos);
        norm = N.multiply(norm).normalize();
        Vec3 viewDir = new Vec3(MV.multiply(pos)).normalize();

        Vertex out = new Vertex(OUT_STRIDE);
        out.setPosition(pos);
        out.setVec2(TEXTURE_OUT, in.getVec2(TEXTURE_IN));
        out.setVec3(NORMAL_OUT, norm);
        //out.setVec3(VIEWDIR_OUT, viewDir);
        out.setVec3(VIEWDIR_OUT, viewDir);
        out.setFloat(FOGDEPTH_OUT, fogDepth);

        return out;
    }

    // Input: an interpolated vertex for a pixel location
    // Output: a color value for that pixel location
    Color fragment(Vertex in) {

        // initialize variables
        Vec3 norm = in.getVec3(NORMAL_OUT);
        Vec2 tex = in.getVec2(TEXTURE_OUT);
        Vec3 viewDir = in.getVec3(VIEWDIR_OUT);

        Color color = texture.getPixel(tex.x, 1 - tex.y);

        // calculate ambient light
        Vec3 ambient = ambientLightColor.mul(ambientLightIntensity);

        // calculate diffuse light
        float diff = Math.max(0f, Math.min(1, norm.dot(directionalLight)));
        Vec3 diffuse = directionalLightColor.mul(diff * directionalLightIntensity);

        // calculate specular highlight
        Vec3 reflectDir = Vec3.reflect(directionalLight.mul(-1), norm);
        float spec = (float) Math.pow(Math.max(0f, viewDir.dot(reflectDir)), shininess);
        Vec3 specular = specularColor.mul(spec * specularStrength);

        float fog = smoothstep(fogMin, fogMax, in.getFloat(FOGDEPTH_OUT));

        // add them together for result
        Vec3 colorOut = ambient.add(diffuse).add(specular).mul(color);
        return new Color(Vec3.Interpolate(colorOut, fogColor, 1 - fog));
    }

    private static float smoothstep(float min, float max, float val) {
        return Math.max(0, Math.min(1, (max - val) / (max - min)));
    }

}
