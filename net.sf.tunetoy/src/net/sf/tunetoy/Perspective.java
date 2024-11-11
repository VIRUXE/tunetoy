package net.sf.tunetoy;

import org.eclipse.ui.IPageLayout;
import org.eclipse.ui.IPerspectiveFactory;

public class Perspective implements IPerspectiveFactory {

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ui.IPerspectiveFactory#createInitialLayout(org.eclipse.ui.IPageLayout)
	 */
	public void createInitialLayout(IPageLayout layout) {
		// TODO Auto-generated method stub
		String editorArea = layout.getEditorArea();
		layout.setEditorAreaVisible(false);

		layout.addStandaloneView(NoncloseableView.ID, false, IPageLayout.LEFT,
				0.25f, editorArea);
		layout.getViewLayout(NoncloseableView.ID).setCloseable(false);
	}

}
