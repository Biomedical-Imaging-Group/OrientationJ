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

package orientation;

import java.util.Vector;

import ij.IJ;
import ij.ImagePlus;
import ij.WindowManager;
import ij.process.ImageProcessor;
import orientation.imageware.Builder;
import orientation.imageware.ImageWare;

public class GroupImage {

	final public static int SCALABLE_NO = 0;
	final public static int SCALABLE = 1;
	final public static int SCALABLE_RANGE_PI = 2;
	final public static int SCALABLE_RANGE_2PI = 3;
		
	public ImageWare source;
	
	public ImageWare gx;
	public ImageWare gy;

	public ImageWare hxx;
	public ImageWare hyy;
	public ImageWare hxy;
	
	public ImageWare energy;
	public ImageWare coherency;
	public ImageWare orientation;
	public ImageWare harris;
	
	public ImageWare selectedDistributionMask;
	public ImageWare selectedDistributionOrientation;

	public double minmaxHarris[] = new double[2];
	
	public int nx;
	public int ny;
	public int nt;

	private static ImagePlus imp;
	
	private LogAbstract log;
	
	public GroupImage(LogAbstract log, ImageProcessor ip, OrientationParameters params) {
		this.log = log;
		this.source = Builder.create(new ImagePlus("", ip));
		create(params);
	}
	
	public GroupImage(LogAbstract log, ImageWare source, OrientationParameters params) {
		this.log = log;
		this.source = source;
		create(params);
	}

	public static ImageWare getCurrentImage() {
		imp = WindowManager.getCurrentImage();
		if (imp == null) {
			IJ.error("No open image.");
			return null;
		}
		if (imp.getType() != ImagePlus.GRAY8 && imp.getType() != ImagePlus.GRAY16 && imp.getType() != ImagePlus.GRAY32) {
			IJ.error("Open process 8-bits, 16-bits, or 32-bits image.");
			return null;
		}
		return Builder.create(imp);
	}
	
	public ImagePlus getImagePlus() {
		return imp;
	}
	
	private void create( OrientationParameters params) {
		nx = source.getWidth();
		ny = source.getHeight();
		nt = source.getSizeZ();
		long kb = (nx*ny*nt*4) / 1024;
		log.progress("Alloction", 10);
		if (params.gradient == OrientationParameters.HESSIAN) {
			hxx = allocate("Hessian Horizontal", kb);
			log.progress("Alloc HXX", 30);
			hyy = allocate("Hessian Vertical", kb);
			log.progress("Alloc HYY", 40);
			hxy = allocate("Hessian Cross Term", kb);
			log.progress("Alloc HXY", 60);
		}
		else {
			gx = allocate("Gradient Horizontal", kb);
			log.progress("Alloc GX", 40);
			gy = allocate("Gradient Vertical", kb);
			log.progress("Alloc GY", 60);
		}
		energy	= allocate("Tensor Energy", kb);
		log.progress("Alloc E", 70);
		coherency 	= allocate("Coherency", kb);
		log.progress("Alloc Coh", 80);
		orientation = allocate("Orientation", kb);
		
		if (params.isServiceHarris()) 
			harris = allocate("Harris Index", kb);
		
		if (params.isServiceDistribution()) {
			selectedDistributionMask = allocate("Selected Mask", kb);
			selectedDistributionOrientation = allocate("Selected Orientation", kb);
		}
		log.progress("Alloc Ori", 90);
	}
	
	private ImageWare allocate(String title, long kb) {
		return Builder.create(nx, ny, nt, ImageWare.FLOAT);
	}
	
	public ImagePlus showFeature(int feature, int countRun, boolean degrees, OrientationParameters params) {
		ImagePlus imp = createImageFeature(feature, countRun, degrees, params);
		if (imp != null)
			imp.show();
		return imp;
	}
	
