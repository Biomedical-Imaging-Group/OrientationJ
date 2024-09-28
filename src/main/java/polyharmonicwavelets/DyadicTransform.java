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
//====================================================================
package polyharmonicwavelets;

import orientation.imageware.Builder;
import orientation.imageware.ImageWare;

/**
* This class performs basis, pyramid and redundant dyadic transform.
* @author Katarina Balac, EPFL.
*/

public class DyadicTransform {

    // Analysis and synthesis filters 
	// FA[0]=H1
	// FA[1]=G1
	// FA[2]=G2
	// FA[3]=G3
	// similar for analysis filters
	// H - lowpass filters
	// G - highpass filters
	
	private ComplexImage[] FA;
	private ComplexImage[] FS;
	private ComplexImage[] FP;
	private ComplexImage P;
	private int J;   //number of iterations       
	private Parameters param;
	private final int nx;
	private final int ny;
	private final double PI2=2.0*Math.PI;	
	private final double sqrt2=Math.sqrt(2.0);
	private DyadicFilters filters;
	
	/**
	* Creates a DyadicTransform object.
	* @param filt the filters used for the dyadic transform
	* @param par the transform parameters 
	*/
	
	public DyadicTransform(DyadicFilters filt, Parameters par) {
		J=par.J;
		param=par;
		FA=filt.FA;
		FS=filt.FS;
		FP=filt.FP;
		P=filt.P;
		nx=FA[0].nx;
		ny=FA[0].ny;
		filters=filt;
	}

	/**
	*/
	public ComplexImage[] analysis(ComplexImage image) {
		if (param.redundancy == Parameters.PYRAMID)
			return analysisPyramid(image);
		else
			return analysisRedundant(image);
		
	}

	/**
	*/
	public ComplexImage synthesis(ComplexImage coef[]) {
		if (param.redundancy == Parameters.PYRAMID)
			return synthesisPyramid(coef);
		else
			return synthesisRedundant(coef);
		
	}

	/**
	*/
	public ComplexImage[][] analysisLowpass(ComplexImage image) {
		if (param.redundancy == Parameters.PYRAMID)
			return analysisPyramidLowpass(image);
		else
			return analysisRedundantLowpass(image);
	}

	/**
	* Added for MonogenicJ, Daniel Sage
	*/
	public ImageWare analysisImage(ImageWare image) {
		if (param.redundancy == Parameters.PYRAMID)
			return analysisPyramidImage(image);
		else
			return analysisRedundantImage(image);
	}

	/**
	* Added for MonogenicJ, Daniel Sage
	*/
	public ImageWare analysisRedundantImage(ImageWare image) {
		int nx = image.getWidth();
		int ny = image.getHeight();
		ImageWare out = Builder.create(nx, ny, J, ImageWare.DOUBLE);
			
		ComplexImage H = FA[0].copyImage();
		ComplexImage G = FA[1].copyImage();
		G.multiply(0.5*sqrt2);      			
		ComplexImage X = new ComplexImage(image);
		X.FFT2D();		
		if (param.prefilter) {
			X.multiply(P);
		}
		int l = 1;
		for(int j=0; j<J; j++) {		
			ComplexImage band = X.copyImage();
			band.multiplyCircular(G,l);
			band.iFFT2D();		
			X.multiplyCircular(H, l);				
			l *= 2;
			double pix[] = out.getSliceDouble(j);
			System.arraycopy(band.real, 0, pix, 0, nx*ny);
		}	
		// Put remaining lowpass in stack
		//Complex lowp = X.copyImage();
		//lowp.iFFT2D();
		
		return out;
	}
	
	/**
	* Added for MonogenicJ, Daniel Sage
	*/
	public ImageWare analysisPyramidImage(ImageWare image) {
		int nx = image.getWidth();
		int ny = image.getHeight();
		ImageWare out = Builder.create(nx, ny, J, ImageWare.DOUBLE);
		ComplexImage H = FA[0];
		ComplexImage G = FA[1];	
		H.multiply(0.25);		
		G.multiply(0.5*sqrt2);      					
		ComplexImage X = new ComplexImage(image);
		X.FFT2D();
		
		if (param.prefilter) {
			X.multiply(P);
		}
		int l = 1;
		for(int j=0; j<J; j++) {
			ComplexImage band = new ComplexImage(X.nx, X.ny);
			band.copyImageContent(X);
			band.multiply(G,l);
			band.iFFT2D();		
			X.multiply(H,l);				
			X.dyadicDownUpCrop();		
			l *= 2;
			
			double pix[] = out.getSliceDouble(j);
			int mx = band.nx;
			int my = band.ny;
			for(int x=0; x<mx; x++)
			for(int y=0; y<my; y++)
				pix[x+nx*y] = band.real[x+y*mx];
			
		}	
		// Put remaining lowpass in stack
		//ComplexImage lowp = X.copyImage();
		//lowp.iFFT2D();
		return out;
	}
	
