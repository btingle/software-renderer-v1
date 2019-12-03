package Game;

import Datatypes.Vec3;
import Engine.Input;
import Engine.Scene.Component;
import Engine.Scene.Transform;

public class PlaneControl extends Component {

    private int pitch = 0;
    private int roll = 0;

    float mass = 1f;

    float pitchForce = 0;
    float rollForce = 0;

    float rotationVelocity = 4f;
    float forwardVelocity = 0.5f;

    private Transform transform;
    private Transform planeTransform;

    protected void Start() {
        transform = object.getTransform();
        planeTransform = object.getChild(0).getTransform();
    }

    @Override
    protected void Update() {

        if (Input.GetKeyDown("Up")) {
            pitch = -1;
        }
        if (Input.GetKeyDown("Down")) {
            pitch = 1;
        }
        if (Input.GetKeyUp("Down") || Input.GetKeyUp("Up")) {
            pitch = 0;
        }

        if (Input.GetKeyDown("A")) {
            roll = -1;
        }
        if (Input.GetKeyDown("D")) {
            roll = 1;
        }
        if (Input.GetKeyUp("A") || Input.GetKeyUp("D")) {
            roll = 0;
        }

        pitchForce += pitch * 0.5f;
        rollForce += roll * 0.5f;

        pitchForce *= 0.9f;
        rollForce *= 0.9f;

        //transform.setLocalRotation(transform.getLocalRotation().add(new Vec3(pitch * rotationVelocity, 0, 0)));
        transform.translate(transform.forward().mul(-forwardVelocity));
        transform.rotate(new Vec3(pitchForce/mass, 0, rollForce/mass));

        Vec3 pos = transform.getPosition();

        if (pos.y > TerrainGenerator.height(pos.x, pos.z)) {
            transform.setLocalPosition(new Vec3(0, -10, 0));
            transform.setLocalRotation(Vec3.zero());
        }
        //transform.setLocalPosition(transform.getLocalPosition().add(transform.forward().mul(-forwardVelocity)));
        //transform.setLocalRotation(transform.getLocalRotation().add(new Vec3(0, roll * rotationVelocity, 0)));
    }

}
