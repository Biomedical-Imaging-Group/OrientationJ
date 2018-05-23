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

import java.text.DecimalFormat;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import ij.IJ;
import ij.ImagePlus;
import ij.ImageStack;
import ij.WindowManager;
import ij.measure.ResultsTable;
import ij.plugin.PlugIn;
import ij.process.ImageProcessor;
import orientation.Gradient;
import orientation.GroupImage;
import orientation.LogMute;
import orientation.OrientationParameters;
import orientation.OrientationService;

public class OrientationJ_Dominant_Direction implements PlugIn {

	public static void main(String arg[]) {
		new OrientationJ_Test_Stack_Image_Small().run("");
		new OrientationJ_Dominant_Direction().run("");
	}

	public void run(String arg) {

		if (IJ.versionLessThan("1.21a")) {
			return;
		}
		ImagePlus imp = WindowManager.getCurrentImage();
		if (imp == null) {
			IJ.error("No open image.");
			return;
		}
		if (imp.getType() != ImagePlus.GRAY8 && imp.getType() != ImagePlus.GRAY16 && imp.getType() != ImagePlus.GRAY32) {
			IJ.error("Only processed 8-bits, 16-bits, or 32 bits images.");
			return;
		}
		ImageStack stack = imp.getStack();
		int nt = stack.getSize();
		IJ.log("Frame, Orientation [Degrees], Coherency [%]");
		String formats[] = {"#0.00", "#0.0000", "#0.00000"};
		ResultsTable table = new ResultsTable();
		int slice = imp.getSlice();
		for (int i=1; i<=nt; i++) {
			table.incrementCounter();
			imp.setSlice(i);
			ImageProcessor ip = imp.getProcessor().crop();
			ip.crop();
			double res[] = computeSpline(ip);
			String s1 = (new DecimalFormat(formats[0])).format(i);
			String s2 = (new DecimalFormat(formats[1])).format(res[0]);
			String s3 = (new DecimalFormat(formats[2])).format(res[1]);
			table.addValue("Slice", i);
			table.addValue("Orientation [Degrees]", res[0]);
			table.addValue("Coherency [%]", res[1]);
			IJ.log(s1 + ", " + s2 + ", " + s3);
		}
		table.show("Dominant Direction of " + imp.getTitle());
		imp.setSlice(slice);
	}
	
	public double[] computeSpline(ImageProcessor ip) {
		LogMute log = new LogMute();
		OrientationParameters params = new OrientationParameters(OrientationService.DOMINANTDIRECTION);
		params.gradient = OrientationParameters.GRADIENT_CUBIC_SPLINE;
		GroupImage gim = new GroupImage(log, ip, params);
		Gradient gradient = new Gradient(log, gim, params);
		
		ExecutorService executor = Executors.newFixedThreadPool(1);
		executor.execute(gradient);
		executor.shutdown();
		while (!executor.isTerminated()) {
		}

		int nx = gim.nx;
		int ny = gim.ny;
		
		double dy = 0;
		double dx = 0;
		double vxy = 0;
		double vxx = 0;
		double vyy = 0;
	
		int area = (nx-2)*(ny-2);
		for(int y=1; y<ny-1; y++)
		for(int x=1; x<nx-1; x++) {
			dx = gim.gx.getPixel(x, y, 0);
			dy = gim.gy.getPixel(x, y, 0);
			vxx += dx * dx;
			vyy += dy * dy;
			vxy += dx * dy;
		}	
		vxy /= area;
		vxx /= area;
		vyy /= area;
		double orientation = Math.toDegrees(0.5*Math.atan2(2.0*vxy, vyy-vxx));
		double d = vyy - vxx;
		double coherency = 0.0;
		if (vxx+vyy > 1) {
			coherency = Math.sqrt(d*d + vxy*vxy*4.0)/(vxx+vyy);
		}
		return new double[] {orientation, coherency};
	}
}