	/**
	* Returns the result of fully redundant pyramid analysis of ComplexImage and all the intermediary lowpass subbands.
	* @param image the image to transform
	* @return in [0][] the array of dyadic transform subbands, and in [1][] the intermidiary lowpass subbands
	*/
	public ComplexImage[][] analysisRedundantLowpass(ComplexImage image) {
		ComplexImage[] lowpassSubbands = new ComplexImage[J+1];
		ComplexImage[] array=new ComplexImage[J+1];	
		ComplexImage H=FA[0].copyImage();
		ComplexImage G=FA[1].copyImage();
		G.multiply(0.5*sqrt2);      			
		ComplexImage X=image.copyImage();
		X.FFT2D();		
		if (param.prefilter) {
			X.multiply(P);
		}	
		for(int j=1,l=1;j<=J;j++) {		
			ComplexImage Yh=X.copyImage();
			Yh.multiplyCircular(G,l);
			// Put Yh in stack
			Yh.iFFT2D();		
			array[j-1]=Yh;
			ComplexImage Yl=X;		
			Yl.multiplyCircular(H,l);				
			lowpassSubbands[j-1]=Yl.copyImage();	
			lowpassSubbands[j-1].iFFT2D();
			X=Yl;
			l*=2;
		}	
		// Put remaining lowpass in stack
		X.iFFT2D();
		array[J]=X;
		lowpassSubbands[J]=X.copyImage();	
		G.multiply(sqrt2);
		ComplexImage[][] out=new ComplexImage[2][];
		out[0]=array;
		out[1]=lowpassSubbands;	
		return out;
	}


	/**
	* Returns the result of fully redundant dyadic analysis of ComplexImage.
	* @param image the ComplexImage to transform
	* @return the array of dyadic transform subbands
	*/
	
	private ComplexImage[] analysisRedundant(ComplexImage image) {
		ComplexImage[] array=new ComplexImage[J+1];	
		ComplexImage H=FA[0].copyImage();
		ComplexImage G=FA[1].copyImage();		
		G.multiply(0.5*sqrt2);  		    			
		ComplexImage X = image.copyImage();
		X.FFT2D();		
		if (param.prefilter) {
			X.multiply(P);
		}	
		for(int j=1,l=1;j<=J;j++) {		
			ComplexImage Yh=X.copyImage();
			Yh.multiplyCircular(G,l);
			// Put Yh in stack
			Yh.iFFT2D();		
			array[j-1]=Yh;
			ComplexImage Yl=X;		
			Yl.multiplyCircular(H,l);				
			X=Yl;
			l*=2;
		}	
		// Put remaining lowpass in stack
		X.iFFT2D();
		array[J]=X;
		G.multiply(sqrt2);
		return array;
	}

		
	/**
	* Returns the result of dyadic pyramid analysis of ComplexImage.
	* @param image the image to transform
	* @return the array of dyadic transform subbands
	*/
	
	private ComplexImage[] analysisPyramid(ComplexImage image) {
		ComplexImage[] array=new ComplexImage[J+1];	
		ComplexImage H=FA[0];
		ComplexImage G=FA[1];	
		H.multiply(0.25);		
		G.multiply(0.5*sqrt2);		      			
		ComplexImage X=image.copyImage();
		X.FFT2D();		
		if (param.prefilter) {
			X.multiply(P);
		}	
		for(int j=1,l=1;j<=J;j++) {		
			ComplexImage Yh=X.copyImage();
			Yh.multiply(G,l);
			// Put Yh in stack
			Yh.iFFT2D();		
			array[j-1]=Yh;	
			ComplexImage Yl=X;		
			Yl.multiply(H,l);				
			l*=2;
			Yl.dyadicDownUpCrop();			
			X=Yl;
		}	
		// Put remaining lowpass in stack
		X.iFFT2D();
		array[J]=X;	
		H.multiply(4.0);		
		G.multiply(sqrt2);	
		return array;
	}


