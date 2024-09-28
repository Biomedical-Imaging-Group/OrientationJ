//=============================================================================================================
//
// Project: Directional Image Analysis - OrientationJ plugins
// 
// Author: Daniel Sage
// 
// Organization: Biomedical Imaging Group (BIG)
// Ecole Polytechnique Federale de Lausanne (EPFL), Lausanne, Switzerland
//
// OrientationJ: https://bigwww.epfl.ch/demo/orientation/
// MonogenicJ: https://bigwww.epfl.ch/demo/monogenic/
// Source code: https://github.com/Biomedical-Imaging-Group/OrientationJ
//  
// Reference on OrientationJ:
// Z. Püspöki, M. Storath, D. Sage, M. Unser
// Transforms and Operators for Directional Bioimage Analysis: A Survey 
// Focus on Bio-Image Informatics, Springer International Publishing, 2016.
//
// Reference on MonogenicJ:
// M. Unser, D. Sage, D. Van De Ville
// Multiresolution Monogenic Signal Analysis Using the Riesz-Laplace Wavelet Transform
// IEEE Transactions on Image Processing, 2009.
//
// Conditions of use: We expect you to include adequate citations and 
// acknowledgments whenever you present or publish results that are based on it.
//
// License: GNU GPLv3 <http://www.gnu.org/licenses/gpl-3.0.html>
//=============================================================================================================

package monogenicj;

import gui_orientation.components.WalkBar;
import orientation.imageware.Builder;
import orientation.imageware.ImageWare;

public class StructureTensor {

	private ImageWare orientation;
	private ImageWare energy;
	private ImageWare coherency;
	private WalkBar walk;
	
	public StructureTensor(WalkBar walk) {
		this.walk = walk;
	}
	
	public void compute(ImageWare gradx, ImageWare grady, double sigma, double epsilon) {
		int nx = gradx.getWidth();
		int ny = gradx.getHeight();
		int nz = gradx.getSizeZ();
		
		if (walk != null)
			walk.reset();

		coherency = Builder.create(nx, ny, nz, ImageWare.FLOAT);
		if (walk != null)
			walk.progress("Structure Tensor", 10);
		orientation = Builder.create(nx, ny, nz, ImageWare.FLOAT);
		if (walk != null)
			walk.progress("Structure Tensor", 20);
		energy = Builder.create(nx, ny, nz, ImageWare.FLOAT);
		if (walk != null)
			walk.progress("Structure Tensor", 30);
		
		ImageWare dxx = Builder.create(nx, ny, 1, ImageWare.FLOAT);
		ImageWare dyy = Builder.create(nx, ny, 1, ImageWare.FLOAT);
		ImageWare dxy = Builder.create(nx, ny, 1, ImageWare.FLOAT);
	
		double xx=0.0, yy=0.0, xy=0.0;	
		for(int z=0; z<nz; z++) {
			if (walk != null)
				walk.progress("Structure Tensor", 30+z*70.0/nz);
			gradx.getXY(0, 0, z, dxx);
			gradx.getXY(0, 0, z, dxy);
			grady.getXY(0, 0, z, dyy);
			dxy.multiply(dyy);
			dxx.multiply(dxx);
			dyy.multiply(dyy);
			dxx.smoothGaussian(sigma, sigma, 0);
			dyy.smoothGaussian(sigma, sigma, 0);
			dxy.smoothGaussian(sigma, sigma, 0);
			
			for(int y=0; y<ny; y++)
			for(int x=0; x<nx; x++) {
				xx = dxx.getPixel(x, y, 0);
				yy = dyy.getPixel(x, y, 0);
				xy = dxy.getPixel(x, y, 0);
				coherency.putPixel(x, y, z, computeCoherency(xx, yy, xy, epsilon));
				orientation.putPixel(x, y, z, computeOrientation(xx, yy, xy));
				energy.putPixel(x, y, z, 0.5 * (xx + yy + Math.sqrt((yy-xx)*(yy-xx)+4*xy*xy)));
				//energy.putPixel(x, y, z, Math.sqrt(xx+yy));
			}
		}
		if (walk != null)
			walk.finish("Structure Tensor");
	}
	
	/**
	*/
	private double computeCoherency(double xx, double yy, double xy, double epsilon) {
		double coherency = Math.sqrt((yy-xx)*(yy-xx) + 4.0*xy*xy)/(xx + yy + epsilon);
		return coherency;
	}

	/**
	*/
	private double computeOrientation(double xx, double yy, double xy) {
		return 0.5*Math.atan2(2.0*xy, (yy-xx));
	}
	
	public ImageWare getOrientation() {
		return orientation;	
	}

	public ImageWare getEnergy() {
		return energy;	
	}

	public ImageWare getCoherency() {
		return coherency;	
	}


}
