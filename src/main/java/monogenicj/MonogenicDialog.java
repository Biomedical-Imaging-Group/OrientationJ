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

import static gui_orientation.Chrono.tic;
import static gui_orientation.Chrono.toc;

import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;

import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JRadioButton;
import javax.swing.JTabbedPane;

import gui_orientation.components.GridPanel;
import gui_orientation.components.GridToolbar;
import gui_orientation.components.Settings;
import gui_orientation.components.SpinnerDouble;
import gui_orientation.components.SpinnerInteger;
import gui_orientation.components.WalkBar;
import ij.IJ;
import ij.ImagePlus;
import ij.WindowManager;
import ij.gui.GUI;
import orientation.imageware.Builder;
import orientation.imageware.ImageWare;


public class MonogenicDialog extends JDialog implements ActionListener, WindowListener, Runnable {

	private Settings		settings		= new Settings("MonogenicJ", IJ.getDirectory("plugins") + "MonogenicJ.txt");
	private Thread			thread			= null;
	private JButton			job				= null;
	private String			list[]			= new String[] {"Input",  "Laplace", "Riesz X", "Riesz Y", "Orientation", "Coherency", "Energy", "Wavenumber", "Modulus", "Phase", "Dir. Hilbert", "Maximum"};
	
	private WalkBar			walk			= new WalkBar("(c) 2009 EPFL, BIG", true, false, true, 40);
	private SpinnerDouble	spnSigma		= new SpinnerDouble(3, 0, 100, 1);
	private SpinnerInteger	spnScale		= new SpinnerInteger(2, 1, 16, 1);
	private SpinnerDouble	spnEpsilon		= new SpinnerDouble(1, 0, 32, 1);
	private JButton			bnRun			= new JButton("Run");
	
	private JCheckBox		ckSignedDir		= new JCheckBox("Signed Dir.", false);
	private JCheckBox		ckPrefilter		= new JCheckBox("Prefilter", false);
	private JCheckBox		ckGradientX		= new JCheckBox("Riesz X", false);
	private JCheckBox		ckGradientY		= new JCheckBox("Riesz Y", false);
	private JCheckBox		ckOrientation	= new JCheckBox("Orientation", true);
	private JCheckBox		ckCoherency		= new JCheckBox("Coherency", true);
	private JCheckBox		ckEnergy		= new JCheckBox("Energy", false);
	private JCheckBox		ckFrequency		= new JCheckBox("Wavenumber", true);
	private JCheckBox		ckDirHilbert	= new JCheckBox("Dir. Hilbert", true);
	private JCheckBox		ckModulus		= new JCheckBox("Modulus", true);
	private JCheckBox		ckPhase			= new JCheckBox("Phase", true);
	private JCheckBox		ckLaplace		= new JCheckBox("Laplace", true);
	private JRadioButton	rbPyramid		= new JRadioButton("Pyramid", true);
	private JRadioButton	rbRedundant		= new JRadioButton("Redundant", true);
	
	private JCheckBox		ckColor			= new JCheckBox("Color map", true);
	private JComboBox<String>		cmbHue			= new JComboBox<String>(list);
	private JComboBox<String>		cmbSat			= new JComboBox<String>(list);
	private JComboBox<String>		cmbBri			= new JComboBox<String>(list);
	private JComboBox<String>		cmbScaled		= new JComboBox<String>(new String[] {"Scaled values / bands", "True values"});
	private JComboBox<String>		cmbStacked		= new JComboBox<String>(new String[] {"Stacked presentation", "Horizontal Flatten", "Vertical Flatten"});

	private JButton			showGradientX	= new JButton(" Show ");
	private JButton			showGradientY	= new JButton(" Show ");
	private JButton			showCoherency	= new JButton(" Show ");
	private JButton			showEnergy		= new JButton(" Show ");
	private JButton			showOrientation	= new JButton(" Show ");
	private JButton			showFrequency	= new JButton(" Show ");
	private JButton			showDirHilbert	= new JButton(" Show ");
	private JButton			showModulus		= new JButton(" Show ");
	private JButton			showPhase		= new JButton(" Show ");
	private JButton			showLaplace		= new JButton(" Show ");
	private JButton			showColor		= new JButton(" Show ");
	
