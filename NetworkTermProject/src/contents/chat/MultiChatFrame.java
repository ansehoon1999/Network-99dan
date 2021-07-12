package contents.chat;

import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.net.*;
import java.util.*;
import java.util.concurrent.*;
import javax.swing.*;
import javax.swing.border.*;
import javax.swing.custom.*;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.text.Style;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyleContext;

import contents.game.GameRequestDialog;
import contents.game.WaitingFrame;
import generic.*;

public class MultiChatFrame extends JFrame
{
	/**
	 * GUI Components
	 */
	private JPanel rootPanel = null;
	private JPanel topPanel = null;
	private JLabel exitLabel = null;
	private JLabel participantLabel = null;
	private JPanel participantPanel = null;
	private JPanel participantListPanel = null;
	private JList<String> participantList = null;
	private JScrollPane listScroller = null; /////////////////////
	private JPanel participantButtonPanel = null;
	private FlatButton gameButton = null;
	private FlatButton recordButton = null;
	private JPanel chatPanel = null;
	private JPanel chatTextAreaPanel = null;
	private JTextPane chatTextArea = null;
	private JPanel chatSenderPanel = null;
	private JPanel chatTextFieldPanel = null;
	private JLabel whisperDestLabel = null;
	private JTextField chatTextField = null;
	private JPanel sendButtonPanel = null;
	private JPanel sendButton = null;
	private JLabel sendIcon = null;

	/**
	 * private final Fields
	 */
	private final MultiChatFrame This = this;
	private final StyleContext styles = new StyleContext();
	private final Style chatStyle = styles.addStyle("CHAT", null);
	private final Style whisperStyle = styles.addStyle("WHISPER", null);
	private final Style systemStyle = styles.addStyle("SYSTEM", null);
	private final Style errorStyle = styles.addStyle("ERROR", null);

	/**
	 * private Fields
	 */
	private String id = null;
	private Vector<String> userList = new Vector<String>();
	private Comparator<String> strComp = new Comparator<String>()
	{
		@Override
		public int compare(String o1, String o2)
		{
			return o1.compareTo(o2);
		}
	};
	private HashMap<String, Integer> stateMap = new HashMap<String, Integer>();
	private ExecutorService receiver = null;
	private Boolean locker = false;
	private String invitationFrom = null;
	private Socket socket = null;
	private PrintWriter out = null;
	private Scanner in = null;

	public MultiChatFrame()
	{
		initializeComponents();

		chatStyle.addAttribute(StyleConstants.Foreground, Design.Themes.Chat.Foreground);
		whisperStyle.addAttribute(StyleConstants.Foreground, Design.Themes.Chat.WhisperForeround);
		systemStyle.addAttribute(StyleConstants.Foreground, Design.Themes.Chat.SystemForeground);
		systemStyle.addAttribute(StyleConstants.Bold, Boolean.valueOf(true));
		errorStyle.addAttribute(StyleConstants.Foreground, Design.Themes.Chat.ErrorForeground);
		errorStyle.addAttribute(StyleConstants.Bold, Boolean.valueOf(true));

		//
		// receiver
		//
		receiver = Executors.newSingleThreadExecutor();
		receiver.execute(new Runnable()
		{
			@Override
			public void run()
			{
				while (true)
				{
					synchronized (locker)
					{
						if (locker)
						{
							try
							{
								Thread.sleep(10);
							}
							catch (InterruptedException e)
							{
								e.printStackTrace();
							}

							continue;
						}
					}

					if (in != null)
					{
						String cmd = "";
						try
						{
							cmd = in.nextLine().trim();
						}
						catch (Exception e)
						{
							break;
						}

						System.out.println("CMD: " + cmd);
						respond(cmd);

					}

					try
					{
						Thread.sleep(10);
					}
					catch (InterruptedException e)
					{
						e.printStackTrace();
					}
				}

				System.out.println("CLIENT LOOP OVER");
			}
		});
	}

