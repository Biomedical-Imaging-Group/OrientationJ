//=============================================================================================================
//
// Project: Directional Image Analysis - OrientationJ plugins
// 
// Author: Daniel Sage
// 
// Organization: Biomedical Imaging Group (BIG)
// Ecole Polytechnique Federale de Lausanne (EPFL), Lausanne, Switzerland
//
// Information: 
// OrientationJ: http://bigwww.epfl.ch/demo/orientation/
// MonogenicJ: http://bigwww.epfl.ch/demo/monogenic/
//  
// Reference on methods and plugins
// Z. Püspöki, M. Storath, D. Sage, M. Unser
// Transforms and Operators for Directional Bioimage Analysis: A Survey 
// Advances in Anatomy, Embryology and Cell Biology, vol. 219, Focus on Bio-Image Informatics 
// Springer International Publishing, ch. 33, 2016.
//
//
// Reference the application measure of coherency
// R. Rezakhaniha, A. Agianniotis, J.T.C. Schrauwen, A. Griffa, D. Sage, 
// C.V.C. Bouten, F.N. van de Vosse, M. Unser, N. Stergiopulos
// Experimental Investigation of Collagen Waviness and Orientation in the Arterial Adventitia 
// Using Confocal Laser Scanning Microscopy
// Biomechanics and Modeling in Mechanobiology, vol. 11, no. 3-4, 2012.

// Reference the application direction of orientation
// E. Fonck, G.G. Feigl, J. Fasel, D. Sage, M. Unser, D.A. Ruefenacht, N. Stergiopulos 
// Effect of Aging on Elastin Functionality in Human Cerebral Arteries
// Stroke, vol. 40, no. 7, 2009.
//
// Conditions of use: You are free to use this software for research or
// educational purposes. In addition, we expect you to include adequate
// citations and acknowledgments whenever you present or publish results that
// are based on it.
//
//=============================================================================================================

package orientation.riesz;

import ij.IJ;
import orientation.fft.ComplexSignal;
import orientation.imageware.Builder;
import orientation.imageware.ImageWare;

public class RieszTransform {

	private int nx;
	private int ny;
	private RieszFilter filter;
	
	public RieszTransform(int nx, int ny, int order, boolean cancelDC) {
		this.nx = nx;
		this.ny = ny;
		filter = new RieszFilter(nx, ny, order, cancelDC);
	}
	
	/**
	*/
	public ImageWare[] analysis(ImageWare image) {
		int N = filter.getChannels();
		double[] in = (image.convert(ImageWare.DOUBLE)).getSliceDouble(0);
		ComplexSignal sin = new ComplexSignal(in, nx, ny);
		ComplexSignal fin = orientation.fft.FFT2D.transform(sin);
		
		ImageWare channelsReal[] = new ImageWare[N];
	
		for(int k=0; k<N; k++) {
			channelsReal[k] = Builder.create(nx, ny, 1, ImageWare.DOUBLE);
			ComplexSignal fcurr = filter.getAnalysis(k);
			ComplexSignal fg = ComplexSignal.multiply(fin, fcurr);
			ComplexSignal g = orientation.fft.FFT2D.inverse(fg);
			storeReal(g, channelsReal[k]);
		}
		return channelsReal;
	}
	
	/**
	*/
	public ImageWare synthesis(ImageWare channels[]) {
		int N = filter.getChannels();
		if (N != channels.length) {
			IJ.error("Not compatible stack of images for inverting Riesz Transform");
			return null;
		}

		ComplexSignal csum = new ComplexSignal(nx, ny);
			
		for(int k=0; k<N; k++) {
			ComplexSignal fc = new ComplexSignal(channels[k].getSliceDouble(0), nx, ny);
			ComplexSignal fg = orientation.fft.FFT2D.transform(fc);
			ComplexSignal fi = ComplexSignal.multiply(fg, filter.getSynthesis(k));
			for(int i=0; i<nx*ny; i++) {
				csum.real[i] += fi.real[i];
				csum.imag[i] += fi.imag[i];
			}
		}
		ComplexSignal rsum = orientation.fft.FFT2D.inverse(csum);
		ImageWare out = Builder.create(nx, ny, 1, ImageWare.DOUBLE);
		storeReal(rsum, out);
		return out;
	}

	/**
	*/
	public RieszFilter getFilters() {
		return filter;
	}
		
	/**
	*/
	private void storeReal(ComplexSignal signal, ImageWare channel) {
		int nx = channel.getWidth();
		int ny = channel.getHeight();
		double data[] = channel.getSliceDouble(0);
		System.arraycopy(signal.real, 0, data, 0, nx*ny);
	}

}