	private MonogenicImage	mgim;

	/**
	* Constructor.
	*/
	public MonogenicDialog() {
		super(new Frame(), "MonogenicJ");
		walk.fillAbout(
			"MonogenicJ", 
			"Version 21.09.2009",
			"Reference: M. Unser, D. Sage, D. Van De Ville, Multiresolution Monogenic Signal Analysis Using the Riesz-Laplace Wavelet Transform, IEEE Transactions on Image Processing, in press.",
			"",
			"Biomedical Imaging Group (BIG)<br>Ecole Polytechnique F&eacute;d&eacute;rale de Lausanne (EPFL)<br>Lausanne, Switzerland",
			"21 September 2009",
			"http://bigwww.epfl.ch/demo/monogenic/");		
	 	doDialog();
		
		cmbHue.setSelectedIndex(3);
		cmbSat.setSelectedIndex(4);
		
		settings.record("MonogenicJ-ckGradientX",	ckGradientX,	false);
		settings.record("MonogenicJ-ckGradientY",	ckGradientY,	false);
		settings.record("MonogenicJ-ckOrientation", ckOrientation,	false);
		settings.record("MonogenicJ-ckCoherency",	ckCoherency,	false);
		settings.record("MonogenicJ-ckEnergy",		ckEnergy,		false);
		settings.record("MonogenicJ-ckColor",		ckColor,		true);
		settings.record("MonogenicJ-ckOrientation",	ckOrientation,	false);
		settings.record("MonogenicJ-ckFrequency",	ckFrequency,	false);
		settings.record("MonogenicJ-ckDirHilbert",	ckDirHilbert,	false);
		settings.record("MonogenicJ-ckModulus",		ckModulus,		false);
		settings.record("MonogenicJ-ckPhase",		ckPhase,		false);
		settings.record("MonogenicJ-ckLaplace",		ckLaplace,		false);
		settings.record("MonogenicJ-cmbHue",		cmbHue,			"Orientation");
		settings.record("MonogenicJ-cmbSat",		cmbSat,			"Coherency");
		settings.record("MonogenicJ-cmbBri",		cmbBri,			"Input");
		settings.record("MonogenicJ-spnSigma",		spnSigma,		"1");
		settings.record("MonogenicJ-spnScale",		spnScale,		"1");
		settings.record("MonogenicJ-spnEpsilon",	spnEpsilon,		"1");
		settings.record("MonogenicJ-ckPyramid",		rbPyramid,		true);
		settings.record("MonogenicJ-rbRedundant",	rbRedundant,	false);
		settings.record("MonogenicJ-cmbScaled",		cmbScaled,		"True values");
		settings.record("MonogenicJ-cmbStacked",	cmbStacked,		"Stacked presentation");
		settings.record("MonogenicJ-ckPrefilter",	ckPrefilter,	false);
		settings.record("MonogenicJ-ckSignedDir",	ckSignedDir,	false);
		
		settings.loadRecordedItems();
	}

