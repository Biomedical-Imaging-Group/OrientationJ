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

/**
* This class computes the filters for the quincunx wavelet transform. 
* @author Katarina Balac, EPFL.
*/

public class QuincunxFilters extends Filters {
	
	/**
	* Constructor, creates a QuincunxFilters objectand computes all the filters.
	* @param param the wavelet transform parameters
	* @param nx the number of columns in the image to transform
	* @param ny the number of rows in the image to transform
	*/
	public QuincunxFilters(Parameters param, int nx, int ny) {
		super(param, nx, ny);
	}

	/**
	* Computes all filters needed for the quincunx transform.
	*/	
	public void compute() {
		ComplexImage ac=null;
		ComplexImage L=null;
		ComplexImage LD=localization(nx, ny, 0.0, 0.0, PI2, PI2,param.order, param.N, 1);	
		double c=Math.pow(0.5,param.order/2.0);
		/*
		if (param.accompute==param.ITERATIVE) {	
			int nx2=2*nx;
			int ny2=2*ny;		
			ComplexImage Ldouble=localization(2*nx, 2*ny, 0.0, 0.0, PI2, PI2,param.order, param.N,1);				
			L=Ldouble.copyImage();
			L.decimateCrop();
			ComplexImage H=localization(2*nx, 2*ny, 0.0, 0.0, 2.0*PI2, 2.0*PI2,param.order, param.N,0);
			H.multiply(c*c);
			H.divide(Ldouble,1.0,0.0);
			ac=H;
			ac.squareModulus();
			//
			Autocorrelation autocorrelation = new Autocorrelation(ac.nx, ac.ny, (int)param.order);
			ac = autocorrelation.computeIterative(false);				
			//ac=Autocorrelation.autocorrIterative(ac);
		} 
		else {
		*/
			L=localization(nx, ny, 0.0, 0.0, PI2, PI2,param.order, param.N, 0);	
			ComplexImage simpleloc=localization(nx,ny,0.0,0.0,PI2,PI2,2*param.order,0,0);
			Autocorrelation autocorrelation = new Autocorrelation(simpleloc.nx, simpleloc.ny, (int)param.order);
			ac = autocorrelation.computeIterative(false);				
			//ac=Autocorrelation.autocorrGamma(simpleloc,param.order);
		//}
		ComplexImage B=LD.copyImage();
		B.multiply(c);
		B.divide(L,Math.cos(0.25*PI*(double)param.N),-Math.sin(0.25*PI*(double)param.N) );
		// Interpolation to calculate acD
		ComplexImage acD=interpolateQuincunxReal(ac);
		ComplexImage loc=L;
		quincunxPrefilter(ac);
		ComplexImage ac0=ac.copyImage();
		ComplexImage loc0=loc.copyImage();
		computeLowpassHighpass(B,ac,acD,loc,true);
		B=loc0;
		B.decimate();
		B.multiply(c);
		B.divide(LD,Math.cos(0.25*PI*(double)param.N),-Math.sin(0.25*PI*(double)param.N) );
		loc=LD;
		ac=acD;
		acD=ac0;
		acD.decimate();		
		computeLowpassHighpass(B,ac,acD,loc,false);	
	}

	
	/**
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


	/**
	* Computes prefilter
	*/
	private void quincunxPrefilter(ComplexImage ac) {
		P=new ComplexImage(nx,ny,true);
		P.settoConstant(1.0);
		P=localization(nx,ny,-PI,-PI,PI,PI,param.order,0,0);
		ComplexImage d=denominator(nx,ny,-PI,-PI,PI,PI,0);
		P.divide(d,1.0,0.0);
		P.shift();
		if (param.flavor==param.ORTHOGONAL) {
			ComplexImage acsqrt=ac.copyImage();
			acsqrt.rootReal();		
			P.divide(acsqrt);		
		}
		if ((param.flavor==param.BSPLINE)||(param.flavor==param.DUALOPERATOR)) {
			P.divide(ac);	
		}	
	}

	/**
	* Computes all the filters for odd iteration
	* B-refinement filter for the quincunx lattice
	* ortho-orthonormalizing factor
	* ac - autocorrelation
	* acD - sampled autocorrelation 
	* it-number of iteration
	* if it=0 computes filters for odd iteration, if it=1 for even iteration
	* H - analysis highpass
	* H1 - synthesis highpass
	* L1 - analysis lowpass
	* L - synthesis lowpass
	* if even, even iteration, else, odd iteration
	*/

