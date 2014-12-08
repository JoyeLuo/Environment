package eu.cloudscaleproject.env.analyser.editors;

import java.io.IOException;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.emf.ecore.util.EcoreAdapterFactory;
import org.eclipse.emf.edit.provider.ComposedAdapterFactory;
import org.eclipse.emf.edit.provider.ReflectiveItemProviderAdapterFactory;
import org.eclipse.emf.edit.provider.resource.ResourceItemProviderAdapterFactory;
import org.eclipse.emf.edit.ui.provider.AdapterFactoryContentProvider;
import org.eclipse.emf.edit.ui.provider.AdapterFactoryLabelProvider;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Tree;
import org.palladiosimulator.edp2.models.ExperimentData.Measurements;
import org.palladiosimulator.edp2.models.ExperimentData.provider.ExperimentDataItemProviderAdapterFactory;

import eu.cloudscaleproject.env.common.explorer.ExplorerProjectPaths;

public class ResultsComposite extends Composite{
	
	private final Tree tree;
	private final TreeViewer treeViewer;
	
	private final IProject project;

	public ResultsComposite(IProject project, Composite parent, int style) {
		super(parent, style);
		this.project = project;

		setLayout(new GridLayout(1, false));
		
		this.tree = new Tree(this, SWT.BORDER);
		this.tree.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
		this.treeViewer = new TreeViewer(tree);
		
		ComposedAdapterFactory adapterFactory = new ComposedAdapterFactory(ComposedAdapterFactory.Descriptor.Registry.INSTANCE);
		adapterFactory.addAdapterFactory(new ResourceItemProviderAdapterFactory());
		adapterFactory.addAdapterFactory(new ExperimentDataItemProviderAdapterFactory());
		adapterFactory.addAdapterFactory(new ReflectiveItemProviderAdapterFactory());
		adapterFactory.addAdapterFactory(new EcoreAdapterFactory());
		
		this.treeViewer.setContentProvider(new AdapterFactoryContentProvider(adapterFactory));
		this.treeViewer.setLabelProvider(new AdapterFactoryLabelProvider(adapterFactory));
		
		this.treeViewer.addDoubleClickListener(new IDoubleClickListener() {
			
			@Override
			public void doubleClick(DoubleClickEvent event) {
				ISelection s = treeViewer.getSelection();
				Object element = ((StructuredSelection)s).getFirstElement();
				if (element instanceof Measurements) {
					//TODO: Open measurements
				}
				
			}
		});
		
		loadModel();
	}
	
	private void loadModel(){
		IFolder analyserFolder = ExplorerProjectPaths.getProjectFolder(project, ExplorerProjectPaths.KEY_FOLDER_ANALYSER);
		IFolder resFolder = analyserFolder.getFolder(ExplorerProjectPaths.getProjectProperty(project, ExplorerProjectPaths.KEY_FOLDER_RESULTS));
		
		IFile modelFile = null;
		try {
			for(IResource res : resFolder.members()){
				if("edp2".equals(res.getFileExtension()) && res instanceof IFile){
					modelFile = (IFile)res;
				}
			}
		} catch (CoreException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		if(modelFile != null){
			ResourceSet resSet = new ResourceSetImpl();
			Resource resource = resSet.createResource(
					URI.createPlatformResourceURI(modelFile.getFullPath().toString(), true));
			try {
				resource.load(resSet.getLoadOptions());
				this.treeViewer.setInput(resource.getContents().get(0));
				this.treeViewer.expandToLevel(3);
				this.treeViewer.refresh();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	public void refresh(){
		loadModel();
	}

}
