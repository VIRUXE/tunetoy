/**
 * 
 */
package net.sf.tunetoy.model;

/**
 * @author robinp
 */
public interface IRomCapabilities {
	/**
	 * @return true if the rom supports ELD, otherwise false
	 */
	public Boolean hasEld();

	/**
	 * @return true if the rom supports VTEC, otherwise false
	 */
	public Boolean hasVtec();

	/**
	 * @return true if ELD is enabled
	 */
	public Boolean isEldEnabled();

	/**
	 * @return true if VTEC is enabled
	 */
	public Boolean isVtecEnabled();
	
	/**
	 * @return true if this is a BoostTools rom
	 */
	public Boolean hasBoostTools();
}
