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

package monogenicj;

import static gui_orientation.Chrono.tic;

import java.util.Arrays;

import gui_orientation.components.WalkBar;
import ij.ImagePlus;
import ij.process.ImageProcessor;
import orientation.imageware.Builder;
import orientation.imageware.ImageWare;
import orientation.riesz.RieszTransform;
import polyharmonicwavelets.ComplexImage;
import polyharmonicwavelets.DyadicFilters;
import polyharmonicwavelets.DyadicTransform;
import polyharmonicwavelets.Parameters;

public class MonogenicImage {

	public ImageWare source;
	public ImageWare sourceColorChannel;
	public ImageWare rx;
	public ImageWare ry;

	public ImageWare laplace;

	public ImageWare energy;
	public ImageWare coherency;
	public ImageWare orientation;

	public ImageWare monogenicFrequency;
	public ImageWare monogenicPhase;
	public ImageWare monogenicModulus;
	public ImageWare directionalHilbert;

	public int nx;
	public int ny;
	public int scale;

	public boolean pyramid;
	private WalkBar walk;
	
	final private double ORDER = 2.0;
	
	/**
	*/
	public MonogenicImage(WalkBar walk, ImageProcessor ip, int scale, boolean pyramid, double sigma) {
		this.walk = walk;
		this.source = Builder.create(new ImagePlus("", ip));
		nx = source.getWidth();
		ny = source.getHeight();
		this.scale = scale;
		this.pyramid = pyramid;
		long kb = (nx*ny*scale*4) / 1024;
		if (walk != null)
			walk.reset();

		monogenicFrequency = Builder.create(nx, ny, scale, ImageWare.FLOAT);
		monogenicModulus = Builder.create(nx, ny, scale, ImageWare.FLOAT);
		monogenicPhase = Builder.create(nx, ny, scale, ImageWare.FLOAT);
		directionalHilbert = Builder.create(nx, ny, scale, ImageWare.FLOAT);
		sourceColorChannel = computeSourceForColorChannel();
	}
	
	/**
	*/
	private ImageWare computeSourceForColorChannel() {
		ImageWare out = Builder.create(nx, ny, scale, ImageWare.FLOAT);
		ImageWare slice = Builder.create(nx, ny, 1, ImageWare.FLOAT);
		source.getXY(0, 0, 0, slice);
		slice.rescale(0, 1);
		out.putXY(0, 0, 0, slice);
		
		if (pyramid) {
			for (int s=1; s<scale; s++) {
				int div =  (int)Math.round(Math.pow(2,s));
				int mx = nx / div;
				int my = ny / div;
				for(int i=0; i<mx; i++)
				for(int j=0; j<my; j++)
					out.putPixel(i, j, s, out.getPixel(i*div, j*div, 0));
			}
		}
		else {
			for (int s=1; s<scale; s++)
				out.putXY(0, 0, s, slice);
		}
		return out;
	}

