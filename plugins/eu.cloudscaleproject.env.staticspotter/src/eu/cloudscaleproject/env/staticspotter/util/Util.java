package eu.cloudscaleproject.env.staticspotter.util;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.emf.ecore.xmi.XMLResource;
import org.eclipse.emf.ecore.xmi.impl.XMIResourceImpl;
import org.eclipse.emf.ecore.xmi.impl.XMLParserPoolImpl;
import org.fujaba.commons.console.ReportLevel;
import org.reclipse.structure.inference.DetectPatternsJob;
import org.reclipse.structure.inference.annotations.ASGAnnotation;
import org.reclipse.structure.inference.annotations.AnnotationsPackage;
import org.reclipse.structure.inference.annotations.SetInstanceAnnotation;
import org.reclipse.structure.inference.annotations.TemporaryAnnotation;
import org.reclipse.structure.inference.util.InferenceExtensionsHelper;
import org.reclipse.structure.inference.util.InferenceExtensionsHelper.AnnotationEvaluatorItem;
import org.reclipse.structure.specification.PSPatternSpecification;

import eu.cloudscaleproject.env.common.explorer.ExplorerProjectPaths;
import eu.cloudscaleproject.env.staticspotter.alternatives.ConfigAlternative;
import eu.cloudscaleproject.env.staticspotter.alternatives.ResultAlternative;
import eu.cloudscaleproject.env.toolchain.ToolchainUtils;
import eu.cloudscaleproject.env.toolchain.resources.ResourceProvider;
import eu.cloudscaleproject.env.toolchain.resources.ResourceRegistry;
import eu.cloudscaleproject.env.toolchain.resources.types.EditorInputFolder;

public class Util
{

	public static DetectPatternsJob createDetectPaternJob(EditorInputFolder configFolder, EditorInputFolder extractorResult)
	{
		assert (extractorResult != null);

		ResourceSet resSet = new ResourceSetImpl();
		IFile catalogFile = (IFile)configFolder.getSubResource(ConfigAlternative.KEY_CATALOG);
		URI catalogURI = URI.createPlatformResourceURI(catalogFile.getFullPath().toString(), true);

		IFile enginesFile = (IFile)configFolder.getSubResource(ConfigAlternative.KEY_ENGINES);
		URI enginesURI = URI.createPlatformResourceURI(enginesFile.getFullPath().toString(), true);

		IFile sdFile = (IFile)extractorResult.getSubResource(ToolchainUtils.KEY_FILE_SOURCEDECORATOR);
		URI sdURI = URI.createPlatformResourceURI(sdFile.getFullPath().toString(), true);

		//
		// Run resources
		//
		Resource catalogResource = resSet.createResource(catalogURI);
		Resource enginesResource = resSet.createResource(enginesURI);
		Resource sourcedecoratorResource = resSet.createResource(sdURI);

		//
		// Configurations
		//
		ReportLevel reportLevel = ReportLevel.MINIMAL;
		AnnotationEvaluatorItem evaluator = InferenceExtensionsHelper.getRegisteredEvaluators().get(0);
		boolean searchForAdditionalElements = false;

		DetectPatternsJob job = new DetectPatternsJob(catalogResource, enginesResource, sourcedecoratorResource, reportLevel);
		job.setAnnotateAdditionalElements(searchForAdditionalElements);
		job.setEvaluator(evaluator.getEvaluator());

		return job;
	}

	public static IFolder getResultsFolder(IProject project)
	{
		IFolder staticSpotterFolder = ExplorerProjectPaths.getProjectFolder(
				project, ExplorerProjectPaths.KEY_FOLDER_STATIC_SPOTTER);
		String resultsFolderString = ExplorerProjectPaths
				.getProjectProperty(project, ExplorerProjectPaths.KEY_FOLDER_RESULTS);
		IFolder resultsFolder = staticSpotterFolder.getFolder(resultsFolderString);

		return resultsFolder;
	}

