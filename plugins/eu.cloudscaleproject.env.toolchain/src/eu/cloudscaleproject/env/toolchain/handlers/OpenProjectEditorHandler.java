 
package eu.cloudscaleproject.env.toolchain.handlers;

import java.util.logging.Logger;

import javax.inject.Named;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.e4.core.contexts.Active;
import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.e4.core.di.annotations.Optional;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.ide.IDE;
import org.eclipse.ui.part.FileEditorInput;

import eu.cloudscaleproject.env.common.CloudscaleContext;
import eu.cloudscaleproject.env.common.explorer.ExplorerProjectPaths;
import eu.cloudscaleproject.env.common.notification.IValidationStatus;
import eu.cloudscaleproject.env.toolchain.editors.ProjectEditor;
import eu.cloudscaleproject.env.toolchain.resources.types.IEditorInputResource;
import eu.cloudscaleproject.env.toolchain.util.OpenAlternativeUtil;

public class OpenProjectEditorHandler {
	
    public static String EDITORS_EXTENSION_POINT_ID = "eu.cloudscaleproject.env.toolchain.editors";
	private static final Logger logger = Logger.getLogger(OpenProjectEditorHandler.class.getName());
	
	@Execute
	public void execute(@Active IProject project,
						@Named("eu.cloudscaleproject.env.toolchain.commandparameter.tabid") String tabid,
						@Optional @Named("eu.cloudscaleproject.env.toolchain.commandparameter.action") String action) {
				

		if (project == null){
			logger.warning("Can't execute command: Project is null!");
			return;
		}

		IValidationStatus validationStatus = CloudscaleContext.getActiveContext().get(IValidationStatus.class);
		if (validationStatus!=null && validationStatus.getProvider() instanceof IEditorInputResource)
		{
			IEditorInputResource alternative = (IEditorInputResource)validationStatus.getProvider();
			OpenAlternativeUtil.openAlternative(alternative);
			return;
		}

			
		IFile file = ExplorerProjectPaths.getDashboardFile(project);

		IEditorInput editorInput = new FileEditorInput(file);
		IWorkbenchPage page = PlatformUI.getWorkbench()
				.getActiveWorkbenchWindow().getActivePage();

		try {
			IEditorPart part = IDE.openEditor(page, editorInput, "eu.cloudscaleproject.env.toolchain.tooleditor");
			
			if (part instanceof ProjectEditor) {
				ProjectEditor pe = (ProjectEditor) part;
				pe.openTab(tabid, action);
			}
		} catch (PartInitException e) {
			e.printStackTrace();
		}
	}
}