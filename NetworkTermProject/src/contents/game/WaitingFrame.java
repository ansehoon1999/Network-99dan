package contents.game;

import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.net.Socket;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.swing.*;
import javax.swing.border.*;
import javax.swing.custom.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.text.Style;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyleContext;

import generic.*;

public class WaitingFrame extends JFrame
{
	/**
	 * GUI Components
	 */
	private JPanel rootPanel = null;
	private JPanel topPanel = null;
	private JLabel exitButton = null;
	private JPanel chatPanel = null;
	private JTextPane chatArea = null;
	private JPanel bottomPanel = null;
	private JPanel chatSenderPanel = null;
	private JTextField chatTextField = null;
	private JLabel sendButton = null;
	private JLabel readyButton = null;

	/**
	 * private Fields
	 */
	// border for ready button
	private final int defaultWidth = 8;
	private final int borderWidth = 3;
	private final Border defaultBorder = new EmptyBorder(defaultWidth, defaultWidth, defaultWidth, defaultWidth);
	private final Border offBorder = BorderFactory.createCompoundBorder(new EmptyBorder(borderWidth, borderWidth, borderWidth, borderWidth), defaultBorder);
	private final Border onBorder = BorderFactory.createCompoundBorder(new LineBorder(Design.Themes.Chat.Foreground, borderWidth), defaultBorder);
	private final Border intermBorder = BorderFactory.createCompoundBorder(new LineBorder(new Color(0xB2B2B2), borderWidth), defaultBorder);

	private final StyleContext styleContext = new StyleContext();
	private final Style chatStyle = styleContext.addStyle("CHAT", null);
	private final Style systemStyle = styleContext.addStyle("SYSTEM", null);

	private LinkedList<ActionListener> gameEndListener = new LinkedList<ActionListener>();
	private LinkedList<CommandListener> passCommandOutListener = new LinkedList<CommandListener>();

	private ExecutorService thread = null;
	private boolean disposed = false;

	private Socket socket = null;
	private PrintWriter out = null;
	private Scanner in = null;

	private boolean isReady = false;
	private GameFrame gameFrame = null;

	/**
	 * Constructors
	 */
	public WaitingFrame(Socket socket)
	{
		initializeComponents();

		chatStyle.addAttribute(StyleConstants.Foreground, Design.Themes.Chat.Foreground);
		systemStyle.addAttribute(StyleConstants.Foreground, Design.Themes.Chat.SystemForeground);
		systemStyle.addAttribute(StyleConstants.Bold, Boolean.valueOf(true));

		this.socket = socket;
		try
		{
			out = new PrintWriter(this.socket.getOutputStream());
			in = new Scanner(this.socket.getInputStream());
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}

		thread = Executors.newSingleThreadExecutor();
		thread.execute(new Runnable()
		{
			@Override
			public void run()
			{
				while (!disposed)
				{
					String cmd;
					try
					{
						cmd = in.nextLine().trim();
					}
					catch (NoSuchElementException e)
					{
						continue;
					}

					System.out.println("WAITING: " + cmd);

					switch (cmd)
					{
					// Passed
					case "newuser":
					{
						String name = in.nextLine().trim();
						raisePassCommandOutEvent(cmd, new Object[] { name });
						break;
					}

					case "deluser":
					{
						String name = in.nextLine().trim();
						raisePassCommandOutEvent(cmd, new Object[] { name });
						break;
					}

					case "broadcast":
					case "whisper":
					{
						String msg = in.nextLine();
						raisePassCommandOutEvent(cmd, new Object[] { msg });
						break;
					}

					case "updtstat":
					{
						String name = in.nextLine();
						int state = in.nextInt();
						raisePassCommandOutEvent(cmd, new Object[] { name, state });
						break;
					}

					// Handled here
					case "gchatrec":
					{
						String message = in.nextLine();
						printAnnounement(message);
						break;
					}

					case "announce":
					{
						String message = in.nextLine();
						printAnnounement(message);
						break;
					}

					case "start":
					{
						printAnnounement("Starting game...");
						try
						{
							Thread.sleep(1000);
						}
						catch (InterruptedException e1)
						{
							e1.printStackTrace();
						}

						isReady = false;
						readyButton.setBorder(offBorder);

						boolean attackFirst = in.nextInt() == 1;
						gameFrame = new GameFrame(attackFirst);
						gameFrame.addSendQuizListener(new CommandListener()
						{
							@Override
							public void commanded(CommandEvent e)
							{
								int op1 = (int) e.getArgument(0);
								int op2 = (int) e.getArgument(1);
								out.println("quiz");
								out.println(op1);
								out.println(op2);
								out.flush();
							}
						});
						gameFrame.addSendAnswerListener(new CommandListener()
						{
							@Override
							public void commanded(CommandEvent e)
							{
								int ans = (int) e.getArgument(0);
								out.println("answer");
								out.println(ans);
								out.flush();
							}
						});
						gameFrame.addGameOverListener(new ActionListener()
						{
							@Override
							public void actionPerformed(ActionEvent e)
							{
								if (e.getActionCommand() != null)
								{
									out.println("srndr");
									out.flush();
								}
								
								setVisible(true);
								gameFrame.setVisible(false);
								gameFrame.dispose();
								gameFrame = null;
							}
						});

						setVisible(false);
						gameFrame.setVisible(true);
						break;
					}

					case "quiz":
					{
						int op1 = in.nextInt();
						int op2 = in.nextInt();
						gameFrame.setQuiz(op1, op2);
						break;
					}

					case "correct":
					{
						gameFrame.openQuizField();
						break;
					}
					
					case "srndr":
					{
						gameFrame.overGame();
						gameFrame.showInfo("상대방이 항복했습니다!");
						
						out.println("recme");
						out.flush();
						
						boolean randomBinary = (int)(Math.random() * 100) % 2 == 1;
						printAnnounement(randomBinary ? ">> Congratulations!!" : ">> You won!");
						break;
					}

					case "win":
					{
						gameFrame.overGame();
						gameFrame.showInfo("이겼습니다!");
						
						out.println("recme");
						out.flush();
						
						boolean randomBinary = (int)(Math.random() * 100) % 2 == 1;
						printAnnounement(randomBinary ? ">> Congratulations!!" : ">> You won!");
						break;
					}

					case "lost":
					{
						gameFrame.overGame();
						gameFrame.showInfo("졌습니다.");
						
						boolean randomBinary = (int)(Math.random() * 100) % 2 == 1;
						printAnnounement(randomBinary ? ">> You're gonna make it next time." : "You lost...");
						break;
					}

					case "endgame":
					{
						raiseGameEndEvent();
						break;
					}

					default:
						break;
					}

					try
					{
						Thread.sleep(10);
					}
					catch (InterruptedException e)
					{
					}
				}

				System.out.println("WAITING LOOP END");
			}
		});
	}

