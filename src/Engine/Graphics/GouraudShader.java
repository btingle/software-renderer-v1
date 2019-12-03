package Engine.Graphics;

import Datatypes.Color;
import Datatypes.Vec2;
import Datatypes.Vec3;
import Datatypes.Vec4;

// The gouraud shader has the same lighting model as the phong shader, but color is calculated per-vertex
// instead of per-fragment. This shader is faster than the Phong shader, but looks less realistic
public class GouraudShader extends Shader {

    public Texture texture;
    public Vec3 specularColor;
    public float specularStrength;
    public float shininess;

    public GouraudShader(Texture texture, Vec3 specularColor, float specularStrength, float shininess) {
        this.texture = texture;
        this.specularColor = specularColor;
        this.specularStrength = specularStrength;
        this.shininess = shininess;
    }

    final int NORMAL_IN = 4;
    final int TEXTURE_IN = 7;

    final int COLOR_OUT = 4;
    final int TEXTURE_OUT = 7;
    final int STRIDE_OUT = 9;

    Vertex vertex(Vertex in) {

        Vec4 pos = in.getPosition();
        Vec3 norm = in.getVec3(NORMAL_IN);
        Vec2 tex = in.getVec2(TEXTURE_IN);
        Vec3 viewDir = new Vec3(MV.multiply(pos)).normalize();

        norm = N.multiply(norm);

        // calculate ambient light
        Vec3 ambient = ambientLightColor.mul(ambientLightIntensity);

        // calculate diffuse light
        float diff = Math.max(0f, Math.min(1, norm.dot(directionalLight)));
        Vec3 diffuse = directionalLightColor.mul(diff * directionalLightIntensity);

        // calculate specular highlight
        Vec3 reflectDir = Vec3.reflect(directionalLight.mul(-1), norm);
        float spec = (float) Math.pow(Math.max(0f, viewDir.dot(reflectDir)), shininess);
        Vec3 specular = specularColor.mul(spec * specularStrength);

        Vec3 colorOut = ambient.add(diffuse).add(specular);

        // create new vertex with color data and return
        Vertex out = new Vertex(STRIDE_OUT);
        out.setPosition(MVP.multiply(pos));
        out.setVec3(COLOR_OUT, colorOut);
        out.setVec2(TEXTURE_OUT, tex);

        return out;
    }

    // just return interpolated color value multiplied by the interpolated texture color value
    Color fragment(Vertex in) {
        Vec2 tex = in.getVec2(TEXTURE_OUT);
        Color color = texture.getPixel(tex.x, 1 - tex.y);
        return new Color(in.getVec3(COLOR_OUT).mul(color));
    }

}
