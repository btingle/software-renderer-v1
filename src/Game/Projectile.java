package Game;

import Application.Application;
import Datatypes.Vec3;
import Engine.Scene.Component;
import Engine.Scene.Scene;
import Engine.Scene.Transform;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.function.Consumer;

public class Projectile extends Component {

    public static final int PlayerLayer = 0;
    public static final int EnemyLayer = 1;

    static HashMap<Integer, ArrayList<Projectile>> projectileMap = new HashMap<>();

    public static ArrayList<Projectile> getProjectiles(int layer) {
        if (projectileMap.get(layer) == null) projectileMap.put(layer, new ArrayList<>());

        return projectileMap.get(layer);
    }

    static boolean CheckForBoxCollision(Vec3 boxMin, Vec3 boxMax, int layer) {

        ArrayList<Projectile> projectiles = projectileMap.get(layer);
        if (projectiles == null) return false;

        for (Projectile proj : projectiles) {
            Vec3 pos = proj.transform.getPosition();
            if (pos.greaterThan(boxMin) && pos.lessThan(boxMax)) {
                proj.dispose();
                return true;
            }
        }
        return false;
    }

    private static void addProjectile(Projectile p) {
        ArrayList<Projectile> layerProjectiles = projectileMap.get(p.layer);
        if (layerProjectiles != null) {
            layerProjectiles.add(p);
        }
        else {
            layerProjectiles = new ArrayList<>();
            layerProjectiles.add(p);
            projectileMap.put(p.layer, layerProjectiles);
        }
    }

    private static void removeProjectile(Projectile p) {
        ArrayList<Projectile> layerProjectiles = projectileMap.get(p.layer);
        if (layerProjectiles != null) {
            layerProjectiles.remove(p);
        }
    }

    private Scene scene;
    private boolean destroy;
    public Transform transform;
    private final long frameLifetime;
    private long currentLifetime;
    private final int layer;
    private final float speed;

    public Projectile(long frameLifetime, int layer, float speed) {
        this.frameLifetime = frameLifetime;
        this.layer = layer;
        this.speed = speed;
    }

    protected void Start() {
        scene = Application.getScene();
        transform = object.getTransform();
        addProjectile(this);
    }

    protected void Update() {
        if (destroy || currentLifetime >= frameLifetime || terrainCollision()) {
            destroy();
        }
        else {
            transform.translate(transform.forward().mul(-speed));
        }
        currentLifetime++;
    }

    private boolean terrainCollision() {
        Vec3 pos = transform.getPosition();
        return pos.y > TerrainGenerator.height(pos.x, pos.z);
    }

    private void destroy() {
        scene.removeSceneObject(object);
        removeProjectile(this);
    }

    public void dispose() {
        destroy = true;
    }

}