	/**
	* Returns the result of dyadic pyramid analysis of ComplexImage and all the intermediary lowpass subbands.
	* @param image the image to transform
	* @return in [0][] the array of dyadic transform subbands, and in [1][] the intermidiary lowpass subbands
	*/
	
	private ComplexImage[][] analysisPyramidLowpass(ComplexImage image) {
		ComplexImage[] lowpassSubbands=new ComplexImage[J+1];
		ComplexImage[] array=new ComplexImage[J+1];	
		ComplexImage H=FA[0];
		ComplexImage G=FA[1];	
		H.multiply(0.25);		
		G.multiply(0.5*sqrt2);      					
		ComplexImage X=image.copyImage();
		//ComplexImage Yh=new ComplexImage(image.nx,image.ny);
		X.FFT2D();		
		if (param.prefilter) {
			X.multiply(P);
		}	
		for(int j=1,l=1;j<=J;j++) {
			ComplexImage Yh=new ComplexImage(image.nx,image.ny);
			Yh.copyImageContent(X);
			Yh.multiply(G,l);
			// Put Yh in stack
			Yh.iFFT2D();		
			array[j-1]=Yh;
			ComplexImage Yl=X;		
			Yl.multiply(H,l);				
			lowpassSubbands[j-1]=Yl.copyImage();	
			lowpassSubbands[j-1].iFFT2D();
			l*=2;
			Yl.dyadicDownUpCrop();		
			X=Yl;
		}	
		// Put remaining lowpass in stack
		X.iFFT2D();
		array[J]=X;
		lowpassSubbands[J]=X.copyImage();
		//H.multiply(4.0);		
		//G.multiply(sqrt2);
		ComplexImage[][] out=new ComplexImage[2][];
		out[0]=array;
		out[1]=lowpassSubbands;	
		return out;
	}

	
	/**
	 * Returns the result of fully redundant dyadic synthesis.
	 * @param array subbands of dyadic pyramid transform 
	 * @return the result of synthesis
	 */
	
	private ComplexImage synthesisRedundant(ComplexImage[] array) {
		ComplexImage H=FS[0].copyImage(); 
		ComplexImage G=FS[1].copyImage();
		G.add(FS[2]);  
		G.add(FS[3]);	
		G.multiply(0.25*sqrt2);		
		H.multiply(0.25);
		if (param.flavor==param.MARR) {
			G.divide(filters.ac);
		}
		int l=1;
		for(int k=1;k<J;k++,l*=2);
		ComplexImage Y=array[J].copyImage();    // Lowpass
		Y.FFT2D();	
		for(int j=J;j>0;j--) {
			ComplexImage Z=array[j-1].copyImage();
			Z.FFT2D();			
			Y.multiplyCircular(H,l);
			Z.multiplyCircular(G,l);
			l/=2;
			Y.add(Z);
		}
		if (param.prefilter) {
			Y.divide(P);
		}	
		Y.iFFT2D();
		return Y;
	}
	
	
	/**
	* Returns the result of pyramid dyadic synthesis of array.
	* @param array subbands of dyadic pyramid transform 
	* @return the result of synthesis
	*/

	private ComplexImage synthesisPyramid(ComplexImage[] array) { 			
		FS[1].multiply(0.5);
		FS[2].multiply(0.5);
		FS[3].multiply(0.5);					
		ComplexImage Gconj=FA[1].copyImage();
		Gconj.multiply(1/sqrt2);	
		ComplexImage[] G0=FP;
		// Reconstruct with calculated filters G0, FS, Gconj
		int l=1;		
		for(int i=0;i<J;i++,l*=2);
		ComplexImage Y1=new ComplexImage(array[0].nx,array[0].ny);
		ComplexImage Y2=new ComplexImage(array[0].nx,array[0].ny);
		ComplexImage GY=new ComplexImage(array[0].nx,array[0].ny);

		Y1.copyImageContent(array[J]);
		Y1.FFT2D();		
		for(int j=J-1;j>=0;j--) {	
			Y2.copyImageContent(array[j]);
			Y2.FFT2D();
			l/=2;			
			Y1.dyadicUpsample();
			Y1.multiply(FS[0],l);					
			GY.copyImageContent(Y1);
			GY.multiply(Gconj,l);
			ComplexImage Y21=Y2;
			Y21.subtract(GY);		
			GY.copyImageContent(Y21);
			GY.multiply(G0[1],l);	
			GY.dyadicDownUp();			
			GY.multiply(FS[2],l);		
			Y1.add(GY);
			GY.copyImageContent(Y21);
			GY.multiply(G0[2],l);
			GY.dyadicDownUp();
			GY.multiply(FS[3],l);
			Y1.add(GY);
			Y21.multiply(G0[0],l);			
			Y21.dyadicDownUp();
			Y21.multiply(FS[1],l);
			Y1.add(Y21);	
		}			
		if (param.prefilter) {	
			Y1.divide(P);
		}	
		Y1.iFFT2D();		
		FS[1].multiply(2.0);
		FS[2].multiply(2.0);
		FS[3].multiply(2.0);					
		return Y1;
	}
	

