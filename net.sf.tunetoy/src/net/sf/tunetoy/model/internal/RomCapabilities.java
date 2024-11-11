package net.sf.tunetoy.model.internal;

import net.sf.tunetoy.model.IRomCapabilities;

public abstract class RomCapabilities implements IRomCapabilities {
	protected Boolean capEld  = Boolean.valueOf(false);
	protected Boolean capVtec = Boolean.valueOf(false);

	/* (non-Javadoc)
	 * @see net.sf.tunetoy.model.IRomCapabilities#hasEld()
	 */
	public final Boolean hasEld() { return this.capEld; }

	/* (non-Javadoc)
	 * @see net.sf.tunetoy.model.IRomCapabilities#hasVtec()
	 */
	public final Boolean hasVtec() { return this.capVtec; }

	/* (non-Javadoc)
	 * @see net.sf.tunetoy.model.IRomCapabilities#isEldEnabled()
	 */
	public abstract Boolean isEldEnabled();

	/* (non-Javadoc)
	 * @see net.sf.tunetoy.model.IRomCapabilities#isVtecEnabled()
	 */
	public abstract Boolean isVtecEnabled();
	
	/* (non-Javadoc)
	 * @see net.sf.tunetoy.model.IRomCapabilities#hasBoostTools()
	 */
	public abstract Boolean hasBoostTools();
}
