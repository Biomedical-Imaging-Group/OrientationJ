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

public class Corner implements Comparable<Corner> {
	
	public int x;
	public int y;
	public int t;
	private double harris;
	
	public Corner(int x, int y, int t, double harris) {
		this.x = x;
		this.y = y;
		this.t = t;
		this.harris = harris;
	}

	public double getHarrisIndex() {
		return harris;
	}
	
	@Override
	public int compareTo(Corner o) {
		return  (o.getHarrisIndex() < this.harris ? -1 :
			(o.getHarrisIndex() == this.harris ? 0 : 1));
	}
}
