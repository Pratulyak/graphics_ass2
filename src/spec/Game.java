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
	
	private final int NUM_TEXTURES = 1;
	private MyTexture myTextures[];
	
	//For rotation TESTING
	private double Xangle = 0;
    private double Yangle = 0;
	private double Zangle = 0;
	
	private double xMove = 0;
    private double yMove = 0;
	private double zMove = 0;

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
		
		// Commands to turn the terrain.
        gl.glRotated(Zangle, 0.0, 0.0, 1.0);
        gl.glRotated(Yangle, 0.0, 1.0, 0.0);
        gl.glRotated(Xangle, 1.0, 0.0, 0.0);
        
        //How much to scale down the teapot
        double scale = 5;
       
        double altitude;
        double width = myTerrain.size().getWidth();
        double height = myTerrain.size().getHeight();
        
        //Get the altitude of the terrain at the teapot's current location
        //Have to check that the teapot is inside the terrain
        //Because of scaling, the variables xMove and zMove have to be divided
        if (xMove >= 0 && zMove >= 0 && xMove/scale < (width-1) && zMove/scale < (height-1)) {
        	altitude = myTerrain.altitude(xMove/scale, zMove/scale);
        } else {
        	altitude = 0;
        }
		
        //Scale down for the teapot
		gl.glScaled(1.0/scale, 1.0/scale, 1.0/scale);
		
		//Camera that follows the teapot (3rd person view)
		GLU glu = new GLU();
		
		//Set the position of the camera. Because of scaling, the altitude is multiplied
		//gluLookAt(eyeX, eyeY, eyeZ, centerX, centerY, centerZ, upX, upY, upZ)
		glu.gluLookAt(xMove-15, (10 + altitude*scale), zMove, xMove+5, altitude*scale, zMove, 0, 1, 0);
        
        //Set color to the teapot
        float[] red = {1.0f, 0.0f, 0.0f, 1.0f};
        gl.glMaterialfv(GL2.GL_FRONT, GL2.GL_DIFFUSE, red, 0);
        float[] emi = {0.4f, 0.0f, 0.0f, 1.0f};
        gl.glMaterialfv(GL2.GL_FRONT, GL2.GL_EMISSION, emi, 0);
        
        //Move the coordinate system (aka move the teapot)
		gl.glTranslated(xMove,1+altitude*scale,zMove);

		//Draw the teapot
		GLUT glut = new GLUT();
		glut.glutSolidTeapot(1);
		
		//Reset the coordinate system after the teapot
		gl.glTranslated(-xMove,-(1+altitude*scale),-zMove);
		gl.glScaled(scale, scale, scale);
		
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
	    distNear = 2;
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
		switch (ev.getKeyCode()) {
		
		case KeyEvent.VK_UP:
			 xMove += 1.0;
			 break;
		
		 case KeyEvent.VK_DOWN:
			 xMove -= 1.0;
			 break;
			 
		 case KeyEvent.VK_LEFT:
			 zMove -= 1.0;
			 break;
			 
		 case KeyEvent.VK_RIGHT:
			 zMove += 1.0;
			 break;
			
		 case KeyEvent.VK_Z:
			 Zangle -= 5.0;
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
}
