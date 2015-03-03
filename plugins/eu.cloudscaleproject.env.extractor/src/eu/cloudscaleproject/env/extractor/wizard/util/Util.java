package eu.cloudscaleproject.env.extractor.wizard.util;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.debug.core.DebugPlugin;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.ILaunchConfigurationWorkingCopy;
import org.eclipse.debug.core.ILaunchManager;
import org.eclipse.debug.ui.DebugUITools;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.gmf.runtime.diagram.core.services.ViewService;
import org.eclipse.gmf.runtime.notation.Diagram;
import org.eclipse.ui.PlatformUI;
import org.somox.analyzer.simplemodelanalyzer.jobs.SaveSoMoXModelsJob;
import org.somox.analyzer.simplemodelanalyzer.jobs.SimpleModelAnalyzerJob;
import org.somox.analyzer.simplemodelanalyzer.jobs.SoMoXBlackboard;
import org.somox.configuration.SoMoXConfiguration;
import org.somox.ui.runconfig.ModelAnalyzerConfiguration;

import com.ibm.icu.text.SimpleDateFormat;

import de.uka.ipd.sdq.pcm.gmf.composite.edit.parts.ComposedProvidingRequiringEntityEditPart;
import de.uka.ipd.sdq.pcm.gmf.composite.part.PalladioComponentModelComposedStructureDiagramEditorPlugin;
import de.uka.ipd.sdq.pcm.gmf.repository.edit.parts.RepositoryEditPart;
import de.uka.ipd.sdq.pcm.gmf.repository.part.PalladioComponentModelRepositoryDiagramEditorPlugin;
import eu.cloudscaleproject.env.common.explorer.ExplorerProjectPaths;
import eu.cloudscaleproject.env.extractor.ConfigPersistenceFolder;
import eu.cloudscaleproject.env.extractor.InputPersitenceFile;
import eu.cloudscaleproject.env.extractor.ResultPersistenceFolder;
import eu.cloudscaleproject.env.toolchain.ToolchainUtils;
import eu.cloudscaleproject.env.toolchain.resources.ResourceProvider;
import eu.cloudscaleproject.env.toolchain.resources.ResourceRegistry;
import eu.cloudscaleproject.env.toolchain.resources.types.IEditorInputResource;

public class Util
{

	// //////////////////////////////////
	// Input/Config/Run Alternatives
	//

	public static IFolder getInputFolder(IProject project)
	{
		IFolder extractorFolder = ExplorerProjectPaths.getProjectFolder(project, ExplorerProjectPaths.KEY_FOLDER_EXTRACTOR);
		IFolder extractorInputFolder = extractorFolder.getFolder(ExplorerProjectPaths.getProjectProperty(project,
				ExplorerProjectPaths.KEY_FOLDER_INPUT));

		return extractorInputFolder;
	}

