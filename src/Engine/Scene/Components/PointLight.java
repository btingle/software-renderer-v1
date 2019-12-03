package Engine.Scene.Components;

import Application.Application;
import Datatypes.Color;
import Datatypes.Vec3;
import Engine.Graphics.RenderManager;
import Engine.Scene.Component;

public class PointLight extends Component {

    private Vec3 pos;
    public float intensity;
    public Color color;

    private int lightIndex;
    private RenderManager renderManager;

    public PointLight(float intensity, Color color) {
        this.intensity = intensity;
        this.color = color;
    }

    @Override
    protected void Start() {
        renderManager = Application.getRenderManager();
        pos = object.getTransform().getPosition();
        lightIndex = renderManager.addPointLight(pos, color, intensity);
    }

    @Override
    protected void LateUpdate() {
        pos = object.getTransform().getPosition();
        renderManager.setPointLight(lightIndex, pos, color, intensity);
    }

}
