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

import java.awt.Color;
import java.awt.Frame;
import java.util.ArrayList;
import java.util.Collections;

import ij.ImagePlus;
import ij.gui.Line;
import ij.gui.OvalRoi;
import ij.gui.Overlay;
import ij.gui.Plot;
import ij.gui.Roi;
import ij.measure.ResultsTable;
import orientation.imageware.FMath;

public class OrientationResults {
	public static String prefix = "OJ-";

	public static void hide() {
		Frame frame[] = Frame.getFrames();
		for (int i=0; i<frame.length; i++) {
			if (frame[i].getTitle().startsWith(prefix))
				frame[i].dispose();
		}
	}

	public static void show(int feature, GroupImage gim, OrientationParameters params, int countRun) {
		if (feature == OrientationParameters.DIST_HISTO_PLOT)
			plotDistribution(gim, params, countRun);
		else if (feature == OrientationParameters.DIST_HISTO_TABLE) 
			tableDistribution(gim, params, countRun);	
		else {
			boolean view[]= new boolean[params.view.length];
			view[feature] = true;
			show(view, gim, params, countRun);
		}
	}

	public static void show(GroupImage gim, OrientationParameters params, int countRun) {
		show(params.view, gim, params, countRun);
	}
	
	public static void show(boolean view[], GroupImage gim, OrientationParameters params, int countRun) {
		int feature;

		feature = OrientationParameters.GRADIENT_HORIZONTAL;
		if (view[feature] && params.isServiceAnalysis())
			display(feature, gim, params, countRun);

		feature = OrientationParameters.GRADIENT_VERTICAL;
		if (view[feature] && params.isServiceAnalysis())
			display(feature, gim, params, countRun);

		feature = OrientationParameters.TENSOR_ENERGY;
		if (view[feature])
			display(feature, gim, params, countRun);

		feature = OrientationParameters.TENSOR_ORIENTATION;
		if (view[feature])
			display(feature, gim, params, countRun);

		feature = OrientationParameters.TENSOR_COHERENCY;
		if (view[feature])
			display(feature, gim, params, countRun);

		feature = OrientationParameters.HARRIS;
		if (view[feature] && params.isServiceHarris())
			display(feature, gim, params, countRun);

		feature = OrientationParameters.SURVEY;
		if (view[feature] && !params.isServiceHarris())
		if (!params.isServiceClustering())
		if (!params.isServiceVectorField())
		if (!params.isServiceDistribution())
			display(feature, gim, params, countRun);

		feature = OrientationParameters.DIST_MASK;
		if (view[feature] && params.isServiceDistribution()) {
			plotDistribution(gim, params, countRun);
			display(feature, gim, params, countRun);
		}
		
		feature = OrientationParameters.DIST_ORIENTATION;
		if (view[feature] && params.isServiceDistribution()) {
			plotDistribution(gim, params, countRun);
			display(feature, gim, params, countRun);
		}
		
		if (params.isServiceHarris())
			displayHarris(gim, params, countRun);
		
		if (params.isServiceVectorField())
			displayVectorField(gim, params, countRun);
		
		if (params.isServiceDistribution()) {
			if (view[OrientationParameters.DIST_HISTO_PLOT])
				plotDistribution(gim, params, countRun);
			if (view[OrientationParameters.DIST_HISTO_TABLE]) 
				tableDistribution(gim, params, countRun);	
		}
	}
	
	public static void display(int feature, GroupImage gim, OrientationParameters params, int countRun) {
		ImagePlus imp = gim.showFeature(feature, countRun, !params.radian, params);
		imp.setTitle(prefix + imp.getTitle());
		imp.show();
	}
	
