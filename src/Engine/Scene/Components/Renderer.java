package Engine.Scene.Components;

import Application.Application;
import Engine.Graphics.Mesh;
import Engine.Graphics.RenderManager;
import Engine.Graphics.Shader;
import Engine.Scene.Component;
import Engine.Scene.Transform;

public class Renderer extends Component {

    public Shader shader;
    public Mesh mesh;

    private RenderManager renderManager;
    private Transform transform;

    public Renderer(Shader shader, Mesh mesh) {
        this.shader = shader;
        this.mesh = mesh;
    }

    @Override
    protected void Start() {
        renderManager = Application.getRenderManager();
        transform = object.getTransform();
        LateUpdate();
    }

    @Override
    protected void LateUpdate() {
        if (mesh == null) return;
        //transform.setLocalRotation(transform.getLocalRotation().add(new Vec3(0, 0, 1)));
        //transform.setLocalPosition(transform.getLocalPosition().add(new Vec3(0, 0, 0.05f)));
        renderManager.pushDrawJob(mesh, shader, transform.getGlobalMatrix());
    }

}