	private void computeLowpassHighpass(ComplexImage B,ComplexImage ac, ComplexImage acD, ComplexImage loc, boolean even) {		
		ComplexImage L=null;
		ComplexImage L1=null;
		ComplexImage H=null;
		ComplexImage H1=null;		
		ComplexImage ortho=acD.copyImage();
		ortho.divide(ac);
		final double sqrt2=Math.sqrt(2.0);
		B.multiply(sqrt2);
		if (param.flavor==param.ORTHOGONAL) {																				
			ComplexImage orthot=ortho;
			orthot.rootReal();
			L1=B;				
			L1.divide(orthot);
			H=L1.copyImage();
			if (even) {	
				H.shift();
			} else {
				H.shiftY();
			}
			if (!param.analysesonly) {			
				L=L1.copyImage();				
				H1=H.copyImage();
				H1.conj();	
			}
			L1.conj();
		}
		if (param.flavor==param.DUAL) {							
			L1=B.copyImage();		
			if (even) {		
				ac.shift();
				B.shift();					
			} else {
				ac.shiftY();
				B.shiftY();
			}
			H=B.copyImage();								
			H.conj();
			H.multiply(ac);			
			if (!param.analysesonly) {
				L=L1.copyImage();
				L.divide(ortho);
				L.conj();
				H1=B;
				H1.divide(acD);
			}
		}
		if (param.flavor==param.BSPLINE) {
			L1=B.copyImage();
			L1.divide(ortho);
			L1.conj();			
			if (!param.analysesonly) {
				L=B.copyImage();
				H=B.copyImage();									
				H.divide(acD);				
			} 
			if (even) {
				B.shift();
			} else {
				B.shiftY();
			}					
			H1=B;
			H1.conj();
			ac.shift();
			H1.multiply(ac);
		}		
		if (param.flavor==param.OPERATOR) {
			L1=B.copyImage();		
			ComplexImage ac0=ac.copyImage();
			ComplexImage loc0=loc;	
			loc0.multiply(sqrt2);	
			if (even) {
				ac.shift();			
				B.shift();						
			} else {
				ac.shiftY();			
				B.shiftY();
			}
			if (!param.analysesonly) {	
				L=L1.copyImage();
				L.divide(ortho);
				L.conj();
				H1=B;
				H1.squareModulus();			
				H1.multiply(ac);
				H1.multiply(ac0);
				H1.divide(loc0,0.0,0.0);		
				H1.divide(acD,0.0,0.0);			
				H1.conj();	
			}
			H=loc0;   	
			H.conj();		
			H.divide(ac0);										
		}		
		if (param.flavor==param.DUALOPERATOR) {	
			L1=B.copyImage();
			if (!param.analysesonly) {
				L=L1.copyImage();
				L.conj();
				H1=loc.copyImage();              
				H1.multiply(sqrt2);					
				H1.divide(ac);	
			}
			L1.divide(ortho);
			ComplexImage ac0=ac.copyImage();
			ComplexImage loc0=loc;
			if (even) {
				ac.shift();
				ortho.shift();
				B.shift();	
			} else {
				ac.shiftY();
				ortho.shiftY();
				B.shiftY();			
			}
			H=B;
			H.squareModulus();
			H.multiply(1.0/Math.sqrt(2.0));
			H.multiply(ac);
			H.multiply(ac0);
			H.divide(loc0,0.0,0.0);
			H.divide(acD,0.0,0.0);								
		}							
		if (param.flavor==param.MARR) {							
			loc.multiply(sqrt2);	
			L1=B.copyImage();   													
			ComplexImage ac0=ac.copyImage();
			ComplexImage loc0=loc;			
			if (even) {
				ac.shift();
				B.shift();	
			} else {
				ac.shiftY();
				B.shiftY();	
			}
			if (!param.analysesonly) {
				H1=B;
				H1.squareModulus();
				H1.multiply(ac);
				H1.multiply(ac0);
				H1.divide(loc0,0.0,0.0);
				H1.divide(acD);
				H1.conj();					
				L=L1.copyImage();
				L.divide(ortho);
				L.conj();		
			}		
			H=loc0;
			H.conj();																				
		}						
		if (even) {
			if (param.redundancy==param.BASIS) {
				H.modulateMinusY();			
				if (!param.analysesonly) {
					H1.modulatePlusY();
				}
			}
			if ((param.redundancy==param.PYRAMID)&&(!param.analysesonly)) {			
				H1.modulatePlusY();
			}
			FA[1]=H;			
			FS[1]=H1;		
			FA[0]=L1;			
			FS[0]=L;
		} 
		else {
			if (param.redundancy==param.BASIS) {
				H.modulateMinusQuincunx();			
				if (!param.analysesonly) {
					H1.modulatePlusQuincunx();
				}
			}
			if ((param.redundancy==param.PYRAMID)&&(!param.analysesonly)) {			
				H1.modulatePlusQuincunx();
			}
			FA[3]=H;			
			FS[3]=H1;		
			FA[2]=L1;			
			FS[2]=L;		
		}
	}

