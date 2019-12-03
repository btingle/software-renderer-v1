package Datatypes;

public class Mat3 {

    float[][] mat;

    public Mat3() {
        mat = new float[3][3];
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                mat[i][j] = 0;
            }
        }
    }

    public Mat3(Mat4 other) {
        mat = new float[3][3];
        for (int i =0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                mat[i][j] = other.mat[i][j];
            }
        }
    }

    public Vec3 multiply(Vec3 v) {

        Vec3 result = Vec3.zero();

        result.x = mat[0][0] * v.x + mat[1][0] * v.y + mat[2][0] * v.z;
        result.y = mat[0][1] * v.x + mat[1][1] * v.y + mat[2][1] * v.z;
        result.z = mat[0][2] * v.x + mat[1][2] * v.y + mat[2][2] * v.z;

        return result;
    }

    public float get(int i, int j) { return mat[i][j]; }

}
