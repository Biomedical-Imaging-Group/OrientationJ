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

import java.awt.Polygon;
import java.awt.Rectangle;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import gui_orientation.Measure;
import ij.IJ;
import ij.ImagePlus;
import ij.process.ByteProcessor;
import orientation.filters.Gaussian;
import orientation.imageware.Builder;
import orientation.imageware.ImageWare;

public class StructureTensor implements Runnable {

	/** Maximum number of iterations for the Hessian search optimization */
	private int maxIteration = 100;

	/** Tolerance to stop the Hessian search optimization */
	private double tolerance = 1.0;

	/** Step for the Hessian search optimization */
	private double etta = 1.0;

	/** Progress bar */
	private LogAbstract log;

	private GroupImage gim;
	private OrientationParameters params;

	/**
	 * Constructor.
	 */
	public StructureTensor(LogAbstract log, GroupImage gim, OrientationParameters params) {
		this.log = log;
		this.gim = gim;
		this.params = params;
	}

	@Override
	public void run() {
		if (params.gradient == OrientationParameters.HESSIAN)
			analysisOnHessian();
		else
			analysisOnGradient();
	}

	/**
	 * Compute all the structure tensor based on the 2-components gradient vector.
	 * 
	 * The 3 Gaussian filters has implemented in parallel (multithread).
	 * 
	 * @author Daniel Sage
	 */
	private void analysisOnGradient() {
		int mx = gim.nx;
		int my = gim.ny;
		int nt = gim.nt;
		double K = params.harrisK;

		double xx, yy, xy;

		double[][] dxx = new double[mx][my];
		double[][] dxy = new double[mx][my];
		double[][] dyy = new double[mx][my];

		for (int t = 0; t < nt; t++) {
			log.progress("Tensor " + (t + 1), 0);
			gim.gx.getXY(0, 0, t, dxx);
			gim.gy.getXY(0, 0, t, dyy);
			for (int y = 0; y < my; y++) {
				log.increment(15.0 / my);
				for (int x = 0; x < mx; x++) {
					dxy[x][y] = dxx[x][y] * dyy[x][y];
					dyy[x][y] = dyy[x][y] * dyy[x][y];
					dxx[x][y] = dxx[x][y] * dxx[x][y];
				}
			}
			if (params.sigmaST > 0) {
				Gaussian gxx = new Gaussian(log, 20, dxx, params.sigmaST, mx, my);
				Gaussian gyy = new Gaussian(log, 20, dyy, params.sigmaST, mx, my);
				Gaussian gxy = new Gaussian(log, 20, dxy, params.sigmaST, mx, my);
				ExecutorService executor = Executors.newFixedThreadPool(3);
				executor.execute(gxx);
				executor.execute(gxy);
				executor.execute(gyy);
				executor.shutdown();
				while (!executor.isTerminated()) {
				}
			}

			for (int y = 0; y < my; y++) {
				log.increment(25.0 / my);
				for (int x = 0; x < mx; x++) {
					xx = dxx[x][y];
					yy = dyy[x][y];
					xy = dxy[x][y];
					gim.energy.putPixel(x, y, t, xx + yy);
					gim.coherency.putPixel(x, y, t, computeCoherency(xx, yy, xy, params.epsilon));
					gim.orientation.putPixel(x, y, t, computeOrientation(xx, yy, xy));
					if (params.isServiceHarris())
						gim.harris.putPixel(x, y, t, (xx * yy - xy * xy - K * (xx + yy) * (xx + yy)));
				}
			}
			
			if (params.isServiceHarris()) {
				gim.harris.smoothGaussian(0.1, 0.1, 0);
				gim.minmaxHarris = gim.harris.getMinMax();
			}
		}
	}

