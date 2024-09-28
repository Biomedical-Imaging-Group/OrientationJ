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

import java.text.DecimalFormat;
import java.util.Random;

import ij.ImagePlus;
import ij.ImageStack;
import ij.process.FloatProcessor;
import orientation.fft.FFT1D;
import orientation.imageware.ImageWare;

/**
* A ComplexImage object is a two-dimensional array of complex numbers.
* @author Katarina Balac, EPFL.
*/

public class ComplexImage {

	/**
	* The real part of the ComplexImage object.
	*/	
	public double real[];
	
	/**
	* The imaginary part of the ComplexImage object.
	*/
	public double imag[];
	
	/**
	* The number of columns in the ComplexImage object.
	*/	
	public int nx;
	
	/**
	* The number of rows in the ComplexImage object.
	*/	
	public int ny;
	
	/**
	* The total number of elements in the ComplexImage object.
	*/
	public int nxy;
	
	private final double PI2=2.0*Math.PI;
	private final double sqrt2=Math.sqrt(2.0);
	
	/**
	* Creates a ComplexImage object from an ImagePlus image.
	* @param imp the ImagePlus image used to create the ComplexImage object
	*/

	public ComplexImage(ImageWare image) {
		nx = image.getWidth();
		ny = image.getHeight();
		int nc = image.getSizeZ();
		nxy = nx*ny;
		real = new double[nxy];
		imag = new double[nxy];
		
		if (image.getType() == ImageWare.BYTE) {
			byte[] pixels = image.getSliceByte(0);
			for(int k=0; k<nx*ny; k++)
				real[k] = (double)(pixels[k] & 0x00FF);
			if (nc == 2) {
				byte[] ipixels = image.getSliceByte(1);
				for(int k=0; k<nx*ny; k++)
					imag[k] = (double)(ipixels[k] & 0x00FF);
			}
		}
		else if (image.getType() == ImageWare.SHORT) {
			short[] pixels = image.getSliceShort(0);
			for(int k=0; k<nx*ny; k++)
				real[k] = (double)(pixels[k]);
			if (nc == 2) {
				short[] ipixels = image.getSliceShort(1);
				for(int k=0; k<nx*ny; k++)
					imag[k] = (double)(ipixels[k]);
			}
		}
		else if (image.getType() == ImageWare.FLOAT) {
			float[] pixels = image.getSliceFloat(0);
			for(int k=0; k<nx*ny; k++)
				real[k] = (double)(pixels[k]);
			if (nc == 2) {
				float[] ipixels = image.getSliceFloat(1);
				for(int k=0; k<nx*ny; k++)
					imag[k] = (double)(ipixels[k]);
			}
		}
		else if (image.getType() == ImageWare.DOUBLE) {
			double[] pixels = image.getSliceDouble(0);
			for(int k=0; k<nx*ny; k++)
				real[k] = pixels[k];
			if (nc == 2) {
				double[] ipixels = image.getSliceDouble(1);
				for(int k=0; k<nx*ny; k++)
					imag[k] = ipixels[k];
			}
		}
	}

	public ComplexImage(ImageWare inr, ImageWare ini) {
		nx = inr.getWidth();
		ny = inr.getHeight();
		nxy = nx*ny;
		real = new double[nxy];
		imag = new double[nxy];
		if (inr.getType() == ImageWare.BYTE) {
			byte[] pixels = inr.getSliceByte(0);
			for(int k=0; k<nx*ny; k++)
				real[k] = (double)(pixels[k] & 0x00FF);
		}
		else if (inr.getType() == ImageWare.SHORT) {
			short[] pixels = inr.getSliceShort(0);
			for(int k=0; k<nx*ny; k++)
				real[k] = (double)(pixels[k]);
		}
		else if (inr.getType() == ImageWare.FLOAT) {
			float[] pixels = inr.getSliceFloat(0);
			for(int k=0; k<nx*ny; k++)
				real[k] = (double)(pixels[k]);
		}
		else if (inr.getType() == ImageWare.DOUBLE) {
			double[] pixels = inr.getSliceDouble(0);
			for(int k=0; k<nx*ny; k++)
				real[k] = pixels[k];
		}
		
		if (ini.getType() == ImageWare.BYTE) {
			byte[] pixels = ini.getSliceByte(0);
			for(int k=0; k<nx*ny; k++)
				imag[k] = (double)(pixels[k] & 0x00FF);
		}
		else if (ini.getType() == ImageWare.SHORT) {
			short[] pixels = ini.getSliceShort(0);
			for(int k=0; k<nx*ny; k++)
				imag[k] = (double)(pixels[k]);
		}
		else if (ini.getType() == ImageWare.FLOAT) {
			float[] pixels = ini.getSliceFloat(0);
			for(int k=0; k<nx*ny; k++)
				imag[k] = (double)(pixels[k]);
		}
		else if (ini.getType() == ImageWare.DOUBLE) {
			double[] pixels = ini.getSliceDouble(0);
			for(int k=0; k<nx*ny; k++)
				imag[k] = pixels[k];
		}
	}
	
	public ComplexImage(ImagePlus imp) {
		nx = imp.getWidth();
		ny = imp.getHeight();
		nxy = nx*ny;
		real = new double[nxy];
		imag = new double[nxy];
		if (imp.getType() == ImagePlus.GRAY8) {
			byte[] pixels = (byte[])imp.getProcessor().getPixels();
			for(int k=0; k<nx*ny; k++)
				real[k] = (double)(pixels[k] & 0x00FF);
		}
		if (imp.getType() == ImagePlus.GRAY16) {
			short[] pixels = (short[])imp.getProcessor().getPixels();
			for(int k=0; k<nx*ny; k++)
				real[k] = (double)(pixels[k]);
		}
		if (imp.getType() == ImagePlus.GRAY32) {
			float[] pixels = (float[])imp.getProcessor().getPixels();
			for(int k=0; k<nx*ny; k++)
				real[k] = (double)(pixels[k]);
		}
	}
	

	/**
	* Creates a Compleximage object of a given size filed with zeros.
	* @param  sizex the number of columns in the ComplexImage
	* @param sizey the number of rows in the ComplexImage
	*/
	
	public ComplexImage(int sizex, int sizey) {	
		nx=sizex;
		ny=sizey;
		nxy = nx*ny;
		real = new double[nxy];
		imag = new double[nxy];
	}
	
		/**
	* Creates a Compleximage object of a given size filed with zeros.
	* @param  sizex the number of columns in the ComplexImage
	* @param sizey the number of rows in the ComplexImage
	*/
	
	public ComplexImage(int sizex, int sizey, double real[]) {	
		nx=sizex;
		ny=sizey;
		nxy = nx*ny;
		this.real = real;
		imag = null;
	}

	/**
	* Creates a Compleximage object of a given size filed with zeros.
	* @param  sizex the number of columns in the ComplexImage
	* @param sizey the number of rows in the ComplexImage
	*/
	
	public ComplexImage(int sizex, int sizey, double real[], double imag[]) {	
		nx=sizex;
		ny=sizey;
		nxy = nx*ny;
		this.real = real;
		this.imag = imag;
	}

	/**
	* Creates a Compleximage object of a given size filed with zeros, enables not to reserve space for the imaginary part.
	* @param  sizex the number of columns in the ComplexImage
	* @param sizey the number of rows in the ComplexImage
	* @param r if true the image is real, the imaginary part is set to null
	*/
	
	public ComplexImage(int sizex, int sizey, boolean r) {	
		nx=sizex;
		ny=sizey;
		nxy = nx*ny;
		real = new double[nxy];
		imag = null;
		if (!r) {
			imag = new double[nxy];
		}
	}
	
	/**
	* If necessary, this ComplexImage is cropped by keeping its central part so that the quincunx wavelet analysis can be performed.
	* @param J number of decomposition levels
	*/
	
	public void cropQuincunx(int J) {
		int ind=1;
		int lx=nx;
		int ly=ny;
		for(int j=0;j<J;j++) {
			if(ind==1) {
				lx/=2;
			}
			else {
				ly/=2;
			}
			ind=1-ind;
		}
		ind=1-ind;
		for(int j=0;j<J;j++) {
			if(ind==1) {
				lx*=2;
			}
			else {
				ly*=2;
			}
			ind=1-ind;
		}
		if(!((lx==nx)&&(ly==ny))) {
			System.out.println("Image has been cropped");
			int dx=(nx-lx)/2;
			int dy=(ny-ly)/2;
			ComplexImage temp=getSubimage(dx,lx-1+dx,dy,ly-1+dy);
			nx=temp.nx;
			ny=temp.ny;
			nxy=temp.nxy;
			System.arraycopy(temp.real,0,real,0,nxy);
			System.arraycopy(temp.imag,0,imag,0,nxy);
			showReal("Cropped image");
		}
	}
	
	
	/**
	* If necessary, this ComplexImage is cropped by keeping its central part so that the dyadic wavelet analysis can be performed.
	* @param J number of decomposition levels
	*/
	
	public void cropDyadic(int J) {
		int lx=nx;
		int ly=ny;
		for(int j=0;j<J;j++) {		
			lx/=2;		
			ly/=2;	
		}
	
		for(int j=0;j<J;j++) {		
			lx*=2;		
			ly*=2;		
		}
		if(!((lx==nx)&&(ly==ny))) {
			System.out.println("Image has been cropped");
			int dx=(nx-lx)/2;
			int dy=(ny-ly)/2;
			ComplexImage temp=getSubimage(dx,lx-1+dx,dy,ly-1+dy);
			nx=temp.nx;
			ny=temp.ny;
			nxy=temp.nxy;
			System.arraycopy(temp.real,0,real,0,nxy);
			System.arraycopy(temp.imag,0,imag,0,nxy);
			showReal("Cropped image");
		}
	}
	
	
	/**
	* Returns the maximum number of wavelet iterations for this ComplexImage.
	* @param type the transform lattice, "Quincunx" or "Dyadic"
	*/
	
