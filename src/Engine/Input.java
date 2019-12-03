package Engine;

import Datatypes.Vec2;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Queue;

public class Input {

    private static ArrayList<String> frameKeyDowns;
    private static ArrayList<String> frameKeyUps;
    private static ArrayList<String> currentKeyUps;
    private static ArrayList<String> currentKeyDowns;
    private static boolean mouseInWindow = false;
    private static boolean mouseDown = false;
    private static boolean _mouseDown = false;
    private static float mouseX = 0;
    private static float _mouseX = 0;
    private static float mouseY = 0;
    private static float _mouseY = 0;

    private static JFrame applicationWindow;

    public static void Initialize(JFrame _applicationWindow) {

        applicationWindow = _applicationWindow;

        applicationWindow.addKeyListener(new KeyListener() {
            @Override
            public void keyTyped(KeyEvent e) {

            }

            @Override
            public void keyPressed(KeyEvent e) {
                String key = KeyEvent.getKeyText(e.getKeyCode());
                Input.frameKeyDowns.add(key);
            }

            @Override
            public void keyReleased(KeyEvent e) {
                String key = KeyEvent.getKeyText(e.getKeyCode());
                Input.frameKeyUps.add(key);
            }
        });

        MouseListener mListener = new MouseListener() {
            @Override
            public void mouseClicked(MouseEvent e) {

            }
            @Override
            public void mousePressed(MouseEvent e) {
                Input.mouseDown = true;
            }
            @Override
            public void mouseReleased(MouseEvent e) {
                Input.mouseDown = false;
            }
            @Override
            public void mouseEntered(MouseEvent e) {
                Input.mouseInWindow = true;
            }
            @Override
            public void mouseExited(MouseEvent e) {
                Input.mouseInWindow = false;
            }
        };

        applicationWindow.addMouseListener(mListener);

        frameKeyUps = new ArrayList<>();
        frameKeyDowns = new ArrayList<>();

        currentKeyUps = new ArrayList<>();
        currentKeyDowns = new ArrayList<>();
    }

    public static void Update() {
        currentKeyUps.clear();
        currentKeyDowns.clear();

        currentKeyUps.addAll(frameKeyUps);
        currentKeyDowns.addAll(frameKeyDowns);

        frameKeyDowns = new ArrayList<>();
        frameKeyUps = new ArrayList<>();

        UpdateMousePosition();
    }

    public static boolean GetKeyDown(String key) {
        return currentKeyDowns.contains(key);
    }

    public static boolean GetKeyUp(String key) {
        return currentKeyUps.contains(key);
    }

    public static void UpdateMousePosition() {
        _mouseX = mouseX;
        _mouseY = mouseY;
        _mouseDown = mouseDown;
        mouseX = MouseInfo.getPointerInfo().getLocation().x - applicationWindow.getLocationOnScreen().x;
        mouseY = MouseInfo.getPointerInfo().getLocation().y - applicationWindow.getLocationOnScreen().y;
    }

    public static Vec2 GetMousePosition() {
        return new Vec2(mouseX, mouseY);
    }

    public static Vec2 GetMouseDrag() {
        // if mouse button has been held down between frames, as well as whether or not the mouse is in the window
        if (mouseInWindow && mouseDown && _mouseDown) {
            return new Vec2(mouseX - _mouseX, mouseY - _mouseY);
        }
        return Vec2.zero();
    }

    public static boolean GetMouseDown() {
        return mouseDown;
    }
}
