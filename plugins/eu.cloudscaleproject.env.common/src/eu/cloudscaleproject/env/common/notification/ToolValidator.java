package eu.cloudscaleproject.env.common.notification;

import java.util.logging.Logger;

import javax.inject.Inject;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.e4.core.di.annotations.Optional;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Cursor;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.PlatformUI;

import eu.cloudscaleproject.env.common.DIExtension;

public abstract class ToolValidator extends DIExtension{
	
	@Optional @Inject
	protected StatusManager statusManager;
	
	private static final Logger logger = Logger.getLogger(ToolValidator.class.getName());
	private static final String UNKNOWN_ERROR = "eu.cloudscaleproject.env.common.notification.ToolValidator.error";
	
	public abstract String getToolID();
	public abstract IResource[] getDependantResources(IProject project);
	
	private static Object lock = new Object();
		
	public final boolean validate(IProject project){
		
		//synchronize to one validator at a time
		synchronized (lock) {
			
			Shell shell = PlatformUI.getWorkbench().getDisplay().getActiveShell();
			Display display = PlatformUI.getWorkbench().getDisplay();
			
			shell.setCursor(new Cursor(display, SWT.CURSOR_WAIT));
			
			try{
				if(statusManager == null){
					logger.severe("Validation manager was not found! Skipping validation!");
					return false;
				}
				
				IToolStatus status = statusManager.getStatus(project, getToolID());
				if(status == null){
					logger.severe("Tool status with id '" + getToolID() + "' was not found! Skipping validation!");
					return false;
				}
				
				status.setInstanceName(null);
				
				if(!status.hasMetRequirements()){
					status.setIsInProgress(false);
					status.setIsDone(false);
					return false;
				}
				
				try{
					return doValidate(project, status);
				}
				catch(Exception e){
					status.setIsDone(false);
					status.addWarning(UNKNOWN_ERROR, e.getMessage());
					e.printStackTrace();
				}
			}
			finally{
				shell.setCursor(new Cursor(display, SWT.CURSOR_ARROW));
			}
		}
		return false;
	}
	
	protected abstract boolean doValidate(IProject project, IToolStatus status);

}
