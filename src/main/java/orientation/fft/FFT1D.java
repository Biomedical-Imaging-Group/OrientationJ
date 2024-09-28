package orientation.fft;

public class FFT1D {

	private boolean radix2 = true;
	private double Rearg[];
	private double Imarg[];
	private double[] yReOut;
	private double[] yImOut;
	 
	public FFT1D(int size) {
	
		int m = 1;
		int size1 = size;
		double fact;
		double arg;
		while ( size1 > 2) {
			size1 /= 2;
			m++;
		}
	
		if ((int)Math.round(Math.pow(2,m)) == size) {
			radix2=true;
			n=1<<m;
			fact= 2.0*Math.PI/(double)n;
			Imarg = new double[n];
			Rearg = new double[n];
			// compute W coefficients
			for (int i=0;i<n;i++) {
				arg = fact*(double)i;
				Rearg[i] = Math.cos(arg);
				Imarg[i] = -Math.sin(arg);
			}	
		}
		else {
			radix2=false;
			maxPrimeFactor = /*65537; */ (int)((double)(size+1));
			maxPrimeFactorDiv2 =(maxPrimeFactor+1)/2;// (int)((double)(maxPrimeFactor+1)/2);

			twiddleRe = new double[maxPrimeFactor];
			twiddleIm = new double[maxPrimeFactor];
			trigRe = new double[maxPrimeFactor];
			trigIm = new double[maxPrimeFactor];
			zRe = new double[maxPrimeFactor];
			zIm = new double[maxPrimeFactor];
			vRe = new double[maxPrimeFactorDiv2];
			vIm = new double[maxPrimeFactorDiv2];
			wRe = new double[maxPrimeFactorDiv2];
			wIm = new double[maxPrimeFactorDiv2];
			yReOut = new double[size];
			yImOut = new double[size];
			//Math.pi = 4*Math.atan(1);    
			n = size;
			sofarRadix = new int[maxFactorCount]; 
			actualRadix = new int[maxFactorCount];
			remainRadix = new int[maxFactorCount];
			transTableSetup(sofarRadix, actualRadix, remainRadix);
		}

	}

	/**
	* Select the algorithm to perform the FFT1D, Cooley-Tukey or Mix.
	*
	* @param	Re		real part of the input signal
	* @param	Im		imaginary part of the input signal
	* @param	size    length of the FFT
	* @param	shift	set the start of the FFT
	*/
	public void transform(double Re[], double Im[], int size, int shift) {
		n = size;
		if (radix2) {
			doFFT1D_CooleyTukey(Re,Im,size,shift);
		}
		else {
			if (shift == 0) 
				doFFT_Mix(Re, Im, size);
			else 
				doFFT_Mix(Re, Im, size, shift);
		}
	}

	/**
	* Select the algorithm to perform the Inverse FFT1D, Cooley-Tukey or Mix.
	*
	* @param	Re		real part of the input signal
	* @param	Im		imaginary part of the input signal
	* @param	size    length of the IFFT
	* @param	shift	set the start of the IFFT
	*/
	public void inverse(double Re[], double Im[], int size, int shift) {
		n = size;
		if (radix2) {
			doIFFT1D_CooleyTukey(Re,Im,size,shift);
		}
		else {
			if (shift == 0) 
				doIFFT_Mix(Re,Im,size);
			else 
				doIFFT_Mix(Re,Im,size,shift);
		}
	}

