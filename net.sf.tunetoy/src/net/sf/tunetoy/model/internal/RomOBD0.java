package net.sf.tunetoy.model.internal;

import net.sf.tunetoy.model.IRomOBD0;

public abstract class RomOBD0 extends Rom implements IRomOBD0 {

	protected RomOBD0(String type) {
		super(type);
	}
}
