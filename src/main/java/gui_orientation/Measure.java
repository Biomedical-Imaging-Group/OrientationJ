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

import java.awt.Polygon;
import java.awt.Rectangle;
import java.text.DecimalFormat;

import ij.ImagePlus;
import ij.process.ByteProcessor;

public class Measure {
	
	public double sigmaLoG;
	public double coherency;
	public double harris;
	public double orientation;
	public double energy;
	public double phase;
	public int count;
	public int px;
	public int py;
	public int pz;
	public int area;
	private DecimalFormat df2 = new DecimalFormat("#0.00");
	private DecimalFormat df3 = new DecimalFormat("#0.000");
	private DecimalFormat dfd = new DecimalFormat("000000.000");
	private DecimalFormat dfi = new DecimalFormat("00000");
	
	public ByteProcessor mask;
	public Rectangle rect;
	public Polygon polygon;
	
	public Measure(int count, int px, int py, int pz, double sigmaLoG, ImagePlus imp, int area, Rectangle rect, Polygon polygon, ByteProcessor mask) {
		this.count = count;
		this.px = px;		
		this.py = py;		
		this.pz = pz;		
		this.sigmaLoG = sigmaLoG;
		this.rect = rect;
		this.polygon = polygon;
		this.mask = mask;
		this.area = area;
	}
	
	public void setTensor(double energy, double phase, double orientation, double coherency) {
		this.energy = energy;
		this.phase = phase;
		this.orientation = orientation;
		this.coherency = coherency;
	}

	public void setHarris(double harris) {
		this.harris = harris;
	}

	public Object[] makeTableLine() {
		int col = 10;
		Object s[] = new Object[col];
		int i = 0;
		s[i++] = new Integer(count);
		s[i++] = new Integer(px);
		s[i++] = new Integer(py);
		s[i++] = new Integer(pz);
		s[i++] = new Boolean(true);
		s[i++] = new Boolean(true);
		s[i++] = df2.format(sigmaLoG);
		s[i++] = df2.format(energy);
		s[i++] = df2.format(Math.toDegrees(orientation));
		s[i++] = df3.format(coherency);
		return s;
	}
	
	public String toString() {
		String str = "";
		str += dfi.format(count) + "\t";
		str += dfi.format(px) + "\t";
		str += dfi.format(py) + "\t";
		str += dfi.format(pz) + "\t";
		str += dfd.format(sigmaLoG) + "\t";
		str += dfd.format(energy) + "\t";
		str += dfd.format(Math.toDegrees(orientation)) + "\t";
		str += dfd.format(coherency);
		return str;
	}
	
	public String headings() {
		String str = "";
		str += "No       " + "\t";
		str += "Xc       " + "\t";
		str += "Yc       " + "\t";
		str += "Zc       " + "\t";
		str += "Sigma (LoG)    " + "\t";
		str += "Energy         " + "\t";
		str += "Orientation  " + "\t";
		str += "Coherency    ";
		return str;
	}

}