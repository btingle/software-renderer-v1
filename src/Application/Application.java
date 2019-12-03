package Application;

import Engine.Input;
import Game.GameMain;
import Engine.Graphics.Framebuffer;
import Engine.Graphics.RenderManager;
import Engine.Scene.Scene;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;

public class Application {

    final static int windowWidth = 800;
    final static int windowHeight = 600;

    private static RenderManager renderManager;
    private static Scene scene;

    private static class ApplicationWindow extends JFrame {

        int width, height;
        BufferedImage image;

        public ApplicationWindow(String name, int width, int height) {
            super(name);

            image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);

            this.width = width;
            this.height = height;

            setFocusable(true);
            requestFocus();

            setSize(width, height);
            setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            setVisible(true);
        }

        @Override
        public void paint(Graphics g) {
            Graphics2D g2 = (Graphics2D) g;
            g2.drawImage(image, 0, 0, null);
        }

        void updateScreen(Framebuffer buffer) {
            image.setRGB(0, 0, width, height, buffer.getRawBuffer(), 0, width);
            paint(getGraphics());
        }
    }

    public static void main(String[] args) {

        renderManager = new RenderManager();
        scene = new Scene();
        renderManager.addFrameBuffer(windowWidth, windowHeight);
        Framebuffer defaultFB = renderManager.getFramebuffer(0);

        GameMain.InitializeScene(scene);

        ApplicationWindow window = new ApplicationWindow("Game", windowWidth, windowHeight);

        Input.Initialize(window);

        while (true) {
            long start = System.currentTimeMillis();

            Input.Update();

            scene.UpdateScene();

            long startRender = System.currentTimeMillis();
            renderManager.Update();
            long endRender = System.currentTimeMillis();
            long renderTime = endRender - startRender;

            window.updateScreen(defaultFB);

            long end = System.currentTimeMillis();
            System.out.println("Took " + (end - start) + " millis to complete frame! FPS: " + (1000 / (end - start)));
            //System.out.println("Took " + renderTime + " millis to complete render tasks, " + ((end - start) - renderTime) + " millis for non-render tasks");
        }
    }

    public static RenderManager getRenderManager() {
        return renderManager;
    }
    public static Framebuffer getFramebuffer() { return renderManager.getFramebuffer(0); }
    public static Scene getScene() { return scene; }

}
