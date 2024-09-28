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


import java.util.*;
import ij.*;
import java.text.DecimalFormat;
import polyharmonicwavelets.ComplexImage;
import polyharmonicwavelets.Parameters;
import static polyharmonicwavelets.Constants.PI;
import static polyharmonicwavelets.Constants.PI2;
import static polyharmonicwavelets.Constants.SQRT2;

public class DyadicFilters extends Filters {
	
	/**
	* Constructor, creates a filters object and reserves the memory space for all the analysis and synthesis filters that will be needed depending on parameters.
	* @param param the wavelet transform parameters
	* @param nx the number of columns in the image to transform
	* @param ny the number of rows in the image to transform
	*/
	public DyadicFilters(Parameters param, int nx, int ny) {
		super(param, nx, ny);
		
		FA[0] = new ComplexImage(nx, ny, param.N==0);
		FA[1] = new ComplexImage(nx, ny, (!(param.redundancy==param.BASIS)&&(param.N==0)));
		
		if(param.redundancy==param.BASIS) {
			FA[2]=new ComplexImage(nx,ny);
			FA[3]=new ComplexImage(nx,ny);
		}
		if (!(param.analysesonly)) {
			FS=new ComplexImage[4];
			FS[0]=new ComplexImage(nx,ny,param.N==0);
			for (int i=1;i<4;i++) {
				FS[i]=new ComplexImage(nx,ny);
			}
			if (param.redundancy==param.PYRAMID) {
				FP=new ComplexImage[3];
				for (int i=0;i<3;i++) {
					FP[i]=new ComplexImage(nx,ny);
				}
			}
		}
	}
	
	/*
	* Computes the prefilter P
	*/
	public ComplexImage computePrefilter(double order) {
		ComplexImage P = multiplicator(nx,ny,-PI,-PI,PI,PI,order,0);
		ComplexImage d = denominator(nx,ny,-PI,-PI,PI,PI,0);
		P.divide(d, 1.0, 0.0);
		P.shift();
		//if (flavor==param.DUALOPERATOR) {
		//	P.divide(ac);
		//}
		return P;
	}

