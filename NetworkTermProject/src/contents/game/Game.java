package contents.game;

import java.net.Socket;

public class Game
{
	public Game(String name1, String name2)
	{
		this.name1 = name1;
		this.name2 = name2;
	}
	
	public String name1 = null;
	public String name2 = null;

	public Socket player1 = null;
	public Socket player2 = null;

	public boolean ready1 = false;
	public boolean ready2 = false;
	
	public Integer answer = null;
}
