package contents.game;

import java.awt.*;
import java.awt.event.*;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Iterator;
import java.util.LinkedList;

import javax.swing.*;
import javax.swing.border.*;
import javax.swing.custom.*;

import generic.*;

public class GameFrame extends JFrame
{
	/**
	 * GUI components
	 */
	private JPanel rootPanel = null;
	private JPanel topPanel = null;
	private JLabel infoLabel = null;
	private JLabel titleLabel = null;
	private JPanel middlePanel = null;
	private JTextField input1Field = null;
	private JTextField input2Field = null;
	private JLabel operatorLabel = null;
	private JPanel bottomPanel = null;
	private JTextField answerField = null;

	/**
	 * private Fields
	 */
	private LinkedList<CommandListener> sendQuizListener = new LinkedList<CommandListener>();
	private LinkedList<CommandListener> sendAnswerListener = new LinkedList<CommandListener>();
	private LinkedList<ActionListener> gameOverListener = new LinkedList<ActionListener>();

	private KeyTypeListener escListener = new KeyTypeListener()
	{
		@Override
		public void keyTyped(KeyEvent e)
		{
			switch (e.getKeyChar())
			{
			case KeyEvent.VK_ESCAPE:
				raiseGameOverEvent(!gameOver);
				break;

			default:
				break;
			}
		}
	};

	private boolean gameOver = false;

	/**
	 * Constructor
	 */
	public GameFrame(boolean attackFirst)
	{
		initializeComponents();

		if (attackFirst)
		{
			input1Field.setEditable(true);
			input2Field.setEditable(true);

			infoLabel.setText("먼저 공격합니다.");
		}
		else
		{
			infoLabel.setText("상대방이 먼저 공격합니다.");
		}
	}

	/**
	 * public Methods
	 */
	public void setQuiz(int op1, int op2)
	{
		input1Field.setText(String.valueOf(op1));
		input2Field.setText(String.valueOf(op2));

		answerField.setEditable(true);
		answerField.grabFocus();

		infoLabel.setText("정답을 입력해주세요.");
	}

	public void openQuizField()
	{
		input1Field.setEditable(true);
		input2Field.setEditable(true);

		input1Field.setText(null);
		input2Field.setText(null);

		input1Field.grabFocus();
		input1Field.selectAll();

		infoLabel.setText("공격할 차례입니다!");
	}

	public void lockAll()
	{
		input1Field.setEditable(false);
		input2Field.setEditable(false);
		answerField.setEditable(false);
	}

	public void showInfo(String info)
	{
		infoLabel.setText(info);
	}

	public void addSendQuizListener(CommandListener l)
	{
		sendQuizListener.add(l);
	}

	public boolean removeSendQuizListener(CommandListener l)
	{
		return sendQuizListener.remove(l);
	}

	public void addSendAnswerListener(CommandListener l)
	{
		sendAnswerListener.add(l);
	}

	public boolean removeSendAnswerListener(CommandListener l)
	{
		return sendAnswerListener.remove(l);
	}

	public void addGameOverListener(ActionListener l)
	{
		gameOverListener.add(l);
	}

	public boolean removeGameOverListener(ActionListener l)
	{
		return gameOverListener.add(l);
	}

	public void overGame()
	{
		lockAll();
		gameOver = true;
	}

