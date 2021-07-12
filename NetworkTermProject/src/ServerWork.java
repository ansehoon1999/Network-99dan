import java.io.*;
import java.net.*;
import java.util.*;
import java.util.concurrent.ExecutorService;

import contents.game.Game;
import identification.info.Account;

public class ServerWork implements Runnable
{
	private static Integer GameId = 0;

	private String clientId = null;
	private Socket client = null;
	private HashMap<String, Account> accounts = null;
	private HashMap<String, Socket> clients = null;
	private HashMap<String, Integer> stateMap = null;
	private HashMap<String, String> requests = null;
	private HashMap<Integer, Game> games = null;
	private Integer myGameId = null;
	private Boolean isFirstPlayer = null;
	private ExecutorService thread = null;

	private static final Object joinLock = new Object();

	public ServerWork(Socket client, HashMap<String, Account> listReference, HashMap<String, Socket> socketList, HashMap<String, Integer> stateMap, HashMap<String, String> reqList, HashMap<Integer, Game> games)
	{
		this.client = client;
		accounts = listReference;
		clients = socketList;
		this.stateMap = stateMap;
		requests = reqList;
		this.games = games;
	}

	public void setThread(ExecutorService thread)
	{
		this.thread = thread;
	}

	@Override
	public void run()
	{
		PrintStream out;
		Scanner in;

		try
		{
			out = new PrintStream(client.getOutputStream());
			in = new Scanner(client.getInputStream());
		}
		catch (IOException e)
		{
			e.printStackTrace();
			try
			{
				client.close();
			}
			catch (IOException e2)
			{
			}
			return;
		}

		System.out.println("Connected: " + client.getRemoteSocketAddress());

		boolean lp = true;
		String firstName = null;
		String lastName = null;
		String id = null;
		String pw = null;
		while (lp)
		{
			String cmd = in.nextLine().trim();

			System.out.print(client.getRemoteSocketAddress() + " >> ");
			System.out.println(cmd);
			switch (cmd)
			{
			case "":
				continue;

			case "cnfid":
				String user = in.nextLine();
				if (accounts.containsKey(user))
				{
					out.println((byte) 0);
					out.flush();
				}
				else
				{
					out.println((byte) 1);
					out.flush();
				}
				break;

			case "sigup":
				firstName = in.nextLine();
				lastName = in.nextLine();
				id = in.nextLine();
				pw = in.nextLine();
				Account newAccount = new Account(id, pw, firstName, lastName);

				synchronized (accounts)
				{
					accounts.put(id, newAccount);
					try
					{
						writeAccount(newAccount);
					}
					catch (IOException e)
					{
						out.println((byte) 0);
						out.flush();
						break;
					}
				}
				out.println((byte) 1);
				out.flush();
				break;

			case "sigin":
				id = in.nextLine();
				pw = in.nextLine();

				Account accnt = accounts.get(id);
				if (accnt == null)
				{
					out.println("Not existing user name");
					out.flush();
				}
				else if (accnt.matchPassword(pw))
				{
					if (clients.containsKey(id))
					{
						out.println("Double sign in.");
						out.flush();
					}
					else
					{
						out.println("normal");
						out.flush();

						accnt.updateSignInTime();
						try
						{
							writeAccount(accnt);

							out.println("normal");
							out.flush();
						}
						catch (IOException e)
						{
							out.println("Failed. Please try again.");
							out.flush();
							break;
						}
					}
				}
				else
				{
					out.println("Wrong password");
					out.flush();
				}
				break;

			case "setpw":
				firstName = in.nextLine();
				lastName = in.nextLine();
				id = in.nextLine();
				pw = in.nextLine();

				synchronized (accounts)
				{
					accnt = accounts.get(id);
					if (accnt == null)
					{
						out.println("Not existing user id");
						out.flush();
					}
					else
					{
						if (accnt.getFirstName().equals(firstName) && accnt.getLastName().equals(lastName))
						{
							accounts.get(id).resetPassword(pw);

							try
							{
								writeAccount(accnt);

								out.println("normal");
								out.flush();
							}
							catch (IOException e)
							{
								out.println("Failed. Please try again.");
								out.flush();
								break;
							}
						}
						else
						{
							out.println("I think it ain't your account.");
							out.flush();
						}
					}
				}
				break;

			case "init":
				id = in.nextLine();
				this.clientId = id;

				out.println("usrlist");
				out.println(clients.size());
				out.flush();
				for (String name : clients.keySet())
				{
					out.println(name);
					out.println(stateMap.get(name).intValue());
					out.flush();
				}

				for (Socket old : clients.values())
				{
					try
					{
						PrintWriter writer = new PrintWriter(old.getOutputStream());
						writer.println("newuser");
						writer.println(id);
						writer.flush();
					}
					catch (IOException e)
					{
						e.printStackTrace();
					}
				}

				clients.put(id, client);
				stateMap.put(id, 1);
				break;

			case "chatting":
				String msg = in.nextLine();
				msg = this.clientId + ": " + msg;

				System.out.println(msg);
				System.out.println(clients.values().size());
				for (Map.Entry<String, Socket> client : clients.entrySet())
				{
					try
					{
						PrintWriter writer = new PrintWriter(client.getValue().getOutputStream());
						writer.println("broadcast");
						writer.println(msg);
						writer.flush();

						System.out.println("Send to " + client.getKey());
					}
					catch (IOException e)
					{
					}
				}
				break;

			case "whisper":
				String dst = in.nextLine();
				if (clients.containsKey(dst))
				{
					msg = this.clientId + "¡æ" + dst + ": " + in.nextLine();
					try
					{
						PrintWriter writer = new PrintWriter(clients.get(dst).getOutputStream());
						writer.println("whisper");
						writer.println(msg);
						writer.flush();

						out.println("whisper");
						out.println(msg);
						out.flush();
					}
					catch (IOException e)
					{
						out.println("error");
						out.println("Whisper failed.");
						out.flush();
					}
				}
				else
				{
					out.println("error");
					out.println("No such user.");
					out.flush();
				}
				break;

			case "askinf":
				id = in.nextLine();
				accnt = accounts.get(id);

				out.println("record");
				if (accnt == null)
				{
					out.println((byte) 0);
					out.flush();
				}
				else
				{
					out.println((byte) 1);
					out.flush();

					out.println(accnt.getWin());
					out.println(accnt.getLose());
					out.println(accnt.getLastSignInTimeAsLong());
					out.println(stateMap.get(id).intValue());
					out.flush();
				}
				break;

			case "reqgam": // from Host
			{
				String target = in.nextLine();
				if (requests.containsValue(clientId))
				{
					break;
				}

				if (clients.containsKey(target)) // if the user signed in
				{
					try
					{
						PrintWriter writer = new PrintWriter(clients.get(target).getOutputStream());
						writer.println("invite");
						writer.println(clientId);
						writer.flush();

						requests.put(target, clientId);
					}
					catch (IOException e)
					{
						e.printStackTrace();
					}
				}
				break;
			}

			case "accept": // from Guest
			{
				final String hostId = requests.get(clientId);
				final int gameId = ++GameId;
				try
				{
					// to Host
					PrintWriter writer = new PrintWriter(clients.get(hostId).getOutputStream());
					writer.println("accepted");
					writer.println(gameId);
					writer.flush();

					// to Guest
					out.println("accepted");
					out.println(gameId);
					out.flush();

					requests.remove(clientId);

					games.put(gameId, new Game(clientId, hostId));
				}
				catch (IOException e)
				{
					e.printStackTrace();
				}

				break;
			}

			case "reject": // from Guest
			{
				String hostId = requests.get(clientId);
				try
				{
					PrintWriter writer = new PrintWriter(clients.get(hostId).getOutputStream());
					writer.println("rejected");
					writer.flush();

					requests.remove(clientId);
				}
				catch (IOException e)
				{
					e.printStackTrace();
				}
				break;
			}

			case "jngame": // from Host, Guest
			{
				int gameId = in.nextInt();
				Game game = games.get(gameId);

				if (game == null)
				{
					out.println("This game's over.");
					out.flush();
					break;
				}
				else
				{
					if (clientId.equals(game.name1))
					{
						game.player1 = client;
						myGameId = gameId;
						isFirstPlayer = true;
						out.println("ok");
						out.flush();
					}
					else if (clientId.equals(game.name2))
					{
						game.player2 = client;
						myGameId = gameId;
						isFirstPlayer = false;
						out.println("ok");
						out.flush();
					}
					else
					{
						out.println("Occupied.");
						out.flush();
						break;
					}

					stateMap.replace(clientId, 0);
					for (Map.Entry<String, Socket> ent : clients.entrySet())
					{
						String name = ent.getKey();
						Socket client = ent.getValue();

						if (!(name.equals(game.name1) || name.equals(game.name2)))
						{
							try
							{
								PrintWriter writer = new PrintWriter(client.getOutputStream());
								writer.println("updtstat");
								writer.println(clientId);
								writer.println((int) 0);
								writer.flush();
							}
							catch (IOException e)
							{
								e.printStackTrace();
							}
						}
					}
				}
				break;
			}

			case "gchat": // from Host, Guest
			{
				String message = clientId + ": " + in.nextLine();
				int gameId = myGameId;

				if (games.containsKey(gameId))
				{
					Game game = games.get(gameId);

					try
					{
						PrintWriter writer = null;

						writer = new PrintWriter(game.player1.getOutputStream());
						writer.println("gchatrec");
						writer.println(message);
						writer.flush();

						writer = new PrintWriter(game.player2.getOutputStream());
						writer.println("gchatrec");
						writer.println(message);
						writer.flush();
					}
					catch (IOException e)
					{
						e.printStackTrace();
					}
				}
				break;
			}

			case "ready": // from Host, Guest
			{
				int gameId = myGameId;

				if (games.containsKey(gameId))
				{
					Game game = games.get(gameId);
					String message = null;

					if (isFirstPlayer)
					{
						game.ready1 = true;
						message = ">> " + game.name1 + " is ready.";
					}
					else
					{
						game.ready2 = true;
						message = ">> " + game.name2 + "is ready.";
					}

					try
					{
						PrintWriter writer;

						writer = new PrintWriter(game.player1.getOutputStream());
						writer.println("announce");
						writer.println(message);
						writer.flush();

						writer = new PrintWriter(game.player2.getOutputStream());
						writer.println("announce");
						writer.println(message);
						writer.flush();
					}
					catch (IOException e)
					{
						e.printStackTrace();
					}

					if (game.ready1 && game.ready2)
					{
						try
						{
							final int randomBinary = (int) (Math.random() * 100) % 2;
							PrintWriter writer = null;

							writer = new PrintWriter(game.player1.getOutputStream());
							writer.println("start");
							writer.println(1 - randomBinary);
							writer.flush();

							writer = new PrintWriter(game.player2.getOutputStream());
							writer.println("start");
							writer.println(randomBinary);
							writer.flush();

							game.ready1 = game.ready2 = false;
							game.answer = null;
						}
						catch (IOException e)
						{
							e.printStackTrace();
						}
					}
				}
				break;
			}

			case "unready": // from Host, Guest
			{
				int gameId = myGameId;

				if (games.containsKey(gameId))
				{
					Game game = games.get(gameId);
					String message = null;

					if (isFirstPlayer)
					{
						game.ready1 = false;
						message = ">> " + game.name1 + " is not ready.";
					}
					else
					{
						game.ready2 = false;
						message = ">> " + game.name2 + " is not ready.";
					}

					try
					{
						PrintWriter writer;

						writer = new PrintWriter(game.player1.getOutputStream());
						writer.println("announce");
						writer.println(message);
						writer.flush();

						writer = new PrintWriter(game.player2.getOutputStream());
						writer.println("announce");
						writer.println(message);
						writer.flush();
					}
					catch (IOException e)
					{
						e.printStackTrace();
					}
				}
				break;
			}

			case "quiz":
			{
				PrintWriter destOut = null;
				Game myGame = games.get(myGameId);
				try
				{
					if (isFirstPlayer)
					{
						destOut = new PrintWriter(myGame.player2.getOutputStream());
					}
					else
					{
						destOut = new PrintWriter(myGame.player1.getOutputStream());
					}
				}
				catch (IOException e)
				{
					e.printStackTrace();
				}

				int op1 = in.nextInt();
				int op2 = in.nextInt();

				myGame.answer = op1 * op2;

				destOut.println("quiz");
				destOut.println(op1);
				destOut.println(op2);
				destOut.flush();
				break;
			}

			case "answer":
			{
				Game myGame = games.get(myGameId);
				int ans = in.nextInt();

				if (ans == myGame.answer)
				{
					out.println("correct");
					out.flush();
				}
				else
				{
					out.println("lost");
					out.flush();

					PrintWriter winnerOut = null;
					try
					{
						if (isFirstPlayer)
						{
							winnerOut = new PrintWriter(myGame.player2.getOutputStream());
						}
						else
						{
							winnerOut = new PrintWriter(myGame.player1.getOutputStream());
						}
					}
					catch (IOException e)
					{
						e.printStackTrace();
					}

					accnt = accounts.get(clientId);
					accnt.lose();
					try
					{
						writeAccount(accnt);
					}
					catch (IOException e)
					{
						e.printStackTrace();
					}

					winnerOut.println("win");
					winnerOut.flush();
				}
				break;
			}

			case "srndr":
			{
				Game myGame = games.get(myGameId);
				PrintWriter winnerOut = null;
				try
				{
					if (isFirstPlayer)
					{
						winnerOut = new PrintWriter(myGame.player2.getOutputStream());
					}
					else
					{
						winnerOut = new PrintWriter(myGame.player1.getOutputStream());
					}
				}
				catch (IOException e)
				{
					e.printStackTrace();
				}

				accnt = accounts.get(clientId);
				accnt.lose();
				try
				{
					writeAccount(accnt);
				}
				catch (IOException e)
				{
					e.printStackTrace();
				}

				winnerOut.println("srndr");
				winnerOut.flush();
				break;
			}

			case "recme":
			{
				accnt = accounts.get(clientId);
				accnt.win();
				try
				{
					writeAccount(accnt);
				}
				catch (IOException e)
				{
					e.printStackTrace();
				}

				break;
			}

			case "endgame": // from Host, Guest
			{
				int gameId = myGameId;

				out.println("endgame");
				out.flush();
				stateMap.replace(clientId, 1);

				for (Map.Entry<String, Socket> ent : clients.entrySet())
				{
					String name = ent.getKey();
					Socket client = ent.getValue();

					if (!name.equals(clientId))
					{
						try
						{
							PrintWriter writer;

							writer = new PrintWriter(client.getOutputStream());
							writer.println("updtstat");
							writer.println(clientId);
							writer.println((int) 1);

							writer.flush();
						}
						catch (IOException e)
						{
							e.printStackTrace();
						}
					}

					if (games.containsKey(gameId))
					{
						Game game = games.get(gameId);

						try
						{
							PrintWriter writer = new PrintWriter((isFirstPlayer ? game.player2 : game.player1).getOutputStream());
							writer.println("announce");
							writer.println(">> " + clientId + " has left.");
							writer.flush();
						}
						catch (IOException e)
						{
							e.printStackTrace();
						}

						games.remove(gameId);
					}
				}
				break;
			}

			case "quit":
				lp = false;
				break;

			case "sigout":
				lp = false;
				clients.remove(this.clientId);
				for (Socket client : clients.values())
				{
					try
					{
						PrintWriter writer = new PrintWriter(client.getOutputStream());
						writer.println("deluser");
						writer.println(this.clientId);
						writer.flush();
					}
					catch (IOException e)
					{
					}
				}
				break;

			default:
				System.out.println("Unknown command: " + cmd);
				break;
			}
		}

		System.out.println("SERVER_CLOSING");

		try
		{
			client.close();
		}
		catch (IOException e)
		{
		}

		thread.shutdownNow();
	}

	private void writeAccount(Account accnt) throws IOException
	{
		File file = new File("accnts\\" + accnt.getId() + ".acc");
		try
		{
			if (file.exists())
			{
				file.delete();
			}
			file.createNewFile();
			FileOutputStream fileOut = new FileOutputStream(file);

			accnt.write(fileOut);
			fileOut.close();
		}
		catch (IOException e)
		{
			throw e;
		}
	}
}