	/**
	*/
	public void compute(double sigma, double epsilon, boolean prefilter, boolean signedDir) {
	
		//----------------------------------
		// WT
		//----------------------------------
		walk.progress("Polyharmonic", 20);
		laplace = computePolyharmonicWavelets(pyramid, source);
				
		//----------------------------------
		// Prefilter + Riesz + Wawelet
		//----------------------------------
		walk.progress("Riesz", 50);
		ImageWare pre = (prefilter ? prefilter(source) : source);
		RieszTransform rt = new RieszTransform(nx, ny, 1, true);
		ImageWare rieszChannels[] = rt.analysis(pre);

		rx = computePolyharmonicWavelets(pyramid, rieszChannels[0]);	
		ry = computePolyharmonicWavelets(pyramid, rieszChannels[1]);
		
		//----------------------------------
		// ANALYSIS COMPLETE FOR WAVENUMBER (ORDER-1)
		//----------------------------------
		
		ImagePlus impSource = new ImagePlus("", source.buildImageStack());
		ComplexImage image = new ComplexImage(impSource);
		walk.progress("Wavenumber", 60);
		Parameters param = new Parameters();
		param.J = scale;
		param.redundancy = (pyramid ? Parameters.PYRAMID : Parameters.REDUNDANT);
		param.analysesonly = true;
		param.flavor = Parameters.MARR;
		param.prefilter = true;
		param.lattice = Parameters.DYADIC; 
		param.order = ORDER-1;
		param.rieszfreq = 1;
		param.N = 0;
		DyadicFilters filters1 = new DyadicFilters(param, image.nx, image.ny);
		filters1.compute();
		DyadicTransform transform = new DyadicTransform(filters1, param);	
		ComplexImage[] q1xq2y = transform.analysis(image);
		//show(q1xq2y, "q1xq2y");

		//----------------------------------
		// ANALYSIS COMPLETE FOR WAVENUMBER (ORDER-1)
		//----------------------------------
		tic();
		//DyadicFilters filters = new DyadicFilters(param, image.nx, image.ny);
		Parameters param1 = new Parameters();
		param1.J = scale;
		param1.redundancy = (pyramid ? Parameters.PYRAMID : Parameters.REDUNDANT);
		param1.analysesonly = true;
		param1.flavor = Parameters.MARR;
		param1.prefilter = true;
		param1.lattice = Parameters.DYADIC; 
		param1.order = ORDER-1;
		param1.rieszfreq = 1;
		param1.N = 1;
		DyadicFilters filters = new DyadicFilters(param1, image.nx, image.ny);
		filters.compute();
		
		DyadicTransform transform1 = new DyadicTransform(filters,param1);	
		ComplexImage[] pxpy = transform1.analysis(image);

		walk.progress("ST", 70);
		
		computeStructureTensor(sigma, epsilon, signedDir);
		
		walk.progress("Monogenic", 70);		
		computeMonogenic(pxpy, q1xq2y);
		
		walk.finish();
	}

	/**
	* 
	*/
	private ImageWare prefilter(ImageWare in) {
		Parameters param = new Parameters();
		param.J = scale;
		param.analysesonly = true;
		param.flavor = Parameters.MARR;
		param.prefilter = true;
		param.lattice = Parameters.DYADIC;    // only gamma and J can be set from the outside
		param.order = ORDER;
		param.rieszfreq = 0;
		param.N = 0;
		DyadicFilters filters = new DyadicFilters(param, in.getWidth(), in.getHeight());
		ComplexImage P = filters.computePrefilter(ORDER);
		ComplexImage X = new ComplexImage(in);;
		X.FFT2D();		
		X.multiply(P);
		X.iFFT2D();
		return convertDoubleToImageWare(X.real, in.getWidth(), in.getHeight());
	}
	
	
	/**
	* Compute Polyharmonic Wavelet
	*/
	public ImageWare computePolyharmonicWavelets(boolean pyramid, ImageWare in) {
		Parameters param = new Parameters();
		param.J = scale;
		param.redundancy = (pyramid ? Parameters.PYRAMID : Parameters.REDUNDANT);
		param.analysesonly = true;
		param.flavor = Parameters.MARR;
		param.prefilter = true;
		param.lattice = Parameters.DYADIC;    // only gamma and J can be set from the outside
		param.order = ORDER;
		param.rieszfreq = 0;
		param.N = 0;
		DyadicFilters filters = new DyadicFilters(param, in.getWidth(), in.getHeight());
		filters.compute();
		DyadicTransform transform = new DyadicTransform(filters, param);	
		ImageWare out = transform.analysisImage(in);		
		return out;
	}
	
	/**
	* Compute Polyharmonic Wavelet
	*/
	public ComplexImage[] computePolyharmonicWavelets(Parameters param, ComplexImage image) {
		param.order = ORDER;
		param.rieszfreq = 0;
		param.N = 0;
		DyadicFilters filtersPHW = new DyadicFilters(param, image.nx, image.ny);
		filtersPHW.setParameters(param);
		filtersPHW.compute();
		DyadicTransform transform = new DyadicTransform(filtersPHW, param);	
		ComplexImage[][] plp = transform.analysisLowpass(image);	
		return plp[0];
		//lowpassSubbands = plp[1];
	}
	
