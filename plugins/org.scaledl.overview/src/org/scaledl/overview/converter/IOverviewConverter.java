package org.scaledl.overview.converter;

import java.util.List;

import org.eclipse.core.resources.IFolder;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.Resource;
import org.scaledl.overview.core.Entity;

public interface IOverviewConverter {

	public final static String KEY_PCM_SYSTEM = "PCM_SYSTEM";
	public final static String KEY_PCM_REPOSITORY = "PCM_REPOSITORY";
	public final static String KEY_PCM_INTERFACE = "PCM_INTERFACE";
	public final static String KEY_PCM_OPERATION = "PCM_OPERATION";

	public boolean addExternalModel(Entity object, List<EObject> external,
			IOverviewConverterCallback callback);

	public boolean removeExternalModel(Entity object, List<EObject> external,
			IOverviewConverterCallback callback);

	public void transform(Resource resource, IFolder folder);

	public boolean canTransform(Resource resource);

}
