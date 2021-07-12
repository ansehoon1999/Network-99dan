package contents.game;

import java.awt.*;
import java.awt.event.*;
import java.util.*;

import javax.swing.*;
import javax.swing.border.*;
import javax.swing.custom.FlatButton;

import generic.*;

public class GameRequestDialog extends JDialog
{
	/**
	 * GUI Components
	 */
	private JPanel rootPanel = null;
	private JLabel messageLabel = null;
	private JPanel buttonPanel = null;
	private FlatButton acceptButton = null;
	private FlatButton rejectButton = null;

	/**
	 * private Fields
	 */
	private boolean isDisposable = false;
	private boolean performed = false;
	private JFrame parent = null;
	private boolean accepted = false;
	private LinkedList<ActionListener> actionListeners = new LinkedList<ActionListener>();

	/**
	 * Constructors
	 */
	public GameRequestDialog(JFrame parent, String id, boolean isDisposable)
	{
		super(parent);

		this.parent = parent;
		this.parent.setEnabled(false);
		this.isDisposable = isDisposable;

		initializeComponents();

		messageLabel.setText(id + messageLabel.getText());
	}

	/**
	 * public Methods
	 */
	public boolean isAccepted()
	{
		return accepted;
	}

	public void addActionListener(ActionListener l)
	{
		actionListeners.add(l);
	}

	public boolean removeActionListener(ActionListener l)
	{
		return actionListeners.remove(l);
	}

	/**
	 * private Methods
	 */
	private void initializeComponents()
	{
		rootPanel = new JPanel();
		messageLabel = new JLabel();
		buttonPanel = new JPanel();
		acceptButton = new FlatButton();
		rejectButton = new FlatButton();

		//
		// messageLabel
		//
		Design.Themes.Identification.initializeLabel(messageLabel, " invited you.");
		messageLabel.setFont(messageLabel.getFont().deriveFont(17.5F));
		messageLabel.setHorizontalAlignment(SwingConstants.CENTER);

		//
		// acceptButton
		//
		Design.Themes.Identification.initializeFlatButton(acceptButton, "Accept");
		acceptButton.addMouseListener(new MouseClickListener()
		{
			@Override
			public void mouseClicked(MouseEvent e)
			{
				if (e.getButton() == MouseEvent.BUTTON1)
				{
					accepted = true;
					raiseActionEvent();
				}
			}
		});

		//
		// rejectButton
		//
		Design.Themes.Identification.initializeFlatButton(rejectButton, "Reject");
		rejectButton.addMouseListener(new MouseClickListener()
		{
			@Override
			public void mouseClicked(MouseEvent e)
			{
				if (e.getButton() == MouseEvent.BUTTON1)
				{
					accepted = false;
					raiseActionEvent();
				}
			}
		});

		//
		// buttonPanel
		//
		buttonPanel.setLayout(new GridLayout(1, 2));
		buttonPanel.setBorder(new EmptyBorder(10, 20, 10, 20));
		buttonPanel.setBackground(Design.Themes.Chat.RootBackground);
		buttonPanel.add(acceptButton);
		buttonPanel.add(rejectButton);

		//
		// rootPanel
		//
		rootPanel.setLayout(new BorderLayout());
		rootPanel.setBorder(new EmptyBorder(40, 30, 20, 30));
		rootPanel.setBackground(Design.Themes.Chat.RootBackground);
		rootPanel.add(messageLabel, BorderLayout.NORTH);
		rootPanel.add(buttonPanel, BorderLayout.CENTER);

		//
		// GameRequestDialog
		//
		this.setContentPane(rootPanel);
		this.setUndecorated(true);
		this.setSize(300, 150);
		this.setLocationRelativeTo(null);
		this.setAlwaysOnTop(true);
		this.addWindowListener(new WindowClosingListener()
		{
			@Override
			public void windowClosing(WindowEvent e)
			{
				accepted = false;
				raiseActionEvent();
			}
		});
	}

	private void raiseActionEvent()
	{
		if (!performed || !isDisposable)
		{
			Iterator<ActionListener> iterator = actionListeners.listIterator();

			while (iterator.hasNext())
			{
				iterator.next().actionPerformed(new ActionEvent(this, 0, null));
			}

			performed = true;
		}
	}
}
