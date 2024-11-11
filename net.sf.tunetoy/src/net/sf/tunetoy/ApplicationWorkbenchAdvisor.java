package net.sf.tunetoy;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.ui.application.IWorkbenchWindowConfigurer;
import org.eclipse.ui.application.WorkbenchAdvisor;
import org.eclipse.ui.application.WorkbenchWindowAdvisor;

/**
 * This workbench advisor creates the window advisor, and specifies the
 * perspective id for the initial window.
 */
public class ApplicationWorkbenchAdvisor extends WorkbenchAdvisor {
	private static final String PERSPECTIVE_ID = "net.sf.tunetoy.perspective"; //$NON-NLS-1$

	@Override
	public WorkbenchWindowAdvisor createWorkbenchWindowAdvisor(IWorkbenchWindowConfigurer configurer) {
		MessageDialog.openInformation(configurer.getWindow().getShell(), "Tunetoy", Messages.getString("ApplicationWorkbenchAdvisor.Disclaimer")); 
		return new ApplicationWorkbenchWindowAdvisor(configurer);
	}

	@Override
	public String getInitialWindowPerspectiveId() { return PERSPECTIVE_ID; }
}
