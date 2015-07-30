package eu.cloudscaleproject.env.analyser.editors.input;

import org.eclipse.core.databinding.observable.list.IObservableList;
import org.eclipse.emf.databinding.edit.EMFEditProperties;
import org.eclipse.emf.databinding.edit.IEMFEditListProperty;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.forms.widgets.ExpandableComposite;
import org.scaledl.usageevolution.Usage;
import org.scaledl.usageevolution.UsageEvolution;
import org.scaledl.usageevolution.UsageevolutionFactory;
import org.scaledl.usageevolution.UsageevolutionPackage;

import eu.cloudscaleproject.env.analyser.ModelUtils;
import eu.cloudscaleproject.env.analyser.alternatives.InputAlternative;
import eu.cloudscaleproject.env.common.dialogs.TextInputDialog;
import eu.cloudscaleproject.env.common.interfaces.IRefreshable;
import eu.cloudscaleproject.env.common.ui.list.ListComposite;
import eu.cloudscaleproject.env.toolchain.ModelType;
import eu.cloudscaleproject.env.toolchain.ToolchainUtils;


public class UsageEvolutionComposite extends Composite implements IRefreshable{
		
	private final InputAlternative alternative;
	private ListComposite usageListComposite;
	
	private UsageEvolution usageEvolution;

	public UsageEvolutionComposite(final InputAlternative alt, Composite parent, int style) {
		super(parent, style);
		
		this.alternative = alt;
		this.usageEvolution = (UsageEvolution)alt.getModelRootSingle(ToolchainUtils.KEY_FILE_USAGEEVOLUTION);
		
		setLayout(new GridLayout(1, false));
		
		//toolbar
		Composite toolbarComposite = new Composite(this, SWT.NONE);
		toolbarComposite.setLayout(new GridLayout(4, false));
		toolbarComposite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));
		
		Label label = new Label(toolbarComposite, SWT.NONE);
		label.setText("Usage evolution specifications:");
		
		//used as expander
		Composite composite = new Composite(toolbarComposite, SWT.NONE);
		GridData gd_composite = new GridData(SWT.FILL, SWT.FILL, true, false, 1, 1);
		gd_composite.heightHint = 1;
		composite.setLayoutData(gd_composite);
				
		Button btnNewButton = new Button(toolbarComposite, SWT.NONE);
		btnNewButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				super.widgetSelected(e);
				
				TextInputDialog dialog = new TextInputDialog(Display.getDefault().getActiveShell());
				dialog.open();
				
				if(dialog.getReturnCode() == IDialogConstants.OK_ID){
					
					Usage usage = UsageevolutionFactory.eINSTANCE.createUsage();
					usage.setEntityName(dialog.getText());
					
					if(usageEvolution == null){
						ModelUtils.createModels(alternative, null, ModelType.USAGE_EVOLUTION);
						usageEvolution = (UsageEvolution)alternative.getModelRootSingle(ToolchainUtils.KEY_FILE_USAGEEVOLUTION);
						initBinding();
					}
					usageEvolution.getUsages().add(usage);
					alternative.setDirty(true);
					
					//show it
					usageListComposite.showChild(usage);
				}
			}
		});
		btnNewButton.setText("Add new");
		
		//usages list
		usageListComposite = new ListComposite(this, SWT.NONE) {
			@Override
			protected Composite createComposite(ExpandableComposite parent, Object source) {
				
				Usage usage = (Usage)source;
				parent.setText("Usage evolution: " + ((UsageEvolution)usage.eContainer()).getUsages().indexOf(usage));
				UsageComposite usageComposite = new UsageComposite(alternative, (Usage)source, parent, SWT.NONE);
				
				return usageComposite;
			}
		};
		usageListComposite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

		//usage list binding
		initBinding();
		layout();
	}
	
	private void initBinding(){
		final IEMFEditListProperty usageProp = EMFEditProperties.list(alternative.getEditingDomain(), 
				UsageevolutionPackage.Literals.USAGE_EVOLUTION__USAGES);
		
		IObservableList usagesObs = usageProp.observe(usageEvolution);
		usageListComposite.initBindings(usagesObs);
		usageListComposite.refresh();
	}

	@Override
	public void refresh() {
		
		UsageEvolution ue = (UsageEvolution)alternative.getModelRootSingle(ToolchainUtils.KEY_FILE_USAGEEVOLUTION);
		if(usageEvolution != ue){
			usageEvolution = ue;
			initBinding();
		}
		
	}
}