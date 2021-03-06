package eu.cloudscaleproject.env.overview.wizard.composites;


import java.util.List;

import org.eclipse.core.databinding.beans.PojoObservables;
import org.eclipse.core.databinding.observable.list.IObservableList;
import org.eclipse.core.databinding.observable.map.IObservableMap;
import org.eclipse.core.databinding.property.Properties;
import org.eclipse.jface.databinding.viewers.ObservableListContentProvider;
import org.eclipse.jface.databinding.viewers.ObservableMapLabelProvider;
import org.eclipse.jface.viewers.ComboViewer;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.scaledl.overview.Overview;
import org.scaledl.overview.architecture.ArchitectureFactory;
import org.scaledl.overview.architecture.CloudEnvironment;
import org.scaledl.overview.architecture.ComputingInfrastructureService;
import org.scaledl.overview.architecture.PlatformRuntimeService;
import org.scaledl.overview.architecture.ProvidedPlatformRuntimeService;
import org.scaledl.overview.deployment.RuntimeDeployment;
import org.scaledl.overview.specification.CloudEnvironmentDescriptor;
import org.scaledl.overview.specification.CloudSpecification;
import org.scaledl.overview.specification.ComputingInfrastructureServiceDescriptor;
import org.scaledl.overview.specification.PlatformRuntimeServiceDescriptor;
import org.scaledl.overview.specification.PlatformServiceDescriptor;
import org.scaledl.overview.specification.ProvidedPlatformRuntimeServiceDescriptor;
import org.scaledl.overview.specification.ServiceSpecification;
import org.scaledl.overview.specification.Specification;
import org.scaledl.overview.util.OverviewSpecificationUtil;

import eu.cloudscaleproject.env.overview.wizard.util.OverviewHelper;

public class DeploymentComposite extends Composite
{
	private List<CloudSpecification> cloudSpecifications;

	private Button rbIaaS;
	private Button rbPaaS;
	private ComboViewer comboViewer;
	private Combo combo;
	private Label lblPlatformService;
	private ComboViewer cvPaaS;
	private Combo cbPaaS;
	private ServiceDeploymentComposite deploymentComposite;

	/**
	 * Create the composite.
	 * @param parent
	 * @param style
	 */
	public DeploymentComposite(Composite parent, int style)
	{
		super(parent, style);

		createComposite(parent);
	}
	
