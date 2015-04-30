package eu.cloudscaleproject.env.common.notification;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.logging.Logger;

import javax.inject.Inject;

import org.eclipse.core.resources.IProject;

import eu.cloudscaleproject.env.common.CloudscaleContext;
import eu.cloudscaleproject.env.common.ExtensionRetriever;

public class StatusManager {
	
	public static final String PROP_STATUS_PROVIDER_ADDED = "eu.cloudscaleproject.env.common.notification.StatusManager.providerAdded";
	public static final String PROP_STATUS_PROVIDER_REMOVED = "eu.cloudscaleproject.env.common.notification.StatusManager.providerRemoved";

	private final PropertyChangeSupport pcs = new PropertyChangeSupport(this);
	
	public enum Tool{
		
		EXTRACTOR_INPUT("ext_input"),
		EXTRACTOR("ext_tool"),
		EXTRACTOR_RESULTS("ext_res"),
		
		ANALYSER_INPUT("analyser_input"),
		ANALYSER("analyser_tool"),
		ANALYSER_RESULTS("analyser_res"),
				
		DYNAMIC_SPOTTER_INPUT("dynamic_spotter_input"),
		DYNAMIC_SPOTTER("dynamic_spotter_tool"),
		DYNAMIC_SPOTTER_RESULTS("dynamic_spotter_res"),
		
		STATIC_SPOTTER_INPUT("static_spotter_input"),
		STATIC_SPOTTER("static_spotter_tool"),
		STATIC_SPOTTER_RESULTS("static_spotter_res"),
								
		OVERVIEW("overview"),
		ARCHITECTURE_TEMPLATES("architectureTemplates"),
		USAGE_EVOLUTION("usageEvolution"),
		
		//actions
		TRANSFORM_OVERVIEW("action_transform_overview_analyser"),
		IMPORT_EXT_ANALYSER("action_import_ext_analyser"),
		IMPORT_EXT_SS("action_import_ext_ss"),
		USE_MEASUREMENTS("action_useMeasurements"),
		IMPORT_EXT_OVERVIEW("action_import_ext_overview");

		
		private final String id;
		
		Tool(String id){
			this.id = id;
		}
		
		public String getID(){
			return id;
		}
	}
	
	private static final Logger logger = Logger.getLogger(StatusManager.class.getName());
		
	private List<IResourceValidator> validators = null;
	private HashMap<IValidationStatusProvider, IProject> statusProviders = new HashMap<IValidationStatusProvider, IProject>();
		
	private static StatusManager instance = null;
	public static StatusManager getInstance(){
		if(instance == null){
			instance = new StatusManager();
			CloudscaleContext.inject(instance);
		}
		return instance;
	}
	
	private final PropertyChangeListener statusProviderListener = new PropertyChangeListener() {
		@Override
		public void propertyChange(PropertyChangeEvent evt) {
			pcs.firePropertyChange(evt);
		}
	};
	
	public synchronized void addStatusProvider(IProject project, IValidationStatusProvider statusProvider){
		statusProviders.put(statusProvider, project);
		statusProvider.addPropertyChangeListener(statusProviderListener);
		pcs.firePropertyChange(PROP_STATUS_PROVIDER_ADDED, null, statusProvider);
	}
	
	public synchronized void removeStatusProvider(IValidationStatusProvider statusProvider){
		statusProviders.remove(statusProvider);
		statusProvider.removePropertyChangeListener(statusProviderListener);			
		pcs.firePropertyChange(PROP_STATUS_PROVIDER_REMOVED, statusProvider, null);
	}
	
	public synchronized List<IValidationStatusProvider> getStatusProvider(IProject project, String id){
		List<IValidationStatusProvider> out = new ArrayList<IValidationStatusProvider>();
		for(Entry<IValidationStatusProvider, IProject> entry : statusProviders.entrySet()){
			try{
				if(project.equals(entry.getValue()) && id.equals(entry.getKey().getID())){
					out.add(entry.getKey());
				}
			}
			catch(NullPointerException e){
				//skip
			}
		}
		return out;
	}
	
	/*
	public List<IValidationStatusProvider> getStatusProviders(IProject project){
		List<IValidationStatusProvider> out = new ArrayList<IValidationStatusProvider>();
		for(IValidationStatusProvider provider : statusProviders){
			try{
				if(project.equals(provider.getProject())){
					out.add(provider);
				}
			}
			catch(NullPointerException e){
				//skip
			}
		}
		return out;
	}
	*/
	
	public void addPropertyChangeListener(PropertyChangeListener listener){
		pcs.addPropertyChangeListener(listener);
	}
	
	public void removePropertyChangeListener(PropertyChangeListener listener){
		pcs.removePropertyChangeListener(listener);
	}
	
	//validation thread
	private static final ConcurrentLinkedQueue<Runnable> validationTasks = new ConcurrentLinkedQueue<Runnable>();
	static {
		Thread validationThread = new Thread(new Runnable() {

			@Override
			public void run() {
				while(!Thread.interrupted()){
										
					synchronized (validationTasks) {
						if(validationTasks.isEmpty()){
							try {
								validationTasks.wait();
							} catch (InterruptedException e) {
								e.printStackTrace();
							}
						}
					}
					
					Runnable r = validationTasks.poll();
					
					try{
						if(r != null){
							r.run();
						}
					}
					catch(Exception e){
						e.printStackTrace();
					}
				
				}
			}
			
		}, "Validation thread");
		validationThread.start();
	}
	
	@Inject
	private ExtensionRetriever er;
	
	public List<IResourceValidator> getValidators(){
		if(validators == null){
			validators = er.retrieveExtensionObjects(
					"eu.cloudscaleproject.env.common.notification.validator",
					"class", IResourceValidator.class);
		}
		return validators;
	}
	
	public boolean hasValidator(String id){
		boolean validatorFound = false;
		for (IResourceValidator v : getValidators()) {
			if (v.getID().equals(id)) {
				validatorFound = true;
			}
		}
		
		return validatorFound;
	}
	
	public void validate(IProject project, IValidationStatusProvider statusProvider){
		doValidate(project, statusProvider);
	}
	
	public void validateAsync(final IProject project, final IValidationStatusProvider statusProvider){
		Runnable r = new Runnable() {
			
			@Override
			public void run() {
				doValidate(project, statusProvider);
			}
		};
		
		synchronized (validationTasks) {
			validationTasks.add(r);
			validationTasks.notify();
		}
	}
	
	private void doValidate(IProject project, IValidationStatusProvider statusProvider) {

		if(project == null){
			logger.severe("Project is NULL! Can not validate: " + statusProvider.toString());
		}
		
		boolean validatorFound = false;
		for (IResourceValidator v : getValidators()) {				
			if (v.getID().equals(statusProvider.getID())) {
				validatorFound = true;
				v.validate(project, statusProvider);
			}
		}

		if (!validatorFound) {
			logger.warning("Validator not found or it is not registered in extension point! ToolID: "
					+ statusProvider.getID());
		}
	}
	
	public synchronized void validateAll(IProject project){
		for(Entry<IValidationStatusProvider, IProject> entry : statusProviders.entrySet()){
			try{
				if(project.equals(entry.getValue())){
					validate(project, entry.getKey());
				}
			}
			catch(NullPointerException e){
				//skip
			}
		}
	}
	
	public synchronized void validateAllAsync(IProject project){
		for(Entry<IValidationStatusProvider, IProject> entry : statusProviders.entrySet()){
			try{
				if(project.equals(entry.getValue())){
					validateAsync(project, entry.getKey());
				}
			}
			catch(NullPointerException e){
				//skip
			}
		}
	}
}
