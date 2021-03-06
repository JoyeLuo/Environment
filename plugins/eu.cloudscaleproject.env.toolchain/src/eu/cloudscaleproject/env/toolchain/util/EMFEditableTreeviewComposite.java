package eu.cloudscaleproject.env.toolchain.util;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.Path;
import org.eclipse.e4.ui.workbench.modeling.ESelectionService;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.edit.domain.AdapterFactoryEditingDomain;
import org.eclipse.emf.edit.ui.celleditor.AdapterFactoryTreeEditor;
import org.eclipse.emf.edit.ui.provider.AdapterFactoryContentProvider;
import org.eclipse.emf.edit.ui.provider.AdapterFactoryLabelProvider;
import org.eclipse.emf.edit.ui.view.ExtendedPropertySheetPage;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.ViewerFilter;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.FocusListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.ide.IDE;
import org.eclipse.ui.views.properties.IPropertySheetPage;
import org.palladiosimulator.mdsdprofiles.provider.StereotypableElementDecoratorAdapterFactory;

import eu.cloudscaleproject.env.common.CloudscaleContext;
import eu.cloudscaleproject.env.common.explorer.ExplorerProjectPaths;
import eu.cloudscaleproject.env.toolchain.IPropertySheetPageProvider;
import eu.cloudscaleproject.env.toolchain.ProjectEditorSelectionService;
import eu.cloudscaleproject.env.toolchain.resources.types.EditorInputEMF;

public class EMFEditableTreeviewComposite extends Composite implements IPropertySheetPageProvider
{

	private final EditorInputEMF alternative;

	private final Tree tree;
	private final TreeViewer treeViewer;
	private final AdapterFactoryContentProvider contentProvider;
	private final EMFPopupMenuSupport menuSupport;
	
	private final PropertyChangeListener editorInputListener = new PropertyChangeListener() {
		
		@Override
		public void propertyChange(final PropertyChangeEvent evt) {
			
			if (EditorInputEMF.PROP_SUB_RESOURCE_CHANGED.equals(evt.getPropertyName())
					|| EditorInputEMF.PROP_LOADED.equals(evt.getPropertyName())){
								
				Display.getDefault().asyncExec(new Runnable() {

					@Override
					public void run() {
						if (!treeViewer.getTree().isDisposed()) {
							treeViewer.refresh(true);
						}
					}
				});
			}
		}
	};

	private ExtendedPropertySheetPage propertySheetPage;
	
	public EMFEditableTreeviewComposite(EditorInputEMF ca, Composite parent, int style)
	{
		this(ca, ca.getResourceSet(), null, parent, style);
	}
	
	public EMFEditableTreeviewComposite(EditorInputEMF ca, IEditorSite site, Composite parent, int style)
	{
		this(ca, ca.getResourceSet(), site, parent, style);
	}

	public EMFEditableTreeviewComposite(EditorInputEMF ca, Object input, IEditorSite site, Composite parent, int style)
	{
		super(parent, style);

		this.alternative = ca;

		setLayout(new GridLayout(1, false));

		this.tree = new Tree(this, SWT.BORDER | SWT.V_SCROLL | SWT.H_SCROLL);
		this.tree.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
		this.treeViewer = new TreeViewer(tree);
		this.treeViewer.addDoubleClickListener(new IDoubleClickListener()
		{
			@Override
			public void doubleClick(DoubleClickEvent event)
			{
				if(handleDoubleClick()){
					return;
				}
				
				//Execute default double-click behavior (locate file and open it, using default editor)
				IFile file = getSelectedDiagramFile();
				if (file == null)
					file = getSelectedModelFile();

				if (file != null)
					openEditor(file);
			}
		});

		contentProvider = new AdapterFactoryContentProvider(new StereotypableElementDecoratorAdapterFactory(alternative.getAdapterFactory()));

		this.treeViewer.setContentProvider(contentProvider);
		
		this.treeViewer.setLabelProvider(new org.eclipse.jface.viewers.DelegatingStyledCellLabelProvider(
				new AdapterFactoryLabelProvider.StyledLabelProvider(alternative.getAdapterFactory(), this.treeViewer)));
		
		//this is needed to show Profile/Stereotype/AT labels - it does not work with the StyledLabelProvider
		//this.treeViewer.setLabelProvider(new AdapterFactoryLabelProvider(
		//		new StereotypableElementDecoratorAdapterFactory(alternative.getAdapterFactory())));
		
		new AdapterFactoryTreeEditor(tree, alternative.getAdapterFactory());

		menuSupport = new EMFPopupMenuSupport(input, alternative.getEditingDomain())
		{
			@Override
			public void menuAboutToShow(IMenuManager menuManager)
			{
				menuManager.add(createOpenMenuManager());
				menuManager.add(new Separator("default"));
				
				IStructuredSelection selection = (IStructuredSelection)getTreeViewer().getSelection();
				EObject selectedElement = (EObject)selection.getFirstElement();
				
				super.menuAboutToShow(menuManager);
				
				menuManager.add(new Separator("default"));
				EMFEditableTreeviewComposite.this.menuAboutToShow(menuManager, selectedElement);
			}
		};
		
		menuSupport.setViewer(site, treeViewer);

		tree.addFocusListener(new FocusListener()
		{
			@Override
			public void focusLost(FocusEvent e)
			{
			}

			@Override
			public void focusGained(FocusEvent e)
			{
				ProjectEditorSelectionService.getInstance().setSelectionProviderDelegate(treeViewer);
			}
		});

		this.treeViewer.setInput(input);
		this.treeViewer.refresh(true);
		
		alternative.addPropertyChangeListener(editorInputListener);
		addDisposeListener(new DisposeListener() {
			
			@Override
			public void widgetDisposed(DisposeEvent e) {
				alternative.removePropertyChangeListener(editorInputListener);
			}
		});
		
		final ESelectionService selectionService = CloudscaleContext.getGlobalContext().get(ESelectionService.class);
		treeViewer.addSelectionChangedListener(new ISelectionChangedListener()
		{
			@Override
			public void selectionChanged(SelectionChangedEvent e)
			{
				selectionService.setSelection(e.getSelection());
			}
		});
		
		Display.getDefault().timerExec(100, new Runnable()
		{
			@Override
			public void run()
			{
				if(treeViewer.getTree().getItemCount() > 0){
					treeViewer.getTree().setSelection(treeViewer.getTree().getItem(0));
				}
				propertySheetPage.selectionChanged(null, treeViewer.getSelection());
			}
		});
	}
	