	/**
	 * private Methods
	 */
	private void initializeComponents()
	{
		rootPanel = new JPanel();
		topPanel = new JPanel();
		infoLabel = new JLabel();
		titleLabel = new JLabel();
		middlePanel = new JPanel();
		input1Field = new JTextField();
		input2Field = new JTextField();
		operatorLabel = new JLabel();
		bottomPanel = new JPanel();
		answerField = new JTextField();

		//
		// infoLabel
		//
		infoLabel.setFont(Design.Fonts.DefaultFont);
		infoLabel.setForeground(Design.Themes.Game.Foreground);
		infoLabel.setHorizontalAlignment(SwingConstants.CENTER);
		infoLabel.setText("상대를 기다리는 중입니다.");

		//
		// titleLabel
		//
		titleLabel.setFont(Design.Fonts.DefaultFont.deriveFont(16F));
		titleLabel.setForeground(Design.Themes.Game.Foreground);
		titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
		titleLabel.setText("구구단을 외자! 구구단을 외자!");
		titleLabel.setPreferredSize(new Dimension(350, 50));

		//
		// topPanel
		//
		topPanel.setLayout(new BorderLayout());
		topPanel.setBackground(Design.Themes.Game.Background);
		topPanel.add(infoLabel, BorderLayout.CENTER);
		topPanel.add(titleLabel, BorderLayout.SOUTH);

		//
		// input1Field
		//
		input1Field.setFont(Design.Fonts.DefaultFont.deriveFont(16F));
		input1Field.setBackground(Design.Themes.Game.Background);
		input1Field.setForeground(Design.Themes.Game.Foreground);
		input1Field.setSelectionColor(Design.Themes.Game.SelectionForeground);
		input1Field.setHorizontalAlignment(SwingConstants.CENTER);
		input1Field.setPreferredSize(new Dimension(40, 30));
		input1Field.setBorder(new MatteBorder(0, 0, 1, 0, Design.Themes.Game.Foreground));
		input1Field.setCaret(new SmoothCaret());
		input1Field.setCaretColor(Design.Themes.Game.Foreground);
		input1Field.setEditable(false);
		input1Field.addKeyListener(new KeyTypeListener()
		{
			@Override
			public void keyTyped(KeyEvent e)
			{
				if (e.getKeyChar() == '\n')
				{
					input2Field.grabFocus();
					input2Field.selectAll();
				}
				else if (!Character.isDigit(e.getKeyChar()))
				{
					e.consume();
				}
			}
		});
		input1Field.addKeyListener(escListener);

		//
		// input2Field
		//
		input2Field.setFont(Design.Fonts.DefaultFont.deriveFont(16F));
		input2Field.setBackground(Design.Themes.Game.Background);
		input2Field.setForeground(Design.Themes.Game.Foreground);
		input2Field.setSelectionColor(Design.Themes.Game.SelectionForeground);
		input2Field.setHorizontalAlignment(SwingConstants.CENTER);
		input2Field.setPreferredSize(new Dimension(40, 30));
		input2Field.setBorder(new MatteBorder(0, 0, 1, 0, Design.Themes.Game.Foreground));
		input2Field.setCaret(new SmoothCaret());
		input2Field.setCaretColor(Design.Themes.Game.Foreground);
		input2Field.setEditable(false);
		input2Field.addKeyListener(new KeyTypeListener()
		{
			@Override
			public void keyTyped(KeyEvent e)
			{
				if (e.getKeyChar() == '\n')
				{
					raiseSendQuizEvent();
				}
				else if (!Character.isDigit(e.getKeyChar()))
				{
					e.consume();
				}
			}
		});
		input2Field.addKeyListener(escListener);

		//
		// operatorLabel
		//
		operatorLabel.setFont(Design.Fonts.DefaultFont.deriveFont(16F));
		operatorLabel.setForeground(Design.Themes.Game.Foreground);
		operatorLabel.setText("X");
		operatorLabel.setPreferredSize(new Dimension(40, 30));
		operatorLabel.setHorizontalAlignment(SwingConstants.CENTER);

		//
		// middlePanel
		//
		middlePanel.setLayout(new FlowLayout(FlowLayout.CENTER));
		middlePanel.setBorder(new EmptyBorder(5, 0, 0, 0));
		middlePanel.setBackground(Design.Themes.Game.Background);
		middlePanel.add(input1Field);
		middlePanel.add(operatorLabel);
		middlePanel.add(input2Field);

		//
		// answerField
		//
		answerField.setFont(Design.Fonts.DefaultFont.deriveFont(16F));
		answerField.setPreferredSize(new Dimension(60, 30));
		answerField.setHorizontalAlignment(SwingConstants.CENTER);
		answerField.setBackground(Design.Themes.Game.Background);
		answerField.setForeground(Design.Themes.Game.Foreground);
		answerField.setSelectionColor(new Color(0x528175));
		answerField.setCaret(new SmoothCaret());
		answerField.setCaretColor(Design.Themes.Game.Foreground);
		answerField.setBorder(new LineBorder(Design.Themes.Game.Foreground));
		answerField.setEditable(false);
		answerField.addKeyListener(new KeyTypeListener()
		{
			@Override
			public void keyTyped(KeyEvent e)
			{
				if (e.getKeyChar() == '\n')
				{
					raiseSendAnswerEvent();
				}
				else if (!Character.isDigit(e.getKeyChar()))
				{
					e.consume();
				}
			}
		});
		answerField.addKeyListener(escListener);

		//
		// bottomPanel
		//
		bottomPanel.setLayout(new FlowLayout(FlowLayout.CENTER));
		bottomPanel.setBackground(Design.Themes.Game.Background);
		bottomPanel.add(answerField);

		//
		// rootPanel
		//
		rootPanel.setLayout(new BorderLayout());
		rootPanel.setBackground(Design.Themes.Game.Background);
		rootPanel.setBorder(new LineBorder(Design.Themes.Game.Background, 60));
		rootPanel.add(topPanel, BorderLayout.NORTH);
		rootPanel.add(middlePanel, BorderLayout.CENTER);
		rootPanel.add(bottomPanel, BorderLayout.SOUTH);

		//
		// GameFrame
		//
		this.setUndecorated(true);
		this.setSize(new Dimension(400, 300));
		this.setLocationRelativeTo(null);
		this.setContentPane(rootPanel);
		DragMoveListener dragMoveListener = new DragMoveListener(this, MouseEvent.BUTTON1);
		this.addMouseMotionListener(dragMoveListener);
		this.addMouseListener(dragMoveListener);
		this.addKeyListener(escListener);
		this.addWindowListener(new WindowClosingListener()
		{
			@Override
			public void windowClosing(WindowEvent e)
			{
				raiseGameOverEvent(!gameOver);
			}
		});
	}

