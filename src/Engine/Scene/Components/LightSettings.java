package Engine.Scene.Components;

import Application.Application;
import Datatypes.Color;
import Datatypes.Vec3;
import Engine.Graphics.RenderManager;
import Engine.Scene.Component;

public class LightSettings extends Component {

    public Color ambientColor;
    public float ambientIntensity;

    public Color directionalColor;
    public float directionalIntensity;

    public float fogMin, fogMax;
    public Color fogColor;

    private RenderManager renderManager;

    public LightSettings(Color ambientColor, float ambientIntensity, Color directionalColor, float directionalIntensity, float fogMin, float fogMax, Color fogColor) {
        this.ambientColor = ambientColor;
        this.ambientIntensity = ambientIntensity;
        this.directionalColor = directionalColor;
        this.directionalIntensity = directionalIntensity;
        this.fogMin = fogMin;
        this.fogMax = fogMax;
        this.fogColor = fogColor;
    }

    @Override
    protected void Start() {
        renderManager = Application.getRenderManager();
        LateUpdate();
    }

    @Override
    protected void LateUpdate() {
        // use transform rotation as direction for directional light
        Vec3 direction = object.getTransform().forward();
        renderManager.setAmbientLight(ambientColor, ambientIntensity);
        renderManager.setDirectionalLight(direction, directionalColor, directionalIntensity);
        renderManager.setFog(fogMin, fogMax, fogColor);
        renderManager.setClearColor(fogColor);
    }

}
