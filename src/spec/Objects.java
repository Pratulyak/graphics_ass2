package spec;

import java.util.ArrayList;
import java.util.List;

import com.jogamp.opengl.GL2;
import com.jogamp.opengl.glu.GLU;
import com.jogamp.opengl.util.gl2.GLUT;

@SuppressWarnings("unused")
public class Objects {

	//Starting position of avatar
	private double xTea = 1;
	private double yTea = 0;
	private double zTea = 1;
	private double altitude = 0;
	
	private double speed = 0.2;

	private double Xangle = 0;
	private double Yangle = 0;
	private double Zangle = 0;

	// Colors
	float[] colorRed = { 1.0f, 0.0f, 0.0f, 1.0f };
	float[] colorGreen = { 0.0f, 1.0f, 0.0f, 1.0f };
	float[] colorBlue = { 0.0f, 0.0f, 1.0f, 1.0f };
	float[] colorOrange = { 1.0f, 0.7f, 1.0f, 1.0f };

	// Emission
	float[] emissionRed = { 0.4f, 0.0f, 0.0f, 1.0f };
	float[] emissionGreen = { 0.0f, 0.4f, 0.0f, 1.0f };
	float[] emissionBlue = { 0.0f, 0.0f, 0.4f, 1.0f };
	float[] emissionOrange = { 0.7f, 0.3f, 0.0f, 1.0f };

	public Objects(Objects o) {
		
	}

	public Objects() {
	}
	
	public void setXtea(double xTea) {
		this.xTea = xTea;
	}
	
	public void setYtea(double yTea) {
		this.yTea = yTea;
	}
	
	public void setZtea(double zTea) {
		this.zTea = zTea;
	}
	
	public double getXtea() {
		return xTea;
	}
	
	public double getYtea() {
		return yTea;
	}
	
	public double getZtea() {
		return zTea;
	}
	
	public void setYangle(double angle) {
		this.Yangle = angle;
	}
	
	public double getYangle() {
		return Yangle;
	}
	
	public void setSpeed(double speed) {
		this.speed = speed;
	}
	
	public double getSpeed() {
		return speed;
	}
	
	public double getAltitude() {
		return altitude;
	}
	
	public void drawTeapot(GL2 gl, Terrain myTerrain, double scale,Camera camera) {
		
		//Turn off texture
        gl.glBindTexture(GL2.GL_TEXTURE_2D, 0);
       
        //Get the altitude of the terrain at the teapot's current location
        altitude = myTerrain.altitude(getXtea(), getZtea());
        
        camera.updateCamera(this);
        
        //Set color to the teapot
        float[] red = {1.0f, 0.0f, 0.0f, 1.0f};
        gl.glMaterialfv(GL2.GL_FRONT, GL2.GL_DIFFUSE, red, 0);
        float[] emi = {0.4f, 0.0f, 0.0f, 1.0f};
        gl.glMaterialfv(GL2.GL_FRONT, GL2.GL_EMISSION, emi, 0);
        
        //Move the the teapot
		gl.glTranslated(xTea, altitude-0.1, zTea);
		
		//Scale the teapot
		gl.glScaled(scale, scale, scale);
		
		//Draw the teapot
		GLUT glut = new GLUT();
		glut.glutSolidTeapot(1);
		
		//Rotate around the y-axis
		gl.glRotated(Yangle, 0.0, 1.0, 0.0);
		
		//Reset the coordinate system after the teapot
		gl.glScaled(1.0/scale, 1.0/scale, 1.0/scale);
		gl.glTranslated(-xTea, -altitude-0.1,-zTea);
	}
	
public void drawPortal(GL2 gl, int x, double y, int z, int height, float[] color, float[] emission){
		
		//Turn off texture
    	gl.glBindTexture(GL2.GL_TEXTURE_2D, 0);
		gl.glPolygonMode(GL2.GL_FRONT_AND_BACK, GL2.GL_FILL);
        gl.glMaterialfv(GL2.GL_FRONT, GL2.GL_DIFFUSE, color, 0);
        gl.glMaterialfv(GL2.GL_FRONT, GL2.GL_EMISSION, emission, 0);
		
        //Draw Portal Cube
		gl.glBegin(GL2.GL_QUADS);
		{
			//Bottom
			gl.glVertex3d(x, 0, z);
			gl.glVertex3d(x, 0, z+1);
			gl.glVertex3d(x+0.1, 0, z+1);
			gl.glVertex3d(x+0.1, 0, z);
			
			//Top
			gl.glVertex3d(x, y+height, z);
			gl.glVertex3d(x, y+height, z+1);
			gl.glVertex3d(x+0.1, y+height, z+1);
			gl.glVertex3d(x+0.1, y+height, z);
			
			//North
			gl.glVertex3d(x+0.1, 0, z);
			gl.glVertex3d(x+0.1, y+height, z);
			gl.glVertex3d(x+0.1, y+height, z+1);
			gl.glVertex3d(x+0.1, 0, z+1);
			
			//South
			gl.glVertex3d(x, 0, z);
			gl.glVertex3d(x, 0, z+1);
			gl.glVertex3d(x, y+height, z+1);
			gl.glVertex3d(x, y+height, z);
			
			//West
			gl.glVertex3d(x, 0, z);
			gl.glVertex3d(x, y+height, z);
			gl.glVertex3d(x+0.1, y+height, z);
			gl.glVertex3d(x+0.1, 0, z);
			
			//East
			gl.glVertex3d(x, 0, z+1);
			gl.glVertex3d(x+0.1, 0, z+1);
			gl.glVertex3d(x+0.1, y+height, z+1);
			gl.glVertex3d(x, y+height, z+1);
		}
		gl.glEnd();
	}

}
