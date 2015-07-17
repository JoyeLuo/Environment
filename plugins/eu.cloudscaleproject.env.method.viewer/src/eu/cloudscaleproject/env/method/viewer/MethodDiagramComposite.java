package eu.cloudscaleproject.env.method.viewer;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.HashMap;

import org.eclipse.core.resources.IProject;
import org.eclipse.draw2d.FigureCanvas;
import org.eclipse.emf.common.command.CommandStack;
import org.eclipse.emf.common.util.TreeIterator;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.emf.transaction.RecordingCommand;
import org.eclipse.emf.transaction.TransactionalEditingDomain;
import org.eclipse.gef.editparts.ZoomManager;
import org.eclipse.graphiti.mm.pictograms.PictogramElement;
import org.eclipse.graphiti.ui.editor.DiagramComposite;
import org.eclipse.graphiti.ui.editor.IDiagramEditorInput;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ControlEvent;
import org.eclipse.swt.events.ControlListener;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;

import eu.cloudscaleproject.env.common.BatchExecutor;
import eu.cloudscaleproject.env.common.notification.IValidationStatus;
import eu.cloudscaleproject.env.common.notification.IValidationStatusListener;
import eu.cloudscaleproject.env.common.notification.IValidationStatusProvider;
import eu.cloudscaleproject.env.common.notification.ResourceValidationStatus;
import eu.cloudscaleproject.env.common.notification.StatusManager;
import eu.cloudscaleproject.env.common.notification.diagram.IValidationDiagram;
import eu.cloudscaleproject.env.method.common.method.MethodFactory;
import eu.cloudscaleproject.env.method.common.method.Requirement;
import eu.cloudscaleproject.env.method.common.method.Section;
import eu.cloudscaleproject.env.method.common.method.StatusNode;
import eu.cloudscaleproject.env.method.common.method.Warning;
import eu.cloudscaleproject.env.method.viewer.diagram.IDiagramView;

public class MethodDiagramComposite extends DiagramComposite implements IValidationDiagram{
		
	private boolean isInitilized = false;
	private IDiagramView part;
	private IProject project;
		
	private HashMap<String, StatusNode> nodes = new HashMap<String, StatusNode>();
	
	private HashMap<String, IValidationStatusProvider> providerBindings = new HashMap<String, IValidationStatusProvider>();
	private HashMap<String, IValidationStatus> statusBindings = new HashMap<String, IValidationStatus>();
	
	PropertyChangeListener statusManagerListener = new PropertyChangeListener() {
		
		@Override
		public void propertyChange(PropertyChangeEvent pce) {
			if(StatusManager.PROP_STATUS_PROVIDER_REMOVED.equals(pce.getPropertyName())){
				IValidationStatusProvider statusProvider = (IValidationStatusProvider)pce.getOldValue();
				
				synchronized (MethodDiagramComposite.this) {
					IValidationStatusProvider bindedProvider = providerBindings.get(statusProvider.getID());
					if(statusProvider.equals(bindedProvider)){
						unbindStatusProvider(statusProvider.getID());
					}
				}
				
			}
		}
	};
	
	PropertyChangeListener statusProviderListener = new PropertyChangeListener() {
		
		@Override
		public void propertyChange(PropertyChangeEvent evt) {
			
			if(IValidationStatusProvider.PROP_STATUS_ADDED.equals(evt.getPropertyName())){
				IValidationStatusProvider statusProvider = (IValidationStatusProvider)evt.getSource();
				
				synchronized (MethodDiagramComposite.this) {
					IValidationStatusProvider bindedProvider = providerBindings.get(statusProvider.getID());
					IValidationStatus status = (IValidationStatus)evt.getNewValue();
					if(statusProvider.equals(bindedProvider)){
						bindStatus(status);
					}
				}
				
			}
			if(IValidationStatusProvider.PROP_STATUS_REMOVED.equals(evt.getPropertyName())){
				IValidationStatusProvider statusProvider = (IValidationStatusProvider)evt.getSource();
				
				synchronized (MethodDiagramComposite.this) {
					IValidationStatusProvider bindedProvider = providerBindings.get(statusProvider.getID());
					IValidationStatus status = (IValidationStatus)evt.getOldValue();
					if(statusProvider.equals(bindedProvider)){
						unbindStatus(status);
					}
				}
			}
			refresh();
		}
	};
	
