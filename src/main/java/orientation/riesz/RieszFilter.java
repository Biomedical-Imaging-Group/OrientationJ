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


package orientation.riesz;

import orientation.fft.ComplexSignal;
import orientation.imageware.Builder;
import orientation.imageware.ImageWare;

public class RieszFilter {

	private ComplexSignal A[];
	private ComplexSignal S[];
	private String name[];
	private int channels;
	private int order;
	private boolean cancelDC = false; // false to perfect reconstruction

	public RieszFilter(int nx, int ny, int order, boolean cancelDC) {
		this.order = order;
		this.cancelDC = cancelDC;
		this.channels = order + 1;
		A = new ComplexSignal[channels];
		S = new ComplexSignal[channels];
		name = new String[channels];
		
		ComplexSignal baseX = generateBaseX(nx, ny);
		ComplexSignal baseY = generateBaseY(nx, ny);
	
		double c =  Math.sqrt(Math.pow(2, order));
		for(int k=0; k<channels; k++) {
			name[k] = "F";
		}
		for(int k=0; k<channels; k++) {
			for(int kx=1; kx<channels-k; kx++) {
				if (A[k] == null)
					A[k] = baseX.duplicate();
				else
					A[k].multiply(baseX);
				name[k] += "X";
			}
			for(int ky=channels-k; ky<channels; ky++) {
				if (A[k] == null)
					A[k] = baseY.duplicate();
				else
					A[k].multiply(baseY);
				name[k] += "Y";
			}
			double coef = Math.sqrt(binomial(channels-1, k));
			A[k].multiply(coef);
			A[k].imag[0] /= c;
			A[k].real[0] /= c;
			S[k] = A[k].conjugate();
		}
	}
	public int getChannels() {
		return channels;
	}
	
	public ComplexSignal getAnalysis(int channel) {
		return A[channel];
	}
	
	public ComplexSignal getSynthesis(int channel) {
		return S[channel];
	}

	public int getOrder() {
		return order;
	}

	/**
	* Return the real (if order is odd) or the imaginary (if order is even) 
	* parts of the analysis filter for a specific channel.
	*/
	public ImageWare getAnalysisVisible(int channel) {
		int nx = A[channel].nx;
		int ny = A[channel].ny;
		ImageWare out = Builder.create(nx, ny, 1, ImageWare.FLOAT);
		float[] pout = out.getSliceFloat(0);
		A[channel].shift();
		if (order%2==0) {
			for(int k=0; k<nx*ny; k++)
				pout[k] = (float)A[channel].real[k];
		}
		else {
			for(int k=0; k<nx*ny; k++)
				pout[k] = (float)A[channel].imag[k];
		}
		A[channel].shift();
		return out;
	}

	/**
	*/
	public String getName(int channel) {
		return name[channel];
	}

	/**
	*/
	private ComplexSignal generateBaseX(int nx, int ny) {
		ComplexSignal filter = new ComplexSignal(nx, ny);
		
		for(int x=0; x<nx/2; x++) 
		for(int y=0; y<=ny/2; y++) {
			double px = (double)x / (nx-1);
			double py = (double)y / (ny-1);
			double w = Math.sqrt(px*px + py*py);
			filter.imag[x + y*nx] = -px/w;
			if (y >= 1)
				filter.imag[x + (ny-y)*nx] = -px/w;
			if (x >= 1)
				filter.imag[nx-x + y*nx] = px/w;
			if (y >= 1 && x >= 1)
				filter.imag[nx-x + (ny-y)*nx] = px/w;
		}
		// Nyquist frequency
		int x = nx/2;
		for(int y=0; y<=ny/2; y++) {
			double px = (double)x / (nx-1);
			double py = (double)y / (ny-1);
			double w = Math.sqrt(px*px + py*py);
			filter.real[x+nx*(y)] = px/w;
			filter.imag[x+nx*(y)] = 0;
			if (y >= 1) {
				filter.real[x+nx*(ny-y)] = px/w;
				filter.imag[x+nx*(ny-y)] = 0;
			}
		}
		// DC frequency
		filter.imag[0] = 0.0;
		filter.real[0] = (cancelDC ? 1 : 0);
		return filter;
	}

	/**
	*/
	private ComplexSignal generateBaseY(int nx, int ny) {
		ComplexSignal filter = new ComplexSignal(nx, ny);
		for(int x=0; x<=nx/2; x++) 
		for(int y=0; y<ny/2; y++) {
			double px = (double)x / (nx-1);
			double py = (double)y / (ny-1);
			double w = Math.sqrt(px*px + py*py);
			filter.imag[x + nx*y] = -py/w;
			if (x >= 1)
				filter.imag[nx-x + nx*y] = -py/w;
			if (y >= 1)
				filter.imag[x + nx*(ny-y)] = py/w;
			if (y >= 1 && x >= 1)
				filter.imag[nx-x + nx*(ny-y)] = py/w;
		}
		// Nyquist frequency
		int y = ny/2;
		for(int x=0; x<=nx/2; x++) {
			double px = (double)x / (nx-1);
			double py = (double)y / (ny-1);
			double w = Math.sqrt(px*px + py*py);
			filter.real[x + nx*y] = py/w;
			filter.imag[x + nx*y] = 0;
			if (x >= 1) {
				filter.real[nx-x + nx*y] = py/w;
				filter.imag[nx-x + nx*y] = 0;
			}
		}
		// DC frequency
		filter.imag[0] = 0.0;
		filter.real[0] = (cancelDC ? 1 : 0);
		return filter;
	}

	private double binomial(int n, int k) {
		return factorial(n) / factorial(k) / factorial(n-k);
	}
	
	private double factorial(int n) {
		if (n == 0)
			return 1;
		int fact = 1;
		for(int i=1; i<=n; i++)
			fact *= i;
		return fact;
	}

}
