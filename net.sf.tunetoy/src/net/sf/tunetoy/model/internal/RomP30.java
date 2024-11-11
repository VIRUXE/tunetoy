package net.sf.tunetoy.model.internal;

import net.sf.tunetoy.model.IRom;
import net.sf.tunetoy.model.IRomP30;
import net.sf.tunetoy.model.InvalidRomException;
import net.sf.tunetoy.model.TableType;
import net.sf.tunetoy.model.WrongRomHandlerException;

public class RomP30 extends RomOBD1 implements IRomP30 {
	class RomP30Capabilities extends RomCapabilities {
		RomP30Capabilities() {
			this.capVtec = true;
			this.capEld = true;
		}

		@Override
		public Boolean hasBoostTools() {
			RomP30 rom = RomP30.this;
			return ((rom.readUnsignedWordAt(0x0829) == 0xA3F5)
					&& (rom.readUnsignedWordAt(0x0A13) == 0xA3F5)
					&& (rom.readUnsignedWordAt(0x0A17) != 0x6E72)
					&& (rom.readUnsignedWordAt(0x1277) == 0x78B9)
					&& (rom.readUnsignedWordAt(0x15A9) == 0xA3C5)
					&& (rom.readUnsignedWordAt(0x1BA5) == 0x78C9)
					&& (rom.readUnsignedWordAt(0x140F) == 0x78DF) && (rom
					.readUnsignedWordAt(0x1EEF) == 0x78F2));
		}

		@Override
		public Boolean isEldEnabled() {
			return RomP30.this
					.readUnsignedByteAt(RomP30.RomP30Constants.sAddressEldDisable) == 0xFF;
		}

		@Override
		public Boolean isVtecEnabled() {
			return RomP30.this
					.readUnsignedByteAt(RomP30.RomP30Constants.sAddressVtecDisable) == 0x00;
		}

	}

	class RomP30Constants {

		// 0x00 enables, 0xFF disables
		private static final int sAddressEldDisable = 0x6005;

		private static final int sAddressHighCamFuelMultiplierTable = 0x71CC;

		private static final int sAddressHighCamFuelTable = 0x7104;

		private static final int sAddressHighCamIgnitionAdvanceTable = 0x7316;

		private static final int sAddressHighCamLambdaTable = 0x7104;

		private static final int sAddressHighCamRPMScalars = 0x701e;

		private static final int sAddressLowCamFuelMultiplierTable = 0x70FA;

		private static final int sAddressLowCamFuelTable = 0x7032;

		private static final int sAddressLowCamIgnitionAdvanceTable = 0x724E;

		private static final int sAddressLowCamLambdaTable = 0x7032;

		private static final int sAddressLowCamRPMScalars = 0x700a;

		private static final int sAddressMAPScalars = 0x7000;

		// 0xFF enables, 0x00 disables
		private static final int sAddressVtecDisable = 0x6001;

		private static final int sNrRpmScalars = 20;

		// TODO: find this out
		// private static final int sAddressHighCamRPMScalarDivisor = 0x0;
	}

	class RomP30NonVtecFuelMap extends RomFuelMap {
		public RomP30NonVtecFuelMap() {
			this.values = new Float[RomP30.this.getNrMapScalars()][RomP30.RomP30Constants.sNrRpmScalars];
			this.mapScalars = RomP30.this.getMapScalars();
			this.rpmScalars = RomP30.this.getNonVtecRpmScalars();

			for (int map = 0; map < this.mapScalars.length; map++)
				for (int rpm = 0; rpm < this.rpmScalars.length; rpm++) {
					Integer addressFuelTableLocation = new Integer(RomP30.this
							.addressLowCamFuelTable()
							+ rpm * RomP30.this.getNrMapScalars() + map);
					Integer addressFuelMultiplierTable = new Integer(
							RomP30.this.addressLowCamFuelMultiplierTable()
									+ map);

					this.values[map][rpm] = new Float(
							(readUnsignedByteAt(addressFuelTableLocation) * readUnsignedByteAt(addressFuelMultiplierTable)) / 4.f);
				}
		}
	}

