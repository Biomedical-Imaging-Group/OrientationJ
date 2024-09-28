//====================================================================
//
// Project: 
// Multiscale Directional Analysis
// 
// Organization: 
// Danie Sage
// Biomedical Imaging Group (BIG)
// Ecole Polytechnique Fédérale de Lausanne (EPFL)
// Lausanne, Switzerland
//
// Information:
// http://bigwww.epfl.ch/demo/monogenic
//
// Conditions of use:
// You'll be free to use this software for research purposes, but you
// should not redistribute it without our consent. In addition, we 
// expect you to include a citation or acknowledgement whenever 
// you present or publish results that are based on it.
//
// History:
// 01.01.2008 Katarina Balac: Creation of the class
// 25.08.2009 Daniel Sage: Completely revamp this class
//====================================================================
package polyharmonicwavelets;

import static gui_orientation.Chrono.tic;

import orientation.imageware.Builder;
import orientation.imageware.ImageWare;

/**
* This class computes the Riesz wavelet transform of the image as well as its monogenic transform parameters 
* such as the local orientation, amplitude and the instantaneous frequency in each subband.
* @author Katarina Balac, EPFL.
*/

public class Riesz {

	public ComplexImage image;					// Source input image
	public ComplexImage[] p;					// The polyharmonic wavelet transform of the image
	public ComplexImage[] q;					// The riesz transform of p, the wavelet Riesz transform of the image.
	//public ComplexImage[] lowpassSubbands;		// This variable stores all the lowpass subbands The j-th subband is in lowpassSubbands[j-1], j=1...J+1.
	public ComplexImage[] q1xq2y=null;
	public ComplexImage[] pxpy=null;
	
	final private double ORDER = 2.0;;

	public Riesz(ComplexImage image, int scale,  boolean pyramid) {
	
		this.image = image;
		Parameters param = new Parameters();
		param.J = scale;
		param.redundancy = (pyramid ? Parameters.PYRAMID : Parameters.REDUNDANT);
		param.analysesonly = true;
		param.flavor = param.MARR;
		param.prefilter = true;
		param.lattice = param.DYADIC;    // only gamma and J can be set from the outside
		
		//----------------------------------
		// POLYHARMONIC WAVELET
		//----------------------------------
		tic();
		param.order = ORDER;
		param.rieszfreq = 0;
		param.N = 0;
		DyadicFilters filtersPHW = new DyadicFilters(param, image.nx, image.ny);
		filtersPHW.setParameters(param);
		filtersPHW.compute();
		//showFilter(scale, filters2, "2");
		DyadicTransform transformPHW = new DyadicTransform(filtersPHW, param);	
		ComplexImage[][] plp = transformPHW.analysisLowpass(image);	
		p = plp[0];
		//lowpassSubbands = plp[1];
		//IJ.write(toc("Analysis PHW"));

		//----------------------------------------------
		// RIESZ TRANSFORM OF THE POLYHARMONIC WAVELET
		//----------------------------------------------
		
		tic();
		param.order = ORDER;
		param.rieszfreq = 0;
		param.N = 1;
		DyadicFilters filters = new DyadicFilters(param, image.nx, image.ny);
		filters.compute();
		
		//show(filters, "Filters for Q");
		
		DyadicTransform transform = new DyadicTransform(filters, param);			
		q= transform.analysis(image);
		
		//show(qi, "QI");
		/*
		IJ.write(toc("1. Q"));
		
		tic();
		q = new ComplexImage[scale];
		for(int j=0; j<scale; j++) {
			int nx = p[j].nx;
			int ny = p[j].ny;	
			ImageWare in = convertDoubleToImageWare(p[j].real, nx, ny);
			RieszTransform rt = new RieszTransform(nx, ny, 1, true);
			ImageWare rtim = rt.analysis(in);
			q[j] = new ComplexImage(rtim);
		}
		IJ.write(toc("2. Q"));
		//show(q, "Q");
		*/	
		//----------------------------------
		// ANALYSIS COMPLETE FOR WAVENUMBER (ORDER-1)
		//----------------------------------
		tic();
		param.order = ORDER-1;
		param.rieszfreq = 1;
		param.N = 0;
		DyadicFilters filters1 = new DyadicFilters(param, image.nx, image.ny);
		filters1.setParameters(param);
		filters1.compute();
		transform = new DyadicTransform(filters1, param);	
		q1xq2y = transform.analysis(image);
		//show(q1xq2y, "q1xq2y");

		//----------------------------------
		// ANALYSIS COMPLETE FOR WAVENUMBER (ORDER-1)
		//----------------------------------
		tic();
		param.order = ORDER-1;
		param.rieszfreq = 1;
		param.N = 1;
		filters.setParameters(param);	
		filters.compute();
		transform=new DyadicTransform(filters,param);	
		pxpy=transform.analysis(image);
		//show(pxpy, "pxpy");
		
	}
	
