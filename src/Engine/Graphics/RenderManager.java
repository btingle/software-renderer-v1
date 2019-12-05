package Engine.Graphics;

import Datatypes.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.concurrent.*;

public class RenderManager {

    private static class CameraProperties {
        Mat4 viewMatrix;
        Mat4 projMatrix;
        int frameBuffer;
        CameraProperties(Mat4 view, Mat4 proj, int fb) {
            viewMatrix = view;
            projMatrix = proj;
            frameBuffer = fb;
        }
    }

    private static class DrawJob implements Comparable<DrawJob> {
        static Mat4 viewProj;
        Mat4 modelMatrix;
        Mesh mesh;
        Shader shader;
        DrawJob(Mat4 model, Mesh mesh, Shader shader) {
            this.modelMatrix = model;
            this.mesh = mesh;
            this.shader = shader;
        }
        // used for sorting drawjobs by their closeness to the viewing plane
        public int compareTo(DrawJob other) {
            Mat4 mvpA = viewProj.multiply(modelMatrix);
            Mat4 mvpB = viewProj.multiply(other.modelMatrix);
            Vec4 posA = mvpA.multiply(new Vec4(0, 0, 0, 1));
            Vec4 posB = mvpB.multiply(new Vec4(0, 0, 0, 1));
            posA.divW();
            posB.divW();
            return Float.compare(posA.z, posB.z);
        }
    }

    Color ambientLightColor;
    float ambientLightIntensity;

    ArrayList<Vec3> pointLights;
    ArrayList<Color>  pointLightColors;
    ArrayList<Float> pointLightIntensities;

    Vec3  directionalLight;
    Color directionalLightColor;
    float directionalLightIntensity;

    ArrayList<CameraProperties> cameras;
    ArrayList<Framebuffer> framebuffers;
    ArrayList<DrawJob> drawJobs;

    float fogMin, fogMax;
    Color fogColor;

    Color clearColor;
    Cubemap skybox = null;

    private CubemapShader skyboxShader = new CubemapShader(null);

    private final int numThreads = 4;
    private final int numCores = 2;
    ExecutorService threadPool;

    public RenderManager() {
        ambientLightColor = new Color(1, 1, 1);
        ambientLightIntensity = 0.3f;
        pointLights = new ArrayList();
        pointLightColors = new ArrayList<>();
        pointLightIntensities = new ArrayList<>();
        directionalLight = Vec3.zero();
        directionalLightColor = new Color(1, 1, 1);
        directionalLightIntensity = 0;
        cameras = new ArrayList<>();
        framebuffers = new ArrayList<>();
        drawJobs = new ArrayList<>();
        clearColor = new Color(Vec3.constant(0.5f));
        fogMin = 50f;
        fogMax = 100f;
        fogColor = clearColor;

        threadPool = Executors.newFixedThreadPool(numThreads);
    }

    // params: ambient light attributes, color and intensity
    // sets the value for the ambient light in this render context, to optionally be used by shaders
    public void setAmbientLight(Color color, float intensity) {
        ambientLightColor = color;
        ambientLightIntensity = intensity;
    }

    // params: directional light attributes, direction, color, and intensity
    // sets the value for the directional light in this render context, to optionally be used by shaders
    public void setDirectionalLight(Vec3 dir, Color color, float intensity) {
        directionalLight = dir;
        directionalLightColor = color;
        directionalLightIntensity = intensity;
    }

    // params: point light attributes; position, color, and intensity
    // returns: an integer, the index of the new point light
    // adds a new point light to this render context, to optionally be used by shaders
    public int addPointLight(Vec3 pos, Color color, float intensity) {
        pointLights.add(pos);
        pointLightColors.add(color);
        pointLightIntensities.add(intensity);
        return pointLights.size() - 1;
    }

    // params: point light attributes & index
    // sets attributes for the point light at [index] in the point light array
    public void setPointLight(int index, Vec3 pos, Color color, float intensity) {
        pointLights.set(index, pos);
        pointLightColors.set(index, color);
        pointLightIntensities.set(index, intensity);
    }

