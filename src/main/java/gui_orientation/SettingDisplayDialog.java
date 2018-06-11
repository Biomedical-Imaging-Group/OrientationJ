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
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JColorChooser;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import ij.gui.GUI;
import orientation.OrientationParameters;

public class SettingDisplayDialog extends JDialog implements ActionListener, WindowListener {

	private GridBagLayout			layout				= new GridBagLayout();
	private GridBagConstraints		constraint			= new GridBagConstraints();
	private JTextField				txtEllipseThickness	= new JTextField(" ", 5);
	private JTextField				txtEllipseOpacity	= new JTextField(" ", 5);
	private JTextField				txtEllipseR			= new JTextField(" ", 5);
	private JTextField				txtEllipseG			= new JTextField(" ", 5);
	private JTextField				txtEllipseB			= new JTextField(" ", 5);
	private JButton					bnEllipseColor		= new JButton("Choose...");

	private JTextField				txtAreaOpacity		= new JTextField(" ", 5);
	private JTextField				txtAreaR			= new JTextField(" ", 5);
	private JTextField				txtAreaG			= new JTextField(" ", 5);
	private JTextField				txtAreaB			= new JTextField(" ", 5);
	private JTextField				txtSigma			= new JTextField("0", 5);
	private JButton					bnAreaColor			= new JButton("Choose...");

	private JButton					bnOK				= new JButton("OK");
	private OrientationParameters	params;

	public SettingDisplayDialog(OrientationParameters params) {
		super(new JFrame(), "Options");
		this.params = params;

		JPanel pnPrefilter = new JPanel(layout);
		pnPrefilter.setBorder(BorderFactory.createTitledBorder("Prefilter"));
		addComponent(pnPrefilter, 1, 0, 1, 1, 2, new JLabel("Laplacian of Gaussian (sigma)"));
		addComponent(pnPrefilter, 1, 3, 1, 1, 2, txtSigma);

		JPanel pnEllipse = new JPanel(layout);
		pnEllipse.setBorder(BorderFactory.createTitledBorder("Ellipse"));
		addComponent(pnEllipse, 1, 0, 1, 1, 2, new JLabel("Thickness"));
		addComponent(pnEllipse, 1, 3, 1, 1, 2, txtEllipseThickness);
		addComponent(pnEllipse, 2, 0, 1, 1, 2, new JLabel("Opacity [0-100]"));
		addComponent(pnEllipse, 2, 3, 1, 1, 2, txtEllipseOpacity);
		addComponent(pnEllipse, 3, 0, 1, 1, 2, new JLabel("Color"));
		addComponent(pnEllipse, 3, 1, 1, 1, 2, txtEllipseR);
		addComponent(pnEllipse, 3, 2, 1, 1, 2, txtEllipseG);
		addComponent(pnEllipse, 3, 3, 1, 1, 2, txtEllipseB);
		addComponent(pnEllipse, 3, 4, 1, 1, 2, bnEllipseColor);

		JPanel pnArea = new JPanel(layout);
		pnArea.setBorder(BorderFactory.createTitledBorder("Area"));
		addComponent(pnArea, 1, 0, 1, 1, 2, new JLabel("Opacity [0-100]"));
		addComponent(pnArea, 1, 3, 1, 1, 2, txtAreaOpacity);
		addComponent(pnArea, 2, 0, 1, 1, 2, new JLabel("Color"));
		addComponent(pnArea, 2, 1, 1, 1, 2, txtAreaR);
		addComponent(pnArea, 2, 2, 1, 1, 2, txtAreaG);
		addComponent(pnArea, 2, 3, 1, 1, 2, txtAreaB);
		addComponent(pnArea, 2, 4, 1, 1, 2, bnAreaColor);

		JPanel pnButton = new JPanel(new FlowLayout());
		pnButton.add(bnOK);

		JPanel pnMain = new JPanel(layout);
		addComponent(pnMain, 0, 0, 1, 1, 8, pnPrefilter);
		addComponent(pnMain, 1, 0, 1, 1, 8, pnEllipse);
		addComponent(pnMain, 2, 0, 1, 1, 8, pnArea);
		addComponent(pnMain, 3, 0, 1, 1, 8, pnButton);

		bnOK.addActionListener(this);
		bnEllipseColor.addActionListener(this);
		bnAreaColor.addActionListener(this);

		addWindowListener(this);
		setLayout(new BorderLayout());
		getContentPane().add(pnMain);
		updateInterface();
		pack();
		setModal(true);
		setResizable(false);
		GUI.center(this);
		setVisible(true);
	}

	/**
	 * Add a component in a panel in the northwest of the cell.
	 */
	private void addComponent(JPanel pn, int row, int col, int width, int height, int space, JComponent comp) {
		constraint.gridx = col;
		constraint.gridy = row;
		constraint.gridwidth = width;
		constraint.gridheight = height;
		constraint.anchor = GridBagConstraints.NORTHWEST;
		constraint.insets = new Insets(space, space, space, space);
		constraint.fill = GridBagConstraints.HORIZONTAL;
		layout.setConstraints(comp, constraint);
		pn.add(comp);
	}

	@Override
	public synchronized void actionPerformed(ActionEvent e) {

		params.sigmaLoG = (new Double(txtSigma.getText())).doubleValue();
		params.colorEllipseR = (new Integer(txtEllipseR.getText())).intValue();
		params.colorEllipseG = (new Integer(txtEllipseG.getText())).intValue();
		params.colorEllipseB = (new Integer(txtEllipseB.getText())).intValue();
		params.colorEllipseOpacity = (new Integer(txtEllipseOpacity.getText())).intValue();
		params.colorEllipseThickness = (new Double(txtEllipseThickness.getText())).doubleValue();
		params.colorAreaR = (new Integer(txtAreaR.getText())).intValue();
		params.colorAreaG = (new Integer(txtAreaG.getText())).intValue();
		params.colorAreaB = (new Integer(txtAreaB.getText())).intValue();
		params.colorAreaOpacity = (new Integer(txtAreaOpacity.getText())).intValue();

		if (e.getSource() == bnOK) {
			dispose();
		}
		else if (e.getSource() == bnEllipseColor) {
			Color c = JColorChooser.showDialog(this, "Choose a color for the ellipse", new Color(params.colorEllipseR, params.colorEllipseG, params.colorEllipseB));
			params.colorEllipseR = c.getRed();
			params.colorEllipseG = c.getGreen();
			params.colorEllipseB = c.getBlue();
		}
		else if (e.getSource() == bnAreaColor) {
			Color c = JColorChooser.showDialog(this, "Choose a color for the area", new Color(params.colorAreaR, params.colorAreaG, params.colorAreaB));
			params.colorAreaR = c.getRed();
			params.colorAreaG = c.getGreen();
			params.colorAreaB = c.getBlue();
		}
		updateInterface();
	}

	private void updateInterface() {
		txtSigma.setText("" + params.sigmaLoG);
		txtEllipseR.setText("" + params.colorEllipseR);
		txtEllipseG.setText("" + params.colorEllipseG);
		txtEllipseB.setText("" + params.colorEllipseB);
		txtEllipseOpacity.setText("" + params.colorEllipseOpacity);
		txtEllipseThickness.setText("" + params.colorEllipseThickness);
		txtAreaR.setText("" + params.colorAreaR);
		txtAreaG.setText("" + params.colorAreaG);
		txtAreaB.setText("" + params.colorAreaB);
		txtAreaOpacity.setText("" + params.colorAreaOpacity);
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