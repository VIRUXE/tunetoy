package net.sf.tunetoy;

import java.util.MissingResourceException;
import java.util.ResourceBundle;

import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;

/**
 * The main plugin class to be used in the desktop.
 */
public class RomEditorPlugin extends AbstractUIPlugin {
	// The shared instance.
	private static RomEditorPlugin plugin;
	// Resource bundle.
	private ResourceBundle resourceBundle;

	/**
	 * The constructor.
	 */
	public RomEditorPlugin() {
		super();
		plugin = this;
	}

	/**
	 * This method is called upon plug-in activation
	 */
	@Override
	public void start(BundleContext context) throws Exception { super.start(context); }

	/**
	 * This method is called when the plug-in is stopped
	 */
	@Override
	public void stop(BundleContext context) throws Exception {
		super.stop(context);
		plugin = null;
		this.resourceBundle = null;
	}

	/**
	 * Returns the shared instance.
	 */
	public static RomEditorPlugin getDefault() { return plugin; }

	/**
	 * Returns the string from the plugin's resource bundle, or 'key' if not
	 * found.
	 */
	public static String getResourceString(String key) {
		var bundle = RomEditorPlugin.getDefault().getResourceBundle();

		try {
			return (bundle != null) ? bundle.getString(key) : key;
		} catch (MissingResourceException e) {
			return key;
		}
	}

	/**
	 * Returns the plugin's resource bundle,
	 */
	public ResourceBundle getResourceBundle() {
		try {
			if (this.resourceBundle == null) this.resourceBundle = ResourceBundle.getBundle("net.sf.tunetoy.RomEditorPluginResources"); //$NON-NLS-1$
		} catch (MissingResourceException x) {
			this.resourceBundle = null;
		}
		
		return this.resourceBundle;
	}
}