	/**
	* Perform the FFT1D.
	*
	* There are two algorithms, the first for power of two length is a Cooley-Tukey 
	* algorithm, the second for all size has been downloaded from the Web (Mixfft.java).
	* These are used to transform rows or columns in the wavelet transform.
	*
	* @param	Re		real part of the input signal
	* @param	Im		imaginary part of the input signal
	* @param	size    length of the FFT
	* @param	shift	set the start of the FFT
	*/
	private void doFFT1D_CooleyTukey(double Re[], double Im[], int size, int shift) {
		
		int m = 1;
		int size1 = size;
		while ( size1 > 2) {
			size1 /= 2;
			m++;
		}

		double	Retmp, Imtmp;
		int i, j, k, stepsize, shifter;
		int i_j, i_j_s;
			
		// bit inversion	
		for (i=j=shift;i<shift+n-1;i++) {
			if (i<j) {
				Retmp=Re[i]; Imtmp=Im[i];
				Re[i]=Re[j]; Im[i]=Im[j];
				Re[j]=Retmp; Im[j]=Imtmp;
			}
			k=n>>1;
			while (k+shift<=j) {
				j-=k;
				k/=2;
			}
			j+=k;
		}

		// Perform the FFT
		for(stepsize=1,shifter=m-1;stepsize<n;stepsize<<=1,--shifter) {
			for(j=shift;j<shift+n;j+=stepsize<<1) {
				for (i=0;i<stepsize;i++) {
					i_j=i+j;
					i_j_s=i_j+stepsize;
					if(i>0) {
						Retmp = Rearg[i<<shifter]*Re[i_j_s]-Imarg[i<<shifter]*Im[i_j_s];
						Im[i_j_s] = Rearg[i<<shifter]*Im[i_j_s]+Imarg[i<<shifter]*Re[i_j_s];
						Re[i_j_s] = Retmp;
					}
					Retmp = Re[i_j] - Re[i_j_s];
					Imtmp = Im[i_j] - Im[i_j_s];
					Re[i_j] += Re[i_j_s];
					Im[i_j] += Im[i_j_s];
					Re[i_j_s] = Retmp;
					Im[i_j_s] = Imtmp;
				}
		
			}
		}
	}

	/**
	* Perform the IFFT1D.
	*
	* Same algorithms as these used for the FFT.
	*
	* @param	Re		real part of the input signal
	* @param	Im		imaginary part of the input signal
	* @param	size    length of the IFFT
	* @param	shift	set the start of the IFFT
	*/
	private void doIFFT1D_CooleyTukey(double Re[], double Im[], final int size, int shift) {
		for (int i=shift;i<shift+size;i++) {
			Im[i] = -Im[i];
		}

		transform(Re,Im,size,shift);

		for (int i=shift;i<shift+size;i++) {
			Re[i] = Re[i]/size;	/*Output is in Re*/
			Im[i] = -Im[i]/size;
		}
	}

	/*
	  fft(int n, double xRe[], double xIm[], double yRe[], double yIm[])
	 ------------------------------------------------------------------------
	  NOTE : This is copyrighted material, Not public domain. See below.
	 ------------------------------------------------------------------------
	  Input/output:
		  int n          transformation length.
		  double xRe[]   real part of input sequence.
		  double xIm[]   imaginary part of input sequence.
		  double yRe[]   real part of output sequence.
		  double yIm[]   imaginary part of output sequence.
	 ------------------------------------------------------------------------
	  Function:
		  The procedure performs a fast discrete Fourier transform (FFT) of
		  a complex sequence, x, of an arbitrary length, n. The output, y,
		  is also a complex sequence of length n.

		  y[k] = sum(x[m]*exp(-i*2*Math.pi*k*m/n), m=0..(n-1)), k=0,...,(n-1)

		  The largest prime factor of n must be less than or equal to the
		  constant maxPrimeFactor defined below.
	 ------------------------------------------------------------------------
	  Author:
		  Jens Joergen Nielsen            For non-commercial use only.
		  Bakkehusene 54                  A $100 fee must be paid if used
		  DK-2970 Hoersholm               commercially. Please contact.
		  DENMARK

		  E-mail : jjn@get2net.dk   All rights reserved. October 2000.
		  Homepage : http://home.get2net.dk/jjn
	 ------------------------------------------------------------------------
	  Implementation notes:
		  The general idea is to factor the length of the DFT, n, into
		  factors that are efficiently handled by the routines.

		  A number of short DFT's are implemented with a minimum of
		  arithmetical operations and using (almost) straight line code
		  resulting in very fast execution when the factors of n belong
		  to this set. Especially radix-10 is optimized.

		  Prime factors, that are not in the set of short DFT's are handled
		  with direct evaluation of the DFP expression.

		  Please report any problems to the author. 
		  Suggestions and improvements are welcomed.
	 ------------------------------------------------------------------------
	  Benchmarks:                   
		  The Microsoft Visual C++ comMath.piler was used with the following 
		  comMath.pile options:
		  /nologo /Gs /G2 /W4 /AH /Ox /D "NDEBUG" /D "_DOS" /FR
		  and the FFTBENCH test executed on a 50MHz 486DX :
		  
		  Length  Time [s]  Accuracy [dB]

			 128   0.0054     -314.8   
			 256   0.0116     -309.8   
			 512   0.0251     -290.8   
			1024   0.0567     -313.6   
			2048   0.1203     -306.4   
			4096   0.2600     -291.8   
			8192   0.5800     -305.1   
			 100   0.0040     -278.5   
			 200   0.0099     -280.3   
			 500   0.0256     -278.5   
			1000   0.0540     -278.5   
			2000   0.1294     -280.6   
			5000   0.3300     -278.4   
		   10000   0.7133     -278.5   
	 ------------------------------------------------------------------------
	  The following procedures are used :
		  factorize       :  factor the transformation length.
		  transTableSetup :  setup table with sofar-, actual-, and remainRadix.
		  permute         :  permutation allows in-place calculations.
		  twiddleTransf   :  twiddle multiplications and DFT's for one stage.
		  initTrig        :  initialise sine/cosine table.
		  fft_4           :  length 4 DFT, a la Nussbaumer.
		  fft_5           :  length 5 DFT, a la Nussbaumer.
		  fft_10          :  length 10 DFT using prime factor FFT.
		  fft_odd         :  length n DFT, n odd.
	*************************************************************************/

