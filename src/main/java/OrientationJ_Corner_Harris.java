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

import gui_orientation.AnalysisDialog;
import gui_orientation.WalkBarOrientationJ;
import ij.Macro;
import ij.plugin.PlugIn;
import orientation.GroupImage;
import orientation.OrientationParameters;
import orientation.OrientationProcess;
import orientation.OrientationResults;
import orientation.OrientationService;
import orientation.imageware.ImageWare;

public class OrientationJ_Corner_Harris implements PlugIn {

	public static void main(String arg[]) {
		new OrientationJ_Test_Stack_Image_Small().run("");
		new OrientationJ_Corner_Harris().run("");
	}

	public void run(String arg) {
		
		if (Macro.getOptions() == null) {
			AnalysisDialog orientation = new AnalysisDialog(OrientationService.HARRIS);
			orientation.showDialog();
		}
		else {
			OrientationParameters params = new OrientationParameters(OrientationService.HARRIS);
			params.getMacroParameters(Macro.getOptions());
			ImageWare source = GroupImage.getCurrentImage();
			if (source == null) {
				return;
			}
			WalkBarOrientationJ walk = new WalkBarOrientationJ();
			OrientationProcess process = new OrientationProcess(walk, source, params);
			process.run();
			OrientationResults.show(process.getGroupImage(), params, 1);
		}
	}
	
}