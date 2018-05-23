package gui_orientation.components;

import javax.swing.*;

/**
 * This class extends the generic JSpinner of Java for a specific JSpinner for
 * integer. It handles int type.
 * 
 * @author Daniel Sage, Biomedical Imaging Group, EPFL, Lausanne, Switzerland.
 * 
 */
public class SpinnerInteger extends JSpinner {

	private SpinnerNumberModel	model;

	private int					defValue;
	private int					minValue;
	private int					maxValue;
	private int					incValue;

	/**
	 * Constructor.
	 */
	public SpinnerInteger(int defValue, int minValue, int maxValue, int incValue) {
		super();
		this.defValue = defValue;
		this.minValue = minValue;
		this.maxValue = maxValue;
		this.incValue = incValue;

		Integer def = new Integer(defValue);
		Integer min = new Integer(minValue);
		Integer max = new Integer(maxValue);
		Integer inc = new Integer(incValue);
		model = new SpinnerNumberModel(def, min, max, inc);
		setModel(model);
	}

	/**
	 * Set the minimal and the maximal limit.
	 */
	public void setLimit(int minValue, int maxValue) {
		this.minValue = minValue;
		this.maxValue = maxValue;
		int value = get();
		Integer min = new Integer(minValue);
		Integer max = new Integer(maxValue);
		Integer inc = new Integer(incValue);
		defValue = (value > maxValue ? maxValue : (value < minValue ? minValue : value));
		Integer def = new Integer(defValue);
		model = new SpinnerNumberModel(def, min, max, inc);
		setModel(model);
	}

	/**
	 * Set the incremental step.
	 */
	public void setIncrement(int incValue) {
		this.incValue = incValue;
		Integer def = (Integer) getModel().getValue();
		Integer min = new Integer(minValue);
		Integer max = new Integer(maxValue);
		Integer inc = new Integer(incValue);
		model = new SpinnerNumberModel(def, min, max, inc);
		setModel(model);
	}

	/**
	 * Returns the incremental step.
	 */
	public int getIncrement() {
		return incValue;
	}

	/**
	 * Set the value in the JSpinner with clipping in the range [min..max].
	 */
	public void set(int value) {
		value = (value > maxValue ? maxValue : (value < minValue ? minValue : value));
		model.setValue(value);
	}

	/**
	 * Return the value without clipping the value in the range [min..max].
	 */
	public int get() {
		if (model.getValue() instanceof Integer) {
			Integer i = (Integer) model.getValue();
			int ii = i.intValue();
			return (ii > maxValue ? maxValue : (ii < minValue ? minValue : ii));
		}
		else if (model.getValue() instanceof Double) {
			Double i = (Double) model.getValue();
			int ii = (int) i.doubleValue();
			return (ii > maxValue ? maxValue : (ii < minValue ? minValue : ii));
		}
		else if (model.getValue() instanceof Float) {
			Float i = (Float) model.getValue();
			int ii = (int) i.floatValue();
			return (ii > maxValue ? maxValue : (ii < minValue ? minValue : ii));
		}
		return 0;
	}
}
