package spec;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.File;
import java.io.FileNotFoundException;
import com.jogamp.opengl.*;
import com.jogamp.opengl.awt.GLJPanel;
import com.jogamp.opengl.glu.GLU;

import javax.swing.JFrame;
import com.jogamp.opengl.util.FPSAnimator;
import com.jogamp.opengl.util.gl2.GLUT;

/**
 * COMMENT: Comment Game
 *
 * @author malcolmr
 */
public class Game extends JFrame implements GLEventListener, KeyListener {

	private Terrain myTerrain;
	
	private double teapotScale;
	
	private final int NUM_TEXTURES = 1;
	private MyTexture myTextures[];
	
	//For rotation
	private double Xangle = 0;
    private double Yangle = 0;
	private double Zangle = 0;
	
	//For movement
	private double xPos = 0;
    private double yPos = 0;
	private double zPos = 0;
	
	private boolean thirdPersonView = true;
	
	//Variables for the camera view
	private double eyeX = 0;
	private double eyeY = 0;
	private double eyeZ = 0;
	private double centerX = 0;
	private double centerY = 0;
	private double centerZ = 0;

	public Game(Terrain terrain) {
		super("Assignment 2");
		myTerrain = terrain;

	}

	/**
	 * Run the game.
	 *
	 */
	public void run() {
		GLProfile glp = GLProfile.getDefault();
		GLCapabilities caps = new GLCapabilities(glp);
		GLJPanel panel = new GLJPanel();
		panel.addGLEventListener(this);
		panel.addKeyListener(this);
		panel.setFocusable(true);

		// Add an animator to call 'display' at 60fps
		FPSAnimator animator = new FPSAnimator(60);
		animator.add(panel);
		animator.start();

		getContentPane().add(panel);
		setSize(800, 600);
		setVisible(true);
		setDefaultCloseOperation(EXIT_ON_CLOSE);
	}

	/**
	 * Load a level file and display it.
	 * 
	 * @param args
	 *            - The first argument is a level file in JSON format
	 * @throws FileNotFoundException
	 */
	public static void main(String[] args) throws FileNotFoundException {
		Terrain terrain = LevelIO.load(new File(args[0]));
		Game game = new Game(terrain);
		game.run();
	}

	@Override
	public void display(GLAutoDrawable drawable) {
		GL2 gl = drawable.getGL().getGL2();

		gl.glClear(GL.GL_COLOR_BUFFER_BIT | GL2.GL_DEPTH_BUFFER_BIT);
		gl.glMatrixMode(GL2.GL_MODELVIEW);
		gl.glLoadIdentity();
		
		//Turn off texture
        gl.glBindTexture(GL2.GL_TEXTURE_2D, 0);
        
        //How much to scale down the teapot
        teapotScale = 5;
       
        //Get the altitude of the terrain at the teapot's current location
        //Because of scaling, the variables xMove and zMove have to be divided
        double altitude;
        altitude = myTerrain.altitude(xPos/teapotScale, zPos/teapotScale);
		
        //Scale down for the teapot
		gl.glScaled(1.0/teapotScale, 1.0/teapotScale, 1.0/teapotScale);
		
		//Camera that follows the teapot (3rd person view)
		GLU glu = new GLU();
		
		//Setting camera coordinates according to the view mode
		if (thirdPersonView) {
			eyeX = xPos-15;
			eyeY = (10 + altitude*teapotScale);
			eyeZ = zPos;
			centerX = xPos+5;
			centerY = altitude*teapotScale;
			centerZ = zPos;
		} else {
			eyeX = xPos;
			eyeY = 2+altitude*teapotScale;
			eyeZ = zPos;
			centerX = xPos+5;
			centerY = altitude*teapotScale;
			centerZ = zPos;
		}
		
		//gluLookAt(eyeX, eyeY, eyeZ, centerX, centerY, centerZ, upX, upY, upZ)
		glu.gluLookAt(eyeX, eyeY, eyeZ, centerX, centerY, centerZ, 0, 1, 0);
        
        //Set color to the teapot
        float[] red = {1.0f, 0.0f, 0.0f, 1.0f};
        gl.glMaterialfv(GL2.GL_FRONT, GL2.GL_DIFFUSE, red, 0);
        float[] emi = {0.4f, 0.0f, 0.0f, 1.0f};
        gl.glMaterialfv(GL2.GL_FRONT, GL2.GL_EMISSION, emi, 0);
        
        //Move the coordinate system (aka move the teapot)
		gl.glTranslated(xPos, 1+altitude*teapotScale, zPos);
		
		//Draw the teapot
		GLUT glut = new GLUT();
		glut.glutSolidTeapot(1);
		
		//Rotate around the y-axis
		gl.glRotated(Yangle, 0.0, 1.0, 0.0);
		
		//Reset the coordinate system after the teapot
		gl.glTranslated(-xPos,-(1+altitude*teapotScale),-zPos);
		gl.glScaled(teapotScale, teapotScale, teapotScale);
		
		//Draw a teleporter
		int teleX = 7;
		int teleZ = 7;
		double teleY = myTerrain.getGridAltitude(teleX, teleZ);
		int teleHeight = 1;
		drawTeleport(gl, teleX, teleY, teleZ, teleHeight);
		
		//Set texture modes
		gl.glTexEnvf(GL2.GL_TEXTURE_ENV, GL2.GL_TEXTURE_ENV_MODE, GL2.GL_REPLACE);
		gl.glTexParameteri(GL2.GL_TEXTURE_2D, GL2.GL_TEXTURE_WRAP_S, GL2.GL_REPEAT);
		
		//Set grass texture
		gl.glBindTexture(GL2.GL_TEXTURE_2D, myTextures[0].getTextureId());
		
		//Draw the terrain
		myTerrain.drawTerrain(gl);

	}

