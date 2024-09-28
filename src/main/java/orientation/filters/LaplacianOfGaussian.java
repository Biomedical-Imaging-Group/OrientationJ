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

package orientation.filters;

import orientation.imageware.Builder;
import orientation.imageware.ImageWare;

public class LaplacianOfGaussian {

	/**
	* Apply a Laplacian of Gaussian 2D.
	* Separable implementation.
	*/
	static public ImageWare run(ImageWare input, double sigmaX, double sigmaY) {
		if (input == null)
			return null;
		int nx = input.getSizeX();
		int ny = input.getSizeY();
		int nt = input.getSizeZ();
		
		int d = 0; // dimension
		d = (sigmaX > 0 ? d+1 : d);
		d = (sigmaY > 0 ? d+1 : d);
		
		if (d == 0)
			return input;
		double pd = Math.pow(2*Math.PI, d/2.0);
		double sx = (sigmaX > 0 ? sigmaX: 1.0);
		double sy = (sigmaY > 0 ? sigmaY: 1.0);
		double cst = 1.0/(pd*sx*sy);
		
		double kernelFactX[] = createKernelLoG_Fact(sigmaX, cst);	
		double kernelBaseX[] = createKernelLoG_Base(sigmaX);	
		double kernelFactY[] = createKernelLoG_Fact(sigmaY, cst);	
		double kernelBaseY[] = createKernelLoG_Base(sigmaY);	
		
		ImageWare outputX = Builder.create(nx, ny, nt, ImageWare.FLOAT);
		ImageWare outputY = Builder.create(nx, ny, nt, ImageWare.FLOAT);
		
 		for( int t=0; t<nt; t++) {
	 		double vinY[] = new double[ny];
	 		double voutY[] = new double[ny];
			for (int x=0; x<nx; x++) {
				input.getY(x, 0, t, vinY);
				convolve(vinY, voutY, kernelFactY);
				outputX.putY(x, 0, t, voutY);
				
				input.getY(x, 0, t, vinY);
				convolve(vinY, voutY, kernelBaseY);
				outputY.putY(x, 0, t, voutY);
			}
	 		double vinX[] = new double[nx];
	 		double voutX[] = new double[nx];
			for (int y=0; y<ny; y++) {
				outputX.getX(0, y, t, vinX);
				convolve(vinX, voutX, kernelBaseX);
				outputX.putX(0, y, t, voutX);
				outputY.getX(0, y, t, vinX);
				convolve(vinX, voutX, kernelFactX);
				outputY.putX(0, y, t, voutX);
			}
		}
		outputX.add(outputY);
		
		return outputX;
	}
	
	/**
	* cst*(x^2/(sigma^4)-1/(sigma^2))*exp(-(x^2)/(sigma^2))
    */
	static private double[] createKernelLoG_Fact(double sigma, double cst) {
		
		if (sigma <= 0.0) {
			double[] kernel = new double[1];
			kernel[0] = 1.0;
			return kernel;
		}
		
		double s2 = sigma*sigma;
		double s4 = s2*s2;
		double dem = 2.0*s2;
		int size = (int)Math.round(((int)(sigma*3.0))*2.0 + 1);	// always odd size
		int size2 = size/2;
		double[] kernel = new double[size];
		
		double x;
		for(int k=0; k<size; k++) {
			x = (k-size2)*(k-size2);
			kernel[k] = cst * (x/s4-1.0/s2) * Math.exp(-x/dem);
		}
		return kernel;
	}
	
	/**
	* exp(-(x^2)/(sigma^2))
	 */
	static private double[] createKernelLoG_Base(double sigma) {
		
		if (sigma <= 0.0) {
			double[] kernel = new double[1];
			kernel[0] = 1.0;
			return kernel;
		}
		
		double s2 = sigma*sigma;
		double dem = 2.0*s2;
		int size = (int)Math.round(((int)(sigma*3.0))*2.0 + 1);	// always odd size
		int size2 = size/2;
		double[] kernel = new double[size];
		
		double x;
		for(int k=0; k<size; k++) {
			x = (k-size2)*(k-size2);
			kernel[k] = Math.exp(-x/dem);
		}
		return kernel;
	}
	
	static private double[] convolve(double vin[], double vout[], double kernel[]) {
		int n = vin.length;
		int nk = kernel.length;
		int kc = nk/2;
		int	period = (n <= 1 ? 1: 2*n - 2);
		
		int im;
		for (int i=0; i<n; i++) {
			double sum=0.0;
			for (int k=0; k<nk; k++) {
				im = i + k - kc;
				while (im < 0)
					im += period;
				while (im >= n) {
					im = period - im;
					im = (im < 0 ? -im : im);
				}
				sum += kernel[k] * vin[im];
			}
			vout[i] = sum;
		}
		return vout;
	}
	
}