	/**
	* Calculates all filters needed to perform dyadic transform with given parameters.
	*/
	public void compute() {
		double k=1.0/Math.pow(2.0,param.order);
		ComplexImage HH=null;
		ComplexImage L1=null;

		/*
		if (param.accompute==param.ITERATIVE) {
			// Compute filter on a 2x finer grid for autocorrelation
			ComplexImage L=multiplicator(2*nx,2*ny,0.0,0.0,2.0*PI2,2.0*PI2,param.order,param.N);  // Numerator(2*omega) is complex
			L1=multiplicator(2*nx,2*ny,0.0,0.0,PI2,PI2,param.order,param.N);	 // Numerator(omega) is complex
			ComplexImage HHdouble=L;
			HHdouble.multiply(k);
			HHdouble.divide(L1,1.0,0.0);		
			// compute filter on a regular grid
			HH=HHdouble.copyImage();		
			HH.decimateCrop();      
			L1.decimateCrop();
			// compute autocorrelation if needed			
			if (!(param.analysesonly)) {
				HHdouble.squareModulus();
				Autocorrelation autocorrelation = new Autocorrelation(HHdouble.nx, HHdouble.ny, (int)param.order);
				ac = autocorrelation.computeIterative(false);
				//ac=Autocorrelation.autocorrIterative(HHdouble);		
			}
		} 
		else {
		*/
			ComplexImage L=multiplicator(nx,ny,0.0,0.0,2.0*PI2,2.0*PI2,param.order,param.N);  // Numerator(2*omega) is complex
			L1=multiplicator(nx,ny,0.0,0.0,PI2,PI2,param.order,param.N);	 // Numerator(omega) is complex	
			HH=L;
			HH.multiply(k);	
			HH.divide(L1,1.0,0.0);			
			if (!((param.analysesonly)&&(param.flavor==Parameters.MARR))) {	
				ComplexImage simpleloc=multiplicator(nx,ny,0.0,0.0,PI2,PI2,2*param.order,0);		
				Autocorrelation autocorrelation = new Autocorrelation(simpleloc.nx, simpleloc.ny, (int)param.order);
				ac = autocorrelation.computeGamma(false);
				//ac=Autocorrelation.autocorrGamma(simpleloc,param.order);
		}
		//}
		calculatePrefilter();
		if ((param.flavor==param.OPERATOR)||(param.flavor==param.DUALOPERATOR)||(param.flavor==param.MARR)) {		
			// Analysis filters
			FA[0].copyImageContent(HH);
			FA[0].multiply(2.0);			
			ComplexImage G=FA[1];
			G.copyImageContent(L1);
			if(!(param.flavor==param.MARR))	{
				G.divide(ac);
			}	
			G.multiply(2.0);  		 
			if (param.rieszfreq==1) {		
				ComplexImage V2=multiplicator(nx, ny, 0.0, 0.0, PI2, PI2,2.0, 0);		
				G.multiply(V2);	
			}				
			G.conj();
			ComplexImage R=null;
			if(param.redundancy==param.BASIS) {   // Basis is not for Marr
				FA[2].copyImageContent(G);
				FA[3].copyImageContent(G);			
			}
			FA[1]=G;
			// Compute synthesis filters
			if (!(param.analysesonly)) {
				// Synthesis lowpass
				ComplexImage L1conj=L1.copyImage();
				L1conj.conj();	
				ComplexImage H=FS[0];
				H.copyImageContent(HH);				
				H.conj();
				double k1=k*4.0;
				H.multiply(k1);				
				ComplexImage acd=ac.copyImage();				
				acd.decimate();
				ComplexImage Gs=ac.copyImage();
				Gs.divide(acd);
				Gs.multiply(0.25);
				H.multiply(Gs);		
				FS[0]=H;
				FS[0].multiply(2.0/k);
				// Synthesis highpass
				Gs.divide(L1conj,0.0,0.0);	
				ComplexImage D=HH.copyImage();	
				D.squareModulus();
				D.multiply(1.0/k);
				D.multiply(ac);
				D.multiply(k1);				
				ComplexImage D1=D.copyImage();
				ComplexImage D2=D.copyImage();
				ComplexImage D12=D.copyImage();									
				D1.shiftX();
				D2.shiftY();
				D12.shift();
				D1.multiply(Gs);
				D2.multiply(Gs);
				D12.multiply(Gs);
				FS[1]=D1.copyImage();
				FS[1].add(D12);				
				FS[2]=D2.copyImage();
				FS[2].add(D12);			
				FS[3]=D1;
				FS[3].add(D2);
			}		
			if (param.flavor==param.DUALOPERATOR) {
				ComplexImage[] Ftmp=FA;
				FA=FS;
				FS=Ftmp;
				FA[0].conj();
				FA[1].conj();
				FA[2].conj();
				FA[3].conj();
				FS[0].conj();
				FS[1].conj();
				FS[2].conj();
				FS[3].conj();
			}	
		}
		if(param.redundancy==param.BASIS) {   // Basis is not for Marr
			FA[1].modulateMinusX();
			FA[2].modulateMinusY();
			FA[3].modulateMinusQuincunx();		
		}
		if (!(param.analysesonly)) {
			FS[1].modulatePlusX();
			FS[2].modulatePlusY();
			FS[3].modulatePlusQuincunx();
		}
	
		if((param.redundancy==param.PYRAMID)&&(!(param.analysesonly))) {
			pyramidSynthesisFilters();
		}
	}	
	
	
	/*
	* Calculates numenator of scaling function, localization
	* support is [ minx : (maxx-minx)/sizex : maxx-(maxx-minx)/sizex, miny : (maxy-miny)/sizey : maxy-(maxy-miny)/sizey ]
	* output is of size [sizex, sizey] and defined on[minx...maxx-eps,miny...maxy-eps]
	*/

