package generic;

import java.awt.*;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.io.File;
import java.io.IOException;

import javax.swing.*;
import javax.swing.border.*;
import javax.swing.custom.*;

public class Design
{
	public static class Themes
	{
		public static class Identification
		{
			public static final Color BackgroundColor = new Color(0x303030);
			public static final Color ForegroundColor = new Color(0xFFFFFF);
			public static final Color ErrorColor = new Color(0xFF7070);
			public static final Color WarningColor = new Color(0xFFFF70);

			public static void initializeLabel(JLabel label, String text)
			{
				label.setFont(Fonts.DefaultFont.deriveFont(15F));
				label.setForeground(ForegroundColor);
				label.setText(text);
			}

			public static void initializeTextField(JTextField textField, int width)
			{
				textField.setFont(Design.Fonts.DefaultFont.deriveFont(15F));
				textField.setBackground(BackgroundColor);
				textField.setForeground(ForegroundColor);
				textField.setCaretColor(ForegroundColor);
				textField.setSelectionColor(Color.LIGHT_GRAY);
				textField.setCaret(new SmoothCaret());
				textField.setPreferredSize(new Dimension(width, textField.getPreferredSize().height));
				textField.setBorder(new MatteBorder(0, 0, 1, 0, Color.LIGHT_GRAY));
			}

			public static void initializeFlatButton(FlatButton flatButton, String text)
			{
				flatButton.setBackground(BackgroundColor);
				flatButton.setForeground(ForegroundColor);
				flatButton.setFont(Design.Fonts.DefaultFont.deriveFont(18F).deriveFont(Font.BOLD));
				flatButton.setText(text);
			}
		}

		public static class Chat
		{
			public static final Color RootBackground = new Color(0x333333);
			public static final Color Background = new Color(0x4D4D4D);
			public static final Color Foreground = Color.WHITE;
			public static final Color WhisperForeround = new Color(0xFFFF65);
			public static final Color SystemForeground = new Color(0x65FFFF);
			public static final Color ErrorForeground = new Color(0xFF6565);
		}

		public static class Game
		{
			public static final Color Background = new Color(0x225145);
			public static final Color Foreground = Color.WHITE;
			public static final Color SelectionForeground = new Color(0x528175);
		}
	}

	public static class Fonts
	{
		private static boolean isInitialized = false;

		public static final Font DefaultFont = new Font("¸¼Àº °íµñ", Font.PLAIN, 12);
		public static final Font Arial = new Font("Arial", Font.PLAIN, 12);
		public static Font Dreamspeak = null;

		public Fonts()
		{
			if (!isInitialized)
			{
				try
				{
					Dreamspeak = Font.createFont(Font.TRUETYPE_FONT, new File("fonts/dreamspeak.ttf")).deriveFont(26F);
				}
				catch (FontFormatException | IOException e)
				{
					e.printStackTrace();
				}
			}
			isInitialized = true;
		}
	}

	public static JPanel createSpace(int width, int height, Color background)
	{
		JPanel panel = new JPanel();
		panel.setBackground(background);
		panel.setPreferredSize(new Dimension(width, height));
		/**
		 * Remove this code if not debugging.
		 */
		// panel.setBorder(new LineBorder(Color.CYAN, 1));

		return panel;
	}

	public static JPanel createSpace(int width, int height)
	{
		return createSpace(width, height, Themes.Identification.BackgroundColor);
	}

	public static JPanel createSpace(int width)
	{
		return createSpace(width, 10);
	}
}
