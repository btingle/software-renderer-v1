package Game;

import Datatypes.Vec2;
import Datatypes.Vec3;
import Engine.Input;
import Engine.Scene.Component;
import Engine.Scene.Transform;

public class FreeCam extends Component {

    public float speed;

    Transform transform;
    float forwardMovement;
    float backwardsMovement;

    public FreeCam(float speed) {
        this.speed = speed;
    }

    @Override
    protected void Start() {
        transform = object.getTransform();
        forwardMovement = 0;
        backwardsMovement = 0;
    }

    @Override
    protected void Update() {
        if (Input.GetKeyDown("W")) {
            forwardMovement = 1;
        }
        if (Input.GetKeyUp("W")) {
            forwardMovement = 0;
        }
        if (Input.GetKeyDown("S")) {
            backwardsMovement = 1;
        }
        if (Input.GetKeyUp("S")) {
            backwardsMovement = 0;
        }

        float movement = (forwardMovement - backwardsMovement) * speed;
        //System.out.println("movement: " + movement);

        if (movement != 0) {
            transform.setLocalPosition(
                    transform.getLocalPosition().add(
                            transform.forward().mul(movement)));
        }

        Vec2 mouseMovment = Input.GetMouseDrag();

        transform.rotate(new Vec3(mouseMovment.x, 0, mouseMovment.y));
    }

}