	@Override
	public void dispose(GLAutoDrawable drawable) {
		// TODO Auto-generated method stub

	}

	@Override
	public void init(GLAutoDrawable drawable) {
		GL2 gl = drawable.getGL().getGL2();
		// If you do not add this line
		// opengl will draw things in the order you
		// draw them in your program
		gl.glEnable(GL2.GL_DEPTH_TEST);
		
		// enable lighting
		gl.glEnable(GL2.GL_LIGHTING);
		
		// turn on a light. Use default settings.
		gl.glEnable(GL2.GL_LIGHT0);
		
		//Position the light source (the sun). The last zero in 'dir' represent direction
		float[] dir = {5, 10, 3, 0};
		gl.glLightfv(GL2.GL_LIGHT0, GL2.GL_POSITION, dir, 0);
		
		//Create ambient light (not pretty...)
        float[] amb = {0.2f, 0.2f, 0.2f, 1.0f};
        //gl.glLightfv(GL2.GL_LIGHT0, GL2.GL_AMBIENT, amb, 0);
        
        //float light_ambient[] = { 0.0f, 0.0f, 0.0f, 1.0f };
        //gl.glLightfv(GL2.GL_LIGHT0, GL2.GL_AMBIENT, light_ambient);
		
		//Enable texturing
		gl.glEnable(GL2.GL_TEXTURE_2D);
		
		//Load in textures from files
    	myTextures = new MyTexture[NUM_TEXTURES];
    	myTextures[0] = new MyTexture(gl,"src/spec/grass.bmp",true);

		// normalise normals (!)
		// this is necessary to make lighting work properly
		gl.glEnable(GL2.GL_NORMALIZE);

	}

	@Override
	public void reshape(GLAutoDrawable drawable, int x, int y, int width, int height) {
		GL2 gl = drawable.getGL().getGL2();
		gl.glMatrixMode(GL2.GL_PROJECTION);
		gl.glLoadIdentity();
        
	    double distNear = 0.01;
	    double ar = ((double)width)/((double)height);
	    GLU glu = new GLU();
	    glu.gluPerspective(60, ar, distNear, 100);
	}

	@Override
	public void keyTyped(KeyEvent e) {
		// TODO Auto-generated method stub

	}

