package eu.cloudscaleproject.env.analyser.editors.composite;

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
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.ide.IDE;
import org.eclipse.ui.views.properties.IPropertySheetPage;
import org.eclipse.ui.views.properties.PropertySheetPage;

import eu.cloudscaleproject.env.analyser.alternatives.ConfAlternative;
import eu.cloudscaleproject.env.common.explorer.ExplorerProjectPaths;
import eu.cloudscaleproject.env.toolchain.IDirtyAdapter;
import eu.cloudscaleproject.env.toolchain.IPropertySheetPageProvider;
import eu.cloudscaleproject.env.toolchain.ProjectEditorSelectionService;
import eu.cloudscaleproject.env.toolchain.util.EMFPopupMenuSupport;
import eu.cloudscaleproject.env.toolchain.util.ISaveableComposite;

public class ConfigAlternativeTreeviewComposite extends Composite implements ISaveableComposite, IPropertySheetPageProvider{

	private final IEditorPart editor;
	private final IDirtyAdapter dirtyAdapter;
	private final ConfAlternative alternative;
	
	private final Tree tree;
	private final TreeViewer treeViewer;
	private final AdapterFactoryContentProvider contentProvider;
	private final EMFPopupMenuSupport menuSupport;
	
	/**
	 * Create the composite.
	 * @param parent
	 * @param style
	 */
	public ConfigAlternativeTreeviewComposite(IEditorPart editor, ConfAlternative ca, Composite parent, int style) {
		super(parent, style);
		
		this.editor = editor;
		this.dirtyAdapter = (IDirtyAdapter)ConfigAlternativeTreeviewComposite.this.editor.getAdapter(IDirtyAdapter.class);
		this.alternative = ca;
		
		setLayout(new GridLayout(1, false));
		
		this.tree = new Tree(this, SWT.BORDER | SWT.V_SCROLL);
		this.tree.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
		this.treeViewer = new TreeViewer(tree);
		this.treeViewer.addDoubleClickListener(new IDoubleClickListener() {
			
			@Override
			public void doubleClick(DoubleClickEvent event) {
				ISelection s = treeViewer.getSelection();
				Object element = ((StructuredSelection)s).getFirstElement();
				if (element instanceof EObject) {
					EObject eo = (EObject)element;
					Resource res = eo.eResource();
					IFile file = ExplorerProjectPaths.getFileFromEmfResource(res);
					
					String path = res.getURI().lastSegment() + "_diagram";
					IFile diagramFile = file.getParent().getParent().getFile(new Path(path));
					
					if(diagramFile.exists()){
						file = diagramFile;
					}
					
					try {
						IDE.openEditor(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage(), file);
					} catch (PartInitException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}
		});
		
		contentProvider = new AdapterFactoryContentProvider(alternative.getAdapterFactory());
		this.treeViewer.setContentProvider(contentProvider);
		this.treeViewer.setLabelProvider(new AdapterFactoryLabelProvider(alternative.getAdapterFactory()));
		//this.treeViewer.addFilter(new ModelViewFilter());
		
		final PropertyChangeListener listener = new PropertyChangeListener() {
			@Override
			public void propertyChange(PropertyChangeEvent evt) {
				dirtyAdapter.fireDirtyState();
			}
		};
		
		alternative.addPropertyChangeListener(listener);
		addDisposeListener(new DisposeListener() {
			
			@Override
			public void widgetDisposed(DisposeEvent e) {
				alternative.removePropertyChangeListener(listener);
			}
		});
				
		new AdapterFactoryTreeEditor(tree, alternative.getAdapterFactory());
		
		menuSupport = new EMFPopupMenuSupport(alternative.getEditingDomain());
		menuSupport.setViewer(treeViewer);		
	}
	
	private void updateTreeview(){
		
		alternative.load();
		
		if(!this.tree.isDisposed()){
			this.treeViewer.setInput(alternative.getResourceSet());
			this.treeViewer.expandToLevel(2);
			this.treeViewer.refresh();
		}
	}
	
	public void update(){
		alternative.load();
		if(!this.isDisposed()){
			updateTreeview();
		}
		
		ProjectEditorSelectionService.getInstance().setSelectionProviderDelegate(treeViewer);
		
		super.update();
	}

	@Override
	public IPropertySheetPage getPropertySheetPage() {
		PropertySheetPage propertySheetPage = new ExtendedPropertySheetPage((AdapterFactoryEditingDomain) alternative.getEditingDomain());
		propertySheetPage.setPropertySourceProvider(contentProvider);
		return propertySheetPage;
	}

	@Override
	public void save() {
		alternative.save();
	}

	@Override
	public void load() {
		updateTreeview();
	}

	@Override
	public boolean isDirty() {
		return alternative.isDirty();
	}
}
