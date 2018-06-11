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

import java.awt.BorderLayout;
import java.awt.Cursor;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.SwingUtilities;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import gui_orientation.components.GridPanel;
import gui_orientation.components.GridToolbar;
import gui_orientation.components.Settings;
import gui_orientation.components.SpinnerDouble;
import gui_orientation.components.SpinnerInteger;
import ij.IJ;
import ij.ImagePlus;
import ij.WindowManager;
import ij.gui.GUI;
import ij.plugin.frame.Recorder;
import orientation.GroupImage;
import orientation.OrientationKMeans;
import orientation.OrientationParameters;
import orientation.OrientationProcess;
import orientation.OrientationResults;
import orientation.OrientationService;
import orientation.imageware.ImageWare;

public class AnalysisDialog extends JDialog implements ActionListener, ChangeListener, WindowListener, Runnable {

	private Settings					settings				= new Settings("OrientationJ", IJ.getDirectory("plugins") + "OrientationJ.txt");
	private Thread					thread				= null;
	protected int					countRun				= 0;

	private String[]				gradientsOperators		= new String[] {
			"Cubic Spline", "Finite Difference", "Fourier",
			"Riesz Filters", "Gaussian", "Hessian" };

	protected OrientationParameters	params;
	protected GroupImage				gim;

	protected WalkBarOrientationJ		walk						= new WalkBarOrientationJ();
	protected JButton				bnRun					= new JButton("Run");
	private JButton					bnHide					= new JButton("Hide");
	private JButton					bnKMeans					= new JButton("K-means");
	
	private SpinnerDouble			spnST					= new SpinnerDouble(1, 0.01, 100, 1);
	private SpinnerDouble			spnLoG					= new SpinnerDouble(0, 0, 100, 0);

	private JComboBox<String>		cmbColorHSB				= new JComboBox<String>(new String[] {"HSB", "RGB"});
	private JComboBox<String>		cmbUnitOrientation		= new JComboBox<String>(new String[] {"rad", "deg"});
	private SpinnerDouble			spnEpsilonCoherency		= new SpinnerDouble(0.01, 0.00001, 10, 0.01);
	private SpinnerDouble			spnHarrisK				= new SpinnerDouble(0.1, 0.01, 0.2, 0.01);
	private SpinnerInteger			spnHarrisL				= new SpinnerInteger(3, 1, 201, 1);
	private SpinnerDouble			spnHarrisMin				= new SpinnerDouble(10, 0, 100.0, 0.1);

	private SpinnerDouble			spnVectorFieldScale		= new SpinnerDouble(80.0, 0, 10000, 1);
	private JComboBox<String>		cmbVectorFieldType		= new JComboBox<String>(new String[] { "Maximum", "~ Energy", "~ Coherency", "~ Ene. x Coh." });
	private SpinnerInteger			spnVectorFieldGrid		= new SpinnerInteger(10, 1, 10000, 1);

	private SpinnerInteger			spnNbClasses				= new SpinnerInteger(3, 1, 10000, 1);
	private ComboFeature				cmbHue					= new ComboFeature("Orientation");
	private ComboFeature				cmbSaturation			= new ComboFeature("Coherency");
	private ComboFeature				cmbBrightness			= new ComboFeature("Original-Image");

	private JLabel					lblHue					= new JLabel("Hue");
	private JLabel					lblSaturation			= new JLabel("Saturation");
	private JLabel					lblBrightness			= new JLabel("Brightness");

	private JCheckBox[]				chkFeature				= new JCheckBox[OrientationParameters.NB_FEATURES];
	protected JButton[]				bnShow					= new JButton[OrientationParameters.NB_FEATURES];
	private JButton					bnDetect					= new JButton("Detect Corners");

	private SpinnerDouble			spnMinEnergy				= new SpinnerDouble(0, 0, 180, 1);
	private SpinnerDouble			spnMinCoherency			= new SpinnerDouble(0, 0, 100, 1);
	private SpinnerDouble			spnDirectionScale		= new SpinnerDouble(100, 0, 1000, 1);
	private JComboBox<String>		cmbGradient				= new JComboBox<String>(gradientsOperators);

