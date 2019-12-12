package Game;

import Application.Application;
import Datatypes.Vec3;
import Engine.Input;
import Engine.Scene.Component;
import Engine.Scene.Transform;

// component that handles player control of the plane
// also responsible for managing player score and time left
public class PlaneControl extends Component {

    private static class MutableInt {
        int value;
        public MutableInt(int val) {
            this.value = val;
        }
    }

    // the various axes of movement that are controlled by the player's keyboard input
    private MutableInt pitch = new MutableInt(0);
    private MutableInt roll = new MutableInt(0);
    private MutableInt yaw = new MutableInt(0);
    private MutableInt forward = new MutableInt(0);

    float mass = 0.9f;

    // the current force on the various movement axes that are controlled by player input
    float yawForce = 0;
    float pitchForce = 0;
    float rollForce = 0;
    float extraForce = 0;

    float forwardVelocity = 0.55f;

    private Transform transform;

    private static final long GameLength = 3000; // game lasts 3000 frames, or roughly 2.5 minutes at 20fps
    private long timeLeft;

    protected void Start() {
        transform = object.getTransform();

        timeLeft = GameLength;

        CollisionBox bbox = (CollisionBox) object.GetComponent(CollisionBox.class);
        bbox.setCollisionCallback(new Runnable() {
            @Override
            public void run() {

                die();
            }
        });
    }

    @Override
    protected void Update() {

        if (Input.GetKeyDown("R")) {
            die();
            Application.setScore(0);
            timeLeft = GameLength;
            playerScore = 0;
            Application.setTime(GameLength);
            Application.PauseGame();
        }

        updateKeyState("Up", "Down", pitch);
        updateKeyState("A", "D", roll);
        updateKeyState("S", "W", forward);
        updateKeyState("Left", "Right", yaw);

        pitchForce += pitch.value * 0.5f;
        rollForce += roll.value * 0.5f;
        extraForce += forward.value * 0.25f;
        yawForce += yaw.value * 0.2f;

        yawForce *= 0.9f;
        pitchForce *= 0.9f;
        rollForce *= 0.9f;
        extraForce *= 0.3f;

        transform.translate(transform.forward().mul(-(forwardVelocity + (extraForce/mass))));
        transform.rotate(new Vec3(pitchForce/mass, yawForce/mass, rollForce/mass));

        Vec3 pos = transform.getPosition();

        // checks for collisions with terrain
        if (pos.y > TerrainGenerator.height(pos.x, pos.z)) {
            die();
        }

        timeLeft --;
        Application.setTime(timeLeft);

        if (timeLeft < 0) {
            Application.EndGame();
        }
    }

    private void die() {
        transform.setLocalPosition(new Vec3(0, -10, 0));
        transform.setLocalRotation(Vec3.zero());
        pitchForce = rollForce = extraForce = yawForce = 0;
        playerScore--;
        Application.setScore(playerScore);
    }

    private void updateKeyState(String keyA, String keyB, MutableInt axis) {
        if (Input.GetKeyDown(keyA)) {
            axis.value = -1;
        }
        if (Input.GetKeyDown(keyB)) {
            axis.value = 1;
        }
        if (Input.GetKeyUp(keyA) || Input.GetKeyUp(keyB)) {
            axis.value = 0;
        }
    }

    static int playerScore = 0;
    static void IncrementScore() {
        playerScore++;
        Application.setScore(playerScore);
    }

}
