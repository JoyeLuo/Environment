package eu.cloudscaleproject.env.overview.wizard.pages;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.IJobChangeEvent;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.core.runtime.jobs.JobChangeAdapter;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StackLayout;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.ProgressBar;
import org.eclipse.swt.widgets.Text;
import org.palladiosimulator.pcm.repository.Repository;
import org.palladiosimulator.pcm.system.System;
import org.scaledl.overview.Overview;
import org.scaledl.overview.application.ApplicationFactory;
import org.scaledl.overview.application.Operation;
import org.scaledl.overview.application.OperationInterface;
import org.scaledl.overview.architecture.ArchitectureFactory;
import org.scaledl.overview.architecture.CloudEnvironment;
import org.scaledl.overview.architecture.ExternalConnection;
import org.scaledl.overview.architecture.SoftwareService;
import org.scaledl.overview.architecture.UsageProxy;
import org.scaledl.overview.converter.ConverterService;
import org.scaledl.overview.converter.IOverviewConverter;
import org.scaledl.overview.parametertype.ParametertypeFactory;
import org.scaledl.overview.parametertype.PrimitiveParameter;
import org.scaledl.overview.parametertype.TypeEnum;
import org.scaledl.overview.util.OverviewUtil;

import eu.cloudscaleproject.env.common.IconSetResources;
import eu.cloudscaleproject.env.common.explorer.ExplorerProjectPaths;
import eu.cloudscaleproject.env.overview.wizard.util.IWizardPageControll;
import eu.cloudscaleproject.env.overview.wizard.util.WizardData;
import eu.cloudscaleproject.env.toolchain.CSToolResource;
import eu.cloudscaleproject.env.toolchain.ToolchainUtils;
import eu.cloudscaleproject.env.toolchain.resources.ResourceProvider;
import eu.cloudscaleproject.env.toolchain.resources.ResourceRegistry;
import eu.cloudscaleproject.env.toolchain.resources.types.EditorInputEMF;
import eu.cloudscaleproject.env.toolchain.resources.types.IEditorInputResource;

public class TransformationPage extends WizardPage implements IWizardPageControll
{
	private WizardData data;
	private Text txtRepositoryModelLoc;
	private Text txtSystemModelLoc;
	private org.eclipse.swt.widgets.List resultsList;
	private Button rbResults;
	private Button rbExternal;
	private Composite stackedComposite;
	private Group grpExternal;
	private Group grpResults;
	private Label lblSelectInput;
	private Composite transformComposite;
	private Composite transformResultComposite;
	private ProgressBar transformProgressBar;
	private Label transformInitLabel;
	private Composite stackedTransformComposite;
	private Label lblResultIcon;
	private Label lblResultText;
	private Job transformationJob;
	private Button btnTransform;

	private boolean useExternal = false;
	private String externalRepositoryModel;
	private String externalSystemModel;
	private EditorInputEMF result;

	/**
	 * Create the wizard.
	 */
	public TransformationPage(WizardData data)
	{
		super("selectModelPage");
		setTitle("Select PCM model");
		setDescription("System and repository PCM model selection.");

		setPageComplete(false);

		this.data = data;
	}

	/**
	 * Create contents of the wizard.
	 * 
	 * @param parent
	 */
	public void createControl(Composite parent)
	{
		Composite container = new Composite(parent, SWT.NULL);
		setControl(container);
		container.setLayout(new GridLayout(1, false));

		Composite grpSelectInput = new Composite(container, SWT.BORDER);
		grpSelectInput.setLayout(new GridLayout(3, false));
		GridData gd_grpSelectInput = new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1);
		gd_grpSelectInput.heightHint = 48;
		grpSelectInput.setLayoutData(gd_grpSelectInput);

		lblSelectInput = new Label(grpSelectInput, SWT.NONE);
		lblSelectInput.setText("Select input: ");

