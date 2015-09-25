package eu.cloudscaleproject.env.spotter.editors;

import javax.inject.Inject;

import org.eclipse.e4.core.di.annotations.Optional;
import org.eclipse.e4.ui.model.application.ui.basic.MPart;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;

import eu.cloudscaleproject.env.spotter.alternatives.ConfigAlternative;
import eu.cloudscaleproject.env.spotter.editors.composite.ConfigAlternativeComposite;
import eu.cloudscaleproject.env.toolchain.editors.AlternativeEditor;

/**
 *
 * @author Vito Čuček <vito.cucek@xlab.si>
 *
 */
public class ConfigEditor extends AlternativeEditor{

	private ConfigAlternativeComposite composite;
	
	@Inject
	@Optional
	public void setAlternative(MPart part, Composite parent, ConfigAlternative alternative){
		
		if(composite != null){
			composite.dispose();
		}
		
		part.setLabel("Dynamic spotter config ["+ alternative.getName() +"]");
		composite = new ConfigAlternativeComposite(alternative.getProject(), parent, SWT.NONE, alternative);
		
		setAlternative(alternative);
		
		parent.layout();
		parent.redraw();
	}
	
}