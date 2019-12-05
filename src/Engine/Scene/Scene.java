package Engine.Scene;

import java.util.ArrayList;

public class Scene extends SceneBehaviour {

    ArrayList<SceneObject> objects;
    ArrayList<SceneObject> toAdd, toRemove;

    public Scene() {
        objects = new ArrayList();
        toAdd = new ArrayList<>();
        toRemove = new ArrayList<>();
    }

    public void addSceneObject(SceneObject obj) {
        toAdd.add(obj);
    }

    public void removeSceneObject(SceneObject obj) {
        toRemove.add(obj);
    }

    public void UpdateScene() {

        //System.out.println("No objects:" + objects.size());

        ArrayList<SceneObject> added = new ArrayList(toAdd);
        added.forEach(o -> o.enabled = true);
        toAdd.clear();

        added.forEach(o -> o.Start());
        objects.forEach(o -> o.Update());
        objects.forEach(o -> o.LateUpdate());

        objects.removeAll(toRemove);
        objects.addAll(added);

        toRemove.forEach(o -> o.enabled = false);
        toRemove.clear();
    }

    /*@Override
    public void Start() {
        updateScene();
        for (SceneObject object : objects) {
            object.Start();
        }
    }

    @Override
    public void Update() {
        updateScene();
        for (SceneObject object : objects) {
            object.Update();
        }
    }

    @Override
    public void LateUpdate() {
        for (SceneObject object : objects) {
            object.LateUpdate();
        }
    }*/

}
