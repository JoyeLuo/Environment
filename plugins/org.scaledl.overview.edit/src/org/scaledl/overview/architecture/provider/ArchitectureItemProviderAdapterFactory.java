/**
 */
package org.scaledl.overview.architecture.provider;

import java.util.ArrayList;
import java.util.Collection;

import org.eclipse.emf.common.notify.Adapter;
import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.common.notify.Notifier;
import org.eclipse.emf.edit.provider.ChangeNotifier;
import org.eclipse.emf.edit.provider.ComposeableAdapterFactory;
import org.eclipse.emf.edit.provider.ComposedAdapterFactory;
import org.eclipse.emf.edit.provider.IChangeNotifier;
import org.eclipse.emf.edit.provider.IDisposable;
import org.eclipse.emf.edit.provider.IEditingDomainItemProvider;
import org.eclipse.emf.edit.provider.IItemLabelProvider;
import org.eclipse.emf.edit.provider.IItemPropertySource;
import org.eclipse.emf.edit.provider.INotifyChangedListener;
import org.eclipse.emf.edit.provider.IStructuredItemContentProvider;
import org.eclipse.emf.edit.provider.ITreeItemContentProvider;
import org.scaledl.overview.architecture.util.ArchitectureAdapterFactory;

/**
 * This is the factory that is used to provide the interfaces needed to support Viewers.
 * The adapters generated by this factory convert EMF adapter notifications into calls to {@link #fireNotifyChanged fireNotifyChanged}.
 * The adapters also support Eclipse property sheets.
 * Note that most of the adapters are shared among multiple instances.
 * <!-- begin-user-doc -->
 * <!-- end-user-doc -->
 * @generated
 */