	/**
	 * public Methods
	 */
	public void addGameEndListener(ActionListener l)
	{
		gameEndListener.add(l);
	}

	public boolean removeGameEndListener(ActionListener l)
	{
		return gameEndListener.remove(l);
	}

	public void addPassCommandOutListener(CommandListener l)
	{
		passCommandOutListener.add(l);
	}

	public boolean removePassCommandOutListener(CommandListener l)
	{
		return passCommandOutListener.remove(l);
	}

	@Override
	public void dispose()
	{
		disposed = true;

		super.dispose();
		thread.shutdownNow();
	}

	/**
	 * private Methods
	 */
	private void initializeComponents()
	{
		rootPanel = new JPanel();
		topPanel = new JPanel();
		exitButton = new JLabel();
		chatPanel = new JPanel();
		chatArea = new JTextPane();
		bottomPanel = new JPanel();
		chatSenderPanel = new JPanel();
		chatTextField = new JTextField();
		sendButton = new JLabel();
		readyButton = new JLabel();

		//
		// exitButton
		//
		Design.Themes.Identification.initializeLabel(exitButton, "X");
		exitButton.setForeground(Color.LIGHT_GRAY);
		exitButton.setFont(Design.Fonts.DefaultFont.deriveFont(13.0F).deriveFont(Font.BOLD));
		exitButton.addMouseListener(new MouseListener()
		{
			@Override
			public void mouseClicked(MouseEvent e)
			{
				if (e.getButton() == MouseEvent.BUTTON1)
				{
					requestEndGame();
				}
			}

			@Override
			public void mouseEntered(MouseEvent e)
			{
				exitButton.setForeground(Color.WHITE);
			}

			@Override
			public void mouseExited(MouseEvent e)
			{
				exitButton.setForeground(Color.LIGHT_GRAY);
			}

			@Override
			public void mousePressed(MouseEvent e)
			{
				// NOTHING
			}

			@Override
			public void mouseReleased(MouseEvent e)
			{
				// NOTHING
			}
		});

		//
		// topPanel
		//
		topPanel.setLayout(new BorderLayout());
		topPanel.setBackground(Design.Themes.Chat.RootBackground);
		topPanel.add(exitButton, BorderLayout.EAST);

		//
		// chatArea
		//
		chatArea.setEditable(false);
		chatArea.setBackground(Design.Themes.Chat.Background);
		chatArea.setForeground(Design.Themes.Chat.Foreground);
		chatArea.setSelectionColor(Color.LIGHT_GRAY);
		chatArea.setFont(Design.Fonts.DefaultFont.deriveFont(15F));

		//
		// chatPanel
		//
		chatPanel.setLayout(new BorderLayout());
		chatPanel.setBackground(Design.Themes.Chat.RootBackground);
		chatPanel.setPreferredSize(new Dimension(763, 100));
		chatPanel.setBorder(new RoundedBorder(Color.LIGHT_GRAY, Design.Themes.Chat.Background, 2.5F, 20));
		chatPanel.add(chatArea, BorderLayout.CENTER);

		//
		// chatTextField
		//
		Design.Themes.Identification.initializeTextField(chatTextField, 0);
		chatTextField.setBorder(new LineBorder(Color.LIGHT_GRAY));
		chatTextField.setForeground(Design.Themes.Chat.Foreground);
		chatTextField.setBackground(Design.Themes.Chat.Background);
		chatTextField.setFont(Design.Fonts.DefaultFont.deriveFont(17F));
		chatTextField.setPreferredSize(new Dimension(650, 30));
		chatTextField.addKeyListener(new KeyTypeListener()
		{
			@Override
			public void keyTyped(KeyEvent e)
			{
				switch (e.getKeyChar())
				{
				case '\n':
					sendChat();
					break;
				}
			}
		});

		//
		// sendButton
		//
		sendButton.setBorder(new LineBorder(Color.LIGHT_GRAY));
		sendButton.setIcon(new ImageIcon(new ImageIcon("images/send.png").getImage().getScaledInstance(20, 20, Image.SCALE_DEFAULT)));
		sendButton.setHorizontalAlignment(SwingConstants.CENTER);
		sendButton.setPreferredSize(new Dimension(60, 0));
		sendButton.setBorder(null);
		sendButton.addMouseListener(new MouseClickListener()
		{
			@Override
			public void mouseClicked(MouseEvent e)
			{
				if (e.getButton() == MouseEvent.BUTTON1)
				{
					sendChat();
				}
			}
		});

		//
		// chatSenderPanel
		//
		chatSenderPanel.setLayout(new BorderLayout());
		chatSenderPanel.setBackground(Design.Themes.Chat.RootBackground);
		chatSenderPanel.add(chatTextField, BorderLayout.CENTER);
		chatSenderPanel.add(sendButton, BorderLayout.EAST);

		//
		// readyButton
		//
		readyButton.setText("READY");
		readyButton.setHorizontalAlignment(SwingConstants.CENTER);
		readyButton.setVerticalAlignment(SwingConstants.CENTER);
		readyButton.setForeground(Design.Themes.Chat.Foreground);
		readyButton.setPreferredSize(new Dimension(100, 0));
		readyButton.setFont(Design.Fonts.Arial.deriveFont(14F).deriveFont(Font.BOLD));
		readyButton.setBorder(offBorder);
		readyButton.addMouseListener(new MouseClickListener()
		{
			@Override
			public void mousePressed(MouseEvent e)
			{
				readyButton.setBorder(intermBorder);
			}

			@Override
			public void mouseReleased(MouseEvent e)
			{
				isReady = !isReady;
				if (isReady)
				{
					readyButton.setBorder(onBorder);
					out.println("ready");
				}
				else
				{
					readyButton.setBorder(offBorder);
					out.println("unready");
				}
				out.flush();
			}

			@Override
			public void mouseClicked(MouseEvent e)
			{
			}
		});

		//
		// bottomPanel
		//
		bottomPanel.setLayout(new BorderLayout());
		bottomPanel.setBorder(new EmptyBorder(10, 0, 0, 0));
		bottomPanel.setBackground(Design.Themes.Chat.RootBackground);
		bottomPanel.add(chatSenderPanel, BorderLayout.CENTER);
		bottomPanel.add(readyButton, BorderLayout.EAST);

		//
		// rootPanel
		//
		rootPanel.setLayout(new BorderLayout());
		rootPanel.setBorder(new EmptyBorder(25, 50, 25, 50));
		rootPanel.setBackground(Design.Themes.Chat.RootBackground);
		rootPanel.add(topPanel, BorderLayout.NORTH);
		rootPanel.add(chatPanel, BorderLayout.CENTER);
		rootPanel.add(bottomPanel, BorderLayout.SOUTH);

		//
		// WaitingFrame
		//
		this.setContentPane(rootPanel);
		this.setSize(863, 585);
		this.setLocationRelativeTo(null);
		this.setUndecorated(true);
		DragMoveListener dragMove = new DragMoveListener(this, MouseEvent.BUTTON1);
		this.addMouseMotionListener(dragMove);
		this.addMouseListener(dragMove);
		this.addWindowListener(new WindowClosingListener()
		{
			@Override
			public void windowClosing(WindowEvent e)
			{
				requestEndGame();
			}
		});
	}

	private void requestEndGame()
	{
		out.println("endgame");
		out.flush();
	}

	private void raiseGameEndEvent()
	{
		Iterator<ActionListener> iterator = gameEndListener.listIterator();
		while (iterator.hasNext())
		{
			iterator.next().actionPerformed(new ActionEvent(this, 0, null));
		}
	}

	private void raisePassCommandOutEvent(String command, Object[] args)
	{
		Iterator<CommandListener> iterator = passCommandOutListener.listIterator();
		while (iterator.hasNext())
		{
			iterator.next().commanded(new CommandEvent(command, args));
		}
	}

	private void sendChat()
	{
		String message = chatTextField.getText().trim();

		if (message.length() > 0)
		{
			out.println("gchat");
			out.println(message);
			out.flush();

			chatTextField.setText(null);
		}
	}
	
	private void printAnnounement(String message)
	{
		try
		{
			Document doc = chatArea.getDocument();
			doc.insertString(doc.getLength(), message + '\n', systemStyle);
		}
		catch (BadLocationException e)
		{
			e.printStackTrace();
		}
	}
}