	public int noIterations(String type) {
		int noit=0;
		int dx=nx;
		int dy=ny;
		if (type=="Dyadic") {
			while ((dx%2==0)&&(dy%2==0)) {
				noit++;
				dx/=2;
				dy/=2;
			}
		}
		if (type=="Quincunx") {
			while ((dx%2==0)&&(dy%2==0)) {
				noit+=2;
				dx/=2;
				dy/=2;
			}
			if (dx%2==0) {
				noit++;
			}
		}
		return noit;
	}

	
	/**
	* Crops this ComplexImage to a given size by keeping its central part.
	* @param lx number of columns in the cropped ComplexImage 
	* @param ly number of rows in the cropped ComplexImage 
	*/
	
	public void croptoSize(int lx, int ly) {
		if(!((lx==nx)&&(ly==ny))) {
			int dx=(nx-lx)/2;
			int dy=(ny-ly)/2;
			ComplexImage temp=getSubimage(dx,lx-1+dx,dy,ly-1+dy);
			nx=temp.nx;
			ny=temp.ny;
			nxy=temp.nxy;
			System.arraycopy(temp.real,0,real,0,nxy);
			System.arraycopy(temp.imag,0,imag,0,nxy);
		}
	}
	
	
	/**
	* Fills the real part of this squared ComplexImage with the zoneplate image.
	*/
	
	public void zone() {
		int size=nx;
		int size2=size/2;
		double c=Math.PI/sqrt2/(double)nx;
		int ind=0;
		for(int y1=-size2; y1<size2; y1++) {
			for(int x1=-size2; x1<size2; x1++) {
				real[ind++]=(1.0+Math.cos(c*(double)(y1*y1+x1*x1)))*127.5;
			}
		}
	}
	
			
	/**
	* Fills the real part of this ComplexImage with random values of Gaussian distribution N(0,1).
	*/
	
	public void makeNoise() {
		Random random=new Random();
		for (int k=0;k<nxy;k++) {
			real[k]=random.nextGaussian();
		}
	}
	
	
	/**
	* Fills the real part of this ComplexImage with the Gaussian shaped spots.
	*/
	
	public void makeGaussianSpots() {
		Random random=new Random();
		int N=random.nextInt(12)+9; //Number of spots, between 10 and 20
		//int N=1;
		int[] X0=new int[N];
		int[] Y0=new int[N];
		double[] sigma=new double[N];
		for (int k=0;k<N;k++) {
			X0[k]=random.nextInt(nx-60)+30;
			Y0[k]=random.nextInt(ny-60)+30;
			sigma[k]=random.nextDouble()*5.0+1.0;   //sigma between 1 and 6
		}
		for (int k=0;k<N;k++) {
			double s2=2.0*sigma[k]*sigma[k];
			//double s1=1.0/(s2*Math.PI);
			double s1=1.0/(2.0*Math.PI);
			for (int y=-30;y<30;y++) {
				int dy=nx*y;
				for (int x=-30;x<30;x++) {
					int p0=Y0[k]*nx+X0[k];
					double s=s1*Math.exp(-(double)(x*x+y*y)/s2);
					real[p0+dy+x]+=s;
				}	
			}
		}
	}
	

	/** 
	* Displays the real part of this ComplexImage on the screen.
	* @param text name of the image to display
	*/
	
	public void showReal(String text) {
		float re[][] = new float[nx][ny];
		int index;
		for(int k=0;k<nx;k++)
		for(int l=0;l<ny;l++) {
			index = l*nx+k;
			re[k][l] = (float)real[index];
		}
		FloatProcessor fp = new FloatProcessor(re);
		ImagePlus imp = new ImagePlus(text, fp);
		imp.show();
	}
	

	/** 
	* Displays the imaginary part of this ComplexImage on the screen.
	* @param text name of the image to display
	*/
	
	public void showImag(String text) {
		float imaginary[][] = new float[nx][ny];
		int index;
		for(int k=0;k<nx;k++)
		for(int l=0;l<ny;l++) {
			index = l*nx+k;
			imaginary[k][l] = (float)imag[index];
		}
		FloatProcessor fp = new FloatProcessor(imaginary);
		ImagePlus imp = new ImagePlus(text, fp);
		imp.show();
	}

	
	/** 
	* Displays the modulus of this ComplexImage on the screen.
	* @param text name of the image to display
	*/
	
	public void showModulus(String text) {
		float mod[][] = new float[nx][ny];
		int index;
		for(int k=0;k<nx;k++)
		if (imag==null) {
			for(int l=0;l<ny;l++) {
				index = l*nx+k;
				mod[k][l] = (float)Math.abs(real[index]);
			}
		} else {
			for(int l=0;l<ny;l++) {
				index = l*nx+k;
				mod[k][l] = (float)Math.sqrt(real[index]*real[index] + imag[index]*imag[index]);
			}
		}
		FloatProcessor fp = new FloatProcessor(mod);
		ImagePlus imp = new ImagePlus(text, fp);
		imp.show();
	}


	/**
	* Displays the real and the imaginary part of this ComplexImage as a stack.
	* @param text name of the stack to be displayed
	*/		
			
	public void displayComplexStack(String text) {		
		double color=-255.0;
		ComplexImage[] out=new ComplexImage[2];	
		ImageStack stack=new ImageStack(nx,ny);
		float ima[][] = new float[nx][ny];
		for(int k=0;k<nx;k++) {
			for(int l=0;l<ny;l++) {
				int index = l*nx+k;
				ima[k][l] = (float)real[index];
			}
		}		
		FloatProcessor fp = new FloatProcessor(ima);	
		fp.setValue(color);	
		stack.addSlice("Real part",fp);
		for(int k=0;k<nx;k++) {
			for(int l=0;l<ny;l++) {
				int index = l*nx+k;
				ima[k][l] = (float)imag[index];
			}
		}
		fp = new FloatProcessor(ima);	
		fp.setValue(color);	
		stack.addSlice("Imaginary part",fp);		
		ImagePlus imp=new ImagePlus(text,stack);
		imp.show();
	}
	
	
	/**
	* Displays the real parts of ComplexImages in an array as a stack.
	* @param array the aray of images to be displayed
	* @param text name of the stack to be displayed
	*/

	public static void displayStack(ComplexImage[] array, String text) {
		int nx=array[0].nx;
		int ny=array[0].ny;
		ImageStack stack=new ImageStack(nx,ny);
		float ima[][] = new float[nx][ny];
		for(int j=0;j<array.length;j++) {
			float im[][] = new float[nx][ny];
			int index;
			for(int k=0;k<nx;k++) {
				for(int l=0;l<ny;l++) {
					index = l*nx+k;
					ima[k][l] = (float)array[j].real[index];
				}
			}
			FloatProcessor fp = new FloatProcessor(ima);
			stack.addSlice("j",fp);
		}
		ImagePlus imp=new ImagePlus(text,stack);
		imp.show();
	}
	
	
	/**
	* Displays the imaginary parts of ComplexImages in an array as a stack.
	* @param array the aray of images to be displayed
	* @param text name of the stack to be displayed
	*/

	public static void displayStackImag(ComplexImage[] array, String text) {
		int nx=array[0].nx;
		int ny=array[0].ny;
		ImageStack stack=new ImageStack(nx,ny);
		float ima[][] = new float[nx][ny];
		for(int j=0;j<array.length;j++) {
			float im[][] = new float[nx][ny];
				int index;
				for(int k=0;k<nx;k++)
					for(int l=0;l<ny;l++) {
						index = l*nx+k;
						ima[k][l] = (float)array[j].imag[index];
					}
			FloatProcessor fp = new FloatProcessor(ima);
			stack.addSlice("j",fp);
		}
		ImagePlus imp=new ImagePlus(text,stack);
		imp.show();
	}

	
	/**
	* Displays this ComplexImage values on the screen.
	* @param text name of the image to be displayed
	*/

	public void displayValues(String text) {
		DecimalFormat decimalFormat=new DecimalFormat();
		decimalFormat.applyPattern("0.0000");
		System.out.println(" ");
		System.out.println(text);	
		System.out.println("sizenxnx="+nx);
		System.out.println("sizeny="+ny);
		for (int i=0;i<ny;i++) {
			for (int j=0;j<nx;j++) {
				System.out.print("  "+decimalFormat.format(real[i*nx+j])+"+"+decimalFormat.format(imag[i*nx+j])+"i");
			}
			System.out.println(" ");
		}
	}
	

	/**
	* Sets both the real and the imaginary part of this ComplexImage object to zero.
	*/

	public void putZeros() {
		if (imag==null) {
			for (int i=0;i<nxy;i++) {
				real[i]=0.0;
			}
		} else {
			for (int i=0;i<nxy;i++) {
				real[i]=imag[i]=0.0;
			}
		}
	}

	
	/**
	* Sets the real part of this ComplexImage object to zero.
	*/
	
	public void setRealtoZero() {
		for(int k=0;k<nxy;k++) {
			real[k]=0.0;
		}
	}
	
	
	/**
	* Sets the imaginary part of this ComplexImage object to zero.
	*/
	
	public void setImagtoZero() {
		for(int k=0;k<nxy;k++) {
			imag[k]=0.0;
		}
	}
	
	
	/**
	* Sets all the values in this ComplexImage to a given constant.
	* @param r - the real part of the ComplexImage to be set
	* @param i - the imaginary part of the ComplexImage to be set
	*/
	
	public void settoConstant(double r, double i) {
		for(int k=0;k<nxy;k++) {
			real[k]=r;
			imag[k]=i;
		}
	}
	
	
	/**
	* Sets the real part of this ComplexImage to a given constant.
	* @param r - the value to be set
	*/
	
