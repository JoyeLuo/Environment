package eu.cloudscaleproject.env.overview.wizard.util;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.List;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Text;

public class SwtUtil {

	
	public static void setEnabledRecursive(Control ctrl, boolean enabled) {
           if (ctrl instanceof Composite) {
              Composite comp = (Composite) ctrl;
              for (Control c : comp.getChildren())
                 setEnabledRecursive(c, enabled);
           } else {
              ctrl.setEnabled(enabled);
           }
        }
	
	
	public static void addListenerRecursive (Control ctrl, Listener l)
	{
           if (ctrl instanceof Composite && !(ctrl instanceof Combo)) {
              Composite comp = (Composite) ctrl;
              for (Control c : comp.getChildren())
                 addListenerRecursive(c, l);
           } else {
        	   if (ctrl instanceof Combo || ctrl instanceof Button)
        		   ctrl.addListener(SWT.Selection, l);
        	   if (ctrl instanceof Text)
        		   ctrl.addListener(SWT.Modify, l);
        	   if (ctrl instanceof List)
        		   ctrl.addListener(SWT.Selection, l);
        	   
           }
	}

	public static void removeListenerReursive (Control ctrl, Listener l)
	{
           if (ctrl instanceof Composite && !(ctrl instanceof Combo)) {
              Composite comp = (Composite) ctrl;
              for (Control c : comp.getChildren())
                 addListenerRecursive(c, l);
           } else {
        	   if (ctrl instanceof Combo || ctrl instanceof Button)
        		   ctrl.removeListener(SWT.Selection, l);
        	   if (ctrl instanceof Text)
        		   ctrl.removeListener(SWT.Modify, l);
        	   
           }
	}
}
