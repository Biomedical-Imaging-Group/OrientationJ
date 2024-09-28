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

import java.awt.Dimension;

import javax.swing.JEditorPane;
import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;

/**
 * This class extends the Java JEditorPane to make a easy to use panel to
 * display HTML information.
 * 
 * @author Daniel Sage, Biomedical Imaging Group, EPFL, Lausanne, Switzerland.
 * 
 */
public class Help extends JEditorPane {

	private String		html		= "";
	private String		header		= "";
	private String		footer		= "";
	private String		font		= "verdana";
	private String		color		= "#222222";
	private String		background	= "#f8f8f8";

	public Help() {
		header += "<!DOCTYPE HTML PUBLIC \"-//W3C//DTD HTML 3.2//EN\">\n";
		header += "<html><head>\n";
		header += "<style>body {background-color:" + background + "; color:" + color + "; font-family: " + font + ";margin:4px}</style>\n";
		header += "<style>h1 {color:#555555; font-size:1.0em; font-weight:bold; padding:1px; margin:1px; padding-top:5px}</style>\n";
		header += "<style>h2 {color:#333333; font-size:0.9em; font-weight:bold; padding:1px; margin:1px;}</style>\n";
		header += "<style>h3 {color:#000000; font-size:0.9em; font-weight:italic; padding:1px; margin:1px;}</style>\n";
		header += "<style>p, li  {color:" + color + "; font-size:0.9em; padding:1px; margin:0px;}</style>\n";
		header += "</head>\n";
		header += "<body>\n";
		footer += "</body></html>\n";
		setEditable(false);
		setContentType("text/html; charset=ISO-8859-1");

		append("<div style=\"text-align:center\">");
		append("h1", Constants.softname + " " + Constants.version);
		append("p", Constants.date);
		append("<p><u>" + Constants.link + "</u></p>");
		append("<p><i>" + Constants.author + "</i></p>");
		append("</div>");

		append("h2", "Local window");
		append("p", "The structure tensor computes  the orientation and isotropy properties in"
				+ " a local window. Here, the local window is characterized by a 2D Gaussian function"
				+ " of standard deviation &sigma;." 
				+ " The parameter &sigma; (expressed in pixel unit) is a critical parameter that"
				+ " determines the scale of the analysis. It should have a value roughly close to the"
				+ " structure of interest (e.g. thickness of the filament)");
		append("h2", "Gradient");
		append("p", "OrientationJ has different methods to compute the gradient.<ul> "
				+ "<li>The cubic spline is always the best choice, fast, accurate, quasi-isotropic and less boundary artefact" 
				+ "<li>The finite difference gradient is very fast but it has poor isotropy properties</li>"
				+ "<li>The Fourier gradient is exact but it has periodic boundary conditions.</li>"
				+ "</ul>");
		append("h2", "Feature");
		append("p", "Check the feature to show the feature at the end of the processing");


	}
	
	@Override
	public String getText() {
		Document doc = this.getDocument();
		try {
			return doc.getText(0, doc.getLength());
		}
		catch (BadLocationException e) {
			e.printStackTrace();
			return getText();
		}
	}
	
	public void append(String content) {
		html += content;
		setText(header + html + footer);
		setCaretPosition(0);
	}

	public void append(String tag, String content) {
		html += "<" + tag + ">" + content + "</" + tag + ">";
		setText(header + html + footer);
		setCaretPosition(0);
	}

	public JScrollPane getPane() {
		JScrollPane scroll = new JScrollPane(this, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		return scroll;
	}
	
	public void show(int w, int h) {
		JScrollPane scroll = new JScrollPane(this, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		scroll.setPreferredSize(new Dimension(w, h));
		JFrame frame = new JFrame();
		frame.getContentPane().add(scroll);
		frame.pack();
		frame.setVisible(true);	
	}
}