	IValidationStatusListener statuslistener = new IValidationStatusListener() {
		@Override
		public void propertyChange(final PropertyChangeEvent pce) {
			
			final TransactionalEditingDomain editingDomain = getDiagramTypeProvider().getDiagramBehavior().getEditingDomain();
			final CommandStack commandStack = editingDomain.getCommandStack();
			
			commandStack.execute(new RecordingCommand((TransactionalEditingDomain) editingDomain){
				
				protected void doExecute(){
					
					IValidationStatus status = (IValidationStatus)pce.getSource();
					StatusNode statusNode = nodes.get(status.getID());
					
					if(statusNode == null){
						return;
					}
					
					synchronized (statusNode) {
						if(IValidationStatus.PROP_WARNING_ADD.equals(pce.getPropertyName())){
							String id = (String)pce.getNewValue();
							Warning w = MethodFactory.eINSTANCE.createWarning();
							w.setId(id);
							w.setSeverity(status.getWarningType(id));
							w.setMessage(status.getWarningMessage(id));
							statusNode.getWarnings().add(w);
						}
						if(IValidationStatus.PROP_WARNING_REMOVE.equals(pce.getPropertyName())){
							Warning warning = null;
							for(Warning w : statusNode.getWarnings()){
								if(pce.getOldValue().equals(w.getId())){
									warning = w;
									break;
								}
							}
							if(warning != null){
								statusNode.getWarnings().remove(warning);
							}
						}
						if(IValidationStatus.PROP_DIRTY.equals(pce.getPropertyName())){
							statusNode.setDirty((boolean)pce.getNewValue());
						}
						if(IValidationStatus.PROP_NAME.equals(pce.getPropertyName())){
							statusNode.setInstanceName((String)pce.getNewValue());
						}
						
						statusNode.setDone(status.isDone());
					}
					
					refresh();
				}
				
			});
		}
	};
	
	public MethodDiagramComposite(IDiagramView part, Composite parent){
		
		super(parent, SWT.NONE);
		this.part = part;		
		StatusManager.getInstance().addPropertyChangeListener(statusManagerListener);
	}
	
	@Override
	public IProject getProject() {
		return project;
	}

	@Override
	public void setProject(IProject project) {
		this.project = project;
	}
	
	@Override
	public void setInput(IDiagramEditorInput input) {
		super.setInput(input);
		initialize();
	}
	
	@Override
	public void setInput(TransactionalEditingDomain editingDomain, IDiagramEditorInput input) {
		super.setInput(editingDomain, input);
		initialize();
	}
	
	private void initialize(){
				
		TreeIterator<EObject> iter = EcoreUtil.getAllContents(getDiagramTypeProvider().getDiagram(), true);
		while(iter.hasNext()){
			EObject eobject = iter.next();
			Object bo = null;
			
			if(eobject instanceof PictogramElement){
				bo = getDiagramTypeProvider().getFeatureProvider().getBusinessObjectForPictogramElement((PictogramElement)eobject);
			}
			
			if(bo instanceof StatusNode){
				StatusNode node = (StatusNode)bo;
				nodes.put(node.getId(), node);
			}
		}
		
		addDisposeListener(new DisposeListener() {
			
			@Override
			public void widgetDisposed(DisposeEvent e) {
				StatusManager.getInstance().removePropertyChangeListener(statusManagerListener);

				for(IValidationStatusProvider statusProvider : providerBindings.values()){
					statusProvider.removeStatusChangeListener(statusProviderListener);
				}
				for(IValidationStatus status : statusBindings.values()){
					status.removeListener(statuslistener);
				}
				providerBindings.clear();
				statusBindings.clear();
			}
		});
		
		isInitilized = true;
	}
	
	@Override
	public void show() {
		this.part.setTopDiagram(this);
	}
	
	public void refresh(){
		BatchExecutor.getInstance().addTask(MethodDiagramComposite.class.getName() + ".refresh", new Runnable() {
			@Override
			public void run() {
				Display.getDefault().asyncExec(new Runnable() {
					
					@Override
					public void run() {
						if(getDiagramTypeProvider().getDiagramBehavior() != null){
							getDiagramTypeProvider().getDiagramBehavior().refreshContent();
						}
					}
				});
			}
		});
		
	}
	
	public synchronized void bindStatusProvider(String id, IValidationStatusProvider statusProvider){
		
		if(!isInitilized){
			return;
		}
		
		unbindStatusProvider(id);
		providerBindings.remove(id);
		
		if(statusProvider == null){
			refresh();
			return;
		}
		
		bindStatus(statusProvider.getSelfStatus());
		for(IValidationStatus status : statusProvider.getSubStatuses()){
			bindStatus(status);
		}
		
		statusProvider.addStatusChangeListener(statusProviderListener);
		providerBindings.put(statusProvider.getID(), statusProvider);
		
		refresh();
	}
	
	public synchronized void unbindStatusProvider(String id){

		if(!isInitilized){
			return;
		}
		
		IValidationStatusProvider statusProvider = providerBindings.get(id);
		if(statusProvider == null){
			return;
		}
		
		unbindStatus(statusProvider.getSelfStatus());
		for(IValidationStatus status : statusProvider.getSubStatuses()){
			unbindStatus(status);
		}
		
		statusProvider.removeStatusChangeListener(statusProviderListener);
		providerBindings.remove(statusProvider.getID());
		
		refresh();
	}
	
	private String getStatusDiagramUniqueID(IValidationStatus status){
		String providerID = status.getProvider() != null ? status.getProvider().getID() : "";
		return providerID + status.getID();
	}
	