	/**
	 * Compute all the structure tensor based on the 3-components hessian vector.
	 * 
	 * @author Pad Pedram
	 */
	private void analysisOnHessian() {

		int NIP = 8;

		int mx = gim.nx;
		int my = gim.ny;
		int nt = gim.nt;

		double[][] fxxfxx = new double[mx][my];
		double[][] fxyfxy = new double[mx][my];
		double[][] fyyfyy = new double[mx][my];
		double[][] fxxfyy = new double[mx][my];
		double[][] fxxfxy = new double[mx][my];
		double[][] fyyfxy = new double[mx][my];

		double[][] hxx = new double[mx][my];
		double[][] hyy = new double[mx][my];
		double[][] hxy = new double[mx][my];

		double hfeatureMax[] = new double[4];
		double hfeatureMin[] = new double[4];
		double[] htensor = new double[6];
		for (int t = 0; t < nt; t++) {

			gim.hxx.getXY(0, 0, t, hxx);
			gim.hyy.getXY(0, 0, t, hyy);
			gim.hxy.getXY(0, 0, t, hxy);
			for (int y = 0; y < my; y++)
				for (int x = 0; x < mx; x++) {
					fxxfxx[x][y] = hxx[x][y] * hxx[x][y];
					fxyfxy[x][y] = hxy[x][y] * hxy[x][y];
					fyyfyy[x][y] = hyy[x][y] * hyy[x][y];
					fxxfyy[x][y] = hxx[x][y] * hyy[x][y];
					fxxfxy[x][y] = hxx[x][y] * hxy[x][y];
					fyyfxy[x][y] = hyy[x][y] * hxy[x][y];
				}
			log.progress("Tensor " + t, 75);
			if (params.sigmaST > 0) {
				Gaussian[] gaussians = new Gaussian[6];
				gaussians[0] = new Gaussian(log, 10, fxxfxx, params.sigmaST, mx, my);
				gaussians[1] = new Gaussian(log, 10, fxyfxy, params.sigmaST, mx, my);
				gaussians[2] = new Gaussian(log, 10, fyyfyy, params.sigmaST, mx, my);
				gaussians[3] = new Gaussian(log, 10, fxxfyy, params.sigmaST, mx, my);
				gaussians[4] = new Gaussian(log, 10, fxxfxy, params.sigmaST, mx, my);
				gaussians[5] = new Gaussian(log, 10, fyyfxy, params.sigmaST, mx, my);
				ExecutorService executor = Executors.newFixedThreadPool(6);
				for (int e = 0; e < 6; e++)
					executor.execute(gaussians[e]);
				executor.shutdown();
				while (!executor.isTerminated())
					;
			}
			log.progress("Tensor " + t, 82);

			for (int x = 0; x < mx; x++)
				for (int y = 0; y < my; y++) {
					htensor[0] = fxxfxx[x][y];
					htensor[1] = fxyfxy[x][y];
					htensor[2] = fyyfyy[x][y];
					htensor[3] = fxxfyy[x][y];
					htensor[4] = fxxfxy[x][y];
					htensor[5] = fyyfxy[x][y];

					hfeatureMax = hessianMaximizer(htensor, NIP);
					hfeatureMin = hessianMinimizer(htensor, NIP);
					gim.orientation.putPixel(x, y, t, hfeatureMax[0]);
					gim.energy.putPixel(x, y, t, hfeatureMax[1]);
					gim.coherency.putPixel(x, y, t,
							(hfeatureMax[1] - hfeatureMin[1]) / (hfeatureMax[1] + hfeatureMin[1]));
				}
		}
	}

