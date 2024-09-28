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

import ij.ImagePlus;
import ij.ImageStack;
import ij.plugin.PlugIn;
import ij.process.FloatProcessor;
import orientation.TestImage;

public class OrientationJ_Test_Stack_Image_Large implements PlugIn {

	public static void main(String arg[]) {
		new OrientationJ_Test_Stack_Image_Large().run("");
	}

	public void run(String arg) {
		int nx = 1024;
		int ny = 1024;
		int nt = 12;
		ImageStack stack = new ImageStack(nx, ny);
		for(int k=0; k<nt; k++) {
			FloatProcessor c = TestImage.chirp(nx, ny);
			FloatProcessor w = TestImage.wave(nx, ny, k*Math.PI/nt, 0.1);
			FloatProcessor fp = add(c, w);
			stack.addSlice("", fp);
		}
		new ImagePlus("Test Stack", stack).show();
	}
	
	private FloatProcessor add(FloatProcessor fp1, FloatProcessor fp2) {
		int ny = Math.min(fp1.getHeight(), fp2.getHeight());
		int nx = Math.min(fp1.getWidth(), fp2.getWidth());
		FloatProcessor fp  = new FloatProcessor(nx, ny);
		for(int i=0; i<nx; i++)
		for(int j=0; j<ny; j++)
			fp.putPixelValue(i, j, (fp1.getPixelValue(i, j) + fp2.getPixelValue(i, j)));
		return fp;
	}
}
