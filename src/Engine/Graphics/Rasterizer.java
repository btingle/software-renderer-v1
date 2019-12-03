package Engine.Graphics;

import Datatypes.Color;
import Datatypes.Vec2;
import Datatypes.Vec3;
import Datatypes.Vec4;

class Rasterizer {

    static boolean SKIP_DEPTH_TEST = false;

    private static class BBOX {
        int minX, maxX, minY, maxY;
        BBOX(int minX, int maxX, int minY, int maxY) {
            this.minX = minX;
            this.maxX = maxX;
            this.minY = minY;
            this.maxY = maxY;
        }
        public String toString() {
            return "mx:" + minX + ", my:" + minY + ", Mx:" + maxX + ", My:" + maxY;
        }
    }

    // rasterizes a triangle
    Rasterizer(Shader shader, Framebuffer buffer, Vertex v1, Vertex v2, Vertex v3) {

        Vec4 vp1 = v1.getPosition();
        Vec4 vp2 = v2.getPosition();
        Vec4 vp3 = v3.getPosition();

        Vec3 wVals = new Vec3(1 / vp1.w, 1 / vp2.w, 1 / vp3.w);
        Vec3 zVals = new Vec3(vp1.z, vp2.z,vp3.z);

        Vec2 p1 = new Vec2(vp1);
        Vec2 p2 = new Vec2(vp2);
        Vec2 p3 = new Vec2(vp3);

        viewportTransform(buffer, p1, p2, p3);
        BBOX bbox = getBoundingBox(buffer, p1, p2, p3);

        float invArea = 1 / edgeFunction(p1, p2, p3);
        if (invArea < 0) return;

        Vec2 startPoint = new Vec2(bbox.minX, bbox.minY);
        Vec3 edgeRow = new Vec3(
                edgeFunction(p2, p3, startPoint),
                edgeFunction(p3, p1, startPoint),
                edgeFunction(p1, p2, startPoint));
        Vec3 edx = new Vec3(p2.x - p1.x, p3.x - p2.x, p1.x - p3.x);
        Vec3 edy = new Vec3(p1.y - p2.y, p2.y - p3.y, p3.y - p1.y);

        // this rasterization method can potentially traverse many pixels that aren't inside the triangle
        // the advantage of this method, however, is its simplicity, as well as its speed
        // we only need to compute the edge function once, and pixels not within the triangle are computationally
        // very simple to recognize, meaning that our algorithm doesn't waste much time going over them
        // there are better methods that visit fewer pixels outside of the triangle, but this one is less of a headache
        for (int y = bbox.minY; y <= bbox.maxY; y++) {

            Vec3 edge = new Vec3(edgeRow);
            boolean hitPixel = false;

            for (int x = bbox.minX; x <= bbox.maxX; x++) {

                // checks whether or not this pixel is in the triangle
                if (edge.x >= 0 && edge.y >= 0 && edge.z >= 0) {
                    hitPixel = true;

                    // tests this pixel's depth against the depth buffer
                    float depth = (edge.mul(invArea)).dot(zVals);
                    if (depth < buffer.getDepth(x, y) && depth <= (1.0001f)) {
                        buffer.setDepth(x, y, depth);

                        // get the barycentric coordinates of this pixel
                        Vec3 edgePersp = edge.mul(wVals);
                        float invEdgeSum = 1 / (edgePersp.x + edgePersp.y + edgePersp.z);
                        // transform our edge values into clamped values in the range [0 -> 1]
                        Vec2 bCoords = new Vec2(edgePersp.y, edgePersp.z).mul(invEdgeSum);

                        // interpolate the vertex for this pixel
                        Vertex interpolated = Vertex.Interpolate(v1, v2, v3, bCoords.x, bCoords.y);

                        Color color = shader.fragment(interpolated);
                        buffer.setPixel(x, y, color);
                    }
                }
                else if (hitPixel) {
                    // if we have previously drawn a pixel in the triangle, and we encounter a pixel not in the triangle
                    // then we know we have reached the edge of the triangle, and can break the current loop
                    break;
                }

                edge.x += edy.y;
                edge.y += edy.z;
                edge.z += edy.x;
            }

            edgeRow.x += edx.y;
            edgeRow.y += edx.z;
            edgeRow.z += edx.x;
        }

        /*System.out.println(bbox);
        System.out.println("points: " + p1 + ", " + p2 + ", " + p3);
        System.out.println("invArea: " + invArea);
        System.out.println("Pixels drawn: " + numPixels);*/
    }

    private static float edgeFunction(Vec2 a, Vec2 b, Vec2 c) {
        return (b.x - a.x)*(c.y - a.y) - (b.y - a.y)*(c.x - a.x);
    }

    private static void viewportTransform(Framebuffer buffer, Vec2 p1, Vec2 p2, Vec2 p3) {
        __viewportTransform(buffer, p1);
        __viewportTransform(buffer, p2);
        __viewportTransform(buffer, p3);
    }

    private static void __viewportTransform(Framebuffer fb, Vec2 p) {
        p.x = (p.x + 1) * fb.width * 0.5f + 0.5f;
        p.y = (p.y + 1) * fb.height * 0.5f + 0.5f;
    }

    private static BBOX getBoundingBox(Framebuffer fb, Vec2 p1, Vec2 p2, Vec2 p3) {
        float[] xVals = new float[] { p1.x, p2.x, p3.x };
        float[] yVals = new float[] { p1.y, p2.y, p3.y };

        float minX = Math.max(0, getMin(xVals));
        float maxX = Math.min(fb.width - 1, getMax(xVals));
        float minY = Math.max(0, getMin(yVals));
        float maxY = Math.min(fb.height - 1, getMax(yVals));

        return new BBOX((int) minX, (int) maxX, (int) minY, (int) maxY);
    }

    private static float getMin(float[] vals) {
        float min = vals[0];
        for (int i = 1; i < vals.length; i++) {
            min = Math.min(vals[i], min);
        }
        return min;
    }

    private static float getMax(float[] vals) {
        float min = vals[0];
        for (int i = 1; i < vals.length; i++) {
            min = Math.max(vals[i], min);
        }
        return min;
    }
}
