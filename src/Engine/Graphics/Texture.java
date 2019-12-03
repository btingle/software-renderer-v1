package Engine.Graphics;

import Datatypes.Color;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class Texture {

    int[] texture;
    int width, height;
    float scaleX, scaleY;

    Color color;

    public Texture(String fileName) {
        scaleX = scaleY = 1;
        try {
            BufferedImage img = ImageIO.read(new File(fileName));
            width = img.getWidth();
            height = img.getHeight();
            texture = img.getRGB(0, 0, width ,height, null, 0, width);
        }
        catch (IOException e) {
            System.out.println("Failed to load " + fileName + " texture file. Using a blank texture instead.");
            texture = null;
            color = new Color(255, 255, 255);
        }
    }

    public void setScale(float sx, float sy) {
        scaleX = sx;
        scaleY = sy;
    }

    public Texture(Color color) {
        scaleX = scaleY = 1;
        this.color = color;
        texture = null;
    }

    public int getPixelRaw(float u, float v) {
        if (texture == null) return color.getRGB();
        int index = ((int) (v * scaleY * height) % height) * width + ((int) (u * scaleX * width) % width);
        if (index < 0) return 0;
        return texture[index];
    }

    public Color getPixel(float u, float v) {
        int pixelVal = getPixelRaw(u, v);
        return new Color(pixelVal);
    }

    public void setPixel(float u, float v, int color) {
        int index = ((int) (v * scaleY * height) % height) * width + ((int) (u * scaleX * width) % width);
        texture[index] = color;
    }

}
