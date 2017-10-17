package spec;

import java.awt.Dimension;
import java.util.ArrayList;
import java.util.List;

import com.jogamp.opengl.GL2;



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
        
    }
    
    public Terrain(Dimension size) {
        this(size.width, size.height);
    }
    
    /**
     * Calculate the vertices, faces, and normals for the terrain
     * @param width
     * @param depth
     */
    public void calculateVertices(int width, int depth){
    	vertices = getVertices(width, depth);
        faces = getFaceList(vertices, width);
        normals = getNormals(vertices, faces);
        normalizedNormals = normalize(normals);
    	
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
    		//Want to skip 9, 19, 29, 39, ... osv
    		if (i % width == 9 && i != 0) {
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
    		
    		//Skip the first vertex of the width (Because it has no neighbors to the left)
    		//Want to skip 0, 10, 20, 30, 40, ... osv
    		if (i % width == 0) {
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
    
    /**
     * Draws the terrain
     * @param gl
     */
    public void drawTerrain(GL2 gl) {
		//Draw the terrain from here?
    	gl.glBegin(GL2.GL_TRIANGLES);
    	{
    		//Because every other triangle is inverted, we have to invert texture coords
    		boolean invert = false;
    		
    		//Each face contains the three points making the triangle
    		for (List<Integer> face : faces) {
    			int p0 = face.get(0);
    			int p1 = face.get(1);
    			int p2 = face.get(2);
    			
    			gl.glNormal3d(normalizedNormals[p0][0], normalizedNormals[p0][1], normalizedNormals[p0][2]);
    			
    			if (!invert) {
    				gl.glTexCoord2d(0,1);
    			} else {
    				gl.glTexCoord2d(1,1);
    			}
    			gl.glVertex3d(vertices[p0][0], vertices[p0][1], vertices[p0][2]);
    			
    			if (!invert) {
    				gl.glTexCoord2d(0,0);
    			} else {
    				gl.glTexCoord2d(0,0);
    			}
    			gl.glVertex3d(vertices[p1][0], vertices[p1][1], vertices[p1][2]);
    			
    			if (!invert) {
    				gl.glTexCoord2d(1,1);
    			} else {
    				gl.glTexCoord2d(1,0);
    			}
    			gl.glVertex3d(vertices[p2][0], vertices[p2][1], vertices[p2][2]);
    			
    			invert = !invert;
    		}
    	}
    	gl.glEnd();
    	
	}

}
