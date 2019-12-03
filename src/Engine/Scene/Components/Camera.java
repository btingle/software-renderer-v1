package Engine.Scene.Components;

import Application.Application;
import Datatypes.Mat4;
import Datatypes.Vec3;
import Datatypes.Vec4;
import Engine.Graphics.Framebuffer;
import Engine.Graphics.RenderManager;
import Engine.Scene.Component;
import Engine.Scene.Transform;

public class Camera extends Component {

    public int frameBuffer;
    public boolean orthographic;
    public float fov, near, far;

    private int cameraIndex;
    private RenderManager renderManager;
    private Transform transform;
    private Mat4 view, proj;
    private Vec4[] frustumPlanes;

    public Camera(int framebuffer, boolean orthographic, float fov, float near, float far) {
        this.frameBuffer = framebuffer;
        this.orthographic = orthographic;
        this.fov = fov;
        this.near = near;
        this.far = far;
        frustumPlanes = new Vec4[6];
    }

    @Override
    protected void Start() {
        transform = object.getTransform();
        renderManager = Application.getRenderManager();
        cameraIndex = renderManager.addCamera(Mat4.Identity(), Mat4.Identity(), frameBuffer);

        Framebuffer fb = renderManager.getFramebuffer(frameBuffer);
        float aspect = (float) fb.width / (float) fb.height;

        if (orthographic) {
            proj = Mat4.orthographic(aspect, near, far);
        }
        else {
            proj = Mat4.perspective(fov, aspect, near, far);
        }

        LateUpdate();
    }

    @Override
    protected void LateUpdate() {

        //transform.setLocalRotation(transform.getLocalRotation().add(new Vec3(0, 2f, 1f)));
        //System.out.println(transform.getLocalRotation());

        Transform t = object.getTransform();
        Vec3 eye = t.getPosition();
        Vec3 dir = t.forward();
        Vec3 center = eye.add(dir);
        Vec3 up = t.up();
        view = Mat4.lookAt(eye, center, up);

        renderManager.setCamera(cameraIndex, view, proj, frameBuffer);
    }
}
