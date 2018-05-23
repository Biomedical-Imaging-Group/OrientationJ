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

/**
* Gaussian class.
* Implementation of the Gaussian filter as a cascade of 3 exponential filters. 
* The boundary conditions are mirroring.
* Thread or directly by calling the run()
*/
public class Gaussian implements Runnable {

	private double signal[][];
	private double sigma;
	private int nx;
	private int ny;
	private LogAbstract log;
	private double wrange;
	
	/**
	* Constructor based on the signal.
	*/
	public Gaussian(LogAbstract log, double wrange, double signal[][], double sigma, int nx, int ny) {
		this.log = log;
		this.wrange = wrange;
		this.signal = signal;
		this.sigma = sigma;
		this.nx = nx;
		this.ny = ny;
	}
	
	/**
	* Run method.
	*/
	public void run() {
		double s2 = sigma * sigma;
		double pole = 1.0 + (3.0/s2) - (Math.sqrt(9.0+6.0*s2)/s2);
	
		for (int x=0; x<nx; x++) {
			signal[x] = convolveIIR_TriplePole(signal[x], pole);
		}

		double row[]  = new double[nx];
		for(int y=0; y<ny; y++) {
			log.increment(wrange/ny);
			for (int x=0; x<nx; x++)
				row[x] = signal[x][y];
			row = convolveIIR_TriplePole(row, pole);
			for (int x=0; x<nx; x++)
				signal[x][y] = row[x];
		}
	}

	/**
	* Convolve with with a Infinite Impulse Response filter (IIR)
	*/
	private double[] convolveIIR_TriplePole(double[] signal, double pole) {
		int l = signal.length;
		int N = 9;
		double lambda = 1.0;
		double[] output = new double[l];
		for (int k=0; k<N; k++) {
			lambda = lambda * (1.0 - pole) * (1.0 - 1.0 / pole);
		}
		for (int n=0; n<l; n++) {
			output[n] = signal[n] * lambda;
		}
		for (int k=0; k<N; k++) {
			output[0] = getInitialCausalCoefficientMirror(output, pole);
			for (int n=1; n<l ; n++) {
				output[n] = output[n] + pole * output[n - 1];
			}
			output[l-1] = getInitialAntiCausalCoefficientMirror(output, pole);
			for (int n=l-2; 0 <= n; n--) {
				output[n] = pole * (output[n+1] - output[n]);
			}
		}
		return output;
	}

	/**
	* Initial conditions
	*/
	private double getInitialAntiCausalCoefficientMirror(double[] c, double z) {
		return((z * c[c.length - 2] + c[c.length - 1]) * z / (z * z - 1.0));
	}

	/**
	* Initial conditions
	*/
	private double getInitialCausalCoefficientMirror(double[] c, double z) {
		double tolerance = 10e-6;
		double z1 = z, zn = Math.pow(z, c.length - 1);
		double sum = c[0] + zn * c[c.length - 1];
		int horizon = c.length;

		if (tolerance > 0.0 ) {
			horizon = 2 + (int)(Math.log(tolerance) / Math.log(Math.abs(z)));
			horizon = (horizon < c.length) ? (horizon) : (c.length);
		}
		zn = zn * zn;
		for (int n=1; n<horizon-1; n++) {
			zn = zn / z;
			sum = sum + (z1 + zn) * c[n];
			z1 = z1 * z;
		}
		return (sum / (1.0 - Math.pow(z, 2 * c.length - 2)));
	}
	
}