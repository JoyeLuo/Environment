package eu.cloudscaleproject.env.analyser.editors.config;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ComboViewer;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;

import eu.cloudscaleproject.env.analyser.alternatives.ConfAlternative;
import eu.cloudscaleproject.env.analyser.alternatives.InputAlternative;
import eu.cloudscaleproject.env.common.notification.diagram.ValidationDiagramService;
import eu.cloudscaleproject.env.toolchain.CSTool;
import eu.cloudscaleproject.env.toolchain.ToolchainUtils;
import eu.cloudscaleproject.env.toolchain.resources.ResourceProvider;
import eu.cloudscaleproject.env.toolchain.resources.ResourceRegistry;
import eu.cloudscaleproject.env.toolchain.resources.types.IEditorInputResource;

public class SelectInputAltComposite extends Composite{
		
	private final ResourceProvider inputResourceProvider;
	private final ComboViewer comboViewerInput;
			
	private final PropertyChangeListener inputResourceListener = new PropertyChangeListener() {
		@Override
		public void propertyChange(PropertyChangeEvent evt) {
			if(ResourceProvider.PROP_RESOURCE_ADDED.equals(evt.getPropertyName())){
				comboViewerInput.add(evt.getNewValue());
			}
			else if(ResourceProvider.PROP_RESOURCE_REMOVED.equals(evt.getPropertyName())){
				comboViewerInput.remove(evt.getOldValue());
			}
		}
	};
	
	/**
	 * Create the composite.
	 * @param parent
	 * @param style
	 */
	public SelectInputAltComposite(final IProject project, final ConfAlternative ca, Composite parent, int style) {
		super(parent, style);
				
		this.inputResourceProvider = ResourceRegistry.getInstance().getResourceProvider(project, CSTool.ANALYSER_INPUT);
		
		setLayout(new GridLayout(3, false));
		
		Label lblSelectInput = new Label(this, SWT.NONE);
		lblSelectInput.setText("Input:");
		new Label(this, SWT.NONE);
		{
			comboViewerInput = new ComboViewer(this, SWT.READ_ONLY);
			Combo combo = comboViewerInput.getCombo();
			GridData gd_combo = new GridData(SWT.LEFT, SWT.CENTER, true, false, 1, 1);
			gd_combo.minimumWidth = 300;
			combo.setLayoutData(gd_combo);
		}
		
		comboViewerInput.setContentProvider(ArrayContentProvider.getInstance());
		comboViewerInput.setLabelProvider(new LabelProvider() {
			
			@Override
			public String getText(Object element) {
				if (element instanceof InputAlternative) {
					InputAlternative ia = (InputAlternative) element;
					return ia.getName();
				}
				return super.getText(element);
			}
			
		});
		
		//configure input alternative combo-box
		inputResourceProvider.addListener(inputResourceListener);
		
		comboViewerInput.setInput(inputResourceProvider.getResources());
		
		IFolder inputAlt = (IFolder)ca.getSubResource(ToolchainUtils.KEY_INPUT_ALTERNATIVE);
		if(inputAlt != null){
			IEditorInputResource eir = inputResourceProvider.getResource(inputAlt);
			if(eir != null){
				comboViewerInput.setSelection(new StructuredSelection(eir), true);
			}
			else{
				comboViewerInput.setSelection(new StructuredSelection(), true);
			}
		}

		comboViewerInput.addSelectionChangedListener(new ISelectionChangedListener() {
			@Override
			public void selectionChanged(SelectionChangedEvent event) {
				IStructuredSelection selection = (IStructuredSelection)event.getSelection();
				InputAlternative ia = (InputAlternative)selection.getFirstElement();				
				ca.setInputAlternative(ia);
				
				ValidationDiagramService.showStatus(project, CSTool.ANALYSER_INPUT.getID(), ia);
				ValidationDiagramService.showStatus(project, CSTool.ANALYSER_CONF.getID(), ca);
			}
		});
		
		//on dispose
		addDisposeListener(new DisposeListener() {
			@Override
			public void widgetDisposed(DisposeEvent e) {
				inputResourceProvider.removeListener(inputResourceListener);
			}
		});
	}
	
	@Override
	public void update() {
		comboViewerInput.refresh(true);
		super.update();
	}

	@Override
	protected void checkSubclass() {
	}
}
