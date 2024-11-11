/**
 * 
 */
package net.sf.tunetoy;

import net.sf.tunetoy.model.IRom;
import net.sf.tunetoy.model.InvalidRomException;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtension;
import org.eclipse.core.runtime.IExtensionPoint;
import org.eclipse.core.runtime.IExtensionRegistry;
import org.eclipse.core.runtime.Platform;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.widgets.TabItem;
import org.eclipse.ui.part.ViewPart;

/**
 * @author
 */
public class OptionsView extends ViewPart {
	public static final String ID = "net.sf.tunetoy.OptionsView"; //$NON-NLS-1$

	protected IRom currentRom = null;

	private Composite parent;

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ui.part.WorkbenchPart#createPartControl(org.eclipse.swt.widgets.Composite)
	 */
	@Override
	public void createPartControl(Composite parentComp) {
		this.parent = parentComp;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ui.part.WorkbenchPart#setFocus()
	 */
	@Override
	public void setFocus() {
		// TODO Auto-generated method stub
	}

	public void setRom(IRom rom) {
		this.currentRom = rom;
		Composite composite = new Composite(this.parent, SWT.NONE); 
		composite.setLayout(new FillLayout());
		
		TabFolder tabfolder = new TabFolder(composite, SWT.NONE);
		IExtensionRegistry registry = Platform.getExtensionRegistry();
		IExtensionPoint extensionPoint = registry
				.getExtensionPoint("net.sf.tunetoy.options"); //$NON-NLS-1$

		IExtension[] extensions = extensionPoint.getExtensions();
		// For each extension ...
		for (IExtension extension : extensions)
			for (IConfigurationElement element : extension
					.getConfigurationElements())
			{
				try {
					RomOption option = (RomOption)element.createExecutableExtension("class"); //$NON-NLS-1$
					option.setRom(this.currentRom);
					TabItem item = new TabItem(tabfolder, SWT.NONE);
					item.setText(element.getAttribute("name")); //$NON-NLS-1$
					Composite optioncomp = new Composite(tabfolder, SWT.NONE);
					optioncomp.setLayout(new FillLayout());
					item.setControl(optioncomp);
					option.buildGUI(optioncomp);
				} catch (CoreException e) {
					// Print the traceback, something is wrong with the plugin
					e.printStackTrace();
				} catch (InvalidRomException e) {
					// Fall through, extension does not support the current rom type
				} 
			}


/*			Button b = new Button(capabilitiesgroup, SWT.DEFAULT);
			b.setText("SAVE"); //$NON-NLS-1$
			b.addSelectionListener(new SelectionListener() {

				public void widgetSelected(SelectionEvent e) {
					// TODO Auto-generated method stub
					romp30.save();
				}

				public void widgetDefaultSelected(SelectionEvent e) {
					// TODO Auto-generated method stub
				}
			}); */

		this.parent.pack();
	}

}