	class RomP30NonVtecIgnitionMap extends RomIgnitionMap {
		public RomP30NonVtecIgnitionMap() {
			this.values = new Float[RomP30.this.getNrMapScalars()][RomP30.RomP30Constants.sNrRpmScalars];
			this.mapScalars = RomP30.this.getMapScalars();
			this.rpmScalars = RomP30.this.getNonVtecRpmScalars();

			for (int map = 0; map < this.mapScalars.length; map++)
				for (int rpm = 0; rpm < this.rpmScalars.length; rpm++) {
					this.values[map][rpm] = (float) ((readUnsignedByteAt(RomP30.this
							.addressLowCamIgnitionAdvanceTable()
							+ rpm * RomP30.this.getNrMapScalars() + map) - 24) / 4.);
				}
		}
	}

	class RomP30NonVtecLambdaMap extends RomLambdaMap {
		public RomP30NonVtecLambdaMap() {
			this.values = new Float[RomP30.this.getNrMapScalars()][RomP30.RomP30Constants.sNrRpmScalars];
			this.mapScalars = RomP30.this.getMapScalars();
			this.rpmScalars = RomP30.this.getNonVtecRpmScalars();

			for (int map = 0; map < this.mapScalars.length; map++)
				for (int rpm = 0; rpm < this.rpmScalars.length; rpm++) {
					// FIXME: Calculations are off
					this.values[map][rpm] = (float) ((255 - readUnsignedByteAt(RomP30.this
							.addressLowCamLambdaTable()
							+ rpm * RomP30.this.getNrMapScalars() + map)) * 0.123);
				}
		}
	}

	class RomP30VtecFuelMap extends RomFuelMap {
		public RomP30VtecFuelMap() {
			this.values = new Float[RomP30.this.getNrMapScalars()][RomP30.RomP30Constants.sNrRpmScalars];
			this.mapScalars = RomP30.this.getMapScalars();
			this.rpmScalars = RomP30.this.getVtecRpmScalars();

			for (int map = 0; map < this.mapScalars.length; map++)
				for (int rpm = 0; rpm < this.rpmScalars.length; rpm++) {
					Integer addressFuelTableLocation = new Integer(RomP30.this
							.addressHighCamFuelTable()
							+ rpm * RomP30.this.getNrMapScalars() + map);
					Integer addressFuelMultiplierTable = new Integer(
							RomP30.this.addressHighCamFuelMultiplierTable()
									+ map);

					this.values[map][rpm] = new Float(
							(readUnsignedByteAt(addressFuelTableLocation) * readUnsignedByteAt(addressFuelMultiplierTable)) / 4.f);
				}
		}
	}

	class RomP30VtecIgnitionMap extends RomIgnitionMap {
		public RomP30VtecIgnitionMap() {
			this.values = new Float[RomP30.this.getNrMapScalars()][RomP30.RomP30Constants.sNrRpmScalars];
			this.mapScalars = RomP30.this.getMapScalars();
			this.rpmScalars = RomP30.this.getVtecRpmScalars();

			for (int map = 0; map < this.mapScalars.length; map++)
				for (int rpm = 0; rpm < this.rpmScalars.length; rpm++) {
					this.values[map][rpm] = new Float(
							(readUnsignedByteAt(new Integer(RomP30.this
									.addressHighCamIgnitionAdvanceTable()
									+ rpm * RomP30.this.getNrMapScalars() + map)) - 24) / 4.);
				}
		}
	}

	class RomP30VtecLambdaMap extends RomLambdaMap {
		public RomP30VtecLambdaMap() {
			this.values = new Float[RomP30.this.getNrMapScalars()][RomP30.RomP30Constants.sNrRpmScalars];
			this.mapScalars = RomP30.this.getMapScalars();
			this.rpmScalars = RomP30.this.getVtecRpmScalars();

			for (int map = 0; map < this.mapScalars.length; map++)
				for (int rpm = 0; rpm < this.rpmScalars.length; rpm++) {
					this.values[map][rpm] = (float) ((255 - readUnsignedByteAt(RomP30.this
							.addressHighCamLambdaTable()
							+ rpm * RomP30.this.getNrMapScalars() + map)) * 0.123); // FIXME:
					// Calculations
					// are
					// off
				}
		}
	}