	private JCheckBox				showHarrisCornerTable	= new JCheckBox("Show Table", true);
	private JCheckBox				showHarrisCornerOverlay	= new JCheckBox("Overlay", true);
	private JCheckBox				showVectorFieldTable		= new JCheckBox("Show Table", true);
	private JCheckBox				showVectorFieldOverlay	= new JCheckBox("Overlay", true);

	private enum Job {NONE, RUN, HARRIS_CORNERS, VECTOR_FIELD};
	private Job job = Job.NONE;
	
	public AnalysisDialog(OrientationService service) {
		super(new JFrame(), "OrientationJ ");
		String title = "OrientationJ ";
		this.params = new OrientationParameters(service);
		setTitle(title + params.getServiceName());
	}

	public void showDialog() {

		// Panel Tensor
		GridToolbar pnTensor = new GridToolbar(false, 2);
		pnTensor.place(0, 0, new JLabel("Local window \u03C3"));
		pnTensor.place(0, 2, spnST);
		pnTensor.place(0, 3, new JLabel("pixel"));
		pnTensor.place(2, 0, new JLabel("Gradient"));
		pnTensor.place(2, 2, 3, 1, cmbGradient);

		for (int k = 0; k < OrientationParameters.NB_FEATURES; k++) {
			chkFeature[k] = new JCheckBox(OrientationParameters.name[k]);
			bnShow[k] = new JButton("Show");
			bnShow[k].addActionListener(this);
			chkFeature[k].addActionListener(this);
		}

		// Panel Features
		GridToolbar pnFeatures = new GridToolbar(false, 2);
		
		if (params.isServiceAnalysis())
			for (int k = 0; k < 2; k++) {
				pnFeatures.place(k, 1, chkFeature[k]);
				pnFeatures.place(k, 3, bnShow[k]);
			}
		
		for (int k = 2; k < OrientationParameters.SURVEY; k++) {
			if (k != OrientationParameters.HARRIS) {
				pnFeatures.place(k, 1, chkFeature[k]);
				pnFeatures.place(k, 3, bnShow[k]);
			}
			if (k == OrientationParameters.TENSOR_ORIENTATION) {
				pnFeatures.place(k, 4, 2, 1, cmbUnitOrientation);
			}
		}
		if (params.isServiceHarris()) {
			int k = OrientationParameters.HARRIS;
			pnFeatures.place(k, 1, chkFeature[k]);
			pnFeatures.place(k, 3, bnShow[k]);
			pnFeatures.place(k, 4, new JLabel("\u03BA"));
			pnFeatures.place(k, 5, spnHarrisK);
		}

		if (params.isServiceAnalysis()) {
			int k = OrientationParameters.SURVEY;
			pnFeatures.place(k, 1, chkFeature[k]);
			pnFeatures.place(k, 3, bnShow[k]);
			pnFeatures.place(k, 4, 2, 1, cmbColorHSB);
			cmbColorHSB.addActionListener(this);
		}
		
		pnFeatures.place(OrientationParameters.SURVEY+2, 3, bnHide);

		GridPanel pnMain1 = new GridPanel("Structure Tensor", 2);
		pnMain1.place(0, 0, pnTensor);
		pnMain1.place(1, 0, pnFeatures);
		pnMain1.place(2, 0, bnRun);
		
		GridPanel pnMain = new GridPanel(false);
		pnMain.place(0, 0, pnMain1);
	
		// Panel Distribution
		if (params.isServiceDistribution()) {
			GridToolbar pnDistribution = new GridToolbar("Selection", 0);
			GridToolbar pn1 = new GridToolbar(false);
			pn1.place(3, 1, new JLabel("Min. Coherency"));
			pn1.place(3, 2, spnMinCoherency);
			pn1.place(3, 3, new JLabel("%"));
			pn1.place(4, 1, new JLabel("Min. Energy"));
			pn1.place(4, 2, spnMinEnergy);
			pn1.place(4, 3, new JLabel("%"));

			GridToolbar pn2 = new GridToolbar(false);
			for (int k = OrientationParameters.SURVEY + 1; k < OrientationParameters.NB_FEATURES; k++) {
				pn2.place(k, 1, chkFeature[k]);
				pn2.place(k, 3, bnShow[k]);
			}
			pnDistribution.place(1, 0, pn1);
			pnDistribution.place(2, 0, pn2);
			pnMain.place(2, 0, pnDistribution);
		}

		// Panel VectorField
		if (params.isServiceVectorField()) {
			GridPanel pnVectors = new GridPanel("Vector Field");
			pnVectors.place(0, 0, new JLabel("Grid size"));
			pnVectors.place(0, 1, spnVectorFieldGrid);
			pnVectors.place(1, 0, new JLabel("Length vector"));
			pnVectors.place(1, 1, cmbVectorFieldType);
			pnVectors.place(2, 0, new JLabel("Scale vector (%)"));
			pnVectors.place(2, 1, spnVectorFieldScale);
			pnVectors.place(6, 0, showVectorFieldTable);
			pnVectors.place(6, 1, showVectorFieldOverlay);
			showVectorFieldTable.addActionListener(this);
			showVectorFieldOverlay.addActionListener(this);
			spnVectorFieldGrid.addChangeListener(this);
			spnVectorFieldScale.addChangeListener(this);
			cmbVectorFieldType.addActionListener(this);
			pnMain.place(3, 0, pnVectors);
		}

		// Panel Directions
		if (params.isServiceClustering()) {
			GridPanel pnKMeans = new GridPanel("Grouping Orientations");
			pnKMeans.place(2, 0, new JLabel("Classes"));
			pnKMeans.place(2, 1, spnNbClasses);
			pnKMeans.place(2, 2, bnKMeans);
			bnKMeans.addActionListener(this);
			pnMain.place(4, 0, pnKMeans);
		}

		// Panel Harris
		if (params.isServiceHarris()) {
			GridToolbar pnHarris = new GridToolbar("Harris Corner Detection");
			pnHarris.place(2, 0, new JLabel("Window size"));
			pnHarris.place(2, 2, spnHarrisL);
			pnHarris.place(3, 0, new JLabel("Min. level"));
			pnHarris.place(3, 2, spnHarrisMin);
			pnHarris.place(4, 0, showHarrisCornerTable);
			pnHarris.place(4, 2, showHarrisCornerOverlay);
			pnMain.place(4, 0, pnHarris);
			spnHarrisL.addChangeListener(this);
			spnHarrisMin.addChangeListener(this);
			showHarrisCornerTable.addActionListener(this);
			showHarrisCornerOverlay.addActionListener(this);
		}
		
		// Panel Color
		if (params.isServiceAnalysis()) {
			GridToolbar pnColor = new GridToolbar("Color survey");
			pnColor.place(1, 0, lblHue);
			pnColor.place(1, 1, cmbHue);
			pnColor.place(2, 0, lblSaturation);
			pnColor.place(2, 1, cmbSaturation);
			pnColor.place(3, 0, lblBrightness);
			pnColor.place(3, 1, cmbBrightness);
			pnMain.place(5, 0, pnColor);
		}
	
		Help help = new Help();
		help.setPreferredSize(pnMain.getSize());
		
		JPanel pnHelp = new JPanel();
		pnHelp.setLayout(new BoxLayout(pnHelp, BoxLayout.PAGE_AXIS));
		pnHelp.add(new JLabel("Advanced setting coherency"));
		pnHelp.add(new JLabel("Smallest denominator (epsilon)"));
		pnHelp.add(spnEpsilonCoherency);
		
		JPanel pnHelpAdvanced = new JPanel(new BorderLayout());
		pnHelpAdvanced.add(pnHelp, BorderLayout.SOUTH);
		pnHelpAdvanced.add(help.getPane(), BorderLayout.CENTER);
	
		GridPanel pn = new GridPanel(false, 4);
		JTabbedPane tab = new JTabbedPane();
		Credits credits = new Credits();
		credits.setPreferredSize(pnMain.getSize());
		tab.add("Processing", pnMain);
		tab.add("Help", pnHelpAdvanced);
		tab.add("Credits", credits.getPane());
		pn.place(0, 0, tab);
		pn.place(1, 0, walk);

		// Listener
		walk.getButtonClose().addActionListener(this);
		bnRun.addActionListener(this);
		bnHide.addActionListener(this);
		
		// Finalize
		addWindowListener(this);
		getContentPane().add(pn);
		pack();
		setResizable(false);
		GUI.center(this);
		setVisible(true);

		for (int k = 0; k < OrientationParameters.NB_FEATURES; k++)
			settings.record("feature-"+OrientationParameters.name[k], chkFeature[k], false);
	
		settings.record("cmbColorHSB", cmbColorHSB, cmbColorHSB.getItemAt(0));
		settings.record("spnEpsilonCoherency", spnEpsilonCoherency, "0.001");
		settings.record("cmbUnitOrientation", cmbUnitOrientation, cmbUnitOrientation.getItemAt(0));
		settings.record("showHarrisCornerTable", showHarrisCornerTable, true);
		settings.record("showHarrisCornerOverlay", showHarrisCornerOverlay, true);
		settings.record("spnHarrisK", spnHarrisK, "0.1");
		settings.record("spnHarrisL", spnHarrisL, "3");
		settings.record("spnHarrisMin", spnHarrisMin, "10");
		settings.record("spnVectorFieldGrid", spnVectorFieldGrid, "10");
		settings.record("cmbVectorFieldType", cmbVectorFieldType, (String) cmbVectorFieldType.getItemAt(0));
		settings.record("spnVectorFieldScale", spnVectorFieldScale, "100");
		settings.record("showVectorFieldTable", showVectorFieldTable, true);
		settings.record("showVectorFieldOverlay", showVectorFieldOverlay, true);
		settings.record("spnLoG", spnLoG, "0");
		settings.record("spnTensor", spnST, "1");
		settings.record("Color_Hue", cmbHue, "Orientation");
		settings.record("Color_Staturation", cmbSaturation, "Coherency");
		settings.record("Color_Brigthness", cmbBrightness, "Original-Image");
		settings.record("spnMinCoherency", spnMinCoherency, "70.0");
		settings.record("spnMinEnergy", spnMinEnergy, "10.0");
		settings.record("spnNbClasses", spnNbClasses, "3");
		settings.record("spnDirectionScale", spnDirectionScale, "50");
		settings.record("cmbGradient", cmbGradient, gradientsOperators[0]);
		
		settings.loadRecordedItems();
		params.load(settings);
		setParameters();
		updateInterface();
	}

