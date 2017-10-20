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
	private Objects objects;
	
	//Colors
	float[] colorRed = {1.0f, 0.0f, 0.0f, 1.0f};
	float[] colorGreen = {0.0f, 1.0f, 0.0f, 1.0f};
	float[] colorBlue = {0.0f, 0.0f, 1.0f, 1.0f};
	float[] colorOrange = {1.0f, 0.7f, 1.0f, 1.0f};
	
	//Emission
	float[] emissionRed = {0.4f, 0.0f, 0.0f, 1.0f};
	float[] emissionGreen = {0.0f, 0.4f, 0.0f, 1.0f};
	float[] emissionBlue = {0.0f, 0.0f, 0.4f, 1.0f};
	float[] emissionOrange = {0.7f, 0.3f, 0.0f, 1.0f};
	
	private final int NUM_TEXTURES = 4;
	private MyTexture myTextures[];
	// Texture file information
	private String leafTexture = "src/spec/leaves.png";
	private String trunkTexture = "src/spec/tree_bark.png";
	private String roadTexture = "src/spec/road4.png";

	
	//Portal positions
	int portal1X = 0;
	int portal1Z = 0;
	int portal2X = 0;
	int portal2Z = 0;

	public Game(Terrain terrain) {
		super("Assignment 2");
		myTerrain = terrain;
		objects = new Objects();
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
		
		//Draw the teapot
		objects.drawTeapot(gl, myTerrain, 0.2);
		
		//Draw portal 1
		portal1X = 3;
		portal1Z = 7;
		double portal1Y = myTerrain.getGridAltitude(portal1X, portal1Z);
		int portal1Height = 1;
		objects.drawPortal(gl, portal1X, portal1Y, portal1Z, portal1Height, colorBlue, emissionBlue);
		
		//Draw portal 2
		portal2X = 6;
		portal2Z = 1;
		double portal2Y = myTerrain.getGridAltitude(portal2X, portal2Z);
		int portal2Height = 1;
		objects.drawPortal(gl, portal2X, portal2Y, portal2Z, portal2Height, colorOrange, emissionOrange);
		
		//Set texture modes
		gl.glTexEnvf(GL2.GL_TEXTURE_ENV, GL2.GL_TEXTURE_ENV_MODE, GL2.GL_REPLACE);
		gl.glTexParameteri(GL2.GL_TEXTURE_2D, GL2.GL_TEXTURE_WRAP_S, GL2.GL_REPEAT);
		
		//Set grass texture
		gl.glBindTexture(GL2.GL_TEXTURE_2D, myTextures[0].getTextureId());
		
		//Draw the terrain
		myTerrain.drawTerrain(gl,this.myTextures);

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
		// Texture of the leaf
		myTextures[2] = new MyTexture(gl, leafTexture, true);
		// Texture of trunk
		myTextures[1] = new MyTexture(gl, trunkTexture, true);
		//texture of road
		myTextures[3] = new MyTexture(gl, roadTexture, true);
		// normalise normals (!)
		// this is necessary to make lighting work properly
		Others temp = new Others();
		temp.display(drawable);
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
		double speed = objects.getSpeed();
		double xTemp = objects.getXtea();
		double zTemp = objects.getZtea();
		double Yangle = objects.getYangle();
		double yRad = Math.toRadians(Yangle);
		
		switch (ev.getKeyCode()) {
		
		case KeyEvent.VK_UP:
			//Have to check that the movement still keeps the avatar within the terrain
			if (((xTemp += Math.cos(yRad)*speed) < width-1) 
					&& ((zTemp += Math.sin(yRad)*speed) < height-1)
					&& ((xTemp += Math.cos(yRad)*speed) >= 0) 
					&& ((zTemp += Math.sin(yRad)*speed) >= 0)) {
				
				double x = objects.getXtea();
				double z = objects.getZtea();
				x += Math.cos(yRad)*speed;
				z += Math.sin(yRad)*speed;
				objects.setXtea(x);
				objects.setZtea(z);
				
				//If avatar goes through portal 1, change location to portal 2
				if (x >= portal1X && z >= portal1Z
						&& x <= portal1X+0.3 && z <= portal1Z+1) {
					
					//Set new x position of teapot to be xPos of portal + a few steps to avoid loop. 
					//The math is there to get the correct direction when going out of the portal
					objects.setXtea( portal2X + (Math.cos(yRad)*0.3) );
					
					//Set new x position of teapot to be zPos of portal + 1/2 square to spawn in the 
					//middle of the other portal
					objects.setZtea( portal2Z + 0.5 );
				}
				
				//If avatar goes through portal 2, change location to portal 1
				if (x >= portal2X && z >= portal2Z
						&& x <= portal2X+0.3 && z <= portal2Z+1) {
					
					objects.setXtea( portal1X + (Math.cos(yRad)*0.3) );
					objects.setZtea( portal1Z + 0.5);
				}
			}
			 break;
		
		 case KeyEvent.VK_DOWN:
			//Have to check that the movement still keeps the avatar within the terrain
			 if ( ((xTemp -= Math.cos(yRad)*speed) < width-1) 
					 && ((zTemp -= Math.sin(yRad)*speed) < height-1) 
					 && ((xTemp -= Math.cos(yRad)*speed) >= 0) 
					 && ((zTemp -= Math.sin(yRad)*speed) >= 0) ) {
				 
				double x = objects.getXtea();
				double z = objects.getZtea();
				x -= Math.cos(yRad)*speed;
				z -= Math.sin(yRad)*speed;
				objects.setXtea(x);
				objects.setZtea(z);
				 
				//If avatar goes through portal 1, change location to portal 2
				 if (x >= portal1X && z >= portal1Z
							&& x <= portal1X+0.3 && z <= portal1Z+1) {
					 
						objects.setXtea( portal2X - (Math.cos(yRad)*0.3) );
						objects.setZtea( portal2Z + 0.5 );
					}
					
				//If avatar goes through portal 2, change location to portal 1
					if (x >= portal2X &&  z >= portal2Z
							&& x <= portal2X+0.3 && z <= portal2Z+1) {
						
						objects.setXtea( portal1X - (Math.cos(yRad)*0.3) );
						objects.setZtea( portal1Z + 0.5 );
					}
			 }
			 break;
			 
		 case KeyEvent.VK_LEFT:
			 Yangle -= 10.0;
			 if (Yangle < 0.0) Yangle += 360.0;
			 objects.setYangle(Yangle);
			 break;
			 
		 case KeyEvent.VK_RIGHT:
			 Yangle += 10.0;
			 if (Yangle > 360.0) Yangle -= 360.0;
			 objects.setYangle(Yangle);
			 break;
			 
			 //Press V to change from 1st to 3rd person view
		 case KeyEvent.VK_V:
			 objects.thirdPersonView = !objects.thirdPersonView;
			 break;
		}

	}

	@Override
	public void keyReleased(KeyEvent e) {
		// TODO Auto-generated method stub

	}
}
