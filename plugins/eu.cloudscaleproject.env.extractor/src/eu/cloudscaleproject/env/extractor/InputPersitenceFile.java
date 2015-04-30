package eu.cloudscaleproject.env.extractor;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;

import eu.cloudscaleproject.env.toolchain.resources.types.EditorInputFile;


public class InputPersitenceFile extends EditorInputFile {
	
	public static final String KEY_PROJECT_URL = "project_url";

	public InputPersitenceFile(IProject project, IFile file) {
		super(project, file);
	}
}