	@Override
	public synchronized void actionPerformed(ActionEvent e) {

		getParameters();
		Object source = e.getSource();
		if (e.getSource() == walk.getButtonClose()) {
			settings.storeRecordedItems();
			params.store(settings);
			dispose();
		}
		
		if (e.getSource() == bnHide) 
			OrientationResults.hide();
		
		for (int k = 0; k < OrientationParameters.NB_FEATURES; k++)
			if (source == bnShow[k] && gim != null)
				OrientationResults.show(k, gim, params, ++countRun);
				
		if (e.getSource() == bnKMeans) {
			if (gim == null)
				return;
			if (gim.orientation == null)
				return;
			OrientationKMeans kmeans = new OrientationKMeans();
			ImageWare out = kmeans.run(gim.orientation, spnNbClasses.get(), 1000);
			out.show("OJ-KMeans " + spnNbClasses.get());
		}
		else if (e.getSource() == cmbColorHSB) 
			params.hsb = cmbColorHSB.getSelectedIndex() == 0;
		else if (e.getSource() == showHarrisCornerOverlay || e.getSource() == showHarrisCornerTable)
			start(Job.HARRIS_CORNERS);
		else if (e.getSource() == showVectorFieldOverlay || e.getSource() == showVectorFieldTable)
			start(Job.VECTOR_FIELD);
		else if (e.getSource() == bnRun) 
			start(Job.RUN);
		else if (gim!=null && e.getSource() == cmbVectorFieldType) 
			start(Job.VECTOR_FIELD);
		updateInterface();
	}

