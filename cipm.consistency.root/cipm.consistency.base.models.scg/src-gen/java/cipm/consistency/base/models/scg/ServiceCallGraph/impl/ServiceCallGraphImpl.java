/**
 */
package cipm.consistency.base.models.scg.ServiceCallGraph.impl;

import java.util.Collection;
import java.util.Map;

import org.apache.commons.lang3.tuple.Pair;
import org.eclipse.emf.common.notify.NotificationChain;
import org.eclipse.emf.common.util.BasicEList;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.common.util.EMap;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.InternalEObject;
import org.eclipse.emf.ecore.impl.MinimalEObjectImpl;
import org.eclipse.emf.ecore.util.EObjectContainmentEList;
import org.eclipse.emf.ecore.util.EcoreEMap;
import org.eclipse.emf.ecore.util.InternalEList;
import org.palladiosimulator.pcm.resourceenvironment.ResourceContainer;
import org.palladiosimulator.pcm.seff.ExternalCallAction;
import org.palladiosimulator.pcm.seff.ResourceDemandingSEFF;

import com.google.common.collect.Maps;

import cipm.consistency.base.models.scg.ServiceCallGraph.ServiceCallGraph;
import cipm.consistency.base.models.scg.ServiceCallGraph.ServiceCallGraphEdge;
import cipm.consistency.base.models.scg.ServiceCallGraph.ServiceCallGraphFactory;
import cipm.consistency.base.models.scg.ServiceCallGraph.ServiceCallGraphNode;
import cipm.consistency.base.models.scg.ServiceCallGraph.ServiceCallGraphPackage;
import edu.kit.ipd.sdq.commons.util.java.Quintuple;

/**
 * <!-- begin-user-doc --> An implementation of the model object '<em><b>Service
 * Call Graph</b></em>'. <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * </p>
 * <ul>
 * <li>{@link cipm.consistency.base.models.scg.ServiceCallGraph.impl.ServiceCallGraphImpl#getNodes
 * <em>Nodes</em>}</li>
 * <li>{@link cipm.consistency.base.models.scg.ServiceCallGraph.impl.ServiceCallGraphImpl#getEdges
 * <em>Edges</em>}</li>
 * <li>{@link cipm.consistency.base.models.scg.ServiceCallGraph.impl.ServiceCallGraphImpl#getOutgoingEdges
 * <em>Outgoing Edges</em>}</li>
 * <li>{@link cipm.consistency.base.models.scg.ServiceCallGraph.impl.ServiceCallGraphImpl#getIncomingEdges
 * <em>Incoming Edges</em>}</li>
 * </ul>
 *
 * @generated
 */
public class ServiceCallGraphImpl extends MinimalEObjectImpl.Container implements ServiceCallGraph {
	/**
	 * The cached value of the '{@link #getNodes() <em>Nodes</em>}' containment
	 * reference list. <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @see #getNodes()
	 * @generated
	 * @ordered
	 */
	protected EList<ServiceCallGraphNode> nodes;

	/**
	 * The cached value of the '{@link #getEdges() <em>Edges</em>}' containment
	 * reference list. <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @see #getEdges()
	 * @generated
	 * @ordered
	 */
	protected EList<ServiceCallGraphEdge> edges;

	/**
	 * The cached value of the '{@link #getOutgoingEdges() <em>Outgoing Edges</em>}'
	 * map. <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @see #getOutgoingEdges()
	 * @generated
	 * @ordered
	 */
	protected EMap<Object, EList<ServiceCallGraphEdge>> outgoingEdges;

	/**
	 * The cached value of the '{@link #getIncomingEdges() <em>Incoming Edges</em>}'
	 * map. <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @see #getIncomingEdges()
	 * @generated
	 * @ordered
	 */
	protected EMap<Object, EList<ServiceCallGraphEdge>> incomingEdges;