	private void createComposite (Composite parent)
	{
		this.setLayout(new GridLayout(1, false));

		Group grpCloudEnvironment = new Group(this, SWT.NONE);
		grpCloudEnvironment.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false, 1, 1));
		grpCloudEnvironment.setText("Cloud environment");
		GridLayout gl_grpCloudEnvironment = new GridLayout(2, false);
		gl_grpCloudEnvironment.horizontalSpacing = 15;
		grpCloudEnvironment.setLayout(gl_grpCloudEnvironment);
		
		Label lblService = new Label(grpCloudEnvironment, SWT.NONE);
		lblService.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		lblService.setText("Providers selection");
		
		Composite composite = new Composite(grpCloudEnvironment, SWT.NONE);
		composite.setLayout(new RowLayout(SWT.HORIZONTAL));
		GridData gd_composite = new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1);
		gd_composite.widthHint = 192;
		gd_composite.heightHint = 29;
		composite.setLayoutData(gd_composite);
		
		rbIaaS = new Button(composite, SWT.RADIO);
		rbIaaS.setText("IaaS");
		
		rbPaaS = new Button(composite, SWT.RADIO);
		rbPaaS.setText("PaaS");
		
		Label lblCloudEnvirionment = new Label(grpCloudEnvironment, SWT.NONE);
		lblCloudEnvirionment.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		lblCloudEnvirionment.setText("Cloud Envirionment");
		
		comboViewer = new ComboViewer(grpCloudEnvironment, SWT.NONE);
		combo = comboViewer.getCombo();
		combo.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));

		rbIaaS.setSelection(true);
		
		lblPlatformService = new Label(grpCloudEnvironment, SWT.NONE);
		lblPlatformService.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1));
		lblPlatformService.setAlignment(SWT.RIGHT);
		lblPlatformService.setText("Platform service");
		cvPaaS = new ComboViewer(grpCloudEnvironment, SWT.NONE);
		cbPaaS = cvPaaS.getCombo();
		cbPaaS.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1));
		
		Group grpDeploymentConfiguration = new Group(this, SWT.NONE);
		grpDeploymentConfiguration.setText("Deployment configuration");
		grpDeploymentConfiguration.setLayout(new FillLayout(SWT.HORIZONTAL));
		grpDeploymentConfiguration.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
		
		this.deploymentComposite = new ServiceDeploymentComposite(grpDeploymentConfiguration, SWT.NONE);

		initCustomBindings();
		updateXaaSSelection();
	}

	private void initCustomBindings()
	{
		
		SelectionListener rbListener = new SelectionListener(){

			@Override
			public void widgetSelected(SelectionEvent e) {
				// 2 events - 1st for desected button, second for selected button
				boolean isSelected = ((Button)e.getSource()).getSelection();
				if (isSelected)
					updateXaaSSelection();
			}

			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
			}
		};
		rbIaaS.addSelectionListener(rbListener);
		rbPaaS.addSelectionListener(rbListener);

		// CloudSpecification instance is changing therefor is not possible to use standard bindings
		comboViewer.addSelectionChangedListener(new ISelectionChangedListener() {
			public void selectionChanged(SelectionChangedEvent event) {
				updateSpecificationSelection();
			}
		});
		
		
		cvPaaS.addSelectionChangedListener(new ISelectionChangedListener(){
			@Override
			public void selectionChanged(SelectionChangedEvent event) {
				updatePaaSSelection();
			}
		});

	}
	
	private void updateXaaSSelection()
	{
		if (rbIaaS.getSelection())
			this.cloudSpecifications = OverviewHelper.getCloudSpecificationsIaaS();
		else if (rbPaaS.getSelection())
			this.cloudSpecifications = OverviewHelper.getCloudSpecificationsPaaS();
		
		//lblPlatformService.setEnabled(rbPaaS.getSelection());
		//cbPaaS.setEnabled(rbPaaS.getSelection());
		cvPaaS.setSelection(null);
		
		//
		// Add CloudSpecifications to combo
		//
		ObservableListContentProvider listContentProvider = new ObservableListContentProvider();
		IObservableMap observeMap = PojoObservables.observeMap(listContentProvider.getKnownElements(), CloudSpecification.class, "descriptor.name");
		comboViewer.setLabelProvider(new ObservableMapLabelProvider(observeMap));
		comboViewer.setContentProvider(listContentProvider);
		IObservableList selfList = Properties.selfList(CloudSpecification.class).observe(cloudSpecifications);
		comboViewer.setInput(selfList);
		
		// set default selection
		if (!cloudSpecifications.isEmpty())
		{
			final ISelection selection = new StructuredSelection(cloudSpecifications.get(0));
			comboViewer.setSelection(selection);
		}
	}

	private void updateSpecificationSelection()
	{
		if (rbPaaS.getSelection())
		{
			initPaaS(getSelectedCloudSpecification());
		}
		else
		{
			initPaaS(OverviewHelper.getServiceSpecifications().get(0));
		}
		
		deploymentComposite.setComputingInfrastructureServiceDescriptor(getComputingInfrastructureServiceDescriptor());
	}
	
	private void updatePaaSSelection ()
	{
		deploymentComposite.setComputingInfrastructureServiceDescriptor(getComputingInfrastructureServiceDescriptor());
	}
	
	private void initPaaS (Specification specification)
	{
		List<? extends PlatformServiceDescriptor> paaSDescriptors = null;

		if (specification instanceof CloudSpecification)
			paaSDescriptors = OverviewHelper.getPaaSRuntimeDescriptors((CloudSpecification)specification);
		else if (specification instanceof ServiceSpecification)
			paaSDescriptors = OverviewHelper.getPlatformDescriptors((ServiceSpecification)specification, false, true);

		ObservableListContentProvider listContentProvider = new ObservableListContentProvider();
		IObservableMap observeMap = PojoObservables.observeMap(listContentProvider.getKnownElements(), ProvidedPlatformRuntimeServiceDescriptor.class, "name");
		cvPaaS.setLabelProvider(new ObservableMapLabelProvider(observeMap));
		cvPaaS.setContentProvider(listContentProvider);
		IObservableList selfList = Properties.selfList(CloudSpecification.class).observe(paaSDescriptors);
		cvPaaS.setInput(selfList);
		
		// set default selection
		if (!paaSDescriptors.isEmpty())
		{
			final ISelection selection = new StructuredSelection(paaSDescriptors.get(0));
			cvPaaS.setSelection(selection);
		}
	}

	private CloudSpecification getSelectedCloudSpecification ()
	{
		if (comboViewer.getSelection() == null) return null;

		CloudSpecification cs = (CloudSpecification) ((IStructuredSelection)comboViewer.getSelection()).getFirstElement();
		
		return cs;
	}
	
	private ProvidedPlatformRuntimeServiceDescriptor getSelectedRuntimeServiceDescriptor ()
	{
		if (cvPaaS.getSelection() == null) return null;

		ProvidedPlatformRuntimeServiceDescriptor prsd = (ProvidedPlatformRuntimeServiceDescriptor) ((IStructuredSelection)cvPaaS.getSelection()).getFirstElement();
		
		return prsd;
	}
	
	private ComputingInfrastructureServiceDescriptor getComputingInfrastructureServiceDescriptor()
	{
		if (rbIaaS.getSelection())
		{
			CloudSpecification cs = getSelectedCloudSpecification();
			List<ComputingInfrastructureServiceDescriptor> iaaSRuntimeDescriptors = OverviewHelper.getIaaSRuntimeDescriptors(cs);
			if (!iaaSRuntimeDescriptors.isEmpty())
				return iaaSRuntimeDescriptors.get(0);
		}
		else
		{
			ProvidedPlatformRuntimeServiceDescriptor selectedRuntimeServiceDescriptor = getSelectedRuntimeServiceDescriptor();
			if (selectedRuntimeServiceDescriptor != null)
                return selectedRuntimeServiceDescriptor.getInfrastructureServiceDescriptor();
		}
		
		return null;
		//throw new IllegalStateException("Computing inrfrastructure service not available.");
	}

	public boolean isComplete()
	{
		return deploymentComposite.isComplete();
	}
	
	public CloudSpecification getCloudSpecification ()
	{
		return getSelectedCloudSpecification();
	}
	
	public PlatformRuntimeService createRuntimeService(Overview overview)
	{
		CloudEnvironment ce = overview.getArchitecture().getCloudEnvironments().get(0);
		CloudSpecification cs = getSelectedCloudSpecification();
		

		// Set CloudEnvironment Descriptor
		CloudEnvironmentDescriptor csd = OverviewSpecificationUtil.getSystemDescriptor(overview, cs.getDescriptor());
		ce.setCloudEnvironmentDescriptor(csd);
		if (!csd.getAvailabilityZones().isEmpty())
			ce.setAvailabilityZoneDescriptor(csd.getAvailabilityZones().get(0));
		
		ce.setName(csd.getName());

		if (rbPaaS.getSelection())
		{
			// Architecture
			ProvidedPlatformRuntimeService pprs = ArchitectureFactory.eINSTANCE.createProvidedPlatformRuntimeService();
			ce.getPlatformLayer().getServices().add(pprs);
			
			// Specification
			ProvidedPlatformRuntimeServiceDescriptor pprsd_org = (ProvidedPlatformRuntimeServiceDescriptor) ((StructuredSelection)cvPaaS.getSelection()).getFirstElement();
		    ProvidedPlatformRuntimeServiceDescriptor pprsd = OverviewSpecificationUtil.getSystemDescriptor(overview, pprsd_org);
			pprs.setDescriptor(pprsd);
			pprs.setName(pprsd.getName());

			RuntimeDeployment runtimeDeployment = deploymentComposite.createAndInsertDeployment(overview);
			pprs.setDeployment(runtimeDeployment);

			return pprs;
		}
		else if (rbIaaS.getSelection())
		{
			// Architecture 
			PlatformRuntimeService pprs = ArchitectureFactory.eINSTANCE.createPlatformRuntimeService();
			ComputingInfrastructureService cis = ArchitectureFactory.eINSTANCE.createComputingInfrastructureService();

			ce.getInfrastructureLayer().getServices().add(cis);
			ce.getPlatformLayer().getServices().add(pprs);

			PlatformRuntimeServiceDescriptor pprsd_org = (PlatformRuntimeServiceDescriptor) ((StructuredSelection)cvPaaS.getSelection()).getFirstElement();
		    PlatformRuntimeServiceDescriptor pprsd = OverviewSpecificationUtil.getSystemDescriptor(overview, pprsd_org);
			pprs.setDescriptor(pprsd);
			pprs.setName(pprsd.getName());

			cis.getPlatformServices().add(pprs);
			
			// Specification
			ComputingInfrastructureServiceDescriptor cisd_org = getComputingInfrastructureServiceDescriptor();
			ComputingInfrastructureServiceDescriptor cisd = OverviewSpecificationUtil.getSystemDescriptor(overview, cisd_org);
			cis.setDescriptor(cisd);
			cis.setName(cisd.getName());
			
			RuntimeDeployment runtimeDeployment = deploymentComposite.createAndInsertDeployment(overview);
			cis.setDeployment(runtimeDeployment);
			
			return pprs;
		}
		else
		{
			// Private
			throw new IllegalStateException("Private not yet supported");
		}
		
	}

}
