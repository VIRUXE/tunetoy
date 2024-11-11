package net.sf.tunetoy.model.internal;

import net.sf.tunetoy.model.IRom;
import net.sf.tunetoy.model.IRomP28;
import net.sf.tunetoy.model.InvalidRomException;
import net.sf.tunetoy.model.TableType;
import net.sf.tunetoy.model.WrongRomHandlerException;

public class RomP28 extends RomOBD1 implements IRomP28 {
	class RomP28Capabilities extends RomCapabilities {
		RomP28Capabilities() {
			this.capVtec = true;
			this.capEld  = false;  // FIXME: unknown
		}

		@Override
		public Boolean isEldEnabled() {
			return false; // FIXME: unknown
		}

		@Override
		public Boolean isVtecEnabled() {
			return RomP28.this.readUnsignedByteAt(RomP28.RomP28Constants.sAddressVtecDisable) == 0x00;
		}

		@Override
		public Boolean hasBoostTools() {
			return false; // No support for boost tools in P28, use P30 instead
		}

	}

	class RomP28Constants {
		private static final int sAddressHighCamFuelMultiplierTable = 0x71EA;
		private static final int sAddressHighCamFuelTable = 0x7122;
		private static final int sAddressHighCamIgnitionAdvanceTable = 0x73AC;
		private static final int sAddressHighCamMAPScalars = 0x700a;
		private static final int sAddressHighCamRPMScalars = 0x7028;
		private static final int sAddressLowCamFuelMultiplierTable = 0x7118;
		private static final int sAddressLowCamFuelTable = 0x7050;
		private static final int sAddressLowCamIgnitionAdvanceTable = 0x72E4;
		private static final int sAddressLowCamMAPScalars = 0x7000;
		private static final int sAddressLowCamRPMScalars = 0x7014;
		private static final int sAddressVtecDisable = 0x60FA;
		private static final int sNrMapScalars = 10;
		private static final int sNrRpmScalars = 20;
	}

	class RomP28NonVtecFuelMap extends RomFuelMap {
		public RomP28NonVtecFuelMap() {
			this.values     = new Float[RomP28Constants.sNrMapScalars][RomP28Constants.sNrRpmScalars];
			this.mapScalars = RomP28.this.getNonVtecMapScalars();
			this.rpmScalars = RomP28.this.getNonVtecRpmScalars();

			for (int map = 0; map < this.mapScalars.length; map++)
				for (int rpm = 0; rpm < this.rpmScalars.length; rpm++) {
					var addressFuelTableLocation   = Integer.valueOf(RomP28Constants.sAddressLowCamFuelTable + rpm * RomP28Constants.sNrMapScalars + map);
					var addressFuelMultiplierTable = Integer.valueOf(RomP28Constants.sAddressLowCamFuelMultiplierTable + map);

					this.values[map][rpm] = Float.valueOf((readUnsignedByteAt(addressFuelTableLocation) * readUnsignedByteAt(addressFuelMultiplierTable)) / 4.f);
				}
		}
	}

	class RomP28NonVtecIgnitionMap extends RomIgnitionMap {
		public RomP28NonVtecIgnitionMap() {
			this.values     = new Float[RomP28Constants.sNrMapScalars][RomP28Constants.sNrRpmScalars];
			this.mapScalars = RomP28.this.getNonVtecMapScalars();
			this.rpmScalars = RomP28.this.getNonVtecRpmScalars();

			for (int map = 0; map < this.mapScalars.length; map++)
				for (int rpm = 0; rpm < this.rpmScalars.length; rpm++)
					this.values[map][rpm] = Float.valueOf((float)((readUnsignedByteAt(Integer.valueOf(RomP28Constants.sAddressLowCamIgnitionAdvanceTable + rpm * RomP28Constants.sNrMapScalars + map)) - 24) / 4.0));
		}
	}

	class RomP28VtecFuelMap extends RomFuelMap {
		public RomP28VtecFuelMap() {
			this.values = new Float[RomP28Constants.sNrMapScalars][RomP28Constants.sNrRpmScalars];
			this.mapScalars = RomP28.this.getVtecMapScalars();
			this.rpmScalars = RomP28.this.getVtecRpmScalars();

			for (int map = 0; map < this.mapScalars.length; map++)
				for (int rpm = 0; rpm < this.rpmScalars.length; rpm++) {
					var addressFuelTableLocation   = Integer.valueOf(RomP28Constants.sAddressHighCamFuelTable + rpm * RomP28Constants.sNrMapScalars + map);
					var addressFuelMultiplierTable = Integer.valueOf(RomP28Constants.sAddressHighCamFuelMultiplierTable + map);

					this.values[map][rpm] = Float.valueOf((readUnsignedByteAt(addressFuelTableLocation) * readUnsignedByteAt(addressFuelMultiplierTable)) / 4.f);
				}
		}
	}

