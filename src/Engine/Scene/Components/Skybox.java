package Engine.Scene.Components;

import Application.Application;
import Engine.Graphics.Cubemap;
import Engine.Scene.Component;

public class Skybox extends Component {

    Cubemap skybox;

    public Skybox(Cubemap skybox) {
        this.skybox = skybox;
    }

    @Override
    protected void Start() {
        Application.getRenderManager().setSkybox(skybox);
    }

}
