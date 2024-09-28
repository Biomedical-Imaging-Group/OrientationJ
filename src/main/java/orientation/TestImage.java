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

import ij.process.FloatProcessor;

public class TestImage {

	public static FloatProcessor chirp(int nx, int ny) {
		double fmin = 0.02;
		double fmax = 8 * fmin;
		double hx = nx * 0.5;
		double hy = ny * 0.5;
		int n = Math.min(nx, ny);
		FloatProcessor fp = new FloatProcessor(nx, ny);
		for (int i = 0; i < nx; i++)
			for (int j = 0; j < ny; j++) {
				double r = Math.sqrt((i - hx) * (i - hx) + (j - hy) * (j - hy));
				double u = 1.0 / (1.0 + Math.exp((r - n * 0.45) / 2.0));
				double f = fmin + r * (fmax - fmin) / n;
				double v = Math.sin(Math.PI * 2 * f * r);
				fp.putPixelValue(i, j, (1.0 + v * u) * 128);
			}
		return fp;
	}
	
	public static FloatProcessor wave(int nx, int ny, double angle, double freq) {
		double hx = nx*0.5;
		double hy = ny*0.5;
		double cosa = Math.cos(angle);
		double sina = Math.sin(angle);
		FloatProcessor fp  = new FloatProcessor(nx, ny);
		for(int i=0; i<nx; i++)
		for(int j=0; j<ny; j++) {
			double u =  (i-hx)*cosa + (j-hy)*sina;
			double w = Math.sin(Math.PI * 2 * freq * u);
			fp.putPixelValue(i, j, w * 100);
		}
		return fp;
	}
}
