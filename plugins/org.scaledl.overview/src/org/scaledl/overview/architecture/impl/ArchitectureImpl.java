/**
 */
package org.scaledl.overview.architecture.impl;

import java.util.Collection;

import org.eclipse.emf.common.notify.NotificationChain;

import org.eclipse.emf.common.util.EList;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.InternalEObject;

import org.eclipse.emf.ecore.util.EObjectContainmentEList;
import org.eclipse.emf.ecore.util.EObjectContainmentWithInverseEList;
import org.eclipse.emf.ecore.util.InternalEList;

import org.scaledl.overview.application.OperationInterface;

import org.scaledl.overview.architecture.Architecture;
import org.scaledl.overview.architecture.ArchitecturePackage;
import org.scaledl.overview.architecture.CloudEnvironment;
import org.scaledl.overview.architecture.ExternalConnection;
import org.scaledl.overview.architecture.Proxy;

import org.scaledl.overview.core.impl.EntityImpl;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>Architecture</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * <ul>
 *   <li>{@link org.scaledl.overview.architecture.impl.ArchitectureImpl#getCloudEnvironments <em>Cloud Environments</em>}</li>
 *   <li>{@link org.scaledl.overview.architecture.impl.ArchitectureImpl#getProxies <em>Proxies</em>}</li>
 *   <li>{@link org.scaledl.overview.architecture.impl.ArchitectureImpl#getUsageConnections <em>Usage Connections</em>}</li>
 *   <li>{@link org.scaledl.overview.architecture.impl.ArchitectureImpl#getExternalConnections <em>External Connections</em>}</li>
 *   <li>{@link org.scaledl.overview.architecture.impl.ArchitectureImpl#getUnresolvedOperationInterfaces <em>Unresolved Operation Interfaces</em>}</li>
 * </ul>
 * </p>
 *
 * @generated
 */
public class ArchitectureImpl extends EntityImpl implements Architecture {
	/**
	 * The cached value of the '{@link #getCloudEnvironments() <em>Cloud Environments</em>}' containment reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getCloudEnvironments()
	 * @generated
	 * @ordered
	 */
	protected EList<CloudEnvironment> cloudEnvironments;

	/**
	 * The cached value of the '{@link #getProxies() <em>Proxies</em>}' containment reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getProxies()
	 * @generated
	 * @ordered
	 */
	protected EList<Proxy> proxies;

	/**
	 * The cached value of the '{@link #getUsageConnections() <em>Usage Connections</em>}' containment reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getUsageConnections()
	 * @generated
	 * @ordered
	 */
	protected EList<ExternalConnection> usageConnections;

	/**
	 * The cached value of the '{@link #getExternalConnections() <em>External Connections</em>}' containment reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getExternalConnections()
	 * @generated
	 * @ordered
	 */
	protected EList<ExternalConnection> externalConnections;

	/**
	 * The cached value of the '{@link #getUnresolvedOperationInterfaces() <em>Unresolved Operation Interfaces</em>}' containment reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getUnresolvedOperationInterfaces()
	 * @generated
	 * @ordered
	 */
	protected EList<OperationInterface> unresolvedOperationInterfaces;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	protected ArchitectureImpl() {
		super();
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	protected EClass eStaticClass() {
		return ArchitecturePackage.Literals.ARCHITECTURE;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EList<CloudEnvironment> getCloudEnvironments() {
		if (cloudEnvironments == null) {
			cloudEnvironments = new EObjectContainmentWithInverseEList<CloudEnvironment>(CloudEnvironment.class, this, ArchitecturePackage.ARCHITECTURE__CLOUD_ENVIRONMENTS, ArchitecturePackage.CLOUD_ENVIRONMENT__ARCHITECTURE);
		}
		return cloudEnvironments;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EList<Proxy> getProxies() {
		if (proxies == null) {
			proxies = new EObjectContainmentEList<Proxy>(Proxy.class, this, ArchitecturePackage.ARCHITECTURE__PROXIES);
		}
		return proxies;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EList<ExternalConnection> getUsageConnections() {
		if (usageConnections == null) {
			usageConnections = new EObjectContainmentEList<ExternalConnection>(ExternalConnection.class, this, ArchitecturePackage.ARCHITECTURE__USAGE_CONNECTIONS);
		}
		return usageConnections;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EList<ExternalConnection> getExternalConnections() {
		if (externalConnections == null) {
			externalConnections = new EObjectContainmentEList<ExternalConnection>(ExternalConnection.class, this, ArchitecturePackage.ARCHITECTURE__EXTERNAL_CONNECTIONS);
		}
		return externalConnections;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EList<OperationInterface> getUnresolvedOperationInterfaces() {
		if (unresolvedOperationInterfaces == null) {
			unresolvedOperationInterfaces = new EObjectContainmentEList<OperationInterface>(OperationInterface.class, this, ArchitecturePackage.ARCHITECTURE__UNRESOLVED_OPERATION_INTERFACES);
		}
		return unresolvedOperationInterfaces;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@SuppressWarnings("unchecked")
	@Override
	public NotificationChain eInverseAdd(InternalEObject otherEnd, int featureID, NotificationChain msgs) {
		switch (featureID) {
			case ArchitecturePackage.ARCHITECTURE__CLOUD_ENVIRONMENTS:
				return ((InternalEList<InternalEObject>)(InternalEList<?>)getCloudEnvironments()).basicAdd(otherEnd, msgs);
		}
		return super.eInverseAdd(otherEnd, featureID, msgs);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public NotificationChain eInverseRemove(InternalEObject otherEnd, int featureID, NotificationChain msgs) {
		switch (featureID) {
			case ArchitecturePackage.ARCHITECTURE__CLOUD_ENVIRONMENTS:
				return ((InternalEList<?>)getCloudEnvironments()).basicRemove(otherEnd, msgs);
			case ArchitecturePackage.ARCHITECTURE__PROXIES:
				return ((InternalEList<?>)getProxies()).basicRemove(otherEnd, msgs);
			case ArchitecturePackage.ARCHITECTURE__USAGE_CONNECTIONS:
				return ((InternalEList<?>)getUsageConnections()).basicRemove(otherEnd, msgs);
			case ArchitecturePackage.ARCHITECTURE__EXTERNAL_CONNECTIONS:
				return ((InternalEList<?>)getExternalConnections()).basicRemove(otherEnd, msgs);
			case ArchitecturePackage.ARCHITECTURE__UNRESOLVED_OPERATION_INTERFACES:
				return ((InternalEList<?>)getUnresolvedOperationInterfaces()).basicRemove(otherEnd, msgs);
		}
		return super.eInverseRemove(otherEnd, featureID, msgs);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public Object eGet(int featureID, boolean resolve, boolean coreType) {
		switch (featureID) {
			case ArchitecturePackage.ARCHITECTURE__CLOUD_ENVIRONMENTS:
				return getCloudEnvironments();
			case ArchitecturePackage.ARCHITECTURE__PROXIES:
				return getProxies();
			case ArchitecturePackage.ARCHITECTURE__USAGE_CONNECTIONS:
				return getUsageConnections();
			case ArchitecturePackage.ARCHITECTURE__EXTERNAL_CONNECTIONS:
				return getExternalConnections();
			case ArchitecturePackage.ARCHITECTURE__UNRESOLVED_OPERATION_INTERFACES:
				return getUnresolvedOperationInterfaces();
		}
		return super.eGet(featureID, resolve, coreType);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@SuppressWarnings("unchecked")
	@Override
	public void eSet(int featureID, Object newValue) {
		switch (featureID) {
			case ArchitecturePackage.ARCHITECTURE__CLOUD_ENVIRONMENTS:
				getCloudEnvironments().clear();
				getCloudEnvironments().addAll((Collection<? extends CloudEnvironment>)newValue);
				return;
			case ArchitecturePackage.ARCHITECTURE__PROXIES:
				getProxies().clear();
				getProxies().addAll((Collection<? extends Proxy>)newValue);
				return;
			case ArchitecturePackage.ARCHITECTURE__USAGE_CONNECTIONS:
				getUsageConnections().clear();
				getUsageConnections().addAll((Collection<? extends ExternalConnection>)newValue);
				return;
			case ArchitecturePackage.ARCHITECTURE__EXTERNAL_CONNECTIONS:
				getExternalConnections().clear();
				getExternalConnections().addAll((Collection<? extends ExternalConnection>)newValue);
				return;
			case ArchitecturePackage.ARCHITECTURE__UNRESOLVED_OPERATION_INTERFACES:
				getUnresolvedOperationInterfaces().clear();
				getUnresolvedOperationInterfaces().addAll((Collection<? extends OperationInterface>)newValue);
				return;
		}
		super.eSet(featureID, newValue);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public void eUnset(int featureID) {
		switch (featureID) {
			case ArchitecturePackage.ARCHITECTURE__CLOUD_ENVIRONMENTS:
				getCloudEnvironments().clear();
				return;
			case ArchitecturePackage.ARCHITECTURE__PROXIES:
				getProxies().clear();
				return;
			case ArchitecturePackage.ARCHITECTURE__USAGE_CONNECTIONS:
				getUsageConnections().clear();
				return;
			case ArchitecturePackage.ARCHITECTURE__EXTERNAL_CONNECTIONS:
				getExternalConnections().clear();
				return;
			case ArchitecturePackage.ARCHITECTURE__UNRESOLVED_OPERATION_INTERFACES:
				getUnresolvedOperationInterfaces().clear();
				return;
		}
		super.eUnset(featureID);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public boolean eIsSet(int featureID) {
		switch (featureID) {
			case ArchitecturePackage.ARCHITECTURE__CLOUD_ENVIRONMENTS:
				return cloudEnvironments != null && !cloudEnvironments.isEmpty();
			case ArchitecturePackage.ARCHITECTURE__PROXIES:
				return proxies != null && !proxies.isEmpty();
			case ArchitecturePackage.ARCHITECTURE__USAGE_CONNECTIONS:
				return usageConnections != null && !usageConnections.isEmpty();
			case ArchitecturePackage.ARCHITECTURE__EXTERNAL_CONNECTIONS:
				return externalConnections != null && !externalConnections.isEmpty();
			case ArchitecturePackage.ARCHITECTURE__UNRESOLVED_OPERATION_INTERFACES:
				return unresolvedOperationInterfaces != null && !unresolvedOperationInterfaces.isEmpty();
		}
		return super.eIsSet(featureID);
	}

} //ArchitectureImpl
