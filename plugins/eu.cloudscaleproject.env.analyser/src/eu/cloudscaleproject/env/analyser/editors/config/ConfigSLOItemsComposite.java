package eu.cloudscaleproject.env.analyser.editors.config;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.emf.ecore.util.EcoreUtil.EqualityHelper;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.forms.events.ExpansionAdapter;
import org.eclipse.ui.forms.events.ExpansionEvent;
import org.eclipse.ui.forms.widgets.ExpandableComposite;
import org.palladiosimulator.servicelevelobjective.ServiceLevelObjective;
import org.palladiosimulator.simulizar.monitorrepository.MeasurementSpecification;
import org.palladiosimulator.simulizar.monitorrepository.Monitor;
import org.palladiosimulator.simulizar.monitorrepository.MonitorRepository;
import org.palladiosimulator.simulizar.monitorrepository.MonitorrepositoryFactory;
import org.palladiosimulator.simulizar.monitorrepository.MonitorrepositoryPackage;

import de.uka.ipd.sdq.identifier.IdentifierPackage;
import eu.cloudscaleproject.env.analyser.alternatives.ConfAlternative;
import eu.cloudscaleproject.env.common.emf.EObjectWrapper;

public class ConfigSLOItemsComposite extends Composite{
	
	private final ConfAlternative alternative;
	private List<SloCollection> sloCollections = new ArrayList<SloCollection>();
	
	private PropertyChangeListener pcl = new PropertyChangeListener() {
		
		@Override
		public void propertyChange(PropertyChangeEvent evt) {
			if(!ConfigSLOItemsComposite.this.isDisposed()){
				update();
			}
		}
	};
		
