package cipm.consistency.runtime.pipeline.pcm.repository.noise;

import org.apache.commons.math3.distribution.NormalDistribution;
import org.apache.commons.math3.stat.descriptive.moment.Mean;
import org.apache.commons.math3.stat.descriptive.moment.StandardDeviation;
import org.palladiosimulator.pcm.core.CoreFactory;
import org.palladiosimulator.pcm.core.PCMRandomVariable;

import cipm.consistency.runtime.pipeline.pcm.repository.IRegressionModelNoiseGenerator;
import cipm.consistency.runtime.pipeline.pcm.repository.IRegressionOutlierDetection;
import cipm.consistency.runtime.pipeline.pcm.repository.outlier.NumericOutlierDetection;

public class NormalDistributionNoise implements IRegressionModelNoiseGenerator {
	private static final StandardDeviation STDEV = new StandardDeviation();
	private static final Mean MEAN = new Mean();
	private static final int PRECISION = 6;

	private IRegressionOutlierDetection outlierDetection;

	public NormalDistributionNoise(float outlierThres) {
		this.outlierDetection = new NumericOutlierDetection(outlierThres);
	}

	@Override
	public PCMRandomVariable generateNoise(double[] deviations, int maxSize) {
		// calculate corresponding normal distribution
		double stdev = STDEV.evaluate(deviations);
		double avg = MEAN.evaluate(deviations);
		NormalDistribution normDistr = new NormalDistribution(avg, stdev > 0 ? stdev : 0.000000001d);
		deviations = this.outlierDetection.filterOutliers(deviations);

		// create pcm distribution
		double lowerThres = avg - stdev * 2;
		double upperThres = avg + stdev * 2;
		double intervalSize = (upperThres - lowerThres) / (double) maxSize;
		double[][] sampleArray = new double[maxSize][2];
		double probabilitySum = 0;
		for (int i = 0; i < maxSize; i++) {
			double currentPointerValue = lowerThres + intervalSize * (0.5d + (double) i);
			double currentProbabilityValue = roundDouble(
					normDistr.probability(lowerThres + intervalSize * i, lowerThres + intervalSize * i + intervalSize),
					PRECISION);
			sampleArray[i][0] = currentPointerValue;
			sampleArray[i][1] = currentProbabilityValue;
			probabilitySum += currentProbabilityValue;
		}
		// scale factor (probs need to sum up to 1)
		for (int i = 0; i < maxSize; i++) {
			sampleArray[i][1] = sampleArray[i][1] * (1d / probabilitySum);
		}

		// build random variable
		PCMRandomVariable pcmVar = CoreFactory.eINSTANCE.createPCMRandomVariable();
		StringBuilder builder = new StringBuilder();
		builder.append("DoublePMF["); // max necessary -> otherwise maybe zero values
		for (double[] sample : sampleArray) {
			builder.append("(");
			builder.append(String.valueOf(roundDouble(sample[0], PRECISION)));
			builder.append(";");
			builder.append(String.valueOf(roundDouble(sample[1], PRECISION)));
			builder.append(")");
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
