package Game;

import Application.Application;
import Datatypes.Vec2;
import Datatypes.Vec3;
import Datatypes.Vec4;
import Engine.Graphics.Mesh;
import Engine.Graphics.Shader;
import Engine.Graphics.Vertex;
import Engine.Scene.Component;
import Engine.Scene.Components.Renderer;
import Engine.Scene.Scene;
import Engine.Scene.SceneObject;
import Engine.Scene.Transform;

import java.util.ArrayList;
import java.util.HashMap;

public class TerrainGenerator extends Component {

    private Transform target; // target object that chunks are generated around
    private float chunkWidth; // size of each chunk in world-space units
    private int chunkResolution; // number of triangle faces per chunk length
    private HashMap<String, SceneObject> chunkMap;

    private Scene scene;
    private Shader chunkShader;

    private final int generationRange = 4;
    private final int cullingRange = 4;

    public TerrainGenerator(Transform target, float chunkWidth, int chunkResolution, Shader chunkShader) {
        this.target = target;
        this.chunkWidth = chunkWidth;
        this.chunkResolution = chunkResolution;
        this.chunkMap = new HashMap<>();
        this.chunkShader = chunkShader;
    }

    @Override
    protected void Start() {
        scene = Application.getScene();
        Update();
    }

    @Override
    protected void Update() {

        // extract normalized chunk coordinates from target's world position
        Vec3 targetPos = target.getPosition();
        Vec2 chunkPos = new Vec2(
            (int) (targetPos.x / chunkWidth),
            (int) (targetPos.z / chunkWidth));

        // System.out.println(chunkPos);
        // generate any chunks within range that have not been generated
        for (int x = (int)chunkPos.x - generationRange; x <= (int)chunkPos.x + generationRange; x++) {
            for (int y = (int)chunkPos.y - generationRange; y <= (int)chunkPos.y + generationRange; y++) {

                Vec2 cPos = new Vec2(x, y);
                String posHash = cPos.toString();
                SceneObject chunk = chunkMap.get(posHash);

                if (chunk == null) {
                    generateChunk(new Vec2(x, y));
                }
            }
        }

        // cull chunks outside of the culling range
        ArrayList<String> toRemove = new ArrayList<>();
        for (String posHash : chunkMap.keySet()) {
            Vec2 pos = Vec2.parseVec2(posHash);
            if (pos.x < chunkPos.x - cullingRange || pos.x > chunkPos.x + cullingRange
            ||  pos.y < chunkPos.y - cullingRange || pos.y > chunkPos.y + cullingRange) {
                toRemove.add(posHash);
            }
        }

        toRemove.forEach(p -> cullChunk(p));

    }

    private int makePositionHash(int x, int y) {
        return (x << 16) | y;
    }

    private void generateChunk(Vec2 chunkPos) {
        System.out.println("Generating chunk: " + chunkPos);
        Vec2 worldPos = new Vec2(chunkPos.x * chunkWidth, chunkPos.y * chunkWidth);

        Thread generationThread = new Thread(new Runnable() {
            @Override
            public void run() {
                Mesh chunkMesh = GenerateChunk(worldPos, chunkWidth, chunkResolution);

                SceneObject chunkObject = new SceneObject("chunk" + chunkPos.toString(), new Transform(), null);
                chunkObject.AddComponent(new Renderer(chunkShader, chunkMesh));

                scene.addSceneObject(chunkObject);
                chunkMap.put(chunkPos.toString(), chunkObject);
            }
        });

        generationThread.run();
    }

    private void cullChunk(String positionHash) {
        System.out.println("Culling chunk: " + positionHash);
        SceneObject chunk = chunkMap.remove(positionHash);
        scene.removeSceneObject(chunk);
    }

