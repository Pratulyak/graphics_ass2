package spec;

import java.util.ArrayList;
import java.util.List;

import com.jogamp.opengl.GL2;

@SuppressWarnings("unused")
public class Objects {

	private double x;
	private double y;
	private double z;

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

}