	public TreeViewer getTreeViewer(){
		return this.treeViewer;
	}
	
	protected void menuAboutToShow(IMenuManager menuManager, EObject selectedElement){
		// Override in subclasses to add additional popup actions.
	}

	protected MenuManager createOpenMenuManager()
	{
		MenuManager mm = new MenuManager("Open");

		mm.add(new Action("Editor")
		{
			@Override
			public void run()
			{
				openEditor(getSelectedModelFile());
			}
			@Override
			public boolean isEnabled()
			{
				return getSelectedModelFile() != null;
			}
		});

		mm.add(new Action("Diagram")
		{
			@Override
			public void run()
			{
				openEditor(getSelectedDiagramFile());
			}
			
			@Override
			public boolean isEnabled()
			{
				return getSelectedDiagramFile() != null;
			}
		});

		return mm;
	}
	
	protected boolean handleDoubleClick(){
		//override in subclasses
		return false;
	}
	
	public Object getSelectedObject(){
		
		ISelection s = treeViewer.getSelection();
		if(s == null){
			return null;
		}
		
		Object element = ((StructuredSelection) s).getFirstElement();
		return element;
	}
	
	public IFile getSelectedModelFile()
	{
		
		Object element = getSelectedObject();
				
		if (element instanceof Resource)
		{
			return ExplorerProjectPaths.getFileFromEmfResource((Resource)element);
		}

		if (element instanceof EObject)
		{
			EObject eo = (EObject) element;
			Resource res = eo.eResource();
			return ExplorerProjectPaths.getFileFromEmfResource(res);
		}
		

		return null;
	}

	public IFile getSelectedDiagramFile ()
	{
		IFile modelFile = getSelectedModelFile();

		if (modelFile == null) return null;

		String path = modelFile.getName() + "_diagram";

        IFile diagramFile = modelFile.getParent().getFile(new Path(path));
        if (diagramFile.exists())
        {
                return diagramFile;
        }
        else if(!(modelFile.getParent() instanceof IProject))
        {
                diagramFile = modelFile.getParent().getParent().getFile(new Path(path));
                if (diagramFile.exists())
                {
                	return diagramFile;
                }
        }
        
        return null;
	}


	protected void openEditor(IFile file)
	{
        try
        {
                IDE.openEditor(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage(), file);
        }
        catch (PartInitException e)
        {
                e.printStackTrace();
        }

	}

	public void addFilter(ViewerFilter filter)
	{
		this.treeViewer.addFilter(filter);
	}

	@Override
	public IPropertySheetPage getPropertySheetPage()
	{
		if (propertySheetPage == null)
		{
			propertySheetPage = new ExtendedPropertySheetPage((AdapterFactoryEditingDomain) alternative.getEditingDomain())
			{
				private ISelection selection;
				private IWorkbenchPart part;

				@Override
				public void createControl(Composite parent)
				{
					super.createControl(parent);

					if (selection != null) super.selectionChanged(part, selection);
				}
				
				@Override
				public void selectionChanged(IWorkbenchPart part, ISelection selection)
				{
					super.selectionChanged(part, selection);
					this.part = part;
					this.selection = selection;
				}
			};

			propertySheetPage.setPropertySourceProvider(contentProvider);
		}

		return propertySheetPage;
	}
}
