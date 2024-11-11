package net.sf.tunetoy;

import java.util.ArrayList;

import net.sf.tunetoy.model.IRom;
import net.sf.tunetoy.model.IRomMap;

import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.ui.part.ViewPart;

public class SampleView extends ViewPart {

	public static final String ID = "net.sf.tunetoy.SampleView"; //$NON-NLS-1$

	private Composite parent = null;

	private Composite mainComposite = null;

	private IRom currentRom = null;

	private String viewType = null;

	IRomMap romMap = null;

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ui.part.WorkbenchPart#createPartControl(org.eclipse.swt.widgets.Composite)
	 */
	@Override
	public void createPartControl(Composite composite) {
		this.parent = composite;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ui.part.WorkbenchPart#setFocus()
	 */
	@Override
	public void setFocus() {
		// TODO Auto-generated method stub

	}

	private void initializeGUI() {
		this.parent.setLayout(new FillLayout());
		this.mainComposite = new Composite(this.parent, SWT.NONE);

		final int mapHeight = this.romMap.getRpmScalars().length;
		final int mapWidth = this.romMap.getMapScalars().length;
		Table table = new Table(this.mainComposite, SWT.BORDER | SWT.SINGLE);

		for (int i = 0; i < mapWidth + 1; i++)
			new TableColumn(table, SWT.CENTER);

		TableViewer tableViewer = new TableViewer(table);
		table.setLinesVisible(true);
		table.setHeaderVisible(false);
		table.setLayoutData(new GridData(GridData.FILL_BOTH));

		// Sets the content provider.
		tableViewer.setContentProvider(new IStructuredContentProvider() {
			public Object[] getElements(@SuppressWarnings("unused")
			Object inputElement) {
				ArrayList<Integer> a = new ArrayList<Integer>();
				for (int i = 0; i < mapHeight + 1; i++)
					a.add(i);
				return a.toArray();
			}

			public void dispose() {//
			}

			public void inputChanged(@SuppressWarnings("unused")
			Viewer viewer, @SuppressWarnings("unused")
			Object oldInput, @SuppressWarnings("unused")
			Object newInput) {//
			}

		});
		// Sets the label provider.
		tableViewer.setLabelProvider(new ITableLabelProvider() {
			public Image getColumnImage(@SuppressWarnings("unused")
			Object element, @SuppressWarnings("unused")
			int columnIndex) {
				return null;
			}

			public String getColumnText(Object element, int columnIndex) {
				Integer i = (Integer) element;
				if (i == 0) {
					if (columnIndex == 0)
						return new String();
					return SampleView.this.romMap.getMapScalars()[columnIndex - 1]
							.toString();
				}
				if (columnIndex == 0)
					return SampleView.this.romMap.getRpmScalars()[i - 1]
							.toString();
				return SampleView.this.romMap.getValue(columnIndex - 1, i - 1)
						.toString();
			}

			public void addListener(@SuppressWarnings("unused")
			ILabelProviderListener listener) {
				//
			}

			public void dispose() {
				//
			}

			public boolean isLabelProperty(@SuppressWarnings("unused")
			Object element, @SuppressWarnings("unused")
			String property) {
				return false;
			}

			public void removeListener(@SuppressWarnings("unused")
			ILabelProviderListener listener) {
				//
			}
		});

		tableViewer.setInput(new Integer[mapHeight + 1]);

		for (TableItem item : table.getItems()) {
			item.setForeground(0, Display.getDefault().getSystemColor(
					SWT.COLOR_WHITE));
			item.setBackground(0, Display.getDefault().getSystemColor(
					SWT.COLOR_BLACK));
		}

		table.getItem(0).setForeground(
				Display.getDefault().getSystemColor(SWT.COLOR_WHITE));
		table.getItem(0).setBackground(
				Display.getDefault().getSystemColor(SWT.COLOR_BLACK));

		for (TableColumn tc : table.getColumns())
			tc.pack();

		this.mainComposite.setLayout(new FillLayout());
		this.setPartName(this.getViewSite().getSecondaryId());
		this.parent.layout();
	}

	public void setRom(IRom rom, String viewType) {
		if (this.currentRom != null)
			this.mainComposite.dispose();
		this.currentRom = rom;
		this.viewType = viewType;
		this.romMap = this.currentRom.getMap(this.viewType);
		this.initializeGUI();
	}
}