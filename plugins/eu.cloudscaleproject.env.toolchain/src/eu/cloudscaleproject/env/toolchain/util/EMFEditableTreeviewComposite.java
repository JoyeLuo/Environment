package eu.cloudscaleproject.env.toolchain.util;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.Path;
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
import org.eclipse.swt.widgets.Tree;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.ide.IDE;
import org.eclipse.ui.views.properties.IPropertySheetPage;
import org.eclipse.ui.views.properties.PropertySheetPage;

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
		public void propertyChange(PropertyChangeEvent evt) {
			treeViewer.refresh();
		}
	};

	/**
	 * Create the composite.
	 * 
	 * @param parent
	 * @param style
	 */
	public EMFEditableTreeviewComposite(EditorInputEMF ca, Composite parent, int style)
	{
		super(parent, style);

		this.alternative = ca;

		setLayout(new GridLayout(1, false));

		this.tree = new Tree(this, SWT.BORDER | SWT.V_SCROLL);
		this.tree.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
		this.treeViewer = new TreeViewer(tree);
		this.treeViewer.addDoubleClickListener(new IDoubleClickListener()
		{
			@Override
			public void doubleClick(DoubleClickEvent event)
			{
				IFile file = getSelectedDiagramFile();
				if (file == null)
					file = getSelectedModelFile();

				if (file != null)
					openEditor(file);
			}
		});

		contentProvider = new AdapterFactoryContentProvider(alternative.getAdapterFactory());
		this.treeViewer.setContentProvider(contentProvider);
		this.treeViewer.setLabelProvider(new AdapterFactoryLabelProvider(alternative.getAdapterFactory()));

		new AdapterFactoryTreeEditor(tree, alternative.getAdapterFactory());

		menuSupport = new EMFPopupMenuSupport(alternative.getEditingDomain())
		{
			@Override
			public void menuAboutToShow(IMenuManager menuManager)
			{
				menuManager.add(createOpenMenuManager());
				menuManager.add(new Separator("default"));
				super.menuAboutToShow(menuManager);

			}
		};
		menuSupport.setViewer(treeViewer);

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

		this.treeViewer.setInput(alternative.getResourceSet());
		this.treeViewer.expandToLevel(2);
		this.treeViewer.refresh();
		
		alternative.addPropertyChangeListener(EditorInputEMF.PROP_LOADED, editorInputListener);
		addDisposeListener(new DisposeListener() {
			
			@Override
			public void widgetDisposed(DisposeEvent e) {
				alternative.removePropertyChangeListener(editorInputListener);
			}
		});
	}

	private MenuManager createOpenMenuManager()
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
	
	private IFile getSelectedModelFile()
	{
		ISelection s = treeViewer.getSelection();
		Object element = ((StructuredSelection) s).getFirstElement();
		
		System.out.println(element);
		
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

	private IFile getSelectedDiagramFile ()
	{
		IFile modelFile = getSelectedModelFile();

		if (modelFile == null) return null;

		String path = modelFile.getName() + "_diagram";

        IFile diagramFile = modelFile.getParent().getFile(new Path(path));
        if (diagramFile.exists())
        {
                return diagramFile;
        }
        else
        {
                diagramFile = modelFile.getParent().getParent().getFile(new Path(path));
                if (diagramFile.exists())
                {
                	return diagramFile;
                }
        }
        
        return null;
	}


	private void openEditor(IFile file)
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
		PropertySheetPage propertySheetPage = new ExtendedPropertySheetPage((AdapterFactoryEditingDomain) alternative.getEditingDomain());
		propertySheetPage.setPropertySourceProvider(contentProvider);
		return propertySheetPage;
	}
}