package Engine.Scene;

import Datatypes.*;

public class Transform extends Component {

    private Vec3 localPosition;
    private Vec3 localRotation;
    private Vec3 localScale;

    private Mat4 translation;
    private Mat4 rotation;
    private Mat4 scaling;

    // flags whether or not the global/local matrix of this transform should be re-calculated
    // saves on unnecessary re-calculations of transform matrix
    private boolean calculateGlobalMatrix, calculateLocalMatrix;
    private Mat4 globalMatrix, localMatrix;

    public Transform(Mat4 translation, Mat4 rotation, Mat4 scaling) {
        this.translation = translation;
        this.rotation = rotation;
        this.scaling = scaling;

        calculateGlobalMatrix = calculateLocalMatrix = true;
    }

    public Transform(Vec3 position, Vec3 rotation, Vec3 scale) {
        this(Mat4.translation(position), Mat4.rotation(rotation), Mat4.scaling(scale));
    }

    public Transform() {
        this(Vec3.zero(), Vec3.zero(), Vec3.constant(1));
    }

    public Transform(Transform other) {
        this(new Mat4(other.translation), new Mat4(other.rotation), new Mat4(other.scaling));
    }

    public Mat4 getLocalMatrix() {
        if (calculateLocalMatrix) {
            localMatrix = translation.multiply(rotation).multiply(scaling);
            //localMatrix = Mat4.translation(localPosition).multiply(Mat4.rotation(localRotation)).multiply(Mat4.scaling(localScale));
            calculateLocalMatrix = false;
        }
        return localMatrix;
    }

    public Mat4 getGlobalMatrix() {
        if (calculateGlobalMatrix) {
            SceneObject parent = object.getParent();
            if (parent != null)
                globalMatrix = parent.getTransform().getGlobalMatrix().multiply(getLocalMatrix());
            else
                globalMatrix = getLocalMatrix();
            calculateGlobalMatrix = false;
        }
        return globalMatrix;
    }

    public void setLocalPosition(Vec3 value) {
        translation = Mat4.translation(value);
        //localPosition = value;
        calculateLocalMatrix = true;
        toggleMatrixCalculateFlag();
    }

    public void setLocalRotation(Vec3 value) {
        rotation = Mat4.rotation(value);
        //localRotation = value;
        calculateLocalMatrix = true;
        toggleMatrixCalculateFlag();
    }

    public void setLocalScale(Vec3 value) {
        scaling = Mat4.scaling(value);
        //localScale = value;
        calculateLocalMatrix = true;
        toggleMatrixCalculateFlag();
    }

    public Vec3 getLocalPosition() { return getLocalMatrix().multiply(new Vec3()); }

    private void toggleMatrixCalculateFlag() {
        calculateGlobalMatrix = true;
        for (int i = 0; i < object.childCount(); i++) {
            Transform child = object.getChild(i).getTransform();
            child.toggleMatrixCalculateFlag();
        }
    }

    public Vec3 getPosition() {
        return getGlobalMatrix().multiply(Vec3.zero());
    }

    public void rotate(Vec3 rotation) {
        this.rotation = this.rotation.multiply(Mat4.rotation(rotation));
        calculateLocalMatrix = true;
        toggleMatrixCalculateFlag();
    }

    public void translate(Vec3 translation) {
        this.translation = this.translation.multiply(Mat4.translation(translation));
        calculateLocalMatrix = true;
        toggleMatrixCalculateFlag();
    }

    public void scale(Vec3 scaling) {
        this.scaling = this.scaling.multiply(Mat4.scaling(scaling));
        calculateLocalMatrix = true;
        toggleMatrixCalculateFlag();
    }

    // rotates this transform to look at a target
    // rotates only on the x and y axes
    public void lookAt(Vec3 target) {
        Vec3 dv = target.sub(getPosition());
        double yaw = Math.toDegrees(-Math.atan2(dv.x, dv.z));
        double zLength = Math.sqrt(dv.x*dv.x + dv.z*dv.z);
        double pitch = Math.toDegrees(Math.atan2(dv.y, zLength));

        this.rotation = Mat4.rotation(new Vec3((float) pitch, (float) yaw, 0));
        calculateLocalMatrix = true;
        toggleMatrixCalculateFlag();
    }

    public Vec3 forward() { return getGlobalMatrix().multiplyDir(new Vec3(0, 0, -1)); }
    public Vec3 up() { return getGlobalMatrix().multiplyDir(new Vec3(0, 1, 0)); }

}