	private int maxPrimeFactor ;
	private int maxPrimeFactorDiv2 ;
	private int maxFactorCount = 20;

	private int n, nFactor;

	private final double  c3_1 = -1.5000000000000E+00;  //  c3_1 = cos(2*Math.pi/3)-1; 
	private final double  c3_2 =  8.6602540378444E-01;  //  c3_2 = sin(2*Math.pi/3);
											  
	//private final double  u5   =  1.2566370614359E+00;  //  u5   = 2*Math.pi/5;
	private final double  c5_1 = -1.2500000000000E+00;  //  c5_1 = (cos(u5)+cos(2*u5))/2-1;
	private final double  c5_2 =  5.5901699437495E-01;  //  c5_2 = (cos(u5)-cos(2*u5))/2;
	private final double  c5_3 = -9.5105651629515E-01;  //  c5_3 = -sin(u5);
	private final double  c5_4 = -1.5388417685876E+00;  //  c5_4 = -(sin(u5)+sin(2*u5));
	private final double  c5_5 =  3.6327126400268E-01;  //  c5_5 = (sin(u5)-sin(2*u5));
	private final double  c8   =  7.0710678118655E-01;  //  c8 = 1/sqrt(2);

	//private double   Math.pi;
	private int      groupOffset, dataOffset, adr;
	private int      groupNo,dataNo,blockNo,twNo;
	private double   omega, tw_re,tw_im;
	private double[] twiddleRe ;
	private double[] twiddleIm;
	private double[] trigRe ;
	private double[] trigIm ;
	private double[] zRe ;
	private double[] zIm ;
	private double[] vRe ;
	private double[] vIm ;
	private double[] wRe ;
	private double[] wIm ;
	private int[] sofarRadix ; 
	private int[] actualRadix ;
	private int[] remainRadix ;
	
	/**
	*
	*/
	private void factorize(int fact[],int num)	{
		int i,j,k;
		int nRadix;
		int[] radices = new int[7];
		int[] factors = new int[maxFactorCount];

		nRadix    =  6;  
		radices[1]=  2;
		radices[2]=  3;
		radices[3]=  4;
		radices[4]=  5;
		radices[5]=  8;
		radices[6]= 10;

		if (num==1) {
			j=1;
			factors[1]=1;
		}
		else 
			j=0;
		i=nRadix;
		while ((num>1) && (i>0)) {
			if ((num % radices[i]) == 0) {
				num=num / radices[i];
				j=j+1;
				factors[j]=radices[i];
			}
			else  i=i-1;
		}
		if (factors[j] == 2)   /*substitute factors 2*8 with 4*4 */
		{   
			i = j-1;
			while ((i>0) && (factors[i] != 8)) i--;
			if (i>0) {
				factors[j] = 4;
				factors[i] = 4;
		  }
		}
		if (num>1) {
			for (k=2; k<Math.sqrt(num)+1; k++)
				while ((num % k) == 0) {
					num=num / k;
					j=j+1;
					factors[j]=k;
				}
			if (num>1) {
				j=j+1;
				factors[j]=num;
			}
		}               
		for (i=1; i<=j; i++) {
		  fact[i] = factors[j-i+1];  
		}
		nFactor=j;
	}

