package identification;

import java.awt.*;
import java.awt.event.*;
import java.io.IOException;
import java.io.PrintStream;
import java.net.Socket;
import java.util.*;

import javax.swing.*;
import javax.swing.border.*;
import javax.swing.custom.*;
import javax.swing.event.*;

import generic.ConnectionListener;
import generic.Design;
import generic.KeyTypeListener;
import generic.MouseClickListener;
import generic.Preferences;

public class SignInPanel extends ModulePanelBase
{
	private final SignInPanel This = this;

	/**
	 * Event handler
	 */
	private LinkedList<ConnectionListener> signInListener = new LinkedList<ConnectionListener>();
	private LinkedList<ActionListener> signUpListener = new LinkedList<ActionListener>();
	private LinkedList<ActionListener> resetPwListener = new LinkedList<ActionListener>();

	/**
	 * GUI elements declaration
	 */
	private JLabel userLabel = null;
	private JTextField userField = null;
	private JLabel pwLabel = null;
	private JPasswordField pwField = null;
	private ShowPasswordCheckBox showPassLabel = null;
	private FlatButton signInButton = null;
	private FlatButton signUpButton = null;
	private FlatButton resetPwButton = null;

	/**
	 * Constructor
	 */
	public SignInPanel()
	{
		initializeComponents();
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

	public void addSignUpListener(ActionListener listener)
	{
		signUpListener.add(listener);
	}

	public boolean removeSignUpListener(ActionListener listener)
	{
		return signUpListener.remove(listener);
	}

	public void addResetPasswordListener(ActionListener listener)
	{
		resetPwListener.add(listener);
	}

	public boolean removeResetPasswordListener(ActionListener listener)
	{
		return resetPwListener.remove(listener);
	}

	@Override
	public void reset()
	{
		userField.setText(null);
		pwField.setText(null);
		showPassLabel.setChecked(false);
		hideError();
	}

	/**
	 * protected Methods
	 */
	@Override
	protected void initializeComponents()
	{
		this.userLabel = new JLabel();
		this.userField = new JTextField();
		this.pwLabel = new JLabel();
		this.pwField = new JPasswordField();
		this.showPassLabel = new ShowPasswordCheckBox();
		this.signInButton = new FlatButton();
		this.signUpButton = new FlatButton();
		this.resetPwButton = new FlatButton();
		this.errorLabel = new JLabel();

		//
		// userLabel
		//
		Design.Themes.Identification.initializeLabel(userLabel, "User");

		//
		// userField
		//
		Design.Themes.Identification.initializeTextField(userField, 230);
		userField.addKeyListener(new KeyTypeListener()
		{
			@Override
			public void keyTyped(KeyEvent e)
			{
				switch (e.getKeyChar())
				{
				case '\\':
				case '/':
				case ':':
				case '*':
				case '?':
				case '\"':
				case '<':
				case '>':
				case '|':
				case ' ':
					e.consume();
					break;

				case '\n':
					raiseSignInEvent();
					break;

				default:
					break;
				}
			}
		});

		//
		// pwLabel
		//
		Design.Themes.Identification.initializeLabel(pwLabel, "Password");

		//
		// pwField
		//

		Design.Themes.Identification.initializeTextField(pwField, 230);
		pwField.addKeyListener(new KeyTypeListener()
		{
			@Override
			public void keyTyped(KeyEvent e)
			{
				switch (e.getKeyChar())
				{
				case ' ':
					e.consume();
					break;

				case '\n':
					raiseSignInEvent();
					break;

				default:
					break;
				}
			}
		});

		//
		// showPassLabel
		//
		showPassLabel.addChangeListener(new ChangeListener()
		{
			@Override
			public void stateChanged(ChangeEvent e)
			{
				if (showPassLabel.getChecked())
				{
					pwField.setEchoChar('\0');
				}
				else
				{
					pwField.setEchoChar((char) 8226);
				}
			}
		});

		//
		// signInButton
		//
		Design.Themes.Identification.initializeFlatButton(signInButton, "Sign in");
		signInButton.addMouseListener(new MouseClickListener()
		{
			@Override
			public void mouseClicked(MouseEvent e)
			{
				if (e.getButton() == MouseEvent.BUTTON1)
				{
					raiseSignInEvent();
				}
			}
		});

		//
		// signUpButton
		//
		Design.Themes.Identification.initializeFlatButton(signUpButton, "Sign up");
		signUpButton.addMouseListener(new MouseClickListener()
		{
			@Override
			public void mouseClicked(MouseEvent e)
			{
				if (e.getButton() == MouseEvent.BUTTON1)
				{
					Iterator<ActionListener> iterator = signUpListener.listIterator();
					while (iterator.hasNext())
					{
						ActionListener listener = iterator.next();
						listener.actionPerformed(new ActionEvent(This, 1, "goto sigup"));
					}
				}
			}
		});

		//
		// resetPwButton
		//
		Design.Themes.Identification.initializeFlatButton(resetPwButton, "Forgot password?");
		resetPwButton.setFont(Design.Fonts.DefaultFont.deriveFont(15F).deriveFont(Font.BOLD));
		resetPwButton.addMouseListener(new MouseClickListener()
		{
			@Override
			public void mouseClicked(MouseEvent e)
			{
				if (e.getButton() == MouseEvent.BUTTON1)
				{
					Iterator<ActionListener> iterator = resetPwListener.listIterator();
					while (iterator.hasNext())
					{
						ActionListener listener = iterator.next();
						listener.actionPerformed(new ActionEvent(This, 1, "goto setpw"));
					}
				}
			}
		});

		//
		// errorLabel
		//
		Design.Themes.Identification.initializeLabel(errorLabel, null);
		errorLabel.setFont(Design.Fonts.DefaultFont.deriveFont(15F).deriveFont(Font.BOLD));

		//
		// SignInPanel
		//
		this.setLayout(new FlowLayout(FlowLayout.LEADING));
		this.setBackground(Design.Themes.Identification.BackgroundColor);
		this.setBorder(new EmptyBorder(17, 5, 17, 17));
		this.add(userLabel);
		this.add(Design.createSpace(235));
		this.add(userField);
		this.add(Design.createSpace(270, 13));
		this.add(pwLabel);
		this.add(Design.createSpace(200));
		this.add(pwField);
		this.add(showPassLabel);
		this.add(Design.createSpace(270, 13));
		this.add(signInButton);
		this.add(Design.createSpace(5));
		this.add(signUpButton);
		this.add(resetPwButton);
		this.add(Design.createSpace(270, 20));
		this.add(errorLabel);
	}

	/**
	 * private Methods
	 */
	private boolean isAvailableForm()
	{
		if (userField.getText().length() == 0)
		{
			showError("You must enter your id!", false);
			userField.grabFocus();
			return false;
		}
		else if (pwField.getPassword().length == 0)
		{
			showError("You must enter your password!", false);
			pwField.grabFocus();
			return false;
		}

		hideError();
		return true;
	}

	private void raiseSignInEvent()
	{
		if (isAvailableForm())
		{
			String response = null;
			Socket client = null;
			PrintStream out = null;
			Scanner in = null;

			try
			{
				client = new Socket(Preferences.HostAddress, Preferences.PortNumber);
				out = new PrintStream(client.getOutputStream());
				in = new Scanner(client.getInputStream());
			}
			catch (IOException e)
			{
				showError("Failed to connect server.", false);
				return;
			}

			String id = userField.getText();
			String pw = new String(pwField.getPassword());
			
			out.println("sigin");
			out.println(id);
			out.println(pw);
			
			response = in.nextLine();

			if (response.startsWith("normal"))
			{
				Iterator<ConnectionListener> iterator = signInListener.listIterator();
				while (iterator.hasNext())
				{
					ConnectionListener listener = iterator.next();
					listener.connected(id);
				}
			}
			else
			{
				showError(response, false);
			}
			
			try
			{
				out.println("quit");
				client.close();
			}
			catch (IOException e)
			{
			}
		}
	}
}