	/**
	 * Computes the global structure tensor features on a mask image.
	 * 
	 * @author Daniel Sage
	 */
	public Measure measure(int z, int countMeasure, ImagePlus imp, int area, Rectangle rect, Polygon polygon,
			ByteProcessor mask) {
		int px = rect.x + rect.width / 2;
		int py = rect.y + rect.height / 2;
		int nx = gim.nx;
		int ny = gim.ny;
		double epsilon = 10e-4;
		ImageWare dxx = Builder.create(nx, ny, 1, ImageWare.FLOAT);
		ImageWare dyy = Builder.create(nx, ny, 1, ImageWare.FLOAT);
		ImageWare dxy = Builder.create(nx, ny, 1, ImageWare.FLOAT);
		IJ.showProgress(0.0);

		gim.gx.getXY(0, 0, 0, dxx);
		gim.gy.getXY(0, 0, 0, dyy);
		dxy = dxx.duplicate();
		dxy.multiply(dyy);
		dxx.multiply(dxx);
		dyy.multiply(dyy);

		double xx = 0.0, yy = 0.0, xy = 0.0, nb = 0.0;
		for (int y = 0; y < ny; y++)
			for (int x = 0; x < nx; x++) {
				if (mask.getPixel(x, y) != 255) {
					xx += dxx.getPixel(x, y, 0);
					yy += dyy.getPixel(x, y, 0);
					xy += dxy.getPixel(x, y, 0);
					nb++;
				}
			}
		xx /= nb;
		yy /= nb;
		xy /= nb;
		computeCoherency(xx, yy, xy, 10e-4);
		double result[] = new double[5];
		result[0] = xx + yy; // energy
		result[1] = computeCoherency(xx, yy, xy, epsilon);
		result[2] = computeOrientation(xx, yy, xy);
		result[3] = 0; // harris
		result[4] = 0;
		IJ.showProgress(1.0);

		Measure measure = new Measure(countMeasure, px, py, z, params.sigmaLoG, imp, area, rect, polygon, mask);
		measure.energy = result[0];
		measure.phase = result[4];
		measure.coherency = result[1];
		measure.orientation = result[2];
		return measure;
	}

	/**
	 * Evaluates the coherency based the gradient information.
	 * 
	 * @author Daniel Sage
	 */
	private double computeCoherency(double xx, double yy, double xy, double epsilon) {
		double coherency = Math.sqrt((yy - xx) * (yy - xx) + 4.0 * xy * xy) / (xx + yy + epsilon);
		return coherency;
	}

	/**
	 * Evaluates the orientation based the gradient information.
	 * 
	 * @author Daniel Sage
	 */
	private double computeOrientation(double xx, double yy, double xy) {
		return 0.5 * Math.atan2(2.0 * xy, (yy - xx));
	}

	/**
	 * Maximizes the hessian vector.
	 * 
	 * @author Pad Pedram
	 */
	private double[] hessianMaximizer(double[] in, int NIP) {
		double out[] = new double[4];
		double coefA = in[0] - 4.0 * in[1] + in[2] - 2.0 * in[3];
		double coefB = 2.0 * in[1] - in[2] + in[3];

		double[] alp = new double[NIP];
		double[] y = new double[NIP];

		double ma = -10000;
		int mi = 0;
		double amp = 0;

		for (int i = 0; i < NIP; i++) {
			alp[i] = i * Math.PI / (NIP - 1) - Math.PI / 2;
			y[i] = hessianTensorEvaluation(in, alp[i], coefA, coefB);
			if (y[i] > ma) {
				ma = y[i];
				mi = i;
				amp = alp[mi];
			}
		}

		double dy = Double.MAX_VALUE;
		double ddy = 0;
		double st = 0;
		int iteration = 0;
		boolean flag = false;
		double amt = 0;

		do {
			dy = hessianTensorDerivative(in, amp, coefA, coefB);
			ddy = hessianTensor2Derivative(in, amp, coefA, coefB);
			flag = false;
			if (ddy < 0) {
				st = dy / ddy;
				if (st < Math.PI) {
					amt = amp - st;
					if (hessianTensorEvaluation(in, amp, coefA, coefB) < hessianTensorEvaluation(in, amt, coefA,
							coefB)) {
						amp = amt;
						flag = true;
					}
				}
			}
			if (!flag) {
				do {
					do {
						etta *= 0.8;
						st = etta * dy;
					} while (st > Math.PI);
					amt = amp + st;
				} while (hessianTensorEvaluation(in, amp, coefA, coefB) > hessianTensorEvaluation(in, amt, coefA,
						coefB));
				amp = amt;
			}
			iteration++;
		} while ((dy > tolerance) && (iteration < maxIteration));

		out[0] = Math.PI * (amp / Math.PI + 1 - Math.ceil(amp / Math.PI + 0.5));
		out[1] = hessianTensorEvaluation(in, amp, coefA, coefB);
		out[2] = dy;
		out[3] = iteration;
		return out;
	}

