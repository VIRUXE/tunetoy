package net.sf.tunetoy.model;

public interface IRomMap {
	public Float getValue(Integer x, Integer y);
	public Integer[] getMapScalars();
	public Integer[] getRpmScalars();
}
