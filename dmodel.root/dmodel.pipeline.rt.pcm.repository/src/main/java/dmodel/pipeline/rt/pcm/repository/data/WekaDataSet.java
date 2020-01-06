package dmodel.pipeline.rt.pcm.repository.data;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import dmodel.pipeline.monitoring.util.ServiceParametersWrapper;
import weka.core.Attribute;
import weka.core.DenseInstance;
import weka.core.Instance;
import weka.core.Instances;

/**
 * A parameters model, which defines different possible parametric dependency
 * relations.
 * 
 * @author JP
 *
 */
public class WekaDataSet<T> {

	private final WekaClassAttribute classAttribute;

	private final ArrayList<Attribute> attributes;

	private final List<WekaServiceParameter> parameters;

	private final Map<String, List<WekaServiceParameter>> parametersToAttributes;

	private final WekaDataSetMode mode;

	private final Instances dataSet;

	/**
	 * Initializes a new instance of {@link WekaServiceParametersModel}.
	 * 
	 * @param basedOnParameters These parameters are used to build the weka
	 *                          attribute.
	 * @param classAttribute    The class attribute for the weka data set.
	 */
	public WekaDataSet(final List<ParametersWithClass<T>> values, final WekaDataSetMode mode) {
		this.attributes = new ArrayList<>();
		this.parameters = new ArrayList<>();
		this.parametersToAttributes = new HashMap<>();
		this.mode = mode;

		ServiceParametersWrapper basedOnParameters = values.get(0).getParameters();

		for (Entry<String, Object> parameter : basedOnParameters.getParameters().entrySet()) {
			this.addParameter(parameter.getKey(), values);
		}

		this.classAttribute = getClassAttribute(values);
		this.attributes.add(this.classAttribute.getWekaAttribute());

		dataSet = new Instances("dataset", this.attributes, 0);
		dataSet.setClass(this.classAttribute.getWekaAttribute());

		this.addInstances(values);
	}

	private void addInstances(final List<ParametersWithClass<T>> values) {
		for (ParametersWithClass<T> value : values) {
			Instance instance = this.buildInstance(value);
			this.dataSet.add(instance);
		}
	}

	private static <T> WekaClassAttribute getClassAttribute(List<ParametersWithClass<T>> values) {
		Object basedOnClassValue = values.get(0).getClassValue();

		if (basedOnClassValue instanceof String) {
			return new StringWekaClassAttribute<>(values);
		} else {
			return new NumericWekaClassAttribute<>();
		}
	}

	/**
	 * Gets the weka data set.
	 * 
	 * @return Weka data set.
	 */
	public Instances getDataSet() {
		return this.dataSet;
	}

	/**
	 * Creates a weka data instance from service parameters and the class value.
	 * 
	 * @param serviceParameters The service parameters for the data instance.
	 * @param classValue        The class value for the data instance.
	 * @return A weka data instance.
	 */
	public Instance buildTestInstance(final ServiceParametersWrapper serviceParameters) {
		return this.buildInstance(serviceParameters, 0.0, 1.0);
	}

	private Instance buildInstance(final ParametersWithClass value) {
		double classValue = this.classAttribute.getValue(value.getClassValue());
		return this.buildInstance(value.getParameters(), classValue, value.getWeight());
	}

	private Instance buildInstance(final ServiceParametersWrapper serviceParameters, final double classValue,
			final double weight) {
		double[] values = new double[this.parameters.size() + 1];

		for (Entry<String, Object> parameter : serviceParameters.getParameters().entrySet()) {
			List<WekaServiceParameter> wekaParameters = this.parametersToAttributes.get(parameter.getKey());
			if (wekaParameters != null) {
				for (WekaServiceParameter wekaServiceParameter : wekaParameters) {
					wekaServiceParameter.setValue(parameter.getValue(), values);
				}
			}
		}

		values[this.parameters.size()] = classValue;
		return new DenseInstance(weight, values);
	}

	/**
	 * Gets all weka attributes, including the class attribute.
	 * 
	 * @return all weka attributes.
	 */
	public ArrayList<Attribute> getAttributes() {
		return this.attributes;
	}

