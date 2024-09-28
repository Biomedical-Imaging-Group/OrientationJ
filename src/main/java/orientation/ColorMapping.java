//=============================================================================================================
//
// Project: Directional Image Analysis - OrientationJ plugins
// 
// Author: Daniel Sage
// 
// Organization: Biomedical Imaging Group (BIG)
// Ecole Polytechnique Federale de Lausanne (EPFL), Lausanne, Switzerland
//
// OrientationJ: https://bigwww.epfl.ch/demo/orientation/
// MonogenicJ: https://bigwww.epfl.ch/demo/monogenic/
// Source code: https://github.com/Biomedical-Imaging-Group/OrientationJ
//  
// Reference on OrientationJ:
// Z. Püspöki, M. Storath, D. Sage, M. Unser
// Transforms and Operators for Directional Bioimage Analysis: A Survey 
// Focus on Bio-Image Informatics, Springer International Publishing, 2016.
//
// Reference on MonogenicJ:
// M. Unser, D. Sage, D. Van De Ville
// Multiresolution Monogenic Signal Analysis Using the Riesz-Laplace Wavelet Transform
// IEEE Transactions on Image Processing, 2009.
//
// Conditions of use: We expect you to include adequate citations and 
// acknowledgments whenever you present or publish results that are based on it.
//
// License: GNU GPLv3 <http://www.gnu.org/licenses/gpl-3.0.html>
//=============================================================================================================

package orientation;

import java.awt.Color;

import ij.ImagePlus;
import ij.ImageStack;
import ij.process.ColorProcessor;
import orientation.imageware.ImageWare;

public class ColorMapping {

	public static ImagePlus colorHSB(int n, String name, ImageWare hue, ImageWare sat, ImageWare bri) {
		int nx = hue.getSizeX();
		int ny = hue.getSizeY();
		ImageStack stack = new ImageStack(nx, ny);
		int size = nx*ny;
		for(int k=0; k<n; k++) {
			int[] pixels = new int[size];
			float[] h = hue.getSliceFloat((k < hue.getSizeZ() ? k : 0));
			float[] s = sat.getSliceFloat((k < sat.getSizeZ() ? k : 0));
			float[] b = bri.getSliceFloat((k < bri.getSizeZ() ? k : 0));
			for (int index=0; index<size; index++) {
				pixels[index] = Color.HSBtoRGB(h[index], s[index], b[index]) + (0xFF << 24);
					
			}
			stack.addSlice("", new ColorProcessor(nx, ny, pixels));
		}
		ImagePlus imp = new ImagePlus(name, stack);
		imp.show();
		return imp;
	}

	public static ImagePlus colorRGB(int n, String name, ImageWare red, ImageWare green, ImageWare blue) {
		int nx = red.getSizeX();
		int ny = red.getSizeY();
		ImageStack stack = new ImageStack(nx, ny);
		int size = nx*ny;
		for(int k=0; k<n; k++) {
			int[] pixels = new int[size];
			float[] r = red.getSliceFloat((k < red.getSizeZ() ? k : 0));
			float[] g = green.getSliceFloat((k < green.getSizeZ() ? k : 0));
			float[] b = blue.getSliceFloat((k < blue.getSizeZ() ? k : 0));
			for (int index=0; index<size; index++) {
				int ri = (int)(r[index]*255);
				int gi = (int)(g[index]*255);
				int bi = (int)(b[index]*255);
				pixels[index] = (bi + (gi<<8) + (ri<<16) + (0xFF << 24));
			}
			stack.addSlice("", new ColorProcessor(nx, ny, pixels));
		}
		ImagePlus imp = new ImagePlus(name, stack);
		imp.show();
		return imp;
	}
	
	/*
	private static void addBar(int bar, float[] r, float[] g, float[] b, int nx, int ny) {
		
		float ratio = 1.0f/(ny/2);
		int index;
		int len = ny/4;		
		for(int j=len; j<3*len; j++) {
			if (bar == 1) {
				for(int i=0; i<5; i++) {
					index = i+j*nx; r[index] = (j-len)*ratio; g[index] = 0; b[index] = 0;
				}
				for(int i=5; i<10; i++) {
					index = i+j*nx; r[index] = 0; g[index] = (j-len)*ratio; b[index] = 0;
				}
				for(int i=10; i<15; i++) {
					index = i+j*nx; r[index] = 0; g[index] = 0; b[index] = (j-len)*ratio;
				}
			}
			else {
				for(int i=nx-15; i<nx-10; i++) {
					index = i+j*nx; r[index] = (j-len)*ratio; g[index] = 0; b[index] = 0;
				}
				for(int i=nx-10; i<nx-5; i++) {
					index = i+j*nx; r[index] = 0; g[index] = (j-len)*ratio; b[index] = 0;
				}
				for(int i=nx-5; i<nx; i++) {
					index = i+j*nx; r[index] = 0; g[index] = 0; b[index] = (j-len)*ratio;
				}
			}
		}
	}
	*/

	/*
	private static void addRing(int bar, float[] h, float[] s, float[] b, int nx, int ny) {
		if (nx < 16)
			return;
		if (ny < 16)
			return;
			
		int index;
		int size = 16;
		float norm = (float)Math.sqrt(2)*size;	
		for(int i=-size; i<=size; i++)
		for(int j=-size; j<=size; j++) {
			float rad = (float)Math.sqrt(i*i+j*j);

			if (rad < size) {
				rad /= norm;
				float theta = (float)((Math.atan2(i, j)+Math.PI/2) / Math.PI);
				if (bar == 1) {
					index = (size+i+1) + nx*(j+ny/2-size); 
					h[index] = theta;
					s[index] = rad;
					b[index] = 1.0f;
					index = (size+i+1) + nx*(j+ny/2+size); 
					h[index] = theta;
					s[index] = 1.0f;
					b[index] = rad;
				}
				else {
					index = (nx-size-1+i) + nx*(j+ny/2-size); 
					h[index] = theta;
					s[index] = rad;
					b[index] = 1.0f;
					index = (nx-size-1+i) + nx*(j+ny/2+size); 
					h[index] = theta;
					s[index] = 1.0f;
					b[index] = rad;
				}
			}
		}
	}
	*/
	
}