	/**
	* Performs linear interpolation on the real part of ComplexImage ac as ac[w]=ac[Dw], where D is the quincunx subsampling matrix 
	* and w is a two element column vector with w1 and w2 uniformly distributed from 0 to 2*PI, and ac[w1+2*k*PI,w2+2*n*PI]=ac[w1,w2] for all k and n integer. 
	*/	
	private ComplexImage interpolateQuincunxReal(ComplexImage ac) {	
		ComplexImage out=new ComplexImage(nx,ny,ac.imag==null);	
		int nx1=nx-1;
		int ny1=ny-1;
		for (int cy=0;cy<ny;cy++) {
			for (int cx=0;cx<nx;cx++) {
				// calculate (x+y)mod(2pi),(y-x)mod(2pi)	
				double x=((double)cx/(double)nx)*PI2;
				double y=((double)cy/(double)ny)*PI2;
				double sum=x+y;
				double dif=y-x;
				// Find closest integers to x,y
				double x1=sum*(double)nx/PI2;
				double y1=dif*(double)ny/PI2;
				double fx=Math.floor(x1);
				double fy=Math.floor(y1);		
				x1=x1-fx;
				y1=y1-fy;	
				int kx=(int)fx;
				int ky=(int)fy;			
				while (ky>ny1) ky-=ny;
				while (ky<0) ky+=ny;
				while (kx>nx1) kx-=nx;	
				while (kx<0) kx+=nx;	
				int ky1=ky+1;
				while (ky1>ny1) ky1-=ny;
				int kx1=kx+1;
				while (kx1>nx1) kx1-=nx;
				double a=ac.real[nx*ky1+kx1];
				double b=ac.real[nx*ky1+kx];		
				double c=ac.real[nx*ky+kx];		
				double d=ac.real[nx*ky+kx1];		
				double res=y1*(x1*a+(1.0-x1)*b)+(1.0-y1)*(x1*d+(1.0-x1)*c);									
				out.real[cy*nx+cx]=res;						
			}
		}	
		return out;
	}

	/**
	* Returns the numenator of  scaling function, localization
	* support is [ minx : (maxx-minx)/sizex : maxx-(maxx-minx)/sizex, miny : (maxy-miny)/sizey : maxy-(maxy-miny)/sizey ]
	* output is of size [sizex, sizey] and defined on[minx...maxx-eps,miny...maxy-eps]
	* type=0, dyadic
	* type=1, quincunx, computes V(D^tw)
	*/
	private ComplexImage localization(int sizex, int sizey, double minx, double miny, double maxx, double maxy,double gama, int N, int type) {	
		ComplexImage result=new ComplexImage(sizex,sizey,N==0);
		double gama2=gama/2.0;	
		double epsx=PI2/(5.0*(double)sizex);
		double epsy=PI2/(5.0*(double)sizey);
		final double d83=8.0/3.0;
		final double d23=2.0/3.0;
		for(int ky=0;ky<sizey;ky++) {
			int kxy=ky*sizex;
			double rx=(maxx-minx)/(double)sizex;
			double ry=(maxy-miny)/(double)sizey;
			for (int kx=0,index=kxy;kx<sizex;kx++,index++) {
				double y=miny+(double)ky*ry;
				double x=minx+(double)kx*rx;
				if(type==1) {   // quincunx
					double xt=x;
					double yt=y;
					x=xt+yt;
					y=yt-xt;
				}						
				double y1=y;
				while (y1>=Math.PI-epsy) y1=y1-PI2;
				while (y1<-Math.PI-epsy) y1=y1+PI2;
				double x1=x;
				while (x1>=Math.PI-epsx) x1=x1-PI2;
				while (x1<-Math.PI-epsx) x1=x1+PI2;
				double a=1.0;  
				// Compute modulus of localization depending on type
				double sx=Math.sin(x/2);
				sx=sx*sx;
				double sy=Math.sin(y/2);
				sy=sy*sy;
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
				if(N>0) {		
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

}
