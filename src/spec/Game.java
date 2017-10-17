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
	private int currIndex = 0; // Currently displayed texture index
	
	//For rotation TESTING
	private double Xangle = 0;
    private double Yangle = 0;
	private double Zangle = 0;

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
		
		//gl.glTranslated(0, 0, -3);
        // Commands to turn the cylinder.
        gl.glRotated(Zangle, 0.0, 0.0, 1.0);
        gl.glRotated(Yangle, 0.0, 1.0, 0.0);
        gl.glRotated(Xangle, 1.0, 0.0, 0.0);
        //gl.glTranslated(0, 0, 3);
		
		gl.glTexEnvf(GL2.GL_TEXTURE_ENV, GL2.GL_TEXTURE_ENV_MODE, GL2.GL_REPLACE); //***
		gl.glTexParameteri(GL2.GL_TEXTURE_2D, GL2.GL_TEXTURE_WRAP_S, GL2.GL_REPEAT); //***

		// Forgetting to clear the depth buffer can cause problems
		// such as empty black screens.
		//gl.glClear(GL.GL_COLOR_BUFFER_BIT | GL2.GL_DEPTH_BUFFER_BIT);
		// gl.glPolygonMode(GL.GL_FRONT_AND_BACK, GL2.GL_LINE);
		//gl.glColor3f(0, 1, 0);
		//GLUT glut = new GLUT();
		// Move teapot so it is not in the same
		// pos as the default light and camera and so it is not clipped
		// gl.glTranslated(0,0,-2);

		//glut.glutSolidTeapot(1);
		//gl.glPolygonMode(GL.GL_FRONT_AND_BACK, GL2.GL_FILL);
		
		//Set current texture
		//gl.glBindTexture(GL2.GL_TEXTURE_2D, myTextures[currIndex].getTextureId());
		gl.glBindTexture(GL2.GL_TEXTURE_2D, myTextures[0].getTextureId());
		
		//Draw the terrain
		myTerrain.drawTerrain(gl);
		//gl.glPolygonMode(GL2.GL_FRONT_AND_BACK, GL2.GL_FILL);

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
		
		/*
		//glFrustum(Left, Right, Bottom, Top, Near, Far)
		gl.glFrustum(-1.0, 4.0, -1.0, 3.0, 5.0, 100.0);
        gl.glMatrixMode(GL2.GL_MODELVIEW);
        gl.glLoadIdentity();
        */

        
	    double distNear = 0.01;
	    double ar = ((double)width)/((double)height);
	    distNear = 2;
	    GLU glu = new GLU();
	    glu.gluPerspective(60, ar, distNear, 100);
	    
	    //gluLookAt(eyeX, eyeY, eyeZ, centerX, centerY, centerZ, upX, upY, upZ)
	    glu.gluLookAt(0, 5, -9, 5, 0, 5, 0, 1, 0);
	    

	}

	@Override
	public void keyTyped(KeyEvent e) {
		// TODO Auto-generated method stub

	}

	@Override
	public void keyPressed(KeyEvent ev) {
		// TODO Auto-generated method stub
		switch (ev.getKeyCode()) {
		
		 case KeyEvent.VK_DOWN:
			 Xangle += 5.0;
			 if (Xangle > 360.0) Xangle -= 360.0;
			 break;
			 
		 case KeyEvent.VK_UP:
			 Xangle -= 5.0;
			 if (Xangle < 0.0) Xangle += 360.0;
			 break;
			 
		 case KeyEvent.VK_LEFT:
			 Yangle -= 5.0;
			 if (Yangle < 0.0) Yangle += 360.0;
			 break;
			 
		 case KeyEvent.VK_RIGHT:
			 Yangle += 5.0;
			 if (Yangle > 360.0) Yangle -= 360.0;
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
