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
public class Credits extends JEditorPane {

	private String	html		= "";
	private String	header		= "";
	private String	footer		= "";
	private String	font		= "verdana";
	private String	color		= "#222222";
	private String	background	= "#f8f8f8";

	private String ref2 = 
			"R. Rezakhaniha et al. "
			+ "Experimental Investigation of Collagen Waviness and Orientation in the Arterial "
			+ "Adventitia Using Confocal Laser Scanning Microscopy,"
			+ " Biomechanics and Modeling in Mechanobiology, 2012.\n";
			
	private String bib2 = 
			"@ARTICLE(http://bigwww.epfl.ch/publications/rezakhaniha1201.html,\n" + 
			"AUTHOR=\"Rezakhaniha, R. and Agianniotis, A. and Schrauwen, J.T.C. and\n" + 
			"        Griffa, A. and Sage, D. and Bouten, C.V.C. and van de Vosse, F.N.\n" + 
			"        and Unser, M. and Stergiopulos, N.\",\n" + 
			"TITLE=\"Experimental Investigation of Collagen Waviness and Orientation\n" + 
			"        in the Arterial Adventitia Using Confocal Laser Scanning\n" + 
			"        Microscopy\",\n" + 
			"JOURNAL=\"Biomechanics and Modeling in Mechanobiology\",\n" + 
			"YEAR=\"2012\",\n" + 
			"volume=\"11\",\n" + 
			"number=\"3-4\",\n" + 
			"pages=\"461--473\",\n" + 
			"month=\"\",\n" + 
			"note=\"\")";
			
	private String ref1 = 
			"E. Fonck et al. "
			+ "Effect of Aging on Elastin Functionality in Human Cerebral Arteries,"
			+ " Stroke, 2009.\n";
			String bib1 = 
			"@ARTICLE(http://bigwww.epfl.ch/publications/fonck0901.html,\n" + 
			"AUTHOR=\"Fonck, E. and Feigl, G.G. and Fasel, J. and Sage, D. and Unser,\n" + 
			"        M. and R{\\\"{u}}fenacht, D.A. and Stergiopulos, N.\",\n" + 
			"TITLE=\"Effect of Aging on Elastin Functionality in Human Cerebral\n" + 
			"        Arteries\",\n" + 
			"JOURNAL=\"Stroke\",\n" + 
			"YEAR=\"2009\",\n" + 
			"volume=\"40\",\n" + 
			"number=\"7\",\n" + 
			"pages=\"2552--2556\",\n" + 
			"month=\"July\",\n" + 
			"note=\"\")";
			
	private String ref0 = 
			"Z. Püspöki et al., "
			+ "Transforms and Operators for Directional Bioimage Analysis: A Survey, "
			+ "Advances in Anatomy, Embryology and Cell Biology, vol. 219, "
			+ "Springer International Publishing, 2016.";
			
	private String bib0 = 
			"@INCOLLECTION(http://bigwww.epfl.ch/publications/puespoeki1603.html,\n" + 
			"AUTHOR=\"P{\\\"{u}}sp{\\\"{o}}ki, Z. and Storath, M. and Sage, D. and Unser,\n" + 
			"        M.\",\n" + 
			"TITLE=\"Transforms and Operators for Directional Bioimage Analysis: {A}\n" + 
			"        Survey\",\n" + 
			"BOOKTITLE=\"Focus on Bio-Image Informatics\",\n" + 
			"PUBLISHER=\"Springer International Publishing\",\n" + 
			"YEAR=\"2016\",\n" + 
			"editor=\"De Vos, W.H. and Munck, S. and Timmermans, J.-P.\",\n" + 
			"volume=\"219\",\n" + 
			"series=\"Advances in Anatomy, Embryology and Cell Biology\",\n" + 
			"type=\"\",\n" + 
			"chapter=\"3\",\n" + 
			"pages=\"69--93\",\n" + 
			"address=\"\",\n" + 
			"edition=\"\",\n" + 
			"month=\"May 21,\",\n" + 
			"note=\"\")";

	public Credits() {
		header += "<!DOCTYPE HTML PUBLIC \"-//W3C//DTD HTML 3.2//EN\">\n";
		header += "<html><head>\n";
		header += "<style>body {background-color:" + background + "; color:" + color + "; font-family: " + font + ";margin:4px}</style>\n";
		header += "<style>h1 {color:#555555; font-size:1.0em; font-weight:bold; padding:1px; margin:1px; padding-top:5px}</style>\n";
		header += "<style>h2 {color:#333333; font-size:0.9em; font-weight:bold; padding:1px; margin:1px;}</style>\n";
		header += "<style>h3 {color:#000000; font-size:0.9em; font-weight:italic; padding:1px; margin:1px;}</style>\n";
		header += "<style>p  {color:" + color + "; font-size:0.9em; padding:1px; margin:0px;}</style>\n";
		header += "<style>pre  {font-size:0.8em; padding:1px; margin:0px;}</style>\n";
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
		
		append("h2", "Reference on the method");
		append("p", ref0);

		append("h2", "Reference on angular distribution");
		append("p", ref1);

		append("h2", "Reference on local measurements");
		append("p", ref2);

		append("h2", "BibTeX");
		append("pre", bib0);
		append("h2", "BibTeX");
		append("pre", bib1);
		append("h2", "BibTeX");
		append("pre", bib2);
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