	public ConfigSLOItemsComposite(ConfAlternative input, Composite parent, int style) {
		super(parent, style);
		this.alternative = input;
		
		setLayout(new GridLayout(1, true));
		
		Composite toolbarComposite = new Composite(this, SWT.NONE);
		toolbarComposite.setLayout(new GridLayout(4, false));
		toolbarComposite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));
		
		Button btnRadioButton = new Button(toolbarComposite, SWT.RADIO);
		btnRadioButton.setEnabled(false);
		btnRadioButton.setText("List");
		
		Button btnRadioButton_1 = new Button(toolbarComposite, SWT.RADIO);
		btnRadioButton_1.setSelection(true);
		btnRadioButton_1.setEnabled(false);
		btnRadioButton_1.setText("Group");
		
		Composite composite = new Composite(toolbarComposite, SWT.NONE);
		GridData gd_composite = new GridData(SWT.FILL, SWT.FILL, true, false, 1, 1);
		gd_composite.heightHint = 1;
		composite.setLayoutData(gd_composite);
				
		Button btnNewButton = new Button(toolbarComposite, SWT.NONE);
		btnNewButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				super.widgetSelected(e);
				
				MonitorRepository monitorRep = alternative.getUsedMonitorRepository();
				Monitor monitor = MonitorrepositoryFactory.eINSTANCE.createMonitor();
				
				MeasurementSpecification spec = MonitorrepositoryFactory.eINSTANCE.createMeasurementSpecification();
				monitor.getMeasurementSpecifications().add(spec);
				
				monitorRep.getMonitors().add(monitor);
				monitor.setEntityName("Monitor group" + Integer.toString(sloCollections.size()+1));
				
				alternative.setDirty(true);
			}
		});
		btnNewButton.setText("Create new Monitor");
				
		addDisposeListener(new DisposeListener() {
			@Override
			public void widgetDisposed(DisposeEvent e) {
				alternative.removePropertyChangeListener(pcl);
			}
		});
		alternative.addPropertyChangeListener(pcl);
		update();
	}
	
	public void update(){
		
		super.update();
		
		for(int i=1; i<this.getChildren().length; i++){
			this.getChildren()[i].dispose();
		}
		
		sloCollections.clear();
		
		for(ServiceLevelObjective m : alternative.getUsedSloRepository().getServicelevelobjectives()){
			boolean hasBeenAdded = false;
			for(SloCollection sloCollection : sloCollections){
				if(sloCollection.add(m)){
					hasBeenAdded = true;
					break;
				}
			}
			
			if(!hasBeenAdded){
				SloCollection newMc = new SloCollection(m);
				sloCollections.add(newMc);
			}
		}
		
		for(int i=0; i<sloCollections.size(); i++){
			SloCollection mc = sloCollections.get(i);
			for(ServiceLevelObjective m : mc.getSlos()){
				m.setName("SLO group " + Integer.toString(i+1));
			}
		}
		
		//create new composites
		for(final SloCollection sloCollection : sloCollections){
			EObjectWrapper monitorsWrapper = new EObjectWrapper(sloCollection.getSlos());
			
			ExpandableComposite expComposite = new ExpandableComposite(this, SWT.BORDER,
					ExpandableComposite.CLIENT_INDENT
					| ExpandableComposite.COMPACT
					| ExpandableComposite.TITLE_BAR
					| ExpandableComposite.TWISTIE);
			
			expComposite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));
			ConfigSLOComposite sloComposite = new ConfigSLOComposite(alternative, monitorsWrapper, expComposite, SWT.NONE);
			//TODO: expComposite.setText(sloComposite.());
			
			Button btnDelete = new Button(expComposite, SWT.NONE);
			btnDelete.addSelectionListener(new SelectionAdapter() {
				@Override
				public void widgetSelected(SelectionEvent e) {
					super.widgetSelected(e);
					for(ServiceLevelObjective slo : sloCollection.getSlos()){
						EcoreUtil.remove(slo);
					}
					alternative.setDirty(true);
				}
			});
			btnDelete.setText("Delete");
			
			expComposite.setClient(sloComposite);
			expComposite.setTextClient(btnDelete);
			
			expComposite.addExpansionListener(new ExpansionAdapter() {
				public void expansionStateChanged(ExpansionEvent e) {
					ConfigSLOItemsComposite.this.layout();
					ConfigSLOItemsComposite.this.redraw();
					ConfigSLOItemsComposite.this.pack();
				}
			});
		}
		
		this.layout();
		this.redraw();
	}
	
	/*
	private void initBindings(){
		
		if(bindingContext != null){
			bindingContext.dispose();
		}
		bindingContext = new DataBindingContext();
		
		UpdateValueStrategy t2mStrategy = new UpdateValueStrategy(UpdateValueStrategy.POLICY_UPDATE);
		t2mStrategy.setConverter(StringToNumberConverter.toInteger(true));
		
		UpdateValueStrategy m2tStrategy = new UpdateValueStrategy(UpdateValueStrategy.POLICY_UPDATE);
		m2tStrategy.setConverter(NumberToStringConverter.fromInteger(true));
		
		IObservableValue average = EMFEditProperties.list(alternative.getEditingDomain(), )
		
		IObservableValue simTimeObs = EMFEditProperties.value(alternative.getEditingDomain(),
				AbstractsimulationPackage.Literals.SIM_TIME_STOP_CONDITION__SIMULATION_TIME).observe(simTime);
		
		Binding simTimeBind = bindingContext.bindValue(WidgetProperties.text(SWT.Modify).observe(textSimTime),
		        simTimeObs, t2mStrategy, m2tStrategy);
		
		
		ControlDecorationSupport.create(simTimeBind, SWT.TOP | SWT.LEFT);
	}
	*/
	
	private static class SloCollection{
		private List<ServiceLevelObjective> slos = new ArrayList<ServiceLevelObjective>();
		
		public SloCollection(ServiceLevelObjective slo) {
			slos.add(slo);
		}
		
		public List<ServiceLevelObjective> getSlos(){
			return slos;
		}
		
		public boolean add(ServiceLevelObjective slo){
			ServiceLevelObjective monitorExample = slos.get(0);
		    EqualityHelper equalityHelper = new EqualityHelper()
		    {
				private static final long serialVersionUID = 1L;

				@Override
		    	protected boolean haveEqualAttribute(EObject eObject1, EObject eObject2, EAttribute c) {
		    		if(c.getFeatureID() == IdentifierPackage.IDENTIFIER__ID){
		    			return true;
		    		}
		    		if(c.getFeatureID() == MonitorrepositoryPackage.MONITOR__MEASURING_POINT){
		    			return true;
		    		}
		    		return super.haveEqualAttribute(eObject1, eObject2, c);
		    	}
		    };
		    
		    boolean isEqual = equalityHelper.equals(monitorExample, slo);
			
			if(isEqual){
				slos.add(slo);
				return true;
			}
			
			return false;
		}
	}
}