	/**
	 * Gets the weka class attribute.
	 * 
	 * @return The weka class attribute.
	 */
	public Attribute getClassAttribute() {
		return this.classAttribute.getWekaAttribute();
	}

	/**
	 * Gets the number of attributes, without the class attribute.
	 * 
	 * @return The number of input attributes.
	 */
	public int getInputAttributesCount() {
		return this.attributes.size() - 1;
	}

	/**
	 * Gets the stochastic expression for a attribute index. For example the string
	 * "a ^ 2" is returned.
	 * 
	 * @param idx The attribute index.
	 * @return The Stochastic Expression for the attribute as string.
	 */
	public String getStochasticExpressionForIndex(final int idx) {
		return this.parameters.get(idx).getStochasticExpression();
	}

	private void addNumericParameter(final String name, final List<ParametersWithClass<T>> values) {
		List<WekaServiceParameter> newParameters = new ArrayList<>();
		int index = this.parameters.size();
		newParameters.add(new NumericWekaServiceParameter(name, index));
		index++;

		if (this.mode != WekaDataSetMode.NoTransformations) {
			newParameters.add(new PowerNumericWekaServiceParameter(name, index, 2.0));
			index++;
			newParameters.add(new PowerNumericWekaServiceParameter(name, index, 3.0));
			index++;

			if (this.mode != WekaDataSetMode.IntegerOnly) {
				newParameters.add(new PowerNumericWekaServiceParameter(name, index, 0.5));
				index++;
			}
		}

		for (WekaServiceParameter wekaServiceParameter : newParameters) {
			this.parameters.add(wekaServiceParameter);
			this.attributes.add(wekaServiceParameter.getWekaAttribute());
		}

		this.parametersToAttributes.put(name, newParameters);
	}

	private void addStringParameter(final String name, final List<ParametersWithClass<T>> values) {
		int index = this.parameters.size();
		WekaServiceParameter newParameter = new StringWekaServiceParameter<T>(name, index, values);

		this.parameters.add(newParameter);
		this.attributes.add(newParameter.getWekaAttribute());
		this.parametersToAttributes.put(name, Collections.singletonList(newParameter));
	}

	private void addBooleanParameter(final String name, final List<ParametersWithClass<T>> values) {
		int index = this.parameters.size();
		WekaServiceParameter newParameter = new BooleanWekaServiceParameter(name, index);

		this.parameters.add(newParameter);
		this.attributes.add(newParameter.getWekaAttribute());
		this.parametersToAttributes.put(name, Collections.singletonList(newParameter));
	}

	private void addParameter(final String name, final List<ParametersWithClass<T>> values) {
		Object value = values.get(0).getParameters().getParameters().get(name);
		if (value instanceof Integer) {
			this.addNumericParameter(name, values);
		} else if (value instanceof Double && this.mode != WekaDataSetMode.IntegerOnly) {
			this.addNumericParameter(name, values);
		} else if (value instanceof Boolean && this.mode != WekaDataSetMode.IntegerOnly
				&& this.mode != WekaDataSetMode.NumericOnly) {
			this.addBooleanParameter(name, values);
		} else if (value instanceof Double && this.mode != WekaDataSetMode.IntegerOnly
				&& this.mode != WekaDataSetMode.NumericOnly) {
			this.addStringParameter(name, values);
		} else {
		}
	}

	private static abstract class WekaClassAttribute {

		private final Attribute wekaAttribute;

		public WekaClassAttribute(final Attribute wekaAttribute) {
			this.wekaAttribute = wekaAttribute;
		}

		public Attribute getWekaAttribute() {
			return this.wekaAttribute;
		}

		public abstract double getValue(Object value);
	}

	private static class NumericWekaClassAttribute<T> extends WekaClassAttribute {

		public NumericWekaClassAttribute() {
			super(new Attribute("class"));
		}

		@Override
		public double getValue(final Object value) {
			double castedValue = 0.0;
			if (value instanceof Integer) {
				castedValue = (Integer) value;
			} else if (value instanceof Double) {
				castedValue = (Double) value;
			} else if (value instanceof Long) {
				castedValue = (Long) value;
			}
			return castedValue;
		}
	}

	private static class StringWekaClassAttribute<T> extends WekaClassAttribute {