	/**
	* After N is factored the parameters that control the stages are generated.
	*
	* @param sofar		the product of the radices so far.
	* @param actual  : the radix handled in this stage.
	* @param remain  : the product of the remaining radices.
	*/
	private void transTableSetup(int sofar[], int actual[], int remain[]) {
		int i;

		factorize(actual,n);
	   
		if (actual[1] > maxPrimeFactor)
		{
			System.out.println("\nPrime factor of FFT length too large : %6d"+actual[1]);
			System.out.println("\nPlease modify the value of maxPrimeFactor in mixfft.c");
		}
	   
		remain[0]=n;
		sofar[1]=1;
		remain[1]=n / actual[1];
		for (i=2; i<=nFactor; i++) {
			sofar[i]=sofar[i-1]*actual[i-1];
			remain[i]=remain[i-1] / actual[i];
		}
	}

	/**
	* The sequence y is the permuted input sequence x so that the following
	* transformations can be performed in-place, and the final result is the
	* normal degree.
	*/

	private void permute(int fact[], int remain[], double xRe[], double xIm[], double yRe[], double yIm[]) {
		int i,j=0,k;
		int[] count = new int[maxFactorCount]; 

		for (i=1; i<=nFactor; i++) 
			count[i]=0;
		k=0;
		
		for (i=0; i<=n-2; i++) {
			yRe[i] = xRe[k];
			yIm[i] = xIm[k];
			j=1;
			k=k+remain[j];
			count[1] = count[1]+1;
			while (count[j] >= fact[j])
			{
				count[j]=0;
				k=k-remain[j-1]+remain[j+1];
				j=j+1;
				count[j]=count[j]+1;
			}
		}
		yRe[n-1]=xRe[n-1];
		yIm[n-1]=xIm[n-1];
	}

	/*
	* Twiddle factor multiplications and transformations are performed on a
	* group of data. The number of multiplications with 1 are reduced by skipMath.ping
	* the twiddle multiplication of the first stage and of the first group of the
	* following stages.
	*/
	final private void initTrig(final int radix) {
		int i;
		double w, xre, xim;

		w=2*Math.PI/radix;
		trigRe[0]=1; 
		trigIm[0]=0;
		xre=Math.cos(w); 
		xim=-Math.sin(w);
		trigRe[1]=xre; 
		trigIm[1]=xim;
		for (i=2; i<radix; i++) {
			trigRe[i]=xre*trigRe[i-1] - xim*trigIm[i-1];
			trigIm[i]=xim*trigRe[i-1] + xre*trigIm[i-1];
		}
	}

	/**
	*
	*/
	private void fft_4(double aRe[], double aIm[]) {
		double  t1_re,t1_im, t2_re,t2_im;
		double  m2_re,m2_im, m3_re,m3_im;

		t1_re=aRe[0] + aRe[2]; t1_im=aIm[0] + aIm[2];
		t2_re=aRe[1] + aRe[3]; t2_im=aIm[1] + aIm[3];

		m2_re=aRe[0] - aRe[2]; m2_im=aIm[0] - aIm[2];
		m3_re=aIm[1] - aIm[3]; m3_im=aRe[3] - aRe[1];

		aRe[0]=t1_re + t2_re; aIm[0]=t1_im + t2_im;
		aRe[2]=t1_re - t2_re; aIm[2]=t1_im - t2_im;
		aRe[1]=m2_re + m3_re; aIm[1]=m2_im + m3_im;
		aRe[3]=m2_re - m3_re; aIm[3]=m2_im - m3_im;
	} 