	private void start(Job job) {
		if (thread != null)
			return;
		this.job = job;
		thread = new Thread(this);
		thread.setPriority(Thread.MIN_PRIORITY);
		thread.start();		
	}
	
	@Override
	public void stateChanged(ChangeEvent e) {
		if (e.getSource() == spnHarrisL || e.getSource() == spnHarrisMin)
			start(Job.HARRIS_CORNERS);

		if (e.getSource() == spnVectorFieldGrid || e.getSource() == spnVectorFieldScale) 
			start(Job.VECTOR_FIELD);

		updateInterface();
	}

	public void getParameters() {
		for (int k = 0; k < OrientationParameters.NB_FEATURES; k++) 
			params.view[k] = chkFeature[k].isSelected();
		params.featureHue = (String) cmbHue.getSelectedItem();
		params.featureSat = (String) cmbSaturation.getSelectedItem();
		params.featureBri = (String) cmbBrightness.getSelectedItem();
		params.sigmaST = spnST.get();
		params.sigmaLoG = spnLoG.get();
		params.harrisK = spnHarrisK.get();
		params.minCoherency = spnMinCoherency.get();
		params.minEnergy = spnMinEnergy.get();
		params.gradient = cmbGradient.getSelectedIndex();
		params.epsilon = spnEpsilonCoherency.get();
		params.radian = cmbUnitOrientation.getSelectedIndex() == 0;
		params.hsb = cmbColorHSB.getSelectedIndex() == 0;

		params.vectorGrid = spnVectorFieldGrid.get();
		params.vectorType = cmbVectorFieldType.getSelectedIndex();
		params.vectorScale = spnVectorFieldScale.get();
		
		params.harrisL = spnHarrisL.get();
		params.harrisMin = spnHarrisMin.get();
		params.showVectorOverlay = showVectorFieldOverlay.isSelected();
		params.showVectorTable = showVectorFieldTable.isSelected();
		params.showHarrisOverlay = showHarrisCornerOverlay.isSelected();
		params.showHarrisTable = showHarrisCornerTable.isSelected();
	}
	
