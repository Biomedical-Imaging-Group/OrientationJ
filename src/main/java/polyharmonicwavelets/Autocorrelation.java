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

import ij.IJ;
/**
* This class computes the autocorrelation of the polyharmonic B-spline function. Two methods can be used, the Gamma function method [1] or the iterative algorithm [2]. <br>
* References: <br>
* [1] Yan Barbotin semmester project <br>
* [2] T. Blu, D. Van De Ville, M. Unser, ''Numerical methods for the computation of wavelet correlation sequences,'' SIAM Numerical Analysis. <br>
*
* @author Katarina Balac, EPFL.
*/

public class Autocorrelation {

	private int nx;
	private int ny;
	private int order;
	
	/** 
	* Constructor.
	*/
	public Autocorrelation(int nx, int ny, int order) {
		this.nx = nx;
		this.ny = ny;
		this.order = order;
	}
	
	/** 
	* Gamma scheme computation.
	*/
	public ComplexImage computeGamma(boolean showLocalisation) {
		double PI2 = Math.PI*2;
		int N = 0;
		ComplexImage localisationFunc = multiplicator(nx, ny, 0.0, 0.0, PI2, PI2, 2*order, N);
		if (showLocalisation)
			localisationFunc.showReal("LocalisationFunc Gamma");
		return autocorrGamma(localisationFunc, order);
	}

	/** 
	* Iterative scheme computation.
	*/
	public ComplexImage computeIterative(boolean showLocalisation) {
		double PI2 = Math.PI*2;
		int N = 0;
		ComplexImage HH  = multiplicator(2*nx, 2*ny, 0.0, 0.0, 2.0*PI2, 2.0*PI2, order, N);  // Numerator(2*omega) is complex
		ComplexImage L1 = multiplicator(2*nx, 2*ny, 0.0, 0.0, PI2, PI2, order, N);	 // Numerator(omega) is complex
		if (showLocalisation)
			L1.showReal("LocalisationFunc Iterative");
		double k=1.0/Math.pow(2.0,order);
		HH.multiply(k);
		HH.divide(L1,1.0,0.0);				
		HH.squareModulus();
		return autocorrIterative(HH);	
	}
	
	/**
	* Returns the Fourier domain autocorrelation of any scaling function given the squared modulus of the refinement filter. <br>
	* Reference: T. Blu, D. Van De Ville, M. Unser, ''Numerical methods for the computation of wavelet correlation sequences,'' SIAM Numerical Analysis.
	* @param HH the squared modulus of the refinement filter.
	* @return the autocorreltion of scaling function. 
	*/
	private  ComplexImage autocorrIterative(ComplexImage HH) {	
		int nx=HH.nx;
		int ny=HH.ny;
		int lx=nx/2;
		int ly=ny/2;
		int lyx=ly*lx;
		ComplexImage A0=new ComplexImage(lx,ly,true);
		ComplexImage Af=new ComplexImage(lx,ly);
		ComplexImage Afe=new ComplexImage(lx+1,ly+1);
		ComplexImage Ad=new ComplexImage(lx,ly,true);
		ComplexImage Aq=new ComplexImage(lx,ly,true);
		ComplexImage A1=new ComplexImage(lx,ly,true);
		ComplexImage At=new ComplexImage(nx,ny,true);   // Does not change size
		ComplexImage Ai=new ComplexImage(nx,ny); 
		for(int i=0;i<lx*ly;i++) {
			A0.real[i]=1.0;
		}
		final double crit=0.00000001;       //stop criterion
		double improvement;	
		int lx2=lx/2;
		int lx231=3*lx2-1;
		int ly2=ly/2;
		int ly231=3*ly2-1;
		int ly32=3*ly/2;
		int k1=nx*3*ly/2+lx/2;            
		int k3=nx*ly/2+3*lx/2;
		int k2=k3-1;
		int k4=nx*(ly32-1)+lx/2;
		int count=0;
		int maxit=100;
		do {
			count++;
			Af.copyImageContent(A0); 
			Af.iFFT2D();
			Af.shift();
			for(int x=0;x<lx;x++) {
				Af.real[x]/=2.0;
				Af.imag[x]/=2.0;
			}   
			for(int y=0;y<lyx;y+=lx) {
				Af.real[y]/=2.0;
				Af.imag[y]/=2.0;	
			}  
			Afe.extend(Af);		
			Ai.putZeros();
			Ai.putSubimage(lx2,ly2,Afe);	
			Ai.shift();
			Ai.FFT2D();   //Ai is real
			//recursion
			A1.putZeros();
			At.copyImageContent(HH);
			At.multiply(Ai);
			Aq.getSubimageContent(0,Aq.nx-1,0,Aq.ny-1,At);		
			A1.add(Aq);	
			Aq.getSubimageContent(0,Aq.nx-1,ly,ly+Aq.ny-1,At);		
			A1.add(Aq);	
			Aq.getSubimageContent(lx,lx+Aq.nx-1,0,Aq.ny-1,At);
			A1.add(Aq);
			Aq.getSubimageContent(lx,lx+Aq.nx-1,ly,ly+Aq.ny-1,At);	
			A1.add(Aq);
			Ad.copyImageContent(A1);
			Ad.subtract(A0);
			improvement=Ad.meanModulus();
			A0=A1.copyImage();				
		} while ((improvement>crit)&&(count<maxit));
		System.out.println("The autocoorelation has been computed in "+count+" iterations." );
		if(count==maxit) {
			System.out.println("The autocorrelation does not converge!");		
		}
		return A0;
	} 


