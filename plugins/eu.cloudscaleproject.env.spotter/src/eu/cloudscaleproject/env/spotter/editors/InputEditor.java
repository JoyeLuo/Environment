package eu.cloudscaleproject.env.spotter.editors;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import org.eclipse.e4.core.di.annotations.Optional;
import org.eclipse.e4.ui.model.application.ui.basic.MPart;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;

import eu.cloudscaleproject.env.spotter.alternatives.InputAlternative;
import eu.cloudscaleproject.env.spotter.editors.composite.InputAlternativeComposite;
import eu.cloudscaleproject.env.toolchain.editors.AlternativeEditor;


/**
 *
 * @author Vito Čuček <vito.cucek@xlab.si>
 *
 */
public class InputEditor extends AlternativeEditor{

	@Inject
	private MPart part;
	private InputAlternativeComposite composite;
	
	@Inject
	@Optional
	@PostConstruct
	public void postConstruct(Composite parent, InputAlternative alternative){
		
		if(composite != null){
			composite.dispose();
		}
		
		part.setLabel("Extractor input ["+ alternative.getName() +"]");
		composite = new InputAlternativeComposite(alternative.getProject(), parent, SWT.NONE, alternative);
		
		setAlternative(alternative);
		
		parent.layout();
		parent.redraw();
	}
	
}