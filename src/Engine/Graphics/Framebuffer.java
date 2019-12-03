package Engine.Graphics;

import Datatypes.Color;

public class Framebuffer {

    int[] pixelBuffer;
    float[] depthBuffer;

    public final int width;
    public final int height;

    public Framebuffer(int width, int height) {
        this.width = width;
        this.height = height;

        pixelBuffer = new int[width * height];
        depthBuffer = new float[width * height];
    }

    public int getPixel(int x, int y) {
        return pixelBuffer[y * width + x];
    }

    public void setPixel(int x, int y, int val) {
        pixelBuffer[y * width + x] = val;
    }

    public void setPixel(int x, int y, Color val) {
        pixelBuffer[y * width + x] = val.getRGB();
    }

    public float aspectRatio() {
        return (float) width / (float) height;
    }

    public float getDepth(int x, int y) {
        return depthBuffer[y * width + x];
    }

    public void setDepth(int x, int y, float val) {
        depthBuffer[y * width + x] = val;
    }

    public void clear(Color clearColor) {
        int rgb = clearColor.getRGB();
        for (int i = 0; i < width * height; i++) {
            pixelBuffer[i] = rgb;
            depthBuffer[i] = 9999;
        }
    }

    public int[] getRawBuffer() {
        return pixelBuffer;
    }

}