	public void settoConstant(double r) {
		for(int k=0;k<nxy;k++) {
			real[k]=r;
		}
	}
	
	
	/**
	* Creates a new ComplexImage object containing the chosen row of this ComplexImage. 
	* @param y the number of row to be copied
	* @return ComplexImage containing the chosen row
	*/
	
	public ComplexImage getRow(int y) {
		ComplexImage row=new ComplexImage(nx,1,imag==null);
		System.arraycopy(real,y*nx,row.real,0,nx);
		if (!(imag==null)) {
			System.arraycopy(imag,y*nx,row.imag,0,nx);
		}
		return row;
	}
	
	
	/**
	* Copies the content of the chosen row of image to this ComplexImage.
	* @param image the ComplexImage to copy from
	* @param y the number of row to be copied
	*/
	
	public void getRowContent(int y, ComplexImage image) {
		nx=image.nx;
		ny=1;
		int s=y*nx;
		System.arraycopy(image.real,s,real,0,image.nx);
		if ((!(imag==null))&&(!(image.imag==null))) {
			System.arraycopy(image.imag,s,imag,0,image.nx);
		}
	}
	
	
	/**
	* Copies an 1D array row to y-th row of this ComplexImage.
	* @param y number of row to modify
	* @param row ComplexImage with one row containing the row to be placed
	*/
	
	public void putRow(int y, ComplexImage row) {	
		System.arraycopy(row.real,0,real,y*nx,nx);
		if ((!(imag==null))&&(!(row.imag==null))) {	
			System.arraycopy(row.imag,0,imag,y*nx,nx);   
		}
	}
	
	
	/**
	* Creates a new ComplexImage object containing the chosen column of this ComplexImage.
	* @param x number of column to be copied
	* @return ComplexImage containing the chosen column of this ComplexImage
	*/
	
	public ComplexImage getColumn(int x) {		
		ComplexImage column=new ComplexImage(ny,1,imag==null);	
		if (imag==null) {
			for(int y=0,n=x;y<ny;y++) {
				column.real[y]=real[n];
				n+=nx;	    
			}
		} else {
			for(int y=0,n=x;y<ny;y++) {
				column.real[y]=real[n];
				column.imag[y]=imag[n];
				n+=nx;	    
			}
		}	
		return column;
	}
	
	
	/**
	* Copies the content of the chosen column of image to this ComplexImage.
	* @param image the ComplexImage to copy from
	* @param x the number of column to be copied
	*/
	
	public void getColumnContent(int x, ComplexImage image) {
		nx=1;
		ny=image.ny;
		if ((image.imag==null)||(imag==null)) {
			for(int y=0,n=x;y<image.ny;y++) {
				real[y]=image.real[n];
				n+=image.nx;	    
			}
		} else {
			for(int y=0,n=x;y<image.ny;y++) {
				real[y]=image.real[n];
				imag[y]=image.imag[n];
				n+=image.nx;	    
			}
		}	
	}
	
	
	/**
	* Copies an 1D array column to x-th column of this ComplexImage.
	* @param x number of column to modify
	* @param column ComplexImage with one column containing the column to be placed
	*/
	
	public void putColumn(int x, ComplexImage column) {	
		if ((!(imag==null))&&(!(column.imag==null))) {	
			for(int y=0,n=x;y<ny;y++) {
				real[n]=column.real[y];
				imag[n]=column.imag[y];
				n+=nx;	    
			}
		} else {
			for(int y=0,n=x;y<ny;y++) {
				real[n]=column.real[y];
				n+=nx;	    
			}
		}
	}
	
	
	/**
	* Creates a new ComplexImage object containing the subimage of this ComplexImage.
	* @param x1 starting x coordinate within this ComplexImage
	* @param x2 end x coordinate within this ComplexImage
	* @param y1 starting y coordinate within this ComplexImage
	* @param y2 end y coordinate within this ComplexImage
	* @return the subimage
	*/
	
	public ComplexImage getSubimage(int x1,int x2,int y1,int y2) {		
		ComplexImage sub=new ComplexImage(x2-x1+1,y2-y1+1,imag==null);	
		int d=x1+y1*nx;
		if (imag==null) {
			for(int y=0;y<sub.ny;y++) {
				System.arraycopy(real,d+y*nx,sub.real,y*sub.nx,sub.nx);
			}
			
		} else {
			for(int y=0;y<sub.ny;y++) {
				System.arraycopy(real,d+y*nx,sub.real,y*sub.nx,sub.nx);
				System.arraycopy(imag,d+y*nx,sub.imag,y*sub.nx,sub.nx);
			}
		}	
		return(sub);
	}
	
	
	/**
	* Copies the content of a subimage of image to this ComplexImage.
	* Gives the same result as getSubimage, but does not create a new object. 
	* Total number of pixels assigned to this ComplexImage when created has to be sufficient for the subimage.
	* @param x1 starting x coordinate within image
	* @param x2 end x coordinate within image
	* @param y1 starting y coordinate within image
	* @param y2 end y coordinate within image
	* @param image the image to copy
	*/
	
	public void getSubimageContent(int x1,int x2, int y1, int y2, ComplexImage image) {		
		int d=x1+y1*image.nx;
		nx=x2-x1+1;
		ny=y2-y1+1;
		nxy=nx*ny;	
		if ((imag==null)||(image.imag==null)) {
			for(int y=0;y<ny;y++) {
				System.arraycopy(image.real,d+y*image.nx,real,y*nx,nx);
			}
		} else {
			for(int y=0;y<ny;y++) {
				System.arraycopy(image.real,d+y*image.nx,real,y*nx,nx);
				System.arraycopy(image.imag,d+y*image.nx,imag,y*nx,nx);
			}
		}	
	}
		
	
	/**
	* Copies the ComplexImage sub to the position in this ComplexImage given by its upper left corner.  
	* @param sub the image to copy
	* @param x1 starting x coordinate
	* @param y1 starting y coordinate
	*/
	
	public void putSubimage(int x1,int y1, ComplexImage sub) {	
		int k;
		int k1;
		int d=x1+y1*nx;
		if (sub.imag==null) {
			for(int y=0;y<sub.ny;y++) {
				System.arraycopy(sub.real,y*sub.nx,real,d+y*nx,sub.nx);
			}
		} else {
			for(int y=0;y<sub.ny;y++) {
				System.arraycopy(sub.real,y*sub.nx,real,d+y*nx,sub.nx);
				System.arraycopy(sub.imag,y*sub.nx,imag,d+y*nx,sub.nx);			    
			}
		}
	}
	
	
	/**
	* Creates a ComplexImage object as a copy of this ComplexImage.
	* @return the copy of this ComplexImage
	*/
	
	public ComplexImage copyImage() {
		ComplexImage temp=null;
		if (imag==null) {
			temp=new ComplexImage(nx,ny,true);	
			System.arraycopy(real, 0, temp.real, 0, nxy);
		} 
		else {
			temp = new ComplexImage(nx,ny);
			System.arraycopy(real, 0, temp.real, 0, nxy);
			System.arraycopy(imag, 0, temp.imag, 0, nxy);	
		}
		return temp;
	}
	
	
	/**
	* Copies the content of ComplexImage original to this ComplexImage.
	* Gives the same result as copyImage, but does not create a new object. 
	* Total number of pixels assigned to this ComplexImage when created has to be sufficient for the original image.
	* @param original the ComplexImage to copy
	*/
	
	public void copyImageContent(ComplexImage original) {
		nx=original.nx;
		ny=original.ny;
		nxy=original.nxy;
		if ((original.imag==null)||(imag==null)) {
			System.arraycopy(original.real,0,real, 0, nxy);
		} 
		else {
			System.arraycopy(original.real,0,real, 0, nxy);
			System.arraycopy(original.imag,0,imag, 0, nxy);	
		}
	}
		
			
	/**
	* Computes a square modulus of this ComplexImage.
	* Puts a square modulus in the real part of this ComplexImage and zeros in the imaginary part if it exists.
	*/
	
	public void squareModulus() {
		if (imag==null) {
			for (int i=0;i<nxy;i++) {
				real[i]=real[i]*real[i];
			}
		} 
		else {
			for (int i=0;i<nxy;i++) {
				real[i]=real[i]*real[i]+imag[i]*imag[i];
				imag[i]=0.0;
			}
		}
	}
	
		
	/**
	* Computes a modulus of this ComplexImage.
	* Puts a modulus in the real part of this ComplexImage and zeros in the imaginary part.
	*/
	
	public void modulus() {
		for(int k=0;k<nxy;k++) {
			real[k] = Math.sqrt(real[k]*real[k] + imag[k]*imag[k]);
			imag[k]=0.0;
		}
	}

		
	/**
	* Computes the phase of this ComplexImage.
	* Puts a phase in the real part of this ComplexImage and zeros in the imaginary part.
	*/
	
	public void phase() {
		for(int k=0;k<nxy;k++) {
			real[k]=Math.atan(imag[k]/real[k]);
			if(real[k]<0.0) {
				real[k]+=Math.PI;
			}
			imag[k]=0.0;
		}
	}
	
	
	/**
	* Adds a ComplexImage object of the same dimensions to this ComplexImage.
	* @param im the ComplexImage to add
	*/
	
	public void add(ComplexImage im) {
		if (!(im.imag==null)) {  // im is complex
			if (imag==null) {
				imag=new double[nxy];
			}
			for(int k=0;k<nxy;k++) {
				real[k]+=im.real[k];
				imag[k]+=im.imag[k];
			} 	
		} else {	// im is real 
			for(int k=0;k<nxy;k++) {
				real[k]+=im.real[k];
			}
		}
	}
	

	/**
	* Adds a real constant to every element in this ComplexImage.
	* @param d the constant to add
	*/
	
	public void add(double d) {
		for(int k=0;k<nxy;k++) {
				real[k]+=d;
		}   
	}  		
	
	
	/**
	* Substracts a ComplexImage of the same dimensions from this ComplexImage.
	* @param im the ComplexImage to be substracted
	*/
	
