package Engine.Graphics;

import Datatypes.*;

import java.util.ArrayList;

public abstract class Shader {

    // built-in shader variables
    protected Mat4 MVP, MV, VP, M, V, P, B;
    protected Mat3 N;
    // N: Mat3 inverse of the transpose of model matrix, used for transforming normals
    // B: Mat4 of Mat3 of view matrix, aka the upper left 3x3 of the view matrix

    protected float fogMin, fogMax;
    protected Color fogColor;

    protected Vec3 viewPosition;
    protected Vec3 faceNormal;

    protected Color ambientLightColor;
    protected float ambientLightIntensity;

    protected ArrayList<Vec3> pointLights;
    protected ArrayList<Color> pointLightColors;
    protected ArrayList<Float> pointLightIntensities;

    protected Vec3 directionalLight;
    protected Color directionalLightColor;
    protected float directionalLightIntensity;

    abstract Vertex vertex(Vertex in);
    abstract Color fragment(Vertex in);

}
