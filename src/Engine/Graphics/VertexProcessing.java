package Engine.Graphics;

import Datatypes.Vec3;
import Datatypes.Vec4;

import java.util.ArrayList;

public class VertexProcessing {

    public VertexProcessing(Shader shader, Framebuffer buffer, Vertex v1, Vertex v2, Vertex v3) {

        // we clip triangles that are behind the near plane of the camera, to avoid crazy shit happening
        // performing the perspective divide on vertices behind the camera can lead to very undesirable visual artifacts
        if (behindClippingPlane(v1) || behindClippingPlane(v2) || behindClippingPlane(v3)) {
            ArrayList<Vertex> clippedTriangles = getClippedTriangles(v1, v2, v3);

            for (int i = 0; i < clippedTriangles.size() - 1; i+=2) {
                Vertex t_v1 = new Vertex(clippedTriangles.get((i+0) % clippedTriangles.size()));
                Vertex t_v2 = new Vertex(clippedTriangles.get((i+1) % clippedTriangles.size()));
                Vertex t_v3 = new Vertex(clippedTriangles.get((i+2) % clippedTriangles.size()));

                perspectiveDivide(t_v1, t_v2, t_v3);
                if (backFacing(t_v1, t_v2, t_v3)) continue;
                new Rasterizer(shader, buffer, t_v1, t_v2, t_v3);
            }
        }
        else {
            perspectiveDivide(v1, v2, v3);
            if (backFacing(v1, v2, v3)) return;
            new Rasterizer(shader, buffer, v1, v2, v3);
        }
    }

    private static boolean outOfBounds(Vertex v1, Vertex v2, Vertex v3) {
        return outOfBounds(v1.getPosition()) && outOfBounds(v2.getPosition()) && outOfBounds(v3.getPosition());
    }

    private static boolean outOfBounds(Vec4 p) {
        boolean xin = p.x >= -p.w && p.x <= p.w;
        boolean yin = p.y >= -p.w && p.y <= p.w;
        boolean zin = p.z >= -p.w && p.z <= p.w;
        return !(xin && yin && zin);
    }

    private static boolean backFacing(Vertex v1, Vertex v2, Vertex v3) {
        Vec3 e1 = new Vec3(v3.getPosition().sub(v1.getPosition()));
        Vec3 e2 = new Vec3(v2.getPosition().sub(v1.getPosition()));
        return e1.cross(e2).z > 0;
    }

    private static boolean behindClippingPlane(Vertex v) {
        return v.getPosition().w < 1;
    }

    private static ArrayList<Vertex> getClippedTriangles(Vertex v1, Vertex v2, Vertex v3) {
        ArrayList<Vertex> tmp_vertexOut = new ArrayList();

        tmp_vertexOut.add(v1);
        tmp_vertexOut.add(getClippingIntersectionVertex(v1, v2));
        tmp_vertexOut.add(v2);
        tmp_vertexOut.add(getClippingIntersectionVertex(v2, v3));
        tmp_vertexOut.add(v3);
        tmp_vertexOut.add(getClippingIntersectionVertex(v3, v1));

        ArrayList<Vertex> vertexOut = new ArrayList();
        for (Vertex v : tmp_vertexOut) {
            if (!(v == null || v.getPosition().w + 1e-5 < 1))
                vertexOut.add(v);
        }

        return vertexOut;
    }

    private static Vertex getClippingIntersectionVertex(Vertex a, Vertex b) {
        float w0 = a.getPosition().w;
        float w1 = b.getPosition().w;

        float wt = (1 - w0) / (w1 - w0);
        if (wt < 0 || wt > 1) return null;

        return Vertex.Interpolate(a, b, wt);
    }

    private static void perspectiveDivide(Vertex v1, Vertex v2, Vertex v3) {
        Vec4 p1 = v1.getPosition();
        Vec4 p2 = v2.getPosition();
        Vec4 p3 = v3.getPosition();

        float w0 = p1.w;
        float w1 = p2.w;
        float w2 = p3.w;

        // preserve w values, because they're still needed during the rasterization stage
        v1.setPosition( new Vec4(new Vec3(p1.div(w0)), w0));
        v2.setPosition( new Vec4(new Vec3(p2.div(w1)), w1));
        v3.setPosition( new Vec4(new Vec3(p3.div(w2)), w2));
    }
}