		public StringWekaClassAttribute(final List<ParametersWithClass<T>> values) {
			super(new Attribute("class", createPossibleValues(values)));
		}

		private static <T> ArrayList<String> createPossibleValues(final List<ParametersWithClass<T>> values) {
			Set<String> possibleValues = new HashSet<String>();
			for (ParametersWithClass<T> parameters : values) {
				String value = (String) parameters.getClassValue();
				possibleValues.add(value);
			}
			return new ArrayList<String>(possibleValues);
		}

		@Override
		public double getValue(final Object value) {
			return this.getWekaAttribute().indexOfValue((String) value);
		}
	}

	private static class BooleanWekaServiceParameter extends WekaServiceParameter {

		public BooleanWekaServiceParameter(final String parameterName, final int index) {
			super(parameterName, index, new Attribute(parameterName, createPossibleValues()));
		}

		private static ArrayList<String> createPossibleValues() {
			ArrayList<String> possibleValues = new ArrayList<String>();
			possibleValues.add(String.valueOf(true));
			possibleValues.add(String.valueOf(false));
			return possibleValues;
		}

		@Override
		public void setValue(final Object value, final double[] result) {
			String stringValue = String.valueOf((Boolean) value);
			result[this.getIndex()] = this.getWekaAttribute().indexOfValue(stringValue);
		}
	}

	private static class StringWekaServiceParameter<T> extends WekaServiceParameter {

		public StringWekaServiceParameter(final String parameterName, final int index,
				final List<ParametersWithClass<T>> values) {
			super(parameterName, index, new Attribute(parameterName, createPossibleValues(parameterName, values)));
		}

		private static <T> ArrayList<String> createPossibleValues(final String parameterName,
				final List<ParametersWithClass<T>> values) {
			Set<String> possibleValues = new HashSet<String>();
			for (ParametersWithClass<T> parameters : values) {
				String value = (String) parameters.getParameters().getParameters().get(parameterName);
				possibleValues.add(value);
			}
			return new ArrayList<String>(possibleValues);
		}

		@Override
		public void setValue(final Object value, final double[] result) {
			result[this.getIndex()] = this.getWekaAttribute().indexOfValue((String) value);
		}
	}

	private static class NumericWekaServiceParameter extends WekaServiceParameter {

		public NumericWekaServiceParameter(final String parameterName, final int index) {
			super(parameterName, index, new Attribute(parameterName));
		}

		@Override
		public void setValue(final Object value, final double[] result) {
			double castedValue = 0.0;
			if (value instanceof Integer) {
				castedValue = (Integer) value;
			} else if (value instanceof Double) {
				castedValue = (Double) value;
			}

			result[this.getIndex()] = castedValue;
		}
	}

	private static class PowerNumericWekaServiceParameter extends WekaServiceParameter {

		private final double power;

		public PowerNumericWekaServiceParameter(final String parameterName, final int index, final double power) {
			super(parameterName, index, new Attribute(parameterName + "-pow-" + String.valueOf(power)));
			this.power = power;
		}

		@Override
		public String getStochasticExpression() {
			return "(" + this.getParameterName() + " ^ " + String.valueOf(this.power) + ")";
		}

		@Override
		public void setValue(final Object value, final double[] result) {
			double castedValue = 0.0;
			if (value instanceof Integer) {
				castedValue = (Integer) value;
			} else if (value instanceof Double) {
				castedValue = (Double) value;
			}

			result[this.getIndex()] = Math.pow(castedValue, this.power);
		}

	}

	private static abstract class WekaServiceParameter {
		private final int index;
		private final String parameterName;
		private final Attribute wekaAttribute;

		public WekaServiceParameter(final String parameterName, final int index, final Attribute wekaAttribute) {
			this.index = index;
			this.parameterName = parameterName;
			this.wekaAttribute = wekaAttribute;
		}

		public int getIndex() {
			return this.index;
		}

		public String getParameterName() {
			return this.parameterName;
		}

		public String getStochasticExpression() {
			return this.getParameterName();
		}

		public Attribute getWekaAttribute() {
			return this.wekaAttribute;
		}

		public abstract void setValue(Object value, double[] result);
	}
}
