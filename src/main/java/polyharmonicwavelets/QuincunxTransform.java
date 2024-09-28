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



import java.util.*;
import ij.*;
import java.text.DecimalFormat;
import ij.text.*;
import ij.process.*;
import ij.plugin.filter.PlugInFilter;
import polyharmonicwavelets.*;


/**
* This class performs basis, pyramid and redundant quincunx transform.
* @author Katarina Balac, EPFL.
*/

public class QuincunxTransform {

    // Analysis and synthesis filters 
	// FA[0]=H1
	// FA[1]=G1
	// FA[2]=H1D
	// FA[3]=G1D
	// similar for analysis filters
	// H - lowpass filters
	// G - highpass filters
	// H1,G1 - filters for odd iteration
	// H1D, G1D - filters for even iteration
	// if transform is pyramid, filters for pyramid transform are FP:
	// FP[0]=Gls
	// FP[1]=GlsD
	
	private Parameters param;
	private ComplexImage[] FA;
	private ComplexImage[] FS;
	private ComplexImage[] FP;
	private ComplexImage P;
	private int J;   //number of iterations 
	private final double PI2=2.0*Math.PI;	
	private final double sqrt2=Math.sqrt(2.0);
	
	
	/**
	* Creates a QuincunxTransform object.
	* @param filt the filters used for the transform
	* @param par the transform parameters 
	*/

