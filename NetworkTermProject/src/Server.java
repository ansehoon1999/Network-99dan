import java.io.*;
import java.net.*;
import java.util.*;
import java.util.concurrent.*;

import contents.game.Game;
import generic.Preferences;
import identification.info.*;

public class Server
{
	private static HashMap<String, Account> accounts = new HashMap<String, Account>();
	private static HashMap<String, Socket> clients = new HashMap<String, Socket>();
	private static HashMap<String, Integer> stateMap = new HashMap<String, Integer>();
	private static HashMap<String, String> requests = new HashMap<String, String>();

	private static HashMap<Integer, Game> games = new HashMap<Integer, Game>();

	public static void main(String[] args) throws IOException
	{
		loadAccountsOrCreateDir("accnts");

		ServerSocket server = null;
		try
		{
			server = new ServerSocket(Preferences.PortNumber);
		}
		catch (IOException e)
		{
			System.out.println("Server has already opened.");
			return;
		}
		System.out.println("Server start...");

		while (true)
		{
			Socket client = server.accept();
			if (client != null)
			{
				ServerWork work = new ServerWork(client, accounts, clients, stateMap, requests, games);
				ExecutorService thread = Executors.newSingleThreadExecutor();
				work.setThread(thread);
				thread.execute(work);
			}
		}
	}

	private static void loadAccountsOrCreateDir(String dirName)
	{
		File accntDirectory = new File(dirName);

		if (!accntDirectory.exists())
		{
			accntDirectory.mkdir();
			System.out.println("Account directory created.");
		}
		else
		{
			File[] files = accntDirectory.listFiles();
			if (files.length > 0)
			{
				int cnt = 0;
				for (File file : files)
				{
					if (file.getPath().endsWith(".acc"))
					{
						cnt++;
						Account accnt = Account.fromFile(file);
						accounts.put(accnt.getId(), accnt);
					}
				}

				System.out.println(cnt + " accounts found.");
			}
			else
			{
				System.out.println("0 accounts found.");
			}
		}
	}
}
