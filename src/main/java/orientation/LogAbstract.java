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

public interface LogAbstract {

	/**
	 * Set a value and a message in the progress bar.
	 */
	public abstract void progress(String msg, int value);

	/**
	 * Set a value and a message in the progress bar.
	 */
	public abstract void increment(double inc);

	/**
	 * Set a value in the progress bar.
	 */
	public abstract void setValue(int value);

	/**
	 * Set a message in the progress bar.
	 */
	public abstract void setMessage(String msg);

	/**
	 * Set a value and a message in the progress bar.
	 */
	public abstract void progress(String msg, double value);
	/**
	 * Set to 0 the progress bar.
	 */
	public abstract void reset();

	/**
	 * Set to 100 the progress bar.
	 */
	public abstract void finish();

	/**
	 * Set to 100 the progress bar with an additional message.
	 */
	public abstract void finish(String msg);
	
}