	public QuincunxTransform(QuincunxFilters filt, Parameters par) {
		J=par.J;
		param=par;
		FA=new ComplexImage[4];
		FS=new ComplexImage[4];
		for(int i=0;i<4;i++) {
			FA[i]=filt.FA[i];
			FS[i]=filt.FS[i];
		}
		P=filt.P.copyImage();
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
	* Returns the fully redundant quincunx analysis of image.
	* @param image the image to transform
	* @return the array of quincunx transform subbands
	*/

	private ComplexImage[] analysisRedundant(ComplexImage image) {
		ComplexImage H=FA[0];  // Lowpass odd iteration filter
		ComplexImage G=FA[1];  // Highpass odd iteration filter
		ComplexImage HD=FA[2];  // Lowpass even iteration filter
		ComplexImage GD=FA[3];  // Highpass even iteration filter
		G.multiply(sqrt2);
		GD.multiply(sqrt2);	
		H.multiply(sqrt2);
		HD.multiply(sqrt2);	
		double sqrt2inv=1.0/sqrt2;
		ComplexImage[] array=new ComplexImage[J+1];
		int l=1;
		//Next index in array
		ComplexImage R=image.copyImage();
		R.FFT2D();
		if (param.prefilter) {	
			R.multiply(P);
		}
		for(int j=1;j<J+1;j++) {	
			int k=j-1;
			array[k]=R.copyImage();	
			if (j%2==1) {        //odd iteration
				array[k].multiplyCircular(G,l);    //Multiply with highpass odd iteration filter
				R.multiplyCircular(H,l);		   //Multiply with lowpass odd iteration filter
			} 
			else {
				array[k].multiplyCircular(GD,l);   //Multiply with highpass even iteration filter
				R.multiplyCircular(HD,l);	      //Multiply with lowpass even iteration filter
				l*=2;	
			}
			array[k].multiply(sqrt2inv);
			R.multiply(sqrt2inv);
			array[k].iFFT2D();
		}	
		array[J]=R.copyImage();
		array[J].iFFT2D();
		G.multiply(1.0/sqrt2);
		GD.multiply(1.0/sqrt2);	
		H.multiply(1.0/sqrt2);
		HD.multiply(1.0/sqrt2);	
		return array;
	}


	/**
	* Returns the fully redundant quincunx synthesis of array.
	* @param array subbands of fully redundant quincunx transform 
	* @return the result of synthesis
	*/

	private ComplexImage synthesisRedundant(ComplexImage[] array) {
		ComplexImage H=FS[0];  // Lowpass odd iteration filter
		ComplexImage G=FS[1];  // Highpass odd iteration filter
		ComplexImage HD=FS[2];  // Lowpass even iteration filter
		ComplexImage GD=FS[3];  // Highpass even iteration filter		
		G.multiply(1.0/sqrt2);
		GD.multiply(1.0/sqrt2);
		H.multiply(1.0/sqrt2);
		HD.multiply(1.0/sqrt2);				
		double sqrt2inv=1.0/Math.sqrt(2.0);
		ComplexImage LP=array[J];
		ComplexImage HP;
		LP.FFT2D();
		int l=1;
		for(int j=1;j<(J+1)/2;j++,l*=2); 
		for(int j=J;j>0;j--) {			
			HP=array[j-1];
			HP.FFT2D();	
			if (j%2==1) {        //odd iteration
				HP.multiplyCircular(G,l);    //Multiply with highpass odd iteration filter
				LP.multiplyCircular(H,l);		//Multiply with lowpass odd iteration filter
				l/=2;
			} else {
				HP.multiplyCircular(GD,l);   //Multiply with highpass even iteration filter
				LP.multiplyCircular(HD,l);	//Multiply with lowpass even iteration filter		
			}
			LP.add(HP);
			LP.multiply(sqrt2inv);
		}
		if (param.prefilter) {	
			LP.divide(P,1.0,0.0);
		}
		LP.iFFT2D();
		G.multiply(sqrt2);
		GD.multiply(sqrt2);
		H.multiply(sqrt2);
		HD.multiply(sqrt2);
		return LP;
	}
	

	/**
	* Returns the result of pyramid quincunx analysis of ComplexImage.
	* @param image the image to transform
	* @return the array of quincunx transform subbands
	*/
	
	private ComplexImage[] analysisPyramid(ComplexImage image) {
		ComplexImage[] array=new ComplexImage[J+1];
		ComplexImage H1=FA[0].copyImage();  // Filters for odd iteration
		H1.multiply(0.5);
		ComplexImage H1Dl=FA[2].getSubimage(0,FA[2].nx/2-1,0,FA[2].ny-1);  // Filters for even iteration
		H1Dl.multiply(0.5);
		ComplexImage G1=FA[1].copyImage();
		ComplexImage G1D=FA[3].copyImage();
		ComplexImage Y2=null;    //highpass subband
		ComplexImage Y1=image.copyImage();  //remaining lowpass subband
		Y1.FFT2D(); 
		if (param.prefilter) {	
			Y1.multiply(P);	
		}
		int l=1;
		for(int j=1;j<=J;j++) {
			int mj=j%2;
			Y2=Y1.copyImage(); 
			if(mj==1) {      //odd iteration			
				Y2.multiply(G1,l);
				Y1.multiply(H1,l);
				Y1.quincunxDownUp();
				//Put Y2 in stack			
				Y2.iFFT2D();	
				array[j-1]=Y2;
			}
			else {           // even iteration
				// Y1=left half of Y1*left half of H1D 
			    Y2.multiply(G1D,l);
				Y2.iFFT2D();
				Y2=Y2.rotate(0);
				array[j-1]=Y2;
				Y1=Y1.getSubimage(0,Y1.nx/2-1,0,Y1.ny-1);
				Y1.multiply(H1Dl,l);
				Y1.dyadicDownY();
				l*=2;
			}
		}
		// insert lowpass subband		
		Y1.iFFT2D();
		if (J%2==1) {
			Y1=Y1.rotate(0.0);
		}
		array[J]=Y1;
		return array;	
	}
	
	
	/**
	* Returns the result of pyramid quincunx synthesis of array.
	* @param array subbands of quincunx pyramid transform 
	* @return the result of synthesis
	*/

	private ComplexImage synthesisPyramid(ComplexImage[] array) {			
		// Compute Gls
		ComplexImage Ge=FS[1].copyImage();
		Ge.multiply(FA[1]);
		ComplexImage Gemodsqr=Ge.copyImage();
		Gemodsqr.squareModulus();
		Gemodsqr.quincunxDownUp();		
		ComplexImage Gls=Ge;
		Gls.conj();
		Gls.divide(Gemodsqr,1.0,0.0);
		// Compute GlsD
		ComplexImage GeD=FS[3].copyImage();
		GeD.multiply(FA[3]);
		Gemodsqr=GeD.copyImage();
		Gemodsqr.squareModulus();
		Gemodsqr.downUpY();			
		ComplexImage GlsD=GeD.copyImage();
		GlsD.conj();
		GlsD.divide(Gemodsqr,1.0,0.0);
		// do synthesis
		// Filters for odd iteration
		ComplexImage H=FS[0];
		ComplexImage H1=FA[1];
		ComplexImage G=FS[1];
		// Filters for even iteration
		ComplexImage HD=FS[2];
		ComplexImage H1D=FA[3];
		ComplexImage GD=FS[3];	
		ComplexImage HP=new ComplexImage(array[1].nx,array[1].ny);
		ComplexImage LP=new ComplexImage(array[1].nx,array[0].ny);
		ComplexImage LP1=new ComplexImage(array[1].nx,array[1].ny);
		// Get lowpass	
		LP.copyImageContent(array[J]);	
		if (J%2==1) {	
		LP.unrotate(array[J-1].nx,array[J-1].ny);		
		}		
		LP.FFT2D();
		int l=1;		
		for(int i=0,J12=(J-1)/2;i<J12;i++,l*=2);
		int mj=J%2;		
		for(int j=J;j>0;j--) {	
			HP.copyImageContent(array[j-1]);			
			if (mj==0) {   // even iteration		
				HP.unrotate(array[j-2].nx,array[j-2].ny);
				HP.FFT2D();
				LP.dyadicUpsample();
				LP.multiply(HD,l);	
				LP1.copyImageContent(LP);
				LP1.multiply(H1D,l);		
				HP.subtract(LP1);
				HP.multiply(GlsD,l);		
				HP.downUpY();
				HP.multiplyCircular(GD,l);
				LP.add(HP);			
			}
			else {     // odd iteration
				HP.FFT2D();
				LP.multiplyCircular(H,l);
				LP1.copyImageContent(LP);
				LP1.multiply(H1,l);		
				HP.subtract(LP1);	
				HP.multiply(Gls,l);	
				HP.quincunxDownUp();
				HP.multiply(G,l);
				LP.add(HP);
				l/=2;
			}
			mj=1-mj;
		}	
		if (param.prefilter) {		
			LP.divide(P,1.0,0.0);
		}
		LP.iFFT2D();
		return LP;
	}

	
	/**
	* Prepares the quincunx pyramid transform coeffitients for being displayed.
	* Rescales the subbands for visualisation and puts all subbands in one ComplexImage.
	* @param array subbands of quincunx pyramid transform 
	* @param back background color
	* @param rescale if rescale=false there is no rescaling	
	* @param lp if lp=false the lowpass subband is not displayed
	* @return the image to display
	*/

	public ComplexImage displayPyramid(ComplexImage[] array, double back, boolean rescale, boolean lp) {		
		int nx=array[0].nx;
		int ny=array[0].ny;
		int s=(nx+ny)/2;
		int l;
		if (nx>ny) {
			l=(nx+ny)/2;
		}
		else {
			l=ny;
		}
		ComplexImage display=new ComplexImage(nx+s,2*l);
		display.settoConstant(back,back);
		int x=0;
		int y=0;
		for (int j=0;j<J;j++) {
			ComplexImage temp=array[j].copyImage();		
			if (rescale) {
				temp.stretch();	
			}
			if (j%2==1) {       //even iteration
				temp.unrotate(array[j-1].nx,array[j-1].ny);
				temp=temp.rotate(back);
			}
				
			temp.frame(back);	
			display.putSubimage(x,y,temp);
			if ((j==J-1)&&(J%2==0)) {
				y+=l;
				x=nx-array[J].nx/2;
			} else {
				if (x==nx) {
					y+=l;
					l/=2;
					x=nx-array[j+1].nx;
				}
				else {
					x=nx;
				}
			}
		}
		if (lp) {
			ComplexImage temp=array[J].copyImage();
			if (J%2==1) {       //even iteration
				temp.unrotate(array[J-1].nx,array[J-1].ny);
				temp=temp.rotate(back);
			}	
			temp.stretch();		
			temp.frame(back);
			display.putSubimage(x,y,temp);				
		}
		return display;
	}
	
	
	/**
	* Prepares a nonredundant quincunx transform for being displayed, stretches each subband.
	* @param image the basis transform coefficients
	* @return the image with rescaled subbands to display
	*/

	public ComplexImage displayBasis(ComplexImage image) {
		int dx=image.nx;
		int dy=image.ny;
		ComplexImage out=new ComplexImage(dx,dy);
		ComplexImage sub;
		for(int j=1;j<=J;j++) {
			if (j%2==1) {   // Odd iteration
				sub=image.getSubimage(dx/2,dx-1,0,dy-1);			
				sub.stretch();		
				out.putSubimage(dx/2,0,sub);
				dx/=2;	
			}
			else {
				sub=image.getSubimage(0,dx-1,dy/2,dy-1);				
				sub.stretch();			
				out.putSubimage(0,dy/2,sub);
				dy/=2;
			}
		}
		sub=image.getSubimage(0,dx-1,0,dy-1);						
		sub.stretch();							
		out.putSubimage(0,0,sub);
		return out;
	}
}