	class RomP28VtecIgnitionMap extends RomIgnitionMap {
		public RomP28VtecIgnitionMap() {
			this.values = new Float[RomP28Constants.sNrMapScalars][RomP28Constants.sNrRpmScalars];
			this.mapScalars = RomP28.this.getVtecMapScalars();
			this.rpmScalars = RomP28.this.getVtecRpmScalars();

			for (int map = 0; map < this.mapScalars.length; map++)
				for (int rpm = 0; rpm < this.rpmScalars.length; rpm++)
					this.values[map][rpm] = Float.valueOf((readUnsignedByteAt(Integer.valueOf(RomP28Constants.sAddressHighCamIgnitionAdvanceTable + rpm * RomP28Constants.sNrMapScalars + map)) - 24) / 4.0f);
		}
	}

	private Integer[] mapNonVtecScalars = null;
	private Integer[] mapVtecScalars = null;
	private Integer[] rpmNonVtecScalars = null;
	private Integer[] rpmVtecScalars = null;

	public RomP28(String filename) throws InvalidRomException, WrongRomHandlerException {
		super(IRom.RomTypes.P28);

		try {
			this.readRom(filename);
		} catch (Exception e) {
			throw new InvalidRomException();
		}

		if (this.readUnsignedByteAt(0) != 237 || this.readUnsignedByteAt(1) != 36) throw new WrongRomHandlerException();

		this.romMaps.put(TableType.FUEL_VTEC, new RomP28VtecFuelMap());
		this.romMaps.put(TableType.FUEL_NO_VTEC, new RomP28NonVtecFuelMap());
		this.romMaps.put(TableType.IGNITION_VTEC, new RomP28VtecIgnitionMap());
		this.romMaps.put(TableType.IGNITION_NO_VTEC, new RomP28NonVtecIgnitionMap());
		this.romCaps = new RomP28Capabilities();
	}

	protected Integer[] getNonVtecMapScalars() {
		if (this.mapNonVtecScalars != null) return this.mapNonVtecScalars;

		this.mapNonVtecScalars = new Integer[RomP28Constants.sNrMapScalars];
		for (int map = 0; map < RomP28Constants.sNrMapScalars; map++) this.mapNonVtecScalars[map] = Integer.valueOf((1764 / 255) * readUnsignedByteAt(Integer.valueOf(RomP28Constants.sAddressLowCamMAPScalars + map)));

		return this.getNonVtecMapScalars();
	}

	protected Integer[] getNonVtecRpmScalars() {
		if (this.rpmNonVtecScalars != null) return this.rpmNonVtecScalars;

		this.rpmNonVtecScalars = new Integer[RomP28Constants.sNrRpmScalars];

		for (int rpm = 0; rpm < RomP28Constants.sNrRpmScalars; rpm++) {
			// http://www.pgmfi.org/twiki/bin/view/Library/OBD1_8bitLowCamRPM
			// FIXME: Calculations are a bit off???

			int value = readUnsignedByteAt(Integer.valueOf(RomP28Constants.sAddressLowCamRPMScalars + rpm));
			int h = value / 64;
			int x = h - 1 > 0 ? h - 1 : 0;
			int l = value + (x * 64);
			int r = (1875000 * l * 2 ^ h) / 240000;

			this.rpmNonVtecScalars[rpm] = Integer.valueOf(r);
		}

		return this.getNonVtecRpmScalars();
	}

	protected Integer[] getVtecMapScalars() {
		if (this.mapVtecScalars != null) return this.mapVtecScalars;

		this.mapVtecScalars = new Integer[RomP28Constants.sNrMapScalars];
		for (int map = 0; map < RomP28Constants.sNrMapScalars; map++) this.mapVtecScalars[map] = Integer.valueOf((1764 / 255) * readUnsignedByteAt(Integer.valueOf(RomP28Constants.sAddressHighCamMAPScalars + map)));

		return this.getVtecMapScalars();
	}

	protected Integer[] getVtecRpmScalars() {
		if (this.rpmVtecScalars != null) return this.rpmVtecScalars;

		this.rpmVtecScalars = new Integer[RomP28Constants.sNrRpmScalars];

		for (int rpm = 0; rpm < RomP28Constants.sNrRpmScalars; rpm++)
			// http://www.pgmfi.org/twiki/bin/view/Library/OBD1_8bitHighCamRPM
			this.rpmVtecScalars[rpm] = Integer.valueOf((1875000 * this.readUnsignedByteAt(Integer.valueOf(RomP28Constants.sAddressHighCamRPMScalars + rpm))) / 53248);

		return this.getVtecRpmScalars();
	}
}