	private void initializeComponents()
	{
		rootPanel = new JPanel(new BorderLayout());
		topPanel = new JPanel(new FlowLayout(FlowLayout.LEADING));
		exitLabel = new JLabel();
		participantLabel = new JLabel();
		participantPanel = new JPanel(new BorderLayout());
		participantListPanel = new JPanel(new BorderLayout());
		participantList = new JList<String>(userList);
		listScroller = new JScrollPane();
		participantButtonPanel = new JPanel();
		gameButton = new FlatButton();
		recordButton = new FlatButton();
		chatPanel = new JPanel();
		chatTextAreaPanel = new JPanel();
		chatTextArea = new JTextPane();
		chatSenderPanel = new JPanel();
		chatTextFieldPanel = new JPanel();
		whisperDestLabel = new JLabel();
		chatTextField = new JTextField();
		sendButtonPanel = new JPanel();
		sendButton = new JPanel();
		sendIcon = new JLabel();

		//
		// participantLabel
		//
		Design.Themes.Identification.initializeLabel(participantLabel, "Participant");
		participantLabel.setPreferredSize(new Dimension(180, 35));
		participantLabel.setBorder(new MatteBorder(0, 0, 2, 0, Color.LIGHT_GRAY));
		new Design.Fonts();
		participantLabel.setFont(Design.Fonts.Dreamspeak.deriveFont(26F));

		//
		// exitLabel
		//
		Design.Themes.Identification.initializeLabel(exitLabel, "X");
		exitLabel.setForeground(Color.LIGHT_GRAY);
		exitLabel.setFont(Design.Fonts.DefaultFont.deriveFont(13.0F).deriveFont(Font.BOLD));
		exitLabel.addMouseListener(new MouseListener()
		{
			@Override
			public void mouseClicked(MouseEvent e)
			{
				if (e.getButton() == MouseEvent.BUTTON1)
				{
					out.println("sigout");
					out.flush();
					System.exit(0);
				}
			}

			@Override
			public void mouseEntered(MouseEvent e)
			{
				exitLabel.setForeground(Color.WHITE);
			}

			@Override
			public void mouseExited(MouseEvent e)
			{
				exitLabel.setForeground(Color.LIGHT_GRAY);
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
		topPanel.setBorder(new EmptyBorder(0, 0, 10, 30));
		topPanel.setBackground(Design.Themes.Chat.RootBackground);
		topPanel.add(participantLabel, BorderLayout.WEST);
		topPanel.add(exitLabel, BorderLayout.EAST);

		//
		// participantList
		//
		participantList.setBackground(Design.Themes.Chat.Background);
		participantList.setForeground(Design.Themes.Chat.Foreground);
		participantList.setSelectionBackground(Color.LIGHT_GRAY);
		participantList.setSelectionForeground(Color.BLACK);
		participantList.setFont(Design.Fonts.DefaultFont.deriveFont(15F));
		participantList.setPreferredSize(new Dimension(200, 300));
		participantList.setCellRenderer(new ListItemStyle());

		//
		// listScroller
		//
		listScroller.setBorder(new EmptyBorder(0, 0, 0, 0));
		listScroller.setViewportView(participantList);
		participantList.setLayoutOrientation(JList.VERTICAL);

		//
		// participantListPanel
		//
		participantListPanel.setBackground(Design.Themes.Chat.RootBackground);
		participantListPanel.setPreferredSize(new Dimension(317, 100));
		participantListPanel.setBorder(new RoundedBorder(Color.LIGHT_GRAY, Design.Themes.Chat.Background, 2.5F, 20));
		participantListPanel.add(listScroller, BorderLayout.CENTER);

		//
		// gameButton
		//
		Design.Themes.Identification.initializeFlatButton(gameButton, "Game");
		gameButton.setBackground(Design.Themes.Chat.Background);
		gameButton.setMaxLineHeight(35);
		gameButton.setFont(Design.Fonts.Arial.deriveFont(20F));
		gameButton.addMouseListener(new MouseClickListener()
		{
			@Override
			public void mouseClicked(MouseEvent e)
			{
				if (e.getButton() == MouseEvent.BUTTON1)
				{
					requestGame();
				}
			}
		});

		//
		// recordButton
		//
		Design.Themes.Identification.initializeFlatButton(recordButton, "Record");
		recordButton.setBackground(Design.Themes.Chat.Background);
		recordButton.setMaxLineHeight(35);
		recordButton.setFont(Design.Fonts.Arial.deriveFont(20F));
		recordButton.addMouseListener(new MouseClickListener()
		{
			@Override
			public void mouseClicked(MouseEvent e)
			{
				if (e.getButton() == MouseEvent.BUTTON1)
				{
					showRecord();
				}
			}
		});

		//
		// participantButtonPanel
		//
		participantButtonPanel.setLayout(new BorderLayout());
		participantButtonPanel.setPreferredSize(new Dimension(participantButtonPanel.getWidth(), 70));
		participantButtonPanel.setBorder(new EmptyBorder(0, 50, 0, 50));
		participantButtonPanel.setBackground(Design.Themes.Chat.RootBackground);
		participantButtonPanel.add(gameButton, BorderLayout.WEST);
		participantButtonPanel.add(recordButton, BorderLayout.EAST);

		//
		// participantPanel
		//
		participantPanel.setBackground(Design.Themes.Chat.RootBackground);
		participantPanel.add(participantListPanel, BorderLayout.CENTER);
		participantPanel.add(participantButtonPanel, BorderLayout.SOUTH);

		//
		// chatTextArea
		//
		chatTextArea.setEditable(false);
		chatTextArea.setBackground(Design.Themes.Chat.Background);
		chatTextArea.setForeground(Design.Themes.Chat.Foreground);
		chatTextArea.setSelectionColor(Color.LIGHT_GRAY);
		chatTextArea.setFont(Design.Fonts.DefaultFont.deriveFont(15F));

		//
		// chatTextAreaPanel
		//
		chatTextAreaPanel.setLayout(new BorderLayout());
		chatTextAreaPanel.setBackground(Design.Themes.Chat.RootBackground);
		chatTextAreaPanel.setPreferredSize(new Dimension(763, 100));
		chatTextAreaPanel.setBorder(new RoundedBorder(Color.LIGHT_GRAY, Design.Themes.Chat.Background, 2.5F, 20));
		chatTextAreaPanel.add(chatTextArea, BorderLayout.CENTER);

		//
		// chatTextField
		//
		Design.Themes.Identification.initializeTextField(chatTextField, 0);
		chatTextField.setBorder(new EmptyBorder(0, 0, 0, 0));
		chatTextField.setForeground(Design.Themes.Chat.Foreground);
		chatTextField.setBackground(Design.Themes.Chat.Background);
		chatTextField.setFont(Design.Fonts.DefaultFont.deriveFont(17F));
		chatTextField.setPreferredSize(new Dimension(650, 30));
		chatTextField.addKeyListener(new KeyTypeListener()
		{
			private boolean deleteSwitch = false;

			@Override
			public void keyTyped(KeyEvent e)
			{
				switch (e.getKeyChar())
				{
				case ' ':
				{
					String str = chatTextField.getText();
					if (str.length() > 1)
					{
						if (str.charAt(0) == '@')
						{
							String dest = str.substring(1, chatTextField.getCaretPosition());
							if (!id.equals(dest))
							{
								for (int i = 0; i < userList.size(); i++)
								{
									if (userList.get(i).substring(3).equals(dest))
									{
										whisperDestLabel.setText('@' + dest + ' ');
										chatTextField.setText(str.substring(chatTextField.getCaretPosition()));
										chatTextField.setCaretPosition(0);
										deleteSwitch = true;
										e.consume();
										break;
									}
								}
								break;
							}
						}
					}
					deleteSwitch = false;
					break;
				}

				case '\b':
					if (whisperDestLabel.getText() != null)
					{
						if (chatTextField.getCaretPosition() == 0)
						{
							if (deleteSwitch)
							{
								whisperDestLabel.setText(null);
								deleteSwitch = false;
							}
							else
							{
								deleteSwitch = true;
							}
						}
					}
					break;

				case '\n':
				{
					if (whisperDestLabel.getText() == null)
					{
						sendChat();
					}
					else
					{
						String str = whisperDestLabel.getText().substring(1).trim();
						sendWhisper(str);
					}
					break;
				}

				default:
					deleteSwitch = chatTextField.getCaretPosition() == 0;
					break;
				}
			}
		});

		//
		// whisperDestLabel
		//
		Design.Themes.Identification.initializeLabel(whisperDestLabel, null);
		whisperDestLabel.setFont(chatTextField.getFont().deriveFont(Font.BOLD));
		whisperDestLabel.setForeground(Design.Themes.Chat.WhisperForeround);

		//
		// chatTextFieldPanel
		//
		chatTextFieldPanel.setLayout(new BorderLayout());
		chatTextFieldPanel.setBorder(BorderFactory.createCompoundBorder(new LineBorder(Color.LIGHT_GRAY), new EmptyBorder(0, 8, 0, 3)));
		chatTextFieldPanel.setBackground(Design.Themes.Chat.Background);
		chatTextFieldPanel.add(whisperDestLabel, BorderLayout.WEST);
		chatTextFieldPanel.add(chatTextField, BorderLayout.CENTER);

		//
		// sendIcon
		//
		sendIcon.setIcon(new ImageIcon(new ImageIcon("images/send.png").getImage().getScaledInstance(20, 20, Image.SCALE_DEFAULT)));

		//
		// sendButton
		//
		sendButton.setBackground(Design.Themes.Chat.RootBackground);
		RoundedBorder border = new RoundedBorder(Color.LIGHT_GRAY, Design.Themes.Chat.RootBackground, 2.5F, 8);
		border.setUsePadding(false);
		sendButton.setBorder(border);
		sendButton.setPreferredSize(new Dimension(88, 0));
		sendButton.add(sendIcon);
		sendButton.addMouseListener(new MouseListener()
		{
			private final Color HoverColor = new Color(0x2A2A2A);
			private final Color PressedColor = new Color(0x1F1F1F);

			@Override
			public void mouseEntered(MouseEvent e)
			{
				border.setBackground(HoverColor);
				sendButton.repaint();
			}

			@Override
			public void mouseExited(MouseEvent e)
			{
				border.setBackground(Design.Themes.Chat.RootBackground);
				sendButton.repaint();
			}

			@Override
			public void mousePressed(MouseEvent e)
			{
				border.setBackground(PressedColor);
				sendButton.repaint();
			}

			@Override
			public void mouseReleased(MouseEvent e)
			{
				border.setBackground(HoverColor);
				sendButton.repaint();
			}

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
		// sendButtonPanel
		//
		sendButtonPanel.setLayout(new BorderLayout());
		sendButtonPanel.setBorder(new EmptyBorder(0, 12, 0, 0));
		sendButtonPanel.setBackground(Design.Themes.Chat.RootBackground);
		sendButtonPanel.add(sendButton);

		//
		// chatSenderPanel
		//
		chatSenderPanel.setLayout(new BorderLayout());
		chatSenderPanel.setBorder(new EmptyBorder(20, 0, 20, 0));
		chatSenderPanel.setBackground(Design.Themes.Chat.RootBackground);
		chatSenderPanel.add(sendButtonPanel, BorderLayout.EAST);
		chatSenderPanel.add(chatTextFieldPanel, BorderLayout.CENTER);

		//
		// chatPanel
		//
		chatPanel.setLayout(new BorderLayout());
		chatPanel.setBorder(new EmptyBorder(0, 30, 0, 30));
		chatPanel.setBackground(Design.Themes.Chat.RootBackground);
		chatPanel.add(chatTextAreaPanel, BorderLayout.CENTER);
		chatPanel.add(chatSenderPanel, BorderLayout.SOUTH);

		//
		// rootPanel
		//
		rootPanel.setBorder(new EmptyBorder(25, 50, 0, 20));
		rootPanel.setBackground(Design.Themes.Chat.RootBackground);
		rootPanel.add(topPanel, BorderLayout.NORTH);
		rootPanel.add(participantPanel, BorderLayout.WEST);
		rootPanel.add(chatPanel, BorderLayout.CENTER);

		//
		// MultiChatFrame
		//
		this.setContentPane(rootPanel);
		this.setUndecorated(true);
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.setSize(1245, 585);
		this.setLocationRelativeTo(null);
		DragMoveListener dragMove = new DragMoveListener(this, MouseEvent.BUTTON1);
		this.addMouseMotionListener(dragMove);
		this.addMouseListener(dragMove);
		this.addWindowListener(new WindowClosingListener()
		{
			@Override
			public void windowClosing(WindowEvent e)
			{
				if (out != null)
				{
					out.println("sigout");
					out.flush();
				}
			}
		});
		this.setDefaultCloseOperation(EXIT_ON_CLOSE);
	}

	public Socket initializeConnection(String id)
	{
		try
		{
			this.id = id;

			System.out.println("ID: " + id);
			userList.clear();
			userList.add("AAA" + id);

			socket = new Socket(Preferences.HostAddress, Preferences.PortNumber);
			out = new PrintWriter(socket.getOutputStream());
			in = new Scanner(socket.getInputStream());

			out.println("init");
			out.println(id);
			out.flush();
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}

		return socket;
	}

	@Override
	public void setVisible(boolean visible)
	{
		super.setVisible(visible);

		if (visible)
		{
			chatTextField.grabFocus();
		}
	}

	private void sendChat()
	{
		String message = chatTextField.getText().trim();

		if (message.length() > 0)
		{
			out.println("chatting");
			out.println(message);
			out.flush();

			chatTextField.setText(null);
		}
	}

	private void sendWhisper(String dest)
	{
		String message = chatTextField.getText().trim();

		if (message.length() > 0)
		{
			out.println("whisper");
			out.println(dest);
			out.println(message);
			out.flush();

			chatTextField.setText(null);
			whisperDestLabel.setText(null);
		}
	}

	private void showRecord()
	{
		String selectedUser = participantList.getSelectedValue().substring(3);

		if (selectedUser != null)
		{
			out.println("askinf");
			out.println(selectedUser);
			out.flush();
		}
	}

	private void requestGame()
	{
		String selectedUser = participantList.getSelectedValue();

		if (selectedUser != null && selectedUser.length() > 0)
		if (selectedUser.startsWith("CAN"))
		{
			invitationFrom = selectedUser.substring(3);
			out.println("reqgam");
			out.println(invitationFrom);
			out.flush();
		}
	}

	private void newUser(String name)
	{
		try
		{
			Document doc = chatTextArea.getDocument();
			doc.insertString(doc.getLength(), ">> " + name + " has joined.\n", systemStyle);
		}
		catch (BadLocationException e)
		{
		}

		userList.add("CAN" + name);
		userList.sort(strComp);
		participantList.setListData(userList);
	}

	private void deleteUser(String name)
	{
		try
		{
			Document doc = chatTextArea.getDocument();
			doc.insertString(doc.getLength(), ">> " + name + " has left.\n", systemStyle);
		}
		catch (BadLocationException e)
		{
		}

		userList.remove(name);
		for (int i = 0; i < userList.size(); i++)
		{
			if (userList.get(i).substring(3).equals(name))
			{
				userList.remove(i);
				break;
			}
		}
		userList.sort(strComp);
		participantList.setListData(userList);
	}

	private void printMessage(String msg, boolean isWhisper)
	{
		try
		{
			Document doc = chatTextArea.getDocument();
			doc.insertString(doc.getLength(), msg + "\n", isWhisper ? whisperStyle : chatStyle);
		}
		catch (BadLocationException e)
		{
		}
	}

	private void updateState(String name, int state)
	{
		for (int i = 0; i < userList.size(); i++)
		{
			if (userList.get(i).substring(3).equals(name))
			{
				userList.remove(i);
				if (state == 0)
				{
					userList.add("NOT" + name);
				}
				else
				{
					userList.add("CAN" + name);
				}

				break;
			}
		}
		userList.sort(strComp);
		participantList.setListData(userList);
	}

	private void respond(String cmd)
	{
		String resp;
		String name;
		String msg;

		switch (cmd)
		{
		case "":
			break;

		case "usrlist":
			try
			{
				int size = in.nextInt();
				for (int i = 0; i < size;)
				{
					while ((name = in.nextLine().trim()).length() == 0)
						;
					int state = in.nextInt();
					if (name.equals(""))
					{
						continue;
					}

					stateMap.put(name, state);
					if (state == 0)
					{
						userList.add("NOT" + name);
					}
					else
					{
						userList.add("CAN" + name);
					}
					userList.sort(strComp);
					participantList.setListData(userList);
					i++;
				}
			}
			catch (Exception e)
			{
				e.printStackTrace();
			}
			break;

		case "newuser":
			name = in.nextLine().trim();
			newUser(name);
			break;

		case "deluser":
			name = in.nextLine().trim();
			deleteUser(name);
			break;

		case "broadcast":
			msg = in.nextLine();
			printMessage(msg, false);
			break;

		case "whisper":
			msg = in.nextLine();
			printMessage(msg, true);
			break;

		case "record":
			if (in.nextByte() == 1)
			{
				name = participantList.getSelectedValue();
				int win = in.nextInt();
				int lose = in.nextInt();
				long signInTime = in.nextLong();
				int state = in.nextInt();
				System.out.println(state);

				if (name != null)
				{
					new RecordFrame(name.substring(3), win, lose, signInTime, state).setVisible(true);
				}
			}
			break;

		case "updtstat":
		{
			name = in.nextLine();
			int state = in.nextInt();
			updateState(name, state);
			break;
		}

		case "invite": // to Guest
		{
			String from = in.nextLine();
			invitationFrom = from;
			GameRequestDialog dialog = new GameRequestDialog(this, from, true);

			this.setEnabled(false);
			dialog.addActionListener(new ActionListener()
			{
				@Override
				public void actionPerformed(ActionEvent e)
				{
					if (dialog.isAccepted())
					{
						out.println("accept");
					}
					else
					{
						out.println("reject");
					}
					out.flush();

					dialog.setVisible(false);
					This.setEnabled(true);
				}
			});
			dialog.setVisible(true);
			break;
		}

		case "accepted": // to Host
		{
			int gameId = in.nextInt();
			
			for (int i = 0; i < userList.size(); i++)
			{
				if (userList.get(i).substring(3).equals(invitationFrom))
				{
					userList.remove(i);
					userList.add("NOT" + invitationFrom);

					userList.sort(strComp);
					participantList.setListData(userList);
				}
			}

			out.println("jngame");
			out.println(gameId);
			out.flush();

			in.nextLine();
			String result = in.nextLine();

			if (result.equals("ok"))
			{
				synchronized (locker)
				{
					locker = true;
				}

				WaitingFrame waitingFrame = new WaitingFrame(socket);
				waitingFrame.addPassCommandOutListener(new CommandListener()
				{
					@Override
					public void commanded(CommandEvent e)
					{
						switch (e.getCommand())
						{
						case "newuser":
						{
							newUser((String) e.getArgument(0));
							break;
						}

						case "deluser":
						{
							deleteUser((String) e.getArgument(0));
							break;
						}

						case "broadcast":
						{
							printMessage((String) e.getArgument(0), false);
							break;
						}
						case "whisper":
						{
							printMessage((String) e.getArgument(0), true);
							break;
						}

						case "updtstat":
						{
							updateState((String) e.getArgument(0), (int) e.getArgument(1));
							break;
						}

						default:
							break;
						}
					}
				});
				waitingFrame.addGameEndListener(new ActionListener()
				{

					@Override
					public void actionPerformed(ActionEvent e)
					{
						This.setVisible(true);
						waitingFrame.setVisible(false);
						waitingFrame.dispose();

						synchronized (locker)
						{
							locker = false;
						}
					}
				});
				this.setVisible(false);
				waitingFrame.setVisible(true);
			}
			else
			{
				try
				{

					Document doc = chatTextArea.getDocument();
					doc.insertString(doc.getLength(), "Error: " + result + "\n", errorStyle);
				}
				catch (BadLocationException e)
				{
				}
			}
			break;
		}

		case "rejected": // to Host
		{
			try
			{
				Document doc = chatTextArea.getDocument();
				doc.insertString(doc.getLength(), ">> Request rejected.\n", systemStyle);
			}
			catch (BadLocationException e)
			{
				e.printStackTrace();
			}
			break;
		}

		case "error":
			resp = in.nextLine();
			try
			{
				Document doc = chatTextArea.getDocument();
				doc.insertString(doc.getLength(), "Error: " + resp + "\n", errorStyle);
			}
			catch (BadLocationException e)
			{
			}
			break;

		default:
			break;
		}
	}
}