	private ComplexImage multiplicator(int sizex, int sizey, double minx, double miny, double maxx, double maxy,double gama, int N) {
		ComplexImage result=new ComplexImage(sizex,sizey,N==0);
		double gama2=gama/2.0;
		final double d83=8.0/3.0;
		final double d23=2.0/3.0;
		double epsx=(maxx-minx)/(4.0*(double)sizex);
		double epsy=(maxy-miny)/(4.0*(double)sizey);
		double rx=(maxx-minx)/(double)sizex;
		double ry=(maxy-miny)/(double)sizey;
		double[] sxarr=new double[sizex];
		double[] x1arr=new double[sizex];
		double x=minx;
		for (int kx=0; kx<sizex;kx++,x+=rx) {
			sxarr[kx]=Math.sin(x/2)*Math.sin(x/2);
			double x1=x;
			while (x1>=Math.PI-epsx) x1=x1-PI2;
			while (x1<-Math.PI-epsx) x1=x1+PI2;			
			x1arr[kx]=x1;
		}	
		double y=miny;
		for(int ky=0;ky<sizey;ky++,y+=ry) {
			int kxy=ky*sizex;
			double sy=Math.sin(y/2.0);
			sy=sy*sy;		
			double y1=y;	
			while (y1>=Math.PI-epsy) y1=y1-PI2;
			while (y1<-Math.PI-epsy) y1=y1+PI2;
			double y11=y1;			
			for (int kx=0,index=kxy;kx<sizex;kx++,index++) {
				y1=y11;
				double x1=x1arr[kx];			
				final double sx=sxarr[kx];
				double a=1.0;
				if (param.type==param.ISOTROPIC) {          // Isotropic 		
					a=4.0*(sx+sy)-d83*(sx*sy); 
				}
				if (param.type==param.CHANGESIGMA) {          
					final double sigma2=param.s2;
					final double b=-16.0/sigma2;
					final double c=24.0/(sigma2*sigma2)-16.0/(3.0*sigma2);
					final double d=8.0/(sigma2*sigma2)+32.0/45.0-16.0/(3.0*sigma2);
					final double e=4.0/3.0-8.0/sigma2;
					a= 4.0*(sx+sy)+b*(sx*sy)+c*(sx*sx*sy+sy*sy*sx)+d*(sx*sx*sx+sy*sy*sy)+e*(sx*sx+sy*sy);
				}	
				double re=Math.pow(a,gama2);
				double im=0.0;
				if (N>0) {
					boolean xpi=((x1<-Math.PI+epsx)&&(x1>-Math.PI-epsx));
					boolean ypi=((y1<-Math.PI+epsy)&&(y1>-Math.PI-epsy));
					boolean x0=((x1<epsx)&&(x1>-epsx));
					boolean y0=((y1<epsy)&&(y1>-epsy));		
					if (!(x0&&y0)) {
						double x1p=x1;
						double y1p=y1;
						if (xpi&&!y0&&!ypi) {
							x1p=0.0;
						}	
						if (ypi&&!x0&&!xpi) {
							y1p=0.0;
						}
						x1=x1p;
						y1=y1p;
					}		
					for (int i=0;i<N;i++) {
						double re1=re*x1-im*y1;
						double im1=re*y1+im*x1;
						re=re1;
						im=im1;
					}
					double t=Math.pow(x1*x1+y1*y1,(double)N/2.0);
					if (t==0.0) {
						result.real[index]=0.0;
						result.imag[index]=0.0;
					} else {
						result.real[index]=re/t;
						result.imag[index]=im/t;			
					}
				} else {
					result.real[index]=re;
				}
			}			
		}
		return result;
	}

	
	/*
	* Calculates denominator of scaling function
	* support is [ 0 : maxx/sizex : maxx-maxx/sizex, 0 : maxy/sizey : maxy-maxy/sizey ]
	* output is of size [sizex, sizey] and defined on[0...maxx-eps,0...maxy-eps]
	*/

	private ComplexImage denominator(int sizex, int sizey, double minx, double miny, double maxx, double maxy, int N) {
		ComplexImage result=new ComplexImage(sizex,sizey);
		double gamaN2;		
		gamaN2=(param.order-N)/2.0;		
		for(int ky=0;ky<sizey;ky++) {
			int kxy=ky*sizex;
			double y=miny+(double)ky*(maxy-miny)/(double)sizey;
			for (int kx=0,index=kxy;kx<sizex;kx++,index++) {
				double x=minx+(double)kx*(maxx-minx)/(double)sizex;
				double re=Math.pow(x*x+y*y,gamaN2);
				double im=0.0;	
				if (N>0) {	
					for (int i=0;i<N;i++) {
						double re1=re*x-im*y;
						double im1=re*y+im*x;
						re=re1;
						im=im1;
					}
					result.real[index]=re;
					result.imag[index]=im;					
				} else {
					result.real[index]=re;
				}
			}	
		}
		return result;
	}

	/*
	* Computes the prefilter P
	*/
	private void calculatePrefilter() {
		P = multiplicator(nx,ny,-PI,-PI,PI,PI,param.order,0);
		ComplexImage d = denominator(nx,ny,-PI,-PI,PI,PI,0);
		P.divide(d, 1.0, 0.0);
		P.shift();
		if (param.flavor==param.DUALOPERATOR) {
			P.divide(ac);
		}
	}
	
	/*
	* Calculate filters for pyramid synthesis
	*/
	
