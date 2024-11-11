package net.sf.tunetoy.model.internal;

import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.Hashtable;

import net.sf.tunetoy.model.IRom;
import net.sf.tunetoy.model.IRomCapabilities;
import net.sf.tunetoy.model.IRomMap;
import net.sf.tunetoy.model.InvalidRomException;

public abstract class Rom implements IRom {
	protected IRomCapabilities romCaps = null;
	protected Hashtable<String, IRomMap> romMaps = new Hashtable<String, IRomMap>();
	private String filename = null;
	private byte[] mRomMap = null;
	private String romType = null;

	protected Rom(String type) { this.romType = type; }

	public IRomCapabilities getCapabilities() { return this.romCaps; }

	/*
	 * (non-Javadoc)
	 * 
	 * @see net.sf.tunetoy.model.IRom#getMap(java.lang.String)
	 */
	public IRomMap getMap(String tableType) { return this.romMaps.get(tableType); }

	/*
	 * @see net.sf.tunetoy.model.IRom#getName()
	 */
	public String getName() {
		var name = this.filename.substring(this.filename.lastIndexOf('\\') + 1);
		return name + "  (" + this.getType() + ")"; //$NON-NLS-1$ //$NON-NLS-2$
	}

	public String getType() { return this.romType; }

	/**
	 * @param fileName
	 * @throws Exception
	 */
	protected void readRom(String fileName) throws Exception {
		this.filename = fileName;

		var romFile = new File(fileName);

		if (romFile.canRead()) {
			var romSize = (int) romFile.length();

			this.mRomMap = new byte[romSize];

			FileInputStream romFileStream = new FileInputStream(fileName);
			DataInputStream romData       = new DataInputStream(romFileStream);

			if (romData.read(this.mRomMap) != romSize) throw new InvalidRomException();

			romData.close();

			// Debug code
			System.err.println("Rom fingerprint"); //$NON-NLS-1$
			System.err.println((this.readUnsignedByteAt(Integer.valueOf(0))));
			System.err.println((this.readUnsignedByteAt(Integer.valueOf(1))));

			// int cntSmallNum = 0;
			// for (int i = 0; i < this.mRomMap.length; i++) {
			// if ((this.readUnsignedByteAt(new Integer(i)) < 20)
			// && (this.readUnsignedByteAt(new Integer(i)) > 0)) {
			// cntSmallNum += 1;
			// } else {
			// if (cntSmallNum > 9) {
			// System.err.println();
			// System.err.print("Found occurence: ");
			// System.err.println(i - cntSmallNum);
			// for (int j = i - cntSmallNum; j < i; j++) {
			// System.err.println(this.readUnsignedByteAt(
			// new Integer(j)));
			// }
			// }
			// cntSmallNum = 0;
			// }
			// }

		} else
			throw new InvalidRomException();
	}

	public Integer readUnsignedByteAt(Integer address) { return Integer.valueOf(this.mRomMap[address] & 0xFF); }

	protected Integer readUnsignedWordAt(Integer address) {
		var loByte = this.readUnsignedByteAt(Integer.valueOf(address + 0));
		var hiByte = this.readUnsignedByteAt(Integer.valueOf(address + 1));

		return Integer.valueOf(hiByte * 256 + loByte);
	}

	public void writeArray(Integer address, Integer[] bytes) {
		for (int i = 0; i < bytes.length; i++)
			this.writeByte(address + i, bytes[i]);
	}

	public void writeByte(int address, Integer ibyte) {
		this.mRomMap[address] = ibyte.byteValue();
	}
	
	public void save() {
		var romFile = new File(this.filename);

		try {
			var fs = new FileOutputStream(romFile);
			fs.write(this.mRomMap);
			fs.close();
		} catch (Exception e) {
			e.printStackTrace(System.err);
		}
	}

}
