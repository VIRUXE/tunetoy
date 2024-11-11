package net.sf.tunetoy.model;

import java.util.ArrayList;

import net.sf.tunetoy.model.internal.RomP04;
import net.sf.tunetoy.model.internal.RomP13;
import net.sf.tunetoy.model.internal.RomP28;
import net.sf.tunetoy.model.internal.RomP30;

public class RomFactory {
	private static ArrayList<IRom> loadedroms = new ArrayList<IRom>();

	private static Class[] supportedroms = { RomP13.class, RomP28.class, RomP30.class, RomP04.class };

	/**
	 * @return all loaded roms
	 */
	public static IRom[] getLoadedRoms() { return loadedroms.toArray(new IRom[] {}); }

	/**
	 * @param filename
	 *            The full pathname of the rom to load
	 * @return An instance of the loaded rom
	 * @throws InvalidRomException
	 */
	public static IRom loadRom(String filename) throws InvalidRomException {
		for (int i = 0; i < supportedroms.length; i++) 
			try {
				var rom = (IRom) supportedroms[i].getConstructor(new Class[] { String.class }).newInstance(new Object[] { filename });
				RomFactory.loadedroms.add(rom);
				return rom;
			} catch (Exception e) {
				// Fall through
			}

		// If none of the rom handlers can handle this file, throw an exception
		throw new InvalidRomException();
	}
}
