package eu.cloudscaleproject.env.staticspotter.editors;

import org.eclipse.core.resources.IProject;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.swt.widgets.Composite;

import eu.cloudscaleproject.env.staticspotter.alternatives.ConfigAlternative;
import eu.cloudscaleproject.env.staticspotter.editors.composites.ConfigAlternativeComposite;
import eu.cloudscaleproject.env.staticspotter.wizard.InputSelectionWizard;
import eu.cloudscaleproject.env.toolchain.ToolchainUtils;
import eu.cloudscaleproject.env.toolchain.resources.ResourceRegistry;
import eu.cloudscaleproject.env.toolchain.resources.types.IEditorInput;
import eu.cloudscaleproject.env.toolchain.resources.types.IEditorInputResource;
import eu.cloudscaleproject.env.toolchain.util.SidebarContentProvider;
import eu.cloudscaleproject.env.toolchain.util.SidebarEditorComposite;

public class ConfigComposite extends SidebarEditorComposite {
	
	private final String[] sections = new String[]{"Configurations:"};
	private IProject project;
	
	/**
	 * Create the composite.
	 * @param parent
	 * @param style
	 */
	public ConfigComposite(IProject project, Composite parent, int style) {
		super(parent, style);
		
		this.project = project;
		
		setResourceProvider(ResourceRegistry.getInstance().getResourceProvider(project, ToolchainUtils.SPOTTER_STA_CONF_ID));
		
		setContentProvider(new SidebarContentProvider() {
			
			@Override
			public String[] getSections() {
				return sections;
			}
			
			@Override
			public String getSection(IEditorInputResource resource) {
				return sections[0];
			}
			
			@Override
			public Composite createComposite(Composite parent, int style,
					IEditorInputResource resource) {
				return new ConfigAlternativeComposite(parent, style, (ConfigAlternative)resource);
			}
		});
		
		//init();
	}

	@Override
	public void update() {
		// TODO Auto-generated method stub
		super.update();
	}
}
