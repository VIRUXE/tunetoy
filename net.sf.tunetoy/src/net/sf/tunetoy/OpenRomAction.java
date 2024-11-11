/**
 * 
 */
package net.sf.tunetoy;

import java.net.URL;

import net.sf.tunetoy.model.InvalidRomException;
import net.sf.tunetoy.model.RomFactory;

import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.ui.IWorkbenchWindow;

/**
 * @author RobinP
 */
public class OpenRomAction extends Action {

	private final IWorkbenchWindow window;

	public OpenRomAction(IWorkbenchWindow window, String label) {
		this.window = window;
		setText(label);
		// The id is used to refer to the action in a menu or toolbar
		setId(ICommandIds.CMD_OPEN);
		// Associate the action with a pre-defined command, to allow key
		// bindings.
		setActionDefinitionId(ICommandIds.CMD_OPEN);
		URL imageURL = Platform.getBundle("net.sf.tunetoy").getEntry( //$NON-NLS-1$
				"/icons/sample2.gif"); //$NON-NLS-1$
		ImageDescriptor desc = ImageDescriptor.createFromURL(imageURL);
		setImageDescriptor(desc);
	}

	@Override
	public void run() {
		FileDialog f = new FileDialog(this.window.getShell(), SWT.OPEN);
		f.setFilterExtensions(new String[] { "*.bin" }); //$NON-NLS-1$
		f.setFilterNames(new String[] { Messages
				.getString("OpenRomAction.HondaBinFiles") }); //$NON-NLS-1$
		String filename = f.open();
		if (filename != null) {
			try {
				RomFactory.loadRom(filename);
			} catch (InvalidRomException e) {
				MessageDialog
						.openError(
								this.window.getShell(),
								Messages.getString("OpenRomAction.Error"), Messages.getString("OpenRomAction.ErrorOpeningRom")); //$NON-NLS-1$ //$NON-NLS-2$
			}
			for (int i = 0; i < this.window.getPages().length; i++) {
				NoncloseableView view = (NoncloseableView) this.window
						.getPages()[i].findView(NoncloseableView.ID);
				if (view != null)
					view.refresh();
			}

		}
	}
}