	/**
	* Build the dialog box.
	*/
	private void doDialog() {
	
		JLabel lblRiesz = new JLabel("Monogenic Components");
		JLabel lblTensor = new JLabel("Structure Tensor");
		JLabel lblMonogenic = new JLabel("Monogenic Analysis");
		
		lblRiesz.setBorder(BorderFactory.createEtchedBorder());
		lblTensor.setBorder(BorderFactory.createEtchedBorder());
		lblMonogenic.setBorder(BorderFactory.createEtchedBorder());
		lblRiesz.setAlignmentX(JLabel.CENTER_ALIGNMENT);
		ButtonGroup group = new ButtonGroup();
		group.add(rbPyramid);
		group.add(rbRedundant);
		
		GridPanel pn0 = new GridPanel(false);
		pn0.place(0, 0, rbPyramid);
		pn0.place(0, 1, rbRedundant);
		pn0.place(1, 0, new JLabel("Nb of Scale"));
		pn0.place(1, 1, spnScale);
		//pn0.place(2, 0, new JLabel("\u03B5_ST x10e-6"));
		//pn0.place(2, 1, spnEpsilon);
		pn0.place(3, 0, new JLabel("Sigma [Tensor]"));
		pn0.place(3, 1, spnSigma);
		//pn0.place(4, 0, ckPrefilter);
		//pn0.place(4, 1, ckSignedDir);

		
		GridPanel pnRun = new GridPanel(false, 0);
		pnRun.place(2, 2, 1, 1, bnRun);
		
		GridToolbar pn1 = new GridToolbar("Features");
		pn1.place(1, 0, 2, 1, lblRiesz);
		pn1.place(2, 0, ckLaplace);
		pn1.place(3, 0, ckGradientX);
		pn1.place(4, 0, ckGradientY);
		
		pn1.place(6, 0, 2, 1, lblTensor);
		pn1.place(7, 0, ckOrientation);
		pn1.place(8, 0, ckCoherency);
		pn1.place(9, 0, ckEnergy);
		
		pn1.place(12, 0, 2, 1, lblMonogenic);
		pn1.place(14, 0, ckFrequency);
		pn1.place(15, 0, ckModulus);
		pn1.place(16, 0, ckPhase);
		pn1.place(17, 0, ckDirHilbert);
		
		pn1.place( 2, 1, showLaplace);
		pn1.place( 3, 1, showGradientX);
		pn1.place( 4, 1, showGradientY);
		pn1.place( 7, 1, showOrientation);
		pn1.place( 8, 1, showCoherency);
		pn1.place( 9, 1, showEnergy);
		pn1.place(14, 1, showFrequency);
		pn1.place(15, 1, showModulus);
		pn1.place(16, 1, showPhase);
		pn1.place(17, 1, showDirHilbert);

		pn1.place(20, 0, 2, 1, cmbScaled);
		pn1.place(21, 0, 2, 1, cmbStacked);

		GridToolbar pn2 = new GridToolbar(false);
		pn2.place(0, 0, new JLabel("Hue"));
		pn2.place(1, 0, new JLabel("Saturation"));
		pn2.place(2, 0, new JLabel("Brightness"));
		pn2.place(0, 2, cmbHue);
		pn2.place(1, 2, cmbSat);
		pn2.place(2, 2, cmbBri);
		pn2.place(5, 0, ckColor);
		pn2.place(5, 2, showColor);
	
		cmbHue.setFont(showGradientX.getFont());
		cmbSat.setFont(showGradientX.getFont());
		cmbBri.setFont(showGradientX.getFont());
		
		JTabbedPane tabbedPane = new JTabbedPane();
		tabbedPane.addTab("Parameters", pn0);
		tabbedPane.addTab("Color Map", pn2);
				
		GridPanel panel = new GridPanel(false, 5);
		//panel.place(0, 0, pn0);
		panel.place(1, 0, pn1);
		panel.place(2, 0, tabbedPane);
		panel.place(3, 0, 1, 1, 0, pnRun);
		
		panel.place(4, 0, walk);
			
		// Add Listeners
		walk.getButtonClose().addActionListener(this);
		showGradientX.addActionListener(this);
		showGradientY.addActionListener(this);
		showOrientation.addActionListener(this);
		showCoherency.addActionListener(this);
		showEnergy.addActionListener(this);
		showColor.addActionListener(this);
		showFrequency.addActionListener(this);
		showDirHilbert.addActionListener(this);
		showModulus.addActionListener(this);
		showPhase.addActionListener(this);
		showLaplace.addActionListener(this);
		bnRun.addActionListener(this);
		addWindowListener(this);
		
		add(panel);
		setResizable(true);
		pack();
		GUI.center(this);
		setVisible(true);
	}
	