	public static float[][] distribution(GroupImage gim, OrientationParameters params) {
		double cohmin = params.minCoherency / 100.0;
		double enemin = params.minEnergy / 100.0;
		double enemax = Math.max(0.0001, gim.energy.getMaximum());
		gim.selectedDistributionOrientation.fillConstant(0);
		gim.selectedDistributionMask.fillConstant(0);
		float histo[][] = new float[gim.nt][180];
	
		int nx = gim.nx;
		int ny = gim.ny;
		double r = 180.0 / Math.PI;
		for(int t=0; t<gim.nt; t++) {
		for (int x = 0; x < nx; x++)
			for (int y = 0; y < ny; y++) {
				double coh = gim.coherency.getPixel(x, y, t);
				if (cohmin <= coh) {
					double ene = gim.energy.getPixel(x, y, t) / enemax;
					if (enemin <= ene) {
						double orideg = 90.0 + gim.orientation.getPixel(x, y, t) * r;
						gim.selectedDistributionOrientation.putPixel(x, y, t, orideg);
						gim.selectedDistributionMask.putPixel(x, y, t, 1.0);
						int a = FMath.floor(orideg);
						a = Math.max(0, Math.min(179, a));
						histo[t][a]++;
					}
				}
			}
		}
		return histo;
	}

	public static void tableDistribution(GroupImage gim, OrientationParameters params, int countRun) {
		float histo[][] = distribution(gim, params);
		int nt = histo.length;
		ResultsTable table = new ResultsTable();
		float angles[] = new float[180];
		for (int a = 0; a < 180; a++)
			angles[a] = a - 89.5f;
		
		for (int a = 0; a < 180; a++) {
			table.incrementCounter();
			table.addValue("Orientation", angles[a]);
			for (int t = 0; t<nt; t++)
				table.addValue("Slice" + (t+1), histo[t][a]);
		}
		table.show(prefix + "Distribution-" + countRun);
	}
	
	public static void plotDistribution(GroupImage gim, OrientationParameters params, int countRun) {
		float histo[][] = distribution(gim, params);
		int nt = histo.length;
		float angles[] = new float[180];
		for (int a = 0; a < 180; a++)
			angles[a] = a - 89.5f;
		for (int t = 0; t<nt; t++) {
			float max = -Float.MIN_VALUE;
			for (int a = 0; a < 180; a++) {
			if (histo[t][a] > max)
				max = histo[t][a];
			}
			String title = prefix + OrientationParameters.name[OrientationParameters.DIST_HISTO_PLOT] + "-" + countRun;
			title = title + "-slice-" + (t+1);
			Plot pw = new Plot(title, "Orientation in Degrees", "Distribution of orientation", angles, histo[t]);
			pw.setColor(Color.red);
			pw.setLineWidth(1);
			pw.setLimits(-90, 90, 0, max);
			pw.show();
		}
	}
	
	/*
	public static void saveDistribution(GroupImage gim, OrientationParameters params) {
		float histo[][] = distribution(gim, params);
		if (params.pathSaveDistribution != "") {
			IJ.log("Saved the distribution in the file: " + params.pathSaveDistribution);
			File file = new File(params.pathSaveDistribution);
			try {
				FileWriter fw = new FileWriter(file);
				for (int a = 0; a < 180; a++) {
					fw.write("" + (a - 90));
					for(int t=0; t<histo.length; t++)
						fw.write("\t" + (histo[a]));
					fw.write("\n");
				}
				fw.close();
			}
			catch (Exception ex) {
				IJ.log("Error to write into the file: " + params.pathSaveDistribution);
			}
		}
	}
	*/
	public static void displayHarris(GroupImage gim, OrientationParameters params, int countRun) {
		if (gim == null)
			return;
		int L = params.harrisL;
		double min = Math.min(1, Math.max(0, params.harrisMin * 0.01));
		if (L <= 0)
			L = 0;

		ArrayList<Corner> corners = new ArrayList<Corner>();
		double v;
		for (int t = 0; t < gim.nt; t++) {
			for (int y = 1; y < gim.ny-1; y++)
				for (int x = 1; x < gim.nx-1; x++) {
					v = gim.harris.getPixel(x, y, t);
					//if ( (v - min) * r > min) {	
						if (gim.harris.getPixel(x-1, y, t) < v)
						if (gim.harris.getPixel(x+1, y, t) < v)
						if (gim.harris.getPixel(x, y-1, t) < v)
						if (gim.harris.getPixel(x, y+1, t) < v)
							corners.add(new Corner(x, y, t, v));
	
					//}
				}
		}
		Collections.sort(corners);
		
		if (params.showHarrisTable) {
			ResultsTable table = new ResultsTable();
			for (int i = 0; i < corners.size()*min; i++) {
				Corner pt = (Corner) corners.get(i);
				table.incrementCounter();
				table.addValue("X", pt.x);
				table.addValue("Y", pt.y);
				table.addValue("Slice", pt.t);
				table.addValue("Harris Index", pt.getHarrisIndex());
			}
			table.show("OJ-Table-Corners Harris-");
		}

		Overlay overlay = new Overlay();
		if (params.showHarrisOverlay) {
			for (int i = 0; i < corners.size()*min; i++) {
				Corner pt = (Corner) corners.get(i);
				Roi roi = new OvalRoi(pt.x-L/2, pt.y-L/2, L, L);
				roi.setPosition(pt.t);
				overlay.add(roi);
			}
	 	}
		gim.getImagePlus().setOverlay(overlay);
	}

