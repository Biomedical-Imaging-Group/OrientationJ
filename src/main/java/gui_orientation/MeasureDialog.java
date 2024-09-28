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

package gui_orientation;

import java.awt.Cursor;
import java.awt.Polygon;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Vector;

import javax.swing.JButton;
import javax.swing.JFrame;

import gui_orientation.components.GridPanel;
import gui_orientation.components.GridToolbar;
import gui_orientation.components.Settings;
import ij.IJ;
import ij.ImagePlus;
import ij.gui.GUI;
import ij.gui.ImageCanvas;
import ij.gui.ImageWindow;
import ij.gui.Roi;
import ij.gui.StackWindow;
import ij.process.ByteBlitter;
import ij.process.ByteProcessor;
import ij.process.ImageProcessor;
import orientation.Gradient;
import orientation.GroupImage;
import orientation.OrientationParameters;
import orientation.OrientationService;
import orientation.StructureTensor;

public class MeasureDialog extends JFrame implements ActionListener, Runnable {
	
	private WalkBarOrientationJ	walk			= new WalkBarOrientationJ();
	private Settings settings = new Settings("OrientationJ", IJ.getDirectory("plugins") + "OrientationJ.txt");
	private MeasureTable table;
	private MeasureCanvas canvas;
	private ImageCanvas canvasSaved;

  	private JButton 		bnCopy  	= new JButton("Copy Results");
	private JButton 		bnRemove 	= new JButton("Remove");
	private JButton 		bnMeasure 	= new JButton("Measure");
	private JButton 		bnMask  	= new JButton("Create Mask");
	private JButton 		bnOptions  	= new JButton("Options");
	
	private Thread				thread;
	private int countMeasure	= 1;
	
	OrientationParameters params = new OrientationParameters(OrientationService.ANALYSIS);
	private ImagePlus imp;
	private boolean log = false;
	
	
	/**
	*/
	public MeasureDialog(ImagePlus imp, boolean log, double sigma) {
		super("OrientationJ Measure [" + imp.getTitle() + "]");
		canvasSaved = imp.getCanvas();
		this.imp = imp;
		this.log = log;
		table = new MeasureTable();
		params.sigmaLoG = sigma;
	}
	
	public void showDialog() {
   		GridToolbar bar = new GridToolbar(false, 0);
     	bar.place(0, 0, bnCopy);
	    bar.place(0, 1, bnRemove);
 	    bnCopy.addActionListener(this);
	    bnRemove.addActionListener(this);

		GridPanel controls  = new GridPanel(false);
		controls.place(0, 2, bnMask);
		controls.place(0, 3, bnOptions);
		controls.place(0, 4, bnMeasure);
	
		walk.getButtonClose().addActionListener(this);
		
		bnMask.addActionListener(this);
		bnMeasure.addActionListener(this);
		bnOptions.addActionListener(this);
		
		GridPanel pnTable = new GridPanel(false, 0);
		pnTable.place(0, 0, table);
		pnTable.place(1, 0, bar);
		
		GridPanel pn = new GridPanel(false);
		pn.place(1, 0, 2, 1, controls);
		pn.place(2, 0, 2, 1, pnTable);
		pn.place(4, 0, bar);
		pn.place(4, 1, walk);

		params.load(settings);
		add(pn);
		pack();
		setResizable(false);
		GUI.center(this);
		setVisible(true);
		pack();
	}