	public void subtract(ComplexImage im) {
		if (!(im.imag==null)) {  // im is complex
			if (imag==null) {
				imag=new double[nxy];
			}
			for(int k=0;k<nxy;k++) {
				real[k]-=im.real[k];
				imag[k]-=im.imag[k];
			} 	
		} else {	// im is real 
			for(int k=0;k<nxy;k++) {
				real[k]-=im.real[k];
			}
		}
	}
	
	
	/**
	* Performs the complex conjugate on this ComplexImage.
	*/
	
	public void conj() {
		if (!(imag==null)) {
			for(int k=0;k<nxy;k++) {
				imag[k]=-imag[k];
			}
		}
	}
	
	
	/**
	* Multiplies each element in this ComplexImage with a given constant.
	* @param constant the multiplication constant
	*/
	
	public void multiply(double constant) {
		if (imag==null) {
			for(int k=0; k<nxy; k++) {
				real[k]*=constant;
			}
		} else {
			for(int k=0; k<nxy; k++) {
				real[k]*=constant;
				imag[k]*=constant;
			}
		}
	}
	
	/**
	* Multiplies this complex image by a complex constant re+i*im.
	* @param re real part of the complex constant
	* @param im imaginary part of the complex constant
	*/
	
	public void multiply(double re, double im) {
		for (int k=0;k<nxy;k++) {
			double r=real[k];
			real[k]=re*real[k]-im*imag[k];
			imag[k]=re*imag[k]+im*r;
		}
	}
	
	
	/**
	* Multiplies this ComplexImage with another ComplexImage of same dimensions, pointwise.
	* @param im the ComplexImage to multiply with
	*/
	
	public void multiply(ComplexImage im) {	
		if (!(im.imag==null)) {  // im is complex	
			if (imag==null) {   
				imag=new double[nxy];
			}
			for(int k=0;k<nxy;k++) {
				double re=real[k];
				real[k]=im.real[k]*re-im.imag[k]*imag[k];
				imag[k]=im.real[k]*imag[k]+im.imag[k]*re;
			}
		} else {	// im is real		
			if (imag==null) {
				for(int k=0;k<nxy;k++) {
					real[k]*=im.real[k];
				} 
			} else {
				for(int k=0;k<nxy;k++) {
					real[k]*=im.real[k];
					imag[k]*=im.real[k];
				} 			
			}
		}
	}


	/**
	* Multiplies this ComplexImage by ComplexImage obtained by taking every l-th sample of circulary extended ComplexImage im.
	* @param im the ComplexImage to multiply with
	* @param l subsampling factor for im
	*/
	
	public void multiplyCircular(ComplexImage im, int l) {	
		int l2=l*l;
		int k;
		int t;
		double ima;
		double re;
		int l2y;
		if (!(im.imag==null)) {  // im is complex
			if (imag==null) {   
				imag=new double[nxy];
			}
			for(int y=0;y<ny;y++) {
				l2y=((l*y)%im.ny)*im.nx;
				for(int x=0;x<nx;x++) {    //x,y-coordinates in the image
					k=nx*y+x;
					t=l2y+((l*x)%im.nx);
					re=real[k];
					ima=imag[k];
					real[k]=im.real[t]*re-im.imag[t]*ima;
					imag[k]=im.real[t]*ima+im.imag[t]*re;
				}   
			}
		} else {	// im is real
			if (imag==null) {
				for(int y=0;y<ny;y++) {
					l2y=((l*y)%im.ny)*im.nx;
					for(int x=0;x<nx;x++) {    //x,y-coordinates in the image
						k=nx*y+x;
						t=l2y+((l*x)%im.nx);
						real[k]*=im.real[t];
					}   
				}
			} else {
				for(int y=0;y<ny;y++) {
					l2y=((l*y)%im.ny)*im.nx;
					for(int x=0;x<nx;x++) {    //x,y-coordinates in the image
						k=nx*y+x;
						t=l2y+((l*x)%im.nx);
						re=real[k];
						ima=imag[k];
						real[k]*=im.real[t];
						imag[k]*=im.real[t];
					}   
				}
			}
		}
	}
	
	
	/**
	* Multiplies this ComplexImage pointwise with ComplexImage obtained by taking every l-th sample of another ComplexImage im along each dimension.
	* Dimensions of im have to be bigger or equal to dimensions of the ComplexImage multiplied by l.
	* @param im the ComplexImage to multiply with
	* @param l subsampling factor for im
	*/

	public void multiply(ComplexImage im, int l) {	
		if (l==1) {
			multiply(im);
		} else {
			int l2=l*l;
			double ima;
			double re;
			int d=l*im.nx;
			if (!(im.imag==null)) {  // im is complex
				if (imag==null) {   
					imag=new double[nxy];
				}
				for(int y=0,l2y=0;y<ny;y++,l2y+=d) {
					for(int t=l2y,k=nx*y,end=nx*y+nx;k<end;t+=l,k++) {    //x,y-coordinates in the image
						re=real[k];
						real[k]=im.real[t]*re-im.imag[t]*imag[k];
						imag[k]=im.real[t]*imag[k]+im.imag[t]*re;
					}   
				}
			} else {	// im is real
				if (imag==null) {
					for(int y=0,l2y=0;y<ny;y++,l2y+=d) {
						for(int t=l2y,k=nx*y,end=nx*y+nx;k<end;t+=l,k++) {    //x,y-coordinates in the image
							real[k]*=im.real[t];						
						}   
					}				
				} else {
					for(int y=0,l2y=0;y<ny;y++,l2y+=d) {
						for(int t=l2y,k=nx*y,end=nx*y+nx;k<end;t+=l,k++) {    //x,y-coordinates in the image
							real[k]*=im.real[t];
							imag[k]*=im.real[t];						
						}   
					}	
				}
			}
		}	
	}

	
	/**
	* Divides this ComplexImage by another ComplexImage im of same dimensions, pointwise.
	* Where both real and imaginary part of im are smaller then 10^-30 result is given by cr+i*cim.
	* @param im the ComplexImage to divide by
	* @param cr the real part of the result if division by zero 
	* @param cim the imaginary part of the result if division by zero 
	*/
	
	public void divide(ComplexImage im, double cr,double cim) {	
		double ima;
		double rea;
		double eps=1E-30;
		if (!(im.imag==null)) {		// im is complex
			if (imag==null) {
				imag=new double[nxy];
			}
			for(int k=0;k<nxy;k++) {	
				if ((Math.abs(im.real[k])<eps)&&(Math.abs(im.imag[k])<eps)) {
					real[k]=cr;
					imag[k]=cim; 	
				}
				else {
					rea=real[k];
					ima=imag[k];
					real[k]=(rea*im.real[k]+ima*im.imag[k])/(im.real[k]*im.real[k]+im.imag[k]*im.imag[k]);
					imag[k]=(ima*im.real[k]-rea*im.imag[k])/(im.real[k]*im.real[k]+im.imag[k]*im.imag[k]);
				}
			}    
		} else {	// im is real
			if (imag==null) {  // divide real by real
				for(int k=0;k<nxy;k++) {
					if ((Math.abs(im.real[k])<eps)) {
						real[k]=cr;
					} else {
						real[k]/=im.real[k];
					}
				} 
			} else {	// divide complex by real
				for(int k=0;k<nxy;k++) {
					if ((Math.abs(im.real[k])<eps)) {
						real[k]=cr;
						imag[k]=cim; 	
					} else {	
						real[k]/=im.real[k];
						imag[k]/=im.real[k];
					}
				} 
			}
		}
	}
	
	
	/**
	* Divides this ComplexImage by another ComplexImage im of the same dimensions, pointwise.
	* Does not check for division by zero.
	* @param im the ComplexImage to divide by
	*/
	
	public void divide(ComplexImage im) {	
		if (!(im.imag==null)) {		// im is complex
			if (imag==null) {
				imag=new double[nxy];
			}
			for(int k=0;k<nxy;k++) {
				double rea=real[k];
				double d=(im.real[k]*im.real[k]+im.imag[k]*im.imag[k]);
				real[k]=(rea*im.real[k]+imag[k]*im.imag[k])/d;
				imag[k]=(imag[k]*im.real[k]-rea*im.imag[k])/d;		
			}   
		} else {	// im is real
			if (imag==null) {  // divide real by real
				for(int k=0;k<nxy;k++) {
					real[k]/=im.real[k];
				} 
			} else {	// divide complex by real
				for(int k=0;k<nxy;k++) {
					real[k]/=im.real[k];
					imag[k]/=im.real[k];
				} 
			}
		}
	}
	
	
	/**
	* Computes the square root of each element of the real part of this ComplexImage and puts it in the real part of this ComplexImage.
	*/
	
	public void rootReal() {
		for(int k=0;k<nxy;k++) { 
			real[k]=Math.sqrt(real[k]);
		}
	}
	

	/**
	* Returns the median of the real part of this ComplexImage.
	* @return median of the real part of this ComplexImage
	*/

	public double median() {
		ComplexImage temp=copyImage();
		java.util.Arrays.sort(temp.real);
		double m=temp.real[nxy/2];	
		return m;
	}


	/**
	* Returns the median absolute deviation of the real part of this ComplexImage.
	* @return median absolute deviation of the real part of this ComplexImage
	*/

	public double mad() {
		double m=median();
		ComplexImage temp=copyImage();
		temp.add(-m);
		for (int x=0;x<nxy;x++) {
			temp.real[x]=Math.abs(temp.real[x]);
		}
		double md=temp.median();
		return md;
	}
	

	/**
	* Rises each element of the real part of this ComplexImage to the power of exp.
	* @param exp exponent
	*/

