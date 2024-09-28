package orientation.fft;

/**
 * FFT2D
 */

public class FFT2D {
	
	/**
	* Perform the FFT 2D.
	*/
	public static ComplexSignal transform(ComplexSignal in) {
		int nx = in.nx;
		int ny = in.ny;
		ComplexSignal fourier = new ComplexSignal(nx, ny);		// real and imag
		double colReal[] = new double[ny];
		double colImag[] = new double[ny];
		FFT1D ffty = new FFT1D(ny);
		
		for (int x=0; x<nx; x++) {

			getY(x, in.real, colReal);
			if (in.imag == null)
				for(int i=0; i<ny; i++) colImag[i] = 0.0;
			else
				getY(x, in.imag, colImag);
			ffty.transform(colReal, colImag, ny, 0);
			putY(x, fourier.real, colReal);
			putY(x, fourier.imag, colImag);

		}
	
		double rowReal[] = new double[nx];
		double rowImag[] = new double[nx];
		
		FFT1D fftx = new FFT1D(nx);

		for (int y=0; y<ny; y++) {

			getX(y, fourier.real, rowReal);	
			getX(y, fourier.imag, rowImag);
			fftx.transform(rowReal, rowImag, nx, 0);
			putX(y, fourier.real, rowReal);	
			putX(y, fourier.imag, rowImag);
		}
		return fourier;
	}

	/**
	* Perform the inverse FFT 2D.
	*/
	public static ComplexSignal inverse(ComplexSignal fourier) {

		int nx = fourier.nx;
		int ny = fourier.ny;
		ComplexSignal out = new ComplexSignal(nx, ny);	
		double colReal[] = new double[ny];
		double colImag[] = new double[ny];

		FFT1D ffty = new FFT1D(ny);
		for (int x=0; x<nx; x++) {
			getY(x, fourier.real, colReal);	
			getY(x, fourier.imag, colImag);
			ffty.inverse(colReal, colImag, ny, 0);
			putY(x, out.real, colReal);	
			putY(x, out.imag, colImag);
		}
	
		double rowReal[] = new double[nx];
		double rowImag[] = new double[nx];
		FFT1D fftx = new FFT1D(nx);
		for (int y=0; y<ny; y++) {
			getX(y, out.real, rowReal);	
			getX(y, out.imag, rowImag);
			fftx.inverse(rowReal, rowImag, nx, 0);
			putX(y, out.real, rowReal);	
			putX(y, out.imag, rowImag);
		}
		return out;
	}
	
	
	private static void getY(int x, double in[], double col[]) {
		int ny = col.length;
		int nx = in.length / ny;
		for(int j=0, k=x; j<ny; j++, k+=nx)
			col[j] = in[k];
	}

	private static void getX(int y, double in[], double row[]) {
		int nx = row.length;
		System.arraycopy(in, y*nx, row, 0, nx);
	}

	private static void putY(int x, double in[], double col[]) {
		int ny = col.length;
		int nx = in.length / ny;
		for(int j=0, k=x; j<ny; j++, k+=nx)
			in[k] = col[j];
	}

	private static void putX(int y, double in[], double row[]) {
		int nx = row.length;
		System.arraycopy(row, 0, in, y*nx, nx);
	}
	

}