		rbResults = new Button(grpSelectInput, SWT.RADIO);
		rbResults.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, true, 1, 1));
		rbResults.setText("Extractor results");

		rbExternal = new Button(grpSelectInput, SWT.RADIO);
		rbExternal.setText("External models");

		stackedComposite = new Composite(container, SWT.NONE);
		stackedComposite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
		StackLayout sl_stackedComposite = new StackLayout();
		sl_stackedComposite.marginWidth = 6;
		sl_stackedComposite.marginHeight = 10;
		stackedComposite.setLayout(sl_stackedComposite);

		grpExternal = new Group(stackedComposite, SWT.NONE);
		grpExternal.setText("External models");
		grpExternal.setLayout(new GridLayout(3, false));

		Label label = new Label(grpExternal, SWT.NONE);
		label.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		label.setText("Repository model:");

		txtRepositoryModelLoc = new Text(grpExternal, SWT.BORDER);
		GridData gd_txtRepositoryModelLoc = new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1);
		gd_txtRepositoryModelLoc.widthHint = 250;
		txtRepositoryModelLoc.setLayoutData(gd_txtRepositoryModelLoc);

		Button btnSelectRepositoryModel = new Button(grpExternal, SWT.NONE);
		btnSelectRepositoryModel.setText("Browse...");

		Label label_1 = new Label(grpExternal, SWT.NONE);
		label_1.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		label_1.setText("System model:");

		txtSystemModelLoc = new Text(grpExternal, SWT.BORDER);
		GridData gd_txtSystemModelLoc = new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1);
		gd_txtSystemModelLoc.widthHint = 250;
		txtSystemModelLoc.setLayoutData(gd_txtSystemModelLoc);

		Button btnSelectSystemModel = new Button(grpExternal, SWT.NONE);
		btnSelectSystemModel.setText("Browse...");

		grpResults = new Group(stackedComposite, SWT.NONE);
		grpResults.setText("Extractor results");
		grpResults.setLayout(new GridLayout(1, false));

		resultsList = new org.eclipse.swt.widgets.List(grpResults, SWT.BORDER);
		resultsList.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));

		ResourceProvider resourceProvider = ResourceRegistry.getInstance()
				.getResourceProvider(this.data.getProject(), CSToolResource.EXTRACTOR_RES);

		final List<IEditorInputResource> results = resourceProvider.getResources();
		for (IEditorInputResource rpf : results)
		{
			resultsList.add(rpf.getName());
		}
		resultsList.addSelectionListener(new SelectionAdapter()
		{
			public void widgetSelected(SelectionEvent e)
			{
				if (resultsList.getSelection().length > 0)
					result = (EditorInputEMF) results.get(resultsList.getSelectionIndex());

				clearData();
			};
		});

		btnSelectSystemModel.addSelectionListener(new SelectionAdapter()
		{
			@Override
			public void widgetSelected(SelectionEvent e)
			{
				FileDialog fd = new FileDialog(getShell());
				fd.setFilterExtensions(new String[] { "*.system" });
				fd.open();
				String loc = fd.getFilterPath() + File.separator + fd.getFileName();

				txtSystemModelLoc.setText(loc);

				if (txtRepositoryModelLoc.getText().isEmpty())
				{
					String loc2 = fd.getFilterPath() + File.separator + fd.getFileName().replace(".system", ".repository");

					if (new File(loc2).exists())
						txtRepositoryModelLoc.setText(loc2);
				}
				java.lang.System.out.println(loc);

				externalRepositoryModel = txtRepositoryModelLoc.getText();
				externalSystemModel = txtSystemModelLoc.getText();
				clearData();

			}
		});

		btnSelectRepositoryModel.addSelectionListener(new SelectionAdapter()
		{
			@Override
			public void widgetSelected(SelectionEvent e)
			{
				FileDialog fd = new FileDialog(getShell());
				fd.setFilterExtensions(new String[] { "*.repository" });
				String res = fd.open();

				String loc = "";

				if (res != null)
				{
					loc = fd.getFilterPath() + File.separator + fd.getFileName();
				}

				txtRepositoryModelLoc.setText(loc);

				if (!loc.isEmpty() && txtSystemModelLoc.getText().isEmpty())
				{
					String loc2 = fd.getFilterPath() + File.separator + fd.getFileName().replace(".repository", ".system");

					if (new File(loc2).exists())
						txtSystemModelLoc.setText(loc2);
				}
				java.lang.System.out.println(loc);

				externalRepositoryModel = txtRepositoryModelLoc.getText();
				externalSystemModel = txtSystemModelLoc.getText();
				clearData();
			}
		});

		SelectionListener rbListener = new SelectionAdapter()
		{
			@Override
			public void widgetSelected(SelectionEvent e)
			{
				StackLayout sl = (StackLayout) stackedComposite.getLayout();

				if (rbExternal.getSelection())
				{
					sl.topControl = grpExternal;
					useExternal = true;
				} else
				{
					sl.topControl = grpResults;
					useExternal = false;
				}

				clearData();
				stackedComposite.layout();
			}
		};

		rbExternal.addSelectionListener(rbListener);
		rbResults.addSelectionListener(rbListener);

		rbResults.setSelection(true);
		StackLayout sl = (StackLayout) stackedComposite.getLayout();
		sl.topControl = grpResults;

		initTransformationComposite(container);
		stackedComposite.layout();
	}

	private void initTransformationComposite(Composite parent)
	{
		transformComposite = new Composite(parent, SWT.BORDER);
		transformComposite.setLayout(new GridLayout(2, false));
		GridData gd_transformComposite = new GridData(SWT.FILL, SWT.FILL, true, false, 1, 1);
		gd_transformComposite.heightHint = 48;
		transformComposite.setLayoutData(gd_transformComposite);

		stackedTransformComposite = new Composite(transformComposite, SWT.NONE);
		stackedTransformComposite.setLayout(new StackLayout());
		stackedTransformComposite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));

		transformInitLabel = new Label(stackedTransformComposite, SWT.NONE);
		transformProgressBar = new ProgressBar(stackedTransformComposite, SWT.INDETERMINATE);
		transformResultComposite = new Composite(stackedTransformComposite, SWT.NONE);

		{ // RESULT
			GridLayout gl_transformResultComposite = new GridLayout(2, false);
			gl_transformResultComposite.marginHeight = 0;
			transformResultComposite.setLayout(gl_transformResultComposite);

			lblResultIcon = new Label(transformResultComposite, SWT.NONE);
			lblResultIcon.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, true, 1, 1));
			lblResultIcon.setImage(IconSetResources.getImage(IconSetResources.THUMB_UP));

			lblResultText = new Label(transformResultComposite, SWT.NONE);
			lblResultText.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, true, 1, 1));

			btnTransform = new Button(transformComposite, SWT.NONE);
			btnTransform.setLayoutData(new GridData(SWT.LEFT, SWT.FILL, false, false, 1, 1));
			btnTransform.setBounds(0, 0, 85, 27);
			btnTransform.setText("Transform");

			btnTransform.addSelectionListener(new SelectionAdapter()
			{
				@Override
				public void widgetSelected(SelectionEvent e)
				{
					btnTransform.setEnabled(false);
					transform();
				}
			});
		}

		updateTransformationPanel();
	}

	@Override
	public void performUpdate()
	{
	}

	public void performBack()
	{
	}

	public void performNext()
	{
	}

	private void clearData()
	{
		this.data.setRepositoryModel(null);
		this.data.setSystemModel(null);
		this.data.setOverviewModel(null);
		transformationJob = null;
		checkComplete();
	}

	private void checkComplete()
	{
		Display.getDefault().asyncExec(new Runnable()
		{

			@Override
			public void run()
			{
				updateTransformationPanel();

				if (data.getOverviewModel() != null)
					setPageComplete(true);
				else
					setPageComplete(false);
			}
		});
	}

	// ////////////////////////////////////////////////////////////////////////////////////////
	//
	// Transformation
	//
	private boolean canTransform()
	{
		if (useExternal)
			return (externalRepositoryModel != null && externalSystemModel != null);
		else
			return result != null;
	}

	private boolean isTransformationRunning()
	{
		return transformationJob != null && transformationJob.getResult() == null;
	}

	private void updateTransformationPanel()
	{
		Display.getDefault().asyncExec(new Runnable()
		{
			@Override
			public void run()
			{
				if (transformationJob == null)
				{
					((StackLayout) stackedTransformComposite.getLayout()).topControl = transformInitLabel;
				} else
				{
					if (isTransformationRunning())
					{
						((StackLayout) stackedTransformComposite.getLayout()).topControl = transformProgressBar;
					} else
					{
						((StackLayout) stackedTransformComposite.getLayout()).topControl = transformResultComposite;

						if (transformationJob.getResult().isOK())
						{
							lblResultText.setText("Successfully transformed to Overview.");
							lblResultIcon.setImage(IconSetResources.getImage(IconSetResources.THUMB_UP));
						} else
						{
							lblResultText.setText("Failed to transform to Overview.");
							lblResultIcon.setImage(IconSetResources.getImage(IconSetResources.ERROR));
						}
					}
				}

				if (canTransform())
				{
					btnTransform.setEnabled(!isTransformationRunning());
				} else
				{
					btnTransform.setEnabled(false);
				}

				stackedTransformComposite.layout(true);
			}
		});
	}

	private void transform()
	{
		if (isTransformationRunning())
			return;

		transformationJob = new Job("")
		{
			@Override
			protected IStatus run(IProgressMonitor monitor)
			{
				updateTransformationPanel();

				// Make it last longer - just to show progress normally
				try
				{
					Thread.sleep(500);
				} catch (Exception e)
				{
				}
				
				doTransform();

				//
				// WORKAROUND : First transformation sometimes fails in first
				// try (QVTo transf. bug)
				//
				if (data.getSoftwareService().getProvidedInterfaces().size() == 1)
				{
					if (data.getSoftwareService().getProvidedInterfaces().get(0).getName().equals("aName"))
					{
						Logger.getLogger(TransformationPage.class.getName()).warning("Applying WORKAROUND: re-run transformation...");
						doTransform();
					}
				}

				return Status.OK_STATUS;
			}
		};

		transformationJob.addJobChangeListener(new JobChangeAdapter()
		{
			@Override
			public void done(IJobChangeEvent event)
			{
				updateTransformationPanel();
				checkComplete();
			}
		});

		transformationJob.schedule();
	}

	/*************************************************************************************
	 * 
	 * Models and transfromation
	 * 
	 */

	private void doTransform()
	{
		if (useExternal)
			initExternalModel();
		else
			initResultsModel();

		prepareOverviewModel();

		//
		// Import - Transformation
		//
		List<EObject> toImport = new ArrayList<EObject>();
		toImport.add(data.getRepositoryModel());
		toImport.add(data.getSystemModel());

		ConverterService.getInstance().addExternalModel(data.getSoftwareService(), toImport, null);
	}

	private void initExternalModel()
	{
		final URI repositoryURI = URI.createFileURI(externalRepositoryModel);
		final URI systemURI = URI.createFileURI(externalSystemModel);

		ResourceSet resSet = new ResourceSetImpl();
		final Resource systemRes = resSet.createResource(systemURI);
		final Resource repositoryRes = resSet.createResource(repositoryURI);

		repositoryRes.unload();
		systemRes.unload();
		try
		{
			repositoryRes.load(null);
			systemRes.load(null);
		} catch (IOException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		EObject sys = systemRes.getContents().get(0);
		EObject rep = repositoryRes.getContents().get(0);

		this.data.setRepositoryModel((Repository) rep);
		this.data.setSystemModel((System) sys);
	}

	private void initResultsModel()
	{
		IFile repositoryFile = (IFile) this.result.getSubResource(ToolchainUtils.KEY_FILE_REPOSITORY);
		IFile systemFile = (IFile) this.result.getSubResource(ToolchainUtils.KEY_FILE_SYSTEM);

		Resource repositoryResource = (ExplorerProjectPaths.getEmfResource(result.getResourceSet(), repositoryFile));
		Resource systemResource = (ExplorerProjectPaths.getEmfResource(result.getResourceSet(), systemFile));

		EObject rep = repositoryResource.getContents().get(0);
		EObject sys = systemResource.getContents().get(0);

		EcoreUtil.resolve(rep, repositoryResource);
		EcoreUtil.resolve(sys, systemResource);

		this.data.setRepositoryModel((Repository) rep);
		this.data.setSystemModel((System) sys);
	}

	private void prepareOverviewModel()
	{
		Overview overview = OverviewUtil.createOverviewModel();

		//
		// CloudEnvironment
		//
		CloudEnvironment cloudEnvironment = ArchitectureFactory.eINSTANCE.createCloudEnvironment();
		cloudEnvironment.setSoftwareLayer(ArchitectureFactory.eINSTANCE.createSoftwareLayer());
		cloudEnvironment.setPlatformLayer(ArchitectureFactory.eINSTANCE.createPlatformLayer());
		cloudEnvironment.setInfrastructureLayer(ArchitectureFactory.eINSTANCE.createInfrastructureLayer());
		overview.getArchitecture().getCloudEnvironments().add(cloudEnvironment);

		this.data.setOverviewModel(overview);

		//
		// Software service
		//
		SoftwareService ss = ArchitectureFactory.eINSTANCE.createSoftwareService();
		ss.getAeMap().put(IOverviewConverter.KEY_PCM_SYSTEM, data.getSystemModel());
		ss.getAeMap().put(IOverviewConverter.KEY_PCM_REPOSITORY, data.getRepositoryModel());
		CloudEnvironment ce = overview.getArchitecture().getCloudEnvironments().get(0);
		ce.getSoftwareLayer().getServices().add(ss);

		this.data.setSoftwareService(ss);

		//
		// Usage Proxy
		//
		UsageProxy usageProxy = ArchitectureFactory.eINSTANCE.createUsageProxy();
		overview.getArchitecture().getProxies().add(usageProxy);
		ExternalConnection connection = ArchitectureFactory.eINSTANCE.createExternalConnection();
		connection.setTarget(ss);
		connection.setSource(usageProxy);
		overview.getArchitecture().getUsageConnections().add(connection);

	}

	@SuppressWarnings("unused")
	private void addFakeIneterfaces(SoftwareService ss)
	{
		if (ss.getRequiredInterfaces().isEmpty())
		{
			OperationInterface oi = ApplicationFactory.eINSTANCE.createOperationInterface();
			oi.setName("I_Database");

			oi.getOperations().add(createFakeOperation("getBook", TypeEnum.STRING, TypeEnum.INT));
			oi.getOperations().add(createFakeOperation("getUser", TypeEnum.STRING, TypeEnum.INT));
			oi.getOperations().add(createFakeOperation("getChart", TypeEnum.STRING, TypeEnum.INT));
			oi.getOperations().add(createFakeOperation("doCheckout", TypeEnum.BOOL, TypeEnum.INT, TypeEnum.STRING, TypeEnum.STRING));

			ss.getRequiredInterfaces().add(oi);
		}
	}

	private Operation createFakeOperation(String name, TypeEnum ret, TypeEnum... par)
	{
		Operation o = ApplicationFactory.eINSTANCE.createOperation();
		o.setName(name);
		for (TypeEnum tt : par)
		{
			PrimitiveParameter pp = ParametertypeFactory.eINSTANCE.createPrimitiveParameter();
			pp.setType(tt);
			o.getParameters().add(pp);
		}

		PrimitiveParameter pp = ParametertypeFactory.eINSTANCE.createPrimitiveParameter();
		pp.setType(ret);
		o.setReturnParameter(pp);

		return o;
	}
}