	/**
	 * Minimizes the hessian vector.
	 * 
	 * @author Pad Pedram
	 */
	private double[] hessianMinimizer(double[] in, int NIP) {
		double out[] = new double[4];

		double coefA = in[0] - 4.0 * in[1] + in[2] - 2.0 * in[3];
		double coefB = 2.0 * in[1] - in[2] + in[3];
		double[] alp = new double[NIP];
		double[] y = new double[NIP];

		double ma = 10000;
		int mi = 0;
		double amp = 0;

		for (int i = 0; i < NIP; i++) {
			alp[i] = i * Math.PI / (NIP - 1) - Math.PI / 2;
			y[i] = hessianTensorEvaluation(in, alp[i], coefA, coefB);
			if (y[i] < ma) {
				ma = y[i];
				mi = i;
				amp = alp[mi];
			}
		}

		double dy = Double.MAX_VALUE;
		double ddy = 0;
		double st = 0;
		int iteration = 0;
		boolean flag = false;
		double amt = 0;

		do {
			dy = hessianTensorDerivative(in, amp, coefA, coefB);
			ddy = hessianTensor2Derivative(in, amp, coefA, coefB);
			flag = false;
			if (ddy > 0) {
				st = dy / ddy;
				if (st < Math.PI) {
					amt = amp - st;
					if (hessianTensorEvaluation(in, amp, coefA, coefB) > hessianTensorEvaluation(in, amt, coefA,
							coefB)) {
						amp = amt;
						flag = true;
					}
				}
			}
			if (!flag) {
				do {
					do {
						etta *= 0.8;
						st = etta * dy;
					} while (st > Math.PI);
					amt = amp - st;
				} while (hessianTensorEvaluation(in, amp, coefA, coefB) < hessianTensorEvaluation(in, amt, coefA,
						coefB));
				amp = amt;
			}
			iteration++;
		} while ((dy > tolerance) && (iteration < maxIteration));

		out[0] = Math.PI * (amp / Math.PI + 1 - Math.ceil(amp / Math.PI + 0.5));
		out[1] = hessianTensorEvaluation(in, amp, coefA, coefB);
		out[2] = dy;
		out[3] = iteration;

		return out;
	}

	/**
	 * Evaluates the Hessian Tensor value.
	 * 
	 * @author Pad Pedram
	 */
	private double hessianTensorEvaluation(double[] coef, double alp, double coefA, double coefB) {
		double cos = Math.cos(alp);
		double sin = Math.sin(alp);
		double cos2 = cos * cos;
		double y = coefA * cos2 * cos2 + 2.0 * coefB * cos2 + coef[2] + 4.0 * coef[4] * cos2 * cos * sin
				+ 4.0 * coef[5] * cos * sin * sin * sin;
		return y;
	}

	/**
	 * Evaluates the Hessian Tensor derivative value.
	 * 
	 * @author Pad Pedram
	 */
	private double hessianTensorDerivative(double[] coef, double alp, double coefA, double coefB) {
		double cos = Math.cos(alp);
		double sin = Math.sin(alp);
		double cos2 = cos * cos;
		double dy = -4.0 * coefA * cos2 * cos * sin - 4.0 * coefB * cos * sin
				+ 4.0 * (5.0 * coef[5] - 3.0 * coef[4]) * cos2 + 16.0 * (coef[4] - coef[5]) * cos2 * cos2
				- 4.0 * coef[5];
		return dy;
	}

	/**
	 * Evaluates the Hessian Tensor second derivative value.
	 * 
	 * @author Pad Pedram
	 */
	private double hessianTensor2Derivative(double[] coef, double alp, double coefA, double coefB) {
		double cos = Math.cos(alp);
		double sin = Math.sin(alp);
		double cos2 = cos * cos;
		double ddy = -16.0 * coefA * cos2 * cos2 - 8.0 * (5.0 * coef[5] - 3.0 * coef[4]) * cos * sin
				+ 4.0 * (3.0 * coef[0] - 16.0 * coef[1] + 5.0 * coef[2] - 8.0 * coef[3]) * cos2
				- 64.0 * (coef[4] - coef[5]) * cos2 * cos * sin + 4.0 * coefB;
		return ddy;
	}
}