	private void show(DyadicFilters filters, String msg) {
		for(int s=0; s<=1; s++) {
			ComplexImage temp = filters.FA[s].copyImage();
			temp.shift();
			temp.showReal(msg + " FA real" + s);
			if (temp.imag != null)
				temp.showImag(msg + " FA imag" + s);
			if  (temp.imag != null)
				temp.showModulus(msg + " FA mod" + s);
		}
	}
	
	private void show(ComplexImage[] c, String msg) {
		for(int s=0; s<1 /*c.length*/; s++) {
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
	* Creates a Riesz object.
	*
	public Riesz1(ComplexImage image, int scale,  boolean pyramid) {
	
		this.image = image;
		Parameters param	= new Parameters();
		param.J				= scale;
		param.redundancy	= (pyramid ? Parameters.PYRAMID : Parameters.REDUNDANT);
		param.analysesonly	= true;
		param.flavor		= param.MARR;
		param.prefilter		= true;
		param.lattice		= param.DYADIC;    // only gamma and J can be set from the outside
		
		//----------------------------------
		// ANALYSIS COMPLETE
		//----------------------------------
		tic();
		param.order = ORDER;
		param.rieszfreq = 0;
		param.N = 1;
		DyadicFilters filters1 = new DyadicFilters(param, image.nx, image.ny);
		filters1.compute();
		//
		for(int s=0; s<scale; s++) {
			ComplexImage temp = filters1.FA[s].copyImage();
			temp.shift();
			temp.showReal("1 FA real" + s);
			if (temp.imag != null)
				temp.showImag("1 FA imag" + s);
			if  (temp.imag != null)
				temp.showModulus("1 FA mod" + s);
		}
		//
		DyadicTransform transform = new DyadicTransform(filters1, param);			
		q = transform.analysis(image);
		IJ.write(toc("1. Analysis Complete"));
		
		double[] temp1 = filters1.FA[0].imag;
		double[] temp2 = filters1.FA[1].imag;
		
		//----------------------------------
		// ANALYSIS LOWPASS
		//----------------------------------
		tic();
		param.order = ORDER;
		param.rieszfreq = 0;
		param.N = 0;
		DyadicFilters filters2 = new DyadicFilters(param, image.nx, image.ny);
		filters2.compute();
		// show filters 
		for(int s=0; s<scale; s++) {
			ComplexImage temp = filters2.FA[s].copyImage();
			temp.shift();
			temp.showReal("2 FA real" + s);
			if (temp.imag != null) 
				temp.showImag("2 FA imag" + s);
			if  (temp.imag != null)
				temp.showModulus("2 FA mod" + s);

		}
		
		transform = new DyadicTransform(filters2, param);	
		ComplexImage[][] plp = transform.analysisLowpass(image);	
		p = plp[0];
		lowpassSubbands = plp[1];
		IJ.write(toc("2. Analysis Lowpass"));
		
		//----------------------------------
		// ANALYSIS COMPLETE FOR WAVENUMBER
		//----------------------------------
		tic();
		param.rieszfreq = 1;
		param.order = ORDER-1;
		param.N = 0;
				
		DyadicFilters filters3 = new DyadicFilters(param, image.nx, image.ny);
		filters3.compute();
		// show filters 
		for(int s=0; s<scale; s++) {
			ComplexImage temp = filters3.FA[s].copyImage();
			temp.shift();
			temp.showReal("3 FA real" + s);
			if (temp.imag != null) 
				temp.showImag("3 FA imag" + s);
			if  (temp.imag != null)
				temp.showModulus("3 FA mod" + s);

		}
		
		transform = new DyadicTransform(filters3, param);	
		q1xq2y = transform.analysis(image);
		IJ.write(toc("3. Analysis q1xq2y WAVENUMBER"));

		//----------------------------------
		// ANALYSIS COMPLETE
		//----------------------------------
		tic();
		param.rieszfreq = 1;
		param.order = ORDER-1;
		param.N = 1;
		
		filters1.FA[0].imag=null;
		filters1.FA[1].imag=null;
		filters1.FA[0].imag=temp1;
		filters1.FA[1].imag=temp2;
		filters1.setParameters(param);	
		filters1.compute();
		
		//DyadicFilters filters4 = new DyadicFilters(param, image.nx, image.ny);
		//filters4.compute();
		
		// show filters 
		for(int s=0; s<scale; s++) {
			ComplexImage temp = filters4.FA[s].copyImage();
			temp.shift();
			temp.showReal("4 FA real" + s);
			if (temp.imag != null) 
				temp.showImag("4 FA imag" + s);
			if  (temp.imag != null)
				temp.showModulus("4 FA mod" + s);

		}
		
		//
		transform = new DyadicTransform(filters1, param);	
		pxpy = transform.analysis(image);	
		IJ.write(toc("4. Analysis pxpy WAVENUMBER"));
	}
	*/
}		
	
	/*
	* Computes the local orientation and places it in orientation.
	*
	private void computeOrientation() {			
		orientation=new ComplexImage[J];
		int lx=image.nx;
		int ly=image.ny;
		ComplexImage modq=new ComplexImage(image.nx,image.ny);
		for(int j=0;j<J;j++) {
			orientation[j]=new ComplexImage(q[j].nx,q[j].ny,true);
			modq.nx=q[j].nx;
			modq.ny=q[j].ny;
			int size=orientation[j].nxy;
			for (int k=0;k<size;k++) {
				double a=modulus[j].real[k]*modulus[j].real[k];
				// Compute the weights modq	to smooth
				//modq.real[k]=(q[j].real[k]*q[j].real[k]+q[j].imag[k]*q[j].imag[k])/a;
				orientation[j].real[k]=Math.atan2(q[j].real[k], q[j].imag[k]);
				if (orientation[j].real[k]<0)
					orientation[j].real[k] += Math.PI/2;
				else
					orientation[j].real[k] -= Math.PI/2;
				orientation[j].real[k] = -orientation[j].real[k];
			}
			// Added by Daniel Sage 30.05.2008
			// Median-like on the angle when the modulus is too low 
			//orientation[j].smooth(modq);
			// modq.showReal("modq "+ j);
			for(int k=0; k<modq.nxy; k++) {
				int x= k / modq.nx;
				int y= k % modq.nx;
				if (x > 0)
				if (x <modq.nx-1)
				if (y > 0)
				if (y <modq.ny-1) {
					double max = modq.real[k];
					int kmax = k;
					double min = modq.real[k];
					int kmin = k;
					for (int u=x-1; u<x+1; u++)
					for (int v=y-1; v<y+1; v++) {
						if (modq.real[u+v*modq.nx] > max) {
							kmax = u+v*modq.nx;
							max = modq.real[kmax];
						}
						if (modq.real[u+v*modq.nx] < min) {
							kmin = u+v*modq.nx;
							min = modq.real[kmin];
						}
					}
					if (max - modq.real[k] > 0.8) {
						orientation[j].real[k] = orientation[j].real[kmax];
					}
					if (modq.real[k]-min > 0.8) {
						orientation[j].real[k] = orientation[j].real[kmin];
					}
				}
			}
			//
		}
	}
	*/
	
	/*
	* Computes the local phase and places it in phase.
	*
	private void computePhase() {
		phase=new ComplexImage[J];
		for(int j=0;j<J;j++) {
			phase[j]=new ComplexImage(q[j].nx,q[j].ny,true);
			int size=phase[j].nxy;
			for (int k=0;k<size;k++) {
				double a=modulus[j].real[k];
				phase[j].real[k]= Math.acos(p[j].real[k]/ a);       
				if(q[j].imag[k]<0) {
					phase[j].real[k]*=-1.0;
				}
			}
		}
	}
	*/

	
	/*
	* Computes the local wave number and places it in wavenumber.
	*
	private void computeWavenumber() {
		param.rieszfreq=1;
		param.N=0;
		param.order=order-1;
		double[] temp1=filters.FA[0].imag;
		filters.FA[0].imag=null;
		double[] temp2=filters.FA[1].imag;
		filters.FA[1].imag=null;
		filters.setParameters(param);
		filters.compute();
		transform=new DyadicTransform(filters,param);	
		q1xq2y=transform.analysis(image);	
		
		
		param.N=1;
		filters.FA[0].imag=temp1;
		filters.FA[1].imag=temp2;
		filters.setParameters(param);	
		filters.compute();
		transform=new DyadicTransform(filters,param);	
		pxpy=transform.analysis(image);	
		
		wavenumber=new ComplexImage[J];
		ComplexImage modq=new ComplexImage(image.nx,image.ny);
		for(int j=0;j<J;j++) {
			wavenumber[j]=new ComplexImage(q[j].nx,q[j].ny,true);
			modq.nx=q[j].nx;
			modq.ny=q[j].ny;
			int size = size=wavenumber[j].nxy;
			for (int k=0;k<size;k++) {
				double a=modulus[j].real[k]*modulus[j].real[k];
				wavenumber[j].real[k]=(p[j].real[k]*q1xq2y[j].real[k]+ q[j].real[k]*pxpy[j].real[k]+ q[j].imag[k]*pxpy[j].imag[k])/a;
			}
		}
	}
	*/
	
	/*
	 * Computes the local wave number and places it in wavenumber.
	 *
	public ComplexImage[] computeModifiedRiesz() {
		ComplexImage[] q1xq2y=null;
		ComplexImage[] pxpy=null;
		param.rieszfreq=1;
		param.N=0;
		param.order=order-1;
		double[] temp1=filters.FA[0].imag;
		filters.FA[0].imag=null;
		double[] temp2=filters.FA[1].imag;
		filters.FA[1].imag=null;
		filters.setParameters(param);
		filters.compute();
		transform=new DyadicTransform(filters,param);	
		param.N=1;
		filters.FA[0].imag=temp1;
		filters.FA[1].imag=temp2;
		filters.setParameters(param);	
		filters.compute();
		transform=new DyadicTransform(filters,param);	
		pxpy=transform.analysis(image);		
		return q1xq2y;
	}
	
	public ComplexImage[] computeModifiedWavelet() {
		ComplexImage[] q1xq2y=null;
		ComplexImage[] pxpy=null;
		param.rieszfreq=1;
		param.N=0;
		param.order=order-1;
		double[] temp1=filters.FA[0].imag;
		filters.FA[0].imag=null;
		double[] temp2=filters.FA[1].imag;
		filters.FA[1].imag=null;
		filters.setParameters(param);
		filters.compute();
		transform=new DyadicTransform(filters,param);	
		q1xq2y=transform.analysis(image);	
	
		param.N=1;
		filters.FA[0].imag=temp1;
		filters.FA[1].imag=temp2;
		filters.setParameters(param);	
		filters.compute();
		transform=new DyadicTransform(filters,param);	
		pxpy=transform.analysis(image);
		return pxpy;
	}
	*/
	/**
	* Displays the local modulus on the screen.
	
	
	public void displayModulus() {
		if (param.redundancy==param.PYRAMID) {		
			ComplexImage disp=transform.displayDyadicPyramidReal(modulus,0.0,false,false);
			disp.showReal("Riesz modulus");
		}
		if (param.redundancy==param.REDUNDANT) {	
			ComplexImage.displayStack(modulus,"Riesz modulus");
		}
	}
	*/
	
	/**
	* Displays the local wave number on the screen.
	
	
	public void displayWaveNumber() {
		if (param.redundancy==param.PYRAMID) {	
			ComplexImage disp=transform.displayDyadicPyramidReal(wavenumber,0.0,false,false);
			disp.showReal("Riesz wave number magnitude");
		}
		if (param.redundancy==param.REDUNDANT) {	
			ComplexImage.displayStack(wavenumber,"Riesz wave number magnitude");
		}
	}
	*/
		
	/**
	* Displays the local phase on the screen.
	
	
	public void displayPhase() {
		if (param.redundancy==param.PYRAMID) {	
			ComplexImage disp=transform.displayDyadicPyramidReal(phase,0.0,false,false);
			disp.showReal("Riesz phase");
		}
		if (param.redundancy==param.REDUNDANT) {	
			ComplexImage.displayStack(phase,"Riesz phase");
		}	
	}
	*/
	
	/**
	* Displays the local orientation on the screen.
	
	
	public void displayOrientation() {
		if (param.redundancy==param.PYRAMID) {	
			ComplexImage disp=transform.displayDyadicPyramidReal(orientation,0.0,false,false);
			disp.showReal("Riesz orientation");
		}
		if (param.redundancy==param.REDUNDANT) {	
			ComplexImage.displayStack(orientation,"Riesz orientation");
		}
	}
	*/
	
	/**
	* Displays the derivative in y direction.
	
	
	public void displayRieszY() {
		if (param.redundancy==param.PYRAMID) {	
			ComplexImage disp=transform.displayDyadicPyramidReal(q,0.0,false,false);
			disp.showReal("Riesz transform y");
		}
		if (param.redundancy==param.REDUNDANT) {	
			ComplexImage.displayStack(q,"Riesz transform y");
		}
	}
	
	*/
	/**
	* Displays the derivative in x direction.
	
	
	public void displayRieszX() {
		if (param.redundancy==param.PYRAMID) {	
			ComplexImage disp=transform.displayDyadicPyramidReal(q,0.0,false,false);
			disp.showImag("Riesz transform x");
		}
		if (param.redundancy==param.REDUNDANT) {	
			ComplexImage.displayStackImag(q,"Riesz transform x");
		}
	}
	*/
	
	/**
	* Displays the mother wavelet transform.
	
	
	public void displayMother() {
		if (param.redundancy==param.PYRAMID) {
			ComplexImage disp=transform.displayDyadicPyramidReal(p,0.0,false,false);
			disp.showReal("Mother transform");
		}
		if (param.redundancy==param.REDUNDANT) {	
			ComplexImage.displayStack(p,"Mother transform");
		}	
	}
	*/
