package spec;

import com.jogamp.opengl.GL2;

/**
 * COMMENT: Comment Tree
 *
 * @author malcolmr
 */
public class Tree {

	private double[] myPos;

	public Tree(double x, double y, double z) {
		myPos = new double[3];
		myPos[0] = x;
		myPos[1] = y;
		myPos[2] = z;

	}

	public double[] getPosition() {
		return myPos;
	}

	public void draw(GL2 gl, double height, double diameter, double leaves_radius,MyTexture myTextures[]) {
		int slices = 100;
		double y1 = 0;
		double y2 = height;
		//this.loadTextures(gl);

		// texture for trunk
		gl.glActiveTexture(GL2.GL_TEXTURE0);
		gl.glEnable(GL2.GL_TEXTURE_2D);
		gl.glBindTexture(GL2.GL_TEXTURE_2D,myTextures[1].getTextureId());

		//bottom circle
		gl.glBegin(GL2.GL_TRIANGLE_FAN);
			gl.glNormal3d(0, -1, 0);
			gl.glVertex3d(0, y1, 0);
			double angleStep = 2 * Math.PI / slices;
			for (int i = 0; i <= slices; i++) {
				double a0 = i * angleStep;
				double x0 = diameter * Math.cos(a0);
				double z0 = diameter * Math.sin(a0);
				gl.glVertex3d(x0, y1, z0);
			}
		gl.glEnd();
		
		//top circle
		gl.glBegin(GL2.GL_TRIANGLE_FAN);
			double angleStep2 = 2 * Math.PI / slices;
			gl.glNormal3d(0, 1, 0);
			gl.glVertex3d(0, y2, 0);
			for (int i = 0; i <= slices; i++) {
				double a0 = i * angleStep2;
				double x0 = diameter * Math.cos(a0);
				double z0 = diameter * Math.sin(a0);
				gl.glVertex3d(x0, y2, z0);
			}
		gl.glEnd();

		//sides
		gl.glBegin(GL2.GL_QUAD_STRIP);
			double angleStep3 = 2 * Math.PI / slices;
			for (int i = 0; i <= slices; i++) {
				double a0 = i * angleStep3;

				double x0 = diameter * Math.cos(a0);
				double z0 = diameter * Math.sin(a0);

				float s = i / (float) slices;

				gl.glNormal3d(x0, 0, z0);
				gl.glTexCoord2d(s, 0);
				gl.glVertex3d(x0, y1, z0);
				gl.glTexCoord2d(s, 1);
				gl.glVertex3d(x0, y2, z0);
			}
		gl.glEnd();

		// textures for leaves
		gl.glDisable(GL2.GL_TEXTURE_2D);
		gl.glActiveTexture(GL2.GL_TEXTURE0);
		gl.glEnable(GL2.GL_TEXTURE_2D);
		gl.glBindTexture(GL2.GL_TEXTURE_2D,myTextures[2].getTextureId());

		// Draw Spheres
		gl.glPushMatrix();
			gl.glTranslated(0, height, 0);
			gl.glRotated(90, 1, 0, 0);
			int stack = 20;
			int slice = 20;
			drawSphere(gl, leaves_radius, slice, stack, true);
		gl.glPopMatrix();
	
	}

	public static void drawSphere(GL2 gl, double radius, int slices, int stacks, boolean makeTexCoords) {
		for (int j = 0; j < stacks; j++) {
			double latitude1 = (Math.PI / stacks) * j - Math.PI / 2;
			double latitude2 = (Math.PI / stacks) * (j + 1) - Math.PI / 2;
			double sinLat1 = Math.sin(latitude1);
			double cosLat1 = Math.cos(latitude1);
			double sinLat2 = Math.sin(latitude2);
			double cosLat2 = Math.cos(latitude2);
			//starts drawing Quads to represent a spere which should be leaves
			gl.glBegin(GL2.GL_QUAD_STRIP);
			for (int i = 0; i <= slices; i++) {
				double longitude = (2 * Math.PI / slices) * i;
				double sinLong = Math.sin(longitude);
				double cosLong = Math.cos(longitude);
				double x1 = cosLong * cosLat1;
				double y1 = sinLong * cosLat1;
				double z1 = sinLat1;
				double x2 = cosLong * cosLat2;
				double y2 = sinLong * cosLat2;
				double z2 = sinLat2;
				gl.glNormal3d(x2, y2, z2);
				if (makeTexCoords)
					gl.glTexCoord2d(1.0 / slices * i, 1.0 / stacks * (j + 1));
				gl.glVertex3d(radius * x2, radius * y2, radius * z2);
				gl.glNormal3d(x1, y1, z1);
				if (makeTexCoords)
					gl.glTexCoord2d(1.0 / slices * i, 1.0 / stacks * j);
				gl.glVertex3d(radius * x1, radius * y1, radius * z1);
			}
			gl.glEnd();
		}
	}
}
