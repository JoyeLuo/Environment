package eu.cloudscaleproject.env.analyser.handlers;

import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.e4.ui.model.application.MApplication;
import org.eclipse.e4.ui.workbench.modeling.EModelService;
import org.eclipse.e4.ui.workbench.modeling.EPartService;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.WorkbenchException;

import eu.cloudscaleproject.env.product.branding.CloudScaleBranding;

public class OpenMainPerspectiveHandler {
	
	// !! Tried parameterized command for main and results perspective but couldn't make it to work
	// Duplication ftw

	@Execute
	public void execute(IWorkbenchWindow window, MApplication application, EModelService service, EPartService partService) {
		
		//using old e3 show perspective call
		try {
			window.getWorkbench().showPerspective("org.palladiosimulator.pcmbench.perspectives.palladio", window);
			CloudScaleBranding.initProjectExplorer();
		} catch (WorkbenchException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
}
