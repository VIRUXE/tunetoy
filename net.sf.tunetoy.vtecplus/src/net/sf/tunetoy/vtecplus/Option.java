package net.sf.tunetoy.vtecplus;

import net.sf.tunetoy.RomOption;
import net.sf.tunetoy.model.IRom;
import net.sf.tunetoy.model.IRomP30;
import net.sf.tunetoy.model.InvalidRomException;
import net.sf.tunetoy.model.TableType;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.FocusAdapter;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

public class Option extends RomOption {
	IRom currentRom = null;

	public Option() {
		super();
	}

	@Override
	public void buildGUI(Composite composite) {
		// TODO Auto-generated method stub
		composite.setLayout(new GridLayout(2, true));

		Composite capabilitiesgroup = composite;
		if (this.currentRom instanceof IRomP30) // FIXME: Use capabilities!!
		// VtecPlus pure for testing
		// reasons
		{
			final IRomP30 romp30 = (IRomP30) this.currentRom;

			final Button vb = new Button(capabilitiesgroup, SWT.CHECK);
			vb.setText("Enable VtecPlus (irreversible)"); //$NON-NLS-1$
			vb.setSelection(this.hasVtecPlus());
			if (this.hasVtecPlus()) {
				vb.setEnabled(false);
			}

			vb.addSelectionListener(new SelectionAdapter() {
				@Override
				public void widgetSelected(SelectionEvent e) {
					if (vb.getSelection()) {
						enableVtecPlus();
						vb.setEnabled(false);
					}
				}
			});

			Label lpad = new Label(capabilitiesgroup, SWT.NONE);
			lpad.setText(""); //$NON-NLS-1$

			final Integer mapscalars[] = romp30.getMap(TableType.FUEL_VTEC)
					.getMapScalars();
			final Integer crossovers[] = this.getVtecPlusCrossOvers();

			for (int i = 0; i < mapscalars.length; i++) {
				final Label l = new Label(capabilitiesgroup, SWT.NONE);
				l.setText("MAP " + mapscalars[i] + "mbar: "); //$NON-NLS-1$ //$NON-NLS-2$

				final Text t = new Text(capabilitiesgroup, SWT.SINGLE);
				final int index = i;
				t.setText(crossovers[i].toString());
				t.addModifyListener(new ModifyListener() {
					public void modifyText(ModifyEvent e) {
						if (t.getText().length() == 0)
							return;

						Integer rpm = new Integer(t.getText());

						if (new Integer(t.getText()) != 0) {
							crossovers[index] = rpm;
							Option.this.setVtecPlusCrossOvers(crossovers);
						}
					}
				});
				t.addFocusListener(new FocusAdapter() {
					@Override
					public void focusLost(FocusEvent e) {
						t.setText(Option.this.getVtecPlusCrossOvers()[index]
								.toString());
					}

				});
			}
			/*
			 * final Float[] fuel = romp30.getFuelEnrichenment(); for (int i =
			 * 0; i < 4; i++) { final int index = i; Label l = new
			 * Label(capabilitiesgroup, SWT.NONE); l.setText("Fuel trim cyl#" +
			 * new Integer(i + 1).toString()); //$NON-NLS-1$
			 * 
			 * final Text t = new Text(capabilitiesgroup, SWT.SINGLE);
			 * t.setText(fuel[i].toString());
			 * 
			 * t.addModifyListener(new ModifyListener() { public void
			 * modifyText(ModifyEvent e) { if (t.getText().length() == 0)
			 * return;
			 * 
			 * Float trim = new Float(t.getText());
			 * 
			 * if (trim != 0.0 && trim > 0.0 && trim < 2.0) { fuel[index] =
			 * trim; romp30.setFuelEnrichenment(fuel); } } });
			 * t.addFocusListener(new FocusListener() { public void
			 * focusGained(FocusEvent e) { // TODO Auto-generated method stub }
			 * 
			 * public void focusLost(FocusEvent e) {
			 * t.setText(romp30.getFuelEnrichenment()[index] .toString()); }
			 * 
			 * }); }
			 */
		}
	}