	/**
	*
	*/
	private void fft_5(double aRe[], double aIm[]) {    
		double  t1_re,t1_im, t2_re,t2_im, t3_re,t3_im;
		double  t4_re,t4_im, t5_re,t5_im;
		double  m2_re,m2_im, m3_re,m3_im, m4_re,m4_im;
		double  m1_re,m1_im, m5_re,m5_im;
		double  s1_re,s1_im, s2_re,s2_im, s3_re,s3_im;
		double  s4_re,s4_im, s5_re,s5_im;

		t1_re=aRe[1] + aRe[4]; t1_im=aIm[1] + aIm[4];
		t2_re=aRe[2] + aRe[3]; t2_im=aIm[2] + aIm[3];
		t3_re=aRe[1] - aRe[4]; t3_im=aIm[1] - aIm[4];
		t4_re=aRe[3] - aRe[2]; t4_im=aIm[3] - aIm[2];
		t5_re=t1_re + t2_re; t5_im=t1_im + t2_im;
		aRe[0]=aRe[0] + t5_re; aIm[0]=aIm[0] + t5_im;
		m1_re=c5_1*t5_re; m1_im=c5_1*t5_im;
		m2_re=c5_2*(t1_re - t2_re); m2_im=c5_2*(t1_im - t2_im);

		m3_re=-c5_3*(t3_im + t4_im); m3_im=c5_3*(t3_re + t4_re);
		m4_re=-c5_4*t4_im; m4_im=c5_4*t4_re;
		m5_re=-c5_5*t3_im; m5_im=c5_5*t3_re;

		s3_re=m3_re - m4_re; s3_im=m3_im - m4_im;
		s5_re=m3_re + m5_re; s5_im=m3_im + m5_im;
		s1_re=aRe[0] + m1_re; s1_im=aIm[0] + m1_im;
		s2_re=s1_re + m2_re; s2_im=s1_im + m2_im;
		s4_re=s1_re - m2_re; s4_im=s1_im - m2_im;

		aRe[1]=s2_re + s3_re; aIm[1]=s2_im + s3_im;
		aRe[2]=s4_re + s5_re; aIm[2]=s4_im + s5_im;
		aRe[3]=s4_re - s5_re; aIm[3]=s4_im - s5_im;
		aRe[4]=s2_re - s3_re; aIm[4]=s2_im - s3_im;
	} 

	/**
	*
	*/
	private void fft_8() {
		double[] aRe = new double [4];
		double[] aIm = new double [4];
		double[] bRe = new double [4];
		double[] bIm = new double [4];
		double gem;
		
		aRe[0] = zRe[0];    bRe[0] = zRe[1];
		aRe[1] = zRe[2];    bRe[1] = zRe[3];
		aRe[2] = zRe[4];    bRe[2] = zRe[5];
		aRe[3] = zRe[6];    bRe[3] = zRe[7];

		aIm[0] = zIm[0];    bIm[0] = zIm[1];
		aIm[1] = zIm[2];    bIm[1] = zIm[3];
		aIm[2] = zIm[4];    bIm[2] = zIm[5];
		aIm[3] = zIm[6];    bIm[3] = zIm[7];

		fft_4(aRe, aIm); fft_4(bRe, bIm);

		gem    = c8*(bRe[1] + bIm[1]);
		bIm[1] = c8*(bIm[1] - bRe[1]);
		bRe[1] = gem;
		gem    = bIm[2];
		bIm[2] =-bRe[2];
		bRe[2] = gem;
		gem    = c8*(bIm[3] - bRe[3]);
		bIm[3] =-c8*(bRe[3] + bIm[3]);
		bRe[3] = gem;
		
		zRe[0] = aRe[0] + bRe[0]; zRe[4] = aRe[0] - bRe[0];
		zRe[1] = aRe[1] + bRe[1]; zRe[5] = aRe[1] - bRe[1];
		zRe[2] = aRe[2] + bRe[2]; zRe[6] = aRe[2] - bRe[2];
		zRe[3] = aRe[3] + bRe[3]; zRe[7] = aRe[3] - bRe[3];

		zIm[0] = aIm[0] + bIm[0]; zIm[4] = aIm[0] - bIm[0];
		zIm[1] = aIm[1] + bIm[1]; zIm[5] = aIm[1] - bIm[1];
		zIm[2] = aIm[2] + bIm[2]; zIm[6] = aIm[2] - bIm[2];
		zIm[3] = aIm[3] + bIm[3]; zIm[7] = aIm[3] - bIm[3];
	} 

