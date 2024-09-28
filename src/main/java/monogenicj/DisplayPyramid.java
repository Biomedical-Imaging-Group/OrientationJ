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

package monogenicj;

import java.awt.Color;

import ij.IJ;
import ij.ImagePlus;
import ij.ImageStack;
import ij.process.ColorProcessor;
import orientation.imageware.Builder;
import orientation.imageware.ImageWare;

public class DisplayPyramid {

	public static int RESCALE = 0;
	public static int NORESCALE = 1;
	
	/**
	*/
	public static void show(ImageWare image, String title, int scaled, int stacked, boolean pyramid) {
		ImageWare disp = create(image, scaled, stacked, pyramid);
		disp.show(title);
	}
	
	/**
	*	Stacked = 0 -> Stack
	*	Stacked = 1 -> Flatten vertical
	*	Stacked = 2 -> Flatten horizontal
	*/
	public static ImageWare create(ImageWare image, int scaled, int stacked, boolean pyramid) {
		int py = 0;
		int px = 0;
		int mx = image.getWidth();
		int my = image.getHeight();
		int nx = mx;
		int ny = my;
		int nscale = image.getSizeZ();
		int factSize = nscale;
		if (pyramid) 
			factSize = (nscale > 1 ? 2 : 1);
			
		ImageWare out;
		if (stacked == 0)
			out = Builder.create(nx, ny, nscale, (scaled==RESCALE ? ImageWare.BYTE : ImageWare.FLOAT)); 
		else if (stacked == 1)
			out = Builder.create(nx*factSize, ny, 1, (scaled==RESCALE ? ImageWare.BYTE : ImageWare.FLOAT));
		else 
			out = Builder.create(nx, ny*factSize, 1, (scaled==RESCALE ? ImageWare.BYTE : ImageWare.FLOAT));
		
		for(int k=0; k<nscale; k++) {
			ImageWare band = Builder.create(mx, my, 1, ImageWare.FLOAT);
			image.getXY(0, 0, k, band);
			if (scaled == RESCALE)
				band.rescale();
			if (stacked == 0)
				out.putXY(0, 0, k, band);
			else if (stacked == 1)
				out.putXY(px, 0, 0, band);
			else
				out.putXY(0, py, 0, band);
			if (pyramid) {
				mx /= 2;
				my /= 2;
				if (stacked == 1)
					px += nx / Math.pow(2, k);
				if (stacked == 2)
					py += ny / Math.pow(2, k);
			}
			else {
				if (stacked == 1)
					px += nx;
				if (stacked == 2)
					py += ny;
			}
		}
		return out;
	}

	/**
	*/
	public static ImagePlus colorHSB(String name, ImageWare hue, ImageWare sat, ImageWare bri, int stacked, boolean pyramid) {
		int py = 0;
		int px = 0;
		int nx = hue.getSizeX();
		int ny = hue.getSizeY();
		int mx = nx;
		int my = ny;
		int nscale = hue.getSizeZ();
		int fx = nx;
		int fy = ny;

		if (pyramid) {
			if (stacked == 1)
				fx = nx*(nscale > 1 ? 2 : 1);
			if (stacked == 2)
				fy = ny*(nscale > 1 ? 2 : 1);
		}
		else {
			if (stacked == 1)
				fx = nx*nscale;
			if (stacked == 2)
				fy = ny*nscale;
		}
		
		int size = fx*fy;
		ImageStack stack = new ImageStack(fx, fy);

		int[] cpixels = new int[size];
		for(int k=0; k<nscale; k++) {
			IJ.showStatus("Show Color Image " + (k+1) + "/" + nscale);
			if (stacked == 0) {
				ColorProcessor cp = new ColorProcessor(fx, fy);
				for (int y=0; y<my; y++)
				for (int x=0; x<mx; x++) {
					float h = (float)hue.getPixel(x, y, k);
					float s = (float)sat.getPixel(x, y, k);
					float b = (float)bri.getPixel(x, y, k);
					cp.putPixel(x, y, Color.HSBtoRGB(h, s, b) + (0xFF << 24));
				}
				stack.addSlice("", cp);
			}
			else {
				int[] pixels = new int[size];
				for (int y=0; y<my; y++)
				for (int x=0; x<mx; x++) {
					float h = (float)hue.getPixel(x, y, k);
					float s = (float)sat.getPixel(x, y, k);
					float b = (float)bri.getPixel(x, y, k);
					cpixels[(py+y)*fx+(x+px)] = Color.HSBtoRGB(h, s, b) + (0xFF << 24);
				}
			}
			
			if (pyramid) {
				mx /= 2;
				my /= 2;
				if (stacked == 1)
					px += nx / Math.pow(2, k);
				if (stacked == 2)
					py += ny / Math.pow(2, k);
			}
			else {
				if (stacked == 1)
					px += nx;
				if (stacked == 2)
					py += ny;
			}
		}
		if (stacked != 0)
			stack.addSlice("", new ColorProcessor(fx, fy, cpixels));
		ImagePlus imp = new ImagePlus(name, stack);
		imp.show();
		return imp;
	}

	/**
	*/
	public static ImagePlus colorRGB(String name, ImageWare red, ImageWare green, ImageWare blue) {
		int nx = red.getSizeX();
		int ny = red.getSizeY();
		int nz = red.getSizeZ();
		ImageStack stack = new ImageStack(nx, ny);
		int size = nx*ny;
		for(int k=0; k<nz; k++) {
			IJ.showStatus("Show Color Image " + (k+1) + "/" + nz);
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
			IJ.showProgress((double)k/nz);
		}
		ImagePlus imp = new ImagePlus(name, stack);
		imp.show();
		return imp;
	}
	
	/**
	*/
	public static ImageWare rescaleAngle(ImageWare in, boolean pyramid) {
		int nx = in.getSizeX();
		int ny = in.getSizeY();
		int nz = in.getSizeZ();
		int mx = nx;
		int my = ny;
		
		float PI = (float)Math.PI;
		float PI2 = (float)Math.PI/2;
		
		ImageWare out = in.replicate();
		for(int k=0; k<nz; k++) {
			ImageWare band = Builder.create(mx, my, 1, ImageWare.FLOAT);
			in.getXY(0, 0, k, band);
			float[] pix	= band.getSliceFloat(0);	
			float[] opix = out.getSliceFloat(k);	
			for (int x=0; x<mx; x++)
			for (int y=0; y<my; y++) 
				opix[x+y*nx] = (pix[x+y*mx] + PI2) / PI;
 			if (pyramid) {
				mx /= 2;
				my /= 2;
			}
		}
		return out;
	}
	
}