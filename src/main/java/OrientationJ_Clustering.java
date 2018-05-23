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
 
import gui_orientation.AnalysisDialog;
import gui_orientation.WalkBarOrientationJ;
import ij.Macro;
import ij.plugin.PlugIn;
import orientation.GroupImage;
import orientation.OrientationParameters;
import orientation.OrientationProcess;
import orientation.OrientationService;
import orientation.imageware.ImageWare;

public class OrientationJ_Clustering implements PlugIn {

	public static void main(String arg[]) {
		new OrientationJ_Test_Stack_Image_Small().run("");
		new OrientationJ_Clustering().run("");
	}

	public void run(String arg) {
		if (Macro.getOptions() == null) {
			AnalysisDialog orientation = new AnalysisDialog(OrientationService.CLUSTERING);
			orientation.showDialog();
		}
		else {
			OrientationParameters params = new OrientationParameters(OrientationService.CLUSTERING);
			params.getMacroParameters(Macro.getOptions());
			ImageWare source = GroupImage.getCurrentImage();
			if (source == null) {
				return;
			}
			WalkBarOrientationJ walk = new WalkBarOrientationJ();
			OrientationProcess process = new OrientationProcess(walk, source, params);
			process.run();
		}
	}
}