	public void powerReal(double exp) {
		for (int i=0;i<nxy;i++) {
			real[i]=Math.pow(real[i],exp);
		}
	}

	
	/**
	* Returns the mean value of the real part of this ComplexImage.
	* @return the mean of the real part of this ComplexImage
	*/

	public double meanReal() {	
		double d=0.0;
		for(int k=0;k<nxy;k++) {
			d+=real[k];
		}	
		d/=nxy;
		return d;
	}
	
	
	/**
	* Returns the mean of the real part of this ComplexImage inside the mask which is defined by real(ma)=val.
	* @param ma mask
	* @param val defines the mask foreground
	* @return the mean value of real part of this ComplexImage inside the mask 
	*/
	
	public double meanMask(int[] ma, int val) {
		double cnt=0;
		double s=0;
		for (int k=0; k<nxy;k++) {
			if(ma[k]==val) {
				cnt+=1.0;
				s+=real[k];
			}
		}
		s/=cnt;
		return s;
	}
	
		
	/**
	* Returns the mean value of this ComplexImage modulus.
	* @return mean of this ComplexImage modulus
	*/
	
	public double meanModulus() {	
		double d=0.0;
		if (imag==null) {
			for(int k=0;k<nxy;k++) { 
				d+=Math.abs(real[k]);
			}
		} else {
			for(int k=0;k<nxy;k++) { 
				d+=Math.pow(real[k]*real[k]+imag[k]*imag[k],0.5);
			}
		}
		d/=(nxy);
		return d;
	}
	
	
	/**
	* Computes the sum of the real part of this ComplexImage.
	* @return the sum of the real part of this ComplexImage
	*/
		
	public double sumReal() {	
		double d=0.0;
		for(int k=0;k<nxy;k++) {
			d+=real[k];
		}
		return d;
	}	
	

	/**
	* Returns the maximum absolute value of the real part of this ComplexImage.
	* @return the maximum absolute value of the real part of this ComplexImage
	*/
	
	public double maxAbsReal() {
		double max=real[0];
		double min=real[0];
		for(int k=0;k<nxy;k++) {
			if (real[k]>max) {
				max=real[k];
			}
			else {
				if (real[k]<min) {
					min=real[k];
				}
			}
		}
		max=Math.abs(max);
		min=Math.abs(min);
		if (min>max) {
			max=min;
		}
		return max;
	}
	
	
	/**
	* Returns the maximum value of the real part of this ComplexImage.
	* @return the maximum value of the real part of this ComplexImage 
	*/
	
	public double max() {
		double m=real[0];
		for(int k=0;k<nxy;k++) {
			if (real[k]>m) {
				m=real[k];
			}
		}
		return m;
	}
	
	
	/**
	* Returns the minimum value of the real part of this ComplexImage.
	* @return the minimum value of the real part of this ComplexImage 
	*/
	
	public double min() {
		double m=real[0];
		for(int k=0;k<nxy;k++) {
			if (real[k]<m) {
				m=real[k];
			}
		}
		return m;
	}
	
	
	/**
	* Returns the standard deviation of the real part of this ComplexImage.
	* @return the standard deviation of the real part of this ComplexImage
	*/
	
	public double deviation() {
		double m=meanReal();
		double s=0;
		for (int k=0;k<nxy;k++) {
			double d=real[k]-m;
			s+=(d*d);
		}
		s/=nxy;
		s=Math.sqrt(s);
		return s;
	}
		
	
	/**
	* Performs the 2D Fast Fourier Transform on this ComplexImage.
	*/

	public void FFT2D() {			
		//perform FFT1D for each row
		FFT1D FFTrow=new FFT1D(nx);
		ComplexImage row = new ComplexImage(nx,1);
		for(int y=0;y<ny;y++) {
			row.getRowContent(y,this);
			FFTrow.transform(row.real,row.imag,nx,0);
			putRow(y,row);  
		}	
		//perform FFT1D for each column
		ComplexImage column = new ComplexImage(1,ny);
		FFT1D FFTcolumn=new FFT1D(ny);
		for(int x=0;x<nx;x++) {
			column.getColumnContent(x,this);
			FFTcolumn.transform(column.real,column.imag,ny,0);
			putColumn(x,column);
		}	
	}

	
	/**
	* Performs the 2D inverse Fast Fourier Transform on this ComplexImage.
	*/
	
	public void iFFT2D() {	
		//perform iFFT1D for each row
		ComplexImage row = new ComplexImage(nx,1);
		FFT1D FFTrow=new FFT1D(nx);
		for(int y=0;y<ny;y++) {
			row.getRowContent(y,this);
			FFTrow.inverse(row.real,row.imag,nx,0);
			putRow(y,row);  
		}	
		//perform iFFT1D for each column
		ComplexImage column = new ComplexImage(1,ny);
		FFT1D FFTcolumn=new FFT1D(ny);
		for(int x=0;x<nx;x++) {
			column.getColumnContent(x,this);
			FFTcolumn.inverse(column.real,column.imag,ny,0);
			putColumn(x,column);
		}	
	}
	
		
	/**
	* Exchanges quadrants of this ComplexImage 1 <-> 3,  2 <-> 4.
	* The quadrants are defined as follows: <br>
	* 1 | 2 <br>
	* ----- <br>
	* 4 | 3 <br>
	*/
	
	public void shift() {
		double p;
		int q1;
		int q2;
		int q3;
		int q4;
		int nxy2=nxy/2;
		int nx2=nx/2;
		if (imag==null) {
			for(int y=0;y<ny/2;y++) {
				int ynx=nx*y;
				for(int x=0;x<nx2;x++) {
					q1=ynx+x;
					q2=q1+nx2;
					q4=q1+nxy2;
					q3=q4+nx2;
					p=real[q1];
					real[q1]=real[q3];
					real[q3]=p;   
					p=real[q2];
					real[q2]=real[q4];
					real[q4]=p;
				}
			}
		} else {
			for(int y=0;y<ny/2;y++) {
				int ynx=nx*y;
				for(int x=0;x<nx2;x++) {
					q1=ynx+x;
					q2=q1+nx2;
					q4=q1+nxy2;
					q3=q4+nx2;
					p=real[q1];
					real[q1]=real[q3];
					real[q3]=p;   
					p=real[q2];
					real[q2]=real[q4];
					real[q4]=p;
					p=imag[q1];
					imag[q1]=imag[q3];
					imag[q3]=p;   
					p=imag[q2];
					imag[q2]=imag[q4];
					imag[q4]=p;
				}
			}
		}
	}
	
	
	/**
	* Exchanges quadrants of this ComplexImage 1 <-> 2,  3 <-> 4.
	* The quadrants are defined as follows: <br>
	* 1 | 2 <br>
	* ----- <br>
	* 4 | 3 <br>
	*/
	
	public void shiftX() {
		double p;
		int q1;
		int q2;
		int nx2=nx/2;
		if (imag==null) {
			for(int y=0;y<ny;y++) {
				int ynx=nx*y;
				for(int x=0;x<nx2;x++) {
					q1=ynx+x;
					q2=q1+nx2;
					p=real[q1];
					real[q1]=real[q2];
					real[q2]=p;   
				}
			}
		} else {
			for(int y=0;y<ny;y++) {
				int ynx=nx*y;
				for(int x=0;x<nx2;x++) {
					q1=ynx+x;
					q2=q1+nx2;
					p=real[q1];
					real[q1]=real[q2];
					real[q2]=p;   
					p=imag[q1];
					imag[q1]=imag[q2];
					imag[q2]=p;   
				}
			}
		}
	}

	
	/**
	* Exchanges quadrants of this ComplexImage 1 <-> 4,  2 <-> 3.
	* The quadrants are defined as follows: <br>
	* 1 | 2 <br>
	* ----- <br>
	* 4 | 3 <br>
	*/

	public void shiftY() {
		final int halfim=nxy/2;
		double p;
		if (imag==null) {
			for(int k=0;k<halfim;k++) {
				p=real[k];
				real[k]=real[k+halfim];
				real[k+halfim]=p;   
			}
		} else {
			for(int k=0;k<halfim;k++) {
				p=real[k];
				real[k]=real[k+halfim];
				real[k+halfim]=p;   
				p=imag[k];
				imag[k]=imag[k+halfim];
				imag[k+halfim]=p;   
			}
		}
	}	
	
	
	/**
	* Shifts this ComplexImage right by shiftx and down by shifty.
	* @param shiftx shift to the right
	* @param shifty shift down
	*/
		
	public ComplexImage circShift(int shiftx, int shifty) {
		while (shiftx<0) {
			shiftx=nx+shiftx;
		}
		while (shifty<0) {
			shifty=ny+shifty;
		}
		while(shiftx>(nx-1)) {
			shiftx=shiftx-nx;
		}
		while(shifty>(ny-1)) {
			shifty=shifty-ny;
		}
		ComplexImage res=new ComplexImage(nx,ny);		
		ComplexImage temp=getSubimage(0,nx-shiftx-1,0,ny-shifty-1);
		res.putSubimage(shiftx,shifty,temp);	
		temp=getSubimage(nx-shiftx,nx-1,ny-shifty,ny-1);
		res.putSubimage(0,0,temp);
		temp=getSubimage(0,nx-shiftx-1,ny-shifty,ny-1);
		res.putSubimage(shiftx,0,temp);
		temp=getSubimage(nx-shiftx,nx-1,0,ny-shifty-1);
		res.putSubimage(0,shifty,temp);	
		return res;
	}
	

	/**
	* Performs quincunx downsampling followed by upsampling in Fourier domain on this ComplexImage.
	* Sums the first quadrant with the third and the second with the forth. <br>
	* 1 | 2 <br>
	* ----- <br>
	* 4 | 3 <br>
	*/

