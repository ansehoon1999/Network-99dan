package contents.chat;

import java.awt.*;
import java.awt.event.*;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.swing.*;
import javax.swing.border.*;

import generic.*;

public class RecordFrame extends JFrame
{
	/**
	 * GUI Elements
	 */
	private JPanel rootPanel = null;
	private JPanel topPanel = null;
	private JLabel titleLabel = null;
	private JLabel exitLabel = null;
	private JPanel contentPanel = null;
	private JLabel idLabel = null;
	private JLabel idValueLabel = null;
	private JLabel stateLabel = null;
	private JLabel stateValueLabel = null;
	private JLabel winLabel = null;
	private JLabel winValueLabel = null;
	private JLabel loseLabel = null;
	private JLabel loseValueLabel = null;
	private JLabel lastSignInLabel = null;
	private JLabel lastSignInValueLabel = null;

	/**
	 * Constructors
	 */
	public RecordFrame(String id, int win, int lose, long time, int state)
	{
		rootPanel = new JPanel();
		topPanel = new JPanel();
		titleLabel = new JLabel();
		exitLabel = new JLabel();
		contentPanel = new JPanel();
		idLabel = new JLabel();
		idValueLabel = new JLabel();
		stateLabel = new JLabel();
		stateValueLabel = new JLabel();
		winLabel = new JLabel();
		winValueLabel = new JLabel();
		loseLabel = new JLabel();
		loseValueLabel = new JLabel();
		lastSignInLabel = new JLabel();
		lastSignInValueLabel = new JLabel();

		//
		// titleLabel
		//
		Design.Themes.Identification.initializeLabel(titleLabel, "Record");
		titleLabel.setPreferredSize(new Dimension(140, 35));
		new Design.Fonts();
		titleLabel.setFont(Design.Fonts.Dreamspeak.deriveFont(26F));
		titleLabel.setBorder(new MatteBorder(0, 0, 2, 0, Color.LIGHT_GRAY));

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
					dispose();
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
		topPanel.setBorder(new EmptyBorder(0, 0, 10, 0));
		topPanel.setBackground(Design.Themes.Chat.RootBackground);
		topPanel.add(titleLabel, BorderLayout.WEST);
		topPanel.add(exitLabel, BorderLayout.EAST);

		//
		// idLabel
		//
		Design.Themes.Identification.initializeLabel(idLabel, "ID: ");
		idLabel.setFont(Design.Fonts.DefaultFont.deriveFont(18F).deriveFont(Font.BOLD));

		//
		// idValueLabel
		//
		Design.Themes.Identification.initializeLabel(idValueLabel, id);

		//
		// stateLabel
		//
		Design.Themes.Identification.initializeLabel(stateLabel, "State: ");
		stateLabel.setFont(Design.Fonts.DefaultFont.deriveFont(18F).deriveFont(Font.BOLD));

		//
		// stateValueLabel
		//
		Design.Themes.Identification.initializeLabel(stateValueLabel, null);
		if (state == 1)
		{
			stateValueLabel.setText("Online");
		}
		else
		{
			stateValueLabel.setText("On Game");
		}

		//
		// winLabel
		//
		Design.Themes.Identification.initializeLabel(winLabel, "WIN: ");
		winLabel.setFont(Design.Fonts.DefaultFont.deriveFont(18F).deriveFont(Font.BOLD));

		//
		// winValueLabel
		//
		Design.Themes.Identification.initializeLabel(winValueLabel, Integer.toString(win));

		//
		// loseLabel
		//
		Design.Themes.Identification.initializeLabel(loseLabel, "LOSE: ");
		loseLabel.setFont(Design.Fonts.DefaultFont.deriveFont(18F).deriveFont(Font.BOLD));

		//
		// loseValueLabel
		//
		Design.Themes.Identification.initializeLabel(loseValueLabel, Integer.toString(lose));

		//
		// lastSignInLabel
		//
		Design.Themes.Identification.initializeLabel(lastSignInLabel, "SIGNIN: ");
		lastSignInLabel.setFont(Design.Fonts.DefaultFont.deriveFont(18F).deriveFont(Font.BOLD));

		//
		// winValueLabel
		//
		Design.Themes.Identification.initializeLabel(lastSignInValueLabel, dateToString(new Date(time)));

		//
		// contentPanel
		//
		contentPanel.setLayout(new GridLayout(5, 2));
		contentPanel.setBorder(BorderFactory.createCompoundBorder(new MatteBorder(1, 2, 0, 0, Color.LIGHT_GRAY), new EmptyBorder(15, 15, 15, 15)));
		contentPanel.setBackground(Design.Themes.Chat.Background);
		contentPanel.add(idLabel);
		contentPanel.add(idValueLabel);
		contentPanel.add(stateLabel);
		contentPanel.add(stateValueLabel);
		contentPanel.add(winLabel);
		contentPanel.add(winValueLabel);
		contentPanel.add(loseLabel);
		contentPanel.add(loseValueLabel);
		contentPanel.add(lastSignInLabel);
		contentPanel.add(lastSignInValueLabel);

		//
		// rootPanel
		//
		rootPanel.setLayout(new BorderLayout());
		rootPanel.setBorder(new EmptyBorder(15, 15, 15, 15));
		rootPanel.setBackground(Design.Themes.Chat.RootBackground);
		rootPanel.add(topPanel, BorderLayout.NORTH);
		rootPanel.add(contentPanel, BorderLayout.CENTER);

		//
		// RecordFrame
		//
		this.setContentPane(rootPanel);
		this.setSize(400, 250);
		this.setUndecorated(true);
		this.addWindowListener(new WindowClosingListener()
		{
			@Override
			public void windowClosing(WindowEvent e)
			{
				dispose();
			}
		});
		DragMoveListener dragMove = new DragMoveListener(this, MouseEvent.BUTTON1);
		this.addMouseMotionListener(dragMove);
		this.addMouseListener(dragMove);
		this.setLocationRelativeTo(null);
		this.setResizable(false);
		this.addKeyListener(new KeyTypeListener()
		{
			@Override
			public void keyTyped(KeyEvent e)
			{
				switch (e.getKeyChar())
				{
				case KeyEvent.VK_ESCAPE:
					dispose();
					break;
				}
			}
		});
	}

	/**
	 * private Methods
	 */
	private String dateToString(Date date)
	{
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy.MM.dd HH:mm:ss");
		return formatter.format(date);
	}
}
