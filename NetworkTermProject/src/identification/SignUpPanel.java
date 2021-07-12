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

import generic.Design;
import generic.KeyTypeListener;
import generic.MouseClickListener;
import generic.Preferences;

public class SignUpPanel extends ModulePanelBase
{
	private final SignUpPanel This = this;

	/**
	 * private Fields
	 */
	private LinkedList<ActionListener> signUpListener = new LinkedList<ActionListener>();

	/**
	 * GUI Components
	 */
	private JLabel userLabel = null;
	private JTextField userField = null;
	private JLabel pwLabel = null;
	private JPasswordField pwField = null;
	private ShowPasswordCheckBox showPassLabel = null;
	private JLabel pwConfLabel = null;
	private JPasswordField pwConfField = null;
	private ShowPasswordCheckBox showConfPassLabel = null;
	private JLabel nameLabel = null;
	private JTextField firstNameField = null;
	private JTextField lastNameField = null;
	private FlatButton signUpButton = null;

	/**
	 * Constructor
	 */
	public SignUpPanel()
	{
		initializeComponents();
	}

	/**
	 * public Methods
	 */
	public void addSignUpListener(ActionListener listener)
	{
		signUpListener.add(listener);
	}

	public boolean removeSignUpListener(ActionListener listener)
	{
		return signUpListener.remove(listener);
	}

	@Override
	public void reset()
	{
		userField.setText(null);
		pwField.setText(null);
		showPassLabel.setChecked(false);
		pwConfField.setText(null);
		showConfPassLabel.setChecked(false);
		firstNameField.setText(null);
		lastNameField.setText(null);
		hideError();
	}