	/**
	* Implements the actionPerformed for the ActionListener.
	*/
	public synchronized  void actionPerformed(ActionEvent e) {
	
		if (e.getActionCommand().equals("Close")) {
			settings.storeRecordedItems();
			dispose();
			return;
		}
		
		if (e.getSource() == bnRun) {
			job = (JButton)e.getSource();
			if (thread == null) {
				thread = new Thread(this);
				thread.setPriority(Thread.MIN_PRIORITY);
				thread.start();
			}
			return;
		}

		if (mgim == null) {
			IJ.error("Run first the algorithm before to show the result");
			return;
		}
		else {
			showFeatures((JButton)e.getSource());
		}
		notify();
	}
	
	
	/**
	* Implements the run for the Runnable.
	*/
	public void run() {
	
		tic();
		if (job == bnRun) {
			ImagePlus imp = getInputImage();

			if (imp != null) {
				int scale = spnScale.get();
				boolean pyramid = rbPyramid.isSelected();
				
				mgim = new MonogenicImage(walk, imp.getProcessor(), scale, pyramid, spnSigma.get());
				mgim.compute(spnSigma.get(), 10e-8, ckPrefilter.isSelected(), ckSignedDir.isSelected());
		
				if (ckLaplace.isSelected())
					showFeatures(showLaplace);
				if (ckGradientX.isSelected())
					showFeatures(showGradientX);
				if (ckGradientY.isSelected())
					showFeatures(showGradientY);
				if (ckOrientation.isSelected())
					showFeatures(showOrientation);
				if (ckCoherency.isSelected())
					showFeatures(showCoherency);
				if (ckEnergy.isSelected())
					showFeatures(showEnergy);
				if (ckFrequency.isSelected())
					showFeatures(showFrequency);
				if (ckDirHilbert.isSelected())
					showFeatures(showDirHilbert);
				if (ckModulus.isSelected())
					showFeatures(showModulus);
				if (ckPhase.isSelected())
					showFeatures(showPhase);
				if (ckColor.isSelected())
					showFeatures(showColor);
			}
		}
		walk.setMessage(toc("End:"));
		thread = null;
	}

	/**
	*/
	private void showFeatures(JButton bn) {
	
		int scaled = cmbScaled.getSelectedIndex();
		int stacked = cmbStacked.getSelectedIndex();
		if (bn == showGradientX)
			DisplayPyramid.show(mgim.rx, "Riesz X", scaled, stacked, mgim.pyramid);
		else if (bn == showGradientY)
			DisplayPyramid.show(mgim.ry, "Riesz Y", scaled, stacked, mgim.pyramid);
		else if (bn == showOrientation)
			DisplayPyramid.show(mgim.orientation, "Orientation", DisplayPyramid.NORESCALE, stacked, mgim.pyramid);
		else if (bn == showCoherency)
			DisplayPyramid.show(mgim.coherency, "Coherency", scaled, stacked, mgim.pyramid);
		else if (bn == showEnergy)
			DisplayPyramid.show(mgim.energy, "Energy", scaled, stacked, mgim.pyramid);
		else if (bn == showFrequency)
			DisplayPyramid.show(mgim.monogenicFrequency, "Monogenic Wavenumber", scaled, stacked, mgim.pyramid);
		else if (bn == showDirHilbert)
			DisplayPyramid.show(mgim.directionalHilbert, "Directional Hilbert", scaled, stacked, mgim.pyramid);
		else if (bn == showModulus)
			DisplayPyramid.show(mgim.monogenicModulus, "Monogenic Modulus", scaled, stacked, mgim.pyramid);
		else if (bn == showPhase)
			DisplayPyramid.show(mgim.monogenicPhase, "Monogenic Phase", DisplayPyramid.NORESCALE, stacked, mgim.pyramid);
		else if (bn == showLaplace)
			DisplayPyramid.show(mgim.laplace, "Laplace", scaled, stacked, mgim.pyramid);
		else if (bn == showColor) {
			ImageWare hue = selectColor(cmbHue);
			ImageWare sat = selectColor(cmbSat);
			ImageWare bri = selectColor(cmbBri);
			DisplayPyramid.colorHSB("Color Survey", hue, sat, bri, stacked, mgim.pyramid);
		}
	}
		
