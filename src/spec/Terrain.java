package spec;

import java.awt.Dimension;
import java.util.ArrayList;
import java.util.List;



/**
 * COMMENT: Comment HeightMap 
 *
 * @author malcolmr
 */
public class Terrain {

    private Dimension mySize;
    private double[][] myAltitude;
    private List<Tree> myTrees;
    private List<Road> myRoads;
    private float[] mySunlight;
    
    private double[][] vertices;
    private List<List<Integer>> faces;
    private double[][] normals;

    /**
     * Create a new terrain
     *
     * @param width The number of vertices in the x-direction
     * @param depth The number of vertices in the z-direction
     */
    public Terrain(int width, int depth) {
        mySize = new Dimension(width, depth);
        myAltitude = new double[width][depth];
        myTrees = new ArrayList<Tree>();
        myRoads = new ArrayList<Road>();
        mySunlight = new float[3];
        
        vertices = getVertices(width, depth);
        faces = getFaceList(vertices, width);
        normals = new double[faces.size()][3];
    }
    
    public Terrain(Dimension size) {
        this(size.width, size.height);
    }

    public Dimension size() {
        return mySize;
    }

    public List<Tree> trees() {
        return myTrees;
    }

    public List<Road> roads() {
        return myRoads;
    }

    public float[] getSunlight() {
        return mySunlight;
    }
    
    /**
     * Return a list of vertices
     * 
     * @param width
     * @param depth
     * @return vertexList
     */
    public double[][] getVertices(int width, int depth) {
    	double[][] vertexList = new double[width * depth][3];
    	
    	int i = 0;
    	for (int z = 0; z < width; z++) {
    		for (int x = 0; x < depth; x++) {
    			vertexList[i][0] = x;
    	    	vertexList[i][1] = getGridAltitude(x, z);
    	    	vertexList[i][2] = z;
    	    	i ++;
    		}
    	}
    	
    	return vertexList;
    }
    
    /**
     * Return a list of all the faces in the terrain mesh
     * 
     * @vertexList List of vertices
     * @width Width of the mesh
     * @return faceList
     */
    public List<List<Integer>> getFaceList(double[][] vertexList, int width) {
    	ArrayList<List<Integer>> faceList = new ArrayList<>();
    	
    	//Iterate through every vertex, except the last depth (last z or row)
    	for (int i = 0; i < (vertexList.length - width); i++) {
    		
    		//Skip the last vertex of the width (Because it has no neighbors to the right)
    		//Will this cover all cases?
    		if (i % width == 0 && i != 0) {
    			continue;
    		}
    		
    		ArrayList<Integer> face = new ArrayList<>();
    		
    		//Add vertices in CCW order
    		face.add(i);
    		face.add(i + width); //vertex[i+width] is the vertex below vertex[i]
    		face.add(i + 1); //vertex[i+1] is the vertex to the right of vertex[i]
    		
    		faceList.add(face);
    	}
    	
    	//Iterate through every vertex, except the last depth (last z or row)
    	//Iterating the list twice because two triangles will start at same vertex
    	for (int i = 1; i < (vertexList.length - width); i++) {
    		
    		//SKip the first vertex of the width (Because it has no neighbors to the left)
    		if (i % width == 1 && i != 1) {
    			continue;
    		}
    		
    		ArrayList<Integer> face = new ArrayList<>();
    		
    		//Add vertices in CCW order
    		face.add(i);
    		face.add(i + width - 1); //vertex[i+width-1] is the vertex below and to the left of vertex[i]
    		face.add(i + width); //vertex[i+width] is the vertex below vertex[i]
    		
    		faceList.add(face);
    	}
    	return faceList;
    }

    /**
     * Set the sunlight direction. 
     * 
     * Note: the sun should be treated as a directional light, without a position
     * 
     * @param dx
     * @param dy
     * @param dz
     */
    public void setSunlightDir(float dx, float dy, float dz) {
        mySunlight[0] = dx;
        mySunlight[1] = dy;
        mySunlight[2] = dz;        
    }
    
    /**
     * Resize the terrain, copying any old altitudes. 
     * 
     * @param width
     * @param height
     */
    public void setSize(int width, int height) {
        mySize = new Dimension(width, height);
        double[][] oldAlt = myAltitude;
        myAltitude = new double[width][height];
        
        for (int i = 0; i < width && i < oldAlt.length; i++) {
            for (int j = 0; j < height && j < oldAlt[i].length; j++) {
                myAltitude[i][j] = oldAlt[i][j];
            }
        }
    }

    /**
     * Get the altitude at a grid point
     * 
     * @param x
     * @param z
     * @return
     */
    public double getGridAltitude(int x, int z) {
        return myAltitude[x][z];
    }

    /**
     * Set the altitude at a grid point
     * 
     * @param x
     * @param z
     * @return
     */
    public void setGridAltitude(int x, int z, double h) {
        myAltitude[x][z] = h;
    }

    /**
     * Get the altitude at an arbitrary point. 
     * Non-integer points should be interpolated from neighbouring grid points
     * 
     * TO BE COMPLETED
     * 
     * @param x
     * @param z
     * @return
     */
    public double altitude(double x, double z) {
        double altitude = 0;

        //Not sure what to do here... Is this one line it?
        altitude = myAltitude[(int)Math.floor(x)][(int)Math.floor(z)];
        
        return altitude;
    }

    /**
     * Add a tree at the specified (x,z) point. 
     * The tree's y coordinate is calculated from the altitude of the terrain at that point.
     * 
     * @param x
     * @param z
     */
    public void addTree(double x, double z) {
        double y = altitude(x, z);
        Tree tree = new Tree(x, y, z);
        myTrees.add(tree);
    }


    /**
     * Add a road. 
     * 
     * @param x
     * @param z
     */
    public void addRoad(double width, double[] spine) {
        Road road = new Road(width, spine);
        myRoads.add(road);        
    }


}
