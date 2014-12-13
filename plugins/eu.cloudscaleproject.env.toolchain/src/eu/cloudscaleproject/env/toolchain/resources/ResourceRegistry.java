package eu.cloudscaleproject.env.toolchain.resources;

import java.util.HashMap;
import java.util.Iterator;
import java.util.logging.Logger;

import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;

import eu.cloudscaleproject.env.toolchain.ToolchainUtils;

public class ResourceRegistry {
	
	private static final Logger logger = Logger.getLogger(ResourceRegistry.class.getName());
	
	private static final String FOLDER_RESOURCE_PROVIDER_ID = "eu.cloudscaleproject.env.toolchain.util.SidebarResourceRegistry.folder";
	private static final String FILE_RESOURCE_PROVIDER_ID = "eu.cloudscaleproject.env.toolchain.util.SidebarResourceRegistry.file";
	
	private static ResourceRegistry instance = null;
	public static ResourceRegistry getInstance(){
		if(instance == null){
			instance = new ResourceRegistry();
		}
		
		//dispose unused resource providers
		Iterator<IFolder> iter = instance.resourceProviders.keySet().iterator();
		while(iter.hasNext()){
			IFolder folder = iter.next();
			if(!folder.exists()){
				instance.resourceProviders.get(folder).dispose();
				iter.remove();
			}
		}
		
		return instance;
	}
	
	private HashMap<String, IResourceProviderFactory> resourceProviderFactories 
											= new HashMap<String, IResourceProviderFactory>();
	private HashMap<IFolder, ResourceProvider> resourceProviders 
											= new HashMap<IFolder, ResourceProvider>();
	
	public ResourceRegistry() {
		//register basic resource provider factories
		registerFactory(FOLDER_RESOURCE_PROVIDER_ID, new FolderResourceProviderFactory());
		registerFactory(FILE_RESOURCE_PROVIDER_ID, new FileResourceProviderFactory());
	}
	
	/**
	 * Register 'IResourceProviderFactory' under specified 'toolchainID'.
	 * ToolchainID should be specified inside 'ToolchainUtils' class. 
	 * 
	 * @param toolchainID ID under which the specified 'IResourceProviderFactory' should be registered.
	 * @param factory 'IResourceProviderFactory'
	 */
	public void registerFactory(String toolchainID, IResourceProviderFactory factory){
		resourceProviderFactories.put(toolchainID, factory);
		logger.info("IResourceProviderFactory registered uder toolchainID: " + toolchainID);
	}
	
	/**
	 * Retrieves 'ResourceProvider' from this registry, or creates it (if it does not exist jet),
	 * using registered 'IResourceProviderFactory'.
	 * If the 'ResourceProviderFactory' is not registered for the specified 'toolchainID', this method returns null.
	 * Returned 'ResourceProvider' root folder is equal to the specified attribute 'folder'. 
	 * 
	 * @param toolchainID String that should be specified in 'ToolchainUtils' class.
	 * @param folder The root folder of the returned 'ResourceProvider'. 
	 * @return ResourceProvider
	 */
	public ResourceProvider getResourceProvider(String toolchainID, IFolder folder){
		
		ResourceProvider resourceProvider = resourceProviders.get(folder);
		
		if(resourceProvider == null){
			IResourceProviderFactory resourceFactory = resourceProviderFactories.get(toolchainID);
			if(resourceFactory == null){
				//resource factory is not registered for the specified tool ID
				return null;
			}
			else{
				resourceProvider = resourceFactory.create(folder);
				resourceProviders.put(folder, resourceProvider);
			}			
		}
		
		//resource factory should not return null resource
		//note: this method can return null resource provider, if the resource factory is not registered for the specified ID!
		assert(resourceProvider != null);
		return resourceProvider;
	}
	
	/**
	 * Returns 'ResourceProvider', created from default 'FolderResourceProviderFactory'
	 * This is convenient method for creating/retrieving 'ResourceProvider'.  
	 * 
	 * @param folder The root folder of the returned 'ResourceProvider'.
	 * @return ResourceProvider, with root folder equal to specified attribute 'folder' 
	 */
	public ResourceProvider getFolderResourceProvider(IFolder folder){
		return getResourceProvider(FOLDER_RESOURCE_PROVIDER_ID, folder);
	}
	
	/**
	 * Returns 'ResourceProvider', created from default 'FileResourceProviderFactory'
	 * This is convenient method for creating/retrieving 'ResourceProvider'.  
	 * 
	 * @param folder The root folder of the returned 'ResourceProvider'.
	 * @return ResourceProvider, with root folder equal to specified attribute 'folder' 
	 */
	public ResourceProvider getFileResourceProvider(IFolder folder){
		return getResourceProvider(FILE_RESOURCE_PROVIDER_ID, folder);
	}
	
	/**
	 * Retrieves 'ResourceProvider' from this registry, or creates it (if it does not exist jet) using registered 'IResourceProviderFactory'.
	 * If the 'ResourceProviderFactory' is not registered for the specified 'toolchainID', this method returns null.
	 * 
	 * @param project IProject used for retrieving/creating 'ResourceProvider' root folder.
	 * @param toolchainID String that should be specified in 'ToolchainUtils' class.
	 * @return ResourceProvider
	 */
	public ResourceProvider getResourceProvider(IProject project, String toolchainID){
		
		//Check if the resource factory is registered for the specified toolchainID
		//If it is not, we don't want to call method 'ToolchainUtils.getToolFolder(project, toolchainID)',
		//because this method creates folders if they do not exist jet!
		IResourceProviderFactory resourceFactory = resourceProviderFactories.get(toolchainID);
		if(resourceFactory == null){
			return null;
		}
		
		return getResourceProvider(toolchainID, ToolchainUtils.getToolFolder(project, toolchainID));
	}

}