	/**
	* Prepares the real dyadic pyramid transform coeffitients for being displayed.
	* Rescales the subbands for visualisation and puts all subbands in one ComplexImage.
	* @param array subbands of dyadic pyramid transform 
	* @param back background color
	* @param rescale if rescale=false there is no rescaling	
	* @param lp if lp=false the lowpass subband is not displayed
	* @return the image to display
	*/

	public ComplexImage displayDyadicPyramidReal(ComplexImage[] array, double back, boolean rescale, boolean lp) {	
		int J1=J;
		if (!lp) {
			J1-=1;
		}
		int nx=array[0].nx;
		int ny=array[0].ny;
		ComplexImage disp=new ComplexImage(nx,2*ny);
		disp.settoConstant(back,back);
		int x=0;
		int y=0;
		int dx=nx/4;
		int dy=ny;
		for (int j=0;j<=J1;j++) {	
			ComplexImage temp=array[j].copyImage();	
			if (rescale) {	
				temp.stretch();	
			} 
			temp.frame(back);
			disp.putSubimage(x,y,temp);
			x+=dx;
			y+=dy;
			dx/=2;
			dy/=2;
		}
		return disp;	
	}
	
	
	/**
	* Prepares the dyadic pyramid transform coeffitients for being displayed.
	* @param array subbands of dyadic pyramid transform 
	* @param back background color
	* @param rescale if rescale=false there is no rescaling	
	* @param lp if lp=false the lowpass subband is not displayed
	* @return the image to display
	*/

	private ComplexImage displayDyadicPyramid(ComplexImage[] array, double back, boolean rescale, boolean lp) {	
		int J1=J;
		if (!lp) {
			J1-=1;
		}
		int nx=array[0].nx;
		int ny=array[0].ny;
		ComplexImage disp=new ComplexImage(2*nx,2*ny);
		disp.settoConstant(back,back);
		int x=0;
		int y=0;
		int dx=nx/2;
		int dy=ny;
		for (int j=0;j<=J1;j++) {	
			ComplexImage temp=array[j].copyImage();	
			if (rescale) {	
				temp.stretch();	
			}	
			temp.frame(back,back);	
						
			disp.putSubimage(x,y,temp);
			System.arraycopy(temp.imag,0,temp.real, 0, temp.nxy);
			disp.putSubimage(x+temp.nx,y,temp);
			disp.setImagtoZero();
			x+=dx;
			y+=dy;
			dx/=2;
			dy/=2;
		}
		return disp;	
	}
	
	
	/**
	* Prepares a nonredundant dyadic transform for being displayed, stretches each subband.
	* @param image the basis transform coefficients
	* @return the image with rescaled subbands to display
	*/

	private ComplexImage displayBasis(ComplexImage image) {
		int dx=image.nx;
		int dy=image.ny;
		ComplexImage out=new ComplexImage(dx,dy);
		ComplexImage sub;
		for(int j=1;j<=J;j++) {
			sub=image.getSubimage(dx/2,dx-1,0,dy/2-1);			
			sub.stretch();		
			out.putSubimage(dx/2,0,sub);
			sub.getSubimageContent(dx/2,dx/2+sub.nx-1,dy/2,dy/2+sub.ny-1,image);			
			sub.stretch();		
			out.putSubimage(dx/2,dy/2,sub);
			sub.getSubimageContent(0,sub.nx-1,dy/2,dy/2+sub.ny-1,image);			
			sub.stretch();		
			out.putSubimage(0,dy/2,sub);
			dx/=2;	
			dy/=2;
		}
		sub=image.getSubimage(0,dx-1,0,dy-1);			
		sub.stretch();		
		out.putSubimage(0,0,sub);
		return out;
	}
}