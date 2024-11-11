package net.sf.tunetoy.model.internal;

import net.sf.tunetoy.model.IRomMap;

/**
 * @author RobinP
 */
public abstract class RomMap implements IRomMap {
	protected Float[][] values = null;
	protected Integer[] mapScalars = null;
	protected Integer[] rpmScalars = null;

	/*
	 * (non-Javadoc)
	 * 
	 * @see net.sf.tunetoy.model.IRomMap#getValue(java.lang.Integer,
	 *      java.lang.Integer)
	 */
	public Float getValue(Integer x, Integer y) { return this.values[x][y] != null ? this.values[x][y] : 0; }

	/*
	 * (non-Javadoc)
	 * 
	 * @see net.sf.tunetoy.model.IRomMap#getMapScalars()
	 */
	public Integer[] getMapScalars() { return this.mapScalars; }

	/*
	 * (non-Javadoc)
	 * 
	 * @see net.sf.tunetoy.model.IRomMap#getRpmScalars()
	 */
	public Integer[] getRpmScalars() { return this.rpmScalars; }
}
