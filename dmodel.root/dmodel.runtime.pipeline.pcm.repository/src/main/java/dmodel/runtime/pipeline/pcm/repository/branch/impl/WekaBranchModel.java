package dmodel.runtime.pipeline.pcm.repository.branch.impl;

import java.util.Optional;
import java.util.Random;

import dmodel.designtime.monitoring.records.ServiceCallRecord;
import dmodel.designtime.monitoring.util.ServiceParametersWrapper;
import dmodel.runtime.pipeline.pcm.repository.data.WekaDataSet;
import weka.classifiers.trees.J48;
import weka.classifiers.trees.j48.C45Split;
import weka.classifiers.trees.j48.ClassifierSplitModel;
import weka.classifiers.trees.j48.ClassifierTree;
import weka.classifiers.trees.j48.NoSplit;
import weka.core.Instance;
import weka.core.Instances;

public class WekaBranchModel implements BranchModel {
	private final WekaBranchModel.StochasticExpressionJ48 classifier;
	private final Random random;
	private final String branchNotExecutedId;
	private final String[] attributeExpressions;

	private final WekaDataSet<String> dataset;

	public WekaBranchModel(final WekaDataSet<String> dataset, final Random random, final String branchNotExecutedId)
			throws Exception {
		this.dataset = dataset;
		WekaBranchModel.StochasticExpressionJ48 tree = new StochasticExpressionJ48();
		tree.buildClassifier(dataset.getDataSet());
		this.classifier = tree;
		this.random = random;
		this.branchNotExecutedId = branchNotExecutedId;
		this.attributeExpressions = new String[dataset.getInputAttributesCount()];
		for (int i = 0; i < this.attributeExpressions.length; i++) {
			this.attributeExpressions[i] = dataset.getStochasticExpressionForIndex(i);
		}
	}

	public WekaBranchModel.StochasticExpressionJ48 getClassifier() {
		return classifier;
	}

	public Instances getDataSet() {
		return dataset.getDataSet();
	}

	@Override
	public Optional<String> predictBranchId(final ServiceCallRecord serviceCall) {
		Instance parametersInstance = this.dataset
				.buildTestInstance(ServiceParametersWrapper.buildFromJson(serviceCall.getParameters()));

		// To mitigate weka.core.UnassignedDatasetException: Instance doesn't have
		// access to a dataset!
		Instances dataset = this.dataset.getDataSet();
		parametersInstance.setDataset(dataset);

		double selectedBranch;
		try {
			selectedBranch = this.classifier.classifyInstance(parametersInstance);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}

		String result = this.dataset.getClassAttribute().value((int) selectedBranch);

		if (result.equals(this.branchNotExecutedId)) {
			return Optional.empty();
		} else {
			return Optional.of(result);
		}
	}

	@Override
	public String getBranchStochasticExpression(final String transitionId) {
		return this.classifier.getBranchStochasticExpression(0, this.attributeExpressions);
	}

	public static class StochasticExpressionJ48 extends J48 {

		/** for serialization */
		private static final long serialVersionUID = -8479361310735737366L;

		public String getBranchStochasticExpression(final int classId, final String[] attributeExpression) {
			StringBuilder result = new StringBuilder();
			this.buildStochasticExpression(this.m_root, classId, attributeExpression, result);
			return result.toString();
		}

		private void buildStochasticExpression(final ClassifierTree tree, final int classId,
				final String[] attributeExpression, final StringBuilder result2) {

			// "BoolPMF[(true;p)(false;q)]"

			if (tree.isLeaf()) {
				int maxClass = tree.getLocalModel().distribution().maxClass(0);
				if (maxClass == classId) {
					result2.append("true");
				} else {
					result2.append("false");
				}
			} else {
				String opposite = null;
				for (int i = 0; i < tree.getSons().length; i++) {
					result2.append("(");
					result2.append(this.toSourceExpression(tree.getLocalModel(), i, attributeExpression,
							tree.getTrainingData())).append(" ? ");

					if (tree.getSons()[i].isLeaf()) {
						int maxClass = tree.getLocalModel().distribution().maxClass(i);
						if (maxClass == classId) {
							result2.append("true");
							opposite = "false";
						} else {
							result2.append("false");
							opposite = "true";
						}
					} else {
						result2.append("( ");
						this.buildStochasticExpression(tree.getSons()[i], classId, attributeExpression, result2);
						result2.append(" )");
					}
					result2.append(" : ");
				}
				result2.append(opposite).append(" ");
				for (int i = 0; i < tree.getSons().length; i++) {
					result2.append(")");
				}
			}
		}

		private String toSourceExpression(final C45Split splitModel, final int index,
				final String[] attributeExpression, final Instances data) {
			StringBuffer expr = new StringBuffer();
			expr.append(attributeExpression[splitModel.attIndex()]);

			if (data.attribute(splitModel.attIndex()).isNominal()) {
				expr.append(" == \"").append(data.attribute(splitModel.attIndex()).value(index)).append("\"");
			} else {
				if (index == 0) {
					expr.append(" <= ").append(splitModel.splitPoint());
				} else {
					expr.append(" > ").append(splitModel.splitPoint());
				}
			}
			return expr.toString();
		}

		private String toSourceExpression(final ClassifierSplitModel splitModel, final int index,
				final String[] attributeExpression, final Instances data) {
			if (splitModel instanceof NoSplit) {
				return "true";
			} else if (splitModel instanceof C45Split) {
				return this.toSourceExpression((C45Split) splitModel, index, attributeExpression, data);
			}
			throw new UnsupportedOperationException();
		}
	}
}