	public void quincunxDownUp() {		
		int nx2=nx/2;
		int ny2=ny/2;
		int nxy2=nxy/2;
		for (int y=0;y<nxy2;y+=nx) {
			for (int q1=y,end=q1+nx2;q1<end;q1++) {
				int q2=q1+nx2;
				int q4=q1+nxy2;
				int q3=q4+nx2;
				double r=real[q1]+real[q3];
				double im=imag[q1]+imag[q3];
				real[q1]=real[q3]=r;
				imag[q1]=imag[q3]=im;
				r=real[q2]+real[q4];
				im=imag[q2]+imag[q4];
				real[q2]=real[q4]=r;
				imag[q2]=imag[q4]=im;
			}
		}	
	}
	
	
	/**
	* Down and upsampling in Y direction
	* Sums the first quadrant with the forth and the third with the third. <br>
	* 1 | 2 <br>
	* ----- <br>
	* 4 | 3 <br>
	*/

	public void downUpY() {		
		int nxy2=nxy/2;
		for (int q1=0;q1<nxy2;q1++) {
			int q2=q1+nxy2;
			double r=real[q1]+real[q2];
			double im=imag[q1]+imag[q2];
			real[q1]=real[q2]=r;
			imag[q1]=imag[q2]=im;
		}	
	}
	
	
	/**
	* Performs dyadic downsampling followed by upsampling in Fourier domain on this ComplexImage. 
	* Sums the four quadrants pointwise and places the sum in each quadrant.
	*/
	
	public void dyadicDownUp() {		
		int nx2=nx/2;
		int ny2=ny/2;
		int nxy2=nxy/2;
		for (int y=0;y<nxy2;y+=nx) {
			for (int q1=y,end=q1+nx2;q1<end;q1++) {
				int q2=q1+nx2;
				int q3=q1+nxy2;
				int q4=q3+nx2;
				double r=real[q1]+real[q2]+real[q3]+real[q4];
				real[q1]=real[q2]=real[q3]=real[q4]=r;
				double im=imag[q1]+imag[q2]+imag[q3]+imag[q4];
				imag[q1]=imag[q2]=imag[q3]=imag[q4]=im;
			}
		}
	}
	

	/**
	* Sums the four quadrants of this ComplexImage pointwise and reduces its size by 2 along each dimension.
	*/
		
	public void dyadicDownUpCrop() {
		int nx2=nx/2;
		int ny2=ny/2;
		int nxy2=nxy/2;
		int ind=0;
		for (int y=0;y<nxy2;y+=nx) {
			for (int q1=y,end=q1+nx2;q1<end;q1++) {
				int q2=q1+nx2;
				int q3=q1+nxy2;
				int q4=q3+nx2;
				double r=real[q1]+real[q2]+real[q3]+real[q4];
				real[ind]=r;
				double im=imag[q1]+imag[q2]+imag[q3]+imag[q4];
				imag[ind++]=im;
			}
		}
		nx=nx2;
		ny=ny2;
		nxy/=4;
	}
		
	
	/**
	* Sums the lower half of this ComplexImage to the upper half, pointwise. Keeps only the upper half.
	*/
	
	public void dyadicDownY() {
		ComplexImage temp=getSubimage(0,nx-1,0,ny/2-1);
		ComplexImage temp1=getSubimage(0,nx-1,ny/2,ny-1);
		temp1.add(temp);
		putSubimage(0,0,temp1);
		ny=ny/2;
		nxy/=2;
	}
	
	
	/**
	* Subsamples this ComplexImage by 2x2, reducing its size by 2 along each dimension.
	*/
	
	public void decimateCrop() {
		int nx2=nx/2;
		int ny2=ny/2;
		if (imag==null) {
			for (int y=0;y<ny2;y++) {
				for (int x=0,k=2*nx*y,k1=nx2*y;x<nx2;x++,k+=2,k1++) {	 
					real[k1]=real[k];
				}
			}
		} else {
			for (int y=0;y<ny2;y++) {
				for (int x=0,k=2*nx*y,k1=nx2*y;x<nx2;x++,k+=2,k1++) {	 
					real[k1]=real[k];
					imag[k1]=imag[k];
				}
			}
		}
		nx/=2;
		ny/=2;
		nxy/=4;
	}
	
	
	/**
	* Subsamples this ComplexImage by 2x2, circularily, while keeping its size unchanged.
	*/
	
	public void decimate() {
		ComplexImage temp=copyImage();
		if (imag==null) {
			for(int l=0, y=0;l<nxy;l+=nx,y+=2) {
				if (y>=ny) {
					y-=ny;
				}	
				for(int k=l,x=0;k<l+nx;k++,x+=2) {
					if (x>=nx) {
						x-=nx;
					}
					int k1=y*nx+x;
					real[k]=temp.real[k1];
				}
			}
		} else {
			for(int l=0, y=0;l<nxy;l+=nx,y+=2) {
				if (y>=ny) {
					y-=ny;
				}	
				for(int k=l,x=0;k<l+nx;k++,x+=2) {
					if (x>=nx) {
						x-=nx;
					}
					int k1=y*nx+x;
					real[k]=temp.real[k1];
					imag[k]=temp.imag[k1];
				}
			}
		}
	}
	
	
	/**
	* Substitutes the 2k-th and the 2k+1-th column of this ComplexImage by their sum, for each integer k.
	* The number of columns of the ComplexImage is reduced by two.
	*/
	
	public void fold() {
		int nxy2=nxy/2;
		for (int k=0;k<nxy2;k++) {
			real[k]=real[2*k]+real[2*k+1];
			imag[k]=imag[2*k]+imag[2*k+1];
		}
		nx/=2;
		nxy/=2;
	}
	
	
	/**
	* Substitutes each column of this ComplexImage by two columns. Each of them contains the even (odd) samples from the initial column and the odd (even) positions contain zeros. 
	* The number of columns of the ComplexImage is increased by two.
	*/
	
	public void unfold() {
		double[] real1=new double[2*nxy];
		double[] imag1=new double[2*nxy];
		for(int k=0;k<2*nxy;k++) 
			real1[k]=imag1[k]=0;
		for(int k=0;k<nx*ny;k++) {
			if (k%(2*nx)<nx) {    //odd row
				real1[2*k]=real[k];
				imag1[2*k]=imag[k];
			}
			else {
				real1[2*k+1]=real[k];
				imag1[2*k+1]=imag[k];
			}
		}
		real=real1;
		imag=imag1;
		nx=nx*2;	
		nxy*=2;				
	}


	/**
	* Extends this ComplexImage circularily to double its size along each direction. <br>
	*/
	
	public void dyadicUpsample() {
		int index;
		int q1;
		int q2;
		int q3;
		int q4;
		int nx2=2*nx;
		int nxy2=2*nxy;
		double r;
		double i;
		if (real.length<4*nxy) {
			double[] re=new double[4*nxy];
			System.arraycopy(real,0,re,0,nxy);
			real=re;
		}
		if (imag.length<4*nxy) {
			double[] im=new double[4*nxy];
			System.arraycopy(imag,0,im,0,nxy);
			imag=im;
		}
		for(int y=ny-1;y>=0;y--) {
			int ynx=y*nx;
			int ynx2=2*ynx;
			for(int x=nx-1;x>=0;x--) {
				index=ynx+x;
				r=real[index];
				i=imag[index];
				q1=ynx2+x;
				real[q1]=r;   // First quadrant
				imag[q1]=i;
				q2=q1+nx;
				real[q2]=r;   // Second quadrant
				imag[q2]=i;
				q3=q1+nxy2;
				real[q3]=r;   //Third quadrant
				imag[q3]=i;
				q4=q3+nx;
				real[q4]=r;  // Forth quadrant
				imag[q4]=i;
			}
		}
		nx*=2;
		ny*=2;
		nxy*=4;
	}
	
	
	/**
	* Multiplies this ComplexImage by e^ix, with x=2*pi*kx/nx, where kx is the column index.
	*/
	
	public void modulatePlusX() {
		if (imag==null) {
			imag=new double[nxy];
		}
		int index;
		double x;
		double c;
		double s;
		double re;
		double im;
		for(int k=1;k<nx;k++) {
			x=PI2*(double)k/(double)nx;
			c=Math.cos(x);
			s=Math.sin(x);
			for(int l=0;l<ny;l++) {
				index=l*nx+k;
				re=real[index];
				im=imag[index];		
				real[index]=c*re-s*im;
				imag[index]=s*re+c*im;							
			}
		}
	}
	
	
	/**
	* Multiplies this ComplexImage by e^-ix, with x=2*pi*kx/nx, where kx is the column index.
	*/
	
	public void modulateMinusX() {
		if (imag==null) {
			imag=new double[nxy];
		}
		int index;
		double x;
		double c;
		double s;
		double re;
		double im;
		for(int k=1;k<nx;k++) {
			x = (double)k*PI2/(double)nx;
			c=Math.cos(x);
			s=Math.sin(x);
			for(int l=0;l<ny;l++) {
				index = l*nx+k;
				re=real[index];
				im=imag[index];
				real[index]=c*re+s*im;
				imag[index]=c*im-s*re;
			}
		}
	}
	
	
	/**
	* Multiplies this ComplexImage by e^iy, with y=2*pi*ky/ny, where ky is the row index.
	*/
	
	public void modulatePlusY() {
		if (imag==null) {
			imag=new double[nxy];
		}
		int index;
		double y;
		double c;
		double s;
		double re;
		double im;
		int knx;
		for(int k=1;k<ny;k++) {
			y=PI2*(double)k/(double)ny;
			c=Math.cos(y);
			s=Math.sin(y);
			knx=k*nx;
			for(int l=0;l<nx;l++) {
				index=knx+l;
				re=real[index];
				im=imag[index];		
				real[index]=c*re-s*im;
				imag[index]=s*re+c*im;									
			}
		}
	}
	
	
	/**
	* Multiplies this ComplexImage by e^-iy, with y=2*pi*ky/ny, where ky is the row index.
	*/
	
