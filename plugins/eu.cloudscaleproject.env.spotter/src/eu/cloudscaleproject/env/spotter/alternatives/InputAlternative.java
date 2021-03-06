package eu.cloudscaleproject.env.spotter.alternatives;

import java.io.InputStream;
import java.util.LinkedList;
import java.util.List;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.ui.IPropertyListener;
import org.eclipse.ui.part.EditorPart;
import org.spotter.eclipse.ui.editors.AbstractSpotterEditor;
import org.spotter.eclipse.ui.model.xml.MeasurementEnvironmentFactory;
import org.spotter.shared.environment.model.XMeasurementEnvironment;
import org.spotter.shared.util.JAXBUtil;

import eu.cloudscaleproject.env.toolchain.CSTool;
import eu.cloudscaleproject.env.toolchain.resources.types.AbstractInputAlternative;

public class InputAlternative extends AbstractInputAlternative
{
	public static final String PLUGIN_FILE_ENVIRONMENT_CONFIG = "resources/alternative/mEnv.xml";
	public static String KEY_ENVIRONMENT_CONFIG = "environment_config";
	
	private List<AbstractSpotterEditor> editors = new LinkedList<AbstractSpotterEditor>();
	
	final IPropertyListener listener = new IPropertyListener()
	{
		@Override
		public void propertyChanged(Object source, int propId)
		{
			if (EditorPart.PROP_DIRTY == propId)
			{
				setDirty(true);
			}
		}
	};

	public InputAlternative(IProject project, IFolder folder)
	{
		super(project, folder, null, CSTool.SPOTTER_DYN);
	}

	public void registerSpotterEditor(final AbstractSpotterEditor editor)
	{
		editor.addPropertyListener(listener);
		editors.add(editor);
	}

	public void unRegisterSpotterEditor(final AbstractSpotterEditor editor)
	{
		editors.remove(editor);
		editor.removePropertyListener(listener);
	}

	@Override
	protected void doSave(IProgressMonitor monitor)
	{
		super.doSave(monitor);
		
		for (AbstractSpotterEditor editor : editors)
		{
			editor.doSave(monitor);
		}
	}


	@Override
	protected void doCreate(IProgressMonitor monitor)
	{
		super.doCreate(monitor);
		try {
			IFile environment = getResource().getFile("mEnv.xml");
			if (!environment.exists()) {
				MeasurementEnvironmentFactory factory = MeasurementEnvironmentFactory.getInstance();
				XMeasurementEnvironment defaultEnvironment = factory.createMeasurementEnvironment();
				InputStream in = JAXBUtil.createInputStreamFromElement(defaultEnvironment);
				environment.create(in, false, null);
				in.close();
			}
			setSubResource(KEY_ENVIRONMENT_CONFIG, environment);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