	protected Integer addressHighCamFuelMultiplierTableBoost;

	protected Integer addressHighCamFuelTableBoost;

	protected Integer addressHighCamIgnitionTableBoost;

	protected Integer addressHighCamLambdaTableBoost;

	protected Integer addressLowCamFuelMultiplierTableBoost;

	protected Integer addressLowCamFuelTableBoost;

	protected Integer addressLowCamIgnitionTableBoost;

	protected Integer addressLowCamLambdaTableBoost;

	private Integer[] mapScalars = null;

	private Integer[] rpmNonVtecScalars = null;

	private Integer[] rpmVtecScalars = null;

	public RomP30(String filename) throws InvalidRomException,
			WrongRomHandlerException {
		super(IRom.RomTypes.P30);
		try {
			this.readRom(filename);
		} catch (Exception e) {
			throw new InvalidRomException();
		}
		/*
		 * for (int i = 1; i < 32767; i++) { Integer lb =
		 * this.readUnsignedByteAt(i - 1); Integer b =
		 * this.readUnsignedByteAt(i); Integer nb = this.readUnsignedByteAt(i +
		 * 1);
		 * 
		 * if (b != 255) { if (lb == 255) System.out .print("this.writeArray(0x" +
		 * Integer.toHexString(i) + ", { "); //$NON-NLS-1$ //$NON-NLS-2$
		 * System.out.print("0x" + Integer.toHexString(b)); //$NON-NLS-1$ if (nb ==
		 * 255) System.out.println(" });"); //$NON-NLS-1$ else
		 * System.out.print(", "); //$NON-NLS-1$ } }
		 */

		if (this.readUnsignedByteAt(0) != 226
				|| this.readUnsignedByteAt(1) != 33)
			throw new WrongRomHandlerException();
		this.romCaps = new RomP30Capabilities();
		if (this.getCapabilities().hasBoostTools())
			this.calculateAdvancedBoostMemoryLocations();
		this.romMaps.put(TableType.FUEL_VTEC, new RomP30VtecFuelMap());
		this.romMaps.put(TableType.FUEL_NO_VTEC, new RomP30NonVtecFuelMap());
		this.romMaps.put(TableType.IGNITION_VTEC, new RomP30VtecIgnitionMap());
		this.romMaps.put(TableType.IGNITION_NO_VTEC,
				new RomP30NonVtecIgnitionMap());
		this.romMaps.put(TableType.LAMBDA_VTEC, new RomP30VtecLambdaMap());
		this.romMaps
				.put(TableType.LAMBDA_NO_VTEC, new RomP30NonVtecLambdaMap());
	}

	@Override
	public String getName() {
		String name = super.getName();
		if (this.getCapabilities().hasBoostTools())
			name = name + " + Advanced Boost Tools "; //$NON-NLS-1$
		return name;
	}

	protected Integer addressHighCamFuelMultiplierTable() {
		if (this.getCapabilities().hasBoostTools()) {
			return this.addressHighCamFuelMultiplierTableBoost;
		}
		return RomP30.RomP30Constants.sAddressHighCamFuelMultiplierTable;
	}

	protected Integer addressHighCamFuelTable() {
		if (this.getCapabilities().hasBoostTools()) {
			return this.addressHighCamFuelTableBoost;
		}
		return RomP30.RomP30Constants.sAddressHighCamFuelTable;
	}

	protected Integer addressHighCamIgnitionAdvanceTable() {
		if (this.getCapabilities().hasBoostTools()) {
			return this.addressHighCamIgnitionTableBoost;
		}
		return RomP30.RomP30Constants.sAddressHighCamIgnitionAdvanceTable;
	}

	protected Integer addressHighCamLambdaTable() {
		if (this.getCapabilities().hasBoostTools()) {
			return this.addressHighCamLambdaTableBoost;
		}
		return RomP30.RomP30Constants.sAddressHighCamLambdaTable;
	}

	protected Integer addressLowCamFuelMultiplierTable() {
		if (this.getCapabilities().hasBoostTools()) {
			return this.addressLowCamFuelMultiplierTableBoost;
		}
		return RomP30.RomP30Constants.sAddressLowCamFuelMultiplierTable;
	}