public class ArchitectureItemProviderAdapterFactory extends ArchitectureAdapterFactory implements ComposeableAdapterFactory, IChangeNotifier, IDisposable {
	/**
	 * This keeps track of the root adapter factory that delegates to this adapter factory.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	protected ComposedAdapterFactory parentAdapterFactory;

	/**
	 * This is used to implement {@link org.eclipse.emf.edit.provider.IChangeNotifier}.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	protected IChangeNotifier changeNotifier = new ChangeNotifier();

	/**
	 * This keeps track of all the supported types checked by {@link #isFactoryForType isFactoryForType}.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	protected Collection<Object> supportedTypes = new ArrayList<Object>();

	/**
	 * This constructs an instance.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public ArchitectureItemProviderAdapterFactory() {
		supportedTypes.add(IEditingDomainItemProvider.class);
		supportedTypes.add(IStructuredItemContentProvider.class);
		supportedTypes.add(ITreeItemContentProvider.class);
		supportedTypes.add(IItemLabelProvider.class);
		supportedTypes.add(IItemPropertySource.class);
	}

	/**
	 * This keeps track of the one adapter used for all {@link org.scaledl.overview.architecture.Architecture} instances.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	protected ArchitectureItemProvider architectureItemProvider;

	/**
	 * This creates an adapter for a {@link org.scaledl.overview.architecture.Architecture}.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public Adapter createArchitectureAdapter() {
		if (architectureItemProvider == null) {
			architectureItemProvider = new ArchitectureItemProvider(this);
		}

		return architectureItemProvider;
	}

	/**
	 * This keeps track of the one adapter used for all {@link org.scaledl.overview.architecture.CloudEnvironment} instances.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	protected CloudEnvironmentItemProvider cloudEnvironmentItemProvider;

	/**
	 * This creates an adapter for a {@link org.scaledl.overview.architecture.CloudEnvironment}.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public Adapter createCloudEnvironmentAdapter() {
		if (cloudEnvironmentItemProvider == null) {
			cloudEnvironmentItemProvider = new CloudEnvironmentItemProvider(this);
		}

		return cloudEnvironmentItemProvider;
	}

	/**
	 * This keeps track of the one adapter used for all {@link org.scaledl.overview.architecture.InfrastructureLayer} instances.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	protected InfrastructureLayerItemProvider infrastructureLayerItemProvider;

	/**
	 * This creates an adapter for a {@link org.scaledl.overview.architecture.InfrastructureLayer}.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public Adapter createInfrastructureLayerAdapter() {
		if (infrastructureLayerItemProvider == null) {
			infrastructureLayerItemProvider = new InfrastructureLayerItemProvider(this);
		}

		return infrastructureLayerItemProvider;
	}

	/**
	 * This keeps track of the one adapter used for all {@link org.scaledl.overview.architecture.PlatformLayer} instances.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	protected PlatformLayerItemProvider platformLayerItemProvider;

	/**
	 * This creates an adapter for a {@link org.scaledl.overview.architecture.PlatformLayer}.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public Adapter createPlatformLayerAdapter() {
		if (platformLayerItemProvider == null) {
			platformLayerItemProvider = new PlatformLayerItemProvider(this);
		}

		return platformLayerItemProvider;
	}

	/**
	 * This keeps track of the one adapter used for all {@link org.scaledl.overview.architecture.SoftwareLayer} instances.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	protected SoftwareLayerItemProvider softwareLayerItemProvider;

	/**
	 * This creates an adapter for a {@link org.scaledl.overview.architecture.SoftwareLayer}.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public Adapter createSoftwareLayerAdapter() {
		if (softwareLayerItemProvider == null) {
			softwareLayerItemProvider = new SoftwareLayerItemProvider(this);
		}

		return softwareLayerItemProvider;
	}

	/**
	 * This keeps track of the one adapter used for all {@link org.scaledl.overview.architecture.ComputingInfrastructureService} instances.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	protected ComputingInfrastructureServiceItemProvider computingInfrastructureServiceItemProvider;

	/**
	 * This creates an adapter for a {@link org.scaledl.overview.architecture.ComputingInfrastructureService}.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public Adapter createComputingInfrastructureServiceAdapter() {
		if (computingInfrastructureServiceItemProvider == null) {
			computingInfrastructureServiceItemProvider = new ComputingInfrastructureServiceItemProvider(this);
		}

		return computingInfrastructureServiceItemProvider;
	}

	/**
	 * This keeps track of the one adapter used for all {@link org.scaledl.overview.architecture.PlatformRuntimeService} instances.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	protected PlatformRuntimeServiceItemProvider platformRuntimeServiceItemProvider;

	/**
	 * This creates an adapter for a {@link org.scaledl.overview.architecture.PlatformRuntimeService}.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public Adapter createPlatformRuntimeServiceAdapter() {
		if (platformRuntimeServiceItemProvider == null) {
			platformRuntimeServiceItemProvider = new PlatformRuntimeServiceItemProvider(this);
		}

		return platformRuntimeServiceItemProvider;
	}

	/**
	 * This keeps track of the one adapter used for all {@link org.scaledl.overview.architecture.PlatformSupportService} instances.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	protected PlatformSupportServiceItemProvider platformSupportServiceItemProvider;

	/**
	 * This creates an adapter for a {@link org.scaledl.overview.architecture.PlatformSupportService}.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public Adapter createPlatformSupportServiceAdapter() {
		if (platformSupportServiceItemProvider == null) {
			platformSupportServiceItemProvider = new PlatformSupportServiceItemProvider(this);
		}

		return platformSupportServiceItemProvider;
	}

	/**
	 * This keeps track of the one adapter used for all {@link org.scaledl.overview.architecture.ProvidedPlatformRuntimeService} instances.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	protected ProvidedPlatformRuntimeServiceItemProvider providedPlatformRuntimeServiceItemProvider;

	/**
	 * This creates an adapter for a {@link org.scaledl.overview.architecture.ProvidedPlatformRuntimeService}.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public Adapter createProvidedPlatformRuntimeServiceAdapter() {
		if (providedPlatformRuntimeServiceItemProvider == null) {
			providedPlatformRuntimeServiceItemProvider = new ProvidedPlatformRuntimeServiceItemProvider(this);
		}

		return providedPlatformRuntimeServiceItemProvider;
	}

	/**
	 * This keeps track of the one adapter used for all {@link org.scaledl.overview.architecture.ProvidedPlatformSupportService} instances.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	protected ProvidedPlatformSupportServiceItemProvider providedPlatformSupportServiceItemProvider;

	/**
	 * This creates an adapter for a {@link org.scaledl.overview.architecture.ProvidedPlatformSupportService}.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public Adapter createProvidedPlatformSupportServiceAdapter() {
		if (providedPlatformSupportServiceItemProvider == null) {
			providedPlatformSupportServiceItemProvider = new ProvidedPlatformSupportServiceItemProvider(this);
		}

		return providedPlatformSupportServiceItemProvider;
	}

	/**
	 * This keeps track of the one adapter used for all {@link org.scaledl.overview.architecture.SoftwareService} instances.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	protected SoftwareServiceItemProvider softwareServiceItemProvider;

	/**
	 * This creates an adapter for a {@link org.scaledl.overview.architecture.SoftwareService}.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public Adapter createSoftwareServiceAdapter() {
		if (softwareServiceItemProvider == null) {
			softwareServiceItemProvider = new SoftwareServiceItemProvider(this);
		}

		return softwareServiceItemProvider;
	}

	/**
	 * This keeps track of the one adapter used for all {@link org.scaledl.overview.architecture.ProvidedSoftwareService} instances.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	protected ProvidedSoftwareServiceItemProvider providedSoftwareServiceItemProvider;

	/**
	 * This creates an adapter for a {@link org.scaledl.overview.architecture.ProvidedSoftwareService}.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public Adapter createProvidedSoftwareServiceAdapter() {
		if (providedSoftwareServiceItemProvider == null) {
			providedSoftwareServiceItemProvider = new ProvidedSoftwareServiceItemProvider(this);
		}

		return providedSoftwareServiceItemProvider;
	}

	/**
	 * This keeps track of the one adapter used for all {@link org.scaledl.overview.architecture.ExternalSoftwareService} instances.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	protected ExternalSoftwareServiceItemProvider externalSoftwareServiceItemProvider;

	/**
	 * This creates an adapter for a {@link org.scaledl.overview.architecture.ExternalSoftwareService}.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public Adapter createExternalSoftwareServiceAdapter() {
		if (externalSoftwareServiceItemProvider == null) {
			externalSoftwareServiceItemProvider = new ExternalSoftwareServiceItemProvider(this);
		}

		return externalSoftwareServiceItemProvider;
	}

	/**
	 * This keeps track of the one adapter used for all {@link org.scaledl.overview.architecture.InternalConnection} instances.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	protected InternalConnectionItemProvider internalConnectionItemProvider;

	/**
	 * This creates an adapter for a {@link org.scaledl.overview.architecture.InternalConnection}.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public Adapter createInternalConnectionAdapter() {
		if (internalConnectionItemProvider == null) {
			internalConnectionItemProvider = new InternalConnectionItemProvider(this);
		}

		return internalConnectionItemProvider;
	}

	/**
	 * This keeps track of the one adapter used for all {@link org.scaledl.overview.architecture.ExternalConnection} instances.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	protected ExternalConnectionItemProvider externalConnectionItemProvider;

	/**
	 * This creates an adapter for a {@link org.scaledl.overview.architecture.ExternalConnection}.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public Adapter createExternalConnectionAdapter() {
		if (externalConnectionItemProvider == null) {
			externalConnectionItemProvider = new ExternalConnectionItemProvider(this);
		}

		return externalConnectionItemProvider;
	}

	/**
	 * This keeps track of the one adapter used for all {@link org.scaledl.overview.architecture.ServiceProxy} instances.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	protected ServiceProxyItemProvider serviceProxyItemProvider;

	/**
	 * This creates an adapter for a {@link org.scaledl.overview.architecture.ServiceProxy}.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public Adapter createServiceProxyAdapter() {
		if (serviceProxyItemProvider == null) {
			serviceProxyItemProvider = new ServiceProxyItemProvider(this);
		}

		return serviceProxyItemProvider;
	}

	/**
	 * This keeps track of the one adapter used for all {@link org.scaledl.overview.architecture.UsageProxy} instances.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	protected UsageProxyItemProvider usageProxyItemProvider;

	/**
	 * This creates an adapter for a {@link org.scaledl.overview.architecture.UsageProxy}.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public Adapter createUsageProxyAdapter() {
		if (usageProxyItemProvider == null) {
			usageProxyItemProvider = new UsageProxyItemProvider(this);
		}

		return usageProxyItemProvider;
	}

	/**
	 * This returns the root adapter factory that contains this factory.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public ComposeableAdapterFactory getRootAdapterFactory() {
		return parentAdapterFactory == null ? this : parentAdapterFactory.getRootAdapterFactory();
	}

	/**
	 * This sets the composed adapter factory that contains this factory.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void setParentAdapterFactory(ComposedAdapterFactory parentAdapterFactory) {
		this.parentAdapterFactory = parentAdapterFactory;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public boolean isFactoryForType(Object type) {
		return supportedTypes.contains(type) || super.isFactoryForType(type);
	}

	/**
	 * This implementation substitutes the factory itself as the key for the adapter.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public Adapter adapt(Notifier notifier, Object type) {
		return super.adapt(notifier, this);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public Object adapt(Object object, Object type) {
		if (isFactoryForType(type)) {
			Object adapter = super.adapt(object, type);
			if (!(type instanceof Class<?>) || (((Class<?>)type).isInstance(adapter))) {
				return adapter;
			}
		}

		return null;
	}

	/**
	 * This adds a listener.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void addListener(INotifyChangedListener notifyChangedListener) {
		changeNotifier.addListener(notifyChangedListener);
	}

	/**
	 * This removes a listener.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void removeListener(INotifyChangedListener notifyChangedListener) {
		changeNotifier.removeListener(notifyChangedListener);
	}

	/**
	 * This delegates to {@link #changeNotifier} and to {@link #parentAdapterFactory}.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void fireNotifyChanged(Notification notification) {
		changeNotifier.fireNotifyChanged(notification);

		if (parentAdapterFactory != null) {
			parentAdapterFactory.fireNotifyChanged(notification);
		}
	}

	/**
	 * This disposes all of the item providers created by this factory. 
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void dispose() {
		if (architectureItemProvider != null) architectureItemProvider.dispose();
		if (cloudEnvironmentItemProvider != null) cloudEnvironmentItemProvider.dispose();
		if (infrastructureLayerItemProvider != null) infrastructureLayerItemProvider.dispose();
		if (platformLayerItemProvider != null) platformLayerItemProvider.dispose();
		if (softwareLayerItemProvider != null) softwareLayerItemProvider.dispose();
		if (computingInfrastructureServiceItemProvider != null) computingInfrastructureServiceItemProvider.dispose();
		if (platformRuntimeServiceItemProvider != null) platformRuntimeServiceItemProvider.dispose();
		if (platformSupportServiceItemProvider != null) platformSupportServiceItemProvider.dispose();
		if (providedPlatformRuntimeServiceItemProvider != null) providedPlatformRuntimeServiceItemProvider.dispose();
		if (providedPlatformSupportServiceItemProvider != null) providedPlatformSupportServiceItemProvider.dispose();
		if (softwareServiceItemProvider != null) softwareServiceItemProvider.dispose();
		if (providedSoftwareServiceItemProvider != null) providedSoftwareServiceItemProvider.dispose();
		if (externalSoftwareServiceItemProvider != null) externalSoftwareServiceItemProvider.dispose();
		if (internalConnectionItemProvider != null) internalConnectionItemProvider.dispose();
		if (externalConnectionItemProvider != null) externalConnectionItemProvider.dispose();
		if (serviceProxyItemProvider != null) serviceProxyItemProvider.dispose();
		if (usageProxyItemProvider != null) usageProxyItemProvider.dispose();
	}

}