	private Integer readInteger(JTextField textField)
	{
		if (textField.getText().length() == 0)
		{
			infoLabel.setText("자연수를 입력해주세요!");
			textField.grabFocus();
			textField.selectAll();
			return null;
		}
		else
		{
			try
			{
				return Integer.parseInt(textField.getText());
			}
			catch (NumberFormatException e)
			{
				infoLabel.setText("자연수만 입력해주세요!");
				textField.grabFocus();
				textField.selectAll();

				return null;
			}
		}
	}

	private int raiseEvent(LinkedList<CommandListener> listeners, CommandEvent e)
	{
		int count = 0;

		Iterator<CommandListener> iterator = listeners.listIterator();
		while (iterator.hasNext())
		{
			iterator.next().commanded(e);
			count++;
		}

		return count;
	}

	private void raiseSendQuizEvent()
	{
		Integer op1, op2;

		op1 = readInteger(input1Field);
		if (op1 == null)
		{
			return;
		}
		op2 = readInteger(input2Field);
		if (op2 == null)
		{
			return;
		}

		input1Field.setText(null);
		input2Field.setText(null);

		CommandEvent e = new CommandEvent("quiz", new Object[] { op1, op2 });
		raiseEvent(sendQuizListener, e);

		input1Field.setEditable(false);
		input2Field.setEditable(false);

		infoLabel.setText("상대방이 정답을 입력중입니다.");
	}

	private void raiseSendAnswerEvent()
	{
		Integer answer = readInteger(answerField);
		if (answer == null)
		{
			return;
		}

		answerField.setText(null);

		CommandEvent e = new CommandEvent("answer", new Object[] { answer });
		raiseEvent(sendAnswerListener, e);

		answerField.setEditable(false);
	}

	private void raiseGameOverEvent(boolean surrender)
	{
		ActionEvent e = new ActionEvent(this, 0, surrender ? "surrender" : null);
		Iterator<ActionListener> iterator = gameOverListener.listIterator();

		while (iterator.hasNext())
		{
			iterator.next().actionPerformed(e);
		}
	}
}
