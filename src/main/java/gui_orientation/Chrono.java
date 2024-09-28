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

import java.text.DecimalFormat;

/**
 * This class provides static methods to measures the elapsed time.
 * It is a equivalent to the function tic and toc of Matlab. 
 * 
 * @author Daniel Sage, Biomedical Imaging Group, EPFL, Lausanne, Switzerland.
 *
 */  
public class Chrono {

	static private double chrono = 0;
	
	/**
	* Register the current time.
	*/
	public static void tic() {
		chrono = System.currentTimeMillis();
	}
	
	/**
	* Returns a string that indicates the elapsed time since the last tic() call.
	*/
	public static String toc() {
		return toc("");
	}

	/**
	* Returns a string that indicates the elapsed time since the last tic() call.
	*
	* @param msg	message to print
	*/
	public static String toc(String msg) {
		double te = System.currentTimeMillis()-chrono;
		String s = msg + " ";
		DecimalFormat df = new DecimalFormat("####.##"); 
		if (te < 3000.0) 
			return s + df.format(te) + " ms";
		te /= 1000;
		if (te < 600.1)
			return s + df.format(te) + " s";
		te /= 60;
		if (te < 240.1)
			return s + df.format(te) + " min.";
		te /= 24;
		return s + df.format(te) + " h.";
	}
 }
