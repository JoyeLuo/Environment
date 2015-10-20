package eu.cloudscaleproject.env.toolchain.explorer.addons;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.inject.Inject;

import org.eclipse.e4.core.contexts.ContextInjectionFactory;
import org.eclipse.e4.core.contexts.IEclipseContext;
import org.eclipse.e4.core.di.annotations.Optional;
import org.eclipse.e4.core.di.extensions.EventTopic;
import org.eclipse.e4.ui.workbench.UIEvents;
import org.osgi.service.event.Event;

import eu.cloudscaleproject.env.toolchain.services.IExplorerService;

public class ExplorerServiceAddon {

	@Inject
	@Optional
	public void applicationStarted(@EventTopic(UIEvents.UILifeCycle.APP_STARTUP_COMPLETE) Event event) {
		
	}
	
	@PostConstruct
	void postConstruct(IEclipseContext context) {
		
		//inject diagram service into the context
		ExplorerService es = ContextInjectionFactory.make(ExplorerService.class, context);
		context.set(IExplorerService.class, es);
		context.set(ExplorerService.class, es);
	}
	
	@PreDestroy
	void preDestroy(IEclipseContext context) {
		
		//remove diagram service from the context
		context.remove(IExplorerService.class);
		context.remove(ExplorerService.class);
	}
	
}
