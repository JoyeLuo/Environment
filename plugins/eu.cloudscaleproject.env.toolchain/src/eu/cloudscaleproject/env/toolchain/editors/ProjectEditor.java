package eu.cloudscaleproject.env.toolchain.editors;

import java.util.HashSet;
import java.util.List;
import java.util.logging.Logger;

import javax.inject.Inject;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceChangeEvent;
import org.eclipse.core.resources.IResourceChangeListener;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.e4.core.contexts.IEclipseContext;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CTabFolder;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.part.EditorPart;
import org.eclipse.ui.views.properties.IPropertySheetPage;

import eu.cloudscaleproject.env.common.CloudscaleContext;
import eu.cloudscaleproject.env.common.explorer.ExplorerProjectPaths;
import eu.cloudscaleproject.env.toolchain.IDirtyAdapter;
import eu.cloudscaleproject.env.toolchain.ProjectEditorExtension;
import eu.cloudscaleproject.env.toolchain.ProjectEditorSelectionService;
import eu.cloudscaleproject.env.toolchain.ToolchainUtils;
import eu.cloudscaleproject.env.toolchain.resources.ResourceProvider;
import eu.cloudscaleproject.env.toolchain.resources.ResourceRegistry;
import eu.cloudscaleproject.env.toolchain.resources.types.EditorInputJob;
import eu.cloudscaleproject.env.toolchain.resources.types.IEditorInputResource;

public class ProjectEditor extends EditorPart implements IDirtyAdapter{

	private static final Logger logger = Logger.getLogger(ProjectEditor.class.getName());	
	public static final String ID = "eu.cloudscaleproject.env.toolchain.ToolchainEditorPart";
		
	private CTabFolder tabFolder;
	
	@Inject
	private ProjectEditorProvider editorProvider;
	
	private IResourceChangeListener editorCloser = new IResourceChangeListener() {
        public void resourceChanged(IResourceChangeEvent event) {
            final IResource closingProject = event.getResource();
            Display.getDefault().asyncExec(new Runnable(){
                public void run() {
                    for (IWorkbenchPage page : getSite().getWorkbenchWindow().getPages()) {
                    	IProject p = ExplorerProjectPaths.getProject(ProjectEditor.this);
                        if (p.equals(closingProject)) page.closeEditor(page.findEditor(ProjectEditor.this.getEditorInput()), true);
                    }
                }
            });
        }   
    };
		
	public ProjectEditor() {
		CloudscaleContext.inject(this);
	}
	
