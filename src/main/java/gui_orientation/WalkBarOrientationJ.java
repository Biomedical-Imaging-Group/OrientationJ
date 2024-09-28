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

package gui_orientation;
import gui_orientation.components.WalkBar;

public class WalkBarOrientationJ extends WalkBar {
	
	/**
	* Constructor
	*/
	public WalkBarOrientationJ() {
		super(Constants.copyright, true, false, true);
		fillAbout(
			Constants.softname + " " + Constants.version, 
			Constants.date,
			"",
			Constants.author,
			"Biomedical Imaging Group (BIG)<br>Ecole Polytechnique F&eacute;d&eacute;rale de Lausanne (EPFL)<br>Lausanne, Switzerland",
			"",
			"https://bigwww.epfl.ch/demo/orientation/" +
			"<br><br>" +
			"<i>Z. Püspöki, M. Storath, D. Sage, M. Unser</i><br>" +
			"<b>Transforms and Operators for Directional Bioimage Analysis: A Survey</b><br>" +
			"Focus on Bio-Image Informatics, Springer International Publishing, 2016.");		
		
	}
}