	@Override
	public void keyPressed(KeyEvent ev) {
		// TODO Auto-generated method stub
		
		int width = myTerrain.size().width;
		int height = myTerrain.size().height;
		double xTemp = xPos;
		double zTemp = zPos;
		double yRad = Math.toRadians(Yangle);
		
		switch (ev.getKeyCode()) {
		
		case KeyEvent.VK_UP:
			//Have to check that the movement still keeps the avatar within the terrain
			if (((xTemp += Math.cos(yRad))/teapotScale < width-1) 
					&& ((zTemp += Math.sin(yRad))/teapotScale < height-1)
					&& ((xTemp += Math.cos(yRad))/teapotScale >= 0) 
					&& ((zTemp += Math.sin(yRad))/teapotScale >= 0)) {
				
				xPos += Math.cos(yRad);
				zPos += Math.sin(yRad);
				
				if (xPos/teapotScale >= 7 && zPos/teapotScale >= 7
						&& xPos/teapotScale <= 8 && zPos/teapotScale <= 8) {
					xPos = 0;
					zPos = 0;
				}
			}
			 break;
		
		 case KeyEvent.VK_DOWN:
			//Have to check that the movement still keeps the avatar within the terrain
			 if ( ((xTemp -= Math.cos(yRad))/teapotScale < width-1) 
					 && ((zTemp -= Math.sin(yRad))/teapotScale < height-1) 
					 && ((xTemp -= Math.cos(yRad))/teapotScale >= 0) 
					 && ((zTemp -= Math.sin(yRad))/teapotScale >= 0) ) {
				 
				 xPos -= Math.cos(yRad);
				 zPos -= Math.sin(yRad);
				 
				 if (xPos/teapotScale >= 7 && zPos/teapotScale >= 7
						 && xPos/teapotScale <= 8 && zPos/teapotScale <= 8) {
						xPos = 0;
						zPos = 0;
					}
			 }
			 break;
			 
		 case KeyEvent.VK_LEFT:
			 Yangle -= 10.0;
			 if (Yangle < 0.0) Yangle += 360.0;
			 break;
			 
		 case KeyEvent.VK_RIGHT:
			 Yangle += 10.0;
			 if (Yangle > 360.0) Yangle -= 360.0;
			 break;
			 
			 //Press V to change from 1st to 3rd person view
		 case KeyEvent.VK_V:
			 thirdPersonView = !thirdPersonView;
			 break;
			
		 case KeyEvent.VK_Z:
			 Zangle -= 10.0;
			 if (Zangle < 0.0) Zangle += 360.0;
			 break;
			 
		 case KeyEvent.VK_X:
			 Zangle += 5.0;
			 if (Zangle > 360.0) Zangle -= 360.0;
			 break;
			 default:
				 break;
		}

	}

	@Override
	public void keyReleased(KeyEvent e) {
		// TODO Auto-generated method stub

	}
	
	/**
	 * Draws a 1x1xheight cube to represent a teleporter
	 * 
	 * @param gl
	 * @param x The starting x-position of the teleporter
	 * @param y The altitude at point x,z
	 * @param z The starting z-position of the teleporter
	 * @param height Height of the teleporter
	 */
	public void drawTeleport(GL2 gl, int x, double y, int z, int height){
		
		gl.glPolygonMode(GL2.GL_FRONT_AND_BACK, GL2.GL_FILL);
		float[] blue = {0.0f, 0.0f, 1.0f, 1.0f};
        gl.glMaterialfv(GL2.GL_FRONT, GL2.GL_DIFFUSE, blue, 0);
        float[] emiBlue = {0.0f, 0.0f, 0.4f, 1.0f};
        gl.glMaterialfv(GL2.GL_FRONT, GL2.GL_EMISSION, emiBlue, 0);
		
        //Draw Portal Cube
		gl.glBegin(GL2.GL_QUADS);
		{
			//Bottom
			gl.glVertex3d(x, 0, z);
			gl.glVertex3d(x, 0, z+1);
			gl.glVertex3d(x+1, 0, z+1);
			gl.glVertex3d(x+1, 0, z);
			
			//Top
			gl.glVertex3d(x, y+height, z);
			gl.glVertex3d(x, y+height, z+1);
			gl.glVertex3d(x+1, y+height, z+1);
			gl.glVertex3d(x+1, y+height, z);
			
			//North
			gl.glVertex3d(x+1, 0, z);
			gl.glVertex3d(x+1, y+height, z);
			gl.glVertex3d(x+1, y+height, z+1);
			gl.glVertex3d(x+1, 0, z+1);
			
			//South
			gl.glVertex3d(x, 0, z);
			gl.glVertex3d(x, 0, z+1);
			gl.glVertex3d(x, y+height, z+1);
			gl.glVertex3d(x, y+height, z);
			
			//West
			gl.glVertex3d(x, 0, z);
			gl.glVertex3d(x, y+height, z);
			gl.glVertex3d(x+1, y+height, z);
			gl.glVertex3d(x+1, 0, z);
			
			//East
			gl.glVertex3d(x, 0, z+1);
			gl.glVertex3d(x+1, 0, z+1);
			gl.glVertex3d(x+1, y+height, z+1);
			gl.glVertex3d(x, y+height, z+1);
		}
		gl.glEnd();
	}
}