	/** 
	* Returns the autocorrelation of the polyharmonic B-spline function given the squared modulus of its localisation. <br>
	* Reference: Yan Barbotin semmester project
	* @param loc the polyharmonic B-spline localisation
	* @param order the order of polyharmonic B-spline
	* @return the polyharmonic B-spline autocorrelation
	*/	
	
	private ComplexImage autocorrGamma(ComplexImage loc, double order) {

		final double PI=Math.PI;
		final double PI2=2.0*PI;
		double[][] d = {{0.0,1.0},{0.0,2.0},{1.0,0.0},{1.0,1.0},{1.0,2.0},{2.0,0.0},{2.0,1.0},
						{-1.0,0.0},{-1.0,1.0},{-1.0,2.0},{-2.0,0.0},{-2.0,1.0},
						{0.0,-1.0},{0.0,-2.0}, {1.0,-1.0},{1.0,-2.0},{2.0,-1.0},
						{-1.0,-1.0},{-1.0,-2.0},{-2.0,-1.0},{0.0,0.0}};        // 21 pair
		int nx=loc.nx;
		int ny=loc.ny;
		int nxy=nx*ny;
		ComplexImage ac=new ComplexImage(nx,ny,true);
		GammaFunction gm=new GammaFunction();
		double gammanorm=Math.exp(gm.lnGamma(order));
		for(int kx=0,nx2=nx/2; kx<=nx2; kx++) {
			for(int ky=0,ny2=ny/2; ky<=ny2; ky++) {
				int kynx=ky*nx;
				if(ac.real[kynx+kx]==0.0) {
					int kxny=kx*ny;
					double x=(double)kx/(double)nx;
					double y=(double)ky/(double)ny;
					double res=1.0/(order-1.0);
					for(int i=0;i<21;i++) {
						double sqn=PI*( (x-d[i][0])*(x-d[i][0])+(y-d[i][1])*(y-d[i][1]));
						res+=gm.incompleteGammaQ(order,sqn)*gammanorm/Math.pow(sqn,order);
						sqn=PI*(d[i][0]*d[i][0]+d[i][1]*d[i][1]);
						if (sqn>0.0) {
							res+=incompleteGammaGeneral(sqn,1.0-order)*Math.cos(PI2*(d[i][0]*x+d[i][1]*y))/Math.pow(sqn,1.0-order);	
						}
					}
					ac.real[kynx+kx]=res;
					if (kx>0) {
						ac.real[kynx+nx-kx]=res;
					}
					if (ky>0) {
						ac.real[nxy-kynx+kx]=res;
					}
					if ((kx>0)&&(ky>0)) {
						ac.real[nxy-kynx+nx-kx]=res;
					}					
					if((((kynx/ny)*ny)==kynx)&&(((kxny/nx)*nx)==kxny)) {
						int kx1=ky*nx/ny;
						int ky1=kx*ny/nx;
						kynx=ky1*nx;
						kxny=kx1*ny;											
						ac.real[kynx+kx1]=res;
						if (kx1>0) {
							ac.real[kynx+nx-kx1]=res;
						}
						if (ky1>0) {
							ac.real[nxy-kynx+kx1]=res;
						}
						if ((kx1>0)&&(ky1>0)) {
							ac.real[nxy-kynx+nx-kx1]=res;
						}
					}
				}
			}		
		}
		ac.multiply(Math.pow(PI,order)/(gammanorm*Math.pow(PI2,2.0*order)) );	
		ac.multiply(loc);
		ac.real[0]=1.0;
		return ac;	
	}
	
	
	
