/**
 * 
 */
package net.sf.tunetoy;

import java.net.URL;

import net.sf.tunetoy.model.IRom;

import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PartInitException;

/**
 * @author robinp
 */
public class OpenGraphMapViewAction extends OpenMapViewAction {

	private final IWorkbenchWindow window;

	private IRom rom;

	private String viewtype;

	public OpenGraphMapViewAction(IWorkbenchWindow window, IRom rom,
			String viewType) {
		this.window = window;
		this.rom = rom;
		this.viewtype = viewType;
		URL imageURL = Platform.getBundle("net.sf.tunetoy").getEntry( //$NON-NLS-1$
				"/icons/sample2.gif"); //$NON-NLS-1$
		ImageDescriptor desc = ImageDescriptor.createFromURL(imageURL);
		setImageDescriptor(desc);
	}

	@Override
	public void run() {
		if (this.window != null) {
			try {
				GraphView view = (GraphView) this.window.getActivePage()
						.showView(
								GraphView.ID,
								Messages.getString(this.viewtype)
										+ " - " + this.rom.getName(), //$NON-NLS-1$
								IWorkbenchPage.VIEW_ACTIVATE);
				view.setRom(this.rom, this.viewtype);
			} catch (PartInitException e) {
				MessageDialog
						.openError(
								this.window.getShell(),
								Messages.getString("OpenMapViewAction.Error"), //$NON-NLS-1$
								Messages
										.getString("OpenMapViewAction.ErrorOpeningView") + e.getMessage()); //$NON-NLS-1$
			}
		}

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ui.actions.ActionFactory.IWorkbenchAction#dispose()
	 */
	public void dispose() {
		// TODO Auto-generated method stub

	}

	@Override
	public String getName() {
		return Messages.getString(this.viewtype + ".graph"); //$NON-NLS-1$
	}

}
