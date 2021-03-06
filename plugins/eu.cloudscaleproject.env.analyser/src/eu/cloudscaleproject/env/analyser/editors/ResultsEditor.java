package eu.cloudscaleproject.env.analyser.editors;

import javax.inject.Inject;

import org.eclipse.e4.core.di.annotations.Optional;
import org.eclipse.e4.ui.model.application.ui.basic.MPart;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;

import eu.cloudscaleproject.env.analyser.alternatives.ResultAlternative;
import eu.cloudscaleproject.env.analyser.editors.result.ResultsComposite;
import eu.cloudscaleproject.env.toolchain.editors.AlternativeEditor;
import eu.cloudscaleproject.env.toolchain.resources.types.IEditorInputResource;


/**
 * 
 * @author Vito Čuček <vito.cucek@xlab.si>
 * 
 */

public class ResultsEditor extends AlternativeEditor{
			
	@Inject
	@Optional
	public void setAlternative(MPart part, IEditorInputResource alternative){
		part.setLabel("Analyser results ["+ alternative.getName() +"]");
	}

	@Override
	public Composite createComposite(Composite composite, IEditorInputResource resource) {
		return new ResultsComposite((ResultAlternative)resource, composite, SWT.NONE);
	}
	

}
