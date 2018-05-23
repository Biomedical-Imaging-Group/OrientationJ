package gui_orientation.components;

import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;

/**
 * This class extends the generic JSpinner of Java for a specific JSpinner for
 * float. It handles float type.
 * 
 * @author Daniel Sage, Biomedical Imaging Group, EPFL, Lausanne, Switzerland.
 * 
 */
public class SpinnerFloat extends JSpinner {

	private SpinnerNumberModel	model;

	private float				defValue;
	private float				minValue;
	private float				maxValue;
	private float				incValue;

	/**
	 * Constructor.
	 */
	public SpinnerFloat(float defValue, float minValue, float maxValue, float incValue) {
		super();
		this.defValue = defValue;
		this.minValue = minValue;
		this.maxValue = maxValue;
		this.incValue = incValue;

		Float def = new Float(defValue);
		Float min = new Float(minValue);
		Float max = new Float(maxValue);
		Float inc = new Float(incValue);
		model = new SpinnerNumberModel(def, min, max, inc);
		setModel(model);
	}

	/**
	 * Set the minimal and the maximal limit.
	 */
	public void setLimit(float minValue, float maxValue) {
		this.minValue = minValue;
		this.maxValue = maxValue;
		float value = get();
		Float min = new Float(minValue);
		Float max = new Float(maxValue);
		Float inc = new Float(incValue);
		defValue = (value > maxValue ? maxValue : (value < minValue ? minValue : value));
		Float def = new Float(defValue);
		model = new SpinnerNumberModel(def, min, max, inc);
		setModel(model);
	}

	/**
	 * Set the incremental step.
	 */
	public void setIncrement(float incValue) {
		this.incValue = incValue;
		Float def = (Float) getModel().getValue();
		Float min = new Float(minValue);
		Float max = new Float(maxValue);
		Float inc = new Float(incValue);
		model = new SpinnerNumberModel(def, min, max, inc);
		setModel(model);
	}

	/**
	 * Returns the incremental step.
	 */
	public float getIncrement() {
		return incValue;
	}

	/**
	 * Set the value in the JSpinner with clipping in the range [min..max].
	 */
	public void set(float value) {
		value = (value > maxValue ? maxValue : (value < minValue ? minValue : value));
		model.setValue(value);
	}

	/**
	 * Return the value without clipping the value in the range [min..max].
	 */
	public float get() {
		if (model.getValue() instanceof Integer) {
			Integer i = (Integer) model.getValue();
			float ii = (float) i.intValue();
			return (ii > maxValue ? maxValue : (ii < minValue ? minValue : ii));
		}
		else if (model.getValue() instanceof Double) {
			Double i = (Double) model.getValue();
			float ii = (float) i.doubleValue();
			return (ii > maxValue ? maxValue : (ii < minValue ? minValue : ii));
		}
		else if (model.getValue() instanceof Float) {
			Float i = (Float) model.getValue();
			float ii = i.floatValue();
			return (ii > maxValue ? maxValue : (ii < minValue ? minValue : ii));
		}
		return 0f;
	}

}
