package generic;

public class CommandEvent
{
	private String command = null;
	private Object[] arguments = null;
	
	public CommandEvent(String command)
	{
		this.command = command;
	}
	public CommandEvent(String command, Object[] args)
	{
		this(command);
		arguments = args;
	}
	
	public String getCommand()
	{
		return command;
	}
	
	public int getArgumntLength()
	{
		if (arguments == null)
		{
			return 0;
		}
		
		return arguments.length;
	}
	
	public Object getArgument(int index)
	{
		if (index < 0 || arguments.length <= index)
		{
			return null;
		}
		
		return arguments[index];
	}
}