	public ImagePlus createImageFeature(int feature, int countRun, boolean degrees, OrientationParameters params) {
		int scalability = 0;
		ImageWare image = null;
		if (feature == OrientationParameters.GRADIENT_HORIZONTAL) {
			image = (hxx != null ? hxx : gx);
			scalability = SCALABLE;
		}
		else if (feature == OrientationParameters.GRADIENT_VERTICAL) {
			image = (hyy != null ? hyy : gy);
			scalability = SCALABLE;
		}
		else if (feature == OrientationParameters.TENSOR_ORIENTATION) {
			image = orientation;
			scalability = SCALABLE_RANGE_PI;
		}
		else if (feature == OrientationParameters.TENSOR_COHERENCY) {
			image = coherency;
			scalability = SCALABLE_NO;
		}
		else if (feature == OrientationParameters.TENSOR_ENERGY) {
			image = energy;
			scalability = SCALABLE;
		}
		else if (feature == OrientationParameters.HARRIS) {
			image = harris;
			scalability = SCALABLE_NO;
		}
		else if (feature == OrientationParameters.DIST_MASK) {
			image = selectedDistributionMask;
			scalability = SCALABLE_NO;
		}
		else if (feature == OrientationParameters.DIST_ORIENTATION) {
			image = selectedDistributionOrientation;
			scalability = SCALABLE_RANGE_PI;
		}
				
		if (image != null) {
			String title = OrientationParameters.name[feature];
			ImageWare pim = prepare(image, scalability, degrees, false);
			return new ImagePlus(title + "-" + countRun, pim.buildImageStack());
		}
			
		if (feature == OrientationParameters.SURVEY) {
			ImageWare c1 = selectChannel(params.featureHue);
			ImageWare c2 = selectChannel(params.featureSat);
			ImageWare c3 = selectChannel(params.featureBri);
			ImagePlus imp = null;
			if (params.hsb)
				imp = ColorMapping.colorHSB(nt,  OrientationParameters.name[OrientationParameters.SURVEY] + "-" + countRun,  c1, c2, c3);
			else
				imp = ColorMapping.colorRGB(nt,  OrientationParameters.name[OrientationParameters.SURVEY] + "-" + countRun,  c1, c2, c3);
			return imp;
		}

		return null;
	}
	
	public void hideFeature(String title, Vector<ImagePlus> listImage, int countRun) {			
		Vector<ImagePlus> updatedList = new Vector<ImagePlus>();
		for(int i=0; i<listImage.size(); i++) {
			ImagePlus imp = (ImagePlus)listImage.get(i);
			boolean closed = false;
			if (imp != null) {
				for (int k=0; k<=countRun; k++) {
					if (imp.getTitle().equals(title + "-" + k)) {
						imp.close();
						closed = true;
					}
				}
			}
			if (closed == false) {
				updatedList.add(imp);
			}
		}
		listImage.removeAllElements();
		for(int i=0; i<updatedList.size(); i++) {
			ImagePlus imp = (ImagePlus)updatedList.get(i);
			listImage.add(imp);
		}
	}
				
	public ImageWare prepare(ImageWare image, int scalability, boolean degrees, boolean forColor) {
		if (image == null) {
			return null;
		}
		return createStacked(image, scalability, degrees, forColor);
	}
		
	private ImageWare createStacked(ImageWare image, int scalability, boolean degrees, boolean forColor) {
		ImageWare stack = image.duplicate();
		if (forColor) 
			rescaleColor(stack, scalability);
		else
			rescaleMono(stack, scalability, degrees);
		return stack;
	} 

	private void rescaleColor(ImageWare stack, int scalability) {
		if (scalability == SCALABLE) {
			stack.rescale(0, 1);
		}
		else if (scalability == SCALABLE_RANGE_PI ) {
			stack.add(Math.PI/2.0);
			stack.multiply(1.0/(Math.PI));
		}
		else if (scalability == SCALABLE_RANGE_2PI ) {
			stack.add(Math.PI);
			stack.multiply(1.0/(Math.PI*2));
		}
	}
	
	private void rescaleMono(ImageWare stack, int scalability, boolean degrees) {
		if (scalability == SCALABLE) {
			stack.rescale(0, 1);
		}
		else if (scalability == SCALABLE_RANGE_PI) {
			if (degrees)
				stack.multiply(180.0/Math.PI);
		}
		else if (scalability == SCALABLE_RANGE_2PI) {
			stack.add(Math.PI);
			if (degrees)
				stack.multiply(180.0/Math.PI);
		}
	}
		
	public ImageWare selectChannel(String name) {
	
		if (name.equals("Gradient-X") && gy != null) {
			return prepare(gy, SCALABLE, false, true);
		}
		else if (name.equals("Gradient-Y") && gy != null) {
			return prepare(gx, SCALABLE, false, true);
		}
		else if (name.equals("Orientation") && orientation != null) {
			return prepare(orientation, SCALABLE_RANGE_PI, false, true);
		}
		else if (name.equals("Coherency") && coherency != null) {
			return prepare(coherency, SCALABLE_NO, false, true);
		}
		else if (name.equals("Energy") && energy != null) {
			return prepare(energy, SCALABLE, false, true);
		}
		else if (name.equals("Constant")) {
			ImageWare max = Builder.create(nx, ny, nt, ImageWare.FLOAT);
			max.fillConstant(1);
			return prepare(max, SCALABLE_NO, false, true);
		}
		
		ImageWare ori = source.convert(ImageWare.FLOAT);
		return prepare(ori, SCALABLE, false, true);
	}
		
}