	/*
	* Solves the incomplete gamma function even for negative a, regularised integral from x to infinity. Not normalised.
	* x has to be positive.
	* Reference: Yan Barbotin semmester project
	*/

	private static double incompleteGammaGeneral(double x, double a) {	
		double res=0;
		GammaFunction gm=new GammaFunction();
		if (a<0) {
			double a0=a;
			int iter=0;
			while(a<0) {
				a+=1.0;
				iter++;
			}
			if (a==0.0) {
				res=expInt(x);
			} else {	
				res=gm.incompleteGammaQ(a,x)*Math.exp(gm.lnGamma(a));
			}
			res*=Math.exp(x-a*Math.log(x));
			for(int k=1;k<=iter;k++) {	
				res=(x*res-1.0)/(a-(double)k);
			}
			res*=Math.exp(a0*Math.log(x)-x);	
		} else {
			if (a==0.0) {
				res=expInt(x);
			} else {	
				res=gm.incompleteGammaQ(a,x)*Math.exp(gm.lnGamma(a));
			}
		}	
		return res;
	}
		
	
	/*
	* Computes the exponential integral for a real positive argument x.
	* Copied from Matlab.
	*/
	
	private static double expInt(double x) {
		double[] p = {-3.602693626336023e-09, -4.819538452140960e-07, -2.569498322115933e-05,
     				  -6.973790859534190e-04, -1.019573529845792e-02, -7.811863559248197e-02,
     				  -3.012432892762715e-01, -7.773807325735529e-01,  8.267661952366478e+00};
		double d=0.0;
		double y=0.0;
		for (int j=0;j<9;j++) {
			d*=x;
			d+=p[j];
		}
		if (d>0.0) {
			double egamma=0.57721566490153286061;
			y=-egamma-Math.log(x);
			double term=x;
			double pterm=x;
			double eps=0.00000000000000000000000000001;
			for(double j=2.0;Math.abs(term)>eps;j+=1.0) {
				y+=term;
				pterm=-x*pterm/j;
				term=pterm/j;
			}
		} else {
			double n=1.0;
			double am2=0.0;
			double bm2=1.0;
			double am1=1.0;
			double bm1=x;
			double f=am1/bm1;
			double oldf=f+100.0;
			double j=2.0;
			double eps=0.000000000000000000000000001;
			while(Math.abs(f-oldf)>eps) {
				double alpha=n-1.0+j/2.0;
				double a=am1+alpha*am2;
       			double b=bm1+alpha*bm2;
				am2=am1/b;
       			bm2=bm1/b;
		        am1=a/b;
		        bm1=1.0;
		        oldf=f;
		        f=am1;
		        j+=1.0;
				alpha=(j-1.0)/2.0;
		        double beta=x;
		        a=beta*am1+alpha*am2;
		        b=beta*bm1+alpha*bm2;
		        am2=am1/b;
		        bm2=bm1/b;
		        am1=a/b;
		        bm1=1.0;
		        oldf=f;
		        f=am1;
		        j+=1.0;
			}
			y=Math.exp(-x)*f;
		}		
		return y;
	}

	/*
	* Calculates numerator of scaling function (localization function)
	* support is [ minx : (maxx-minx)/sizex : maxx-(maxx-minx)/sizex, miny : (maxy-miny)/sizey : maxy-(maxy-miny)/sizey ]
	* output is of size [sizex, sizey] and defined on[minx...maxx-eps,miny...maxy-eps]
	*/

	public ComplexImage multiplicator(int sizex, int sizey, double minx, double miny, double maxx, double maxy,double gama, int N) {
		double PI2 = 2*Math.PI;
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
				a=4.0*(sx+sy)-d83*(sx*sy); 
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

	public ComplexImage denominator(int sizex, int sizey, double minx, double miny, double maxx, double maxy, int order, int N) {
		ComplexImage result=new ComplexImage(sizex,sizey);
		double gamaN2;		
		gamaN2=(order-N)/2.0;		
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



}
		