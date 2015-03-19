package eu.cloudscaleproject.env.extractor.editors.composites;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.databinding.DataBindingContext;
import org.eclipse.core.databinding.beans.BeanProperties;
import org.eclipse.core.databinding.beans.PojoObservables;
import org.eclipse.core.databinding.observable.list.IObservableList;
import org.eclipse.core.databinding.observable.map.IObservableMap;
import org.eclipse.core.databinding.observable.value.IObservableValue;
import org.eclipse.core.databinding.property.Properties;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IProjectNature;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.jface.databinding.viewers.ObservableListContentProvider;
import org.eclipse.jface.databinding.viewers.ObservableMapLabelProvider;
import org.eclipse.jface.databinding.viewers.ViewerProperties;
import org.eclipse.jface.viewers.ComboViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CTabFolder;
import org.eclipse.swt.custom.CTabItem;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.somox.common.MetricsDetails.GroupID;

import eu.cloudscaleproject.env.extractor.ConfigPersistenceFolder;
import eu.cloudscaleproject.env.extractor.wizard.util.ExtractorRunJob;
import eu.cloudscaleproject.env.toolchain.ui.RunComposite;

public class ConfigAlternativeComposite extends RunComposite
{
	private DataBindingContext m_bindingContext;
	private ConfigPersistenceFolder configPersistenceFolder;

	/**
	 * Create the composite.
	 * 
	 * @param parent
	 * @param style
	 */
	public ConfigAlternativeComposite(Composite parent, int style, ConfigPersistenceFolder cif)
	{
		super(parent, style);

		this.configPersistenceFolder = cif;

		// this.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1,
		// 1));
		getContainer().setLayout(new GridLayout(2, false));

		setTitle(configPersistenceFolder.getName());

		{
		Label lblNewLabel = new Label(getContainer(), SWT.NONE);
		lblNewLabel.setText("Input project");

		comboViewer = new ComboViewer(getContainer(), SWT.NONE);
		Combo combo = comboViewer.getCombo();
		GridData gd_combo = new GridData(SWT.LEFT, SWT.CENTER, false, false);
		gd_combo.widthHint = 250;
		combo.setLayoutData(gd_combo);
		}

		Composite containerConfiguration = new Composite(getContainer(), SWT.NONE);
		//containerConfiguration.setLayout(new FillLayout(SWT.HORIZONTAL));
		containerConfiguration.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 3, 1));
		containerConfiguration.setLayout(new GridLayout(1, false));

		//new SomoxConfigurationComposite(configPersistenceFolder.getSomoxConfiguration(), containerConfiguration, SWT.NONE);

		CTabFolder tabFolder = new CTabFolder(containerConfiguration, SWT.BORDER);
		tabFolder.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
		tabFolder.setTabHeight(32);
		tabFolder.setSelectionBackground(Display.getCurrent().getSystemColor(SWT.COLOR_TITLE_INACTIVE_BACKGROUND_GRADIENT));

		{
			SomoxConfigurationComposite composite = new SomoxConfigurationComposite(tabFolder, SWT.NONE, 
					configPersistenceFolder, GroupID.GROUP_CLUSTERING, GroupID.GROUP_MERGING);

			CTabItem tabItem = new CTabItem(tabFolder, SWT.NONE);
			tabItem.setControl(composite);
			tabItem.setText("Clustering / Merging");
			tabFolder.setSelection(tabItem);
		}
		{
			SomoxConfigurationComposite composite = new SomoxConfigurationComposite(tabFolder, SWT.NONE, 
					configPersistenceFolder, GroupID.GROUP_METRICS);

			CTabItem tabItem = new CTabItem(tabFolder, SWT.NONE);
			tabItem.setControl(composite);
			tabItem.setText("Other metrics");
			tabFolder.setSelection(tabItem);
		}
		{
			ModiscoConfigurationComposite composite = new ModiscoConfigurationComposite(tabFolder, SWT.NONE, configPersistenceFolder);
			CTabItem tabItem = new CTabItem(tabFolder, SWT.NONE);
			tabItem.setControl(composite);
			tabItem.setText("Modisco configuration");
			tabFolder.setSelection(tabItem);
		}
		
		tabFolder.setSelection(0);

		m_bindingContext = initDataBindings();
	}


	private static String JAVA_NATURE_ID = "org.eclipse.jdt.core.javanature";
	private ComboViewer comboViewer;

	private List<IProject> getJavaProjects()
	{
		List<IProject> javaProjects = new ArrayList<>();
		IWorkspaceRoot root = ResourcesPlugin.getWorkspace().getRoot();

		for (IProject project : root.getProjects())
		{
			IProjectNature nature;
			try
			{
				nature = project.getNature(JAVA_NATURE_ID);
				if (nature != null)
					javaProjects.add(project);
			} catch (CoreException e)
			{
				System.out.println("Unable to retrieve nature: " + e.getMessage());
			}
		}

		return javaProjects;
	}

	@Override
	public void update()
	{
		this.configPersistenceFolder.load();
		m_bindingContext.updateTargets();

		super.update();
	}

	@Override
	protected IStatus doRun(IProgressMonitor m)
	{
		ExtractorRunJob job = new ExtractorRunJob(this.configPersistenceFolder);
		return job.run(m);
	}

	@Override
	protected void checkSubclass()
	{
		// Disable the check that prevents subclassing of SWT components
	}

	protected DataBindingContext initDataBindings()
	{
		DataBindingContext bindingContext = new DataBindingContext();
		//
		ObservableListContentProvider listContentProvider = new ObservableListContentProvider();
		IObservableMap observeMap = PojoObservables.observeMap(listContentProvider.getKnownElements(), IProject.class, "name");
		comboViewer.setLabelProvider(new ObservableMapLabelProvider(observeMap));
		comboViewer.setContentProvider(listContentProvider);
		//
		IObservableList selfList = Properties.selfList(IProject.class).observe(getJavaProjects());
		comboViewer.setInput(selfList);
		//
		IObservableValue observeSingleSelectionComboViewer_1 = ViewerProperties.singleSelection().observe(comboViewer);
		IObservableValue projectConfigPersistenceFolderObserveValue = BeanProperties.value("extractedProject").observe(
				configPersistenceFolder);
		bindingContext.bindValue(observeSingleSelectionComboViewer_1, projectConfigPersistenceFolderObserveValue, null, null);
		//
		return bindingContext;
	}
}