	@Override
	public String getPartName() {
		IProject p = ExplorerProjectPaths.getProject(this);
		return "Dashboard ("+p.getName()+")";
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public <T> T getAdapter(Class<T> adapter) {
		
		if (adapter.equals(IPropertySheetPage.class)) {
			for(ProjectEditorExtension pee : editorProvider.getToolExtensions()){
				if(tabFolder.getSelection() != null && tabFolder.getSelection().equals(pee.getTabItem())){
					return (T)pee.getPropertySheetPage();
				}
			}
			return super.getAdapter(adapter);
		}
		else if(adapter.equals(IDirtyAdapter.class)){
			return (T)this;
		}
		else {
			return super.getAdapter(adapter);
		}
	}
	
	public CTabFolder getTabFolder(){
		return tabFolder;
	}
	
	public void openTab(String editorExtensionID, String action){
		if(tabFolder != null){
			ProjectEditorExtension pee = editorProvider.getToolExtension(editorExtensionID);
			if(pee != null){
				tabFolder.setSelection(pee.getTabItem());
				pee.handleAction(action);				
			}
			else{
				logger.warning("Can't open tab item! Can't find extension pont!");
			}
		}
	}
	
	@Override
	public void fireDirtyState() {
		firePropertyChange(IEditorPart.PROP_DIRTY);
	}

	@Override
	public void doSave(IProgressMonitor monitor) {
		for(ProjectEditorExtension pee : editorProvider.getToolExtensions()){
			pee.save(monitor);
		}
		fireDirtyState();
	}

	@Override
	public void doSaveAs() {
		// saveAs is not allowed!
	}

	@Override
	public void init(IEditorSite site, IEditorInput input)
			throws PartInitException {

		ResourcesPlugin.getWorkspace().addResourceChangeListener(editorCloser, IResourceChangeEvent.PRE_CLOSE | IResourceChangeEvent.PRE_DELETE);
		setSite(site);
		setInputWithNotify(input);
		
		IEclipseContext context = (IEclipseContext)site.getService(IEclipseContext.class);
		context.set(IProject.class, ExplorerProjectPaths.getProject(this));
	}

	@Override
	public boolean isDirty() {
		for(ProjectEditorExtension pee : editorProvider.getToolExtensions()){
			if(pee.isDirty()){
				return true;
			}
		}
		return false;
	}

	@Override
	public boolean isSaveAsAllowed() {
		return false;
	}

	@Override
	public void createPartControl(Composite parent) {
		
		tabFolder = new CTabFolder(parent, SWT.BORDER | SWT.FLAT);
		tabFolder.setTabHeight(40);
		tabFolder.setSelectionBackground(Display.getCurrent().getSystemColor(SWT.COLOR_TITLE_INACTIVE_BACKGROUND_GRADIENT));
		
		final List<ProjectEditorExtension> editors = editorProvider.getToolExtensions();
		
		for(ProjectEditorExtension tee : editors){
			tee.createTabItem(this);
		}
		
		if(!editorProvider.getToolExtensions().isEmpty()){
			tabFolder.setSelection(editorProvider.getToolExtensions().get(0).getTabItem());
		}
		
		tabFolder.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				
				//trigger set focus on tab change!
				for(ProjectEditorExtension pee : editorProvider.getToolExtensions()){
					if(pee.getTabItem() == tabFolder.getSelection()){
						pee.setFocus();
					}
				}
			}
		});
		
		getSite().setSelectionProvider(ProjectEditorSelectionService.getInstance());
		
		//load all resources
		EditorInputJob job = new EditorInputJob("Loading Dashboard resources...") {
			
			@Override
			public IStatus execute(IProgressMonitor monitor) {
				
				monitor.beginTask("Loading Dasboard resources", IProgressMonitor.UNKNOWN);
				for(ProjectEditorExtension pee : editorProvider.getToolExtensions()){
					pee.load(monitor);
				}
				return Status.OK_STATUS;
			}
		};
		
		job.setUser(true);
		job.schedule();
	}
	
	@Override
	public void dispose() {
		
		//discard editor changes
		final HashSet<IEditorInputResource> openedAlternatives = ToolchainUtils.getOpenedAlternatives();
		final IProject project = ExplorerProjectPaths.getProject(this);
		
		EditorInputJob job = new EditorInputJob("Discarding changes...") {
			
			@Override
			public IStatus execute(IProgressMonitor monitor) {
				monitor.beginTask("Discarding editor changes", IProgressMonitor.UNKNOWN);
				
				for(ResourceProvider rp : ResourceRegistry.getInstance().getResourceProviders(project)){
					for(IEditorInputResource eir : rp.getResources()){
						if(!openedAlternatives.contains(eir) && eir.isDirty()){
							eir.load();
						}
					}
				}
				
				monitor.done();
				return Status.OK_STATUS;
			}
		};
		job.setUser(false);
		job.schedule();
		
        ResourcesPlugin.getWorkspace().removeResourceChangeListener(editorCloser);
		super.dispose();
	}

	@Override
	public void setFocus() {
		
		for(ProjectEditorExtension pee : editorProvider.getToolExtensions()){
			if(tabFolder.getSelection() != null && tabFolder.getSelection().equals(pee.getTabItem())){
				pee.setFocus();
			}
		}		
	}
}
