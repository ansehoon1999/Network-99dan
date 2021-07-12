package identification;

import javax.swing.*;

import generic.Design;

public abstract class ModulePanelBase extends JPanel
{
	/**
	 * protected Fields
	 */
	protected JLabel errorLabel = null;

	/**
	 * protected Methods
	 */
	protected abstract void initializeComponents();
	
	protected final String checkPasswordFormat(char[] password)
	{
		// (?=.*[0-9]) # a digit must occur at least once
		// (?=.*[a-z]) # a lower case letter must occur at least once
		// (?=.*[A-Z]) # an upper case letter must occur at least once
		// (?=.*[@#$%^&+=]) # a special character must occur at least once
		// (?=\S+$) # no whitespace allowed in the entire string
		// .{8,} # anything, at least eight places though

		return null;
	}

	/**
	 * public Methods
	 */
	public void showError(String message, boolean warning)
	{
		if (warning)
		{
			errorLabel.setForeground(Design.Themes.Identification.WarningColor);
		}
		else
		{
			errorLabel.setForeground(Design.Themes.Identification.ErrorColor);
		}
		errorLabel.setText(message);
	}

	public void hideError()
	{
		errorLabel.setText(null);
	}

	public abstract void reset();
}