	@Override
	public synchronized  void actionPerformed(ActionEvent e) {

     	if (e.getSource() == walk.getButtonClose()) {
    		if (imp != null) {
				if (imp.getStack().getSize() > 1)
					imp.setWindow(new StackWindow(imp, canvasSaved));
				else
					imp.setWindow(new ImageWindow(imp, canvasSaved));
			}	
			params.store(settings);
  			dispose();
     	}
 		else if (e.getSource() == bnMask) {
 			int nx = imp.getWidth();
 			int ny = imp.getHeight();
 			ByteProcessor bp = new ByteProcessor(nx, ny);
 			bp.setColor(255);
 			bp.fill();
 			Vector<Measure> measures = table.getMeasures();
			for(int i=0; i<measures.size(); i++) {
 				Measure measure = (Measure)measures.get(i);
 				bp.copyBits(measure.mask, measure.rect.x, measure.rect.y, ByteBlitter.MIN);
 			}
 			ImagePlus imp = new ImagePlus("Mask", bp);
			imp.show();
    	}
 		else if (e.getSource() == bnOptions) {
			new SettingDisplayDialog(params);
			canvas.repaint();
      	}
     	else if (e.getSource() == bnMeasure && imp != null) {
			if (thread == null) {
				thread = new Thread(this);
				thread.setPriority(Thread.MIN_PRIORITY);
				thread.start();
			}
     	}
     	else if (e.getSource() == bnRemove) {
     		table.remove();
     	}
        else if (e.getSource() == bnCopy) {
      		table.copy();
        }
	}
	
	@Override
	public void run() {
		
		Roi roi = imp.getRoi();
		if (roi==null) {
			IJ.error("No ROI selected.");
			thread = null;
			return;
		}
		if (roi.getType() != Roi.RECTANGLE)
		if (roi.getType() != Roi.OVAL)
		if (roi.getType() != Roi.FREEROI)
		if (roi.getType() != Roi.POLYGON)
		if (roi.getType() != Roi.POINT) {
			IJ.error("The ROI should be a Rectangle or Oval or FreeRoi or Polygon or Point.");
			thread = null;
			return;
		}
		
		Rectangle rect = roi.getBounds();
		if (rect.width < 2) {
			IJ.error("The width of the ROI should be greater than 2.");
			thread = null;
			return;
		}
		if (rect.height < 2) {
			IJ.error("The height of the ROI should be greater than 2.");
			thread = null;
			return;
		}
	 	if (canvas == null) {
	 		canvas  = new MeasureCanvas(imp);
			if (imp.getStack().getSize() > 1)
				imp.setWindow(new StackWindow(imp, canvas));
			else
				imp.setWindow(new ImageWindow(imp, canvas));
			canvas.repaint();
			table.setCanvas(canvas);
		}
		
		Polygon polygon = roi.getPolygon();
		ByteProcessor mask = (ByteProcessor)imp.getMask();
		ByteProcessor bpmask = null;
		
		int area = 0;
		if (mask == null) {
			bpmask = new ByteProcessor(rect.width, rect.height);
			bpmask.setColor(countMeasure);
			bpmask.fill();
			area = rect.width * rect.height;
		}
		else {
			bpmask = new ByteProcessor(mask.getWidth(), mask.getHeight());
			for(int i=0; i<mask.getWidth(); i++)
			for(int j=0; j<mask.getHeight(); j++) {
				if (mask.getPixel(i, j) == 0)
					bpmask.putPixel(i, j, 255);
				else {
					bpmask.putPixel(i, j, countMeasure);
					area++;
				}
			}
		}

		Cursor cursor = getCursor();
		setCursor(new Cursor(Cursor.WAIT_CURSOR));
		int n = imp.getSlice();
		
		walk.reset();
		int nz = imp.getStack().getSize();
		Vector<Measure> measures = table.getMeasures();
		for(int z=1; z<=nz; z++) {
			walk.progress("Frame:" + z, (z/(double)nz)*100.0);
			imp.setSlice(z);
			imp.setRoi(roi);
			ImageProcessor ip = imp.getProcessor().crop();
			
			GroupImage gim = new GroupImage(walk, ip, params);
			(new Gradient(null, gim, params)).run();			
			Measure measure = (new StructureTensor(walk, gim, params)).measure(z, countMeasure, imp, area, rect, polygon, bpmask);
			measures.add(measure); 
			if (log) {
				IJ.log(measure.headings());
				IJ.log(measure.toString());
			}
			table.add(measure);
			
			canvas.setMeasures(measures, table, params);
		}
		walk.finish("Mesure");
		countMeasure++;
		if (canvas != null)
			canvas.repaint();
		setCursor(cursor);
		imp.setSlice(n);
		thread = null;
			
	}
	
}