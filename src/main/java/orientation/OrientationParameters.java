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

package orientation;

import gui_orientation.components.Settings;
import ij.Macro;

public class OrientationParameters {

	final public static int		MODE_ANALYSIS			= 0;
	final public static int		MODE_HARRIS				= 1;
	final public static int		MODE_DISTRIBUTION		= 2;
	final public static int		MODE_DIRECTIONS			= 3;

	private OrientationService	service					= OrientationService.ANALYSIS;

	final public static int		GRADIENT_CUBIC_SPLINE	= 0;
	final public static int		GRADIENT_FINITE_DIFF	= 1;
	final public static int		GRADIENT_FOURIER_DOMAIN	= 2;
	final public static int		GRADIENT_RIESZ			= 3;
	final public static int		GRADIENT_GAUSSIAN		= 4;
	final public static int		HESSIAN					= 5;

	final public static int		GRADIENT_HORIZONTAL		= 0;
	final public static int		GRADIENT_VERTICAL		= 1;
	final public static int		TENSOR_ENERGY			= 2;
	final public static int		TENSOR_ORIENTATION		= 3;
	final public static int		TENSOR_COHERENCY			= 4;
	final public static int		HARRIS					= 5;
	final public static int		SURVEY					= 6;
	final public static int		DIST_MASK				= 7;
	final public static int		DIST_ORIENTATION			= 8;
	final public static int		DIST_HISTO_PLOT			= 9;
	final public static int		DIST_HISTO_TABLE			= 10;
	final public static int		NB_FEATURES				= 11;

	public int					gradient					= GRADIENT_CUBIC_SPLINE;

	public double				sigmaLoG					= 0;
	public double				sigmaST					= 2;
	public double				epsilon					= 0.001;
	public boolean				radian					= true;

	public double				minCoherency				= 0;
	public double				minEnergy				= 0;

	public double				harrisK					= 0.05;
	public int					harrisL					= 2;
	public double				harrisMin				= 10.0;

	public boolean				showHarrisTable			= true;
	public boolean				showHarrisOverlay		= true;
	public boolean				showVectorTable			= true;
	public boolean				showVectorOverlay		= true;

	public int					vectorGrid				= 10;
	public double				vectorScale				= 100;
	public int					vectorType				= 0;

	public boolean				hsb						= true;
	public boolean				view[]					= new boolean[NB_FEATURES];

	final static public String	name[]					= { 
			"Gradient-X", "Gradient-Y", "Energy", "Orientation",
			"Coherency", "Harris-index", "Color-survey", "Binary Mask", "Orientation Mask", 
			"Histogram", "Table"};

	// Parameters for the measurement tools
	public int					colorEllipseR			= 255;
	public int					colorEllipseG			= 0;
	public int					colorEllipseB			= 0;
	public int					colorEllipseOpacity		= 100;
	public int					colorAreaR				= 128;
	public int					colorAreaG				= 128;
	public int					colorAreaB				= 0;
	public int					colorAreaOpacity			= 50;
	public double				colorEllipseThickness	= 0.5;

	public String				featureHue				= "Orientation";
	public String				featureSat				= "Coherency";
	public String				featureBri				= "Image Original";

	public OrientationParameters(OrientationService service) {
		this.service = service;
		view[SURVEY] = true;
		view[DIST_HISTO_PLOT] = true;
	}

	public String getServiceName() {
		if (isServiceHarris())
			return "Corner Harris";
		else if (isServiceAnalysis())
			return "Analysis";
		else if (isServiceDistribution())
			return "Distribution";
		else if (isServiceClustering())
			return "Clustering";
		else if (isServiceVectorField())
			return "Vector Field";
		return "Untitled Service";
	}

	public boolean isServiceAnalysis() {
		return service == OrientationService.ANALYSIS;
	}

	public boolean isServiceDistribution() {
		return service == OrientationService.DISTRIBUTION;
	}

	public boolean isServiceClustering() {
		return service == OrientationService.CLUSTERING;
	}

	public boolean isServiceVectorField() {
		return service == OrientationService.VECTORFIELD;
	}

	public boolean isServiceHarris() {
		return service == OrientationService.HARRIS;
	}

	public void load(Settings settings) {
		epsilon = settings.loadValue("epsilon", epsilon);
		radian = settings.loadValue("radian", true);
		hsb = settings.loadValue("hsb", hsb);
		for (int k = 0; k < OrientationParameters.NB_FEATURES; k++) {
			view[k] = settings.loadValue("view_" + name[k],
					((k == SURVEY || k == HARRIS || k == DIST_HISTO_TABLE  || k == DIST_HISTO_PLOT) ? true : false));
		}

		colorEllipseR = settings.loadValue("Measure_colorEllipseR", colorEllipseR);
		colorEllipseG = settings.loadValue("Measure_colorEllipseG", colorEllipseG);
		colorEllipseB = settings.loadValue("Measure_colorEllipseB", colorEllipseB);
		colorEllipseOpacity = settings.loadValue("Measure_colorEllipseOpacity", colorEllipseOpacity);
		colorEllipseThickness = settings.loadValue("Measure_colorEllipseThickness", colorEllipseThickness);
		colorAreaR = settings.loadValue("Measure_colorAreaR", colorAreaR);
		colorAreaG = settings.loadValue("Measure_colorAreaG", colorAreaG);
		colorAreaB = settings.loadValue("Measure_colorAreaB", colorAreaB);
		colorAreaOpacity = settings.loadValue("Measure_colorAreaOpacity", colorAreaOpacity);
	}

