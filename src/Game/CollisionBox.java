package Game;

import Datatypes.Mat4;
import Datatypes.Vec3;
import Engine.Graphics.Mesh;
import Engine.Scene.Component;
import Engine.Scene.Components.Renderer;
import Engine.Scene.Transform;

import javax.swing.*;

public class CollisionBox extends Component {

    private Vec3 min, max;
    private Transform transform;
    private final int layer;
    private boolean terrainCollisions = false;

    private Vec3[] bbox = new Vec3[8];
    private Vec3[] norms = new Vec3[3];

    private Runnable callback;

    public CollisionBox(Vec3 min, Vec3 max, int layer) {
        this.min = min;
        this.max = max;
        this.layer = layer;
    }

    protected void Start() {
        transform = object.getTransform();
    }

    protected void Update() {
        Mat4 model = transform.getGlobalMatrix();
        Vec3 tMin = model.multiply(min);
        Vec3 tMax = model.multiply(max);

        calcBbox();
        boolean didCallback = false;
        for (Projectile p : Projectile.getProjectiles(layer)) {
            if (checkCollision(p.transform.getPosition())) {
                //System.out.println(object.getName() + "'s BBOX was collided with!");
                p.dispose();
                if (!didCallback && callback != null) {
                    didCallback = true;
                    callback.run();
                }
            }
        }

        if (terrainCollisions) {
            if (checkTerrainCollision()) {
                object.getParent().getTransform().setLocalPosition(new Vec3(0, -10, 0));
                //System.out.println(object.getName() + "'s BBOX collided terrain");
                if (!didCallback && callback != null) {
                    callback.run();
                }
            }
        }
    }

    public void setCollisionCallback(Runnable callback) {
        this.callback = callback;
    }

    void calcBbox() {
        Mat4 model = transform.getGlobalMatrix();

        bbox[0] = model.multiply(new Vec3(min.x, min.y, min.z));
        bbox[1] = model.multiply(new Vec3(min.x, min.y, max.z));
        bbox[2] = model.multiply(new Vec3(min.x, max.y, min.z));
        bbox[3] = model.multiply(new Vec3(min.x, max.y, max.z));
        bbox[4] = model.multiply(new Vec3(max.x, min.y, min.z));
        bbox[5] = model.multiply(new Vec3(max.x, min.y, max.z));
        bbox[6] = model.multiply(new Vec3(max.x, max.y, min.z));
        bbox[7] = model.multiply(new Vec3(max.x, max.y, max.z));

        norms[0] = model.multiplyDir(new Vec3(0, 0, 1));
        norms[1] = model.multiplyDir(new Vec3(0, 1, 0));
        norms[2] = model.multiplyDir(new Vec3(1, 0, 0));
    }

    public void enableTerrainCollisions() {
        terrainCollisions = true;
    }

    private boolean checkTerrainCollision() {
        for (int v = 0; v < 8; v++) {
            Vec3 vert = bbox[v];
            float height = TerrainGenerator.height(vert.x, vert.z);
            if ((vert.y - height) > 2) {
                System.out.println(vert.y - height);
                return true;
            }
        }
        return false;
    }

    private boolean checkCollision(Vec3 point) {
        for (int n = 0; n < 3; n++) {
            Vec3 norm = norms[n];

            float minProj = bbox[0].dot(norm);
            float maxProj = bbox[0].dot(norm);

            for (int i = 1; i < 8; i++) {
                float proj = bbox[i].dot(norm);
                minProj = Math.min(minProj, proj);
                maxProj = Math.max(maxProj, proj);
            }

            float ptProj = norm.dot(point);
            if (ptProj < minProj || ptProj > maxProj) {
                return false;
            }
        }
        return true;
    }

}
