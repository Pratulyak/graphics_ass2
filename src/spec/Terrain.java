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
    private double[][] normalizedNormals;

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
        normals = getNormals(vertices, faces);
        normalizedNormals = normalize(normals);
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
     * 
     * @return vertexList
     */
    public double[][] getVertices(int width, int depth) {
    	double[][] vertexList = new double[width * depth][3];
    	
    	int index = 0;
    	for (int z = 0; z < depth; z++) {
    		for (int x = 0; x < width; x++) {
    			vertexList[index][0] = x;
    	    	vertexList[index][1] = getGridAltitude(x, z);
    	    	vertexList[index][2] = z;
    	    	index ++;
    		}
    	}
    	
    	return vertexList;
    }
    
    /**
     * Return a list of all the faces in the terrain mesh
     * 
     * @param vertexList List of vertices
     * @param width of the mesh
     * 
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
     * Calculate and return list of normals for each face
     * 
     * @param vertexList
     * @param faces
     * 
     * @return normalList
     */
    public double[][] getNormals(double[][] vertexList, List<List<Integer>> faces) {
    	double[][] normalList = new double[faces.size()][3];
    	
    	int index = 0;
    	for (List<Integer> face : faces) {
			
    		//Get the three vertices (the indexes) that makes the triangle of the face
    		int p0 = face.get(0);
    		int p1 = face.get(1);
    		int p2 = face.get(2);
    		
    		//Get {x,y,z} coordinates from vertexList
    		double[] V2 = vertexList[p2];
    		double[] V1 = vertexList[p1];
    		double[] V0 = vertexList[p0];
    		
    		//v1 = V3 - V1
    		double[] v1 = {V2[0] - V0[0], V2[1] - V0[1], V2[2] - V0[2]};
    		
    		//v2 = p2 - p1
    		double[] v2 = {V1[0] - V0[0], V1[1] - V0[1], V1[2] - V0[2]};
    		
    		//Cross product of v1 and v1
    		double[] v1xv2 = {v1[1]*v2[2] - v1[2]*v2[1], 
    				v1[2]*v2[0] - v1[0]*v2[2], 
    				v1[0]*v2[1] - v1[1]*v2[0]};
    		
    		normalList[index] = v1xv2;
    		index ++;
    	}
    	
    	return normalList;
    }
    
    /**
     * Normalize normals
     * 
     * @param normals
     * return normalizedNormals
     */
    public double[][] normalize(double[][] normals) {
    	double[][] normalizedNormals = new double[normals.length][3];
    	
    	int index = 0;
    	for (double[] normal : normals) {
    		double magnitude = getMagnitude(normal);
    		double norm[] = {normal[0]/magnitude ,normal[1]/magnitude, normal[2]/magnitude};
    		
    		normalizedNormals[index] = norm;
    		index ++;
    	}
    	
    	return normalizedNormals;
    }
    
    /**
     * Get the magnitude of normals
     * 
     * @param n
     * @return mag
     */
    public double getMagnitude(double[] n) {
    	double mag = n[0]*n[0] + n[1]*n[1] + n[2]*n[2];
    	mag = Math.sqrt(mag);
    	
    	return mag;
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
