package net.sf.tunetoy;

import java.net.URL;

import net.sf.tunetoy.model.IRom;

import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PartInitException;

public class OpenTableMapViewAction extends OpenMapViewAction {
	private final IWorkbenchWindow window;
	private IRom rom;
	private String viewtype;

	public OpenTableMapViewAction(IWorkbenchWindow window, IRom rom, String viewType) {
		this.window = window;
		this.rom = rom;
		this.viewtype = viewType;
		URL imageURL = Platform.getBundle("net.sf.tunetoy").getEntry("/icons/sample2.gif");
		ImageDescriptor desc = ImageDescriptor.createFromURL(imageURL);
		setImageDescriptor(desc);
	}

	@Override
	public void run() {
		if (this.window != null) {
			try {
				SampleView view = (SampleView) this.window.getActivePage().showView(SampleView.ID, Messages.getString(this.viewtype)+ " - " + this.rom.getName(), IWorkbenchPage.VIEW_ACTIVATE);
				view.setRom(this.rom, this.viewtype);
			} catch (PartInitException e) {
				MessageDialog.openError(this.window.getShell(), Messages.getString("OpenMapViewAction.Error"), Messages.getString("OpenMapViewAction.ErrorOpeningView") + e.getMessage());
			}
		}

	}

	public void dispose() {
		// TODO Auto-generated method stub
	}

	@Override
	public String getName() { return Messages.getString(this.viewtype + ".table"); }
}