/**
 * 
 */
package net.sf.tunetoy;

import net.sf.tunetoy.model.IRom;
import net.sf.tunetoy.model.InvalidRomException;

import org.eclipse.swt.widgets.Composite;

/**
 * @author
 * 
 */
public abstract class RomOption {
	public abstract void setRom(IRom rom) throws InvalidRomException;	
	public abstract void buildGUI(Composite composite);
}
