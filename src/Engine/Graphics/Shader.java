package Engine.Graphics;

import Datatypes.*;

import java.util.ArrayList;

public abstract class Shader {

    // built-in shader variables
    Mat4 MVP, MV, VP, M, V, P, B;
    Mat3 N;
    // N: Mat3 inverse of the transpose of model matrix, used for transforming normals
    // B: Mat4 of Mat3 of view matrix, aka the upper left 3x3 of the view matrix

    float fogMin, fogMax;
    Color fogColor;

    Vec3 viewPosition;
    Vec3 faceNormal;

    Color ambientLightColor;
    float ambientLightIntensity;

    ArrayList<Vec3> pointLights;
    ArrayList<Color> pointLightColors;
    ArrayList<Float> pointLightIntensities;

    Vec3 directionalLight;
    Color directionalLightColor;
    float directionalLightIntensity;

    abstract Vertex vertex(Vertex in);
    abstract Color fragment(Vertex in);

}