	protected Integer addressLowCamFuelTable() {
		if (this.getCapabilities().hasBoostTools()) {
			return this.addressLowCamFuelTableBoost;
		}
		return RomP30.RomP30Constants.sAddressLowCamFuelTable;
	}

	protected Integer addressLowCamIgnitionAdvanceTable() {
		if (this.getCapabilities().hasBoostTools()) {
			return this.addressLowCamIgnitionTableBoost;
		}
		return RomP30.RomP30Constants.sAddressLowCamIgnitionAdvanceTable;
	}

	protected Integer addressLowCamLambdaTable() {
		if (this.getCapabilities().hasBoostTools()) {
			return this.addressLowCamLambdaTableBoost;
		}
		return RomP30.RomP30Constants.sAddressLowCamLambdaTable;
	}

	protected void calculateAdvancedBoostMemoryLocations() {
		Integer boostColumns = this.getNrMapScalars() - 0x10;
		Integer HiRevTblCor = this.getVtecFuelTableHeight() - 0x14;
		Integer LoRevTblCor = this.getNonVtecFuelTableHeight() - 0x14;

		Integer HiLmbTblCor = boostColumns * this.getVtecLambdaTableHeight()
				+ HiRevTblCor * 0x10;
		Integer LoLmbTblCor = boostColumns * this.getNonVtecLambdaTableHeight()
				+ LoRevTblCor * 0x10;
		Integer HiIgnTblCor = boostColumns * this.getVtecIgnitionTableHeight()
				+ HiRevTblCor * 0x10;
		Integer LoIgnTblCor = boostColumns
				* this.getNonVtecIgnitionTableHeight() + LoRevTblCor * 0x10;
		Integer HiFulTblCor = boostColumns
				* (this.getVtecFuelTableHeight() + 1) + HiRevTblCor * 0x10;
		Integer LoFulTblCor = boostColumns
				* (this.getNonVtecFuelTableHeight() + 1) + LoRevTblCor * 0x10;
		LoLmbTblCor = LoLmbTblCor + HiLmbTblCor;
		LoIgnTblCor = HiIgnTblCor + LoIgnTblCor;
		HiFulTblCor = LoIgnTblCor + HiFulTblCor;
		LoFulTblCor = HiFulTblCor + LoFulTblCor;
		HiRevTblCor = LoFulTblCor + HiRevTblCor;
		LoRevTblCor = HiRevTblCor + LoRevTblCor;

		// Integer ScalarCor = LoRevTblCor + boostColumns;
		// rom.addressOf('NFO_LOW_TABLE') = 0x1400 + MapSize;
		// rom.addressOf('NFO_HIGH_TABLE') = 0x1400 + MapSize;
		// rom.addressOf('LOW_REV_SCALAR') = 0x7102 - LoRevTblCor;
		// rom.addressOf('HIGH_REV_SCALAR') = 0x7116 - HiRevTblCor;
		this.addressLowCamFuelTableBoost = 0x712A - LoFulTblCor;
		this.addressHighCamFuelTableBoost = 0x727A - HiFulTblCor;
		this.addressLowCamIgnitionTableBoost = 0x73CA - LoIgnTblCor;
		this.addressHighCamIgnitionTableBoost = 0x750A - HiIgnTblCor;
		this.addressLowCamLambdaTableBoost = 0x7D7F - LoLmbTblCor;
		this.addressHighCamLambdaTableBoost = 0x7EBF - HiLmbTblCor;
		this.addressLowCamFuelMultiplierTableBoost = this.addressLowCamFuelTableBoost
				+ this.getNrMapScalars() * this.getNonVtecFuelTableHeight();
		this.addressHighCamFuelMultiplierTableBoost = this.addressHighCamFuelTableBoost
				+ this.getNrMapScalars() * this.getVtecFuelTableHeight();

	}