	public static List<InputPersitenceFile> getInputAlternatives(IProject project)
	{
		List<InputPersitenceFile> out = new ArrayList<InputPersitenceFile>();

		IFolder extractorInputFolder = getInputFolder(project);

		try
		{
			for (IResource res : extractorInputFolder.members())
			{
				if (res instanceof IFile)
				{
					InputPersitenceFile ipf = new InputPersitenceFile(project, (IFile) res);
					out.add(ipf);
				}
			}
		}
		catch (CoreException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return out;
	}

	public static IFolder getConfigurationFolder(IProject project)
	{
		IFolder extractorFolder = ExplorerProjectPaths.getProjectFolder(
				project, ExplorerProjectPaths.KEY_FOLDER_EXTRACTOR);
		String configurationFolderString = ExplorerProjectPaths
				.getProjectProperty(project,
						ExplorerProjectPaths.KEY_FOLDER_CONFIGURATION);
		IFolder configurationFolder = extractorFolder
				.getFolder(configurationFolderString);

		return configurationFolder;
	}

	public static List<ConfigPersistenceFolder> getConfigurations(IProject project)
	{
		List<ConfigPersistenceFolder> out = new ArrayList<ConfigPersistenceFolder>();

		IFolder extractorConfigurationFolder = Util.getConfigurationFolder(project);

		try
		{
			for (IResource res : extractorConfigurationFolder.members())
			{
				if (res instanceof IFolder)
				{
					ConfigPersistenceFolder cpf = new ConfigPersistenceFolder(project, (IFolder) res);
					out.add(cpf);
				}
			}
		}
		catch (CoreException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return out;
	}

	public static IFolder getResultsFolder(IProject project)
	{
		IFolder extractorFolder = ExplorerProjectPaths.getProjectFolder(
				project, ExplorerProjectPaths.KEY_FOLDER_EXTRACTOR);
		String resultsFolderString = ExplorerProjectPaths
				.getProjectProperty(project,
						ExplorerProjectPaths.KEY_FOLDER_RESULTS);
		IFolder resultsFolder = extractorFolder
				.getFolder(resultsFolderString);

		return resultsFolder;
	}

	public static List<ResultPersistenceFolder> getResults(IProject project)
	{
		List<ResultPersistenceFolder> out = new ArrayList<ResultPersistenceFolder>();

		IFolder extractorInputFolder = Util.getResultsFolder(project);

		try
		{
			for (IResource res : extractorInputFolder.members())
			{
				if (res instanceof IFolder)
				{
					ResultPersistenceFolder ipf = new ResultPersistenceFolder(project, (IFolder) res);
					out.add(ipf);
				}
			}
		}
		catch (CoreException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return out;
	}

	// ////////////////////////////////////////////////////////////////////////////////////////////////
	// Other stuff / running, viewing, ...
	//

	public static void runConfigurationAlternative(ConfigPersistenceFolder cif)
	{
		new ExtractorRunJob(cif).schedule(100);
	}

	public static ILaunchConfiguration getLaunchConfiguration(IFile configFile)
	{
		ILaunchManager mgr = DebugPlugin.getDefault().getLaunchManager();
		ILaunchConfiguration launchConf = mgr
				.getLaunchConfiguration(configFile);

		return launchConf;
	}

	public static void showConfigurationFile(IFile configFile)
	{
		ILaunchConfiguration launchConf = getLaunchConfiguration(configFile);

		DebugUITools.openLaunchConfigurationDialog(
				PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(),
				launchConf, DebugUITools.getLaunchGroup(launchConf, "run").getIdentifier(), null
				);
	}

	private static class ExtractorRunJob extends Job
	{
		private IProject project;
		private IFile modiscoConfig;
		private IFile somoxConfig;
		private ConfigPersistenceFolder configInputFolder;
		private IProject projectToExtract;

		public ExtractorRunJob(ConfigPersistenceFolder configInputFolder)
		{
			super("Extractor run");

			this.configInputFolder = configInputFolder;
			this.project = configInputFolder.getProject();
			this.modiscoConfig = configInputFolder.getModiscoConfigFile();
			this.somoxConfig = configInputFolder.getSomoxConfigFile();

			this.projectToExtract = getProjectToExtract();

			setUser(true);
		}

		private IProject getProjectToExtract()
		{
			ResourceProvider resourceProvider = ResourceRegistry.getInstance().getResourceProvider(configInputFolder.getProject(),
					ToolchainUtils.EXTRACTOR_INPUT_ID);

			String input = configInputFolder.getProperty(ConfigPersistenceFolder.KEY_INPUT_ALTERNATIVE);
			IEditorInputResource resource = resourceProvider.getResourceByName(input);
			String projectToExtractURL = resource.getProperty(InputPersitenceFile.KEY_PROJECT_URL);
			IProject projectToExtract = ResourcesPlugin.getWorkspace().getRoot().getProject(projectToExtractURL);
			return projectToExtract;
		}

		@Override
		protected IStatus run(IProgressMonitor monitor)
		{

			// Infinite progress: -1
			monitor.beginTask("Extracting PCM from source code.", -1);

			try
			{
				runModisco();
				runSomox(monitor);

				copyFilesToResultFolder(projectToExtract);

			}
			catch (CoreException e)
			{
				e.printStackTrace();
				return new Status(Status.ERROR, "", "Extraction failed...");
			}

			monitor.done();

			return Status.OK_STATUS;
		}

		private void runModisco() throws CoreException
		{
			ILaunchConfiguration modisco = getLaunchConfiguration(modiscoConfig);

			String modiscoKey = "discoverer_launch_model";
			String modiscoResourceKey = "org.eclipse.core.resources.IResource";
			String modiscoResourceRegex = modiscoResourceKey + ":/\\w*";

			// This is XML
			// For now we find resource property with regex and replace with
			// selected value
			String value = modisco.getAttribute(modiscoKey, "");
			value = value.replaceAll(modiscoResourceRegex, modiscoResourceKey + ":/" + getProjectToExtract().getName());

			// Save property
			ILaunchConfigurationWorkingCopy workingCopy = modisco.getWorkingCopy();
			workingCopy.setAttribute(modiscoKey, value);
			workingCopy.launch(ILaunchManager.DEBUG_MODE, null);
		}

		private void runSomox(IProgressMonitor monitor)
		{
			SoMoXConfiguration somoxConfiguration = SomoxConfigurationUtil.createDefaultSomoxConfiguration();

			somoxConfiguration.getFileLocations().setProjectName(projectToExtract.getName());
			somoxConfiguration.getFileLocations().setAnalyserInputFile(
					"/" + projectToExtract.getName() + "/" + projectToExtract.getName() + "_java2kdm.xmi");
			somoxConfiguration.getFileLocations().setOutputFolder("/model");

			try
			{
				ModelAnalyzerConfiguration conf = new ModelAnalyzerConfiguration();
				conf.setSomoxConfiguration(somoxConfiguration);
				SimpleModelAnalyzerJob job = new SimpleModelAnalyzerJob(conf);
				SoMoXBlackboard blackboard = new SoMoXBlackboard();
				job.setBlackboard(blackboard);

				job.execute(monitor);

				SaveSoMoXModelsJob saveJob = new SaveSoMoXModelsJob(somoxConfiguration);
				saveJob.setBlackboard(blackboard);

				saveJob.execute(monitor);
			}
			catch (Exception e1)
			{
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}

		}

		private SimpleDateFormat sdf_name = new SimpleDateFormat("hh:mm:ss");

		private IFolder createResultFolder() throws CoreException
		{
			IFolder resultsFolder = getResultsFolder(project);
			IFolder resultFolder = resultsFolder.getFolder(configInputFolder.getName() + " [" + sdf_name.format(new Date()) + "]");
			resultFolder.create(true, false, null);

			return resultFolder;
		}

		private void copyFilesToResultFolder(IProject outputProject)
		{
			try
			{
				IFolder resultFolder = createResultFolder();
				ResultPersistenceFolder rif = new ResultPersistenceFolder(outputProject, resultFolder);
				rif.setName(rif.getName());
				rif.create();

				IPath projectPath = new Path("/" + project.getName());

				//
				// Modisco output
				//
				IFolder modiscoFolder = (IFolder) rif.getSubResource(ResultPersistenceFolder.KEY_MODISCO_FOLDER);

				// Java2kdmFraments folder
				String fragments = "java2kdmFragments";
				IFolder srcFragFolder = outputProject.getFolder(fragments);
				IFolder destFragFolder = modiscoFolder.getFolder(fragments);
				srcFragFolder.copy(projectPath.append(destFragFolder.getProjectRelativePath()), true, null);
				srcFragFolder.delete(true, null);

				// XMI files
				String prefix = outputProject.getName();
				String kdm = prefix + "_kdm.xmi";
				String java = prefix + "_java.xmi";
				String java2kdm = prefix + "_java2kdm.xmi";

				for (String fileName : new String[] { kdm, java, java2kdm })
				{
					IFile file = outputProject.getFile(fileName);
					IFile dest = modiscoFolder.getFile(fileName);
					file.copy(projectPath.append(dest.getProjectRelativePath()), true, null);
					file.delete(true, null);
				}

				//
				// Somox output
				//
				IFolder somoxFolder = (IFolder) rif.getSubResource(ResultPersistenceFolder.KEY_SOMOX_FOLDER);

				IFolder modelFolder = outputProject.getFolder("model");

				for (IResource r : modelFolder.members())
				{
					if (r instanceof IFile)
					{
						IFile srcFile = (IFile) r;
						IFile dstFile = somoxFolder.getFile(srcFile.getName());
						srcFile.copy(projectPath.append(dstFile.getProjectRelativePath()), true, null);
					}
				}

				modelFolder.delete(true, null);

				initializeDiagrams(somoxFolder);

				IFile repositoryModelFile = somoxFolder.getFile(ResultPersistenceFolder.RESULT_REPOSITORY);
				IFile systemModelFile = somoxFolder.getFile(ResultPersistenceFolder.RESULT_SYSTEM);
				IFile repositoryDiagramFile = somoxFolder.getFile(ResultPersistenceFolder.RESULT_REPOSITORY_DIAGRAM);
				IFile systemDiagramFile = somoxFolder.getFile(ResultPersistenceFolder.RESULT_SYSTEM_DIAGRAM);

				rif.setSubResource(ResultPersistenceFolder.KEY_REPOSITORY_MODEL, repositoryModelFile);
				rif.setSubResource(ResultPersistenceFolder.KEY_SYSTEM_MODEL, systemModelFile);
				rif.setSubResource(ResultPersistenceFolder.KEY_REPOSITORY_DIAGRAM, repositoryDiagramFile);
				rif.setSubResource(ResultPersistenceFolder.KEY_SYSTEM_DIAGRAM, systemDiagramFile);

				IFile sourceDecoratorFile = somoxFolder.getFile(ResultPersistenceFolder.RESULT_SOURCEDECORATOR);
				rif.setSubResource(ResultPersistenceFolder.KEY_SOURCEDECORATOR_MODEL, sourceDecoratorFile);

				rif.save();
				// ViewService.createDiagram(
				// model,
				// RepositoryEditPart.MODEL_ID,
				// PalladioComponentModelRepositoryDiagramEditorPlugin.DIAGRAM_PREFERENCES_HINT);

			}
			catch (Exception e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		private void initializeDiagrams(IFolder somoxFolder)
		{
			IFile repositoryFile = somoxFolder.getFile(ResultPersistenceFolder.RESULT_REPOSITORY);
			IFile systemFile = somoxFolder.getFile(ResultPersistenceFolder.RESULT_SYSTEM);

			final URI resourceURI = URI.createPlatformResourceURI(repositoryFile.getFullPath().toString(), true);
			final URI systemURI = URI.createPlatformResourceURI(systemFile.getFullPath().toString(), true);

			ResourceSet resSet = new ResourceSetImpl();
			final Resource systemRes = resSet.createResource(systemURI);
			final Resource repositoryRes = resSet.createResource(resourceURI);

			IFile repositoryDiagramFile = somoxFolder.getFile(ResultPersistenceFolder.RESULT_REPOSITORY_DIAGRAM);
			IFile systemDiagramFile = somoxFolder.getFile(ResultPersistenceFolder.RESULT_SYSTEM_DIAGRAM);

			final URI resourceDiagramURI = URI.createPlatformResourceURI(repositoryDiagramFile.getFullPath().toString(), true);
			final URI systemDiagramURI = URI.createPlatformResourceURI(systemDiagramFile.getFullPath().toString(), true);

			Resource repositoryDiagramResource = resSet.createResource(resourceDiagramURI);
			Resource systemDiagramResource = resSet.createResource(systemDiagramURI);

			try
			{
				systemRes.load(null);
				repositoryRes.load(null);
				// repositoryDiagramResource.load(null);
				// systemDiagramResource.load(null);

				Diagram repositoryDiagram = ViewService.createDiagram(
						repositoryRes.getContents().get(0),
						RepositoryEditPart.MODEL_ID,
						PalladioComponentModelRepositoryDiagramEditorPlugin.DIAGRAM_PREFERENCES_HINT);
				repositoryDiagramResource.getContents().add(repositoryDiagram);
				repositoryDiagramResource.save(null);

				Diagram systemDiagram = ViewService.createDiagram(
						systemRes.getContents().get(0),
						ComposedProvidingRequiringEntityEditPart.MODEL_ID,
						PalladioComponentModelComposedStructureDiagramEditorPlugin.DIAGRAM_PREFERENCES_HINT);

				systemDiagramResource.getContents().add(systemDiagram);
				systemDiagramResource.save(null);
			}
			catch (Exception e)
			{
				e.printStackTrace();
			}

			repositoryRes.unload();
			repositoryDiagramResource.unload();
			systemRes.unload();
			systemDiagramResource.unload();

		}
	}

}