	/*
	 * This method display the vectors and open a table for the 
	 * vector field plugin
	 * 
	 * @author Daniel Sage
	 */
	public static void displayVectorField(GroupImage gim, OrientationParameters params, int countRun) {
		if (gim == null)
			return;

		int size = params.vectorGrid;
		int type = params.vectorType;
		double scale = params.vectorScale;
		
		int nt = gim.energy.getSizeZ();
		Clusters[] clusters = new Clusters[nt];
		int xstart = (gim.nx - (gim.nx / size) * size) / 2;
		int ystart = (gim.ny - (gim.ny / size) * size) / 2;
		double max = gim.energy.getMaximum();
		if (max <= 0)
			return;
		
		int size2 = size * size;
		for (int t = 0; t < nt; t++) {
			clusters[t] = new Clusters();
			for (int y = ystart; y < gim.ny; y += size)
				for (int x = xstart; x < gim.nx; x += size) {
					double dx = 0.0;
					double dy = 0.0;
					double coherencies = 0.0;
					double energies = 0.0;
					for (int k = 0; k < size; k++)
						for (int l = 0; l < size; l++) {
							double angle = gim.orientation.getPixel(x, y, t);
							double coh = gim.coherency.getPixel(x, y, t);
							dx += Math.cos(angle);
							dy += Math.sin(angle);
							coherencies += coh;
							energies += gim.energy.getPixel(x, y,t);
						}
					dx /= size2;
					dy /= size2;
					coherencies /= size2;
					energies /= size2;
					if (energies > 0)
						if (coherencies > 0)
							clusters[t].add(new Cluster(x, y, size, size, dx, dy, coherencies, (energies / max)));
				}
		}
		
		if (params.showVectorTable) {
			ResultsTable table = new ResultsTable();
			for (int t = 0; t < nt; t++)
			for (Cluster c : clusters[t]) {
				double a = Math.toDegrees(Math.atan2(c.dy, c.dx));
				if (a < -90)
					a += 180;
				if (a > 90)
					a -= 180;
				table.incrementCounter();
				table.addValue("X", c.x + size / 2);
				table.addValue("Y", c.y + size / 2);
				table.addValue("Slice", t);
				table.addValue("DX", -c.dx);
				table.addValue("DY", c.dy);
				table.addValue("Orientation", a);
				table.addValue("Coherency", c.coherency);
				table.addValue("Energy", c.energy);
			}
			table.show("OJ-Table-Vector-Field-");
		}
		
		Overlay overlay = new Overlay();
		if (params.showVectorOverlay) {
			double r = scale / 100.0 * size * 0.5;
			for (int t = 0; t < nt; t++)
			for (Cluster c : clusters[t]) {
				double a = r;
				if (type == 1)
					a = r * c.energy;
				else if (type == 2)
					a = r * c.coherency;
				else if (type == 3)
					a = r * c.energy * c.coherency;

				int x1 = (int) Math.round(c.x + a * c.dx);
				int y1 = (int) Math.round(c.y - a * c.dy);
				int x2 = (int) Math.round(c.x - a * c.dx);
				int y2 = (int) Math.round(c.y + a * c.dy);
				Roi roi = new Line(x1, y1, x2, y2);
				roi.setPosition(t + 1);
				//Roi.setColor(new Color(200, 0, (int)(200*Math.random()), 100));
				overlay.add(roi);
			}
		}
		gim.getImagePlus().setOverlay(overlay);
	}

}
