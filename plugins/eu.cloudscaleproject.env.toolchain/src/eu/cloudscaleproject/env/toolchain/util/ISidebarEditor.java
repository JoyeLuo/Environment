package eu.cloudscaleproject.env.toolchain.util;

import java.util.List;

import org.eclipse.swt.graphics.Color;

import eu.cloudscaleproject.env.toolchain.resources.types.IEditorInput;

public interface ISidebarEditor{
	
	public void init();
	public void showInput(IEditorInput input);
	
	public void addSidebarEditor(IEditorInput ei, String section);
	public void removeSidebarEditor(IEditorInput ei);
	
	public List<IEditorInput> getInputs(String section);
	public String[] getSidebarSections();
	
	public void update();
	
	public Color getSidebarSectionBackgroundColor();
	public Color getSidebarSectionForegroundColor();
	public Color getSidebarBackgroundColor();
	public Color getSidebarForegroundColor();
	
	public void setNewButtonEnabled(boolean enable);
	public void setNewFromButtonEnabled(boolean enabled);
	public void setRemoveButtonEnabled(boolean enabled);
	
	public void dispose();
}
