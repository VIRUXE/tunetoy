package net.sf.tunetoy.model.internal;

import net.sf.tunetoy.model.IRom;
import net.sf.tunetoy.model.IRomP13;
import net.sf.tunetoy.model.InvalidRomException;
import net.sf.tunetoy.model.TableType;
import net.sf.tunetoy.model.WrongRomHandlerException;

public class RomP13 extends RomOBD1 implements IRomP13 {
	class RomP13Capabilities extends RomCapabilities {
		RomP13Capabilities() {
			this.capVtec = true;
			this.capEld  = false;  // FIXME: Unknown
		}

		@Override
		public Boolean isEldEnabled() {
			return false; // FIXME: Unknown
		}

		@Override
		public Boolean isVtecEnabled() {
			return true; // FIXME: Make this dynamic
		}

		@Override
		public Boolean hasBoostTools() {
			return false; // No support for boost tools in P13, use P30 instead
		}

	}

	class RomP13Constants {
		private static final int sAddressHighCamFuelMultiplierTable = 0x6a4f;
		private static final int sAddressHighCamFuelTable = 0x6172;
		private static final int sAddressHighCamIgnitionAdvanceTable = 0x659C;
		private static final int sAddressHighCamRPMScalars = 0x6028;
		private static final int sAddressLowCamFuelMultiplierTable = 0x6a4f;
		private static final int sAddressLowCamFuelTable = 0x60AA;
		private static final int sAddressLowCamIgnitionAdvanceTable = 0x63f8;
		private static final int sAddressLowCamRPMScalars = 0x6000;
		private static final int sNrMapScalars = 10;
		private static final int sNrRpmScalars = 20;
	}

	class RomP13NonVtecFuelMap extends RomFuelMap {
		public RomP13NonVtecFuelMap() {
			this.values     = new Float[RomP13Constants.sNrMapScalars][RomP13Constants.sNrRpmScalars];
			this.mapScalars = RomP13.this.getMapScalars();
			this.rpmScalars = RomP13.this.getNonVtecRpmScalars();

			for (int map = 0; map < this.mapScalars.length; map++)
				for (int rpm = 0; rpm < this.rpmScalars.length; rpm++) {
					var addressFuelTableLocation = Integer.valueOf(RomP13Constants.sAddressLowCamFuelTable + rpm* RomP13Constants.sNrMapScalars + map);
					var addressFuelMultiplierTable = Integer.valueOf(RomP13Constants.sAddressLowCamFuelMultiplierTable+ map);

					this.values[map][rpm] = (readUnsignedByteAt(addressFuelTableLocation) * readUnsignedByteAt(addressFuelMultiplierTable)) / 4.f;
				}
		}
	}

	class RomP13NonVtecIgnitionMap extends RomIgnitionMap {
		public RomP13NonVtecIgnitionMap() {
			this.values     = new Float[RomP13Constants.sNrMapScalars][RomP13Constants.sNrRpmScalars];
			this.mapScalars = RomP13.this.getMapScalars();
			this.rpmScalars = RomP13.this.getNonVtecRpmScalars();

			for (int map = 0; map < this.mapScalars.length; map++)
				for (int rpm = 0; rpm < this.rpmScalars.length; rpm++)
					this.values[map][rpm] = (readUnsignedByteAt(RomP13Constants.sAddressLowCamIgnitionAdvanceTable + rpm * RomP13Constants.sNrMapScalars + map) - 24) / 4.f;
		}
	}

	class RomP13VtecFuelMap extends RomFuelMap {
		public RomP13VtecFuelMap() {
			this.values     = new Float[RomP13Constants.sNrMapScalars][RomP13Constants.sNrRpmScalars];
			this.mapScalars = RomP13.this.getMapScalars();
			this.rpmScalars = RomP13.this.getVtecRpmScalars();

			for (int map = 0; map < this.mapScalars.length; map++)
				for (int rpm = 0; rpm < this.rpmScalars.length; rpm++) {
					var addressFuelTableLocation   = Integer.valueOf(RomP13Constants.sAddressHighCamFuelTable + rpm * RomP13Constants.sNrMapScalars + map);
					var addressFuelMultiplierTable = Integer.valueOf(RomP13Constants.sAddressHighCamFuelMultiplierTable + map);

					this.values[map][rpm] = (readUnsignedByteAt(addressFuelTableLocation) * readUnsignedByteAt(addressFuelMultiplierTable)) / 4.f;
				}
		}
	}

	class RomP13VtecIgnitionMap extends RomIgnitionMap {
		public RomP13VtecIgnitionMap() {
			this.values     = new Float[RomP13Constants.sNrMapScalars][RomP13Constants.sNrRpmScalars];
			this.mapScalars = RomP13.this.getMapScalars();
			this.rpmScalars = RomP13.this.getVtecRpmScalars();

			for (int map = 0; map < this.mapScalars.length; map++)
				for (int rpm = 0; rpm < this.rpmScalars.length; rpm++)
					this.values[map][rpm] = (readUnsignedByteAt(RomP13Constants.sAddressHighCamIgnitionAdvanceTable + rpm * RomP13Constants.sNrMapScalars + map) - 24) / 4.f;
		}
	}

	public RomP13(String filename) throws InvalidRomException, WrongRomHandlerException {
		super(IRom.RomTypes.P13);

		try {
			this.readRom(filename);
		} catch (Exception e) {
			throw new InvalidRomException();
		}

		if (this.readUnsignedByteAt(Integer.valueOf(0)) != 131 || this.readUnsignedByteAt(Integer.valueOf(1)) != 7) throw new WrongRomHandlerException();

		this.romMaps.put(TableType.FUEL_VTEC, new RomP13VtecFuelMap());
		this.romMaps.put(TableType.FUEL_NO_VTEC, new RomP13NonVtecFuelMap());
		this.romMaps.put(TableType.IGNITION_VTEC, new RomP13VtecIgnitionMap());
		this.romMaps.put(TableType.IGNITION_NO_VTEC, new RomP13NonVtecIgnitionMap());
		this.romCaps = new RomP13Capabilities();
	}

	protected Integer[] getMapScalars() {
		var result = new Integer[RomP13Constants.sNrMapScalars];
		for (int x = 0; x < RomP13Constants.sNrMapScalars; x++) result[x] = x;
		return result;
	}

	protected Integer[] getNonVtecRpmScalars() {
		var result = new Integer[RomP13Constants.sNrRpmScalars];

		for (int offset = 0; offset < RomP13Constants.sNrRpmScalars; offset++) {
			var addressRPMScalar = RomP13Constants.sAddressLowCamRPMScalars + offset * 2;
			result[offset] = 1875000 / this.readUnsignedWordAt(addressRPMScalar);
		}

		return result;
	}

	protected Integer[] getVtecRpmScalars() {
		var result = new Integer[RomP13Constants.sNrRpmScalars];

		for (int offset = 0; offset < RomP13Constants.sNrRpmScalars; offset++) {
			var addressRPMScalar = Integer.valueOf(RomP13Constants.sAddressHighCamRPMScalars + offset * 2);
			result[offset] = 1875000 / this.readUnsignedWordAt(addressRPMScalar);
		}

		return result;
	}
}
