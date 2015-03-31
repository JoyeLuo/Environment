package eu.cloudscaleproject.env.analyser.wizard.pages;

import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

public class NameSelectionPage extends WizardPage{
	
	private String name = "";

	public NameSelectionPage(String title) {
		super(title);
	}
	
	public String getName(){
		return this.name;
	}

	@Override
	public void createControl(Composite parent) {
		
		Composite container = new Composite(parent, SWT.NONE);
		container.setLayout(new GridLayout(2, false));
		
		Label lblName = new Label(container, SWT.NONE);
		lblName.setText("Alternative name:");
		
		final Text text = new Text(container, SWT.NONE);
		text.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));
		
		text.addModifyListener(new ModifyListener() {
			
			@Override
			public void modifyText(ModifyEvent e) {
				name = text.getText();
			}
		});
		
		setControl(container);
	}

}