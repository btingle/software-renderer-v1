package Datatypes;

public class Mat4 {

    float[][] mat;

    public Mat4(float[][] values) {
        mat = new float[4][4];
        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 4; j++) {
                mat[i][j] = values[i][j];
            }
        }
    }

    public Mat4(Mat4 other) {
        this(other.mat);
    }

    public Mat4(Mat3 other) {
        mat = new float[4][4];
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                mat[i][j] = other.mat[i][j];
            }
        }
    }

    public static Mat4 Identity() {
        float[][] n_mat = new float[4][4];
        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 4; j++) {
                n_mat[i][j] = i == j ? 1 : 0;
            }
        }
        return new Mat4(n_mat);
    }

    // I'm transferring these operations over from an old C project, so they're a bit archaic...
    public Mat4 multiply(Mat4 other) {
        float[][] m1 = mat;
        float[][] m2 = other.mat;
        float[][] dest = new float[4][4];

        float   a00 = m1[0][0], a01 = m1[0][1], a02 = m1[0][2], a03 = m1[0][3],
                a10 = m1[1][0], a11 = m1[1][1], a12 = m1[1][2], a13 = m1[1][3],
                a20 = m1[2][0], a21 = m1[2][1], a22 = m1[2][2], a23 = m1[2][3],
                a30 = m1[3][0], a31 = m1[3][1], a32 = m1[3][2], a33 = m1[3][3],

                b00 = m2[0][0], b01 = m2[0][1], b02 = m2[0][2], b03 = m2[0][3],
                b10 = m2[1][0], b11 = m2[1][1], b12 = m2[1][2], b13 = m2[1][3],
                b20 = m2[2][0], b21 = m2[2][1], b22 = m2[2][2], b23 = m2[2][3],
                b30 = m2[3][0], b31 = m2[3][1], b32 = m2[3][2], b33 = m2[3][3];

        dest[0][0] = a00 * b00 + a10 * b01 + a20 * b02 + a30 * b03;
        dest[0][1] = a01 * b00 + a11 * b01 + a21 * b02 + a31 * b03;
        dest[0][2] = a02 * b00 + a12 * b01 + a22 * b02 + a32 * b03;
        dest[0][3] = a03 * b00 + a13 * b01 + a23 * b02 + a33 * b03;
        dest[1][0] = a00 * b10 + a10 * b11 + a20 * b12 + a30 * b13;
        dest[1][1] = a01 * b10 + a11 * b11 + a21 * b12 + a31 * b13;
        dest[1][2] = a02 * b10 + a12 * b11 + a22 * b12 + a32 * b13;
        dest[1][3] = a03 * b10 + a13 * b11 + a23 * b12 + a33 * b13;
        dest[2][0] = a00 * b20 + a10 * b21 + a20 * b22 + a30 * b23;
        dest[2][1] = a01 * b20 + a11 * b21 + a21 * b22 + a31 * b23;
        dest[2][2] = a02 * b20 + a12 * b21 + a22 * b22 + a32 * b23;
        dest[2][3] = a03 * b20 + a13 * b21 + a23 * b22 + a33 * b23;
        dest[3][0] = a00 * b30 + a10 * b31 + a20 * b32 + a30 * b33;
        dest[3][1] = a01 * b30 + a11 * b31 + a21 * b32 + a31 * b33;
        dest[3][2] = a02 * b30 + a12 * b31 + a22 * b32 + a32 * b33;
        dest[3][3] = a03 * b30 + a13 * b31 + a23 * b32 + a33 * b33;

        return new Mat4(dest);
    }

    Mat4 translateMat(int x, int y) {
        x %= 4;
        y %= 4;
        float[][] m = new float[4][4];
        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 4; j++) {
                m[(i+x)%4][(j+y)%4] = mat[i][j];
            }
        }
        return new Mat4(m);
    }

    public static Mat4 perspective(float fov, float aspect, float near, float far) {

        float scale = (float) Math.tan(fov / 2) * near;
        float right = aspect * scale;
        float left  = -right;
        float top = scale;
        float bot = -top;

        return Mat4.orthographic(left, right, bot, top, near, far);
    }

    public static Mat4 orthographic(float left, float right, float bottom, float top, float near, float far) {
        float rl, tb, fn, nv;
        float[][] m = new float[4][4];

        rl = 1.0f / (right  - left);
        tb = 1.0f / (top    - bottom);
        fn =-1.0f / (far - near);
        nv = 2.0f * near;

        m[0][0] = nv * rl;
        m[1][1] = nv * tb;
        m[2][0] = (right  + left)    * rl;
        m[2][1] = (top    + bottom)  * tb;
        m[2][2] = (far + near) * fn;
        m[2][3] = -1.0f;
        m[3][2] = far * nv * fn;

        return new Mat4(m);
    }

    public static Mat4 orthographic(float aspectRatio, float near, float far) {
        if (aspectRatio >= 1) {
            return orthographic(-aspectRatio, aspectRatio, -1, 1, near, far);
        }
        else {
            aspectRatio = 1 / aspectRatio;
            return orthographic(-1, 1, -aspectRatio, aspectRatio, near, far);
        }
    }

    public static Mat4 lookAt(Vec3 eye, Vec3 center, Vec3 up) {

        Vec3 zaxis = center.sub(eye).normalize();
        Vec3 xaxis = up.cross(zaxis).normalize();
        Vec3 yaxis = zaxis.cross(xaxis);

        float[][] m = new float[][] {
                { xaxis.x, yaxis.x, zaxis.x, 0},
                { xaxis.y, yaxis.y, zaxis.y, 0},
                { xaxis.z, yaxis.z, zaxis.z, 0},
                {-xaxis.dot(eye), -yaxis.dot(eye), -zaxis.dot(eye), 1}
        };

        return new Mat4(m);
    }

    public static Mat4 lookAtDir(Vec3 eye, Vec3 center, Vec3 up) {
        Vec3 zaxis = center.sub(eye).normalize();
        Vec3 xaxis = up.cross(zaxis).normalize();
        Vec3 yaxis = zaxis.cross(xaxis);

        float[][] m = new float[][] {
                { xaxis.x, yaxis.x, zaxis.x, 0},
                { xaxis.y, yaxis.y, zaxis.y, 0},
                { xaxis.z, yaxis.z, zaxis.z, 0},
                { 0, 0, 0, 1 }
        };

        return new Mat4(m);
    }

    public static Mat4 translation(Vec3 t) {
        Mat4 mat = Mat4.Identity();
        float[][] m = mat.mat;
        m[3][0] = t.x;
        m[3][1] = t.y;
        m[3][2] = t.z;
        return mat;
    }

    public static Mat4 scaling(Vec3 scale) {
        Mat4 mat = Mat4.Identity();
        float[][] m = mat.mat;
        m[0][0] = scale.x;
        m[1][1] = scale.y;
        m[2][2] = scale.z;
        return mat;
    }

    public static Mat4 rotation(Vec3 eulerAngles) {

        Mat4 rotationX = Mat4._rotationX((float) Math.toRadians(eulerAngles.x));
        Mat4 rotationY = Mat4._rotationY((float) Math.toRadians(eulerAngles.y));
        Mat4 rotationZ = Mat4._rotationZ((float) Math.toRadians(eulerAngles.z));

        return rotationY.multiply(rotationX).multiply(rotationZ);
    }

    public static Mat4 _rotationX(float angle) {

        float[][] m = new float[][] {
                { 1, 0, 0, 0 },
                { 0, (float) Math.cos(angle), (float) -Math.sin(angle), 0 },
                { 0, (float) Math.sin(angle), (float)  Math.cos(angle), 0 },
                { 0, 0, 0, 1 }
        };
        return new Mat4(m);
    }
    public static Mat4 _rotationZ(float angle) {

        float[][] m = new float[][] {
                { (float) Math.cos(angle), (float) -Math.sin(angle), 0, 0 },
                { (float) Math.sin(angle), (float)  Math.cos(angle), 0, 0 },
                { 0, 0, 1, 0 },
                { 0, 0, 0, 1 }
        };
        return new Mat4(m);
    }
    public static Mat4 _rotationY(float angle) {

        float[][] m = new float[][] {
                { (float) Math.cos(angle), 0, (float) Math.sin(angle), 0 },
                { 0, 1, 0, 0 },
                { (float) -Math.sin(angle), 0, (float)  Math.cos(angle), 0 },
                { 0, 0, 0, 1 }
        };
        return new Mat4(m);
    }

    public Vec3 multiply(Vec3 vec) {
        Vec4 _vec = new Vec4(vec, 1);
        Vec4 _res = multiply(_vec);
        return new Vec3(_res.x, _res.y, _res.z);
    }

    public Vec4 multiply(Vec4 vec) {
        float[] tmp = new float[4];
        float[] v = new float[4];
        v[0] = vec.x;
        v[1] = vec.y;
        v[2] = vec.z;
        v[3] = vec.w;

        tmp[0] = mat[0][0] * v[0] + mat[1][0] * v[1] + mat[2][0] * v[2] + mat[3][0] * v[3];
        tmp[1] = mat[0][1] * v[0] + mat[1][1] * v[1] + mat[2][1] * v[2] + mat[3][1] * v[3];
        tmp[2] = mat[0][2] * v[0] + mat[1][2] * v[1] + mat[2][2] * v[2] + mat[3][2] * v[3];
        tmp[3] = mat[0][3] * v[0] + mat[1][3] * v[1] + mat[2][3] * v[2] + mat[3][3] * v[3];

        return new Vec4(tmp[0], tmp[1], tmp[2], tmp[3]);
    }

    public Vec3 multiplyDir(Vec3 v) {
        Vec3 result = Vec3.zero();

        result.x = mat[0][0] * v.x + mat[1][0] * v.y + mat[2][0] * v.z;
        result.y = mat[0][1] * v.x + mat[1][1] * v.y + mat[2][1] * v.z;
        result.z = mat[0][2] * v.x + mat[1][2] * v.y + mat[2][2] * v.z;

        return result;
    }

    public Mat4 inverse() {

        float[][] dest = new float[4][4];

        float[] t = new float[6];
        float det;
        float a = mat[0][0], b = mat[0][1], c = mat[0][2], d = mat[0][3],
                e = mat[1][0], f = mat[1][1], g = mat[1][2], h = mat[1][3],
                i = mat[2][0], j = mat[2][1], k = mat[2][2], l = mat[2][3],
                m = mat[3][0], n = mat[3][1], o = mat[3][2], p = mat[3][3];

        t[0] = k * p - o * l; t[1] = j * p - n * l; t[2] = j * o - n * k;
        t[3] = i * p - m * l; t[4] = i * o - m * k; t[5] = i * n - m * j;

        dest[0][0] =  f * t[0] - g * t[1] + h * t[2];
        dest[1][0] =-(e * t[0] - g * t[3] + h * t[4]);
        dest[2][0] =  e * t[1] - f * t[3] + h * t[5];
        dest[3][0] =-(e * t[2] - f * t[4] + g * t[5]);

        dest[0][1] =-(b * t[0] - c * t[1] + d * t[2]);
        dest[1][1] =  a * t[0] - c * t[3] + d * t[4];
        dest[2][1] =-(a * t[1] - b * t[3] + d * t[5]);
        dest[3][1] =  a * t[2] - b * t[4] + c * t[5];

        t[0] = g * p - o * h; t[1] = f * p - n * h; t[2] = f * o - n * g;
        t[3] = e * p - m * h; t[4] = e * o - m * g; t[5] = e * n - m * f;

        dest[0][2] =  b * t[0] - c * t[1] + d * t[2];
        dest[1][2] =-(a * t[0] - c * t[3] + d * t[4]);
        dest[2][2] =  a * t[1] - b * t[3] + d * t[5];
        dest[3][2] =-(a * t[2] - b * t[4] + c * t[5]);

        t[0] = g * l - k * h; t[1] = f * l - j * h; t[2] = f * k - j * g;
        t[3] = e * l - i * h; t[4] = e * k - i * g; t[5] = e * j - i * f;

        dest[0][3] =-(b * t[0] - c * t[1] + d * t[2]);
        dest[1][3] =  a * t[0] - c * t[3] + d * t[4];
        dest[2][3] =-(a * t[1] - b * t[3] + d * t[5]);
        dest[3][3] =  a * t[2] - b * t[4] + c * t[5];

        det = 1.0f / (a * dest[0][0] + b * dest[1][0]
                + c * dest[2][0] + d * dest[3][0]);

        Mat4 result = new Mat4(dest);
        result.scale(det);

        return new Mat4(dest);
    }

    public Mat4 transpose() {
        float[][] dest = new float[4][4];

        dest[0][0] = mat[0][0]; dest[1][0] = mat[0][1];
        dest[0][1] = mat[1][0]; dest[1][1] = mat[1][1];
        dest[0][2] = mat[2][0]; dest[1][2] = mat[2][1];
        dest[0][3] = mat[3][0]; dest[1][3] = mat[3][1];
        dest[2][0] = mat[0][2]; dest[3][0] = mat[0][3];
        dest[2][1] = mat[1][2]; dest[3][1] = mat[1][3];
        dest[2][2] = mat[2][2]; dest[3][2] = mat[2][3];
        dest[2][3] = mat[3][2]; dest[3][3] = mat[3][3];

        return new Mat4(dest);
    }

    private void scale(float s) {
        for (int row = 0; row < 4; row++)
            for (int col = 0; col < 4; col++)
                mat[row][col] = mat[row][col] * s;
    }

    public Vec4 getColumn(int i) {
        return new Vec4(mat[0][i], mat[1][i], mat[2][i], mat[3][i]);
    }

    public Vec3 transform(Vec3 vec) {
        Vec4 v = multiply(new Vec4(vec, 1));
        v.divW();
        return new Vec3(v.x, v.y, v.z);
    }

    public String toString() {
        String matString = "";
        for (int x = 0; x < 4; x++) {
            matString += "[";
            for (int y = 0; y < 4; y++) {
                matString += mat[x][y] + ",";
            }
            matString += "]\n";
        }
        return matString;
    }

    public float get(int i, int j) {
        return mat[i][j];
    }
}