    // params: camera attributes; view matrix, projection matrix, and framebuffer target
    // returns: an integer, the index of the new camera
    // adds a new camera to this render context
    public int addCamera(Mat4 view, Mat4 projection, int frameBuffer) {
        cameras.add(new CameraProperties(view, projection, frameBuffer));
        return cameras.size() - 1;
    }

    // params: camera attributes & index
    // sets the values for the camera at [index] in the camera array
    public void setCamera(int index, Mat4 view, Mat4 proj, int framebuffer) {
        CameraProperties camera = cameras.get(index);
        camera.viewMatrix = view;
        camera.projMatrix = proj;
        camera.frameBuffer = framebuffer;
    }

    // params: framebuffer attributes; width and height
    // returns: an integer, the index of the new framebuffer
    // creates a new framebuffer for this render context
    public int addFrameBuffer(int width, int height) {
        framebuffers.add(new Framebuffer(width, height));
        return framebuffers.size() - 1;
    }

    public void setClearColor(Color clearColor) {
        this.clearColor = clearColor;
    }

    public void setFog(float mindist, float maxdist, Color color) {
        fogMin = mindist;
        fogMax = maxdist;
        fogColor = color;
    }

    public void setSkybox(Cubemap skybox) {
        this.skybox = skybox;
        skyboxShader.cubemap = skybox;
    }

    // params: index, an integer
    // gets the framebuffer at [index] in the framebuffer array
    public Framebuffer getFramebuffer(int index) {
        return framebuffers.get(index);
    }

    // params: mesh, a triangle mesh, shader, a shader, modelMatrix, the model matrix for this draw call
    // queues a new draw call for the mesh, to be evaluated with the shader using the model matrix transform
    public void pushDrawJob(Mesh mesh, Shader shader, Mat4 modelMatrix) {
        drawJobs.add(new DrawJob(modelMatrix, mesh, shader));
    }

    public void Clear() {
        for (Framebuffer framebuffer : framebuffers) {
            framebuffer.clear(clearColor);
        }
    }

    long vertexStageTime = 0;
    long fragmentStageTime = 0;

    // called at the end of each frame
    public void Update() {
        Clear();

        for (CameraProperties camera : cameras) {
            DrawJob.viewProj = camera.projMatrix.multiply(camera.viewMatrix);

            // draw objects closest to the camera first, to hopefully minimize re-drawing over pixels
            Collections.sort(drawJobs);

            Framebuffer target = getFramebuffer(camera.frameBuffer);

            for (DrawJob job : drawJobs) {
                drawMesh(job.mesh, job.shader, target, job.modelMatrix, camera.viewMatrix, camera.projMatrix, false);
            }

            if (skybox != null) {
                //Rasterizer.SKIP_DEPTH_TEST = true;
                drawMesh(Cubemap.skyboxMesh, skyboxShader, target, Mat4.Identity(), camera.viewMatrix, camera.projMatrix, true);
                //Rasterizer.SKIP_DEPTH_TEST = false;
            }
        }

        //System.out.println("Took " + vertexStageTime + "ms for vertex processing, " + fragmentStageTime + "ms for fragment processing");

        vertexStageTime = fragmentStageTime = 0;

        drawJobs.clear();
    }

    private boolean frustumCulling(Vec3 min, Vec3 max, Mat4 MVP) {
        Vec4[] obb = new Vec4[8];
        // create the clip-space obb bounding box from the min/max points of the mesh
        obb[0] = MVP.multiply(new Vec4(min.x, max.y, min.z, 1));
        obb[1] = MVP.multiply(new Vec4(min.x, max.y, max.z, 1));
        obb[2] = MVP.multiply(new Vec4(max.x, max.y, max.z, 1));
        obb[3] = MVP.multiply(new Vec4(max.x, max.y, min.z, 1));
        obb[4] = MVP.multiply(new Vec4(max.x, min.y, min.z, 1));
        obb[5] = MVP.multiply(new Vec4(max.x, min.y, max.z, 1));
        obb[6] = MVP.multiply(new Vec4(min.x, min.y, max.z, 1));
        obb[7] = MVP.multiply(new Vec4(min.x, min.y, min.z, 1));
        // test if any of the components of all 8 points lie outside of the viewing plane
        for (int i = 0; i < 3; i++) {
            boolean outPositive = true;
            boolean outNegative = true;
            for (int j = 0; j < 8; j++) {
                float xyz = obb[j].get(i);
                outPositive = outPositive && ( xyz >  obb[j].w);
                outNegative = outNegative && (-xyz < -obb[j].w);
            }
            if (outNegative || outPositive) return false;
        }
        return true;
    }

