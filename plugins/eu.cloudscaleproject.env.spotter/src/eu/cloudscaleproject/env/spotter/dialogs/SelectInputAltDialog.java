package eu.cloudscaleproject.env.spotter.dialogs;

import javax.inject.Inject;

import org.eclipse.core.resources.IProject;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.List;
import org.eclipse.swt.widgets.Shell;

import eu.cloudscaleproject.env.common.notification.StatusManager;
import eu.cloudscaleproject.env.spotter.ResourceUtils;
import eu.cloudscaleproject.env.toolchain.ToolchainUtils;
import eu.cloudscaleproject.env.toolchain.resources.ResourceProvider;
import eu.cloudscaleproject.env.toolchain.resources.ResourceRegistry;
import eu.cloudscaleproject.env.toolchain.resources.types.EditorInputFolder;
import eu.cloudscaleproject.env.toolchain.resources.types.IEditorInputResource;

public class SelectInputAltDialog extends Dialog{
	
	@Inject
	private StatusManager statusManager;
	
	private final ResourceProvider inputResourceProvider;
	private final ResourceProvider confResourceProvider;

	private final IProject project;
	
	private List list = null;

	public SelectInputAltDialog(IProject project, Shell parentShell) {
		super(parentShell);
		this.project = project;
		this.inputResourceProvider = ResourceRegistry.getInstance().
				getResourceProvider(project, ToolchainUtils.SPOTTER_DYN_INPUT_ID);
		this.confResourceProvider = ResourceRegistry.getInstance().
				getResourceProvider(project, ToolchainUtils.SPOTTER_DYN_CONF_ID);
	}

	@Override
	protected Control createDialogArea(Composite parent) {
		Composite container = (Composite) super.createDialogArea(parent);
		
		list = new List(container, SWT.BORDER);
		GridData gd_list = new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1);
		list.setLayoutData(gd_list);
		
		java.util.List<IEditorInputResource> confResources = inputResourceProvider.getResources();
		for(IEditorInputResource input : confResources){
			list.add(input.getName());
		}
		
		return container;
	}
	
	@Override
	protected void buttonPressed(int buttonId) {
		if (IDialogConstants.OK_ID == buttonId) {
			int selectionIndex = list.getSelectionIndex();
			if(selectionIndex >= 0){
				
				IEditorInputResource selectedResource = inputResourceProvider.getResources().get(selectionIndex);
				inputResourceProvider.tagResource(ResourceProvider.TAG_SELECTED, selectedResource);
				
				java.util.List<EditorInputFolder> applicableConfAlt = ResourceUtils.getConfAlternatives(project, (EditorInputFolder)selectedResource);
				confResourceProvider.tagResource(ResourceProvider.TAG_SELECTED, applicableConfAlt.isEmpty() ? null : applicableConfAlt.get(0));
				
				Display.getDefault().asyncExec(new Runnable() {
					public void run() {
						statusManager.validate(project, StatusManager.Tool.DYNAMIC_SPOTTER_INPUT.getID());
						statusManager.validate(project, StatusManager.Tool.DYNAMIC_SPOTTER.getID());
					};
				});
			}
		}
		super.buttonPressed(buttonId);
	}
	
	@Override
	protected Point getInitialSize() {
		return new Point(450, 300);
	}
}