	public void store(Settings settings) {
		settings.storeValue("epsilon", epsilon);
		settings.storeValue("radian", radian);
		settings.storeValue("hsb", hsb);
		for (int k = 0; k < OrientationParameters.NB_FEATURES; k++) {
			settings.storeValue("view_" + name[k], view[k]);
		}
		settings.storeValue("Measure_colorEllipseR", colorEllipseR);
		settings.storeValue("Measure_colorEllipseG", colorEllipseG);
		settings.storeValue("Measure_colorEllipseB", colorEllipseB);
		settings.storeValue("Measure_colorEllipseOpacity", colorEllipseOpacity);
		settings.storeValue("Measure_colorEllipseThickness", colorEllipseThickness);
		settings.storeValue("Measure_colorAreaR", colorAreaR);
		settings.storeValue("Measure_colorAreaG", colorAreaG);
		settings.storeValue("Measure_colorAreaB", colorAreaB);
		settings.storeValue("Measure_colorAreaOpacity", colorAreaOpacity);
	}

	public void getMacroParameters(String options) {
		sigmaST = Double.parseDouble(Macro.getValue(options, "tensor", "1"));
		gradient = Integer.parseInt(Macro.getValue(options, "gradient", "0"));
		radian = Macro.getValue(options, "radian", "on").equals("on");
		hsb = Macro.getValue(options, "hsb", "on").equals("on");

		int k;
		k = GRADIENT_HORIZONTAL;
		view[k] = Macro.getValue(options, OrientationParameters.name[k].toLowerCase(), "off").equals("on");
		k = GRADIENT_VERTICAL;	
		view[k] = Macro.getValue(options, OrientationParameters.name[k].toLowerCase(), "off").equals("on");
		k = TENSOR_ENERGY;	
		view[k] = Macro.getValue(options, OrientationParameters.name[k].toLowerCase(), "off").equals("on");
		k = TENSOR_ORIENTATION;	
		view[k] = Macro.getValue(options, OrientationParameters.name[k].toLowerCase(), "off").equals("on");
		k = TENSOR_COHERENCY;	
		view[k] = Macro.getValue(options, OrientationParameters.name[k].toLowerCase(), "off").equals("on");
		k = HARRIS;	
		view[k] = Macro.getValue(options, OrientationParameters.name[k].toLowerCase(), "off").equals("on");
		k = SURVEY;	
		view[k] = Macro.getValue(options, OrientationParameters.name[k].toLowerCase(), "on").equals("on");

		// Distribution
		k = DIST_MASK;	
		view[k] = Macro.getValue(options, OrientationParameters.name[k].toLowerCase(), "off").equals("on");
		k = DIST_ORIENTATION;	
		view[k] = Macro.getValue(options, OrientationParameters.name[k].toLowerCase(), "off").equals("on");
		k = DIST_HISTO_PLOT;	
		view[k] = Macro.getValue(options, OrientationParameters.name[k].toLowerCase(), "on").equals("on");
		k = DIST_HISTO_TABLE;	
		view[k] = Macro.getValue(options, OrientationParameters.name[k].toLowerCase(), "off").equals("on");
		minCoherency = Double.parseDouble(Macro.getValue(options, "min-coherency", "0"));
		minEnergy = Double.parseDouble(Macro.getValue(options, "min-energy", "0"));

		// Harris
		harrisK = Double.parseDouble(Macro.getValue(options, "harrisk", "0.1"));
		harrisL = Integer.parseInt(Macro.getValue(options, "harrisl", "3"));
		harrisMin = Double.parseDouble(Macro.getValue(options, "harrismin", "1"));
		showHarrisTable = Macro.getValue(options, "harristable", "on").equals("on");
		showHarrisOverlay = Macro.getValue(options, "harrisoverlay", "on").equals("on");
	
		// Vector Field
		showVectorTable = Macro.getValue(options, "vectortable", "on").equals("on");
		showVectorOverlay = Macro.getValue(options, "vectoroverlay", "on").equals("on");
		vectorGrid = Integer.parseInt(Macro.getValue(options, "vectorgrid", "10"));
		vectorScale = Double.parseDouble(Macro.getValue(options, "vectorscale", "100"));
		vectorType = Integer.parseInt(Macro.getValue(options, "vectortype", "0"));

		// Color
		featureHue = Macro.getValue(options, "hue", "Orientation");
		featureSat = Macro.getValue(options, "sat", "Coherency");
		featureBri = Macro.getValue(options, "bri", "Constant");

	}

}