    // draws a mesh, given the mesh, shader, framebuffer target, as well as model, view, and projection matrices
    private void drawMesh(Mesh mesh, Shader shader, Framebuffer framebuffer, Mat4 M, Mat4 V, Mat4 P, boolean skipCull) {

        // calculate MVP
        shader.MVP = P.multiply(V).multiply(M);

        // perform clip-space culling using the bounding box of the mesh and MVP matrix
        if (!skipCull && !frustumCulling(mesh.getMin(), mesh.getMax(), shader.MVP)) {
            return;
        }

        // initialize various shader built-in matrices
        // it's more efficient to set commonly used matrices like these here than calculate them per-vertex in a shader
        shader.MV = V.multiply(M);
        shader.VP = P.multiply(V);
        shader.M = M;
        shader.V = V;
        shader.P = P;
        shader.N = new Mat3(M.inverse().transpose());
        shader.B = new Mat4(new Mat3(V));

        shader.viewPosition = shader.MV.multiply(new Vec3());

        // initialize shader lighting built-ins
        shader.ambientLightColor = ambientLightColor;
        shader.ambientLightIntensity = ambientLightIntensity;
        shader.pointLights = pointLights;
        shader.pointLightColors = pointLightColors;
        shader.pointLightIntensities = pointLightIntensities;
        shader.directionalLight = directionalLight;
        shader.directionalLightColor = directionalLightColor;
        shader.directionalLightIntensity = directionalLightIntensity;
        shader.fogColor = fogColor;
        shader.fogMin = fogMin;
        shader.fogMax = fogMax;

        ArrayList<Future> threadFutures = new ArrayList<>();

        // transform mesh vertices NOT using multithreading
        // not multithreading it because it was pretty slow when I tried, if each thread is only given one
        // vertex to process at a time the thread switching overhead really kills the performance
        // need to process them in batches to mitigate this, but am too lazy to implement


        int processAmount = mesh.numVertices() / numCores;

        long sv = System.currentTimeMillis();
        for (int i = 0; i < mesh.numVertices(); i += processAmount) {

            int start = i;
            int end = Math.min((i + processAmount), mesh.numVertices());

            //System.out.println(start + ", " + end);

            threadFutures.add(threadPool.submit(new Runnable() {
                @Override
                public void run() {
                    processVerts(mesh, start, end, shader);
                }
            }));
        }

        awaitFutures(threadFutures);
        long ev = System.currentTimeMillis();

        vertexStageTime += (ev - sv);

        // parallel processing and rasterization of each triangle in the mesh
        // using multithreading roughly doubles the speed of each draw call
        // ...which makes sense, because my laptop has two CPU cores
        long sf = System.currentTimeMillis();
        for (int i = 0; i < mesh.numTriangles(); i++) {
            Vertex[] tri = mesh.getTriangle(i);

            threadFutures.add(threadPool.submit(new Runnable() {
                @Override
                public void run() {
                    new VertexProcessing(shader, framebuffer, tri[0], tri[1], tri[2]);
                }
            }));
        }

        // blocks the main thread until all triangles have finished being drawn
        awaitFutures(threadFutures);
        long ef = System.currentTimeMillis();

        fragmentStageTime += (ef - sf);
    }

    private void processVerts(Mesh mesh, int start, int end, Shader shader) {
        for (int i = start; i < end; i++) {
            mesh.setTransformedVertex(i, shader.vertex(mesh.getVertex(i)));
        }
    }

    private void awaitFutures(ArrayList<Future> threadFutures) {
        for (Future future : threadFutures) {
            try {
                future.get();
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (ExecutionException e) {
                e.printStackTrace();
            }
        }
        threadFutures.clear();
    }

    public void terminate() {
        threadPool.shutdown();
        try {
            if (!threadPool.awaitTermination(800, TimeUnit.MILLISECONDS)) {
                threadPool.shutdownNow();
            }
        } catch (InterruptedException e) {
            threadPool.shutdownNow();
        }
    }

}