	/**
	*/
	private ImageWare selectColor(JComboBox<String> cmb) {
		ImageWare out = null;
		String item = (String)cmb.getSelectedItem();
		if (item.equals("Laplace")) {
			out = mgim.laplace.convert(ImageWare.FLOAT);
			out.rescale(0, 1);
		}
		else if (item.equals("Riesz X")) {
			out = mgim.rx.duplicate();
			out.rescale(0, 1);
		}
		else if (item.equals("Riesz Y")) {
			out = mgim.ry.duplicate();
			out.rescale(0, 1);
		}
		else if (item.equals("Orientation")) {
			out = DisplayPyramid.rescaleAngle(mgim.orientation, mgim.pyramid);
		}
		else if (item.equals("Coherency")) {
			out = mgim.coherency.duplicate();
		}
		else if (item.equals("Energy")) {
			out = mgim.energy.duplicate();
			out.rescale(0, 1);
		}
		else if (item.equals("Wavenumber")) {
			out = mgim.monogenicFrequency.convert(ImageWare.FLOAT);
			out.rescale(0, 1);
		}
		else if (item.equals("Modulus")) {
			out = mgim.monogenicModulus.convert(ImageWare.FLOAT);
			out.rescale(0, 1);
		}
		else if (item.equals("Phase")) {
			out = DisplayPyramid.rescaleAngle(mgim.monogenicPhase, mgim.pyramid);
		}
		else if (item.equals("Dir. Hilbert")) {
			out = mgim.directionalHilbert.convert(ImageWare.FLOAT);
			out.rescale(0, 1);
		}
		else if (item.equals("Maximum")) {
			out = Builder.create(mgim.nx, mgim.ny, mgim.scale, ImageWare.FLOAT);
			out.fillConstant(1.0);
		}
		else {
			return mgim.sourceColorChannel;
		}
		return out;
	}
	
	/**
	*/
	private ImagePlus getInputImage() {
		ImagePlus imp = WindowManager.getCurrentImage();
		if (imp == null) {
			IJ.error("No open image.");
			return null;
		}
		if (imp.getType() != ImagePlus.GRAY8 && imp.getType() != ImagePlus.GRAY16 && imp.getType() != ImagePlus.GRAY32) {
			IJ.error("Only processed 8-bits, 16-bits, or 32 bits images.");
			return null;
		}
		if (imp.getStack().getSize() > 1) {
			IJ.error("Do not processed stack of images.");
			return null;
		}
		int m = 1;
		for (int s=0; s<spnScale.get(); s++) 
			m *= 2;

		int nx = imp.getWidth();
		int ny = imp.getHeight();
		
		if (nx % m != 0) {
			IJ.error("The width [" + nx + "] of the input image is not a multiple of 2^scale [" + m + "].");
			return null;
		}
		if (ny % m != 0) {
			IJ.error("The height  [" + ny + "] of the input image is not a multiple of 2^scale [" + m + "].");
			return null;
		}
		return imp;
	}

	/**
	* Implements the methods for the WindowListener.
	*/
	public void windowActivated(WindowEvent e) 		{}
	public void windowClosed(WindowEvent e) 		{}
	public void windowDeactivated(WindowEvent e) 	{}
	public void windowDeiconified(WindowEvent e)	{}
	public void windowIconified(WindowEvent e)		{}
	public void windowOpened(WindowEvent e)			{}			
	public void windowClosing(WindowEvent e) 		{ 
		dispose();
	}


}

