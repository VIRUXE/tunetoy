package net.sf.tunetoy;

import org.eclipse.jface.action.Action;
import org.eclipse.ui.actions.ActionFactory;

/**
 * @author robinp
 */
public abstract class OpenMapViewAction extends Action implements ActionFactory.IWorkbenchAction {
	public abstract String getName();
}