	public void modulateMinusY() {
		if (imag==null) {
			imag=new double[nxy];
		}
		int index;
		double y;
		double c;
		double s;
		double re;
		double im;
		int knx;
		for(int k=1;k<ny;k++) {
			y=PI2*(double)k/(double)ny;
			c=Math.cos(y);
			s=Math.sin(y);	
			knx=k*nx;
			for(int l=0;l<nx;l++) {
				index=knx+l;
				re=real[index];
				im=imag[index];					
				real[index]=c*re+s*im;
				imag[index]=c*im-s*re;										
			}
		}
	}
	
	
	/**
	* Multiplies this ComplexImage by e^i(x+y), with x=2*pi*kx/nx, where kx is the column index, and y=2*pi*ky/ny, where ky is the row index.
	*/
	
	public void modulatePlusQuincunx() {
		if (imag==null) {
			imag=new double[nxy];
		}
		int index;
		double x;
		double c;
		double s;
		double re;
		double im;
		for(int k=0;k<nx;k++) {	
			for(int l=0;l<ny;l++) {
				x=PI2*( (double)k/(double)nx+(double)l/(double)ny );
				c=Math.cos(x);
				s=Math.sin(x);
				index=l*nx+k;
				re=real[index];
				im=imag[index];
				real[index]=c*re-s*im;
				imag[index]=s*re+c*im;
			}
		}
	}
	
	
	/**
	* Multiplies this ComplexImage by e^-i(x+y), with x=2*pi*kx/nx, where kx is the column index, and y=2*pi*ky/ny, where ky is the row index.
	*/
	
	public void modulateMinusQuincunx() {
		if (imag==null) {
			imag=new double[nxy];
		}
		int index;
		double x;
		double c;
		double s;
		double re;
		double im;
		for(int k=0;k<nx;k++) {
			for(int l=0;l<ny;l++) {
				x=PI2*( (double)k/(double)nx+(double)l/(double)ny );
				c=Math.cos(x);
				s=Math.sin(x);
				index = l*nx+k;
				re=real[index];
				im=imag[index];
				real[index]=c*re+s*im;
				imag[index]=c*im-s*re;
			}
		}
	}
		
	
	/**
	* Returns this ComplexImage rotated by 45 degrees clockwise.
	* Only the ComplexImage samples on the quincunx grid are kept, the ComplexImage is rotated so that the remaining samples are all in the same region, and the rest of the output ComplexImage is filled by the background.
	* Size of rotated image is bigger or equal to image size before rotation
	* @param back the background color to be used.
	* @return the rotated ComplexImage
	*/

	public ComplexImage rotate(double back) {	 	
		double s1=0;
		double s0=0;
		int k=0;
		int i=1;
		for (int y=0;y<nxy;y+=nx) {
			for (int ind=y+k,end=y+nx;ind<end;ind+=2) {	
				s0+=Math.abs(real[ind]);
				s1+=Math.abs(real[ind+i]);
			}
			k=1-k;
			i=-i;
		}
		k=(s0<s1)?1:0;	
		int s=(nx+ny)/2;
		ComplexImage rot=new ComplexImage(s,s);
		rot.settoConstant(back,back);
		for (int y=0;y<ny;y++) {
			for (int x=k,ind=y*nx+k;x<nx;x+=2,ind+=2) {
				int x1=(x-y+ny-1)/2;
				int y1=(x+y)/2;
				rot.real[rot.nx*y1+x1]=real[ind];
				rot.imag[rot.nx*y1+x1]=imag[ind];
			}
			k=1-k;
		}
		return rot;
	}	


	/**
	* Rotates this ComplexImage back, anticlockwise by 45 degrees.
	* After applying the rotate operation followed by the unrotate, only the samples on the quincunx grid are kept.
	* @param kx the number of rows in the unrotated ComplexImage
	* @param ky the number of columns in the unrotated ComplexImage
	*/

	public void unrotate(int kx,int ky) {		
		int kxy=kx*ky;
		int nx1=nx;
		int ny1=ny;
		double[] re=new double[kxy];
		double[] im=new double[kxy];
		nx=kx;
		ny=ky;
		nxy=nx*ny;
		int k=0;
		for (int y=0;y<ky;y++) {
			for (int x=k,ind=kx*y+k;x<kx;x+=2,ind+=2) {
				int x1=(x-y+ky-1)/2;
				int y1=(x+y)/2;
				re[ind]=real[nx1*y1+x1];
				im[ind]=imag[nx1*y1+x1];
			}
			k=1-k;
		}
		System.arraycopy(re,0,real,0,kxy);
		System.arraycopy(im,0,imag,0,kxy);	
	}	

	
	/**
	* Places the ComplexImage original extended by its first row and first column in this ComplexImage.
	* @param original the image to extend
	*/

	public void extend(ComplexImage original) {	
		putSubimage(0,0,original);
		for(int x=0;x<original.nx;x++) {
			real[(original.nx+1)*original.ny+x]=real[x];
		}
		for(int y=0;y<original.ny+1;y++) { 
			real[(original.nx+1)*y+original.nx]=real[(original.nx+1)*y];
		}
	}


	/**
	* Normalizes the modulus of this ComplexImage so that all values are in the range (0,250). 
	* Does not modify the background given by the value 255.0 in the real part. 
	*/ 
	
	public void stretch() {
		double max=250.0;
		double back=255.0;
		ComplexImage mod=copyImage();
		if (imag==null) {
			double maximage=mod.maxAbsReal();
			double sp=max/maximage;
			for (int i=0;i<nxy;i++) {
				if (real[i]!=back) {
					real[i]*=sp;
				}	
			}
		} else {
			mod.modulus();
			double maximage=mod.max();
			double sp=max/maximage;
			for (int i=0;i<nxy;i++) {
				if (real[i]!=back) {
					real[i]*=sp;
					imag[i]*=sp;
				}	
			}
		}
	}
								
		
	/**
	* Stretches each subband of the nonredundant quincunx transform in this ComplexImage so that all values are between -250 and 250.
	* @param J number of decomposition levels
	*/

	void displayQuincunxNonredundant(int J) {
		int dx=nx;
		int dy=ny;
		for(int j=1;j<=J;j++) {
			ComplexImage sub;
			if (j%2==1) {   // Odd iteration
				sub=getSubimage(dx/2,dx-1,0,dy-1);	
				sub.stretch();
				putSubimage(dx/2,0,sub);
				dx/=2;	
			}
			else {
				sub=getSubimage(0,dx-1,dy/2,dy-1);	
				sub.stretch();
				putSubimage(0,dy/2,sub);
				dy/=2;
			}
		}
	}
	
	
	/**
	* Puts a frame of the given color around the real part of this ComplexImage.
	* @param color the frame color
	*/
	
	public void frame(double color) {
		for(int k=0,nxy1=nxy-1;k<nx;k++) {
			real[k]=real[nxy1-k]=color;	
		}
		for(int k=1;k<ny;k++) {
			real[k*nx]=real[k*nx-1]=color;	
		}
	}
		
	
	/**
	* Puts the frames of given colors around both the real and the complex part of this ComplexImage.
	* @param colorr the frame color for the real part
	* @param colori the frame color for the complex part
	*/
	
	public void frame(double colorr, double colori) {
		for(int k=0,nxy1=nxy-1;k<nx;k++) {
			real[k]=real[nxy1-k]=colorr;
			imag[k]=imag[nxy1-k]=colori;	
		}
		for(int k=1;k<ny;k++) {
			real[k*nx]=real[k*nx-1]=colorr;
			imag[k*nx]=imag[k*nx-1]=colori;	
		}
	}
		
	
	/**
	* Extends this ComplexImage by adding zero rows and columns around the edges.
	*/
	
	public void extendWithZeros() {
		ComplexImage temp=new ComplexImage(nx+2,ny+2);
		ComplexImage temp1=copyImage();
		temp.putSubimage(1,1,temp1);
		real=temp.real;
		imag=temp.imag;
		nx+=2;
		ny+=2;
		nxy=nx*ny;
	}

	
	/**
	* Removes the first and the last row and coulumn of this ComplexImage.
	*/
	
	public void reduce() {
		ComplexImage temp=getSubimage(1,nx-2,1,ny-2);
		real=temp.real;
		imag=temp.imag;
		nx-=2;
		ny-=2;
		nxy=nx*ny;
	}
	
	
	/**
	* Sets this ComplexImage value to 1 where it is different from 0.
	*/
	
	public void createMap() { 
		for(int i=0;i<nxy;i++) {
			if (!(real[i]==0.0)) {
				real[i]=1.0;
			}
		}	
	}
		

	/**
	* Implements soft threshold on this ComplexImage. 
	* Where the modulus of the ComplexImage is bigger then t it is reduced by t while the phase of the ComplexImage remains unchanged. Elsewhere, the value is set to zero.
	* @param t threshold
	*/

	public void softThreshold(double t) {
		double t2=t*t;
		for (int k=0;k<nxy;k++) {	
			if ((real[k]*real[k]+imag[k]*imag[k])<t2) {
				real[k]=imag[k]=0;
			} else {
				double r=real[k];
				double im=imag[k];
				double m=Math.sqrt(r*r+im*im);
				real[k]-=r/m;
				imag[k]-=im/m;
			}
		}
	}
	

	/**
	* Implements hard threshold on this ComplexImage.
	* Where the modulus of the ComplexImage is smaler then t the value it is set to zero.
	* @param t the threshold
	*/

	public void hardThreshold(double t) {
		double t2=t*t;
		for (int k=0;k<nxy;k++) {
			if ((real[k]*real[k]+imag[k]*imag[k])<t2) {
				real[k]=imag[k]=0.0;
			}		
		}
	}
	
	/**
	* Sets the real part of this ComplexImage to max where it is higher then max and to min where it is smaller then min.
	* @param min the lower limit
	* @param max the upper limit
	*/
	
