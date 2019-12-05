package Game;

import Engine.Scene.Component;
import Engine.Scene.Transform;

public class Looker extends Component {

    Transform target;

    public Looker(Transform target) {
        this.target = target;
    }

    @Override
    protected void Update() {
        object.getTransform().lookAt(target.getPosition());
    }

}
