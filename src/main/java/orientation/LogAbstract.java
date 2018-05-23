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