	protected Map<Pair<String, String>, ServiceCallGraphNode> nodeMapping;
	protected Map<Quintuple<String, String, String, String, String>, ServiceCallGraphEdge> edgeMapping;

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	protected ServiceCallGraphImpl() {
		super();

		this.nodeMapping = Maps.newHashMap();
		this.edgeMapping = Maps.newHashMap();
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	@Override
	protected EClass eStaticClass() {
		return ServiceCallGraphPackage.Literals.SERVICE_CALL_GRAPH;
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	@Override
	public EList<ServiceCallGraphNode> getNodes() {
		if (nodes == null) {
			nodes = new EObjectContainmentEList<ServiceCallGraphNode>(ServiceCallGraphNode.class, this,
					ServiceCallGraphPackage.SERVICE_CALL_GRAPH__NODES);
		}
		return nodes;
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	@Override
	public EList<ServiceCallGraphEdge> getEdges() {
		if (edges == null) {
			edges = new EObjectContainmentEList<ServiceCallGraphEdge>(ServiceCallGraphEdge.class, this,
					ServiceCallGraphPackage.SERVICE_CALL_GRAPH__EDGES);
		}
		return edges;
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	@Override
	public EMap<Object, EList<ServiceCallGraphEdge>> getOutgoingEdges() {
		if (outgoingEdges == null) {
			outgoingEdges = new EcoreEMap<Object, EList<ServiceCallGraphEdge>>(
					ServiceCallGraphPackage.Literals.EDGE_LIST, EdgeListImpl.class, this,
					ServiceCallGraphPackage.SERVICE_CALL_GRAPH__OUTGOING_EDGES);
		}
		return outgoingEdges;
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	@Override
	public EMap<Object, EList<ServiceCallGraphEdge>> getIncomingEdges() {
		if (incomingEdges == null) {
			incomingEdges = new EcoreEMap<Object, EList<ServiceCallGraphEdge>>(
					ServiceCallGraphPackage.Literals.EDGE_LIST, EdgeListImpl.class, this,
					ServiceCallGraphPackage.SERVICE_CALL_GRAPH__INCOMING_EDGES);
		}
		return incomingEdges;
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	@Override
	public ServiceCallGraphNode addNode(final ResourceDemandingSEFF seff, final ResourceContainer host) {
		ServiceCallGraphNode node = ServiceCallGraphFactory.eINSTANCE.createServiceCallGraphNode();
		node.setHost(host);
		node.setSeff(seff);
		getNodes().add(node);
		getIncomingEdges().put(node, new BasicEList<ServiceCallGraphEdge>());
		getOutgoingEdges().put(node, new BasicEList<ServiceCallGraphEdge>());
		nodeMapping.put(Pair.of(seff.getId(), host == null ? null : host.getId()), node);

		return node;
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	@Override
	public void addEdge(final ResourceDemandingSEFF from, final ResourceDemandingSEFF to,
			final ResourceContainer fromContainer, final ResourceContainer toContainer,
			final ExternalCallAction externalCall, final int value) {
		// nodes
		ServiceCallGraphNode fromNode = hasNode(from, fromContainer);
		if (fromNode == null) {
			fromNode = this.addNode(from, fromContainer);
		}
		ServiceCallGraphNode toNode = hasNode(to, toContainer);
		if (toNode == null) {
			toNode = this.addNode(to, toContainer);
		}
		// edge
		ServiceCallGraphEdge edge = ServiceCallGraphFactory.eINSTANCE.createServiceCallGraphEdge();
		edge.setFrom(fromNode);
		edge.setTo(toNode);
		edge.setValue(value);
		edge.setExternalCall(externalCall);

		if (!getOutgoingEdges().containsKey(fromNode)) {
			getOutgoingEdges().put(fromNode, new BasicEList<ServiceCallGraphEdge>());
		}
		getOutgoingEdges().get(fromNode).add(edge);

		if (!getIncomingEdges().containsKey(toNode)) {
			getIncomingEdges().put(toNode, new BasicEList<ServiceCallGraphEdge>());
		}
		getIncomingEdges().get(toNode).add(edge);

		// add globally
		getEdges().add(edge);
		edgeMapping.put(new Quintuple<>(from.getId(), to.getId(), fromContainer == null ? null : fromContainer.getId(),
				toContainer == null ? null : toContainer.getId(), externalCall.getId()), edge);
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	@Override
	public void incrementEdge(final ResourceDemandingSEFF from, final ResourceDemandingSEFF to,
			final ResourceContainer fromContainer, final ResourceContainer toContainer,
			final ExternalCallAction externalCall) {
		ServiceCallGraphEdge edge = this.hasEdge(from, to, fromContainer, toContainer, externalCall);
		if (edge != null) {
			edge.setValue(edge.getValue() + 1);
		} else {
			this.addEdge(from, to, fromContainer, toContainer, externalCall, 1);
		}
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	@Override
	public ServiceCallGraphEdge hasEdge(final ResourceDemandingSEFF from, final ResourceDemandingSEFF to,
			final ResourceContainer fromContainer, final ResourceContainer toContainer,
			final ExternalCallAction externalCall) {

		return edgeMapping
				.get(new Quintuple<>(from.getId(), to.getId(), fromContainer == null ? null : fromContainer.getId(),
						toContainer == null ? null : toContainer.getId(), externalCall.getId()));
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	@Override
	public ServiceCallGraphNode hasNode(final ResourceDemandingSEFF node, final ResourceContainer host) {
		return nodeMapping.get(Pair.of(node.getId(), host == null ? null : host.getId()));
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	@Override
	public boolean nodeEqual(final ResourceDemandingSEFF node, final ResourceContainer host,
			final ResourceDemandingSEFF node2, final ResourceContainer host2) {
		boolean nullEqual1 = node == null && node2 == null;
		boolean nullEqual2 = host == null && host2 == null;

		boolean anyNull1 = node == null || node2 == null;
		boolean anyNull2 = host == null || host2 == null;

		if (nullEqual1 && nullEqual2) {
			return true;
		} else if (nullEqual2 && !anyNull1) {
			return node.getId().equals(node2.getId());
		} else if (nullEqual1 && !anyNull2) {
			return host.getId().equals(host2.getId());
		} else if (anyNull1 || anyNull2) {
			return false;
		} else {
			return node.getId().equals(node2.getId()) && host.getId().equals(host2.getId());
		}
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	@Override
	public void removeEdge(final ServiceCallGraphEdge edge) {
		// remove from all sub lists
		getOutgoingEdges().get(edge.getFrom()).remove(edge);
		getIncomingEdges().get(edge.getTo()).remove(edge);

		// remove from global list
		getEdges().remove(edge);
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	@Override
	public void rebuild() {
		getIncomingEdges().clear();
		getOutgoingEdges().clear();
		nodeMapping.clear();
		edgeMapping.clear();

		for (ServiceCallGraphNode node : getNodes()) {
			getOutgoingEdges().put(node, new BasicEList<ServiceCallGraphEdge>());
			getIncomingEdges().put(node, new BasicEList<ServiceCallGraphEdge>());
			nodeMapping.put(Pair.of(node.getSeff().getId(), node.getHost().getId()), node);
		}

		for (ServiceCallGraphEdge edge : getEdges()) {
			getOutgoingEdges().get(edge.getFrom()).add(edge);
			getIncomingEdges().get(edge.getTo()).add(edge);
			edgeMapping.put(new Quintuple<>(edge.getFrom().getSeff().getId(), edge.getTo().getSeff().getId(),
					edge.getFrom().getHost().getId(), edge.getTo().getHost().getId(), edge.getExternalCall().getId()),
					edge);
		}
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	@Override
	public void removeNode(final ServiceCallGraphNode node) {
		if (getIncomingEdges().get(node).size() == 0 && getOutgoingEdges().get(node).size() == 0) {
			getNodes().remove(node);
		} else {
			throw new IllegalStateException(
					"There are existing edges for a node that should be removed. Please remove the edges in advance to be able to remove the node.");
		}
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	@Override
	public NotificationChain eInverseRemove(InternalEObject otherEnd, int featureID, NotificationChain msgs) {
		switch (featureID) {
		case ServiceCallGraphPackage.SERVICE_CALL_GRAPH__NODES:
			return ((InternalEList<?>) getNodes()).basicRemove(otherEnd, msgs);
		case ServiceCallGraphPackage.SERVICE_CALL_GRAPH__EDGES:
			return ((InternalEList<?>) getEdges()).basicRemove(otherEnd, msgs);
		case ServiceCallGraphPackage.SERVICE_CALL_GRAPH__OUTGOING_EDGES:
			return ((InternalEList<?>) getOutgoingEdges()).basicRemove(otherEnd, msgs);
		case ServiceCallGraphPackage.SERVICE_CALL_GRAPH__INCOMING_EDGES:
			return ((InternalEList<?>) getIncomingEdges()).basicRemove(otherEnd, msgs);
		}
		return super.eInverseRemove(otherEnd, featureID, msgs);
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	@Override
	public Object eGet(int featureID, boolean resolve, boolean coreType) {
		switch (featureID) {
		case ServiceCallGraphPackage.SERVICE_CALL_GRAPH__NODES:
			return getNodes();
		case ServiceCallGraphPackage.SERVICE_CALL_GRAPH__EDGES:
			return getEdges();
		case ServiceCallGraphPackage.SERVICE_CALL_GRAPH__OUTGOING_EDGES:
			if (coreType)
				return getOutgoingEdges();
			else
				return getOutgoingEdges().map();
		case ServiceCallGraphPackage.SERVICE_CALL_GRAPH__INCOMING_EDGES:
			if (coreType)
				return getIncomingEdges();
			else
				return getIncomingEdges().map();
		}
		return super.eGet(featureID, resolve, coreType);
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	@SuppressWarnings("unchecked")
	@Override
	public void eSet(int featureID, Object newValue) {
		switch (featureID) {
		case ServiceCallGraphPackage.SERVICE_CALL_GRAPH__NODES:
			getNodes().clear();
			getNodes().addAll((Collection<? extends ServiceCallGraphNode>) newValue);
			return;
		case ServiceCallGraphPackage.SERVICE_CALL_GRAPH__EDGES:
			getEdges().clear();
			getEdges().addAll((Collection<? extends ServiceCallGraphEdge>) newValue);
			return;
		case ServiceCallGraphPackage.SERVICE_CALL_GRAPH__OUTGOING_EDGES:
			((EStructuralFeature.Setting) getOutgoingEdges()).set(newValue);
			return;
		case ServiceCallGraphPackage.SERVICE_CALL_GRAPH__INCOMING_EDGES:
			((EStructuralFeature.Setting) getIncomingEdges()).set(newValue);
			return;
		}
		super.eSet(featureID, newValue);
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	@Override
	public void eUnset(int featureID) {
		switch (featureID) {
		case ServiceCallGraphPackage.SERVICE_CALL_GRAPH__NODES:
			getNodes().clear();
			return;
		case ServiceCallGraphPackage.SERVICE_CALL_GRAPH__EDGES:
			getEdges().clear();
			return;
		case ServiceCallGraphPackage.SERVICE_CALL_GRAPH__OUTGOING_EDGES:
			getOutgoingEdges().clear();
			return;
		case ServiceCallGraphPackage.SERVICE_CALL_GRAPH__INCOMING_EDGES:
			getIncomingEdges().clear();
			return;
		}
		super.eUnset(featureID);
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	@Override
	public boolean eIsSet(int featureID) {
		switch (featureID) {
		case ServiceCallGraphPackage.SERVICE_CALL_GRAPH__NODES:
			return nodes != null && !nodes.isEmpty();
		case ServiceCallGraphPackage.SERVICE_CALL_GRAPH__EDGES:
			return edges != null && !edges.isEmpty();
		case ServiceCallGraphPackage.SERVICE_CALL_GRAPH__OUTGOING_EDGES:
			return outgoingEdges != null && !outgoingEdges.isEmpty();
		case ServiceCallGraphPackage.SERVICE_CALL_GRAPH__INCOMING_EDGES:
			return incomingEdges != null && !incomingEdges.isEmpty();
		}
		return super.eIsSet(featureID);
	}

} // ServiceCallGraphImpl
