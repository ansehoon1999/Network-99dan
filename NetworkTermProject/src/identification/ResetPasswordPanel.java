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

public class ResetPasswordPanel extends ModulePanelBase
{
	private final ResetPasswordPanel This = this;

	/**
	 * private Fields
	 */
	private LinkedList<ActionListener> resetListener = new LinkedList<ActionListener>();

	/**
	 * GUI Elements
	 */
	private JLabel userLabel = null;
	private JTextField userField = null;
	private JLabel nameLabel = null;
	private JTextField firstNameField = null;
	private JTextField lastNameField = null;
	private JLabel pwLabel = null;
	private JPasswordField pwField = null;
	private ShowPasswordCheckBox showPassLabel = null;
	private JLabel pwConfLabel = null;
	private JPasswordField pwConfField = null;
	private ShowPasswordCheckBox showConfPassLabel = null;
	private FlatButton resetButton = null;

	/**
	 * Constructor
	 */
	public ResetPasswordPanel()
	{
		initializeComponents();
	}

	/**
	 * public Methods
	 */
	public void addResetListener(ActionListener listener)
	{
		resetListener.add(listener);
	}

	public boolean removeResetListener(ActionListener listener)
	{
		return resetListener.remove(listener);
	}

	@Override
	public void reset()
	{
		userField.setText(null);
		firstNameField.setText(null);
		lastNameField.setText(null);
		pwField.setText(null);
		showPassLabel.setChecked(false);
		pwConfField.setText(null);
		showConfPassLabel.setChecked(false);
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
		this.nameLabel = new JLabel();
		this.firstNameField = new JTextField();
		this.lastNameField = new JTextField();
		this.pwLabel = new JLabel();
		this.pwField = new JPasswordField();
		this.showPassLabel = new ShowPasswordCheckBox();
		this.pwConfLabel = new JLabel();
		this.pwConfField = new JPasswordField();
		this.showConfPassLabel = new ShowPasswordCheckBox();
		this.resetButton = new FlatButton();
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

				default:
					break;
				}
			}
		});

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
					if (firstNameField.getSelectedText() == null && firstNameField.getCaretPosition() == firstNameField.getText().length())
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
		// pwLabel
		//
		Design.Themes.Identification.initializeLabel(pwLabel, "New Password");

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
					requestReset();
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
		// resetButton
		//
		Design.Themes.Identification.initializeFlatButton(resetButton, "Reset");
		resetButton.addMouseListener(new MouseClickListener()
		{
			@Override
			public void mouseClicked(MouseEvent e)
			{
				if (e.getButton() == MouseEvent.BUTTON1)
				{
					requestReset();
				}
			}
		});

		//
		// errorLabel
		//
		Design.Themes.Identification.initializeLabel(errorLabel, null);
		errorLabel.setFont(Design.Fonts.DefaultFont.deriveFont(15F).deriveFont(Font.BOLD));

		//
		// ResetPasswordPanel
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
		this.add(Design.createSpace(165));
		this.add(pwField);
		this.add(showPassLabel);
		this.add(Design.createSpace(270, 13));
		this.add(pwConfLabel);
		this.add(Design.createSpace(210));
		this.add(pwConfField);
		this.add(showConfPassLabel);
		this.add(Design.createSpace(270, 13));
		this.add(resetButton);
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

	private void requestReset()
	{
		if (isAvailableForm())
		{
			String response = null;
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

			out.println("setpw");
			out.println(firstNameField.getText());
			out.println(lastNameField.getText());
			out.println(userField.getText());
			out.println(new String(pwField.getPassword()));
			response = in.nextLine();

			if (response.equals("normal"))
			{
				Iterator<ActionListener> iterator = resetListener.listIterator();
				while (iterator.hasNext())
				{
					ActionListener listener = iterator.next();
					listener.actionPerformed(new ActionEvent(This, 1, "setpw"));
				}
			}
			else
			{
				showError(response, false);
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
