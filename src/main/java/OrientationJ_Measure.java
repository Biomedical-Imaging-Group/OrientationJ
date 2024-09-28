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

import gui_orientation.MeasureDialog;
import ij.IJ;
import ij.ImagePlus;
import ij.Macro;
import ij.WindowManager;
import ij.plugin.PlugIn;

public class OrientationJ_Measure implements PlugIn {

	public static void main(String arg[]) {
		new OrientationJ_Test_Stack_Image_Small().run("");
		new OrientationJ_Measure().run("");
	}

	public void run(String arg) {
	
		ImagePlus imp = WindowManager.getCurrentImage();
		if (imp == null) {
			IJ.noImage();
			return;
		}
		
		if (imp.getType() != ImagePlus.GRAY8 && imp.getType() != ImagePlus.GRAY16 && imp.getType() != ImagePlus.GRAY32) {
			IJ.error("Only process 8-bits, 16 bits or 32 bits image.");
			return;
		}
		
		if (Macro.getOptions() == null) {
			MeasureDialog dialog = new MeasureDialog(imp, false, 0.0);
			dialog.showDialog();
		}
		else {
			String options = Macro.getOptions();
			double sigma = Double.parseDouble(Macro.getValue(options, "sigma", "0.0"));
			MeasureDialog dialog = new MeasureDialog(imp, true, sigma);
			dialog.run();	
		}
	}
}