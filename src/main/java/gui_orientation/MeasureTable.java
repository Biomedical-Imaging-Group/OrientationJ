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

import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.util.Vector;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.DefaultTableModel;

import gui_orientation.components.GridPanel;

public class MeasureTable extends JPanel implements TableModelListener {

	private JTable			table;
	private MyModel			model;
	private Vector<Measure>	measures;
	private MeasureCanvas	canvas;
	private String[]			headings;

	public MeasureTable() {

		int col = 10;
		headings = new String[col];

		int i = 0;
		headings[i++] = "No.";
		headings[i++] = "Xc";
		headings[i++] = "Yc";
		headings[i++] = "Zc";
		headings[i++] = "Zone";
		headings[i++] = "Ellipse";
		headings[i++] = "Prefilter";
		headings[i++] = "Energy";
		headings[i++] = "Orientation";
		headings[i++] = "Coherency";

		this.measures = new Vector<Measure>();
		model = new MyModel();
		table = new JTable();
		table.setModel(model);
		for (i = 0; i < headings.length; i++) {
			model.addColumn(headings[i]);
		}
		table.getColumnModel().getColumn(0).setPreferredWidth(40);
		table.getColumnModel().getColumn(1).setPreferredWidth(40);
		table.getColumnModel().getColumn(2).setPreferredWidth(40);
		table.getColumnModel().getColumn(3).setPreferredWidth(40);
		table.getColumnModel().getColumn(4).setPreferredWidth(40);
		table.getColumnModel().getColumn(5).setPreferredWidth(40);
		JScrollPane scrollPane = new JScrollPane(table);

		GridPanel pn = new GridPanel(false, 0);
		pn.place(0, 0, scrollPane);

		add(pn);

		model.addTableModelListener(this);
	}

	public Vector<Measure> getMeasures() {
		return measures;
	}

	/**
	*/
	public void setCanvas(MeasureCanvas canvas) {
		this.canvas = canvas;
	}

	/**
	*/
	public void copy() {
		Clipboard clip = getToolkit().getSystemClipboard();
		if (clip == null)
			return;
		int selection[] = table.getSelectedRows();
		if (selection.length == 0) {
			table.selectAll();
			selection = table.getSelectedRows();
		}
		String s = "";

		for (int k = 0; k < headings.length; k++) {
			s += headings[k] + "\t";
		}
		s += "\n";

		for (int i = 0; i < selection.length; i++) {
			int n = model.getColumnCount();
			for (int k = 0; k < n; k++) {
				s += model.getValueAt(i, k) + "\t";
			}
			s += "\n";
		}
		StringSelection cont = new StringSelection(s);
		clip.setContents(cont, cont);
		if (canvas != null)
			canvas.repaint();
	}

	@Override
	public void tableChanged(TableModelEvent e) {
		if (model == null)
			return;
		int col = e.getColumn();
		int row = e.getFirstRow();
		if (row < 0)
			return;
		if (row > table.getRowCount())
			return;
		if (col < 0)
			return;
		if (col > table.getColumnCount())
			return;

		if (col == 4 || col == 5) {
			if (canvas != null)
				canvas.repaint();
		}
		model.fireTableDataChanged();
	}

	/**
	*/
	public boolean isRectangle(int row) {
		if (row < 0)
			return false;
		if (row >= model.getRowCount())
			return false;
		return ((Boolean) model.getValueAt(row, 4)).booleanValue();
	}

	/**
	*/
	public boolean isEllipse(int row) {
		if (row < 0)
			return false;
		if (row >= model.getRowCount())
			return false;
		return ((Boolean) model.getValueAt(row, 5)).booleanValue();
	}

	/**
	*/
	public void remove() {
		int selection[] = table.getSelectedRows();
		int n = model.getRowCount();
		for (int i = 0; i < n; i++)
			model.removeRow(0);

		Vector<Measure> tmp = new Vector<Measure>();
		for (int i = 0; i < measures.size(); i++) {
			boolean keep = true;
			for (int k = 0; k < selection.length; k++) {
				if (selection[k] == i) {
					keep = false;
					break;
				}
			}
			if (keep)
				tmp.add((Measure) measures.get(i));
		}

		measures.removeAllElements();
		for (int i = 0; i < tmp.size(); i++) {
			Measure measure = (Measure) tmp.get(i);
			model.addRow(measure.makeTableLine());
			measures.add(measure);
		}
		if (canvas != null) {
			canvas.repaint();
		}
	}

	public void add(Measure measure) {
		model.addRow(measure.makeTableLine());
		if (canvas != null)
			canvas.repaint();
		model.fireTableDataChanged();
	}

	/**
	*/
	class MyModel extends DefaultTableModel {
		@Override
		public Class getColumnClass(int c) {
			return getValueAt(0, c).getClass();
		}

		@Override
		public boolean isCellEditable(int row, int col) {
			return (col == 4 || col == 5);
		}
	}
}
