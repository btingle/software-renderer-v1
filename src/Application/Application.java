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

        String scoreText = "Player Score: 0", timeText = "Time = 100";
        String gameOver = "";
        int width, height;
        BufferedImage image;

        boolean paused = false;

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

            g2.setColor(Color.WHITE);

            if (!paused) {
                g2.scale(3, 3);
                g2.drawString(scoreText, 15, 30);
                g2.drawString(timeText, 15, 50);
            }
            else {
                g2.scale(2, 2);
                g2.drawString("PAUSED; Press P to un-pause", 15, 30);

                g2.drawString("Instructions:", 15, 70);
                g2.drawString("W/S: Accelerate Plane", 15, 90);
                g2.drawString("A/D: Roll Plane", 15, 110);
                g2.drawString("Up/Down: Tilt plane up/down", 15, 130);
                g2.drawString("Left/Right: Rotate plane left/right", 15, 150);

                g2.drawString("Objective:", 15, 190);
                g2.drawString("Kill as many turrets as possible before time runs out!", 15, 210);
            }
        }

        void updateScreen(Framebuffer buffer) {
            image.setRGB(0, 0, width, height, buffer.getRawBuffer(), 0, width);
            paint(getGraphics());
        }
    }

    private static boolean exitGame = false;
    private static ApplicationWindow window;

    public static void main(String[] args) {

        renderManager = new RenderManager();
        scene = new Scene();
        renderManager.addFrameBuffer(windowWidth, windowHeight);
        Framebuffer defaultFB = renderManager.getFramebuffer(0);

        GameMain.InitializeScene(scene);

        window = new ApplicationWindow("Game", windowWidth, windowHeight);
        window.paused = true;

        Input.Initialize(window);

        while ((!Input.GetKeyDown("Escape")) && !exitGame) {

            Input.Update();

            scene.UpdateScene();

            renderManager.Update();

            window.updateScreen(defaultFB);

            if (Input.GetKeyDown("P")) {
                window.paused = true;
            }
            if (window.paused) {
                window.paint(window.getGraphics());
                Input.Update();
                while (!Input.GetKeyDown("P")) {
                    Input.Update();
                    try {
                        Thread.sleep(1);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                window.paused = false;
            }
        }

        renderManager.terminate();
        window.timeText = "Game Over!";
        window.updateScreen(defaultFB);
    }

    public static void EndGame() {
        exitGame = true;
    }

    public static RenderManager getRenderManager() {
        return renderManager;
    }
    public static Framebuffer getFramebuffer() { return renderManager.getFramebuffer(0); }
    public static Scene getScene() { return scene; }

    // so... I don't want to use my renderer for UI stuff when swing already has that capability
    // So I'm using a sorta hacky method for the game UI and just stuffing the logic for it here
    // it's fine
    public static void setScore(int score) {
        window.scoreText = "Player Score: " + score;
    }

    public static void setTime(long time) {
        window.timeText = "Time: " + time;
    }

}