	/**
	*
	*/
	private void fft_10() {
		double[] aRe = new double[5];
		double[] aIm = new double[5];
		double[] bRe = new double[5];
		double[] bIm = new double[5];
		
		aRe[0] = zRe[0];    bRe[0] = zRe[5];
		aRe[1] = zRe[2];    bRe[1] = zRe[7];
		aRe[2] = zRe[4];    bRe[2] = zRe[9];
		aRe[3] = zRe[6];    bRe[3] = zRe[1];
		aRe[4] = zRe[8];    bRe[4] = zRe[3];

		aIm[0] = zIm[0];    bIm[0] = zIm[5];
		aIm[1] = zIm[2];    bIm[1] = zIm[7];
		aIm[2] = zIm[4];    bIm[2] = zIm[9];
		aIm[3] = zIm[6];    bIm[3] = zIm[1];
		aIm[4] = zIm[8];    bIm[4] = zIm[3];

		fft_5(aRe, aIm); 
		fft_5(bRe, bIm);

		zRe[0] = aRe[0] + bRe[0]; zRe[5] = aRe[0] - bRe[0];
		zRe[6] = aRe[1] + bRe[1]; zRe[1] = aRe[1] - bRe[1];
		zRe[2] = aRe[2] + bRe[2]; zRe[7] = aRe[2] - bRe[2];
		zRe[8] = aRe[3] + bRe[3]; zRe[3] = aRe[3] - bRe[3];
		zRe[4] = aRe[4] + bRe[4]; zRe[9] = aRe[4] - bRe[4];

		zIm[0] = aIm[0] + bIm[0]; zIm[5] = aIm[0] - bIm[0];
		zIm[6] = aIm[1] + bIm[1]; zIm[1] = aIm[1] - bIm[1];
		zIm[2] = aIm[2] + bIm[2]; zIm[7] = aIm[2] - bIm[2];
		zIm[8] = aIm[3] + bIm[3]; zIm[3] = aIm[3] - bIm[3];
		zIm[4] = aIm[4] + bIm[4]; zIm[9] = aIm[4] - bIm[4];
	} 

	/**
	*
	*/
	private void fft_odd(int radix) {
		double  rere, reim, imre, imim;
		int     i,j,k,p,max;

		p = radix;
		max = (p + 1)/2;
		for (j=1; j < max; j++)
		{
		  vRe[j] = zRe[j] + zRe[p-j];
		  vIm[j] = zIm[j] - zIm[p-j];
		  wRe[j] = zRe[j] - zRe[p-j];
		  wIm[j] = zIm[j] + zIm[p-j];
		}

		for (j=1; j < max; j++)
		{
			zRe[j]=zRe[0]; 
			zIm[j]=zIm[0];
			zRe[p-j]=zRe[0]; 
			zIm[p-j]=zIm[0];
			k=j;
			for (i=1; i < max; i++)
			{
				rere = trigRe[k] * vRe[i];
				imim = trigIm[k] * vIm[i];
				reim = trigRe[k] * wIm[i];
				imre = trigIm[k] * wRe[i];
				
				zRe[p-j] += rere + imim;
				zIm[p-j] += reim - imre;
				zRe[j]   += rere - imim;
				zIm[j]   += reim + imre;

				k = k + j;
				if (k >= p)  k = k - p;
			}
		}
		for (j=1; j < max; j++)
		{
			zRe[0]=zRe[0] + vRe[j]; 
			zIm[0]=zIm[0] + wIm[j];
		}
	}

