package Engine.Scene.Components;

import Datatypes.Vec3;
import Engine.Scene.Component;
import Engine.Scene.Transform;

public class Mover extends Component {

    Transform transform;

    Vec3 movementPerFrame, rotationPerFrame;
    public Mover(Vec3 m, Vec3 r) {
        movementPerFrame = m;
        rotationPerFrame = r;
    }

    @Override
    protected void Start() {
        transform = object.getTransform();
    }

    protected void Update() {
        //transform.setLocalRotation(transform.getLocalRotation().add(rotationPerFrame));
        //transform.setLocalPosition(transform.getLocalPosition().add(movementPerFrame));
    }

}
