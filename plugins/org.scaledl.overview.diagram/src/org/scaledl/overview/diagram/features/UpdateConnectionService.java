/*******************************************************************************
 * <copyright>
 *
 * Copyright (c) 2005, 2010 SAP AG.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    SAP AG - initial API, implementation and documentation
 *
 * </copyright>
 *
 *******************************************************************************/
package org.scaledl.overview.diagram.features;

import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.transaction.RecordingCommand;
import org.eclipse.emf.transaction.TransactionalEditingDomain;
import org.eclipse.graphiti.features.IFeatureProvider;
import org.eclipse.graphiti.features.IReason;
import org.eclipse.graphiti.features.context.IUpdateContext;
import org.eclipse.graphiti.features.impl.AbstractUpdateFeature;
import org.eclipse.graphiti.features.impl.Reason;
import org.eclipse.graphiti.mm.algorithms.Text;
import org.eclipse.graphiti.mm.pictograms.Connection;
import org.eclipse.graphiti.mm.pictograms.ConnectionDecorator;
import org.eclipse.swt.widgets.Display;
import org.scaledl.overview.architecture.ExternalConnection;

public class UpdateConnectionService extends AbstractUpdateFeature {

	public UpdateConnectionService(IFeatureProvider fp) {
		super(fp);
	}

	public boolean canUpdate(IUpdateContext context) {
		// return true, if linked business object is an EClass
		Object bo = getBusinessObjectForPictogramElement(context.getPictogramElement());
		return (bo instanceof Connection);
	}

	public IReason updateNeeded(IUpdateContext context) {
		// retrieve name from pictogram model
		Connection connection = (Connection) context.getPictogramElement();
		org.scaledl.overview.architecture.Connection csmConnection = 
				(org.scaledl.overview.architecture.Connection) getBusinessObjectForPictogramElement(context.getPictogramElement());
		
		EList<ConnectionDecorator> connectionDecorators = connection.getConnectionDecorators();
		if (connectionDecorators == null || connectionDecorators.isEmpty())
			return Reason.createFalseReason();
		
		
		ConnectionDecorator connectionDecorator = connectionDecorators.get(0);
		
		Text text = (Text) connectionDecorator.getGraphicsAlgorithm();
		
		if (!text.getValue().equals(createConnectionText(csmConnection)))
		{
			// Strange behavior here -- update(context) is never called. 
			// Return false reason (to not highlight the connection in the diagram)
			// and manually update the connection
			final TransactionalEditingDomain editingDomain = TransactionalEditingDomain.Factory.INSTANCE.getEditingDomain(connection.eResource().getResourceSet());
			final IUpdateContext fContext = context;
			
			Display.getDefault().asyncExec(new Runnable() {
				
				@Override
				public void run() {
					// TODO Auto-generated method stub
					editingDomain.getCommandStack().execute(new RecordingCommand(editingDomain) {
						@Override
						protected void doExecute() {
							update(fContext);
						}
					});
					
				}
			});
			return Reason.createFalseReason(); 
		}
		else
			return Reason.createFalseReason();
	}

	public boolean update(IUpdateContext context) {
		// retrieve name from business model
		Connection connection = (Connection) context.getPictogramElement();
		org.scaledl.overview.architecture.Connection csmConnection = 
				(org.scaledl.overview.architecture.Connection) getBusinessObjectForPictogramElement(context.getPictogramElement());
		
		EList<ConnectionDecorator> connectionDecorators = connection.getConnectionDecorators();
		if (connectionDecorators == null || connectionDecorators.isEmpty())
			return false; 
		
		
		ConnectionDecorator connectionDecorator = connectionDecorators.get(0);
		
		Text text = (Text) connectionDecorator.getGraphicsAlgorithm();
		
		text.setValue(createConnectionText(csmConnection));
		
		return true;
	}
	
	
	private String createConnectionText (org.scaledl.overview.architecture.Connection c)
	{
		String format = "%s \n Latency: %s \n Bandwidth: %s";
		
		String t = String.format(format, c.getName(), "?", "?");
		if (c instanceof ExternalConnection)
		{
			ExternalConnection ec = (ExternalConnection)c;
			t = String.format(format, c.getName(), ec.getLatency(), ec.getBandwidth());
		}
		else{
			return "";
		}
		
		return t;
	}
}
