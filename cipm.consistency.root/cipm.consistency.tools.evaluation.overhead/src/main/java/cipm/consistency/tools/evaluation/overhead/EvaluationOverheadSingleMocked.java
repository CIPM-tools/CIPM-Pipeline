package cipm.consistency.tools.evaluation.overhead;

import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.math3.stat.descriptive.rank.Median;
import org.apache.commons.math3.stat.descriptive.rank.Percentile;

import com.google.common.collect.Lists;

import cipm.consistency.bridge.monitoring.controller.ServiceParameters;
import cipm.consistency.bridge.monitoring.controller.ThreadMonitoringController;
import me.tongfei.progressbar.ProgressBar;

public class EvaluationOverheadSingleMocked {

	public static void main(String[] args) {
		new EvaluationOverheadSingleMocked().execute();
	}

	public void execute() {
		// start server
		final PlainReceiveServer server = new PlainReceiveServer();
		ExecutorService execService = Executors.newSingleThreadExecutor();
		execService.submit(new Runnable() {
			@Override
			public void run() {
				server.start();
			}
		});

		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

		executeOverheadMeasurements(true);

		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

		executeOverheadMeasurements(false);

		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

		// server.stop();
		execService.shutdown();

		System.exit(0);
	}

	private void executeOverheadMeasurements(boolean fineGrained) {
		List<Double> executionTimes = Lists.newArrayList();
		int execs = 5000;

		ThreadMonitoringController.shutdown();
		ThreadMonitoringController.mockingMode();

		for (@SuppressWarnings("unused")
		Integer z : ProgressBar.wrap(IntStream.range(0, execs).boxed().collect(Collectors.toList()),
				"Measure overhead.")) {
			int orderItems = 25;
			// generate IDs for actions
			String[] ids = IntStream.range(0, 100).mapToObj(i -> randomStr()).toArray(String[]::new);
			String[] non_ids = IntStream.range(0, 100).mapToObj(i -> randomStr()).toArray(String[]::new);
			String[] util = IntStream.range(0, 100).mapToObj(i -> randomStr()).toArray(String[]::new);

			Set<String> idset = Stream.of(ids).collect(Collectors.toSet());

			// start it
			ThreadMonitoringController.mockingIds(idset);

			long startNano = System.nanoTime();
			ThreadMonitoringController.setSessionId(UUID.randomUUID().toString());
			ServiceParameters parameters = new ServiceParameters();
			// outer service
			ThreadMonitoringController.getInstance().enterService(ids[0], this, parameters);
			ThreadMonitoringController.getInstance().setExternalCallId(util[0]);

			// internal 1
			ThreadMonitoringController.getInstance().enterInternalAction(fineGrained ? ids[1] : non_ids[1], util[3]);
			ThreadMonitoringController.getInstance().exitInternalAction(fineGrained ? ids[1] : non_ids[1], util[3]);

			// inner service 1
			ThreadMonitoringController.getInstance().enterService(ids[2], this, parameters);
			ThreadMonitoringController.getInstance().exitService(ids[2]);

			// internal preproc
			ThreadMonitoringController.getInstance().enterInternalAction(fineGrained ? ids[3] : non_ids[3], util[3]);
			ThreadMonitoringController.getInstance().exitInternalAction(fineGrained ? ids[3] : non_ids[3], util[3]);

			// ex persist order
			ThreadMonitoringController.getInstance().setExternalCallId(util[4]);

			ThreadMonitoringController.getInstance().enterService(ids[4], this);
			ThreadMonitoringController.getInstance().enterInternalAction(fineGrained ? ids[5] : non_ids[5], util[3]);
			ThreadMonitoringController.getInstance().exitInternalAction(fineGrained ? ids[5] : non_ids[5], util[3]);
			ThreadMonitoringController.getInstance().enterInternalAction(fineGrained ? ids[6] : non_ids[6], util[3]);
			ThreadMonitoringController.getInstance().exitInternalAction(fineGrained ? ids[6] : non_ids[6], util[3]);
			ThreadMonitoringController.getInstance().exitService(ids[4]);

			// ex persist order item
			for (int i = 0; i < orderItems; i++) {
				ThreadMonitoringController.getInstance().setExternalCallId(util[7]);
				ThreadMonitoringController.getInstance().enterService(ids[10], this);
				ThreadMonitoringController.getInstance().enterInternalAction(fineGrained ? ids[11] : non_ids[11],
						util[3]);
				ThreadMonitoringController.getInstance().exitInternalAction(fineGrained ? ids[11] : non_ids[11],
						util[3]);

				ThreadMonitoringController.getInstance().enterInternalAction(fineGrained ? ids[16] : non_ids[16],
						util[3]);
				ThreadMonitoringController.getInstance().exitInternalAction(fineGrained ? ids[16] : non_ids[16],
						util[3]);
				ThreadMonitoringController.getInstance().exitService(ids[10]);
			}

			ThreadMonitoringController.getInstance().exitLoop(fineGrained ? ids[16] : non_ids[16], orderItems);

			// recommend
			ThreadMonitoringController.getInstance().enterService(ids[12], this);
			ThreadMonitoringController.getInstance().enterInternalAction(fineGrained ? ids[14] : non_ids[14], util[3]);
			ThreadMonitoringController.getInstance().exitInternalAction(fineGrained ? ids[14] : non_ids[14], util[3]);
			ThreadMonitoringController.getInstance().enterService(ids[13], this);
			ThreadMonitoringController.getInstance().enterInternalAction(fineGrained ? ids[13] : non_ids[13], util[3]);
			ThreadMonitoringController.getInstance().exitInternalAction(fineGrained ? ids[13] : non_ids[13], util[3]);
			ThreadMonitoringController.getInstance().exitService(ids[13]);
			ThreadMonitoringController.getInstance().enterInternalAction(fineGrained ? ids[15] : non_ids[15], util[3]);
			ThreadMonitoringController.getInstance().exitInternalAction(fineGrained ? ids[15] : non_ids[15], util[3]);
			ThreadMonitoringController.getInstance().exitService(ids[12]);

			ThreadMonitoringController.getInstance().exitService(ids[0]);
			executionTimes.add((System.nanoTime() - startNano) / 1000000d);
		}

		double[] valArray = executionTimes.stream().mapToDouble(l -> l).toArray();
		double perc5 = new Percentile(5d).evaluate(valArray);// cancel hard outliers
		double perc95 = new Percentile(95d).evaluate(valArray);// cancel hard outliers
		double[] valArrayFiltered = executionTimes.stream().mapToDouble(l -> l).filter(d -> d > perc5 && d < perc95)
				.toArray();

		double median = new Median().evaluate(valArrayFiltered);
		double stdev = calculateStandardDeviation(valArrayFiltered);

		System.out.println("Fine grained monitoring: " + fineGrained);
		System.out.println("Average overhead in ms: " + (median) + "ms");
		System.out.println("Standard deviation in ms: " + (stdev) + "ms");
	}

	public String randomStr() {
		return RandomStringUtils.randomAlphanumeric(20).toUpperCase();
	}

	private static double calculateStandardDeviation(double[] array) {

		// finding the sum of array values
		double sum = 0.0;

		for (int i = 0; i < array.length; i++) {
			sum += array[i];
		}

		// getting the mean of array.
		double mean = sum / array.length;

		// calculating the standard deviation
		double standardDeviation = 0.0;
		for (int i = 0; i < array.length; i++) {
			standardDeviation += Math.pow(array[i] - mean, 2);

		}

		return Math.sqrt(standardDeviation / array.length);
	}

}
