package Engine.Graphics;

import Datatypes.Color;
import Datatypes.Vec3;
import Datatypes.Vec4;

public class CubemapShader extends Shader {

    Cubemap cubemap;

    public CubemapShader(Cubemap cubemap) {
        this.cubemap = cubemap;
    }

    final static int OUT_STRIDE = 7;
    final static int CUBECOORDS_OUT = 4;

    Vertex vertex(Vertex in) {

        Vec4 pos = in.getPosition();
        Vec3 cubeCoords = new Vec3(pos);
        pos = P.multiply(B).multiply(pos);
        pos.z = pos.w;

        Vertex out = new Vertex(OUT_STRIDE);
        out.setPosition(pos);
        out.setVec3(CUBECOORDS_OUT, cubeCoords);

        //System.out.println("skybox pos out: " + pos + ", dir out: " + cubeCoords);

        return out;
    }

    Color fragment(Vertex in) {
        return cubemap.getPixel(in.getVec3(CUBECOORDS_OUT));
    }

}
