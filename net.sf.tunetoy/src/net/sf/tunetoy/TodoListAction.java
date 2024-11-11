/**
 * 
 */
package net.sf.tunetoy;

import java.io.InputStream;
import java.net.URL;

import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.ui.IWorkbenchWindow;

/**
 * @author RobinP
 */
public class TodoListAction extends Action {
	private final IWorkbenchWindow window;

	TodoListAction(String text, IWorkbenchWindow window) {
		super(text);
		this.window = window;
		// The id is used to refer to the action in a menu or toolbar
		setId(ICommandIds.CMD_TODO_LIST);
		// Associate the action with a pre-defined command, to allow key
		// bindings.
		setActionDefinitionId(ICommandIds.CMD_TODO_LIST);
		setImageDescriptor(null);
	}

	@Override
	public void run() {
		URL url = Platform.getBundle("net.sf.tunetoy").getEntry("/todo.txt"); //$NON-NLS-1$ //$NON-NLS-2$

		try {
			var stream = (InputStream) url.getContent();
			var b = new byte[1024];

			while (stream.read(b) == b.length) {
				b = new byte[b.length * 2];
				stream.close();
				stream = (InputStream) url.getContent();
			}

			stream.close();

			var s = new String(b);
			MessageDialog.openInformation(this.window.getShell(), Messages.getString("TodoListAction.TodoList"), s); //$NON-NLS-1$
		} catch (Exception e) {
			e.printStackTrace(System.err);
		}
	}
}