	/**
	*/
	public void computeStructureTensor(double sigma, double epsilon, boolean signedDir) {
		StructureTensor tensor = new StructureTensor(null);
		tensor.compute(rx, ry, sigma, epsilon);
		orientation = tensor.getOrientation();
		energy = tensor.getEnergy();
		coherency = tensor.getCoherency();
		if (signedDir) {
			for(int k=0; k<scale; k++)
			for(int x=0; x<nx; x++) 
			for(int y=0; y<ny; y++) {
				if (ry.getPixel(x, y, k) < 0)
					orientation.putPixel(x, y, k, -orientation.getPixel(x, y, k));
			}
		}
	}

	/**
	*/
	public void computeMonogenic(ComplexImage[] pxpy, ComplexImage[] q1xq2y ) {
		double theta, cos, sin, nu, amp, qmu, or, r1, r2, p, q;
		int k;
		double correctionFreq = 1;
		for(int j=0; j<scale; j++) {
			ComplexImage w = pxpy[j];	// Polyharmonic wavelet ORDER-1
			ComplexImage r = q1xq2y[j];	// Riesz ORDER-1
			for(int x=0; x<w.nx; x++) 
			for(int y=0; y<w.ny; y++) {
				k = x + y*w.nx;
				theta = orientation.getPixel(x, y, j);
				p = laplace.getPixel(x, y, j);
				r1 = rx.getPixel(x, y, j);
				r2 = ry.getPixel(x, y, j);
				cos = Math.cos(theta);
				sin = Math.sin(theta);
				q = r2 * cos + r1 * sin;
				amp = Math.sqrt(p*p + q*q);
				nu = q * (w.real[k] * cos + w.imag[k]*sin) + p * r.real[k] ;
				monogenicModulus.putPixel(x, y, j, amp);
				monogenicPhase.putPixel(x, y, j, Math.atan2(q, p));
				monogenicFrequency.putPixel(x, y, j, (nu * correctionFreq )/ (amp*amp) );
				directionalHilbert.putPixel(x, y, j, q);
			}
			correctionFreq = correctionFreq / 2.0;

		}
	}
	
	private void show(ComplexImage[] c, String msg) {
		for(int s=0; s<c.length; s++) {
			ComplexImage temp = c[s].copyImage();
			temp.showReal(msg + " real" + s);
			if (temp.imag != null)
				temp.showImag(msg + " imag" + s);
			if  (temp.imag != null)
				temp.showModulus(msg + " mod" + s);
		}
	}

	/**
	*/
	private ImageWare convertDoubleToImageWare(double array[], int mx, int my) {
		ImageWare image = Builder.create(mx, my, 1, ImageWare.DOUBLE);
		int k = 0;
		for(int y=0; y<my; y++)
		for(int x=0; x<mx; x++)
			image.putPixel(x, y, 0, array[k++]);
		return image;
	}	

	/**
	*/
	private double[] convertImageWareToDouble(ImageWare image) {
		int nx = image.getWidth();
		int ny = image.getHeight();
		int k = 0;
		double array[] = new double[nx*ny];
		for(int y=0; y<ny; y++)
		for(int x=0; x<nx; x++) {
			array[k] = image.getPixel(x, y, 0);
			k++;
		}
		return array;
	}	
	
	/**
	*/
	private ImageWare median(ImageWare in) {
		ImageWare out = in.replicate();
		for(int s=0; s<in.getSizeZ(); s++) {
			double block[][] = new double[3][3];
			double array[] = new double[5];
			for(int i=0; i<in.getWidth(); i++) 
			for(int j=0; j<in.getHeight(); j++) {
				in.getNeighborhoodXY(i, j, s, block, ImageWare.MIRROR);
				array[0] = block[1][0];
				array[1] = block[1][2];
				array[2] = block[0][1];
				array[3] = block[2][1];
				array[4] = block[1][1];
				Arrays.sort(array);
				out.putPixel(i, j, s, array[2]);
			}
		}
		return out;
	}	
}

