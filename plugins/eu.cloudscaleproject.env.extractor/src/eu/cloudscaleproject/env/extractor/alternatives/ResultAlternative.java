package eu.cloudscaleproject.env.extractor.alternatives;

import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;

import eu.cloudscaleproject.env.toolchain.CSTool;
import eu.cloudscaleproject.env.toolchain.ModelType;
import eu.cloudscaleproject.env.toolchain.resources.types.AbstractResultAlternative;

public class ResultAlternative extends AbstractResultAlternative {
	
	public static final String KEY_FOLDER_SOMOX = "folder_somox";
	public static final String KEY_FOLDER_MODISCO = "folder_modisco";

	public static final String KEY_FILE_MODISCO_JAVA2KDM = "file_modisco_java2kdm";
	public static final String KEY_FILE_MODISCO_JAVA = "file_modisco_java";
	public static final String KEY_FILE_MODISCO_KDM = "file_modisco_kdm";
	public static final String KEY_FILE_SYSTEM_DIAGRAM = "file_system_diagram";
	public static final String KEY_FILE_REPOSITORY_DIAGRAM = "file_repositroy_diagram";

	private static final String RESULT_FOLDER_SOMOX = "somox/";
	private static final String RESULT_FOLDER_MODISCO = "modisco/";

	public ResultAlternative(IProject project, IFolder folder){
		super(project, folder, ModelType.GROUP_PCM, CSTool.EXTRACTOR_RES.getID(), CSTool.EXTRACTOR_CONF.getID());
	}
	
	@Override
	public void doCreate(IProgressMonitor monitor) {		
		//
		// Prepare folder structure
		//
		IFolder modiscoFolder = getResource().getFolder(RESULT_FOLDER_MODISCO);
		IFolder somoxFolder = getResource().getFolder(RESULT_FOLDER_SOMOX);
		
		try {
			modiscoFolder.create(true, true, null);
			somoxFolder.create(true, true, null);
		} catch (CoreException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		setSubResource(KEY_FOLDER_MODISCO, modiscoFolder);
		setSubResource(KEY_FOLDER_SOMOX, somoxFolder);
		
		save(monitor);
	}
}
