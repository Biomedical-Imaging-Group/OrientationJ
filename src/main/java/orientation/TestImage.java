//=============================================================================================================
//
// Project: Directional Image Analysis - OrientationJ plugins
// 
// Author: Daniel Sage
// 
// Organization: Biomedical Imaging Group (BIG)
// Ecole Polytechnique Federale de Lausanne (EPFL), Lausanne, Switzerland
//
// Information: 
// OrientationJ: http://bigwww.epfl.ch/demo/orientation/
// MonogenicJ: http://bigwww.epfl.ch/demo/monogenic/
//  
// Reference on methods and plugins
// Z. Püspöki, M. Storath, D. Sage, M. Unser
// Transforms and Operators for Directional Bioimage Analysis: A Survey 
// Advances in Anatomy, Embryology and Cell Biology, vol. 219, Focus on Bio-Image Informatics 
// Springer International Publishing, ch. 33, 2016.
//
//
// Reference the application measure of coherency
// R. Rezakhaniha, A. Agianniotis, J.T.C. Schrauwen, A. Griffa, D. Sage, 
// C.V.C. Bouten, F.N. van de Vosse, M. Unser, N. Stergiopulos
// Experimental Investigation of Collagen Waviness and Orientation in the Arterial Adventitia 
// Using Confocal Laser Scanning Microscopy
// Biomechanics and Modeling in Mechanobiology, vol. 11, no. 3-4, 2012.

// Reference the application direction of orientation
// E. Fonck, G.G. Feigl, J. Fasel, D. Sage, M. Unser, D.A. Ruefenacht, N. Stergiopulos 
// Effect of Aging on Elastin Functionality in Human Cerebral Arteries
// Stroke, vol. 40, no. 7, 2009.
//
// Conditions of use: You are free to use this software for research or
// educational purposes. In addition, we expect you to include adequate
// citations and acknowledgments whenever you present or publish results that
// are based on it.
//
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
