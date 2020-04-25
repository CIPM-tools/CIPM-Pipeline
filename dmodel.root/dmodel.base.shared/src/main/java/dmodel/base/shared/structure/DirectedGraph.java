package dmodel.base.shared.structure;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.commons.lang3.tuple.Pair;

public class DirectedGraph<N, E> {
	private Set<N> nodes;
	private Map<Pair<N, N>, E> edgeProjection;
	private Map<N, List<Pair<N, E>>> outgoingEdges;
	private Map<N, List<Pair<N, E>>> incomingEdges;

	public DirectedGraph() {
		this.nodes = new HashSet<>();
		this.edgeProjection = new HashMap<>();
		this.outgoingEdges = new HashMap<>();
		this.incomingEdges = new HashMap<>();
	}

	public Set<N> getNodes() {
		return nodes;
	}

	public List<Pair<N, E>> getOutgoingEdges(String node) {
		return outgoingEdges.get(node);
	}

	public List<Pair<N, E>> getIncomingEdges(String node) {
		return incomingEdges.get(node);
	}

	public int incomingEdges(N node) {
		return this.incomingEdges.containsKey(node) ? this.incomingEdges.get(node).size() : 0;
	}

	public int outgoingEdges(N node) {
		return this.outgoingEdges.containsKey(node) ? this.outgoingEdges.get(node).size() : 0;
	}

	public void modifyEdge(N from, N to, E value) {
		this.edgeProjection.put(Pair.of(from, to), value);

		if (!outgoingEdges.containsKey(from)) {
			outgoingEdges.put(from, new ArrayList<>());
		}
		outgoingEdges.get(from).add(Pair.of(to, value));

		if (!incomingEdges.containsKey(to)) {
			incomingEdges.put(to, new ArrayList<>());
		}
		incomingEdges.get(to).add(Pair.of(from, value));
	}

	public void addEdge(N from, N to, E data) {
		if (!nodes.contains(from)) {
			this.addNode(from);
		}
		if (!nodes.contains(to)) {
			this.addNode(to);
		}
		this.modifyEdge(from, to, data);
	}

	public void addNode(N node) {
		this.nodes.add(node);
	}

	public boolean hasEdge(N from, N to) {
		return this.edgeProjection.containsKey(Pair.of(from, to));
	}

	public int edges() {
		return this.edgeProjection.size();
	}

	public E getEdge(N n1, N n2) {
		return this.edgeProjection.get(Pair.of(n1, n2));
	}

	public List<Pair<N, N>> getEdges() {
		return this.edgeProjection.entrySet().stream().map(e -> Pair.of(e.getKey().getLeft(), e.getKey().getRight()))
				.collect(Collectors.toList());
	}

}