	/**
	*
	*/
	private void twiddleTransf(int sofarRadix, int radix, int remainRadix, double yRe[], double yIm[]) { 
		double  cosw, sinw, gem;
		double  t1_re,t1_im, t2_re,t2_im, t3_re,t3_im;
		double  t4_re,t4_im, t5_re,t5_im;
		double  m2_re,m2_im, m3_re,m3_im, m4_re,m4_im;
		double  m1_re,m1_im, m5_re,m5_im;
		double  s1_re,s1_im, s2_re,s2_im, s3_re,s3_im;
		double  s4_re,s4_im, s5_re,s5_im;

		initTrig(radix);
		omega = 2*Math.PI/(double)(sofarRadix*radix);
		cosw =  Math.cos(omega);
		sinw = -Math.sin(omega);
		tw_re = 1.0;
		tw_im = 0;
		dataOffset=0;
		groupOffset=dataOffset;
		adr=groupOffset;
		for (dataNo=0; dataNo<sofarRadix; dataNo++) {
			if (sofarRadix>1) {
				twiddleRe[0] = 1.0; 
				twiddleIm[0] = 0.0;
				twiddleRe[1] = tw_re;
				twiddleIm[1] = tw_im;
				for (twNo=2; twNo<radix; twNo++) {
					twiddleRe[twNo]=tw_re*twiddleRe[twNo-1]-tw_im*twiddleIm[twNo-1];
					twiddleIm[twNo]=tw_im*twiddleRe[twNo-1]+tw_re*twiddleIm[twNo-1];
				}
				gem   = cosw*tw_re - sinw*tw_im;
				tw_im = sinw*tw_re + cosw*tw_im;
				tw_re = gem;                      
			}
			for (groupNo=0; groupNo<remainRadix; groupNo++) {
				if ((sofarRadix>1) && (dataNo > 0)) {
					zRe[0]=yRe[adr];
					zIm[0]=yIm[adr];
					blockNo=1;
					do {
						adr = adr + sofarRadix;
						zRe[blockNo]=twiddleRe[blockNo]*yRe[adr]-twiddleIm[blockNo]*yIm[adr];
						zIm[blockNo]=twiddleRe[blockNo]*yIm[adr]+twiddleIm[blockNo]*yRe[adr]; 
						blockNo++;
					} while (blockNo < radix);
				}
				else
					for (blockNo=0; blockNo<radix; blockNo++) {
					   zRe[blockNo]=yRe[adr];
					   zIm[blockNo]=yIm[adr];
					   adr=adr+sofarRadix;
					}
				switch(radix) {
				  case  2  : gem=zRe[0] + zRe[1];
							 zRe[1]=zRe[0] -  zRe[1]; zRe[0]=gem;
							 gem=zIm[0] + zIm[1];
							 zIm[1]=zIm[0] - zIm[1]; zIm[0]=gem;
							 break;
				  case  3  : t1_re=zRe[1] + zRe[2]; t1_im=zIm[1] + zIm[2];
							 zRe[0]=zRe[0] + t1_re; zIm[0]=zIm[0] + t1_im;
							 m1_re=c3_1*t1_re; m1_im=c3_1*t1_im;
							 m2_re=c3_2*(zIm[1] - zIm[2]); 
							 m2_im=c3_2*(zRe[2] -  zRe[1]);
							 s1_re=zRe[0] + m1_re; s1_im=zIm[0] + m1_im;
							 zRe[1]=s1_re + m2_re; zIm[1]=s1_im + m2_im;
							 zRe[2]=s1_re - m2_re; zIm[2]=s1_im - m2_im;
							 break;
				  case  4  : t1_re=zRe[0] + zRe[2]; t1_im=zIm[0] + zIm[2];
							 t2_re=zRe[1] + zRe[3]; t2_im=zIm[1] + zIm[3];

							 m2_re=zRe[0] - zRe[2]; m2_im=zIm[0] - zIm[2];
							 m3_re=zIm[1] - zIm[3]; m3_im=zRe[3] - zRe[1];

							 zRe[0]=t1_re + t2_re; zIm[0]=t1_im + t2_im;
							 zRe[2]=t1_re - t2_re; zIm[2]=t1_im - t2_im;
							 zRe[1]=m2_re + m3_re; zIm[1]=m2_im + m3_im;
							 zRe[3]=m2_re - m3_re; zIm[3]=m2_im - m3_im;
							 break;
				  case  5  : t1_re=zRe[1] + zRe[4]; t1_im=zIm[1] + zIm[4];
							 t2_re=zRe[2] + zRe[3]; t2_im=zIm[2] + zIm[3];
							 t3_re=zRe[1] - zRe[4]; t3_im=zIm[1] - zIm[4];
							 t4_re=zRe[3] - zRe[2]; t4_im=zIm[3] - zIm[2];
							 t5_re=t1_re + t2_re; t5_im=t1_im + t2_im;
							 zRe[0]=zRe[0] + t5_re; zIm[0]=zIm[0] + t5_im;
							 m1_re=c5_1*t5_re; m1_im=c5_1*t5_im;
							 m2_re=c5_2*(t1_re - t2_re); 
							 m2_im=c5_2*(t1_im - t2_im);

							 m3_re=-c5_3*(t3_im + t4_im); 
							 m3_im=c5_3*(t3_re + t4_re);
							 m4_re=-c5_4*t4_im; m4_im=c5_4*t4_re;
							 m5_re=-c5_5*t3_im; m5_im=c5_5*t3_re;

							 s3_re=m3_re - m4_re; s3_im=m3_im - m4_im;
							 s5_re=m3_re + m5_re; s5_im=m3_im + m5_im;
							 s1_re=zRe[0] + m1_re; s1_im=zIm[0] + m1_im;
							 s2_re=s1_re + m2_re; s2_im=s1_im + m2_im;
							 s4_re=s1_re - m2_re; s4_im=s1_im - m2_im;

							 zRe[1]=s2_re + s3_re; zIm[1]=s2_im + s3_im;
							 zRe[2]=s4_re + s5_re; zIm[2]=s4_im + s5_im;
							 zRe[3]=s4_re - s5_re; zIm[3]=s4_im - s5_im;
							 zRe[4]=s2_re - s3_re; zIm[4]=s2_im - s3_im;
							 break;
				  case  8  : fft_8(); break;
				  case 10  : fft_10(); break;
				  default  : fft_odd(radix); break;
				}
				adr=groupOffset;
				for (blockNo=0; blockNo<radix; blockNo++) {
					yRe[adr]=zRe[blockNo]; yIm[adr]=zIm[blockNo];
					adr=adr+sofarRadix;
				}
				groupOffset=groupOffset+sofarRadix*radix;
				adr=groupOffset;
			}
			dataOffset=dataOffset+1;
			groupOffset=dataOffset;
			adr=groupOffset;
		}
	}