	public void setParameters() {
		for (int k = 0; k < OrientationParameters.NB_FEATURES; k++) 
			 chkFeature[k].setSelected(params.view[k]);
		cmbHue.setSelectedItem(params.featureHue);
		cmbSaturation.setSelectedItem(params.featureSat);
		cmbBrightness.setSelectedItem(params.featureBri);
		spnST.set(params.sigmaST);
		spnLoG.set(params.sigmaLoG);
		spnHarrisK.set(params.harrisK);
		spnMinCoherency.set(params.minCoherency);
		spnMinEnergy.set(params.minEnergy);
		cmbGradient.setSelectedIndex(params.gradient);
		spnEpsilonCoherency.set(params.epsilon);
		cmbUnitOrientation.setSelectedIndex(params.radian ? 0 : 1);
		cmbColorHSB.setSelectedIndex(params.hsb ? 0 : 1);

		spnVectorFieldGrid.set(params.vectorGrid);
		cmbVectorFieldType.setSelectedIndex(params.vectorType);
		spnVectorFieldScale.set(params.vectorScale);
		
		spnHarrisL.set(params.harrisL);
		spnHarrisMin.set(params.harrisMin);
		showVectorFieldOverlay.setSelected(params.showVectorOverlay);
		showVectorFieldTable.setSelected(params.showVectorTable);
		showHarrisCornerOverlay.setSelected(params.showHarrisOverlay);
		showHarrisCornerTable.setSelected(params.showHarrisTable);
	}