	//
	// Saving annotations
	//
	public static void saveAnnotations(EditorInputFolder configFolder, DetectPatternsJob job)
	{
		ResourceProvider resultResProvider = ResourceRegistry.getInstance().
				getResourceProvider(configFolder.getProject(), ToolchainUtils.SPOTTER_STA_RES_ID);

		ResultAlternative rif = (ResultAlternative)resultResProvider
				.createNewResource(configFolder.getName() + " " + sdf.format(new Date()), "");

		//IFolder resultFolder = createResultFolder(configFolder.getProject(), configFolder.getName());
		//ResultPersistenceFolder rif = new ResultPersistenceFolder(configFolder.getProject(), resultFolder);
		//rif.setName(resultFolder.getName());

		List<ASGAnnotation> annotations = getAnnotations(job);

		IFile file = rif.getResource().getFile(ResultAlternative.RESULT_PSA_FILE);
		URI uri = URI.createPlatformResourceURI(file.getFullPath().toString(), true);

		ResourceSet ress = new ResourceSetImpl();
		Resource res = ress.createResource(uri);

		// fill resource
		for (ASGAnnotation anno : annotations)
		{
			// rename annotation type (due to usage of specific created
			// annotation classes)
			// TODO: this is evil!
			String name = AnnotationsPackage.eINSTANCE.getASGAnnotation().getName();
			if (anno instanceof SetInstanceAnnotation)
			{
				name = AnnotationsPackage.eINSTANCE.getSetInstanceAnnotation().getName();
			}
			else if (anno instanceof TemporaryAnnotation)
			{
				name = AnnotationsPackage.eINSTANCE.getTemporaryAnnotation().getName();
			}

			anno.eClass().setName(name);
			anno.eClass().getEPackage().setName(AnnotationsPackage.eNAME);
			anno.eClass().getEPackage().setNsPrefix(AnnotationsPackage.eNS_PREFIX);
			anno.eClass().getEPackage().setNsURI(AnnotationsPackage.eNS_URI);

			res.getContents().add(anno);
		}

		try
		{
			res.save(Collections.emptyMap());
			rif.setSubResource(ResultAlternative.KEY_PSA, file);
			rif.save();
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}

	}

	private static List<ASGAnnotation> getAnnotations(DetectPatternsJob job)
	{
		Map<PSPatternSpecification, Collection<ASGAnnotation>> results = job.getEngine().getResults();
		ArrayList<ASGAnnotation> annotations = new ArrayList<ASGAnnotation>();

		if (results != null)
		{
			for (PSPatternSpecification key : results.keySet())
			{
				for (ASGAnnotation anno : results.get(key))
				{
					annotations.add(anno);
				}
			}
		}

		// sort them (by pattern)
		Collections.sort(annotations, new Comparator<ASGAnnotation>()
		{
			@Override
			public int compare(ASGAnnotation one, ASGAnnotation two)
			{
				return one.getPattern().getName().compareTo(two.getPattern().getName());
			}
		});
		return annotations;
	}

	private static SimpleDateFormat sdf = new SimpleDateFormat("[hh:mm:ss]");

	private static IFolder createResultFolder(IProject project, String name)
	{
		IFolder resultsFolder = getResultsFolder(project);
		IFolder resultFolder = resultsFolder.getFolder(name + " " + sdf.format(new Date()));

		try
		{
			resultFolder.create(true, false, null);
		}
		catch (CoreException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return resultFolder;
	}

	/////////////////////////////////////////
	// Loading annotations
	//
	public static Collection<ASGAnnotation> loadAnnotations(ResultAlternative resultFolder)
	{
		IFile resultFile = (IFile)resultFolder.getSubResource(ResultAlternative.KEY_PSA);
		URI resultUri = URI.createPlatformResourceURI(resultFile.getFullPath().toString(), true);

		ResourceSet ress = new ResourceSetImpl();
		Resource res = ress.getResource(resultUri, true);

		// load resource
		try
		{
			res.load(getLoadOptions((XMIResourceImpl) res));
		}
		catch (IOException e)
		{
			e.printStackTrace();
			return new TreeSet<ASGAnnotation>();
		}

		// get annotations
		Set<ASGAnnotation> annos = new HashSet<ASGAnnotation>();
		for (EObject element : res.getContents())
		{
			if (element instanceof ASGAnnotation)
			{
				annos.add((ASGAnnotation) element);
			}
		}
		
		return annos;
	}

   protected static Map<Object, Object> getLoadOptions(XMIResourceImpl resource)
   {
      Map<Object, Object> options = resource.getDefaultLoadOptions();

      options.put(XMLResource.OPTION_DEFER_ATTACHMENT, Boolean.TRUE);
      options.put(XMLResource.OPTION_DEFER_IDREF_RESOLUTION, Boolean.TRUE);
      options.put(XMLResource.OPTION_USE_DEPRECATED_METHODS, Boolean.TRUE);
      options.put(XMLResource.OPTION_USE_PARSER_POOL, new XMLParserPoolImpl());
      options.put(XMLResource.OPTION_USE_XML_NAME_TO_FEATURE_MAP,
            new HashMap<Object, Object>());

      resource.setIntrinsicIDToEObjectMap(new HashMap<String, EObject>());

      return options;
   }

}
