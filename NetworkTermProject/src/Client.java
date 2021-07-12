import contents.chat.MultiChatFrame;
import generic.ConnectionListener;
import identification.*;

public class Client
{
	public static void main(String[] args)	
	{
		IdentificationFrame idFrame = new IdentificationFrame();
		MultiChatFrame chatFrame = new MultiChatFrame();
		
		idFrame.addSignInListener(new ConnectionListener()
		{
			@Override
			public void connected(String id)
			{
				idFrame.setVisible(false);
				idFrame.dispose();
				
				chatFrame.initializeConnection(id);
				chatFrame.setVisible(true);
			}	
		});

		idFrame.setVisible(true);
	}
}