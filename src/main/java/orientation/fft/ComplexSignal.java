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

package orientation.fft;

/**
 * Class ComplexSignal
 */
public class ComplexSignal {

	public final static byte REAL		= (byte)0;
	public final static byte IMAGINARY	= (byte)1;
	public final static byte COMPLEX	= (byte)2;
	public double[] real;
	public double[] imag;
	public int nx;
	public int ny;

	/**
	* Creates an empty Fourier Space of size [nx*ny]. 
	*/
	public ComplexSignal(int nx, int ny) {
		real = new double[nx*ny];
		imag = new double[nx*ny];
		this.nx = nx;
		this.ny = ny;
	}

	/**
	* Creates an empty Fourier Space of size [nx*ny]. 
	*/
	public ComplexSignal(int nx, int ny, byte allocate) {
		if (allocate == COMPLEX || allocate == REAL)
			real = new double[nx*ny];
		if (allocate == COMPLEX || allocate == IMAGINARY)
			imag = new double[nx*ny];
		this.nx = nx;
		this.ny = ny;
	}

	/**
	 */
	public ComplexSignal(float freal[], int nx, int ny) {
		this.real = new double[freal.length];
		for(int k=0; k<freal.length; k++)
			this.real[k] = freal[k];
		this.nx = nx;
		this.ny = ny;
	}
	
	/**
	 */
	public ComplexSignal(float freal[], float fimag[], int nx, int ny) {
		this.real = new double[freal.length];
		for(int k=0; k<freal.length; k++)
			this.real[k] = freal[k];
		this.imag = new double[fimag.length];
		for(int k=0; k<fimag.length; k++)
			this.imag[k] = fimag[k];
		this.nx = nx;
		this.ny = ny;
	}

	/**
	*/
	public ComplexSignal(double real[], int nx, int ny) {
		this.real = real;
		this.nx = nx;
		this.ny = ny;
	}

	/**
	*/
	public ComplexSignal(double real[], double imag[], int nx, int ny) {
		this.real = real;
		this.imag = imag;
		this.nx = nx;
		this.ny = ny;
	}

	/**
	*/
	public double[] module() {
		double module[] = new double[nx*ny];
		if (real != null && imag != null) {
			for(int k=0 ; k<ny*nx; k++)
				module[k] = Math.sqrt(real[k]*real[k]+imag[k]*imag[k]);	
		}
		if (real == null && imag != null) {
			for(int k=0 ; k<ny*nx; k++)
				module[k] = Math.abs(real[k]);	
		}
		if (real != null && imag == null) {
			for(int k=0 ; k<ny*nx; k++)
				module[k] = Math.abs(real[k]);	
		}
		return module;
	}

	/**
	* 2D shift of the half size of the input.
	*/
	public void shift() {
		double[] tmpreal = new double[nx*ny];
		double[] tmpimag = new double[nx*ny];
		int nx2 = nx/2;
		int ny2 = ny/2;
		int ky = 0;
		int kj = 0;
		for(int x=0; x<nx; x++) {
			int i = (x >= nx2 ? x - nx2 : x + nx2);
			for(int y=0; y<ny; y++) {
				ky = y*nx;
				tmpreal[i+ky] = real[x+ky];
				tmpimag[i+ky] = imag[x+ky];
			}
		}
		for(int y=0; y<ny; y++) {
			int j = (y >= ny2 ? y - ny2 : y + ny2);
			ky = y*nx;
			kj = j*nx;
			for(int x=0; x<nx; x++) {
				real[x+kj] = tmpreal[x+ky];
				imag[x+kj] = tmpimag[x+ky];
			}
		}
	}
	
	/**
	* Complex multiplication operator.
	*/
	public static ComplexSignal multiply(ComplexSignal a, ComplexSignal b) {
		int nx = a.nx;
		int ny = a.ny;
		int nxy = nx*ny;
		ComplexSignal p = new ComplexSignal(nx, ny);
		for(int k=0; k<nxy; k++) {
			p.real[k] = a.real[k]*b.real[k] - a.imag[k]*b.imag[k];
			p.imag[k] = a.real[k]*b.imag[k] + a.imag[k]*b.real[k];
		}
		return p;
	}

	/**
	* Complex multiplication in place.
	*/
	public void multiply(ComplexSignal a) {
		int nx = a.nx;
		int ny = a.ny;
		int nxy = nx*ny;
		double tmp =0 ;
		for(int k=0; k<nxy; k++) {
			tmp = a.real[k]*real[k] - a.imag[k]*imag[k];
			imag[k] = a.real[k]*imag[k] + a.imag[k]*real[k];
			real[k] = tmp;
		}
	}

	/**
	* Complex subtract operator.
	*/
	public static ComplexSignal subtract(ComplexSignal a, ComplexSignal b) {
		int nx = a.nx;
		int ny = a.ny;
		int nxy = nx*ny;
		ComplexSignal p = new ComplexSignal(nx, ny);
		for(int k=0; k<nxy; k++) {
			p.real[k] = a.real[k] - b.real[k];
			p.imag[k] = a.imag[k] - b.imag[k];
		}
		return p;
	}

	/**
	* Complex subtract in place.
	*/
	public void subtract(ComplexSignal a) {
		int nx = a.nx;
		int ny = a.ny;
		int nxy = nx*ny;
		for(int k=0; k<nxy; k++) {
			real[k] -= a.real[k];
			imag[k] -= a.imag[k];
		}
	}

	/**
	*/
	public ComplexSignal conjugate() {
		int nxy = nx*ny;
		ComplexSignal out = new ComplexSignal(nx, ny);
		System.arraycopy(real, 0, out.real, 0, nxy);
		for(int k=0; k<nxy; k++) {
			out.imag[k] = -imag[k];
		}
		return out;
	}

	/**
	*/
	public void multiply(double coef) {
		int nxy = nx*ny;
		for(int k=0; k<nxy; k++) {
			real[k] *= coef;
			imag[k] *= coef;
		}
	}

	/**
	*/
	public ComplexSignal duplicate() {
		int nxy = nx*ny;
		ComplexSignal out = new ComplexSignal(nx, ny);
		System.arraycopy(real, 0, out.real, 0, nxy);
		System.arraycopy(imag, 0, out.imag, 0, nxy);
		return out;
	}
		
}

