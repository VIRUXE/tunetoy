package net.sf.tunetoy;

import org.eclipse.ui.application.ActionBarAdvisor;
import org.eclipse.ui.application.IActionBarConfigurer;
import org.eclipse.ui.application.IWorkbenchWindowConfigurer;
import org.eclipse.ui.application.WorkbenchWindowAdvisor;

public class ApplicationWorkbenchWindowAdvisor extends WorkbenchWindowAdvisor {

	public ApplicationWorkbenchWindowAdvisor(IWorkbenchWindowConfigurer configurer) { super(configurer); }

	@Override
	public ActionBarAdvisor createActionBarAdvisor(IActionBarConfigurer configurer) { return new ApplicationActionBarAdvisor(configurer); }

	@Override
	public void preWindowOpen() {
		IWorkbenchWindowConfigurer configurer = getWindowConfigurer();
		// configurer.setInitialSize(new Point(640, 480));
		configurer.setShowCoolBar(true);
		configurer.setShowStatusLine(false);
	}

}
