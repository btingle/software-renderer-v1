package Game;

import Application.Application;
import Datatypes.Color;
import Datatypes.Vec3;
import Engine.Graphics.GouraudShader;
import Engine.Graphics.Mesh;
import Engine.Graphics.Shader;
import Engine.Graphics.Texture;
import Engine.Scene.Component;
import Engine.Scene.Components.Renderer;
import Engine.Scene.Scene;
import Engine.Scene.SceneObject;
import Engine.Scene.Transform;

import java.io.IOException;

public class Shooter extends Component {

    static Mesh projectileMesh;
    static Shader projectileShader;
    static {
        try {
            Texture projectileTexture = new Texture(Color.black());

            projectileShader = new GouraudShader(projectileTexture, Color.white(), 1f, 32);
            projectileMesh = Mesh.loadObj("sphere.obj");
        }
        catch (IOException e) {
            System.out.println("Failed to load projectile data!");
        }
    }

    private final int frequency, layer;
    private final float speed;
    private Scene scene;

    public Shooter(int freq, int layer, float speed) {
        this.frequency = freq;
        this.layer = layer;
        this.speed = speed;
    }

    protected void Start() {
        scene = Application.getScene();
    }

    private int timer = 0;
    private SceneObject lastProjectile = null;

    protected void Update() {

        if (timer >= frequency) {
            spawnProjectile();
            timer = 0;
        }
        else {
            timer++;
        }

        /*if (object.getName() == "Model" && lastProjectile != null && lastProjectile.isEnabled()) {
            System.out.println(object.getName() + "'s projectile position:" + lastProjectile.getTransform().getPosition());
            System.out.println("Model's position:" + object.getTransform().getPosition());
        }*/
    }

    void spawnProjectile() {
        //System.out.println(object.getName() + " shot projectile");
        Transform projectileTransform = new Transform(object.getTransform());
        SceneObject projectileObject = new SceneObject("Projectile", projectileTransform, null);
        projectileTransform.scale(Vec3.constant(0.1f));
        projectileObject.AddComponent(new Renderer(projectileShader, projectileMesh));
        projectileObject.AddComponent(new Projectile(30 * 10, layer, 10f * speed));

        scene.addSceneObject(projectileObject);
        lastProjectile = projectileObject;
    }

}
