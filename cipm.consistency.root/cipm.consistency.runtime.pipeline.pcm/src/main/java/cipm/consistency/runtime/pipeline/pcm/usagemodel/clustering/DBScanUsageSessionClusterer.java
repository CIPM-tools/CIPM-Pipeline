package cipm.consistency.runtime.pipeline.pcm.usagemodel.clustering;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

import org.apache.commons.math3.ml.clustering.Cluster;
import org.apache.commons.math3.ml.clustering.Clusterable;
import org.apache.commons.math3.ml.clustering.DBSCANClusterer;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import cipm.consistency.runtime.pipeline.pcm.usagemodel.ServiceCallSession;
import lombok.extern.java.Log;

@Log
public class DBScanUsageSessionClusterer implements IUsageSessionClustering {

	@Override
	public List<List<ServiceCallSession>> clusterSessions(List<ServiceCallSession> initial) {
		log.info("Creating service mapping.");
		// 1. convert to service calls to integers (index)
		ConcurrentMap<String, Integer> serviceCallIndexMapping = Maps.newConcurrentMap();
		AtomicInteger currentId = new AtomicInteger(0);
		initial.parallelStream().forEach(session -> {
			session.getEntryCalls().parallelStream().forEach(e -> {
				synchronized (serviceCallIndexMapping) {
					if (!serviceCallIndexMapping.containsKey(e.getServiceId())) {
						serviceCallIndexMapping.put(e.getServiceId(), currentId.getAndIncrement());
					}
				}
			});
		});

		System.out.println(serviceCallIndexMapping);

		// 2. sessions to points
		log.info("Build n-dimensional points.");
		ConcurrentMap<String, ServiceCallSessionClusterable> pointReduction = Maps.newConcurrentMap();
		List<ServiceCallSessionClusterable> points = Lists.newArrayList();
		initial.stream().forEach(s -> {
			double[] sessionPoint = generatePoints(s, serviceCallIndexMapping);
			StringBuilder contentId = new StringBuilder();
			for (double d : sessionPoint) {
				contentId.append((int) d);
			}
			if (pointReduction.containsKey(contentId.toString())) {
				pointReduction.get(contentId.toString()).addSession(s);
			} else {
				ServiceCallSessionClusterable nClusterable = new ServiceCallSessionClusterable(s, sessionPoint);
				pointReduction.put(contentId.toString(), nClusterable);
				points.add(nClusterable);
			}
		});
		System.out.println(pointReduction);

		// 3. do dbscan clustering
		log.info("Do clustering.");
		DBSCANClusterer<ServiceCallSessionClusterable> clusterer = new DBSCANClusterer<ServiceCallSessionClusterable>(
				serviceCallIndexMapping.size() / 2, 0);
		List<Cluster<ServiceCallSessionClusterable>> clusters = clusterer.cluster(points);

		// 4. convert back to session clusters
		log.info("Convert clusters back to sessions.");
		return clusters.parallelStream().map(c -> c.getPoints().parallelStream().map(p -> p.sessions)
				.flatMap(List::stream).collect(Collectors.toList())).collect(Collectors.toList());
	}

	private double[] generatePoints(ServiceCallSession session, Map<String, Integer> mapping) {
		double[] points = new double[mapping.size()];
		Arrays.fill(points, 0);

		// get values
		session.getEntryCalls().stream().forEach(s -> {
			points[mapping.get(s.getServiceId())]++;
		});

		return points;
	}

	private static class ServiceCallSessionClusterable implements Clusterable {
		private List<ServiceCallSession> sessions;
		private double[] points;

		private ServiceCallSessionClusterable(ServiceCallSession session, double[] points) {
			this.sessions = Lists.newArrayList(session);
			this.points = points;
		}

		private void addSession(ServiceCallSession sess) {
			this.sessions.add(sess);
		}

		@Override
		public double[] getPoint() {
			return this.points;
		}

		@Override
		public String toString() {
			return Arrays.toString(points);
		}
	}

}
