package net.sf.tunetoy.model.internal;

import net.sf.tunetoy.model.IRom;
import net.sf.tunetoy.model.IRomOBD0;
import net.sf.tunetoy.model.InvalidRomException;
import net.sf.tunetoy.model.TableType;
import net.sf.tunetoy.model.WrongRomHandlerException;

public class RomP04 extends RomOBD0 implements IRomOBD0 {
	class RomP04Capabilities extends RomCapabilities {
		RomP04Capabilities() {
			this.capVtec = false;
			this.capEld  = false;
		}

		@Override
		public Boolean isEldEnabled() {
			// TODO Auto-generated method stub
			return false;
		}

		@Override
		public Boolean isVtecEnabled() {
			// TODO Auto-generated method stub
			return false;
		}

		@Override
		public Boolean hasBoostTools() {
			// TODO Auto-generated method stub
			return false;
		}
	}

	class RomP04NonVtecFuelMap extends RomFuelMap {
		// FIXME: Implement me
	}

	class RomP04NonVtecIgnitionMap extends RomIgnitionMap {
		// FIXME: Implement me
	}

	class RomP04NonVtecLambdaMap extends RomLambdaMap {
		// FIXME: Implement me
	}

	public RomP04(String filename) throws InvalidRomException, WrongRomHandlerException {
		super(IRom.RomTypes.P04);

		try {
			this.readRom(filename);
		} catch (Exception e) {
			throw new InvalidRomException();
		}

		if (this.readUnsignedByteAt(0) != 178 || this.readUnsignedByteAt(1) != 20) throw new WrongRomHandlerException();

		this.romCaps = new RomP04Capabilities();
		this.romMaps.put(TableType.FUEL_NO_VTEC, new RomP04NonVtecFuelMap());
		this.romMaps.put(TableType.IGNITION_NO_VTEC, new RomP04NonVtecIgnitionMap());
		this.romMaps.put(TableType.LAMBDA_NO_VTEC, new RomP04NonVtecLambdaMap());
	}
}
