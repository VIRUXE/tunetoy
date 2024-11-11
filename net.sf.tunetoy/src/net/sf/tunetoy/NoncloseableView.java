package net.sf.tunetoy;

import java.util.ArrayList;

import net.sf.tunetoy.model.IRom;
import net.sf.tunetoy.model.RomFactory;
import net.sf.tunetoy.model.TableType;

import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.part.ViewPart;

public class NoncloseableView extends ViewPart {
	class TreeObject implements IAdaptable {
		private OpenMapViewAction mAction;

		private TreeParent parent;

		public TreeObject(OpenMapViewAction action) { this.mAction = action; }

		public OpenMapViewAction getAction() { return this.mAction; }

		public Object getAdapter(Class key) {
			key = null; // FIXME: Hack to supress warning
			return key;
		}

		public String getName() { return this.mAction.getName(); }

		public TreeParent getParent() { return this.parent; }

		public void setParent(TreeParent parent) { this.parent = parent; }

		@Override
		public String toString() { return getName(); }
	}

	class TreeParent extends TreeObject {
		private ArrayList<TreeObject> children;

		private IRom rom;

		public TreeParent(IRom rom) {
			super(null);
			this.rom = rom;
			this.children = new ArrayList<TreeObject>();
		}

		public void addChild(TreeObject child) {
			this.children.add(child);
			child.setParent(this);
		}

		public TreeObject[] getChildren() { return this.children.toArray(new TreeObject[this.children.size()]); }

		@Override
		public String getName() { return this.rom.getName(); }

		public boolean hasChildren() { return this.children.size() > 0; }

		public void removeChild(TreeObject child) {
			this.children.remove(child);
			child.setParent(null);
		}
	}

	class ViewContentProvider implements IStructuredContentProvider, ITreeContentProvider {
		private TreeParent invisibleRoot;

		public void dispose() {
			//
		}

		public Object[] getChildren(Object parent) {
			if (parent instanceof TreeParent) return ((TreeParent) parent).getChildren();
			return new Object[0];
		}

		public Object[] getElements(Object parent) {
			if (parent.equals(getViewSite())) {
				this.initialize();
				return getChildren(this.invisibleRoot);
			}

			return getChildren(parent);
		}

		public Object getParent(Object child) {
			if (child instanceof TreeObject) return ((TreeObject) child).getParent();
			return null;
		}

		public boolean hasChildren(Object parent) {
			if (parent instanceof TreeParent) return ((TreeParent) parent).hasChildren();
			return false;
		}

		public void inputChanged(@SuppressWarnings("unused") Viewer v, @SuppressWarnings("unused") Object oldInput, @SuppressWarnings("unused") Object newInput) {
			//
		}

		/*
		 * We will set up a dummy model to initialize tree heararchy. In a real
		 * code, you will connect to a real model and expose its hierarchy.
		 */
		private void initialize() {
			this.invisibleRoot = new TreeParent(null);
			for (IRom rom : RomFactory.getLoadedRoms()) {
				TreeParent parent = new TreeParent(rom);

				// Table map for non-vtec fuel map
				parent.addChild(new TreeObject(new OpenTableMapViewAction(getSite().getWorkbenchWindow(), rom, TableType.FUEL_NO_VTEC)));
				// Graph map for non-vtec fuel map
				parent.addChild(new TreeObject(new OpenGraphMapViewAction(getSite().getWorkbenchWindow(), rom, TableType.FUEL_NO_VTEC)));

				if (rom.getCapabilities().hasVtec().booleanValue()) {
					// Table map for vtec fuel map
					parent.addChild(new TreeObject(new OpenTableMapViewAction(getSite().getWorkbenchWindow(), rom, TableType.FUEL_VTEC)));
					// Graph map for vtec fuel map
					parent.addChild(new TreeObject(new OpenGraphMapViewAction(getSite().getWorkbenchWindow(), rom, TableType.FUEL_VTEC)));
				}

				// Table map for non-vtec ignition map
				parent.addChild(new TreeObject(new OpenTableMapViewAction(getSite().getWorkbenchWindow(), rom, TableType.IGNITION_NO_VTEC)));
				// Graph map for non-vtec ignition map
				parent.addChild(new TreeObject(new OpenGraphMapViewAction(getSite().getWorkbenchWindow(), rom, TableType.IGNITION_NO_VTEC)));

				if (rom.getCapabilities().hasVtec().booleanValue()) {
					// Table map for vtec igniion map
					parent.addChild(new TreeObject(new OpenTableMapViewAction(getSite().getWorkbenchWindow(), rom, TableType.IGNITION_VTEC)));
					// Graph map for vtec igniion map
					parent.addChild(new TreeObject(new OpenGraphMapViewAction(getSite().getWorkbenchWindow(), rom, TableType.IGNITION_VTEC)));
				}

				// Table map for non-vtec lambda map
				parent.addChild(new TreeObject(new OpenTableMapViewAction(getSite().getWorkbenchWindow(), rom, TableType.LAMBDA_NO_VTEC)));
				// Graph map for non-vtec lambda map
				parent.addChild(new TreeObject(new OpenGraphMapViewAction(getSite().getWorkbenchWindow(), rom, TableType.LAMBDA_NO_VTEC)));

				if (rom.getCapabilities().hasVtec().booleanValue()) {
					// Table map for non-vtec lambda map
					parent.addChild(new TreeObject(new OpenTableMapViewAction(getSite().getWorkbenchWindow(), rom, TableType.LAMBDA_VTEC)));
					// Graph map for non-vtec lambda map
					parent.addChild(new TreeObject(new OpenGraphMapViewAction(getSite().getWorkbenchWindow(), rom, TableType.LAMBDA_VTEC)));
				}

				// Rom options
				parent.addChild(new TreeObject(new OpenRomOptionsAction(getSite().getWorkbenchWindow(), rom, TableType.OPTIONS)));

				this.invisibleRoot.addChild(parent);
			}

		}
	}

	class ViewLabelProvider extends LabelProvider {

		@Override
		public Image getImage(Object obj) {
			String imageKey = ISharedImages.IMG_OBJ_ELEMENT;

			if (obj instanceof TreeParent) imageKey = ISharedImages.IMG_OBJ_FOLDER;

			return PlatformUI.getWorkbench().getSharedImages().getImage(imageKey);
		}

		@Override
		public String getText(Object obj) { return obj.toString(); }
	}

	public static final String ID = "net.sf.tunetoy.NoncloseableView"; //$NON-NLS-1$

	protected ViewContentProvider contentprovider;

	protected TreeViewer viewer;

	/**
	 * This is a callback that will allow us to create the viewer and initialize
	 * it.
	 */
	@Override
	public void createPartControl(Composite parent) {
		this.viewer = new TreeViewer(parent, SWT.H_SCROLL | SWT.V_SCROLL | SWT.BORDER);
		this.viewer.setContentProvider(this.contentprovider = new ViewContentProvider());
		this.viewer.setLabelProvider(new ViewLabelProvider());
		this.viewer.setInput(getViewSite());
		this.viewer.addDoubleClickListener(new IDoubleClickListener() {
			public void doubleClick(DoubleClickEvent event) {
				// TODO Auto-generated method stub
				TreeObject to = ((TreeObject) ((StructuredSelection) event.getViewer().getSelection()).getFirstElement());

				if (to.getClass() == TreeObject.class) to.getAction().run();
			}
		});

		this.refresh();
	}

	public void refresh() { this.viewer.refresh(); }

	/**
	 * Passing the focus request to the viewer's control.
	 */
	@Override
	public void setFocus() { this.viewer.getControl().setFocus(); }
}