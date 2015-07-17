package eu.cloudscaleproject.env.spotter.alternatives;

import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;

import eu.cloudscaleproject.env.toolchain.CSTool;
import eu.cloudscaleproject.env.toolchain.resources.types.EditorInputFolder;

public class ResultAlternative extends EditorInputFolder {
	
	public ResultAlternative(IProject project, IFolder folder) {
		// TODO Auto-generated constructor stub
		super (project, folder, CSTool.SPOTTER_DYN_RES.getID());
	}
	
}