	public void limitImage(double min, double max) {
		for(int i=0;i<nxy;i++) {
			if (real[i]>max) {
				real[i]=max;
			}
			else {
				if (real[i]<min) {
					real[i]=min;
				}
			}
		}
	}
	
	
	/*
	* Searches for neighbouring pixels to include in the zerocrossings map
	*/
		
	private void search8(int x, int y) {
		if ((x<nx)&&(x>=0)&&(y<ny)&&(y>=0)) {
			int i=y*nx+x;
			if(real[i]==1.0) {
				real[i]=3.0;
				search8(x+1,y);
				search8(x-1,y);
				search8(x,y+1);
				search8(x,y-1);
				search8(x+1,y+1);
				search8(x-1,y-1);
				search8(x-1,y+1);
				search8(x+1,y-1);
			}
		}
	}
	
	
	/*
	* Creates the zerocrossing map from the primary map
	*/	
	
	private void zeroCrossHysteresis8() {
		for(int y=0;y<ny;y++) {
			int i=y*nx;
			for(int x=0;x<nx;x++,i++) {
				if (real[i]==2.0) {
					search8(x+1,y);
					search8(x-1,y);
					search8(x,y+1);
					search8(x,y-1);
					search8(x+1,y+1);
					search8(x-1,y-1);
					search8(x-1,y+1);
					search8(x+1,y-1);
				}
			}
		}	
		for(int x=0;x<nxy;x++) {
			if (real[x]<1.5) {
				real[x]=0.0;
			}		
			else {
				real[x]=1.0;
			}	
		}
	}
	
	
	/**
	* Performs Canny edge detection on this ComplexImage and keeps only the pixels that belong to the edge map. The edge of the image is included in the map.
	* @param Tl the lower threshold
	* @param Th the higher threshold
	* @return the percentage of pixels that belong to the edge map, excluding the edge
	*/

	public double canny(double Tl, double Th) {
		ComplexImage mod=copyImage();
		mod.modulus();		
		// determin directions
		int[] dir=new int[nxy]; // Directions of gradient 0,1,2,3
		for (int x=0;x<nxy;x++) {
			if (((imag[x]<=0.0)&&(real[x]>-imag[x]))||((imag[x]>=0.0)&&(real[x]<-imag[x]))) {
				dir[x]=0;
			}
			if (((real[x]>0.0)&&(-imag[x]>=real[x]))||((real[x]<0.0)&&(real[x]>=-imag[x]))) {
				dir[x]=1;
			}			
			if (((real[x]<=0.0)&&(real[x]>imag[x]))||((real[x]>=0.0)&&(real[x]<imag[x]))) {
				dir[x]=2;
			}	
			if (((imag[x]<0.0)&&(real[x]<=imag[x]))||((imag[x]>0.0)&&(real[x]>=imag[x]))) {
				dir[x]=3;
			}
		}
		// Nonmaxima supression
		ComplexImage mod1=mod.copyImage();
		int[][] neighbours={{nx,nx-1,-nx,-nx+1 },{-1,nx-1,1,-nx+1},{-1,-nx-1,1,nx+1},{-nx,-nx-1,nx,nx+1}};
		//exclude edges
		for (int x=0,x1=nxy-1;x<nx;x++,x1--) {
			mod1.real[x]=mod1.real[x1]=0.0;
		}
		for (int x=0,x1=nxy-1;x<nxy;x+=nx,x1-=nx) {
			mod1.real[x]=mod1.real[x1]=0.0;
		}
		for (int x=nx+1;x<nxy-nx-1;x++) {
			double d=Math.abs(imag[x]/real[x]);
			if (d>1.0) {
				d=1.0/d;
			}
			double ss1=mod.real[x+neighbours[dir[x]][0]]*(1.0-d)+mod.real[x+neighbours[dir[x]][1]]*d;
			double ss2=mod.real[x+neighbours[dir[x]][2]]*(1.0-d)+mod.real[x+neighbours[dir[x]][3]]*d;
			if(!((mod.real[x]>ss1)&&(mod.real[x]>ss2))) {
				mod1.real[x]=0.0;
			}
		}
		// Hysteresis
		// Form a map
		ComplexImage map=new ComplexImage(nx,ny);
		for(int x=0;x<nxy;x++) {
			if (mod1.real[x]>Th) {
				map.real[x]=2.0;
			} else {
				if (mod1.real[x]>Tl) {
					map.real[x]=1.0;
				}
			}
		}	
		map.zeroCrossHysteresis8();	
		double pr=map.sumReal()/map.nxy;
		map.frame(1.0);
		multiply(map);	
		return pr;
	}	


	/**
	* Differentiates the real part of this ComplexImage along x. 
	*/
	
	public void derivativeX() {
		ComplexImage temp=new ComplexImage(nx,ny);
		System.arraycopy(real,0,temp.real,0,nxy);	
		temp.FFT2D();
		for(int k=0;k<nx;k++) {
			double x=PI2*(double)k/(double)nx;
			if(x>Math.PI) {
				x-=PI2;
			}
			for(int l=0;l<ny;l++) {
				int index=l*nx+k;
				temp.real[index]*=x;	
				temp.imag[index]*=x;					
			}
		}	
		temp.conj();
		System.arraycopy(temp.real,0,imag,0,nxy);
		System.arraycopy(temp.imag,0,real,0,nxy);		
		iFFT2D();
	}
	

	/**
	* Differentiates the real part of this ComplexImage along y. 
	*/
	
	public void derivativeY() {
		ComplexImage temp=new ComplexImage(nx,ny);
		System.arraycopy(real,0,temp.real,0,nxy);
		temp.FFT2D();
		for(int k=0;k<ny;k++) {
			double y=PI2*(double)k/(double)ny;
			if(y>Math.PI) {
				y-=PI2;
			}
			for(int l=0;l<nx;l++) {
				int index=k*nx+l;
				temp.real[index]*=y;	
				temp.imag[index]*=y;			
			}
		}	
		temp.conj();
		System.arraycopy(temp.real,0,imag,0,nxy);
		System.arraycopy(temp.imag,0,real,0,nxy);
		iFFT2D();
	}
	
	
	/**
	* Smooth the real part of this ComplexImage.
	* The value of each pixel is computed as the mean value of its 3x3 neighbourhood. 
	*/
	
	public void smooth() {
		// Define neighbour
		int[] d={1,-1,nx,-nx,nx+1,nx-1,-nx+1,-nx-1,0};
		double[] w1={1.0,1.0,1.0,1.0,1.0,1.0,1.0,1.0,1.0}; // additional weights that depend on position
		ComplexImage temp=copyImage();
		for(int i=nx+1;i<nxy-nx-1;i++) {
			double m=0.0;
			double s=0.0;
			for (int k=0;k<9;k++) {
				double w=w1[k];
				s+=w;     // normalization
				m+=(temp.real[i+d[k]]*w);
			}
			m/=s;
			real[i]=m;
		}
	}


	/**
	* Smooth the real part of this ComplexImage using the given weights.
	* The value of each pixel is computed as the mean value of its weighted 3x3 neighbourhood. 
	* @param weights the weights, a Compleximage of the same size as this ComplexImage
	*/
	
	public void smooth(ComplexImage weights) {
		// Define neighbour
		int[] d={1,-1,nx,-nx,nx+1,nx-1,-nx+1,-nx-1,0};
		double[] w1={1.0,1.0,1.0,1.0,1.0,1.0,1.0,1.0,1.0}; // additional weights that depend on position
		ComplexImage temp=copyImage();
		for(int i=nx+1;i<nxy-nx-1;i++) {
			double m=0.0;
			double s=0.0;
			for (int k=0;k<9;k++) {
				double w=weights.real[i+d[k]]*w1[k];
				s+=w;     // normalization
				double t=temp.real[i+d[k]];
				m+=(t*w);
			}
			m/=s;
			real[i]=m;
		}
	}
	
	
	/**
	* Computes the first parameter of Riesz transform of this ComplexImage.
	*/
	
	public void riesz1() {
		FFT2D();
		ComplexImage mult1=new ComplexImage(nx,ny); 		
		for(int y=0;y<ny;y++) {
			double omy=2.0*Math.PI*(double)y/(double)ny-Math.PI;
			for(int x=0;x<nx;x++) {
				double omx=2.0*Math.PI*(double)x/(double)nx-Math.PI;
				mult1.real[nx*y+x]=1.0/Math.sqrt(omx*omx+omy*omy);	
				mult1.real[nx*y+x]*=omx;	
			}
		}			
		mult1.shift();
		mult1.real[0]=1.0;										
		multiply(mult1);
		iFFT2D();										
		double[] temparray=new double[nxy];
		System.arraycopy(real,0,temparray,0,nxy);
		System.arraycopy(imag,0,real,0,nxy);
		System.arraycopy(temparray,0,imag,0,nxy);
		conj();	
	}


	/**
	* Computes the second parameter of Riesz transform of this ComplexImage.
	*/
	
	public void riesz2() {
		FFT2D();
		ComplexImage mult1=new ComplexImage(nx,ny); 		
		for(int y=0;y<ny;y++) {
			double omy=2.0*Math.PI*(double)y/(double)ny-Math.PI;
			for(int x=0;x<nx;x++) {
				double omx=2.0*Math.PI*(double)x/(double)nx-Math.PI;
				mult1.real[nx*y+x]=1.0/Math.sqrt(omx*omx+omy*omy);	
				mult1.real[nx*y+x]*=omy;	
			}
		}			
		mult1.shift();
		mult1.real[0]=1.0;										
		multiply(mult1);
		double[] temparray=new double[nxy];
		System.arraycopy(real,0,temparray,0,nxy);
		System.arraycopy(imag,0,real,0,nxy);
		System.arraycopy(temparray,0,imag,0,nxy);
		conj();
		iFFT2D();										
	}
}