    // params {
    // position: position of chunk in world space
    // width: length of each side of chunk, in world space
    // resolution: number of triangles per side of chunk
    // }
    // constructs a mesh of repeating triangles with y values generated from a height function
    static Mesh GenerateChunk(Vec2 position, float width, int resolution) {
        if (resolution % 2 != 0) return null; // resolution must be a multiple of two

        // list of points, including neighbor points that aren't in the final mesh
        ArrayList<Vec3> verticesFull = new ArrayList();

        int nColumns = resolution + 4;
        int nRows = nColumns;
        float dw = width / resolution;
        float xOffset = -dw * 1.5f;
        float yOffset = -dw;
        float xo = xOffset;

        //System.out.println("dw: " + dw);
        //System.out.println("xyo: " + xo + ", " + xOffset + ", " + yOffset);

        //int _i = 0;
        // build points list
        for (int y = 0; y < nColumns; y++) {
            for (int x = 0; x < nRows; x++) {
                float vx = position.x + x * dw + xo;
                float vy = position.y + y * dw + yOffset;
                //System.out.println(_i++ + ": " + vx + "," + vy);
                verticesFull.add(new Vec3(vx, height(vx, vy), vy));
            }
            // since we're working with a triangle mesh, the number of vertices per row oscillates between #columns and #columns-1
            nRows = nRows == nColumns ? nRows - 1 : nColumns;
            xo = xo == xOffset ? xOffset + (0.5f * dw) : xOffset;
        }

        // final list of vertices, including normals
        ArrayList<Vertex> vertices = new ArrayList<>();

        nRows = nColumns;
        // build adjacency information
        for (int i = nColumns + 1; i < verticesFull.size() - nColumns; i+=nRows) {
            for (int j = i; j < i + resolution + 1; j++) {
                Vec3 curr = verticesFull.get(j);

                // top left, top right, left, right, bot left, and bot right adjacency indexes for this vertex
                // the neighbors of this vertex form a hexagonal shape
                int tli = j - nColumns, tri = tli + 1;
                int  li = j - 1,         ri = j + 1;
                int bri = j + nColumns, bli = bri - 1;

                Vec3[] neighbors = new Vec3[] {
                        verticesFull.get(tli), verticesFull.get(tri), verticesFull.get(ri),
                        verticesFull.get(bri),  verticesFull.get(bli), verticesFull.get(li)
                };

                // calculate normal from adjacent points
                Vec3 normalTotal = Vec3.zero();
                for (int n = 0; n < 6; n++) {
                    //float dist = Vec3.distance(curr, neighbors[n]);
                    //    System.out.println("distance:" + dist + ", " + curr + ", " + neighbors[n]);
                    Vec3 e1 = curr.sub(neighbors[n]);
                    Vec3 e2 = curr.sub(neighbors[(n+1)%6]);
                    Vec3 norm = e1.cross(e2).normalize();
                    //System.out.println("neighbors:" + neighbors[n] + ", " + neighbors[(n+1)%6]);
                    //System.out.println("norm:" + norm);
                    normalTotal = normalTotal.add(norm);
                }

                // normalize the resulting sum
                Vec3 normOut = normalTotal.normalize();
                //normOut.y = -Math.abs(normOut.y);

                //System.out.println("V" + vertices.size() + ": " + curr);
                //System.out.println("N" + vertices.size() + ": " + normOut);

                Vertex currV = new Vertex(7);
                currV.setPosition(new Vec4(curr, 1));
                currV.setVec3(4, normOut);
                vertices.add(currV);
            }

            nRows = nRows == nColumns ? nRows - 1 : nColumns;
        }

        // final list of triangle indices
        ArrayList<Integer> triangles = new ArrayList<>();

        // build triangles list
        nColumns = resolution + 1;
        nRows = nColumns - 1;
        for (int i = nColumns; i < vertices.size() - nColumns; i+=nColumns) {

            for (int j = i + 1; j <= i + nRows; j++) {

                // the indices of the top right, top left, bot left, and bot right corners of the parallelogram formed by this vertex
                // this vertex is the bottom right corner of the parallelogram
                int tri = j - nColumns, tli = tri - 1;
                int bli = j - 1,        bri = j;

                // build two triangles from this parallelogram
                triangles.add(tli);
                triangles.add(tri);
                triangles.add(bli);

                triangles.add(bri);
                triangles.add(bli);
                triangles.add(tri);
            }

            //nRows = nRows == nColumns ? nColumns - 1 : nColumns;
        }

        //System.out.println(triangles);

        Mesh chunk = new Mesh(vertices.toArray(new Vertex[vertices.size()]), triangles.toArray(new Integer[triangles.size()]));
        return chunk;
    }

    public static float height(float x, float y) {

        double coarseNoise = ImprovedNoise.noise(x * 0.03f, y * 0.03f, 0);
        double finerNoise = ImprovedNoise.noise(x * 0.1f, y * 0.1f, 0);
        double finestNoise = ImprovedNoise.noise(x * 0.4f, y * 0.4f, 0);

        double coarse = Math.pow(coarseNoise*2, 3)*10;

        //return (float) coarse;
        return (float) (coarse + finerNoise*2 + finestNoise*coarse*0.5f);
    }

}