	@Override
	public void run() {		
		getParameters();
		walk.reset();
		
		if (job == Job.RUN) {
			ImageWare source = GroupImage.getCurrentImage();
			if (source == null) {
				thread = null;
				return;
			}
			recordMacroParameters();
			Cursor cursor = getCursor();
			setCursor(new Cursor(Cursor.WAIT_CURSOR));
			OrientationProcess process = new OrientationProcess(walk, source, params);
			process.start();
			while (process.isAlive()) {
			}
			gim = process.getGroupImage();
			OrientationResults.show(params.view, gim, params, ++countRun);
			setCursor(cursor);
			if (params.isServiceHarris())
				OrientationResults.displayHarris(gim, params, ++countRun);		
			if (params.isServiceVectorField())
				OrientationResults.displayVectorField(gim, params, ++countRun);		
		}
		
		if (job == Job.HARRIS_CORNERS)
			OrientationResults.displayHarris(gim, params, ++countRun);		
	
		if (job == Job.VECTOR_FIELD)
			OrientationResults.displayVectorField(gim, params, ++countRun);
		
		walk.finish();
		updateInterface();
		thread = null;
	}

	public void updateInterface() {

		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				int list[] = WindowManager.getIDList();
				bnHide.setEnabled(false);
				if (list != null) {
					for (int i = 0; i < list.length; i++) {
						ImagePlus imp = WindowManager.getImage(list[i]);
						for (int feature = 0; feature < OrientationParameters.NB_FEATURES; feature++)
							for (int k = 0; k <= countRun; k++) {
								if (imp.getTitle().startsWith("OJ")) {
									bnHide.setEnabled(true);
								}
							}
					}
				}

				// Enable the show button
				if (gim != null) {
					bnShow[OrientationParameters.GRADIENT_HORIZONTAL].setEnabled(gim.gx != null || gim.hxx != null);
					bnShow[OrientationParameters.GRADIENT_VERTICAL].setEnabled(gim.gy != null || gim.hyy != null);
					bnShow[OrientationParameters.TENSOR_ORIENTATION].setEnabled(gim.orientation != null);
					bnShow[OrientationParameters.TENSOR_COHERENCY].setEnabled(gim.coherency != null);
					bnShow[OrientationParameters.TENSOR_ENERGY].setEnabled(gim.energy != null);
					bnShow[OrientationParameters.HARRIS].setEnabled(gim.harris != null);
					bnShow[OrientationParameters.SURVEY].setEnabled(gim != null);
					bnShow[OrientationParameters.DIST_HISTO_PLOT].setEnabled(gim != null);
					bnShow[OrientationParameters.DIST_MASK].setEnabled(gim != null);
					bnShow[OrientationParameters.DIST_ORIENTATION].setEnabled(gim != null);
					bnShow[OrientationParameters.DIST_HISTO_TABLE].setEnabled(gim != null);
					bnDetect.setEnabled(gim.harris != null);
					bnKMeans.setEnabled(gim.orientation != null);
				}
				else {
					bnDetect.setEnabled(false);
					bnKMeans.setEnabled(false);
					for (int k = 0; k < OrientationParameters.NB_FEATURES; k++)
						bnShow[k].setEnabled(false);
				}

				// Enabled the color survey channels
				if (cmbColorHSB.getSelectedIndex() != 0) {
					lblHue.setText("Red");
					lblSaturation.setText("Green");
					lblBrightness.setText("Blue");
				}
				else {
					lblHue.setText("Hue");
					lblSaturation.setText("Saturation");
					lblBrightness.setText("Brightness");
				}
				if (params.gradient == OrientationParameters.HESSIAN) {
					if (chkFeature[0] != null)
						chkFeature[0].setText("Hessian-XX");
					if (chkFeature[1] != null)
						chkFeature[1].setText("Hessian-YY");
				}
				else {
					if (chkFeature[0] != null)
						chkFeature[0].setText("Gradient-X");
					if (chkFeature[1] != null)
						chkFeature[1].setText("Gradient-Y");
				}
			}
		});

	}

	private void recordMacroParameters() {
		if (!Recorder.record)
			return;
		String options = "";
		String plugin = "OrientationJ " + params.getServiceName();
		
		options += "tensor=" + spnST.get() + " ";
		options += "gradient=" + cmbGradient.getSelectedIndex() + " ";

		int k = 0;
		if (params.isServiceAnalysis()) {
			k = OrientationParameters.SURVEY;	
			options += params.view[k] ? OrientationParameters.name[k].toLowerCase() + "=on " : "";
			k = OrientationParameters.GRADIENT_HORIZONTAL;
			options += params.view[k] ? OrientationParameters.name[k].toLowerCase() + "=on " : "";
			k = OrientationParameters.GRADIENT_VERTICAL;
			options += params.view[k] ? OrientationParameters.name[k].toLowerCase() + "=on " : "";
			options += "hsb=" + (params.hsb ? "on ": "off "); 
			options += "hue=" + cmbHue.getSelectedItem() + " ";
			options += "sat=" + cmbSaturation.getSelectedItem() + " ";
			options += "bri=" + cmbBrightness.getSelectedItem() + " ";
		}

		k = OrientationParameters.TENSOR_ENERGY;
		options += params.view[k] ? OrientationParameters.name[k].toLowerCase() + "=on " : "";
		k = OrientationParameters.TENSOR_ORIENTATION;
		options += params.view[k] ? OrientationParameters.name[k].toLowerCase() + "=on " : "";
		k = OrientationParameters.TENSOR_COHERENCY;
		options += params.view[k] ? OrientationParameters.name[k].toLowerCase() + "=on " : "";
		options += "radian=" + (params.radian ? "on ": "off "); 

		// Distribution
		if (params.isServiceDistribution()) {
			k = OrientationParameters.DIST_MASK;	
			options += params.view[k] ? OrientationParameters.name[k].toLowerCase() + "=on " : "";
			k = OrientationParameters.DIST_ORIENTATION;	
			options += params.view[k] ? OrientationParameters.name[k].toLowerCase() + "=on " : "";
			k = OrientationParameters.DIST_HISTO_PLOT;	
			options += params.view[k] ? OrientationParameters.name[k].toLowerCase() + "=on " : "";
			k = OrientationParameters.DIST_HISTO_TABLE;	
			options += params.view[k] ? OrientationParameters.name[k].toLowerCase() + "=on " : "";
			options += "min-coherency=" + spnMinCoherency.get() + " ";
			options += "min-energy=" + spnMinEnergy.get() + " ";
		}

		if (params.isServiceHarris()) {
			k = OrientationParameters.HARRIS;	
			options += params.view[k] ? OrientationParameters.name[k].toLowerCase() + "=on " : "";
			options += "harrisk=" + spnHarrisK.get() + " ";
			options += "harrisl=" + spnHarrisL.get() + " ";
			options += "harrismin=" + spnHarrisMin.get() + " ";
			options += params.showHarrisOverlay ? "harrisoverlay=on " : "harrisoverlay=off ";
			options += params.showHarrisTable ? "harristable=on " : "harristable=off ";
		}
	
		if (params.isServiceVectorField()) {
			options += "vectorgrid=" + spnVectorFieldGrid.get() + " ";
			options += "vectorscale=" + spnVectorFieldScale.get() + " ";
			options += "vectortype=" + cmbVectorFieldType.getSelectedIndex();
			options += params.showVectorOverlay ? "vectoroverlay=on " : "vectoroverlay=off ";
			options += params.showVectorTable ? "vectortable=on " : "vectortable=off ";
		}
		
		Recorder.record("run", plugin, options);
	}

	public OrientationParameters getSettingParameters() {
		return params;
	}

	@Override
	public void windowActivated(WindowEvent e) {
	}

	@Override
	public void windowClosed(WindowEvent e) {
	}

	@Override
	public void windowDeactivated(WindowEvent e) {
	}

	@Override
	public void windowDeiconified(WindowEvent e) {
	}

	@Override
	public void windowIconified(WindowEvent e) {
	}

	@Override
	public void windowOpened(WindowEvent e) {
	}

	@Override
	public void windowClosing(WindowEvent e) {
		dispose();
	}

}
