package cipm.consistency.runtime.pipeline.pcm.repository.noise;

import java.util.Arrays;

import org.apache.commons.math3.stat.descriptive.rank.Max;
import org.apache.commons.math3.stat.descriptive.rank.Min;
import org.palladiosimulator.pcm.core.CoreFactory;
import org.palladiosimulator.pcm.core.PCMRandomVariable;

import cipm.consistency.runtime.pipeline.pcm.repository.IRegressionModelNoiseGenerator;
import cipm.consistency.runtime.pipeline.pcm.repository.IRegressionOutlierDetection;
import cipm.consistency.runtime.pipeline.pcm.repository.outlier.NumericOutlierDetection;

public class DeviationDistributionNoise implements IRegressionModelNoiseGenerator {
	private Min min = new Min();
	private Max max = new Max();
	private static final int PRECISION = 6;

	private IRegressionOutlierDetection outlierDetection;

	public DeviationDistributionNoise(float outlierThres) {
		this.outlierDetection = new NumericOutlierDetection(outlierThres);
	}

	@Override
	public PCMRandomVariable generateNoise(double[] deviations, int maxSize) {
		deviations = this.outlierDetection.filterOutliers(deviations);

		double lowest = min.evaluate(deviations);
		double highest = max.evaluate(deviations);

		double step = (highest - lowest) / (double) maxSize;
		int[] counts = new int[maxSize];
		Arrays.fill(counts, 0);

		for (double dev : deviations) {
			int index = (int) Math.floor((dev - lowest) / step);
			if (index == counts.length)
				index--;
			counts[index]++;
		}

		PCMRandomVariable pcmVar = CoreFactory.eINSTANCE.createPCMRandomVariable();
		StringBuilder builder = new StringBuilder();
		builder.append("DoublePMF["); // max necessary -> otherwise maybe zero values
		for (int index = 0; index < counts.length; index++) {
			int count = counts[index];
			if (count > 0) {
				double probability = (double) count / (double) deviations.length;
				double value = index * step + lowest + step / 2;

				builder.append("(");
				builder.append(String.valueOf(roundDouble(value, PRECISION)));
				builder.append(";");
				builder.append(String.valueOf(roundDouble(probability, PRECISION)));
				builder.append(")");
			}
		}
		builder.append("]");
		pcmVar.setSpecification(builder.toString());

		return pcmVar;
	}

	private double roundDouble(double val, int chars) {
		double factor = Math.pow(10, chars);
		return Math.round(val * factor) / factor;
	}

}
