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

import orientation.GroupImage;
import orientation.LogAbstract;
import orientation.OrientationParameters;

public class Hessian implements Runnable {

	private GroupImage gim;
	private LogAbstract log;
	
	public Hessian(LogAbstract log, GroupImage gim, OrientationParameters params) {
		this.log = log;
		this.gim = gim;
	}
	
	@Override
	public void run() {
		log.reset();
		for(int t=0; t<gim.nt; t++) {
			log.progress("Hessian", (t+1)*100.0/gim.nt);
			hessianXX(gim, t);
			hessianYY(gim, t);
			hessianXY(gim, t);
		}
	}
	
	private void hessianXX(GroupImage gim, int t) {
		int nx = gim.nx;
		int ny = gim.ny;
		double kernelX[] = {1.0/6.0, -2.0/6.0, 1.0/6.0};
		double kernelY[] = {1.0, 4, 1.0};

		double colin[] = new double[ny];
		double colou[] = new double[ny];
		for(int x=0; x<nx; x++) {
			gim.source.getY(x, 0, t, colin);
			convolve3taps(colin, colou, kernelX);
			gim.hxx.putY(x, 0, t, colou);
		}
		
		double rowin[] = new double[nx];
		double rowou[] = new double[nx];
		for(int y=0; y<ny; y++) {
			gim.hxx.getX(0, y, t, rowin);
			convolve3taps(rowin, rowou, kernelY);
			gim.hxx.putX(0, y, t, rowou);
		}
	}
	
	private void hessianXY(GroupImage gim, int t) {
		int nx = gim.nx;
		int ny = gim.ny;
		double kernelX[] = {1.0/2.0, 0, -1.0/2.0};
		double kernelY[] = {1.0/2.0, 0, -1.0/2.0};

		double colin[] = new double[ny];
		double colou[] = new double[ny];
		for(int x=0; x<nx; x++) {
			gim.source.getY(x, 0, 0, colin);
			convolve3taps(colin, colou, kernelX);
			gim.hxy.putY(x, 0, 0, colou);
		}
		
		double rowin[] = new double[nx];
		double rowou[] = new double[nx];
		for(int y=0; y<ny; y++) {
			gim.hxy.getX(0, y, t, rowin);
			convolve3taps(rowin, rowou, kernelY);
			gim.hxy.putX(0, y, t, rowou);
		}
	}
	
	private void hessianYY(GroupImage gim, int t) {
		int nx = gim.nx;
		int ny = gim.ny;
		double kernelX[] = {1.0, 4.0, 1.0};
		double kernelY[] = {1.0/6.0, -2.0/6.0, 1.0/6.0};

		double colin[] = new double[ny];
		double colou[] = new double[ny];
		for(int x=0; x<nx; x++) {
			gim.source.getY(x, 0, t, colin);
			convolve3taps(colin, colou, kernelX);
			gim.hyy.putY(x, 0, t, colou);
		}
		
		double rowin[] = new double[nx];
		double rowou[] = new double[nx];
		for(int y=0; y<ny; y++) {
			gim.hyy.getX(0, y, t, rowin);
			convolve3taps(rowin, rowou, kernelY);
			gim.hyy.putX(0, y, t, rowou);
		}
	}
	
	/**
	 * Convolves a 1D signal to a kernel with mirror boundary conditions. 
	 * 
	 * Be careful: 
	 * 1) the kernel should be a 3-taps array.
	 * 2) the in and the out should be allocated with the same size.
	 */
	private void convolve3taps(double[] in, double out[], double[] kernel) {
		int n = in.length;
		out[0] = in[1] * kernel[0] + in[0] * kernel[1] + in[1] * kernel[2];
		for(int k=1; k<n-1; k++)
			out[k] = in[k-1] * kernel[0] + in[k] * kernel[1] + in[k+1] * kernel[2];
		out[n-1] = in[n-2] * kernel[0] + in[n-1] * kernel[1] + in[n-2] * kernel[2];
	}
	
}
