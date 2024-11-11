package net.sf.tunetoy;

/**
 * Interface defining the application's command IDs. Key bindings can be defined
 * for specific commands. To associate an action with a command, use
 * IAction.setActionDefinitionId(commandId).
 * 
 * @see org.eclipse.jface.action.IAction#setActionDefinitionId(String)
 */
public interface ICommandIds {

	public static final String CMD_OPEN = "net.sf.tunetoy.open"; //$NON-NLS-1$

	public static final String CMD_TODO_LIST = "net.sf.tunetoy.todo"; //$NON-NLS-1$
}
