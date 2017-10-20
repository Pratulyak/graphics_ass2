package spec;

import com.jogamp.opengl.glu.GLU;

public class Camera extends Objects{
	
	//Variables for the camera view
	public boolean thirdPersonView = true;
	private double eyeX = 0;
	private double eyeY = 0;
	private double eyeZ = 0;
	private double centerX = 0;
	private double centerY = 0;
	private double centerZ = 0;
	
	public Camera(){
		
	}
	
	public void updateCamera(Objects objects) {
		//Camera that follows the teapot (3rd person view)
				GLU glu = new GLU();
				
				//Setting camera coordinates according to the view mode
				if (thirdPersonView) {
					eyeX = objects.getXtea()-3;
					eyeY = (2 + objects.getAltitude());
					eyeZ = objects.getZtea();
					centerX = objects.getXtea()+1;
					centerY = objects.getAltitude();
					centerZ = objects.getZtea();
				} else {
					eyeX = objects.getXtea()+0.2;
					eyeY = objects.getAltitude();
					eyeZ = objects.getZtea();
					centerX = objects.getXtea()+2;
					centerY = objects.getAltitude();
					centerZ = objects.getZtea();
				}
				
				//gluLookAt(eyeX, eyeY, eyeZ, centerX, centerY, centerZ, upX, upY, upZ)
				glu.gluLookAt(eyeX, eyeY, eyeZ, centerX, centerY, centerZ, 0, 1, 0);
	}

}