	protected Integer[] getMapScalars() {
		if (this.mapScalars != null)
			return this.mapScalars;

		this.mapScalars = new Integer[this.getNrMapScalars()];

		if (this.getCapabilities().hasBoostTools()) {
			for (int map = 0; map < this.getNrMapScalars(); map++) {
				int map_min = this.readUnsignedWordAt(0x70EE - 664) - 0x8000;
				int map_max = this.readUnsignedWordAt(0x70F0 - 664) - 0x8000;
				int byteval = this.readUnsignedByteAt(0x6e5a + map);
				this.mapScalars[map] = (byteval * (map_max - map_min)) / 255
						+ map_min;
			}
		} else {
			for (int map = 0; map < this.getNrMapScalars(); map++)
				this.mapScalars[map] = (1764 * readUnsignedByteAt(RomP30.RomP30Constants.sAddressMAPScalars
						+ map)) / 255;
		}
		return this.getMapScalars();
	}

	protected Integer getNonVtecFuelTableHeight() {
		return this.readUnsignedByteAt(0x0B3C);
	}

	protected Integer getNonVtecIgnitionTableHeight() {
		return this.readUnsignedByteAt(0x0B3C);
	}

	protected Integer getNonVtecLambdaTableHeight() {
		return this.readUnsignedByteAt(0x0B3C);
	}

	protected Integer[] getNonVtecRpmScalars() {
		if (this.rpmNonVtecScalars != null)
			return this.rpmNonVtecScalars;

		this.rpmNonVtecScalars = new Integer[RomP30.RomP30Constants.sNrRpmScalars];
		for (int rpm = 0; rpm < RomP30.RomP30Constants.sNrRpmScalars; rpm++) {
			// http://www.pgmfi.org/twiki/bin/view/Library/OBD1_8bitLowCamRPM
			// FIXME: Calculations are a bit off???
			int value = readUnsignedByteAt(new Integer(
					RomP30.RomP30Constants.sAddressLowCamRPMScalars + rpm));

			int h = value / 64;
			int x = h - 1 < 0 ? 0 : h - 1;
			int l = value + (x * 64);
			int r = (1875000 * l * 2 ^ h) / 240000;
			this.rpmNonVtecScalars[rpm] = new Integer(r);
		}

		return this.getNonVtecRpmScalars();
	}

	protected Integer getNrMapScalars() {
		return this.readUnsignedByteAt(0x0B3A);
	}

	protected Integer getNrOfExtraColumns() {
		return this.getNrMapScalars() - 0x10;
	}

	protected Integer getVtecFuelTableHeight() {
		return this.readUnsignedByteAt(0x0B3C);
	}

	protected Integer getVtecIgnitionTableHeight() {
		return this.readUnsignedByteAt(0x0B3C);
	}

	protected Integer getVtecLambdaTableHeight() {
		return this.readUnsignedByteAt(0x0B3C);
	}

	protected Integer[] getVtecRpmScalars() {
		if (this.rpmVtecScalars != null)
			return this.rpmVtecScalars;

		this.rpmVtecScalars = new Integer[RomP30.RomP30Constants.sNrRpmScalars];

		Integer address = this.getCapabilities().hasBoostTools() ? 0x7102 - this
				.getVtecIgnitionTableHeight() + 0x14
				: RomP30.RomP30Constants.sAddressHighCamRPMScalars;

		for (int rpm = 0; rpm < RomP30.RomP30Constants.sNrRpmScalars; rpm++) {

			// http://www.pgmfi.org/twiki/bin/view/Library/OBD1_8bitHighCamRPM
			this.rpmVtecScalars[rpm] = (1875000 * this
					.readUnsignedByteAt(address + rpm)) / 53248;
		}

		return this.getVtecRpmScalars();
	}


	public Float[] getFuelEnrichenment() {
		Float[] values = new Float[4];
		// 1342
		values[0] = this.readUnsignedByteAt(0x61ba) / 128.0f;
		values[2] = this.readUnsignedByteAt(0x61bb) / 128.0f;
		values[3] = this.readUnsignedByteAt(0x61bc) / 128.0f;
		values[1] = this.readUnsignedByteAt(0x61bd) / 128.0f;
		return values;
	}

	public void setFuelEnrichenment(Float[] fuel) {
		this.writeByte(0x61ba, (int) (fuel[0] * 128.0f));
		this.writeByte(0x61bb, (int) (fuel[2] * 128.0f));
		this.writeByte(0x61bc, (int) (fuel[3] * 128.0f));
		this.writeByte(0x61bd, (int) (fuel[1] * 128.0f));
	}

}
