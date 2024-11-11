package net.sf.tunetoy.model;

/**
 * @author RobinP
 */
public interface IRom {

	/**
	 * Container class for rom types
	 */
	public class RomTypes {
		public static final String P30 = "P30-203"; //$NON-NLS-1$
		public static final String P28 = "P28-304"; //$NON-NLS-1$
		public static final String P13 = "P13"; //$NON-NLS-1$
		public static final String P04 = "P04"; //$NON-NLS-1$
	}

	/**
	 * @return the full pathname of this Rom
	 */
	public String getName();

	/**
	 * @returns the Rom type
	 */
	public String getType();

	/**
	 * @return an IRomCapabilities instance describing the capabilities of this
	 *         rom (supports VTEC (and implies it has multiple ignition/fuel
	 *         maps), ELD, Knock etc.)
	 */
	public IRomCapabilities getCapabilities();

	/**
	 * @param tableType
	 *            the type of map to return
	 * @return an instance of IRomMap with
	 */
	public IRomMap getMap(String tableType);

	public Integer readUnsignedByteAt(Integer address);
	public void writeArray(Integer address, Integer[] bytes);
	public void writeByte(int address, Integer ibyte);
}