	@Override
	public void setRom(IRom rom) throws InvalidRomException {
		if (!(rom instanceof IRomP30))
			throw new InvalidRomException();

		this.currentRom = rom;
	}

	public Boolean hasVtecPlus() {
		return (this.currentRom.readUnsignedByteAt(0x1166) == 0xc5)
				&& (this.currentRom.readUnsignedByteAt(0x1167) == 0xc1)
				&& (this.currentRom.readUnsignedByteAt(0x1168) == 0xc0)
				&& (this.currentRom.readUnsignedByteAt(0x1169) == 0x44);
	}

	public void enableVtecPlus() {
		if (this.hasVtecPlus())

		// FIXME: Pull up to super class - move to plugin
		{
			System.out.println("Already has VTEC plus"); //$NON-NLS-1$
			return;
		}

		this.currentRom.writeArray(0x1166, new Integer[] { 0xc5, 0xc1, 0xc0,
				0x44, 0xcd, 0x29, 0x90, 0x9d, 0x10, 0x60, 0xce, 0xd, 0x77, 0xa,
				0x98, 0x19, 0xeb, 0x16, 0x1, 0x78, 0xc5, 0xb4, 0xc2, 0xca,
				0x16, 0x62, 0xd2, 0x1, 0xf2, 0xb5, 0x6, 0xa9, 0xaf, 0x11, 0xc5,
				0x7, 0xc3, 0xac, 0xca, 0x7, 0xc7, 0xac, 0xc8, 0x10, 0x3, 0x34,
				0x12, 0xc5, 0x22, 0xd0, 0xfc, 0xc4, 0x1f, 0xa, 0xc4, 0x1f, 0x9,
				0x3, 0x34, 0x12, 0xc5, 0x22, 0xe0, 0x3, 0xc4, 0x1f, 0x1a, 0xc4,
				0x1f, 0x19, 0x3, 0x34, 0x12, 0xcd, 0xca, 0xcd, 0xca, 0xcd,
				0xca, 0xcd, 0xca, 0xcd, 0xca, 0xcd, 0xca, 0xcd, 0xca, 0xcd,
				0xca, 0xcd, 0xca, 0xcd, 0xca, 0xcd, 0xca, 0xcd, 0xca, 0xcd,
				0xca, 0xcd, 0xca, 0xcd, 0xca, 0xcd, 0xca, 0xcd, 0xca, 0xcd,
				0xca, 0xcd, 0xca, 0xcd, 0xca, 0xcd, 0xca, 0xcd, 0xca, 0xcd,
				0xca, 0xcd, 0xca, 0xcd, 0xca, 0xcd, 0xca, 0xcd, 0xca, 0xcd,
				0xca, 0xcd, 0xca, 0xcd, 0xca });
		this.currentRom.writeArray(0x1234, new Integer[] { 0x0 });
		this.currentRom.writeArray(0x6010, new Integer[] { 0x0 });

	}

	public Integer[] getVtecPlusCrossOvers() {
		// FIXME: clean up
		Integer values[] = new Integer[this.currentRom.getMap(
				TableType.FUEL_NO_VTEC).getMapScalars().length];
		Integer address = 0x1166 + 73;
		for (int i = 0; i < this.currentRom.getMap(TableType.FUEL_NO_VTEC)
				.getMapScalars().length; i++)
			values[i] = (1875000 * this.currentRom.readUnsignedByteAt(address
					+ 2 * i)) / 53248;
		return values;
	}

	public void setVtecPlusCrossOvers(Integer[] values) {
		Integer address = 0x1166 + 73;
		for (int i = 0; i < this.currentRom.getMap(TableType.FUEL_NO_VTEC)
				.getMapScalars().length; i++) {
			this.currentRom.writeByte((address + 2 * i),
					(values[i] * 53248) / 1875000 + 1);
			this.currentRom.writeByte((address + 2 * i + 1),
					(values[i] * 53248) / 1875000 - 2);
		}
	}

}
