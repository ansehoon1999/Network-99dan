package javax.swing.custom;

import java.awt.*;
import java.awt.event.MouseEvent;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.concurrent.*;

import javax.swing.*;
import javax.swing.border.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import generic.MouseClickListener;

public final class ToggleButton extends JPanel
{
	private static final double AnimationDuration = 300;

	private final ToggleButton This = this;

	private JLabel textLabel = null;

	private EmptyBorder defaultBorder = new EmptyBorder(5, 5, 5, 5);
	private Color beltColor = Color.BLACK;
	private int beltWidth = 3;

	private LinkedList<ChangeListener> checkedChangeListeners = new LinkedList<ChangeListener>();
	private Boolean checked = false;
	private double progress = 0;
	private long time = 0;

	public ToggleButton()
	{
		initializeComponents();

		this.addMouseListener(new MouseClickListener()
		{
			@Override
			public void mouseClicked(MouseEvent e)
			{
				synchronized (checked)
				{
					checked = !checked;
					time = System.currentTimeMillis();
					
					Iterator<ChangeListener> iterator = checkedChangeListeners.listIterator();
					while (iterator.hasNext())
					{
						iterator.next().stateChanged(new ChangeEvent(This));
					}
				}
			}
		});

		Executors.newSingleThreadExecutor().execute(new Runnable()
		{
			@Override
			public void run()
			{
				while (true)
				{
					synchronized (checked)
					{
						if (checked)
						{
							if (progress < 1)
							{
								progress = (double) (System.currentTimeMillis() - time) / AnimationDuration;
								if (progress > 1)
								{
									progress = 1;
								}

								repaint();
							}
						}
						else
						{
							if (progress > 0)
							{
								progress = 1 - (double) (System.currentTimeMillis() - time) / AnimationDuration;
								if (progress < 0)
								{
									progress = 0;
								}

								repaint();
							}
						}
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
			}
		});
	}

	public ToggleButton(String text)
	{
		this();
		textLabel.setText(text);
	}

	@Override
	public void setFont(Font font)
	{
		super.setFont(font);
		if (textLabel != null)
		{
			textLabel.setFont(font);
		}
	}

	@Override
	public void setForeground(Color fg)
	{
		super.setForeground(fg);

		if (textLabel != null)
		{
			textLabel.setForeground(fg);
		}
	}

	public String getText()
	{
		return textLabel.getText();
	}

	public void setText(String text)
	{
		if (textLabel != null)
		{
			textLabel.setText(text);
		}
	}

	@Override
	public void setBorder(Border border)
	{
		super.setBorder(BorderFactory.createCompoundBorder(border, defaultBorder));
	}

	public Color getBeltColor()
	{
		return beltColor;
	}

	public void setBeltColor(Color color)
	{
		beltColor = color;
		repaint();
	}

	public int getBeltWidth()
	{
		return beltWidth;
	}

	public void setBeltWidth(int width)
	{
		beltWidth = width;
		repaint();
	}
	
	public boolean getChecked()
	{
		return checked;
	}
	
	public void setChecked(boolean checked)
	{
		this.checked = checked;
	}
	
	public void addCheckedChangedListener(ChangeListener l)
	{
		checkedChangeListeners.add(l);
	}
	
	public boolean removeCheckedChangedListener(ChangeListener l)
	{
		return checkedChangeListeners.remove(l);
	}

	private final void initializeComponents()
	{
		textLabel = new JLabel();

		textLabel.setHorizontalAlignment(SwingConstants.CENTER);
		textLabel.setVerticalAlignment(SwingConstants.CENTER);

		this.setLayout(new BorderLayout());
		this.setBorder(defaultBorder);
		this.setDoubleBuffered(true);
		this.add(textLabel, BorderLayout.CENTER);
	}
}
