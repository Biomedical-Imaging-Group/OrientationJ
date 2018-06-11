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
 
package gui_orientation;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Polygon;
import java.awt.Rectangle;
import java.util.Vector;

import ij.ImagePlus;
import ij.gui.ImageCanvas;
import orientation.OrientationParameters;
import orientation.imageware.FMath;

public class MeasureCanvas extends ImageCanvas {
	
	private MeasureTable table;
	private ImagePlus imp;
	private OrientationParameters params;
	private Vector<Measure> measures;
	private double LIMIT_ELLIPSE = 0.99;
	private Color areaColor = new Color(200, 100, 0, 100);
	private Color elliColor = new Color(255, 0, 0, 200);
	
	public MeasureCanvas(ImagePlus imp) {
   		super(imp);
		this.imp = imp;
	}

	public void setMeasures(Vector<Measure> measures, MeasureTable table, OrientationParameters params) {
		this.table = table;
		this.measures = measures;
		this.params = params;
	}

	@Override
	public void paint(Graphics g) {
		super.paint(g);
		if (params == null)
			return;
		if (imp == null)
			return;
		areaColor = new Color(params.colorAreaR, params.colorAreaG, params.colorAreaB, (int)(params.colorAreaOpacity*2.55));
		elliColor = new Color(params.colorEllipseR, params.colorEllipseG, params.colorEllipseB, (int)(params.colorEllipseOpacity*2.55));
		int z = imp.getCurrentSlice();

		if (table != null) {
			double mag = getMagnification();
			Rectangle off = getSrcRect();
			for(int i=0; i<measures.size(); i++) {
				Measure measure = (Measure)measures.get(i);
				if (measure != null) 
				if (measure.pz == z) {
					int xc = FMath.round((measure.px-off.x)*mag);
					int yc = FMath.round((measure.py-off.y)*mag);
					
					
					if (table.isRectangle(i)) {
						g.setColor(areaColor);
						//g.drawRect(xt, yt, FMath.round(w*mag), FMath.round(h*mag));
						Polygon polygonZoomed = new Polygon();
						for (int p=0; p<measure.polygon.npoints; p++)
							polygonZoomed.addPoint(FMath.round((measure.polygon.xpoints[p]-off.x)*mag), FMath.round((measure.polygon.ypoints[p]-off.y)*mag));
						g.fillPolygon(polygonZoomed);
						drawString(g, ""+ measure.count, xc, yc, Color.green);
					}
					if (table.isEllipse(i)) {
						//g.setColor(new Color((255-10*measure.rankScale > 0 ? (255-10*measure.rankScale) : 0), 0, 0));
						double k1 = (1.0 - LIMIT_ELLIPSE*measure.coherency);
						double k2 = (1.0 + LIMIT_ELLIPSE*measure.coherency);
						double norm = 1.0;
						double a = Math.sqrt(norm * (measure.area/(2.0*Math.PI)) * (k2/k1));
						double b = Math.sqrt(norm * (measure.area/(2.0*Math.PI)) * (k1/k2));
						Polygon ellipse = new Polygon();
						double cosa = Math.cos(measure.orientation);
						double sina = Math.sin(measure.orientation);
						double astep = Math.PI/36.0;
						double thick = (double)params.colorEllipseThickness / 2.0 / mag;
						for(double th = -thick; th<=thick; th+=0.25/mag)
						for(double an=0; an<=2*Math.PI; an+=astep) {
							double xe = (a+th) * Math.cos(an);
							double ye = (b+th) * Math.sin(an);
							int x = FMath.round((measure.px + cosa * xe + sina * ye - off.x)*mag);
							int y = FMath.round((measure.py - sina * xe + cosa * ye - off.y)*mag);
							ellipse.addPoint(x, y);
						}
						g.setColor(elliColor);
						g.drawPolygon(ellipse);
					}
				}
			}
		}
		
	}

	/*
	private void drawStartingText(Graphics g) {
		g.setColor(areaColor);
		g.fillRect(0, 0, nx, 80);
		g.drawLine(0, 47, nx, 47);
		g.drawLine(0, 80, nx, 80);
		g.drawLine(0, 47, nx, 47);
		g.drawLine(0, 80, nx, 80);
		drawString(g, "OrientationJ Measurement - Java Applet Version ("+ System.getProperty("java.version") + ") - 24/01/2007", 10, 17, Color.orange);
		drawString(g, "More info: http://bigwww.epfl.ch/demo/orientation/", 10, 37, Color.orange);
		drawString(g, "Image: Elastin fibers in cerebral artery. Courtesy of Edouard Fonck, LHCT, EPFL.", 10, 67, Color.orange);
		drawString(g, "Drag the mouse or click to measure. Reset button to clear measurements.", 10, ny-6, Color.cyan);
	}
	*/
	
	/**
	*/
	private void drawString(Graphics g, String msg, int x, int y, Color c) {
		g.setColor(Color.black);
		g.drawString(msg, x+1, y+1);
		g.setColor(c);
		g.drawString(msg, x, y);
	}	
}
