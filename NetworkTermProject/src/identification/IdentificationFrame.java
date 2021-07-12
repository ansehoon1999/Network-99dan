package identification;

import java.awt.*;
import java.awt.event.*;
import java.net.*;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.concurrent.*;

import javax.swing.*;
import javax.swing.border.*;

import generic.ConnectionListener;
import generic.Design;
import generic.DragMoveListener;

public class IdentificationFrame extends JFrame
{
	/**
	 * Nested types
	 */
	private enum IdMode
	{
		SIGNIN, SIGNUP, RESETPW
	}

	/**
	 * Event listener
	 */
	private LinkedList<ConnectionListener> signInListener = new LinkedList<ConnectionListener>();

	/**
	 * private Fields
	 */
	private IdMode currentMode = IdMode.SIGNIN;

	/**
	 * GUI elements declaration
	 */
	private JPanel rootPanel = null;
	private JPanel controlBox = null;
	private JLabel backButton = null;
	private JLabel exitButton = null;
	private JPanel contentPanel = null;
	private JLabel titleLabel = null;

	private SignInPanel signInPanel = null;
	private SignUpPanel signUpPanel = null;
	private ResetPasswordPanel resetPwPanel = null;

	/**
	 * Constructor
	 */
	public IdentificationFrame()
	{
		initializeComponents();
	}

	private void initializeComponents()
	{
		ExecutorService gc = Executors.newSingleThreadExecutor();
		gc.execute(new Runnable()
		{
			@Override
			public void run()
			{
				while (true)
				{
					System.gc();
					try
					{
						Thread.sleep(5000);
					}
					catch (InterruptedException e)
					{
						e.printStackTrace();
					}
				}
			}
		});

		this.rootPanel = new JPanel(new BorderLayout());
		this.controlBox = new JPanel(new BorderLayout());
		this.backButton = new JLabel();
		this.exitButton = new JLabel();
		this.contentPanel = new JPanel(new BorderLayout());
		this.titleLabel = new JLabel();
		this.signInPanel = new SignInPanel();
		this.signUpPanel = new SignUpPanel();
		this.resetPwPanel = new ResetPasswordPanel();

		//
		// backButton
		//
		backButton.setText("<");
		backButton.setForeground(Color.LIGHT_GRAY);
		backButton.setFont(Design.Fonts.DefaultFont.deriveFont(13.0F).deriveFont(Font.BOLD));
		backButton.setVisible(false);
		backButton.addMouseListener(new MouseListener()
		{
			@Override
			public void mouseClicked(MouseEvent e)
			{
				if (e.getButton() == MouseEvent.BUTTON1)
				{
					switchMode(IdMode.SIGNIN);
				}
			}

			@Override
			public void mouseEntered(MouseEvent e)
			{
				backButton.setForeground(Color.WHITE);
			}

			@Override
			public void mouseExited(MouseEvent e)
			{
				backButton.setForeground(Color.LIGHT_GRAY);
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
		// exitButton
		//
		exitButton.setText("X");
		exitButton.setForeground(Color.LIGHT_GRAY);
		exitButton.setFont(Design.Fonts.DefaultFont.deriveFont(13.0F).deriveFont(Font.BOLD));
		exitButton.addMouseListener(new MouseListener()
		{
			@Override
			public void mouseClicked(MouseEvent e)
			{
				if (e.getButton() == MouseEvent.BUTTON1)
				{
					System.exit(0);
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
		// controlBox
		//
		controlBox.setBackground(Design.Themes.Identification.BackgroundColor);
		controlBox.add(backButton, BorderLayout.WEST);
		controlBox.add(exitButton, BorderLayout.EAST);
		controlBox.setBorder(new EmptyBorder(5, 9, 2, 9));

		//
		// titleLabel
		//
		titleLabel.setFont(Design.Fonts.DefaultFont.deriveFont(25F));
		titleLabel.setText("Sign In");
		titleLabel.setForeground(Design.Themes.Identification.ForegroundColor);

		//
		// signInPanel
		//
		signInPanel.addSignInListener(new ConnectionListener()
		{
			@Override
			public void connected(String id)
			{
				Iterator<ConnectionListener> iterator = signInListener.listIterator();
				while (iterator.hasNext())
				{
					ConnectionListener listener = iterator.next();
					listener.connected(id);
				}
			}
		});
		signInPanel.addSignUpListener(new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent e)
			{
				switchMode(IdMode.SIGNUP);
			}
		});
		signInPanel.addResetPasswordListener(new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent e)
			{
				switchMode(IdMode.RESETPW);
			}
		});

		//
		// signUpPanel
		//
		signUpPanel.addSignUpListener(new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent e)
			{
				switchMode(IdMode.SIGNIN);
			}
		});

		//
		// resetPwPanel
		//
		resetPwPanel.addResetListener(new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent e)
			{
				switchMode(IdMode.SIGNIN);
			}
		});

		//
		// contentPanel
		//
		contentPanel.setBackground(Design.Themes.Identification.BackgroundColor);
		contentPanel.setBorder(new EmptyBorder(30, 50, 50, 50));
		contentPanel.add(titleLabel, BorderLayout.NORTH);
		contentPanel.add(signInPanel, BorderLayout.CENTER);

		//
		// rootPanel
		//
		rootPanel.setBackground(Design.Themes.Identification.BackgroundColor);
		rootPanel.add(controlBox, BorderLayout.NORTH);
		rootPanel.add(contentPanel, BorderLayout.CENTER);

		//
		// SignInFrame
		//
		this.setLocationRelativeTo(null);
		this.setSize(400, 600);
		this.setUndecorated(true);
		this.setResizable(false);
		this.setContentPane(rootPanel);
		this.setLocationRelativeTo(null);
		DragMoveListener dragMoveListener = new DragMoveListener(this, MouseEvent.BUTTON1);
		this.addMouseMotionListener(dragMoveListener);
		this.addMouseListener(dragMoveListener);
		this.setDefaultCloseOperation(EXIT_ON_CLOSE);
	}
	
	/**
	 * public Methods
	 */
	public void addSignInListener(ConnectionListener listener)
	{
		signInListener.add(listener);
	}
	
	public boolean removeSignInListener(ConnectionListener listener)
	{
		return signInListener.remove(listener);
	}

	/**
	 * private Methods
	 */
	private void switchMode(IdMode mode)
	{
		if (currentMode != mode)
		{
			switch (currentMode)
			{
			case SIGNIN:
				contentPanel.remove(signInPanel);
				signInPanel.reset();
				break;

			case SIGNUP:
				contentPanel.remove(signUpPanel);
				signUpPanel.reset();
				break;

			case RESETPW:
				contentPanel.remove(resetPwPanel);
				resetPwPanel.reset();
				break;

			default:
				break;
			}

			currentMode = mode;

			switch (mode)
			{
			case SIGNIN:
				titleLabel.setText("Sign In");
				contentPanel.add(signInPanel, BorderLayout.CENTER);
				backButton.setVisible(true);
				break;

			case SIGNUP:
				titleLabel.setText("Sign Up");
				contentPanel.add(signUpPanel, BorderLayout.CENTER);
				backButton.setVisible(true);
				break;

			case RESETPW:
				titleLabel.setText("Reset Password");
				contentPanel.add(resetPwPanel, BorderLayout.CENTER);
				backButton.setVisible(true);
				break;

			default:
				break;
			}

			contentPanel.updateUI();
		}
	}
}