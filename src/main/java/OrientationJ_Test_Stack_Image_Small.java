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

import ij.ImagePlus;
import ij.ImageStack;
import ij.plugin.PlugIn;
import ij.process.FloatProcessor;
import orientation.TestImage;

public class OrientationJ_Test_Stack_Image_Small implements PlugIn {

	public static void main(String arg[]) {
		new OrientationJ_Test_Stack_Image_Small().run("");
	}

	public void run(String arg) {
		int nx = 256;
		int ny = 256;
		int nt = 4;
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