	private void pyramidSynthesisFilters() {
		FA[1].multiply(1/SQRT2);		
		ComplexImage[] Ge=FP;
		Ge[0].copyImageContent(FA[1]);
		Ge[1].copyImageContent(FA[1]);
		Ge[2].copyImageContent(FA[1]);			
		Ge[0].multiply(FS[1]);
		Ge[1].multiply(FS[2]);
		Ge[2].multiply(FS[3]);		
		Ge[0].multiply(0.5);
		Ge[1].multiply(0.5);
		Ge[2].multiply(0.5);		
		ComplexImage[] Geconj=Ge;		
		Geconj[0].conj();
		Geconj[1].conj();
		Geconj[2].conj();	
		int nx2=FA[1].nx/2;
		int ny2=FA[1].ny/2;	
		int nxy2=FA[1].nxy/2;	
		int[] d={0,nx2,nxy2,nx2+nxy2};
		double[] mr=new double[9];
		double[] mi=new double[9];
		for (int ky=0,km=0;ky<nxy2;ky+=FA[1].nx) {
			for (int kx=ky,end=ky+nx2;kx<end;kx++,km++) {
				for(int i=0;i<9;i++) {
					mr[i]=mi[i]=0.0;
				}
				double inr0=0.0; double inr1=0.0; double inr2=0.0;  double inr4=0.0; double inr5=0.0; double inr8=0.0;
				double inri=0.0; double ini1=0.0; double ini2=0.0;  double ini4=0.0; double ini5=0.0; double ini8=0.0;			
				for (int l=0;l<4;l++) {
					int k=kx+d[l];
					inr0+=Geconj[0].real[k]*Geconj[0].real[k]+Geconj[0].imag[k]*Geconj[0].imag[k];								
					inr4+=Geconj[1].real[k]*Geconj[1].real[k]+Geconj[1].imag[k]*Geconj[1].imag[k];
					inr8+=Geconj[2].real[k]*Geconj[2].real[k]+Geconj[2].imag[k]*Geconj[2].imag[k];			
					inr1+=Geconj[0].real[k]*Geconj[1].real[k]+Geconj[0].imag[k]*Geconj[1].imag[k];
					ini1+=(-Geconj[0].real[k]*Geconj[1].imag[k]+Geconj[0].imag[k]*Geconj[1].real[k]);					
					inr2+=Geconj[0].real[k]*Geconj[2].real[k]+Geconj[0].imag[k]*Geconj[2].imag[k];		
					ini2+=(-Geconj[0].real[k]*Geconj[2].imag[k]+Geconj[0].imag[k]*Geconj[2].real[k]);					
					inr5+=Geconj[1].real[k]*Geconj[2].real[k]+Geconj[1].imag[k]*Geconj[2].imag[k];		
					ini5+=(-Geconj[1].real[k]*Geconj[2].imag[k]+Geconj[1].imag[k]*Geconj[2].real[k]);
				}
				//invert m
				mr[0]=(inr4*inr8)-(inr5*inr5+ini5*ini5);
				mr[1]=(inr2*inr5+ini2*ini5)-(inr1*inr8);
				mi[1]=(-inr2*ini5+ini2*inr5)-(ini1*inr8);		
				mr[2]=(inr1*inr5-ini1*ini5)-(inr2*inr4);
				mi[2]=(inr1*ini5+ini1*inr5)-(ini2*inr4);
				double dr=mr[0]*inr0+mr[1]*inr1+mi[1]*ini1+mr[2]*inr2+mi[2]*ini2;
				mr[3]=((inr2*inr5+ini2*ini5)-(inr1*inr8))/dr;
				mi[3]=((inr2*ini5-ini2*inr5)+(ini1*inr8))/dr;		
				mr[4]=((inr0*inr8)-(inr2*inr2+ini2*ini2))/dr;
				mr[5]=((inr1*inr2+ini1*ini2)-(inr0*inr5))/dr;
				mi[5]=((inr1*ini2-ini1*inr2)-(inr0*ini5))/dr;			
				mr[6]=((inr1*inr5-ini1*ini5)-(inr2*inr4))/dr;
				mi[6]=((-inr1*ini5-ini1*inr5)+(ini2*inr4))/dr;			
				mr[7]=((inr2*inr1+ini2*ini1)-(inr0*inr5))/dr;	
				mi[7]=((inr2*ini1-ini2*inr1)+(inr0*ini5))/dr;			
				mr[8]=((inr0*inr4)-(inr1*inr1+ini1*ini1))/dr;
				mr[0]/=dr;	
				mr[1]/=dr;	
				mi[1]/=dr;
				mr[2]/=dr;	
				mi[2]/=dr;				
				//end invert m	
				for (int l=0;l<4;l++) {
					int k=kx+d[l];
					double[] ger=new double[3];
					double[] gei=new double[3];
					for(int i=0;i<3;i++) {
						ger[i]=Geconj[i].real[k];
						gei[i]=Geconj[i].imag[k];
					}
					for(int i=0;i<3;i++) {
						double gr=0.0;	
						double gi=0.0;		
						for (int j=0;j<3;j++) {
							gr+=ger[j]*mr[3*i+j]-gei[j]*mi[3*i+j];
							gi+=ger[j]*mi[3*i+j]+gei[j]*mr[3*i+j];
						}
						Geconj[i].real[k]=gr;
						Geconj[i].imag[k]=gi;
					}
				}
			}
		}
		FP=Geconj;
		FA[1].multiply(SQRT2);	
	}
	


}