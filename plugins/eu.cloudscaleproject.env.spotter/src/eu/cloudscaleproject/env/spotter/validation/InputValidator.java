package eu.cloudscaleproject.env.spotter.validation;

import java.text.MessageFormat;
import java.util.List;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.spotter.eclipse.ui.Activator;
import org.spotter.eclipse.ui.ServiceClientWrapper;
import org.spotter.eclipse.ui.UICoreException;
import org.spotter.eclipse.ui.model.xml.MeasurementEnvironmentFactory;
import org.spotter.shared.environment.model.XMeasurementEnvironment;

import eu.cloudscaleproject.env.common.notification.IToolStatus;
import eu.cloudscaleproject.env.common.notification.StatusManager;
import eu.cloudscaleproject.env.common.notification.ToolValidator;
import eu.cloudscaleproject.env.toolchain.ToolchainUtils;
import eu.cloudscaleproject.env.toolchain.resources.ResourceProvider;
import eu.cloudscaleproject.env.toolchain.resources.ResourceRegistry;
import eu.cloudscaleproject.env.toolchain.resources.types.IEditorInputResource;

public class InputValidator extends ToolValidator {
	
	private final String ERROR = "eu.cloudscaleproject.env.spotter.validation.InputValidator.error.input";
	private final String MESSAGE_PATTERN = "Alternative {0}: {1}";
	
	@Override
	public String getToolID() {
		return StatusManager.Tool.DYNAMIC_SPOTTER_INPUT.getID();
	}

	@Override
	public IResource[] getDependantResources(IProject project) {
		return new IResource[]{ToolchainUtils.getToolFolder(project, ToolchainUtils.SPOTTER_DYN_INPUT_ID)};
	}

	@Override
	public boolean doValidate(IProject project, IToolStatus status) {
		
		ResourceProvider inputResourceProvider = ResourceRegistry.getInstance().
				getResourceProvider(project, ToolchainUtils.SPOTTER_DYN_INPUT_ID);
		
		IEditorInputResource selectedRes = inputResourceProvider.getTaggedResource(ResourceProvider.TAG_SELECTED);
		
		//retrieve first
		if(!inputResourceProvider.getResources().isEmpty()){
			selectedRes = inputResourceProvider.getResources().get(0);
			inputResourceProvider.tagResource(ResourceProvider.TAG_SELECTED, selectedRes);
		}
		
		if(selectedRes == null){
			status.setIsInProgress(false);
			status.setIsDone(false);
			return false;
		}
		
		status.setInstanceName(selectedRes.getName());
		status.setIsInProgress(true);

		//validate selected input alternatives
		try {								
			// check connection
			ServiceClientWrapper client = Activator.getDefault().getClient(selectedRes.getResource().getName());
			String hostname = selectedRes.getProperty("hostname");
			String port = selectedRes.getProperty("port");
			
			status.handleWarning(ERROR, hostname == null || hostname.isEmpty(), true, 
					MessageFormat.format(MESSAGE_PATTERN, selectedRes.getName(),"Connection hostname is not specified!"));
			status.handleWarning(ERROR, port == null || port.isEmpty(), true, 
					MessageFormat.format(MESSAGE_PATTERN, selectedRes.getName(),"Connection port is not specified!"));
			
			client.saveServiceClientSettings(hostname, port);
			
			status.handleWarning(ERROR, !client.testConnection(false), true, 
					MessageFormat.format(MESSAGE_PATTERN, selectedRes.getName(),"Connection to server can not be established!"));
			
			// check input files
			IFolder folder = (IFolder)selectedRes.getResource();
			IFile confFile = folder.getFile("mEnv.xml");
			status.handleWarning(ERROR, !confFile.exists(), true, 
					MessageFormat.format(MESSAGE_PATTERN, selectedRes.getName(),"Instrumentation controller and Measurement controller not specified!"));
			
			MeasurementEnvironmentFactory factory = MeasurementEnvironmentFactory.getInstance();
				
			XMeasurementEnvironment confEnv = factory.parseXMLFile(confFile.getLocation().toString());
			status.handleWarning(ERROR, confEnv == null, true, 
					MessageFormat.format(MESSAGE_PATTERN, selectedRes.getName(),"Invalid input configuration!"));
			
			List<?> ic = confEnv.getInstrumentationController();
			List<?> mc = confEnv.getMeasurementController();
			
			status.handleWarning(ERROR, ic == null || ic.isEmpty(), true, 
					MessageFormat.format(MESSAGE_PATTERN, selectedRes.getName(),"Instrumentation controller not specified!"));					
			status.handleWarning(ERROR, mc == null || mc.isEmpty(), true,
					MessageFormat.format(MESSAGE_PATTERN, selectedRes.getName(),"Measurement controller not specified!"));
				
		} catch (IllegalStateException e) {
			status.setIsDone(false);
			return false;
		} catch (UICoreException e) {
			e.printStackTrace();
		}
		
		status.setIsDone(true);
		return true;
	}

}
