package eu.cloudscaleproject.env.spotter.editors;

import javax.inject.Inject;

import org.eclipse.e4.core.di.annotations.Optional;
import org.eclipse.e4.ui.model.application.ui.basic.MPart;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;

import eu.cloudscaleproject.env.spotter.alternatives.ConfigAlternative;
import eu.cloudscaleproject.env.spotter.editors.composite.ConfigAlternativeComposite;
import eu.cloudscaleproject.env.toolchain.editors.AlternativeEditor;
import eu.cloudscaleproject.env.toolchain.resources.types.IEditorInputResource;

/**
 *
 * @author Vito Čuček <vito.cucek@xlab.si>
 *
 */
public class ConfigEditor extends AlternativeEditor{
	
	@Inject
	@Optional
	public void setAlternative(MPart part, IEditorInputResource alternative){
		part.setLabel("Dynamic spotter config ["+ alternative.getName() +"]");
	}

	@Override
	public Composite createComposite(Composite composite, IEditorInputResource resource) {
		ConfigAlternative alternative = (ConfigAlternative)resource;
		return new ConfigAlternativeComposite(alternative.getProject(), composite, SWT.NONE, alternative);
	}
	
}