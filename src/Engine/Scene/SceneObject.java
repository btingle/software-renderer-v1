package Engine.Scene;

import Datatypes.Vec3;

import java.util.ArrayList;

public class SceneObject extends SceneBehaviour {

    private String name;
    private Transform transform;
    private SceneObject parent;
    private ArrayList<SceneObject> children;
    private ArrayList<Component> components;

    public SceneObject(String name, Transform transform, SceneObject parent) {
        this.name = name;
        this.transform = transform;
        this.transform.object = this;
        this.setParent(parent);
        this.children = new ArrayList();
        this.components = new ArrayList();
    }

    public SceneObject() {
        this("SceneObject", new Transform(), null);
    }

    public SceneObject(String name, Vec3 position, Vec3 rotation, Vec3 scale, SceneObject parent) {
        this(name, new Transform(position, rotation, scale), parent);
    }

    @Override
    protected void Start() {
        components.forEach(c -> c.Start());
    }

    @Override
    protected void Update() {
        components.forEach(c -> c.Update());
    }

    @Override
    protected void LateUpdate() {
        components.forEach(c -> c.LateUpdate());
    }

    public void AddComponent(Component component) {
        components.add(component);
        component.object = this;
    }

    public Component GetComponent(Class type) {
        for (Component c : components) {
            if (c.getClass() == type) {
                return c;
            }
        }
        return null;
    }

    public String getName() {
        return name;
    }

    public Transform getTransform() {
        return transform;
    }

    public SceneObject getParent() {
        return parent;
    }

    public int childCount() {
        return children.size();
    }

    public SceneObject getChild(int index) {
        return children.get(index);
    }

    public void setParent(SceneObject parent) {
        if (parent == null) return;

        if (this.parent != null) {
            this.parent.children.remove(this);
        }
        this.parent = parent;
        parent.children.add(this);
    }
}