	/**
	 * protected Methods
	 */
	@Override
	protected void initializeComponents()
	{
		userLabel = new JLabel();
		userField = new JTextField();
		pwLabel = new JLabel();
		pwField = new JPasswordField();
		showPassLabel = new ShowPasswordCheckBox();
		pwConfLabel = new JLabel();
		pwConfField = new JPasswordField();
		showConfPassLabel = new ShowPasswordCheckBox();
		nameLabel = new JLabel();
		firstNameField = new JTextField();
		lastNameField = new JTextField();
		signUpButton = new FlatButton();
		errorLabel = new JLabel();

		//
		// nameLabel
		//
		Design.Themes.Identification.initializeLabel(nameLabel, "Name");

		//
		// firstNameField
		//
		Design.Themes.Identification.initializeTextField(firstNameField, 110);
		firstNameField.addKeyListener(new KeyTypeListener()
		{
			@Override
			public void keyTyped(KeyEvent e)
			{
				switch (e.getKeyChar())
				{
				case ' ':
					e.consume();
					if (firstNameField.getSelectedText() == null && firstNameField.getSelectionEnd() == firstNameField.getText().length())
					{
						lastNameField.grabFocus();
						lastNameField.selectAll();
					}
					break;

				default:
					char keyChar = e.getKeyChar();
					if (Character.isAlphabetic(keyChar))
					{
						if (firstNameField.getText().length() == 0)
						{
							e.setKeyChar(Character.toUpperCase(keyChar));
						}
						else
						{
							e.setKeyChar(Character.toLowerCase(keyChar));
						}
					}
					else
					{
						e.consume();
					}
					break;
				}
			}
		});

		//
		// lastNameField
		//
		Design.Themes.Identification.initializeTextField(lastNameField, 110);
		lastNameField.addKeyListener(new KeyTypeListener()
		{
			@Override
			public void keyTyped(KeyEvent e)
			{
				switch (e.getKeyChar())
				{
				case '\b':
					e.consume();
					if (lastNameField.getSelectedText() == null && lastNameField.getCaretPosition() == 0)
					{
						firstNameField.grabFocus();
						firstNameField.selectAll();
					}
					break;

				default:
					char keyChar = e.getKeyChar();
					if (Character.isAlphabetic(keyChar))
					{
						if (lastNameField.getText().length() == 0)
						{
							e.setKeyChar(Character.toUpperCase(keyChar));
						}
						else
						{
							e.setKeyChar(Character.toLowerCase(keyChar));
						}
					}
					else
					{
						e.consume();
					}
					break;
				}
			}
		});

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

				default:
					break;
				}
			}
		});
		pwField.addFocusListener(new FocusListener()
		{
			@Override
			public void focusLost(FocusEvent e)
			{
				checkPasswordMatch();
			}

			@Override
			public void focusGained(FocusEvent e)
			{
				// NOTHING
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
		// pwConfLabel
		//
		Design.Themes.Identification.initializeLabel(pwConfLabel, "Confirm");

		//
		// pwConfField
		//
		Design.Themes.Identification.initializeTextField(pwConfField, 230);
		pwConfField.addKeyListener(new KeyTypeListener()
		{
			@Override
			public void keyTyped(KeyEvent e)
			{
				switch (e.getKeyChar())
				{
				case '\n':
					requestSignUp();
					break;

				case ' ':
					e.consume();
					break;

				default:
					break;
				}
			}
		});
		pwConfField.addFocusListener(new FocusListener()
		{
			@Override
			public void focusLost(FocusEvent e)
			{
				checkPasswordMatch();
			}

			@Override
			public void focusGained(FocusEvent e)
			{
				// NOTHING
			}
		});

		//
		// showConfPassLabel
		//
		showConfPassLabel.addChangeListener(new ChangeListener()
		{
			@Override
			public void stateChanged(ChangeEvent e)
			{
				if (showConfPassLabel.getChecked())
				{
					pwConfField.setEchoChar('\0');
				}
				else
				{
					pwConfField.setEchoChar((char) 8226);
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
					requestSignUp();
				}
			}
		});

		//
		// errorLabel
		//
		Design.Themes.Identification.initializeLabel(errorLabel, null);
		errorLabel.setFont(Design.Fonts.DefaultFont.deriveFont(15F).deriveFont(Font.BOLD));

		//
		// SignUpPanel
		//
		this.setLayout(new FlowLayout(FlowLayout.LEADING));
		this.setBackground(Design.Themes.Identification.BackgroundColor);
		this.setBorder(new EmptyBorder(17, 5, 17, 17));
		this.add(nameLabel);
		this.add(Design.createSpace(225));
		this.add(firstNameField);
		this.add(Design.createSpace(5));
		this.add(lastNameField); 
		this.add(Design.createSpace(270, 13));
		this.add(userLabel);
		this.add(Design.createSpace(235));
		this.add(userField);
		this.add(Design.createSpace(270, 13));
		this.add(pwLabel);
		this.add(Design.createSpace(200));
		this.add(pwField);
		this.add(showPassLabel);
		this.add(Design.createSpace(270, 13));
		this.add(pwConfLabel);
		this.add(Design.createSpace(210));
		this.add(pwConfField);
		this.add(showConfPassLabel);
		this.add(Design.createSpace(270, 13));
		this.add(signUpButton);
		this.add(Design.createSpace(270, 20));
		this.add(errorLabel);
	}

	/**
	 * private Methods
	 */
	private boolean isAvailableForm()
	{
		String buffer = null;

		if (firstNameField.getText().length() == 0)
		{
			firstNameField.grabFocus();
			showError("You must enter your name.", false);
			return false;
		}
		else if (userField.getText().length() == 0)
		{
			userField.grabFocus();
			showError("You must enter your id.", false);
			return false;
		}
		else if (userField.getText().length() < 5)
		{
			userField.grabFocus();
			userField.selectAll();
			showError("Too short user id. (5 minimum)", false);
			return false;
		}
		else if (pwField.getPassword().length == 0)
		{
			pwField.grabFocus();
			showError("You must enter your password.", false);
			return false;
		}
		else if (pwField.getPassword().length < 8)
		{
			pwField.grabFocus();
			showError("Too short password. (8 minimum)", false);
			return false;
		}
		else if ((buffer = checkPasswordFormat(pwField.getPassword())) != null)
		{
			pwField.grabFocus();
			pwField.selectAll();
			showError(buffer, false);
			return false;
		}
		else if (pwConfField.getPassword().length == 0)
		{
			pwConfField.grabFocus();
			pwConfField.selectAll();
			showError("Please enter your password again.", false);
			return false;
		}
		else if (!checkPasswordMatch())
		{
			pwConfField.grabFocus();
			pwConfField.selectAll();
			showError("Password doesn't match!", false);
			return false;
		}

		return true;
	}

	private void requestSignUp()
	{
		if (isAvailableForm())
		{
			boolean response = false;
			Socket socket = null;
			PrintStream out = null;
			Scanner in = null;

			try
			{
				socket = new Socket(Preferences.HostAddress, Preferences.PortNumber);
				out = new PrintStream(socket.getOutputStream());
				in = new Scanner(socket.getInputStream());
			}
			catch (IOException e)
			{
				showError("Failed to connect server.", false);
				return;
			}

			out.println("cnfid");
			out.println(userField.getText());
			response = in.nextByte() == 1;

			if (response)
			{
				out.println("sigup");
				out.println(firstNameField.getText());
				out.println(lastNameField.getText());
				out.println(userField.getText());
				out.println(new String(pwField.getPassword()));
				response = in.nextByte() == 1;

				if (response)
				{
					Iterator<ActionListener> iterator = signUpListener.listIterator();
					while (iterator.hasNext())
					{
						ActionListener listener = iterator.next();
						listener.actionPerformed(new ActionEvent(This, 1, "sigup"));
					}
				}
				else
				{
					showError("Request rejected", false);
				}
			}
			else
			{
				showError("This ID is already in use.", false);
				userField.grabFocus();
				userField.selectAll();
			}

			try
			{
				out.println("quit");
				in.close();
				out.close();
				socket.close();
			}
			catch (IOException e)
			{
			}
		}
	}

	private boolean checkPasswordMatch()
	{
		if (pwField.getPassword().length > 0)
		{
			if (!new String(pwField.getPassword()).equals(new String(pwConfField.getPassword())))
			{
				showError("Password doesn't match!", true);
				return false;
			}
			else
			{
				hideError();
				return true;
			}
		}
		else
		{
			return false;
		}
	}
}
