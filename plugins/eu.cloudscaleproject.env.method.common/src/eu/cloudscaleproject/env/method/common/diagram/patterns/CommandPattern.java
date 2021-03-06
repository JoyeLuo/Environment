package eu.cloudscaleproject.env.method.common.diagram.patterns;

import org.eclipse.graphiti.features.context.IAddContext;
import org.eclipse.graphiti.features.context.ICreateContext;
import org.eclipse.graphiti.features.context.ILayoutContext;
import org.eclipse.graphiti.mm.algorithms.RoundedRectangle;
import org.eclipse.graphiti.mm.algorithms.Text;
import org.eclipse.graphiti.mm.algorithms.styles.Orientation;
import org.eclipse.graphiti.mm.pictograms.ContainerShape;
import org.eclipse.graphiti.mm.pictograms.PictogramElement;
import org.eclipse.graphiti.mm.pictograms.PictogramLink;
import org.eclipse.graphiti.mm.pictograms.Shape;
import org.eclipse.graphiti.services.Graphiti;
import org.eclipse.graphiti.services.IGaService;
import org.eclipse.graphiti.services.IPeService;

import eu.cloudscaleproject.env.method.common.method.Command;
import eu.cloudscaleproject.env.method.common.method.Container;
import eu.cloudscaleproject.env.method.common.method.Node;
import eu.cloudscaleproject.env.method.common.method.Section;
import eu.cloudscaleproject.env.method.common.util.MethodUtil;

public class CommandPattern extends NodePattern {
	
	@Override
	public String getCreateName() {
		return "Command";
	}

	public boolean isMainBusinessObjectApplicable(Object mainBusinessObject) {
		return (mainBusinessObject instanceof Command);
	}
	
	@Override
	public boolean canCreate(ICreateContext context) {
		PictogramLink link = context.getTargetContainer().getLink();
		if(link == null){
			return false;
		}
		
		Object o = link.getBusinessObjects().get(0);
		if(o instanceof Section || o instanceof Container){
			return true;
		}
		return false;
	}

	@Override
	public Object[] create(ICreateContext context) {
		Command command = MethodUtil.createCommand();
		Object bo = context.getTargetContainer().getLink().getBusinessObjects().get(0);
		
		if(bo instanceof Container){
			Container container = (Container)bo;
			container.getCommands().add(command);
		}
		else if(bo instanceof Section){
			Section section = (Section)bo;
			section.getCommands().add(command);
		}
		
		addGraphicalRepresentation(context, command);
		return new Object[]{command};
	}
	
	@Override
	public PictogramElement add(IAddContext context) {
		PictogramElement root = super.add(context);
		PatternUtil.addShapeID(root, "command");
		return root;
	}
	
	@Override
	protected ContainerShape addNode(Node node, ContainerShape parent, int x, int y, int w, int h){
		// Get Graphiti services for easier access
		IPeService peService = Graphiti.getPeService();
		IGaService gaService = Graphiti.getGaService();
				
		ContainerShape containerShape = peService.createContainerShape(parent, true);
		PatternUtil.addShapeID(containerShape, "node");

		link(containerShape, node);

		RoundedRectangle roundedRectangle = gaService.createRoundedRectangle(containerShape, 5, 5);
		gaService.setLocationAndSize(roundedRectangle, x, y, w, h);
		roundedRectangle.setBackground(getColorBackground(node));
		roundedRectangle.setForeground(getColorForeground(node));
			
		addName(node, containerShape, 5, 5, w-5, 20);
		
		return containerShape;
	}
	
	@Override
	public boolean layout(ILayoutContext context) {
		ContainerShape root = (ContainerShape)context.getPictogramElement();
		Shape nameShape = getName((ContainerShape)context.getPictogramElement());
		
		Text text = (Text)nameShape.getGraphicsAlgorithm();
		text.setX(0);
		text.setY(0);
		text.setWidth(root.getGraphicsAlgorithm().getWidth());
		text.setHeight(root.getGraphicsAlgorithm().getHeight());

		text.setHorizontalAlignment(Orientation.ALIGNMENT_CENTER);
		text.setVerticalAlignment(Orientation.ALIGNMENT_CENTER);
		return true;
	}
}
