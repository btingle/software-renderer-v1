package Game;

import Application.Application;
import Datatypes.Color;
import Datatypes.Vec2;
import Datatypes.Vec3;
import Engine.Graphics.Mesh;
import Engine.Graphics.PhongShader;
import Engine.Graphics.Shader;
import Engine.Graphics.Texture;
import Engine.Scene.Component;
import Engine.Scene.Components.Renderer;
import Engine.Scene.Scene;
import Engine.Scene.SceneObject;
import Engine.Scene.Transform;

import java.io.IOException;
import java.util.ArrayList;

public class TurretSpawner extends Component {

    private static class Turret {
        SceneObject base;
        SceneObject turret;
        Turret(SceneObject base, SceneObject turret) {
            this.base = base;
            this.turret = turret;
        }
    }

    Shader turretShader;
    Mesh turretMesh;
    Mesh turretBaseMesh;
    Transform target;

    private Scene scene;
    private int numSpawned = 0;
    private final int maxSpawn = 5;
    private final float maxSpawnRange = 50f;
    private final float minSpawnRange = 25f;

    public TurretSpawner(Transform target) {

        this.target = target;

        try {
            turretMesh = Mesh.loadObj("TurretO.obj");
            turretBaseMesh = Mesh.loadObj("TurretBase.obj");
        }
        catch (IOException e) {
            System.out.println("Failed to load turret mesh!");
        }

        Texture turretTexture = new Texture("metal.jpg");
        turretShader = new PhongShader(turretTexture, Color.white(), 1f, 32);
    }

    private ArrayList<Turret> spawnedTurrets;

    @Override
    protected void Start() {
        scene = Application.getScene();
        spawnedTurrets = new ArrayList<>();
    }

    @Override
    protected void Update() {
        Vec3 targetPos = target.getPosition();
        if (spawnedTurrets.size() < maxSpawn) {
            for (int i = 0; i < maxSpawn - spawnedTurrets.size(); i++) {
                double angle = Math.random() * Math.PI * 2;
                double x = Math.cos(angle);
                double z = Math.sin(angle);
                x *= randomRange(minSpawnRange, maxSpawnRange);
                z *= randomRange(minSpawnRange, maxSpawnRange);
                x += targetPos.x;
                z += targetPos.z;
                spawnedTurrets.add(spawnAATurret(new Vec2((float)x, (float)z)));
            }
            numSpawned = maxSpawn;
        }

        Vec2 targetXZ = new Vec2(targetPos.x, targetPos.z);
        for (int i = 0; i < spawnedTurrets.size(); i++) {
            Turret curr = spawnedTurrets.get(i);
            Vec3 turretPos = curr.base.getTransform().getPosition();
            Vec2 turretXZ = new Vec2(turretPos.x, turretPos.z);

            if (Vec2.distance(targetXZ, turretXZ) > maxSpawnRange*1.5f) {
                killTurret(curr);
                spawnedTurrets.remove(i);
                i--; // because curr will have been removed from the list
                //System.out.println("Culled turret, d=" + Vec2.distance(targetXZ, turretXZ));
            }
        }
    }

    final int baseShootrate = 60;

    private Turret spawnAATurret(Vec2 worldPos) {
        Vec3 position = new Vec3(worldPos.x, TerrainGenerator.height(worldPos.x, worldPos.y) - 4f, worldPos.y);
        Transform baseTransform = new Transform(position, Vec3.zero(), Vec3.constant(2));
        Transform turretTransform = new Transform(position, Vec3.zero(), Vec3.constant(2));
        SceneObject baseObject = new SceneObject("TurretBase", baseTransform, null);
        SceneObject turretObject = new SceneObject("Turret", turretTransform, null);

        Renderer turretRender = new Renderer(turretShader, turretMesh);
        Renderer turretBaseRender = new Renderer(turretShader, turretBaseMesh);

        turretObject.AddComponent(turretRender);
        turretObject.AddComponent(new Looker(target));
        int shootRate = baseShootrate + (int)Math.round((Math.random() * baseShootrate));
        turretObject.AddComponent(new Shooter(shootRate, Projectile.PlayerLayer, 1));

        Turret turret = new Turret(baseObject, turretObject);

        baseObject.AddComponent(turretBaseRender);
        CollisionBox baseBox = new CollisionBox(turretBaseMesh.getMin(), turretBaseMesh.getMax(), Projectile.EnemyLayer);
        baseBox.setCollisionCallback(new Runnable() {
            @Override
            public void run() {
                killTurret(turret);
                PlaneControl.IncrementScore();
            }
        });
        baseObject.AddComponent(baseBox);

        //System.out.println("Spawned turret @" + position);

        scene.addSceneObject(baseObject);
        scene.addSceneObject(turretObject);

        return turret;
    }

    private static double randomRange(double min, double max) {
        return min + Math.random() * (max - min);
    }

    private void killTurret(Turret turret) {
        scene.removeSceneObject(turret.base);
        scene.removeSceneObject(turret.turret);
    }

}
