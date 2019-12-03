package Game;

import Datatypes.Color;
import Datatypes.Vec3;
import Engine.Graphics.*;
import Engine.Scene.*;
import Engine.Scene.Components.*;

import java.io.IOException;

// initializes the game's scene
// decided that I'd rather hardcode the scene than try and create some clumsy system of loading scenes from a file
public class GameMain {
    
    public static void InitializeScene(Scene scene) {

        SceneObject lightObject, cameraObject, planeParent, planeObject, groundObject;
        Transform lightTransform, cameraTransform, planeParentTransform, planeTransform, groundTransform;
        Camera cameraComponent;
        LightSettings lightSettingsComponent;
        Renderer planeRender, groundRender, groundRender2;
        TerrainGenerator terrainGenerator;

        float near = 1f;
        float far = 150f;

        lightTransform = new Transform(new Vec3(0, 0, 0), new Vec3(60, 0, 0), Vec3.constant(1));
        lightObject = new SceneObject("Light", lightTransform, null);
        planeParentTransform = new Transform(new Vec3(0, -10, 0), new Vec3(0, 0, 0), Vec3.constant(1));
        planeParent = new SceneObject("Plane Parent", planeParentTransform, null);
        planeTransform = new Transform(new Vec3(0, 0, 0), new Vec3(0, 0, 180), Vec3.constant(1));
        planeObject = new SceneObject("Model", planeTransform, planeParent);
        cameraTransform = new Transform(new Vec3(0, -3, -10), new Vec3(0, 0, 0), Vec3.constant(1));
        cameraObject = new SceneObject("Camera", cameraTransform, planeParent);
        groundTransform = new Transform(new Vec3(0, 0, 0), new Vec3(0, 0, 0), Vec3.constant(1));
        groundObject = new SceneObject("Ground", groundTransform, null);

        cameraComponent = new Camera(0, false, (float) Math.toRadians(90), near, far);
        cameraObject.AddComponent(cameraComponent);

        lightSettingsComponent = new LightSettings(Color.white(), 0.4f, Color.white(), 1f, far/2, far, new Color(Vec3.constant(0.5f)));//new Color(0.251f, 0.666f, 0.92f));
        lightObject.AddComponent(lightSettingsComponent);

        try {
            Texture tex = new Texture("SopCamel (C).jpg");
            Shader shader = new PhongShader(tex, Color.white(), 1f, 64);
            Mesh mesh = Mesh.loadObj("planebasic.obj");
            planeRender = new Renderer(shader, mesh);

            Texture groundTex = new Texture("grass2.jpg");
            Texture groundTex2 = new Texture("dirt_9.png");
            Texture groundTex3 = new Texture("dirt.png");
            groundTex.setScale(0.1f, 0.1f);
            groundTex2.setScale(0.1f, 0.1f);
            groundTex3.setScale(0.1f, 0.1f);
            //Texture groundTex = new Texture(new Color(0.8f, 0.8f, 0.9f));
            Shader groundShader = new TriplanarTerrainShader(groundTex3, groundTex, groundTex3);
            //Shader groundShader = new FlatTerrainShader(groundTex, Color.white(), 0.5f, 16);
            terrainGenerator = new TerrainGenerator(planeTransform, 25f, 16, groundShader);
        }
        catch (IOException e) {
            System.out.println("Failed to load mesh or texture");
            return;
        }

        planeObject.AddComponent(planeRender);
        planeParent.AddComponent(new PlaneControl());
        groundObject.AddComponent(terrainGenerator);

        /*String[] cubemapFilenames = new String[] {
                "badomen_lf.jpg", "badomen_dn.jpg", "badomen_ft.jpg", "badomen_rt.jpg", "badomen_up.jpg", "badomen_bk.jpg"
        };
        Cubemap skybox = new Cubemap(cubemapFilenames);
        Skybox skyboxComponent = new Skybox(skybox);
        planeObject.AddComponent(skyboxComponent);*/

        scene.addSceneObject(lightObject);
        scene.addSceneObject(cameraObject);
        scene.addSceneObject(planeParent);
        scene.addSceneObject(planeObject);
        scene.addSceneObject(groundObject);
    }
    
}