	public synchronized void bindStatus(IValidationStatus status){
		
		if(!isInitilized){
			return;
		}
		
		if(status == null){
			return;
		}
		
		IValidationStatus old = statusBindings.get(getStatusDiagramUniqueID(status));
		if(old != null){
			unbindStatus(old);
		}
		
		status.addListener(statuslistener);
		StatusNode node = nodes.get(status.getID());
		
		if(node != null){
			bind(node, status);
		}
		statusBindings.put(getStatusDiagramUniqueID(status), status);
		
		refresh();
	}
	
	public synchronized void unbindStatus(IValidationStatus status){
		
		if(!isInitilized){
			return;
		}
		
		if(status == null){
			return;
		}
		
		status.removeListener(statuslistener);
		
		StatusNode node = nodes.get(status.getID());
		if(node != null){
			bind(node, null);
		}
		statusBindings.remove(getStatusDiagramUniqueID(status));
		
		refresh();
	}
	
	public synchronized IValidationStatusProvider getActiveStatusProvider(String id){
		return providerBindings.get(id);
	}
	
	private void bind(final StatusNode statusNode, final IValidationStatus status){
		
		assert(statusNode != null);
		
		if(status == null){
			//clear status
			final TransactionalEditingDomain editingDomain = getDiagramTypeProvider().getDiagramBehavior().getEditingDomain();
			final CommandStack commandStack = editingDomain.getCommandStack();
			
			commandStack.execute(new RecordingCommand((TransactionalEditingDomain) editingDomain) {
				
				protected void doExecute() {
					
					synchronized (statusNode) {
						statusNode.setInstanceName("");
						statusNode.setSource(null);
						statusNode.setDirty(false);
						statusNode.setDone(false);
						statusNode.getWarnings().clear();
						
						if(statusNode instanceof Section){
							Section section = (Section)statusNode;
							section.setInProgress(false);
						}
						
						if(statusNode instanceof Requirement){
							Requirement req = (Requirement)statusNode;
							req.setResource(null);
						}
					}
				}
			});
			return;
		}
		
		final TransactionalEditingDomain editingDomain = getDiagramTypeProvider().getDiagramBehavior().getEditingDomain();
		final CommandStack commandStack = editingDomain.getCommandStack();
		
		commandStack.execute(new RecordingCommand((TransactionalEditingDomain) editingDomain) {
			
			protected void doExecute() {
				
				synchronized (statusNode) {
					statusNode.setInstanceName(status.getName());
					statusNode.setSource(status);
					statusNode.setDirty(status.isDirty());
					statusNode.setDone(status.isDone());
					if(statusNode instanceof Section){
						Section section = (Section)statusNode;
						section.setInProgress(true);
					}
					setWarnings(statusNode, status);
					
					if(statusNode instanceof Requirement){
						if(status instanceof ResourceValidationStatus){
							Requirement req = (Requirement)statusNode;
							req.setResource(((ResourceValidationStatus) status).getResource());
						}
					}
				}
			}
		});		
	}
	
	private void setWarnings(StatusNode node, IValidationStatus status){
		synchronized (node) {
			node.getWarnings().clear();
			for(String id : status.getWarningIDs()){
				Warning w = MethodFactory.eINSTANCE.createWarning();
				w.setId(id);
				w.setSeverity(status.getWarningType(id));
				w.setMessage(status.getWarningMessage(id));
				node.getWarnings().add(w);
			}
		}
	}

	//ZOOM Workaround!
	
	private final Object lock = new Object();
	private boolean resize = false;
	
	public void initializeGraphicalViewer() {
		super.initializeGraphicalViewer();
				
		//set zoom
		final ZoomManager zoomManager = (ZoomManager) getAdapter(ZoomManager.class);
		zoomManager.setZoomAnimationStyle(ZoomManager.ANIMATE_NEVER);
		
		getGraphicalViewer().getControl().addControlListener(new ControlListener() {
			
			@Override
			public void controlResized(ControlEvent e) {
				synchronized (lock) {
					resize = true;
					lock.notifyAll();
				}
			}
			
			@Override
			public void controlMoved(ControlEvent e) {
			}
		});
		
		if(getGraphicalViewer().getControl() instanceof FigureCanvas){
			FigureCanvas fc = (FigureCanvas)getGraphicalViewer().getControl();
			
			fc.setVerticalScrollBarVisibility(FigureCanvas.NEVER);
			fc.setHorizontalScrollBarVisibility(FigureCanvas.NEVER);
		}
		
		Thread t = new Thread(new Runnable() {
			
			@Override
			public void run() {
				while(!Thread.currentThread().isInterrupted()){
					try {
						synchronized (lock) {
							if(!resize){
								lock.wait();
							}
							resize = false;
						}
						
						try {
							Thread.sleep(300);
						} catch (InterruptedException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						
						Display.getDefault().asyncExec(new Runnable() {
							public void run() {
								zoomManager.setZoomAsText(ZoomManager.FIT_ALL);
							}
						});
						
						try {
							Thread.sleep(1000);
						} catch (InterruptedException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						
					} catch (InterruptedException e) {
					}
					
				}
			}
		});
		t.start();
	}
}