	/**
	* Perform the FFT for all sizes of signal.
	*/
	private void doFFT_Mix(double xRe[], double xIm[], final int size) {
		//int[] sofarRadix = new int[maxFactorCount]; 
		//int[] actualRadix = new int[maxFactorCount];
		//int[] remainRadix = new int[maxFactorCount];
		int   count;
		
		// Mod
	   
		//Math.pi = 4*Math.atan(1);    
		n = size;
		transTableSetup(sofarRadix, actualRadix, remainRadix);
		permute(actualRadix, remainRadix, xRe, xIm, yReOut, yImOut);
		
		for (count=1; count<=nFactor; count++)
		  twiddleTransf(sofarRadix[count], actualRadix[count], remainRadix[count], 
						yReOut, yImOut);
		// Copy results
		for (int i=0;i<n;i++) {
			xRe[i] = yReOut[i];
			xIm[i] = yImOut[i];
		}
	}

	/*
	* Perform the FFT for all sizes of signal, the start and the length of the FFT
	* can be choosen to transform a part of a signal.
	*/
	private void doFFT_Mix(double xRe[], double xIm[], final int size, final int shift) {
		double[] tmp_xRe = new double[size];
		double[] tmp_xIm = new double[size];
		
		for (int i=0;i<size;i++) {
			tmp_xRe[i] = xRe[i+shift];
			tmp_xIm[i] = xIm[i+shift];
		}
		
		doFFT_Mix(tmp_xRe,tmp_xIm,size);
		for (int i=0;i<size;i++) {
			xRe[i+shift] = tmp_xRe[i];
			xIm[i+shift] = tmp_xIm[i];
		}
	}

	/*
	* Perform the IFFT for all sizes of signal.
	*/
	private void doIFFT_Mix(double xRe[], double xIm[], final int size) {
		for (int i=0;i<size;i++) {
			xIm[i] = -xIm[i];
		}

		doFFT_Mix(xRe,xIm,size);

		for (int i=0;i<size;i++) {
			xRe[i] = xRe[i]/size;
			xIm[i] = -xIm[i]/size;
		}
	}

	/*
	* Perform the IFFT for all sizes of signal, the start and the length of the IFFT
	* can be choosen to transform a part of a signal.
	*/
	private void doIFFT_Mix(double xRe[], double xIm[], final int size, final int shift) {
		double[] tmp_xRe = new double[size];
		double[] tmp_xIm = new double[size];
		
		for (int i=0;i<size;i++) {
			tmp_xRe[i] = xRe[i+shift];
			tmp_xIm[i] = xIm[i+shift];
		}
		doIFFT_Mix(tmp_xRe,tmp_xIm,size);
		for (int i=0;i<size;i++) {
			xRe[i+shift] = tmp_xRe[i];
			xIm[i+shift] = tmp_xIm[i];
		}
	}
} 
