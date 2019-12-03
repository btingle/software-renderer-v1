package Engine.Graphics;

import Datatypes.Color;
import Datatypes.Vec2;
import Datatypes.Vec3;
import Datatypes.Vec4;

public class TriplanarTerrainShader extends Shader {

    Texture xTexture, yTexture, zTexture;

    public TriplanarTerrainShader(Texture x, Texture y, Texture z) {
        xTexture = x;
        yTexture = y;
        zTexture = z;
    }

    final int NORMAL_IN = 4;

    final int NORMAL_OUT = 4;
    final int WORLDPOS_OUT = 7;
    final int INTENSITY_OUT = 10;
    final int FOG_OUT = 11;
    final int STRIDE_OUT = 12;

    Vertex vertex(Vertex in) {
        Vec4 pos = in.getPosition();
        Vec3 norm = in.getVec3(NORMAL_IN);

        //Vec2  tex       = new Vec2(Math.abs(pos.x), Math.abs(pos.z));
        Vec3  worldPos  = M.multiply(new Vec3(pos));
        Vec3  worldNorm = M.multiplyDir(norm).normalize();
        float intensity = Math.max(0f, norm.dot(directionalLight));
        float fogDepth  = MV.multiply(pos).magnitude();

        Vertex out = new Vertex(STRIDE_OUT);
        out.setPosition(MVP.multiply(pos));
        out.setVec3(NORMAL_OUT, worldNorm);
        out.setVec3(WORLDPOS_OUT, worldPos);
        out.setFloat(FOG_OUT, fogDepth);
        out.setFloat(INTENSITY_OUT, intensity);

        return out;
    }

    Color fragment(Vertex in) {

        Vec3 worldPos = in.getVec3(WORLDPOS_OUT).abs();
        Vec3 worldNorm = in.getVec3(NORMAL_OUT).abs();
        float intensity = in.getFloat(INTENSITY_OUT);
        float fog = smoothstep(fogMin, fogMax, in.getFloat(FOG_OUT));

        Vec2 uvX = new Vec2(worldPos.y, worldPos.z);
        Vec2 uvY = new Vec2(worldPos.x, worldPos.z);
        Vec2 uvZ = new Vec2(worldPos.x, worldPos.y);

        Color rgbX = xTexture.getPixel(uvX.x, uvX.y);
        Color rgbY = yTexture.getPixel(uvY.x, uvY.y);
        Color rgbZ = zTexture.getPixel(uvZ.x, uvZ.y);

        //worldNorm = worldNorm.div(worldNorm.x + worldNorm.y + worldNorm.z);

        Vec3 rgb = Color.blend(rgbX, rgbY, rgbZ, worldNorm).mul(intensity);

        return new Color(Vec3.Interpolate(rgb, fogColor, 1 - fog));
    }

    private static float smoothstep(float min, float max, float val) {
        return Math.max(0, Math.min(1, (max - val) / (max - min)));
    }

}
