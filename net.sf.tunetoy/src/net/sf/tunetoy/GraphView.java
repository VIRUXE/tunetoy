/**
 * 
 */
package net.sf.tunetoy;

import net.sf.tunetoy.model.IRom;
import net.sf.tunetoy.model.IRomMap;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Canvas;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.part.ViewPart;

public class GraphView extends ViewPart {
	float angle = 0.0f;
	public static final String ID = "net.sf.tunetoy.GraphView"; //$NON-NLS-1$
	protected IRomMap romMap = null;
	protected IRom currentRom = null;

	class GraphPainter implements PaintListener {
		GC gc = null;

		final int[] pointZero = { 35, 95 };
		final int[] pointMaxMap = { 5, 85 };
		final int[] pointMaxRPM = { 95, 85 };
		final int[] pointMaxMapMaxRPM = { 65, 75 };
		final int graphHeightPerc = 35;

		public GraphPainter(GC gc) { this.gc = gc; }

		public float getMaxValue() {
			float maxValue = 0;

			for (int x = 0; x < GraphView.this.romMap.getMapScalars().length; x++)
				for (int y = 0; y < GraphView.this.romMap.getRpmScalars().length; y++)
					maxValue = Math.max(maxValue, GraphView.this.romMap.getValue(x,y));

			return maxValue;
		}

		public void paintControl(PaintEvent e) {
			if (e.count > 0) return;
			if (GraphView.this.currentRom == null) return;

			// TODO CLEAN THIS MESS UP!
			final float width  = this.gc.getClipping().width;
			final float height = this.gc.getClipping().height;

			this.gc.setBackground(Display.getDefault().getSystemColor(SWT.COLOR_WHITE));
			this.gc.setForeground(Display.getDefault().getSystemColor(SWT.COLOR_WHITE));
			this.gc.fillRectangle(0, 0, (int) width, (int) height);
			this.gc.setForeground(Display.getDefault().getSystemColor(SWT.COLOR_BLACK));

			final int pointZeroX = (int) (this.pointZero[0] * width / 100.);
			final int pointZeroY = (int) (this.pointZero[1] * height / 100.);
			
			final int pointMaxMapX = (int) (this.pointMaxMap[0] * width / 100.);
			final int pointMaxMapY = (int) (this.pointMaxMap[1] * height / 100.);

			final int pointMaxRPMX = (int) (this.pointMaxRPM[0] * width / 100.);
			final int pointMaxRPMY = (int) (this.pointMaxRPM[1] * height / 100.);

			final int pointMaxMapMaxRPMX = (int) (this.pointMaxMapMaxRPM[0]* width / 100.);
			final int pointMaxMapMaxRPMY = (int) (this.pointMaxMapMaxRPM[1]* height / 100.);

			final int graphHeight = (int) (this.graphHeightPerc * height / 100.);

			// Base
			this.gc.drawLine(pointZeroX, pointZeroY, pointMaxMapX, pointMaxMapY);
			this.gc.drawLine(pointZeroX, pointZeroY, pointMaxRPMX, pointMaxRPMY);

			this.gc.drawLine(pointMaxMapX, pointMaxMapY, pointMaxMapMaxRPMX, pointMaxMapMaxRPMY);
			this.gc.drawLine(pointMaxRPMX, pointMaxRPMY, pointMaxMapMaxRPMX, pointMaxMapMaxRPMY);

			// Center line
			this.gc.drawLine(pointMaxMapMaxRPMX, pointMaxMapMaxRPMY - graphHeight, pointMaxMapMaxRPMX, pointMaxMapMaxRPMY);

			// Left backplane (MAP)
			this.gc.drawLine(pointMaxMapX, pointMaxMapY - graphHeight, pointMaxMapMaxRPMX, pointMaxMapMaxRPMY - graphHeight);
			this.gc.drawLine(pointMaxMapX, pointMaxMapY, pointMaxMapX, pointMaxMapY - graphHeight);

			// Right backplane (RPM)
			this.gc.drawLine(pointMaxRPMX, pointMaxRPMY - graphHeight, pointMaxMapMaxRPMX, pointMaxMapMaxRPMY - graphHeight);
			this.gc.drawLine(pointMaxRPMX, pointMaxRPMY, pointMaxRPMX, pointMaxRPMY - graphHeight);

			final float mapIncrementalX = (pointMaxMapX - pointZeroX) / (float) GraphView.this.romMap.getMapScalars().length;
			final float mapIncrementalY = (pointMaxMapY - pointZeroY) / (float) GraphView.this.romMap.getMapScalars().length;

			final float rpmIncrementalX = (pointMaxRPMX - pointZeroX) / (float) GraphView.this.romMap.getRpmScalars().length;
			final float rpmIncrementalY = (pointMaxRPMY - pointZeroY) / (float) GraphView.this.romMap.getRpmScalars().length;

			int[][][] coordinates = new int[GraphView.this.romMap.getMapScalars().length][GraphView.this.romMap.getRpmScalars().length][3];

			for (int map = 0; map < GraphView.this.romMap.getMapScalars().length; map++)
				for (int rpm = 0; rpm < GraphView.this.romMap.getRpmScalars().length; rpm++) {
					float x = map * mapIncrementalX + rpm * rpmIncrementalX + pointZeroX;
					float y = map * mapIncrementalY + rpm * rpmIncrementalY + pointZeroY;

					coordinates[map][rpm][0] = (int) x;

					var dif = (int) ((graphHeight * (GraphView.this.romMap.getValue(map, rpm) / this.getMaxValue())));
					coordinates[map][rpm][1] = (int) (y - dif);
					coordinates[map][rpm][2] = (int) (255 * GraphView.this.romMap.getValue(map, rpm) / this.getMaxValue()); // for color
				}

			for (int x = 0; x < coordinates.length - 1; x++)
				for (int y = 0; y < coordinates[x].length - 1; y++) {
					var coords = new int[8];

					coords[0] = coordinates[x][y][0];
					coords[1] = coordinates[x][y][1];
					coords[2] = coordinates[x + 1][y][0];
					coords[3] = coordinates[x + 1][y][1];
					coords[4] = coordinates[x + 1][y + 1][0];
					coords[5] = coordinates[x + 1][y + 1][1];
					coords[6] = coordinates[x][y + 1][0];
					coords[7] = coordinates[x][y + 1][1];

					int color = coordinates[x][y][2];
					color = Math.max(0, color);
					color = Math.min(255, color);

					Color col = new Color(Display.getDefault(), color,
							255 - color, 255 - color);
					this.gc.setBackground(col);
					col.dispose();
					this.gc.fillPolygon(coords);

					this.gc.setBackground(Display.getDefault().getSystemColor(SWT.COLOR_WHITE));
					this.gc.setForeground(Display.getDefault().getSystemColor(SWT.COLOR_BLACK));
					this.gc.drawPolyline(coords);
				}
		}
	}

	@Override
	public void createPartControl(Composite parent) {
		// TODO clean up... we shouldn't be loading roms here...
		parent.setLayout(new FillLayout());
		Canvas canvas = new Canvas(parent, SWT.NONE);
		GC gc = new GC(canvas, SWT.NONE);

		canvas.addPaintListener(new GraphPainter(gc));

		this.setPartName(this.getViewSite().getSecondaryId());
		parent.layout();
	}

	public void setRom(IRom rom, String viewType) {
		this.currentRom = rom;
		this.romMap = rom.getMap(viewType);
	}
}